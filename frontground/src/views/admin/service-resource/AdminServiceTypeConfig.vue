<template>
  <div class="admin-service-category-container">
    <h3>服务类型配置</h3>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <div class="input-items-group evenly-distributed-group">
        <el-form-item label="类型名称">
          <el-input v-model="searchForm.typeName" placeholder="请输入类型名称关键词" clearable></el-input>
        </el-form-item>
        <el-form-item label="服务类型价格乘数范围">
          <div class="price-multiplier-range">
            <el-input-number v-model="searchForm.minPriceMultiplier" :precision="2" :step="0.05"
                             :min="0" placeholder="最小" controls-position="right"
                             class="range-input-number"></el-input-number>
            <span class="range-separator">至</span>
            <el-input-number v-model="searchForm.maxPriceMultiplier" :precision="2" :step="0.05"
                             :min="0" placeholder="最大" controls-position="right"
                             class="range-input-number"></el-input-number>
          </div>
        </el-form-item>
      </div>

      <div class="input-items-group">
        <el-form-item label="创建日期">
          <el-date-picker v-model="searchForm.createTimeRange" type="daterange" range-separator="至"
                          start-placeholder="开始日期" end-placeholder="结束日期"
                          value-format="YYYY-MM-DD HH:mm:ss" :clearable="true"
                          :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"></el-date-picker>
        </el-form-item>
      </div>

      <el-form-item class="button-group">
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearchForm">重置</el-button>
        <el-button type="success" @click="handleAddServiceCategory"
                   class="add-button-separate">新增服务类型</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" align="center" width="80"></el-table-column>
      <el-table-column prop="typeName" label="类型名称" align="left" min-width="150"
                       show-overflow-tooltip></el-table-column>
      <el-table-column prop="description" label="描述" align="left" min-width="250"
                       show-overflow-tooltip></el-table-column>
      <el-table-column prop="priceMultiplier" label="服务类型价格乘数" width="170"
                       align="center"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" align="center"></el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="180" align="center"></el-table-column>
      <el-table-column prop="createUserName" label="创建人" width="120"
                       align="center"></el-table-column>
      <el-table-column prop="updateUserName" label="更新人" width="120"
                       align="center"></el-table-column>

      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="showDetails(scope.row)"
                     class="detail-button">详情</el-button>
          <el-button size="small" type="primary" style="margin-left: 10px"
                     @click="handleEditServiceCategory(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" style="margin-left: 10px"
                     @click="handleDeleteServiceCategory(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                   :current-page="pagination.page" :page-sizes="[10, 20, 50, 100]"
                   :page-size="pagination.pageSize" layout="total, sizes, prev, pager, next, jumper"
                   :total="pagination.total" background class="pagination"></el-pagination>

    <el-dialog v-model="editDialogVisible" :title="editDialogTitle" width="600px"
               @close="resetEditForm">
      <el-form :model="editForm" :rules="editFormRules" ref="editFormRef" label-width="150px">
        <el-form-item label="类型名称" prop="typeName">
          <el-input v-model="editForm.typeName" placeholder="请输入服务类型名称"></el-input>
        </el-form-item>
        <el-form-item label="服务类型价格乘数" prop="priceMultiplier" style="margin-top: 20px">
          <el-input-number v-model="editForm.priceMultiplier" :precision="2" :step="0.05" :min="0"
                           placeholder="请输入服务类型价格乘数" style="width: 100%"
                           controls-position="right"></el-input-number>
        </el-form-item>
        <el-form-item label="描述" prop="description" style="margin-top: 20px">
          <el-input v-model="editForm.description" type="textarea" :rows="4"
                    placeholder="请输入服务类型描述"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="服务类型详情" width="600px">
      <el-form label-width="150px" v-if="currentRowDetails">
        <el-form-item label="ID">{{ currentRowDetails.id }}</el-form-item>
        <el-form-item label="类型名称">{{ currentRowDetails.typeName }}</el-form-item>

        <el-form-item label="服务类型价格乘数">{{ currentRowDetails.priceMultiplier }}</el-form-item>
        <el-form-item label="描述">
          <div class="service-category-description-display" style="margin-top: -5px">
            {{ currentRowDetails.description }}
          </div>
        </el-form-item>
        <el-form-item label="创建时间">{{ currentRowDetails.createTime }}</el-form-item>
        <el-form-item label="创建人">{{ currentRowDetails.createUserName }}</el-form-item>
        <el-form-item label="更新时间">{{ currentRowDetails.updateTime }}</el-form-item>
        <el-form-item label="更新人">{{ currentRowDetails.updateUserName }}</el-form-item>
      </el-form>
      <div v-else>正在加载服务类型详情...</div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
  import { ref, onMounted, computed } from 'vue';
  import {
    ElMessage,
    ElMessageBox,
    ElDialog,
    ElForm,
    ElFormItem,
    ElTag, // Not used in this component, but keep if styles depend on it
    ElInput,
    ElSelect, // Not used in this component, but keep if styles depend on it
    ElDatePicker,
    ElButton,
    ElTable,
    ElTableColumn,
    ElPagination,
    ElSwitch, // Not used in this component, but keep if styles depend on it
    ElOption, // Not used in this component, but keep if styles depend on it
    ElInputNumber, // Needed for price multiplier
  } from 'element-plus';

  // 导入后台服务类型 API
  import {
    getBackServiceCategoryListPageApi,
    getBackServiceCategoryDetailApi,
    addBackServiceCategoryApi,
    updateBackServiceCategoryApi,
    deleteBackServiceCategoryApi,
    // getBackServiceCategoryListApi, // 导入非分页列表API，如果需要
  } from '@/api/serviceCategoryApi.js'; // <-- 导入服务类型 API

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
    typeName: '', // 类型名称关键词
    minPriceMultiplier: null, // 最小服务类型价格乘数
    maxPriceMultiplier: null, // 最大服务类型价格乘数
    createTimeRange: null, // 创建时间范围 [start, end]
  });

  // 新增/修改弹窗状态和表单数据
  const editDialogVisible = ref(false); // 控制新增/修改弹窗显示
  const editDialogTitle = computed(() => (editForm.value.id ? '修改服务类型' : '新增服务类型')); // 弹窗标题
  const editFormRef = ref(null); // 新增/修改表单的引用
  const editForm = ref({
    id: null, // ID 为 null 表示新增
    typeName: '',
    description: '',
    priceMultiplier: null, // 使用 priceMultiplier 字段
  });
  // 新增/修改表单校验规则
  const editFormRules = ref({
    typeName: [{ required: true, message: '请输入服务类型名称', trigger: 'blur' }],
    description: [{ required: true, message: '请输入服务类型描述', trigger: 'blur' }],
    priceMultiplier: [
      { required: true, message: '请输入服务类型价格乘数', trigger: 'change' }, // Trigger on change for input-number
      {
        type: 'number',
        min: 0.01, // 服务类型价格乘数通常大于0
        message: '服务类型价格乘数必须是大于0的数字',
        trigger: ['blur', 'change'],
      },
    ],
  });

  // 详情弹窗状态和当前选中行数据
  const detailDialogVisible = ref(false); // 控制详情弹窗显示
  const currentRowDetails = ref(null); // 存储当前查看详情的行数据

  // === 数据获取方法 ===
  const fetchServiceCategoryList = async () => {
    loading.value = true;
    try {
      // 准备请求参数
      const params = {
        page: pagination.value.page,
        pageSize: pagination.value.pageSize,
        typeName: searchForm.value.typeName || undefined,
        minPriceMultiplier: searchForm.value.minPriceMultiplier || undefined,
        maxPriceMultiplier: searchForm.value.maxPriceMultiplier || undefined,
        createTimeStart: searchForm.value.createTimeRange
          ? searchForm.value.createTimeRange[0]
          : undefined,
        createTimeEnd: searchForm.value.createTimeRange
          ? searchForm.value.createTimeRange[1]
          : undefined,
      };

      const { data: res } = await getBackServiceCategoryListPageApi(params); // 调用后台服务类型分页 API

      if (res.code === 1) {
        tableData.value = res.data.records;
        pagination.value.total = res.data.total;
      } else {
        ElMessage.error(res.msg || '获取服务类型列表失败');
        tableData.value = [];
        pagination.value.total = 0;
      }
    } catch (error) {
      console.error('获取服务类型列表请求失败:', error);
      ElMessage.error('获取服务类型列表失败，请稍后再试');
      tableData.value = [];
      pagination.value.total = 0;
    } finally {
      loading.value = false;
    }
  };

  // === 搜索相关方法 ===
  const handleSearch = () => {
    pagination.value.page = 1; // 从第一页开始搜索
    fetchServiceCategoryList();
  };

  const resetSearchForm = () => {
    searchForm.value = {
      typeName: '',
      minPriceMultiplier: null,
      maxPriceMultiplier: null,
      createTimeRange: null,
    };
    handleSearch(); // 重置后立即执行查询，回到第一页
  };

  // === 分页相关方法 ===
  const handleSizeChange = (val) => {
    pagination.value.pageSize = val;
    pagination.value.page = 1; // 切换每页大小时回到第一页
    fetchServiceCategoryList();
  };

  const handleCurrentChange = (val) => {
    pagination.value.page = val;
    fetchServiceCategoryList();
  };

  // === 新增/修改服务类型相关方法 ===

  // 打开新增服务类型弹窗
  const handleAddServiceCategory = () => {
    resetEditForm(); // 打开前先重置表单，确保是新增模式
    editDialogVisible.value = true;
  };

  // 打开修改服务类型弹窗
  const handleEditServiceCategory = (row) => {
    // 将当前行的数据填充到表单，进入修改模式
    // 这里直接使用 row 数据填充，因为API示例的PUT只需要这些字段
    editForm.value = { ...row };
    editDialogVisible.value = true;
  };

  // 提交新增/修改表单
  const submitEditForm = async () => {
    // 进行表单校验
    const valid = await editFormRef.value.validate();
    if (!valid) {
      // 前端校验失败，Element Plus 会在输入框下方提示错误信息
      ElMessage.error('请填写完整且符合要求的表单项'); // 额外弹出提示
      return;
    }

    // 前端校验通过，判断是新增还是修改
    const isAdding = !editForm.value.id; // 根据是否有 ID 判断模式

    try {
      const apiCall = isAdding ? addBackServiceCategoryApi : updateBackServiceCategoryApi;
      // 注意：updateBackServiceCategoryApi 期望整个包含 id 的对象
      const { data: res } = await apiCall(editForm.value);

      if (res.code === 1) {
        // 后端返回成功
        ElMessage.success(isAdding ? '服务类型添加成功！' : '服务类型修改成功！');
        editDialogVisible.value = false; // 关闭弹窗
        resetEditForm(); // 重置表单数据
        fetchServiceCategoryList(); // 刷新列表
      } else {
        // 后端返回业务错误 (code !== 1)
        ElMessage.error(res.msg || (isAdding ? '服务类型添加失败' : '服务类型修改失败'));
        // 保持弹窗打开，以便用户根据后端错误信息修改输入
      }
    } catch (error) {
      // 请求本身发生错误 (网络问题, CORS, request.js 拦截器抛出错误等)
      console.error('提交服务类型请求失败:', error);

      const specificErrorMessage =
        error.message || (error.response && error.response.data && error.response.data.msg);

      const errorMessageToDisplay =
        specificErrorMessage &&
          specificErrorMessage !== '未知错误' &&
          specificErrorMessage !== 'Internal Server Error'
          ? `操作失败: ${specificErrorMessage}`
          : isAdding
            ? '新增服务类型失败，请稍后再试'
            : '修改服务类型失败，请稍后再试';

      ElMessage.error(errorMessageToDisplay);

      // 如果是新增操作失败，并且弹窗应该保持打开以修改，这里不需要关闭弹窗和重置
      // 如果希望失败后弹窗自动关闭并清空，可以在这里调用 resetEditForm() 和设置 editDialogVisible.value = false;
    }
  };

  // 重置新增/修改表单
  const resetEditForm = () => {
    if (editFormRef.value) {
      editFormRef.value.resetFields();
    }
    // 手动确保 id 为 null，以正确判断新增模式，并清空其他字段值
    editForm.value = {
      id: null,
      typeName: '',
      description: '',
      priceMultiplier: null,
    };
  };

  // === 删除服务类型相关方法 ===
  const handleDeleteServiceCategory = (row) => {
    ElMessageBox.confirm(`确定要删除服务类型《${row.typeName}》吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        // 注意：如果修改了 request.js 不再 reject，这里就不用 try...catch 了
        // 如果保留了 try...catch，当后端 code !== 1 时，不会进入 catch 块

        const { data: res } = await deleteBackServiceCategoryApi(row.id); // 调用删除 API

        // 在 .then 块中处理业务结果
        if (res.code === 1) {
          ElMessage.success('服务类型删除成功！');
          // 删除成功后判断是否需要留在当前页或跳转到上一页
          if (tableData.value.length === 1 && pagination.value.page > 1) {
            pagination.value.page--;
          }
          fetchServiceCategoryList(); // 刷新列表
        } else {
          // res.code !== 1，业务失败。错误提示已经在 request.js 拦截器中弹出，这里无需再次弹出。
          // 可以选择性地在控制台打印一下日志：
          console.warn('服务类型删除业务失败:', res.msg);
          // ElMessage.error(res.msg || '服务类型删除失败'); // 这行应该移除或注释掉，避免重复提示
        }
      })
      .catch((error) => {
        // 这个 catch 只会捕获真正的请求错误（例如网络中断、超时、或者 request.js 中仍然 reject 的登录过期等情况）
        console.error('服务类型删除请求失败:', error);
        // 对于真正的请求错误，才弹出通用提示
        // 避免在业务错误（code !== 1）时也弹出这个通用提示
        // 检查 error 是否是拦截器 reject 的 Error 对象，或者是否有 response 属性等来区分
        // 简单处理：如果 error 不是由 request.js 的业务错误 reject 来的（例如是网络错误或其他 unexpected 错误），再弹出通用提示。
        // 如果修改了 request.js，这个 catch 更多是处理 HTTP 错误或网络问题。
        if (
          error.message !==
          '当前服务分类关联了服务,不能删除' /* 避免捕获到拦截器原先 reject 的特定业务错误 */
        ) {
          ElMessage.error('删除服务类型失败，请稍后再试'); // <-- 保留用于非业务错误
        } else {
          // 如果捕获到的是拦截器 reject 的业务错误，说明 request.js 的修改不完整，或者这里的 catch 处理需要更精细
          // 但如果 request.js 已经修改为不再 reject 业务错误，这部分代码将不会在业务错误时被执行到。
        }

        // ElMessage.info('已取消删除'); // 这个是用户点击取消时执行的，不应放在 .then/.catch 链式调用里
      })
      .catch(() => {
        // 这是 ElMessageBox.confirm 的 catch，处理用户点击取消
        ElMessage.info('已取消删除');
      });
  };

  // === 查看服务类型详情相关方法 ===
  const showDetails = async (row) => {
    detailDialogVisible.value = true;
    // 在加载新的详情数据前，清空旧的数据，以免显示陈旧信息
    currentRowDetails.value = null;

    try {
      // 调用后台服务类型详情 API 根据ID获取完整的详情数据
      const { data: res } = await getBackServiceCategoryDetailApi(row.id);

      if (res.code === 1 && res.data) {
        currentRowDetails.value = res.data; // 使用详情接口返回的数据更新 currentRowDetails
      } else {
        ElMessage.error(res.msg || '获取服务类型详情失败');
        detailDialogVisible.value = false; // 获取失败则关闭弹窗
      }
    } catch (error) {
      console.error('获取服务类型详情请求失败:', error);
      ElMessage.error('获取服务类型详情失败，请稍后再试');
      detailDialogVisible.value = false; // 请求失败则关闭弹窗
    }
  };

  // === 组件挂载后，首次加载数据 ===
  onMounted(() => {
    fetchServiceCategoryList(); // 获取服务类型列表
  });
</script>

<style scoped lang="less">
  .admin-service-category-container {
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
      gap: 10px 20px; /* 行和列之间的间距 */

      .input-items-group {
        display: flex;
        flex-wrap: wrap; // 允许换行，以应对窄屏幕
        align-items: center;
        width: 100%; // 确保组占据一行以便均匀分布
        margin-bottom: 0; // 组下方添加垂直间距
        gap: 10px 20px; /* 组内的行和列间距 */

        .el-form-item {
          margin-right: 0 !important; // 确保不被其他规则影响
          margin-bottom: 0 !important; // 确保不被其他规则影响，使用 gap 控制垂直间距
          flex-grow: 0; // 不拉伸
          flex-shrink: 1; // 允许缩小
          flex-basis: auto; // 根据内容确定初始大小
        }
      }

      // 调整 search-form 直接子元素的 el-form-item 的垂直间距
      > .el-form-item {
        flex-shrink: 0; // 防止日期选择器或按钮组缩小
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
        :deep(.el-range-input::placeholder),
        :deep(.el-input-number__inner::placeholder) {
          // Added placeholder for input-number
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
          flex-grow: 0;
          width: auto;
          display: flex; /* 使内部元素更好地对齐 */
          align-items: center;
        }

        // === 设置输入框、选择框、数字输入框、日期选择器组件的固定宽度 ===
        :deep(.el-input),
        :deep(.el-select) {
          width: 200px; /* 基础宽度 */
        }

        // Specific width for price multiplier range inputs
        .price-multiplier-range {
          display: flex;
          align-items: center;
          .range-input-number {
            width: 100px; // Adjust width for range inputs
          }
          .range-separator {
            margin: 0 8px; // Space between input numbers
            color: #606266;
          }
        }

        :deep(.el-date-editor--daterange) {
          width: 240px; /* 日期范围选择器宽度 */
        }
        // Specific width for standalone input-number in dialog
        :deep(.el-input-number) {
          width: 100%; /* Make standalone input-number fill container */
        }
      }

      // === 针对第一个分组的均匀分布样式 ===
      .evenly-distributed-group {
        .el-form-item {
          flex-grow: 1; // 让 form item 增长
          flex-basis: 0; // 使其起始基准为 0，更容易实现均匀分布
          min-width: 150px; // 设置一个最小宽度防止挤压过窄

          :deep(.el-form-item__content) {
            flex-grow: 1; // 内容区域也增长
            width: 100%; // 让内部 input/select/range 填充
          }

          :deep(.el-input),
          :deep(.el-select) {
            width: 100% !important; // input/select 填充父级内容区域
            min-width: auto; // 移除通用最小宽度限制
          }

          .price-multiplier-range {
            width: 100%; // Make the range container fill its space
            .range-input-number {
              flex-grow: 1; // Allow range inputs to grow
              width: auto; // Reset specific width
              min-width: 80px; // Minimum width for range input
            }
          }
        }
      }

      .button-group {
        margin-left: auto; /* 将按钮组推到最右边 */
        margin-right: 0 !important; /* 确保没有右侧 margin */
        flex-shrink: 0; // 防止按钮组缩小

        .el-button {
          margin-left: 10px; // 按钮之间的水平间距
          &:first-child {
            margin-left: 0;
          }
        }

        // === 新增按钮的特定样式，增加左外边距 ===
        .add-button-separate {
          margin-left: 30px; // 设置一个更大的左外边距，例如 30px
        }

        .el-button {
          height: 32px;
          border-radius: 4px;
        }
        .el-button--primary {
          background-color: #1890ff !important;
          border-color: #1890ff !important;
          color: #fff !important;
          font-weight: bold;
        }

        // 新增按钮的绿色风格
        .el-button--success {
          background-color: #67c23a !important;
          border-color: #67c23a !important;
          color: #fff !important;
        }
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
      :deep(.el-table__cell .cell) {
        display: flex;
        align-items: center;
        justify-content: center; /* 默认居中对齐 */
        word-break: break-word; /* 允许单词中断 */
        white-space: pre-wrap; /* 保留空白符，但允许换行 */
      }
      // 对齐特定列
      .el-table-column[prop="typeName"],
      .el-table-column[prop="description"] :deep(.cell) {
        justify-content: flex-start; /* 名称、描述左对齐 */
      }

      .detail-button {
        background-color: #f0f0f0 !important;
        border: 1px solid #dcdcdc !important;
        color: #333 !important;
        border-radius: 2px !important;
        margin-right: 5px;
      }
      .el-button + .el-button {
        /* 按钮之间的默认间距 */
        margin-left: 5px;
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
        // 调整详情弹窗内表单的样式
        .el-form-item {
          margin-bottom: 10px;
          /* 详情项下方间距可以小一些 */
          .el-form-item__label {
            font-weight: bold;
            color: #555;
            width: 100px !important; /* 标签宽度与弹窗表单一致 */
            text-align: right;
          }
          .el-form-item__content {
            color: #333;
            line-height: 1.5;
            word-break: break-all;
            flex-grow: 1; // 内容区域填充剩余空间
            margin-left: 0 !important; /* 取消 el-form-item__label-wrap 可能产生的左外边距 */

            .service-category-description-display {
              width: 100%;
              box-sizing: border-box;
              padding: 5px 0;
            }
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
