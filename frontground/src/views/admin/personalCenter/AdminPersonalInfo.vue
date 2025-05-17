<script setup>
  import { ref, reactive, onMounted, computed } from 'vue';
  import { defineOptions } from 'vue';
  import { ElMessage } from 'element-plus';
  import { myStore } from '@/stores/store.js';
  // 导入后台管理员相关的 API 函数
  import {
    getBackAdminDetailApi,
    updateBackAdminInfoApi,
    uploadBackFileApi
  } from '@/api/adminApi.js';
  // 导入后台司机相关的 API 函数
  import {
    getBackDriverDetailApi,
    updateBackDriverInfoApi
  } from '@/api/driverApi.js';
  // 导入后台搬家工人相关的 API 函数
  import {
    getBackMoverDetailApi, // 获取搬家工人详情 API
    updateBackMoverInfoApi // 修改搬家工人信息 API
  } from '@/api/moverApi.js';

  import { Upload } from '@element-plus/icons-vue';

  defineOptions({
    name: 'AdminPersonalInfo',
  });

  const store = myStore();
  // 获取当前登录用户的角色和 ID
  const currentUserInfo = computed(() => store.backUserInfo);
  const currentUserId = computed(() => currentUserInfo.value?.id);
  const currentUserRole = computed(() => currentUserInfo.value?.role);

  // 个人信息表单数据
  // 包含所有可能角色的字段，fetchAllUserInfo 会用后端返回的数据填充
  const userInfoForm = reactive({
    id: null,
    username: '',
    name: '',
    photoUrl: '',
    // 通用字段 (根据后端实际返回决定是否有值)
    gender: '',
    phone: '',
    email: '', // 管理员和司机后端数据中都无
    idCard: '', // 管理员后端数据中无
    familyPhone: '', // 后端数据中无

    // 司机特有字段
    drivingYears: null,
    averageRating: null, // 司机和搬家工人共享的评分字段
    ratingCount: null, // 司机和搬家工人共享的评分字段

    // 移除搬家工人特有字段 moverSpecificField

    // 管理员/系统通用字段 (创建/更新时间和操作人)
    createTime: '',
    updateTime: '',
    createUserName: '', // 仅管理员有
    updateUserName: '', // 仅管理员有

    // 角色字段 (从 store 获取并赋值)
    role: '',
  });

  // 个人信息表单验证规则
  // 根据实际需求设置 required 和 pattern
  const userInfoRules = reactive({
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
    phone: [
      // 手机号通常是重要联系方式，设为必填
      { required: true, message: '请输入手机号', trigger: 'blur' },
      { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' },
    ],
    // 邮箱根据业务需求决定是否必填和校验
    email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
    idCard: [
      // 身份证号通常用于身份认证，设为必填
      { required: true, message: '请输入身份证号', trigger: 'blur' },
      {
        pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
        message: '身份证号格式不正确',
        trigger: 'blur',
      },
    ],
    gender: [{ required: false, message: '请选择性别', trigger: 'change' }], // 性别根据业务需求决定是否必填
    drivingYears: [
      // 驾龄是司机重要信息，设为必填
      { required: true, message: '请输入驾龄', trigger: 'blur' },
      // 校验类型必须为数字
      { type: 'number', message: '驾龄必须是数字', trigger: 'blur' },
      // 校验驾龄数值范围 0-100
      {
        validator: (rule, value, callback) => {
          if (value !== null && value !== undefined && value !== '') {
            if (typeof value !== 'number' || value < 0 || value > 100) {
              callback(new Error('驾龄必须在 0 到 100 之间'));
            } else {
              callback();
            }
          } else {
            callback(); // 由 required 规则处理空值
          }
        },
        trigger: 'blur', // Trigger validation on blur
      },
    ],
    // 移除 moverSpecificField 相关的验证规则
    // ... 添加其他字段的规则
  });

  // Element Plus 表单 Ref
  const userInfoFormRef = ref(null);
  const avatarInput = ref(null); // 文件输入框的 ref

  // 编辑状态控制
  const isEditingInfo = ref(false);
  // 用于取消编辑时恢复原始数据
  let originalUserInfo = {};

  // 默认头像 URL (可以是一个本地图片路径或一个网络图片)
  const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d4dafd62fb4fc7fd09c3d7313png.png'; // 替换为你的默认头像图片路径

  // === 自动调整驾龄到 [0, 100] 范围内 (仅司机需要) ===
  const clampDrivingYears = () => {
    // 只对司机角色的驾龄进行限制
    if (currentUserRole.value === 'driver') {
      const value = userInfoForm.drivingYears;

      // 只有当 value 是一个数字时才进行调整
      if (typeof value === 'number') {
        if (value < 0) {
          userInfoForm.drivingYears = 0;
        } else if (value > 100) {
          userInfoForm.drivingYears = 100;
        }
      } else if (value !== null && value !== undefined && value !== '') {
        // 如果不是数字，由表单验证规则处理
      }
    }
  };

  // === 根据角色获取用户信息的函数 ===
  const fetchUserInfo = async () => {
    const userId = currentUserId.value;
    const role = currentUserRole.value;

    // 在获取用户信息前先清空表单数据，避免显示旧数据或不存在的字段值
    Object.assign(userInfoForm, {
      id: null,
      username: '',
      name: '',
      photoUrl: '',
      gender: '',
      phone: '',
      email: '',
      idCard: '',
      familyPhone: '',
      drivingYears: null,
      averageRating: null,
      ratingCount: null,
      // 移除 moverSpecificField 的清空
      createTime: '',
      updateTime: '',
      createUserName: '',
      updateUserName: '',
      role: '', // 确保 role 被清空
    });
    originalUserInfo = {}; // 清空原始数据

    if (!userId || !role) {
      ElMessage.error('无法获取用户信息，用户ID或角色不存在！');
      return;
    }

    try {
      let res;
      // 根据用户角色调用不同的 API
      if (role === 'admin') {
        res = await getBackAdminDetailApi(userId);
      } else if (role === 'driver') {
        res = await getBackDriverDetailApi(userId);
      } else if (role === 'mover') {
        res = await getBackMoverDetailApi(userId); // *** 为搬家工人调用对应的 API ***
      } else {
        ElMessage.error('未知用户角色，无法获取信息。');
        return;
      }

      if (res.data && res.data.code === 1 && res.data.data) { // 确保 res.data.data 存在
        // 填充表单数据
        Object.assign(userInfoForm, res.data.data);
        // 存储原始数据用于取消编辑
        originalUserInfo = { ...userInfoForm }; // 复制当前填充好的数据

        // 手动设置 role 字段，确保 form 里的 role 字段总是正确反映当前角色
        userInfoForm.role = role;

        // 同时更新 Pinia store 中的后台用户信息，确保 header 等地方显示最新信息
        if (store.backUserInfo) {
          const updatedBackUserInfo = {
            ...store.backUserInfo, // 保留 token 等 Pinia store 特有的字段
            ...res.data.data // 用后端返回的最新数据覆盖
          };
          // 如果后端返回的数据有 photoUrl，就使用后端返回的，否则使用 store 的
          if (res.data.data.photoUrl) {
            updatedBackUserInfo.photoUrl = res.data.data.photoUrl;
          } else if (store.backUserInfo.photoUrl) {
            updatedBackUserInfo.photoUrl = store.backUserInfo.photoUrl;
          } else {
            // 如果后端和 store 都没有 photoUrl，可以设置为 null 或默认头像 URL
            updatedBackUserInfo.photoUrl = null; // 或者 defaultAvatar
          }

          store.saveBackUserInfo(updatedBackUserInfo);
        } else {
          console.warn('Pinia store 中的 backUserInfo 对象不存在或未初始化');
          // 作为健壮性处理，可以尝试用后端返回的数据初始化
          store.saveBackUserInfo({ ...res.data.data, role: role, token: localStorage.getItem('backToken') });
        }

      } else {
        // 业务失败 (code !== 1)，且非登录过期 (request.js 已经处理了)
        console.warn(`${role}信息业务失败:`, res.data?.msg);
      }
    } catch (error) {
      // 捕获真正的请求错误 (网络问题、HTTP 错误等)
      console.error('API调用捕获到异常:', error);
      // 获取失败时，清空本地表单数据 (重新清空一次，确保一致)
      Object.assign(userInfoForm, {
        id: null, username: '', name: '', photoUrl: '',
        gender: '', phone: '', email: '', idCard: '', familyPhone: '',
        drivingYears: null, averageRating: null, ratingCount: null,
        createTime: '', updateTime: '', createUserName: '', updateUserName: '', role: ''
      });
      originalUserInfo = {};
    } finally {
      // 无论成功失败，都确保 form 里的 role 字段被设置
      userInfoForm.role = currentUserRole.value;
    }
  };

  // 切换编辑状态
  const toggleEditingInfo = () => {
    isEditingInfo.value = !isEditingInfo.value;
    if (!isEditingInfo.value) {
      // 取消编辑时恢复原始数据
      Object.assign(userInfoForm, { ...originalUserInfo });
      // 清除表单验证状态
      userInfoFormRef.value?.clearValidate();
    } else {
      // 进入编辑状态时，复制一份当前数据作为原始数据
      originalUserInfo = { ...userInfoForm };
    }
  };

  // === 保存用户信息 ===
  const saveUserInfo = async () => {
    if (!userInfoFormRef.value) return;

    userInfoFormRef.value.validate(async (valid) => {
      if (valid) {
        const userId = currentUserId.value;
        const role = currentUserRole.value;

        if (!userId || !role) {
          ElMessage.error('无法保存用户信息，用户ID或角色不存在！');
          return;
        }

        try {
          // 构造要发送的数据
          // 包含通用字段和角色特有字段，过滤掉不需要发送的字段 (如ID, Create/Update User/Time, Rating等不可编辑字段)
          // 注意：这里只包含您后端接口允许修改的字段
          const updateData = {
            id: userId, // 必须包含用户ID
            username: userInfoForm.username, // 假设用户名可修改
            name: userInfoForm.name,
            photoUrl: userInfoForm.photoUrl, // 发送当前 photoUrl
            // 以下字段根据后端接口是否允许修改和是否需要发送来决定
            // 确保只包含后端接口接收并允许修改的字段
            ...(role === 'admin' && {
              // 添加管理员可修改字段，例如 email, phone, gender, idCard (如果后端允许)
              // email: userInfoForm.email,
              // phone: userInfoForm.phone,
              // gender: userInfoForm.gender,
              // idCard: userInfoForm.idCard,
            }),
            ...(role === 'driver' && {
              gender: userInfoForm.gender, // 司机可能有，假设可修改
              phone: userInfoForm.phone, // 司机可能有，假设可修改
              idCard: userInfoForm.idCard, // 司机可能有，假设可修改
              drivingYears: userInfoForm.drivingYears, // 司机特有，假设可修改
              // averageRating, ratingCount 不应由前端修改
            }),
            ...(role === 'mover' && {
              // *** 添加搬家工人可修改字段 (如果后端允许) ***
              gender: userInfoForm.gender, // 假设搬家工人也有性别、手机、身份证且可修改
              phone: userInfoForm.phone,
              idCard: userInfoForm.idCard,
              // 移除 moverSpecificField
              // moverSpecificField: userInfoForm.moverSpecificField, // 示例字段
              // averageRating, ratingCount 不应由前端修改
            }),
            // 如果某些字段是所有角色都可以修改的通用字段，且后端接口是通用的，可以放在这里
            // email: userInfoForm.email, // 假设所有角色都可以修改 email
            // phone: userInfoForm.phone, // 假设所有角色都可以修改 phone
          };

          // 过滤掉值为 null/undefined 的字段 (但保留 photoUrl 为 "" 的情况)
          const payload = {};
          for (const key in updateData) {
            // 确保 id 总是包含在 payload 中
            if (key === 'id') {
              payload[key] = updateData[key];
              continue;
            }
            // 过滤掉 null 和 undefined，但允许空字符串 ""
            if (updateData[key] !== null && updateData[key] !== undefined) {
              payload[key] = updateData[key];
            }
          }


          console.log('保存用户信息 payload:', payload); // 调试用

          let res;
          // 根据用户角色调用不同的 API
          if (role === 'admin') {
            res = await updateBackAdminInfoApi(payload); // 确保 payload 包含管理员允许修改的字段
          } else if (role === 'driver') {
            res = await updateBackDriverInfoApi(payload); // 确保 payload 包含司机允许修改的字段
          } else if (role === 'mover') {
            res = await updateBackMoverInfoApi(payload); // *** 为搬家工人调用对应的 API ***
          } else {
            ElMessage.error('未知用户角色，无法保存信息。');
            return;
          }

          if (res.data.code === 1) {
            ElMessage.success(`${role === 'admin' ? '管理员' : role === 'driver' ? '司机' : '搬家工人'}信息更新成功！`);
            isEditingInfo.value = false; // 保存成功后退出编辑状态

            // !!! 关键：信息更新成功后，重新获取最新数据并更新 store !!!
            await fetchUserInfo();
          } else {
            // 业务失败 (code !== 1)，且非登录过期 (request.js 已经处理了)
            console.warn(`${role}信息更新业务失败:`, res.data?.msg);
          }
        } catch (error) {
          // 捕获真正的请求错误 (网络问题、HTTP 错误等)
          console.error('API调用捕获到异常:', error);
        }
      } else {
        ElMessage.warning('请检查填写的信息');
      }
    });
  };

  // 触发头像文件选择
  const handleAvatarUpload = () => {
    // 只有在编辑状态下才允许上传
    if (isEditingInfo.value) {
      avatarInput.value?.click();
    }
  };

  // 处理文件选择后的逻辑 (实现实际的上传API调用)
  const onFileChange = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    const maxSize = 5 * 1024 * 1024; // 5MB

    if (file.size > maxSize) {
      ElMessage.error('图片大小不能超过 5MB！');
      event.target.value = ''; // 清空文件输入框
      return;
    }

    try {
      const formData = new FormData();
      formData.append('file', file); // 这里的 'file' 对应后端接收文件的字段名

      const { data: uploadRes } = await uploadBackFileApi(formData);

      if (uploadRes.code === 1 && uploadRes.data) {
        ElMessage.success('头像上传成功！');
        const newPhotoUrl = uploadRes.data;
        userInfoForm.photoUrl = newPhotoUrl; // !!! 只更新本地表单状态 !!!

        // 更新原始数据，防止在未保存个人信息时取消编辑导致头像回退
        if (isEditingInfo.value) {
          originalUserInfo.photoUrl = newPhotoUrl;
        }

      } else {
        // 业务失败 (code !== 1)，且非登录过期 (request.js 已经处理了)
        console.warn('头像上传业务失败:', uploadRes.msg);
      }
    } catch (error) {
      // 捕获真正的请求错误 (网络问题、HTTP 错误等)
      console.error('uploadBackFileApi 调用捕获到异常:', error);
    } finally {
      event.target.value = ''; // 清空文件输入框
    }
  };

  // 组件挂载时获取用户信息
  onMounted(() => {
    fetchUserInfo();
  });
