<template>
  <div class="admin-driver-list-container">
    <h3>司机管理</h3>

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

        <el-form-item label="驾龄(年)">
          <div class="range-inputs">
            <el-input-number v-model="searchForm.minDrivingYears" :min="0" controls-position="right"
                             placeholder="最低" clearable style="width: 100px"></el-input-number>
            <span class="range-separator">至</span>
            <el-input-number v-model="searchForm.maxDrivingYears"
                             :min="searchForm.minDrivingYears || 0" controls-position="right"
                             placeholder="最高" clearable style="width: 100px"></el-input-number>
          </div>
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
        <el-button type="success" @click="handleAddDriver"
                   class="add-button-separate">新增司机</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" align="center" width="80"></el-table-column>
      <el-table-column label="头像" width="100" align="center">
        <template #default="scope">
          <img v-if="scope.row.photoUrl" :src="scope.row.photoUrl" alt="司机头像"
               style="width: auto; height: 45px; border-radius: 50%; object-fit: cover" />
          <span v-else>无头像</span>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" width="100" align="center"></el-table-column>
      <el-table-column prop="name" label="姓名" align="center" width="100"></el-table-column>
      <el-table-column prop="phone" label="手机号" width="120" align="center"></el-table-column>
      <el-table-column prop="drivingYears" label="驾龄(年)" width="100"
                       align="center"></el-table-column>
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

    <el-dialog v-model="dialogVisible" title="司机详情" width="600px">
      <el-form label-width="120px" v-if="currentRowDetails">
        <el-form-item label="ID">{{ currentRowDetails.id }}</el-form-item>
        <el-form-item label="用户名">{{ currentRowDetails.username }}</el-form-item>
        <el-form-item label="姓名">{{ currentRowDetails.name }}</el-form-item>
        <el-form-item label="性别">{{
					currentRowDetails.gender === 0 ? '女' : currentRowDetails.gender === 1 ? '男' : '未知'
				}}</el-form-item>
        <el-form-item label="手机号">{{ currentRowDetails.phone }}</el-form-item>
        <el-form-item label="身份证号">{{ currentRowDetails.idCard }}</el-form-item>
        <el-form-item label="驾龄(年)">{{ currentRowDetails.drivingYears }}</el-form-item>
        <el-form-item label="平均评分">{{ currentRowDetails.averageRating }}</el-form-item>
        <el-form-item label="评价数">{{ currentRowDetails.ratingCount }}</el-form-item>
        <el-form-item label="头像">
          <img v-if="currentRowDetails.photoUrl" :src="currentRowDetails.photoUrl" alt="司机头像"
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

    <el-dialog v-model="addDialogVisible" title="新增司机" width="400px" @close="resetAddForm">
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
    ElAvatar,
  } from 'element-plus';
  // 导入司机 API 和 用户注册 API
  import { getDriverListPageApi, updateDriverStatusApi } from '@/api/driverApi.js';
  import { userRegisterApi } from '@/api/userApi.js'; // 导入用户注册 API

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
    minDrivingYears: null, // 司机特有搜索条件
    maxDrivingYears: null,
    minAverageRating: null,
    maxAverageRating: null,
    minRatingCount: null,
    maxRatingCount: null,
  });

  // 详情弹窗状态和当前选中行数据
  const dialogVisible = ref(false);
  const currentRowDetails = ref(null);

  // === 新增司机功能相关状态和方法 ===
  const addDialogVisible = ref(false); // 控制新增弹窗显示
  const addFormRef = ref(null); // 新增表单的引用
  const addForm = ref({
    username: '',
    password: '',
  });
  // 新增表单校验规则
  const addFormRules = ref({
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  });

  // 打开新增司机弹窗
  const handleAddDriver = () => {
    resetAddForm(); // 打开前先重置表单
    addDialogVisible.value = true;
  };

  // 重置新增表单
  const resetAddForm = () => {
    if (addFormRef.value) {
      addFormRef.value.resetFields(); // 重置表单字段和校验状态
    } else {
      // 如果 ref 还没初始化，手动清空数据
      addForm.value = {
        username: '',
        password: '',
      };
    }
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
      // 构造注册司机的请求体
      const registrationData = {
        username: addForm.value.username,
        password: addForm.value.password,
        role: 'driver', // 指定角色为 driver
      };
      const { data: res } = await userRegisterApi(registrationData);

      if (res.code === 1) {
        ElMessage.success('司机添加成功！');
        addDialogVisible.value = false; // 关闭弹窗
        resetAddForm(); // 重置表单数据
        fetchDriverList(); // 刷新司机列表
      } else {
        // 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
        // ElMessage.error(res.msg || '司机添加失败'); // <-- 移除此行
        console.warn('新增司机业务失败:', res.msg); // 可以保留日志
      }
    } catch (error) {
      // 捕获真正的请求错误
      console.error('新增司机请求失败:', error);
      ElMessage.error('新增司机失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
    }
  };

  // === 数据获取方法 ===
  const fetchDriverList = async () => {
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
        // 司机特有搜索参数
        minDrivingYears: searchForm.value.minDrivingYears || undefined,
        maxDrivingYears: searchForm.value.maxDrivingYears || undefined,
        minAverageRating: searchForm.value.minAverageRating || undefined,
        maxAverageRating: searchForm.value.maxAverageRating || undefined,
        minRatingCount: searchForm.value.minRatingCount || undefined,
        maxRatingCount: searchForm.value.maxRatingCount || undefined,
      };

      const { data: res } = await getDriverListPageApi(params); // 调用司机 API

      if (res.code === 1) {
        // 确保后端返回的数据字段名与表格 prop 和详情 v-if 匹配
        tableData.value = res.data.records;
        pagination.value.total = res.data.total;
      } else {
        // 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
        // ElMessage.error(res.msg || '获取司机列表失败'); // <-- 移除此行
        console.warn('获取司机列表业务失败:', res.msg); // 可以保留日志
        tableData.value = [];
        pagination.value.total = 0;
      }
    } catch (error) {
      // 捕获真正的请求错误
      console.error('获取司机列表请求失败:', error);
      ElMessage.error('获取司机列表失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
      tableData.value = [];
      pagination.value.total = 0;
    } finally {
      loading.value = false;
    }
  };

  // === 搜索相关方法 ===
  const handleSearch = () => {
    pagination.value.page = 1; // 从第一页开始搜索
    fetchDriverList();
  };

  const resetSearchForm = () => {
    searchForm.value = {
      username: '',
      name: '',
      phone: '',
      isBanned: null,
      createTimeRange: null,
      minDrivingYears: null,
      maxDrivingYears: null,
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
    fetchDriverList();
  };

  const handleCurrentChange = (val) => {
    pagination.value.page = val;
    fetchDriverList();
  };

  // === 封禁/解封操作相关方法 ===

  // 在状态切换前进行确认
  const beforeStatusChange = (row) => {
    const confirmText = row.isBanned
      ? `确定要解封司机 "${row.username}" 吗？`
      : `确定要封禁司机 "${row.username}" 吗？`;
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
    const driverId = row.id; // 获取司机ID

    try {
      const { data: res } = await updateDriverStatusApi(driverId, newStatus); // 调用更新司机状态 API

      if (res.code === 1) {
        ElMessage.success(`${newStatus === 1 ? '封禁' : '解封'}成功！`);
        // 状态已由 switch 自动更新
      } else {
        // 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
        // ElMessage.error(res.msg || `${newStatus === 1 ? '封禁' : '解封'}失败`); // <-- 移除此行
        console.warn('更新司机状态业务失败:', res.msg); // 可以保留日志
        // 操作失败，手动将 switch 状态拨回原样
        row.isBanned = !row.isBanned;
      }
    } catch (error) {
      // 捕获真正的请求错误
      console.error('更新司机状态请求失败:', error);
      // 操作失败，手动将 switch 状态拨回原样
      row.isBanned = !row.isBanned;
      ElMessage.error('更新司机状态失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
    }
  };

  // === 查询详情相关方法 ===
  const showDetails = (row) => {
    // 注意：这里直接使用了列表项的 row 数据作为详情，没有调用详情接口。
    // 如果详情接口返回更多数据，需要像 AdminNewsList/AdminNoticeList 那样调用详情 API
    currentRowDetails.value = row; // 将当前行的数据赋值给详情状态变量
    dialogVisible.value = true; // 打开弹窗
  };

  // === 组件挂载后，首次加载数据 ===
  onMounted(() => {
    fetchDriverList();
  });
</script>

<style scoped lang="less">
  .admin-driver-list-container {
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

        // 移除或调整原有的 margin-right 和 margin-bottom 规则
        .el-form-item {
          margin-right: 0 !important; // 确保不被其他规则影响
          margin-bottom: 10px; // 换行时保留垂直间距
          flex-grow: 0;
          flex-shrink: 1;
          flex-basis: auto; // 根据内容确定初始大小
        }
      }

      .range-filters-group {
        justify-content: flex-start; // 范围筛选组不需要均匀分布
        gap: 10px 30px; /* 范围筛选组内的行和列间距更大一些 */

        .el-form-item {
          margin-bottom: 0; // 组内的垂直间距由组的 margin-bottom 控制
        }
      }

      .range-inputs {
        display: flex; // 使得范围输入框和分隔符在同一行
        align-items: center; // 垂直居中对齐
        // 如果需要在 range-inputs 内设置宽度：
        // width: 200px; // 或者一个合适的值
      }

      .range-separator {
        margin: 0 5px; // 控制分隔符与前后输入框的水平间距，数值小一点
        color: #606266; // 保持文字颜色一致
      }

      // 调整 search-form 直接子元素的 el-form-item 的垂直间距
      // 这里主要影响日期选择器和按钮组，因为它们不在 input-items-group 内
      > .el-form-item {
        margin-bottom: 10px; // 在日期选择器和按钮组下方添加 10px 垂直间距
        margin-right: 15px; // 在日期选择器后添加 15px 水平间距
        flex-shrink: 0; // 防止缩小
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
        // 针对 input-number 移除内边距以便更好地与分隔符对齐
        :deep(.el-input-number__inner) {
          padding-left: 5px; // Adjust as needed
          padding-right: 5px; // Adjust as needed
        }

        .el-form-item__label {
          padding-right: 8px;
          line-height: 32px;
          font-weight: bold; // 标签文字加粗
          color: #555; // 标签文字颜色
          flex-shrink: 0; // 防止标签被挤压
        }
        .el-form-item__content {
          line-height: 32px;
          flex-grow: 0; // 内容区域不拉伸
          width: auto; // 宽度由内部元素决定
          display: flex; // 使用 flex 布局，让 input/select 更好地根据内容自适应
          align-items: center; // 垂直居中内容
        }

        // === 设置输入框、选择框、数字输入框、日期选择器组件的固定宽度 ===
        :deep(.el-input),
        :deep(.el-select) {
          width: 200px; /* 基础宽度 */
        }
        :deep(.el-input-number) {
          // 通用设置，但范围输入框内的 style 优先级更高
          width: 100% !important; // 使弹窗内的 el-input-number 宽度填充父容器
        }

        :deep(.el-date-editor--datetimerange) {
          width: 380px;
        }
        // 通用设置，但 range-inputs 内部的 style 优先级更高
        :deep(.el-input__inner),
        :deep(.el-select__inner),
        :deep(.el-range-input),
        :deep(.el-input__wrapper),
        :deep(.el-select__wrapper),
        :deep(.el-input-number__input-wrap),
        :deep(.el-date-editor .el-input__wrapper) {
        }
      }

      .button-group {
        margin-top: 28px;
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
        :deep(.el-table__cell .cell) {
          display: flex;
          align-items: center;
          justify-content: center;
          word-break: break-word; /* 允许单词中断 */
          white-space: pre-wrap; /* 保留空白符，但允许换行 */
        }

        .detail-button {
          background-color: #f0f0f0 !important;
          border: 1px solid #dcdcdc !important;
          color: #333 !important;
          border-radius: 2px !important;
        }
        // 调整操作列按钮与 switch 的间距
        .el-button + .el-switch {
          margin-left: 10px;
        }
        .el-switch + .el-button {
          margin-left: 10px; // unlikely but just in case
        }
        .el-button {
          // 确保操作按钮高度一致
          height: 28px; // Element Plus small size default height
        }
      }

      .pagination {
        justify-content: flex-end;
        margin-top: 20px;
      }

      // 详情和新增弹窗的通用样式
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
          padding-bottom: 10px;
        }

        .el-form {
          // 调整弹窗内表单的样式
          .el-form-item {
            margin-bottom: 20px; // 对话框表单项增加下方间距
            .el-form-item__label {
              font-weight: bold;
              color: #555;
              // 详情弹窗 label 宽度是 120px，新增弹窗是 80px
              // 为了样式统一，这里可以单独设置或根据实际情况调整
            }
            .el-form-item__content {
              color: #333;
              line-height: 1.5;
              word-break: break-all; /* 允许单词中断换行 */
              img {
                vertical-align: middle;
              }
              .el-tag {
                vertical-align: middle;
              }
            }
            .el-form-item__label-wrap {
              margin-left: 0 !important;
            }
          }
          // 详情弹窗内的 label 宽度
          & > .el-form-item > .el-form-item__label {
            width: 120px !important;
            text-align: right;
          }
          // 新增弹窗内的 label 宽度
          // 注意：由于 :deep 的限制，直接在父选择器 .el-form-item 内很难区分详情和新增弹窗的 label 宽度
          // 如果需要区分，可以在 el-dialog 上添加 class 或 style 进行更精确的控制
          // 例如: .el-dialog.add-dialog .el-form-item__label { width: 80px !important; }
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
  }
</style>
