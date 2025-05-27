<template>
  <div class="admin-mover-list-container">
    <h3>搬家工人管理</h3>

    <el-form label-position="top" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.isBanned" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="false"></el-option>
            <el-option label="已封禁" :value="true"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="平均评分">
          <div class="range-inputs">
            <el-input-number v-model="searchForm.minAverageRating" :min="1" :max="5" :precision="1"
                             :step="0.1" controls-position="right" placeholder="最低" clearable
                             style="width: 100px"></el-input-number>
            <span class="range-separator">至</span>
            <el-input-number v-model="searchForm.maxAverageRating"
                             :min="searchForm.minAverageRating || 1" :max="5" :precision="1"
                             :step="0.1" controls-position="right" placeholder="最高" clearable
                             style="width: 100px"></el-input-number>
          </div>
        </el-form-item>

        <el-form-item label="评价数">
          <div class="range-inputs">
            <el-input-number v-model="searchForm.minRatingCount" :min="0" controls-position="right"
                             placeholder="最低" clearable style="width: 100px"></el-input-number>
            <span class="range-separator">至</span>
            <el-input-number v-model="searchForm.maxRatingCount"
                             :min="searchForm.minRatingCount || 0" controls-position="right"
                             placeholder="最高" clearable style="width: 100px"></el-input-number>
          </div>
        </el-form-item>

      <el-form-item label="注册时间">
        <el-date-picker v-model="searchForm.createTimeRange" type="datetimerange"
                        range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间"
                        value-format="YYYY-MM-DD HH:mm:ss"></el-date-picker>
      </el-form-item>

      <el-form-item class="button-group">
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearchForm">重置</el-button>
        <el-button type="success" @click="handleAddMover"
                   class="add-button-separate">新增工人</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" align="center" width="80"></el-table-column>
      <el-table-column label="头像" width="100" align="center">
        <template #default="scope">
          <img v-if="scope.row.photoUrl" :src="scope.row.photoUrl" alt="搬家工人头像"
               style="width: auto; height: 45px; border-radius: 50%; object-fit: cover" />
          <span v-else>无头像</span>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" width="100" align="center"></el-table-column>
      <el-table-column prop="name" label="姓名" align="center" width="100"></el-table-column>
      <el-table-column prop="phone" label="手机号" width="120" align="center"></el-table-column>
      <el-table-column prop="averageRating" label="平均评分" width="110"
                       align="center"></el-table-column>
      <el-table-column prop="ratingCount" label="评价数" width="100" align="center"></el-table-column>
      <el-table-column label="状态" align="center" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.isBanned ? 'danger' : 'success'">
            {{ scope.row.isBanned ? '已封禁' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180" align="center"></el-table-column>
      <el-table-column label="操作" width="250" align="center" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="showDetails(scope.row)"
                     class="detail-button">详情</el-button>

          <el-switch v-model="scope.row.isBanned" active-text="已封禁" inactive-text="正常"
                     :active-value="true" :inactive-value="false"
                     @change="handleStatusChange(scope.row)"
                     :before-change="() => beforeStatusChange(scope.row)"
                     style="margin-left: 20px"></el-switch>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                   :current-page="pagination.page" :page-sizes="[10, 20, 50, 100]"
                   :page-size="pagination.pageSize" layout="total, sizes, prev, pager, next, jumper"
                   :total="pagination.total" background class="pagination"></el-pagination>

    <el-dialog v-model="dialogVisible" title="搬家工人详情" width="600px">
      <el-form label-width="120px" v-if="currentRowDetails">
        <el-form-item label="ID">{{ currentRowDetails.id }}</el-form-item>
        <el-form-item label="用户名">{{ currentRowDetails.username }}</el-form-item>
        <el-form-item label="姓名">{{ currentRowDetails.name }}</el-form-item>
        <el-form-item label="性别">
          {{
						currentRowDetails.gender === 0 ? '女' : currentRowDetails.gender === 1 ? '男' : '未知'
					}}
        </el-form-item>
        <el-form-item label="手机号">{{ currentRowDetails.phone }}</el-form-item>
        <el-form-item label="身份证号">{{ currentRowDetails.idCard }}</el-form-item>
        <el-form-item label="平均评分">{{ currentRowDetails.averageRating }}</el-form-item>
        <el-form-item label="评价数">{{ currentRowDetails.ratingCount }}</el-form-item>
        <el-form-item label="头像">
          <img v-if="currentRowDetails.photoUrl" :src="currentRowDetails.photoUrl" alt="搬家工人头像"
               style="width: 80px; height: 80px; border-radius: 50%; object-fit: cover" />
          <span v-else>无头像</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-tag :type="currentRowDetails.isBanned ? 'danger' : 'success'">
            {{ currentRowDetails.isBanned ? '已封禁' : '正常' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="注册时间">{{ currentRowDetails.createTime }}</el-form-item>
        <el-form-item label="更新时间">{{ currentRowDetails.updateTime }}</el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="addDialogVisible" title="新增搬家工人" width="400px" @close="resetAddForm">
      <el-form :model="addForm" :rules="addFormRules" ref="addFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="addForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="addForm.password" type="password" placeholder="请输入密码"
                    show-password></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAddForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
  import { ref, onMounted } from 'vue';
  import {
    ElMessage,
    ElMessageBox,
    ElDialog,
    ElForm,
    ElFormItem,
    ElTag,
    ElInput,
    ElInputNumber,
    ElSelect,
    ElDatePicker,
    ElButton,
    ElTable,
    ElTableColumn,
    ElPagination,
    ElSwitch,
    ElAvatar, // Not used but imported
  } from 'element-plus';
  // 导入搬家工人 API
  import { getMoverListPageApi, updateMoverStatusApi } from '@/api/moverApi.js';
  // 导入用户注册 API (通用接口)
  import { userRegisterApi } from '@/api/userApi.js';

  // 表格数据和加载状态
  const tableData = ref([]);
  const loading = ref(false);

  // 分页数据
  const pagination = ref({
    page: 1,
    pageSize: 10,
    total: 0,
  });

  // 搜索表单数据
  const searchForm = ref({
    username: '',
    name: '',
    phone: '',
    isBanned: null, // null 表示不筛选状态
    createTimeRange: null, // 日期范围选择器的绑定值
    // createTimeStart 和 createTimeEnd 会从 createTimeRange 中提取
    minAverageRating: null, // 搬家工人特有搜索条件 (来自提供的 API 示例)
    maxAverageRating: null,
    minRatingCount: null,
    maxRatingCount: null,
  });

  // 详情弹窗状态和当前选中行数据
  const dialogVisible = ref(false); // 控制详情弹窗显示
  const currentRowDetails = ref(null); // 存储当前查看详情的行数据

  // === 新增搬家工人功能相关状态和方法 ===
  const addDialogVisible = ref(false); // 控制新增弹窗显示
  const addFormRef = ref(null); // 新增表单的引用
  const addForm = ref({
    username: '',
    password: '',
    // TODO: 如果注册搬家工人还需要其他字段，请在这里添加
  });
  // 新增表单校验规则
  const addFormRules = ref({
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  });

  // 打开新增搬家工人弹窗
  const handleAddMover = () => {
    resetAddForm(); // 打开前先重置表单
    addDialogVisible.value = true;
  };

  // 重置新增表单
  const resetAddForm = () => {
    if (addFormRef.value) {
      resetAddFormRef.value.resetFields(); // 使用 ref 提供的 resetFields 方法
    }
    // 手动清空其他可能没有校验规则的字段或确保所有字段被清空
    addForm.value = {
      username: '',
      password: '',
      // TODO: 其他字段清空
    };
  };

  // 提交新增表单
  const submitAddForm = async () => {
    // 进行表单校验
    const valid = await addFormRef.value.validate();
    if (!valid) {
      ElMessage.error('请填写完整且符合要求的表单项');
      return;
    }

    // 校验通过，调用注册接口
    try {
      // 构造注册搬家工人的请求体
      const registrationData = {
        username: addForm.value.username,
        password: addForm.value.password,
        role: 'mover', // 指定角色为 mover
        // TODO: 如果注册搬家工人还需要其他字段，请在这里添加并从 addForm.value 中获取
        // name: addForm.value.name,
        // phone: addForm.value.phone,
      };
      const { data: res } = await userRegisterApi(registrationData); // 调用通用用户注册 API

      if (res.code === 1) {
        ElMessage.success('搬家工人添加成功！');
        addDialogVisible.value = false; // 关闭弹窗
        resetAddForm(); // 重置表单数据
        fetchMoverList(); // 刷新搬家工人列表
      } else {
        // 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
        // ElMessage.error(res.msg || '搬家工人添加失败'); // <-- 移除此行
        console.warn('新增搬家工人业务失败:', res.msg); // 可以保留日志
      }
    } catch (error) {
      // 捕获真正的请求错误
      console.error('新增搬家工人请求失败:', error);
      ElMessage.error('新增搬家工人失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
    }
  };

  // === 数据获取方法 ===
  const fetchMoverList = async () => {
    loading.value = true;
    try {
      // 准备请求参数
      const params = {
        page: pagination.value.page,
        pageSize: pagination.value.pageSize,
        username: searchForm.value.username || undefined,
        name: searchForm.value.name || undefined,
        phone: searchForm.value.phone || undefined,
        isBanned: searchForm.value.isBanned !== null ? searchForm.value.isBanned : undefined,
        createTimeStart: searchForm.value.createTimeRange
          ? searchForm.value.createTimeRange[0]
          : undefined,
        createTimeEnd: searchForm.value.createTimeRange
          ? searchForm.value.createTimeRange[1]
          : undefined,
        // 搬家工人特有搜索参数 (来自提供的 API 示例)
        minAverageRating: searchForm.value.minAverageRating || undefined,
        maxAverageRating: searchForm.value.maxAverageRating || undefined,
        minRatingCount: searchForm.value.minRatingCount || undefined,
        maxRatingCount: searchForm.value.maxRatingCount || undefined,
      };

      const { data: res } = await getMoverListPageApi(params); // 调用搬家工人 API

      if (res.code === 1) {
        // 确保后端返回的数据字段名与表格 prop 和详情 v-if 匹配
        // 如果后端返回的 isBanned 是数字0/1，需要转换为 false/true
        // res.data.records.forEach(item => {
        //     item.isBanned = item.isBanned === 1; // 示例转换
        // });
        tableData.value = res.data.records;
        pagination.value.total = res.data.total;
      } else {
        // 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
        // ElMessage.error(res.msg || '获取搬家工人列表失败'); // <-- 移除此行
        console.warn('获取搬家工人列表业务失败:', res.msg); // 可以保留日志
        tableData.value = [];
        pagination.value.total = 0;
      }
    } catch (error) {
      // 捕获真正的请求错误
      console.error('获取搬家工人列表请求失败:', error);
      ElMessage.error('获取搬家工人列表失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
      tableData.value = [];
      pagination.value.total = 0;
    } finally {
      loading.value = false;
    }
  };

  // === 搜索相关方法 ===
  const handleSearch = () => {
    pagination.value.page = 1; // 从第一页开始搜索
    fetchMoverList();
  };

  const resetSearchForm = () => {
    searchForm.value = {
      username: '',
      name: '',
      phone: '',
      isBanned: null,
      createTimeRange: null,
      minAverageRating: null,
      maxAverageRating: null,
      minRatingCount: null,
      maxRatingCount: null,
    };
    handleSearch(); // 重置后立即执行查询，回到第一页
  };

  // === 分页相关方法 ===
  const handleSizeChange = (val) => {
    pagination.value.pageSize = val;
    pagination.value.page = 1; // 切换每页大小时回到第一页
    fetchMoverList();
  };

  const handleCurrentChange = (val) => {
    pagination.value.page = val;
    fetchMoverList();
  };

  // === 封禁/解封操作相关方法 ===

  // 在状态切换前进行确认
  const beforeStatusChange = (row) => {
    const confirmText = row.isBanned
      ? `确定要解封搬家工人 "${row.username}" 吗？`
      : `确定要封禁搬家工人 "${row.username}" 吗？`;
    return ElMessageBox.confirm(confirmText, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(() => {
        return true;
      })
      .catch(() => {
        ElMessage.info('操作已取消');
        return false;
      });
  };

  // 处理状态切换
  const handleStatusChange = async (row) => {
    // row.isBanned 在 beforeStatusChange 确认后已经更新为期望的新状态 (true 或 false)
    const newStatus = row.isBanned ? 1 : 0; // 1 表示封禁，0 表示解封 (对应后端接口)
    const moverId = row.id; // 获取搬家工人ID

    try {
      const { data: res } = await updateMoverStatusApi(moverId, newStatus); // 调用更新搬家工人状态 API

      if (res.code === 1) {
        ElMessage.success(`${newStatus === 1 ? '封禁' : '解封'}成功！`);
        // 状态已由 switch 自动更新
      } else {
        // 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
        // ElMessage.error(res.msg || `${newStatus === 1 ? '封禁' : '解封'}失败`); // <-- 移除此行
        console.warn('更新搬家工人状态业务失败:', res.msg); // 可以保留日志
        // 操作失败，手动将 switch 状态拨回原样
        row.isBanned = !row.isBanned;
      }
    } catch (error) {
      // 捕获真正的请求错误
      console.error('更新搬家工人状态请求失败:', error);
      ElMessage.error(`${newStatus === 1 ? '封禁' : '解封'}失败，请稍后再试`); // <-- 这个用于网络或HTTP错误
      // 操作失败，手动将 switch 状态拨回原样
      row.isBanned = !row.isBanned;
    }
  };

  // === 查询详情相关方法 ===
  // 实现 showDetails 方法 (这个函数不调用 API，直接显示当前行数据)
  const showDetails = (row) => {
    // 如果需要通过API获取完整详情，则需要修改此处调用详情API
    // currentRowDetails.value = null; // 如果是API获取，先清空旧数据
    // try {
    //     const { data: res } = await getMoverDetailApi(row.id); // 假设有详情API
    //     if (res.code === 1 && res.data) {
    //         currentRowDetails.value = res.data;
    //     } else {
    //         ElMessage.error(res.msg || '获取详情失败');
    //         dialogVisible.value = false;
    //     }
    // } catch (error) {
    //     console.error('获取详情请求失败:', error);
    //     ElMessage.error('获取详情失败，请稍后再试');
    //     dialogVisible.value = false;
    // }

    // 目前代码是直接显示当前行数据，只需要赋值和打开弹窗
    currentRowDetails.value = row; // 将当前行的数据赋值给详情状态变量
    dialogVisible.value = true; // 打开弹窗
  };

  // === 组件挂载后，首次加载数据 ===
  onMounted(() => {
    fetchMoverList();
  });
</script>

<style scoped lang="less">
  .admin-mover-list-container {
    padding: 20px; // 整体内边距
    background-color: #fff; // 整体白色背景
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    h3 {
      font-size: 22px;
      margin-bottom: 20px;
      color: #333;
      border-bottom: 1px solid #eee;
      padding-bottom: 15px;
    }

    .search-form {
      margin-bottom: 20px;
      padding: 15px;
      background-color: #f8f8f8; // 搜索区域背景色
      border-radius: 6px;

      display: flex;
      flex-wrap: wrap; // 允许换行
      align-items: center;

      .input-items-group {
        display: flex;
        flex-wrap: wrap; // 允许换行，以应对窄屏幕
        align-items: center;
        width: 100%; // 确保组占据一行以便均匀分布
        margin-bottom: 10px; // 组下方添加垂直间距

        // 使子元素（el-form-item 或其他直接子元素）在水平方向上均匀分布
        justify-content: space-between;

        // 移除或调整原有的 margin-right 规则，因为 space-between 会创建间隔
        .el-form-item {
          margin-right: 0 !important; // 确保不被其他规则影响
          margin-bottom: 10px; // 换行时保留垂直间距
          // 如果希望每个表单项固定宽度并自动换行，可以使用 width: calc(XX% - YYpx)
          // width: calc(25% - 20px); // 例如，一行四个，考虑间距
          // 或者让 el-form-item 自己决定宽度，让 flex-wrap 处理换行
          flex-basis: auto; // 允许 flex 项基于其内容自动调整大小
          flex-grow: 0; // 不拉伸
        }
      }

      .range-filters-group {
        // 调整范围筛选组的布局，使其子项（如平均评分、评价数）在一行内分布
        justify-content: flex-start; // 或者 center, space-around
        gap: 20px; // 使用 gap 创建列间距

        .el-form-item {
          margin-bottom: 10px;
          margin-right: 0 !important; // 确保不受其他 margin-right 影响
        }
      }

      .range-inputs {
        display: flex; // 使得范围输入框和分隔符在同一行
        align-items: center; // 垂直居中对齐
      }

      .range-separator {
        margin: 0 5px; // 控制分隔符与前后输入框的水平间距，数值小一点
        color: #606266; // 保持文字颜色一致
      }

      // 调整 search-form 直接子元素的 el-form-item 的垂直间距
      > .el-form-item {
        margin-bottom: 10px; // 在日期选择器和按钮组下方添加 10px 垂直间距
        margin-right: 15px; // 在日期选择器后添加 15px 水平间距
      }

      // === 风格统一的表单项样式 ===
      .el-form-item {
        :deep(.el-input__wrapper),
        :deep(.el-select__wrapper),
        :deep(.el-input-number__input-wrap),
        :deep(.el-date-editor .el-input__wrapper) {
          border-color: #ccc !important;
          border-width: 1px !important;
          border-style: solid !important;
          box-shadow: none !important;
        }
        :deep(.el-input__wrapper.is-focus),
        :deep(.el-select__wrapper.is-focus),
        :deep(.el-input-number__input-wrap.is-focus),
        :deep(.el-date-editor .el-input__wrapper.is-focus) {
          box-shadow: none !important;
        }
        :deep(.el-input__inner::placeholder),
        :deep(.el-select__placeholder),
        :deep(.el-range-input::placeholder) {
          color: #999;
        }
        :deep(.el-input__inner),
        :deep(.el-select__inner),
        :deep(.el-range-input) {
          font-size: 14px;
        }
        :deep(.el-input-number__inner) {
          font-size: 14px;
          text-align: left;
        }

        .el-form-item__label {
          padding-right: 8px;
          line-height: 32px;
          font-weight: bold; // 标签文字加粗
          color: #555; // 标签文字颜色
        }
        .el-form-item__content {
          line-height: 32px;
        }

        // === 设置输入框、选择框、数字输入框、日期选择器组件的固定宽度 ===
        :deep(.el-input),
        :deep(.el-select) {
          width: 200px; /* 基础宽度 */
        }
        :deep(.el-input-number) {
          // 通用设置，但范围输入框内的 style 优先级更高
        }

        :deep(.el-date-editor--datetimerange) {
          width: 380px;
        }
        :deep(.el-input__inner),
        :deep(.el-select__inner),
        :deep(.el-range-input),
        :deep(.el-input__wrapper),
        :deep(.el-select__wrapper),
        :deep(.el-input-number__input-wrap),
        :deep(.el-date-editor .el-input__wrapper) {
          // 通用设置，但 range-inputs 内部的 style 优先级更高
        }
      }

      .button-group {
        margin-top: 28px;
      }
    }
    .el-table {
      margin-bottom: 20px;

      :deep(.el-table__cell) {
        padding: 8px 10px;
      }
      :deep(.el-table__header-wrapper th) {
        background-color: #f5f7fa;
        color: #606266;
        font-weight: bold;
        padding: 8px 10px;
      }
      // 调整表格内容水平对齐方式为 center
      :deep(.el-table__cell .cell) {
        display: flex;
        align-items: center;
        justify-content: center;
        // 解决内容溢出问题，如果列宽固定且内容可能超出
        // overflow: hidden;
        // text-overflow: ellipsis;
        // white-space: nowrap;
      }

      .detail-button {
        background-color: #f0f0f0 !important;
        border: 1px solid #dcdcdc !important;
        color: #333 !important;
        border-radius: 2px !important;
      }
    }

    .pagination {
      justify-content: flex-end;
      margin-top: 20px;
    }

    // 对话框样式
    .el-dialog {
      .el-dialog__header {
        border-bottom: 1px solid #eee;
        padding: 20px;
        .el-dialog__title {
          font-size: 18px;
          font-weight: bold;
        }
      }

      .el-dialog__body {
        padding: 20px;
        // padding-bottom: 0; // 如果底部有 footer，可以减小底部内边距
      }

      .el-form {
        // 对话框内部表单样式
        .el-form-item {
          margin-bottom: 15px; // 对话框表单项下方间距
          .el-form-item__label {
            font-weight: bold;
            color: #555;
          }
          .el-form-item__content {
            color: #333;
            line-height: 1.5;
            word-break: break-all; // 避免长文本溢出
            img {
              vertical-align: middle;
            }
            .el-tag {
              vertical-align: middle;
            }
          }
          .el-form-item__label-wrap {
            margin-right: 12px;
          }
        }
      }

      .el-dialog__footer {
        border-top: 1px solid #eee;
        padding: 15px 20px;
        .dialog-footer {
          text-align: right;
        }
      }
    }
  }
</style>