</script>

<template>
  <div class="personal-info-container">
    <h2 class="page-title">
      {{ currentUserRole === 'admin' ? '管理员' : currentUserRole === 'driver' ? '司机' : currentUserRole === 'mover' ? '搬家工人' : '用户' }}个人信息
    </h2>

    <div class="info-section">
      <el-form :model="userInfoForm" :rules="userInfoRules" ref="userInfoFormRef"
               label-width="100px">

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="头像">
              <div class="avatar-upload-box">
                <img :src="userInfoForm.photoUrl || defaultAvatar" alt="头像" class="user-avatar" />
                <el-button v-if="isEditingInfo" type="primary" :icon="Upload" size="small"
                           @click="handleAvatarUpload" style="margin-left: 20px">
                  上传头像
                </el-button>
                <input type="file" ref="avatarInput" style="display: none" @change="onFileChange"
                       accept="image/*" />
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="userInfoForm.username" :disabled="!isEditingInfo"></el-input>
            </el-form-item>
            <el-form-item label="姓名" prop="name">
              <el-input v-model="userInfoForm.name" :disabled="!isEditingInfo"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <template v-if="currentUserRole === 'driver'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-select v-model="userInfoForm.gender" placeholder="请选择性别"
                           :disabled="!isEditingInfo">
                  <el-option label="男" value="男"></el-option>
                  <el-option label="女" value="女"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="userInfoForm.phone" :disabled="!isEditingInfo"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="身份证号" prop="idCard">
                <el-input v-model="userInfoForm.idCard" :disabled="!isEditingInfo"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="驾龄/年" prop="drivingYears">
                <el-input v-model.number="userInfoForm.drivingYears" :disabled="!isEditingInfo"
                          type="number" @blur="clampDrivingYears"> </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="平均评分">
                {{ userInfoForm.averageRating !== undefined && userInfoForm.averageRating !== null ? userInfoForm.averageRating.toFixed(2) : '暂无评分' }}
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="评分次数">
                {{ userInfoForm.ratingCount !== undefined && userInfoForm.ratingCount !== null ? userInfoForm.ratingCount : 0 }}
                次
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <template v-if="currentUserRole === 'mover'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-select v-model="userInfoForm.gender" placeholder="请选择性别"
                           :disabled="!isEditingInfo">
                  <el-option label="男" value="男"></el-option>
                  <el-option label="女" value="女"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="userInfoForm.phone" :disabled="!isEditingInfo"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="身份证号" prop="idCard">
                <el-input v-model="userInfoForm.idCard" :disabled="!isEditingInfo"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="平均评分">
                {{ userInfoForm.averageRating !== undefined && userInfoForm.averageRating !== null ? userInfoForm.averageRating.toFixed(2) : '暂无评分' }}
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="评分次数">
                {{ userInfoForm.ratingCount !== undefined && userInfoForm.ratingCount !== null ? userInfoForm.ratingCount : 0 }}
                次
              </el-form-item>
            </el-col>
            <el-col :span="12"></el-col>
          </el-row>
        </template>

        <el-row :gutter="20">
          <el-col :span="12" v-if="userInfoForm.createTime">
            <el-form-item label="创建时间">{{ userInfoForm.createTime }}</el-form-item>
          </el-col>
          <el-col :span="12" v-if="userInfoForm.updateTime">
            <el-form-item label="更新时间">{{ userInfoForm.updateTime }}</el-form-item>
          </el-col>
        </el-row>
        <template v-if="currentUserRole === 'admin'">
          <el-row :gutter="20">
            <el-col :span="12"
                    v-if="userInfoForm.createUserName !== undefined && userInfoForm.createUserName !== null">
              <el-form-item label="创建人">{{ userInfoForm.createUserName }}</el-form-item>
            </el-col>
            <el-col :span="12"
                    v-if="userInfoForm.updateUserName !== undefined && userInfoForm.updateUserName !== null">
              <el-form-item label="更新人">{{ userInfoForm.updateUserName }}</el-form-item>
            </el-col>
          </el-row>
        </template>

        <el-form-item class="form-action-buttons">
          <el-button type="primary" @click="toggleEditingInfo">{{
            isEditingInfo ? '取消编辑' : '编辑信息'
          }}</el-button>
          <el-button type="success" @click="saveUserInfo"
                     :disabled="!isEditingInfo">保存信息</el-button>
        </el-form-item>

      </el-form>
    </div>
  </div>
