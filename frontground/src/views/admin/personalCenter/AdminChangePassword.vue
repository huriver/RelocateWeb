<template>
  <div class="admin-change-password-container">
    <el-card class="password-card">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>

      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules"
               label-width="100px" @submit.prevent="submitForm">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password
                    placeholder="请输入原密码"></el-input>
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password
                    placeholder="请输入新密码"></el-input>
        </el-form-item>

        <el-form-item label="确认新密码" prop="rePassword">
          <el-input v-model="passwordForm.rePassword" type="password" show-password
                    placeholder="请再次输入新密码"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">确认修改</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
  import { ref, reactive } from 'vue';
  import { ElMessage, ElForm, ElFormItem, ElInput, ElButton, ElCard } from 'element-plus';
  import { changeBackAdminPasswordApi } from '@/api/adminApi'; // 导入管理员修改密码 API
  import { changeBackDriverPasswordApi } from '@/api/driverApi'; // 导入司机修改密码 API
  import { changeBackMoverPasswordApi } from '@/api/moverApi'; // *** 导入搬家工人修改密码 API ***
  import { myStore } from '@/stores/store.js'; // 导入 store
  import { useRouter } from 'vue-router'; // 用于跳转登录页

  // 表单引用
  const passwordFormRef = ref(null);

  const router = useRouter();
  const store = myStore();

  const currentUserRole = store.backUserInfo?.role; // 获取当前用户角色

  // 表单数据
  const passwordForm = reactive({
    oldPassword: '',
    newPassword: '',
    rePassword: '',
  });

  // 表单验证规则
  const passwordRules = reactive({
    oldPassword: [
      { required: true, message: '请输入原密码', trigger: 'blur' },
    ],
    newPassword: [
      { required: true, message: '请输入新密码', trigger: 'blur' },
      { min: 4, max: 20, message: '密码长度应在 4 到 20 个字符', trigger: 'blur' },
      // 可以添加更复杂的密码规则，例如包含字母、数字、特殊字符
      // { pattern: /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{6,20}$/, message: '密码需包含字母、数字和特殊字符', trigger: 'blur' }
    ],
    rePassword: [
      { required: true, message: '请再次输入新密码', trigger: 'blur' },
      {
        validator: (rule, value, callback) => {
          if (value === '') {
            callback(new Error('请再次输入新密码'));
          } else if (value !== passwordForm.newPassword) {
            callback(new Error('两次输入的密码不一致!'));
          } else {
            callback();
          }
        },
        trigger: 'blur',
      },
    ],
  });

  // 加载状态
  const loading = ref(false);

  // 提交表单
  const submitForm = () => {
    if (!passwordFormRef.value) return;
    passwordFormRef.value.validate(async (valid) => {
      if (valid) {
        loading.value = true;
        try {
          let response;
          const passwordData = {
            oldPassword: passwordForm.oldPassword,
            newPassword: passwordForm.newPassword,
            rePassword: passwordForm.rePassword,
          };

          if (currentUserRole === 'admin') {
            response = await changeBackAdminPasswordApi(passwordData);
          } else if (currentUserRole === 'driver') {
            response = await changeBackDriverPasswordApi(passwordData);
          } else if (currentUserRole === 'mover') {
            // *** 调用搬家工人修改密码 API ***
            response = await changeBackMoverPasswordApi(passwordData);
            // 移除之前的 warning 和 early exit
            // ElMessage.warning('搬家工人修改密码功能暂未实现');
            // loading.value = false;
            // return;
          } else {
            ElMessage.error('未知用户角色，无法修改密码');
            loading.value = false;
            return;
          }

          // 只判断业务成功，业务失败的提示由 request.js 统一处理
          if (response.data.code === 1) {
            ElMessage.success('密码修改成功！请使用新密码重新登录');
            resetForm(); // 清空表单
            // 密码修改成功后，清空 store 并跳转到登录页
            store.clearBackSession(); // 清除后台用户的会话
            router.push({ name: 'adminLogin' }); // 跳转到后台登录页
            console.log('密码修改成功，已清除 store 并跳转到后台登录');

          } else {
            // 当 response.code !== 1 时，表示业务失败。
            // request.js 已经根据后端的 response.msg 弹出了错误提示，这里不需要再次弹出了。
            console.warn('密码修改业务失败，错误信息已由拦截器处理', response.data.msg);
            // 如果是业务失败，可以不清空表单和不跳转，让用户看到错误信息并修正
          }
        } catch (error) {
          // catch 块处理的是网络错误、请求取消、或者 request.js 中明确 reject 的情况（如登录过期 HTTP 401）
          // 登录过期的情况 request.js 已经处理并跳转，这里的 error 可能是其他网络问题
          console.error('密码修改请求捕获到异常:', error); // 只留日志
        } finally {
          loading.value = false;
        }
      } else {
        ElMessage.warning('请检查填写的信息');
        return false;
      }
    });
  };

  // 重置表单
  const resetForm = () => {
    if (!passwordFormRef.value) return;
    passwordFormRef.value.resetFields();
  };
</script>

<style scoped>
  .admin-change-password-container {
    display: flex;
    justify-content: center;
    align-items: flex-start; /* 调整垂直对齐方式 */
    padding: 20px;
    background-color: #f5f7fa; /* 轻微的背景色 */
    min-height: calc(
      100vh - var(--el-header-height, 60px) - var(--el-footer-height, 0px)
    ); /* 撑满剩余高度 */
  }

  .password-card {
    width: 100%; /* 默认宽度 */
    max-width: 500px; /* 最大宽度，防止过宽 */
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
    border-radius: 8px; /* 圆角 */
  }

  .card-header {
    font-size: 18px;
    font-weight: bold;
    color: #333;
  }

  /* 可以根据需要调整表单项的间距 */
  .el-form-item {
    margin-bottom: 20px;
  }

  /* 让按钮在表单项内靠左对齐，Element Plus 默认即是 */
  /* .el-form-item__content {
            display: flex;
            justify-content: flex-start;
          } */

  /* 按钮组的样式 */
  .el-form-item .el-button {
    margin-right: 10px; /* 按钮之间的间距 */
  }
</style>