</template>

<style scoped lang="less">
  /* 复用 UserPersonalInfo.vue 的样式，并根据需要微调 */
  .personal-info-container {
    padding: 20px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);

    .page-title {
      font-size: 24px;
      color: #333;
      margin-bottom: 20px;
      text-align: center;
      /* 标题居中 */
    }

    .info-section {
      margin-bottom: 30px;
      padding: 20px;
      border: 1px solid #eee;
      border-radius: 8px;
    }

    .avatar-upload-box {
      display: flex;
      align-items: center;

      .user-avatar {
        width: 100px;
        // 头像更大
        height: 100px;
        // 头像更大
        border-radius: 50%;
        object-fit: cover;
        border: 3px solid #eee;
        // 更明显的头像边框
        flex-shrink: 0;
        // 防止头像被压缩
      }

      .el-button {
        margin-left: 30px !important;
        // 调整按钮与头像的距离
      }
    }

    // 调整 Element Plus 表单项的样式
    // 注意：这里使用 ::v-deep 或 >>> 进行样式穿透，以影响 Element Plus 内部元素的样式
    ::v-deep(.el-form-item__label) {
      font-weight: bold;
    }

    ::v-deep(.el-input.is-disabled .el-input__inner),
    ::v-deep(.el-select .el-input.is-disabled .el-input__inner),
    ::v-deep(.el-textarea.is-disabled .el-textarea__inner) {
      color: #606266;
      /* Disabled input text color */
      -webkit-text-fill-color: #606266;
      /* For Chrome autofill */
      background-color: #f5f7fa;
      /* Disabled background color */
      box-shadow: none;
      /* 移除阴影 */
      cursor: default;
      /* 鼠标样式 */
    }

    ::v-deep(.el-select.is-disabled .el-input__suffix) {
      cursor: default;
    }

    /* 按钮居中样式 */
    .form-action-buttons {
      display: flex;
      justify-content: center;
      margin-top: 30px;

      ::v-deep(.el-form-item__content) {
        justify-content: center;
        margin-left: 0 !important;
      }

      .el-button {
        margin: 0 10px;
      }
    }
  }
</style>