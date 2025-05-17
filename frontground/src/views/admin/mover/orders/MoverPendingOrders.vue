<script setup>
  import { ref, onMounted, computed } from 'vue'; // 导入 computed
  import {
    ElMessage,
    ElMessageBox,
    ElDialog,
    ElForm,
    ElFormItem,
    ElInput,
    ElDatePicker,
    ElButton,
    ElTable,
    ElTableColumn,
    ElPagination,
    ElInputNumber, // 导入用于所需工人数量的 InputNumber
    ElDivider, // 导入 Divider 组件用于分组分隔线
    ElSelect, // 导入下拉选择框
    ElOption, // 导入下拉选择项
  } from 'element-plus';

  // 导入 Pinia Store
  import { myStore } from '@/stores/store.js';

  // 导入搬家工人待接订单相关的 API (使用新增的 mover-specific APIs)
  import {
    getMoverAvailableOrdersApi,
    getMoverAvailableOrderDetailApi,
    acceptMoverOrderApi,
  } from '@/api/orderApi.js'; // 确保路径正确

  // 导入服务类别相关的 API
  import { getBackServiceCategoryListApi } from '@/api/serviceCategoryApi.js'; // 导入获取服务类别列表的 API


  // 获取 store 实例和当前搬家工人 ID
  const store = myStore();
  // 使用 computed 获取当前登录搬家工人的信息和 ID
  const currentMoverInfo = computed(() => store.backUserInfo);
  const currentMoverId = computed(() => currentMoverInfo.value ? currentMoverInfo.value.id : null); // 获取当前搬家工人的 ID


  // 表格数据和加载状态
  const tableData = ref([]);
  const loading = ref(false);

  // 分页数据
  const pagination = ref({
    page: 1,
    pageSize: 10,
    total: 0,
  });


  // 服务类别列表 (用于筛选下拉框)
  const serviceCategories = ref([]); // 存储服务类别列表

  // 搜索表单数据
  const searchForm = ref({
    reservationTimeRange: null, // 预约时间范围 [start, end]
    // truckTypeId: null, // 已移除：所需车型 ID 筛选
    serviceCategoryId: null, // 服务类别 ID
    numberOfHelpers: null, // 所需工人数量
  });

  // 详情弹窗状态和当前选中行详情数据
  const detailDialogVisible = ref(false); // 控制详情弹窗显示
  const currentRowDetails = ref(null); // 存储当前查看详情的行数据

  // === 数据获取方法 ===
  const fetchPendingOrdersList = async () => {
    loading.value = true;
    try {
      // 准备请求参数
      const params = {
        page: pagination.value.page,
        pageSize: pagination.value.pageSize,
        // 将日期范围分解为 startDate 和 endDate
        startDate: searchForm.value.reservationTimeRange
          ? searchForm.value.reservationTimeRange[0]
          : undefined,
        endDate: searchForm.value.reservationTimeRange
          ? searchForm.value.reservationTimeRange[1]
          : undefined,
        // 已移除：包含所需车型 ID 筛选条件
        // truckTypeId: searchForm.value.truckTypeId || undefined,
        // 包含服务类别 ID 筛选条件
        serviceCategoryId: searchForm.value.serviceCategoryId || undefined, // 如果为 null 或 undefined 则不传递该参数
        // 包含所需工人数量筛选条件
        numberOfHelpers: searchForm.value.numberOfHelpers || undefined, // 如果为 null 或 undefined 则不传递该参数
      };

      // 调用搬家工人待接订单列表 API
      const { data: res } = await getMoverAvailableOrdersApi(params);

      // request.js 拦截器会处理 code !== 1 的错误提示
      if (res.code === 1) {
        tableData.value = res.data.records;
        pagination.value.total = res.data.total;
      } else {
        // 业务失败，消息已由拦截器弹出，这里只需清空数据
        tableData.value = [];
        pagination.value.total = 0;
        console.warn('获取搬家工人待接订单列表业务失败:', res.msg);
      }
    } catch (error) {
      // 请求本身发生错误 (网络问题, HTTP错误等)，消息已由拦截器弹出
      console.error('获取搬家工人待接订单列表请求失败:', error);
      tableData.value = [];
      pagination.value.total = 0;
    } finally {
      loading.value = false;
    }
  };


  // === 获取服务类别列表 (用于筛选下拉框) ===
  const fetchServiceCategories = async () => {
    try {
      // 调用 API 获取所有服务类别列表（非分页）
      const { data: res } = await getBackServiceCategoryListApi();

      // request.js 拦截器处理错误提示
      // 确保 code 为 1 且 data 是一个数组
      if (res.code === 1 && Array.isArray(res.data)) {
        // 将后端返回的服务类别列表转换为 { value, label } 格式
        serviceCategories.value = res.data.map(category => ({
          value: category.id, // 服务类别 ID 作为 value
          label: category.typeName // 服务类别名称作为 label
        }));
      } else {
        console.warn('获取服务类别列表业务失败或数据格式错误:', res.msg, res.data);
        // 失败时只显示空列表或默认状态（如果有）
        serviceCategories.value = [];
      }
    } catch (error) {
      console.error('获取服务类别列表请求失败:', error);
      // 失败时只显示空列表或默认状态（如果有）
      serviceCategories.value = [];
    }
  };


  // === 搜索相关方法 ===
  const handleSearch = () => {
    pagination.value.page = 1; // 从第一页开始搜索
    fetchPendingOrdersList();
  };

  const resetSearchForm = () => {
    searchForm.value = {
      reservationTimeRange: null,
      // truckTypeId: null, // 已移除：重置所需车型 ID
      serviceCategoryId: null,
      numberOfHelpers: null,
    };
    handleSearch(); // 重置后立即执行查询，回到第一页
  };

  // === 分页相关方法 ===
  const handleSizeChange = (val) => {
    pagination.value.pageSize = val;
    pagination.value.page = 1; // 切换每页大小时回到第一页
    fetchPendingOrdersList();
  };

  const handleCurrentChange = (val) => {
    pagination.value.page = val;
    fetchPendingOrdersList();
  };

  // === 查看订单详情相关方法 ===
  const showDetails = async (row) => {
    detailDialogVisible.value = true;
    // 在加载新的详情数据前，清空旧的数据
    currentRowDetails.value = null;

    try {
      // 调用搬家工人待接订单详情 API 根据ID获取完整的详情数据
      const { data: res } = await getMoverAvailableOrderDetailApi(row.orderId); // 使用 row.orderId

      // request.js 拦截器会处理错误提示
      if (res.code === 1 && res.data) {
        currentRowDetails.value = res.data; // 使用详情接口返回的数据更新 currentRowDetails
      } else {
        // 业务失败，消息已由拦截器弹出，这里只需关闭弹窗
        detailDialogVisible.value = false;
        console.warn('获取搬家工人待接订单详情业务失败:', res.msg);
      }
    } catch (error) {
      // 请求本身发生错误，消息已由拦截器弹出，这里只需关闭弹窗
      console.error('获取搬家工人待接订单详情请求失败:', error);
      detailDialogVisible.value = false;
    }
  };

  // === 接单相关方法 ===
  const handleAcceptOrder = (row) => {
    ElMessageBox.confirm(`确定要接取订单号为《${row.orderNumber}》的订单吗？`, '接单确认', {
      confirmButtonText: '确定接单',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        try {
          // 调用搬家工人接单 API
          const { data: res } = await acceptMoverOrderApi({ orderId: row.orderId }); // 发送包含 orderId 的请求体

          // request.js 拦截器会处理业务失败的错误提示
          if (res.code === 1) {
            ElMessage.success('接单成功！');
            // 接单成功后刷新待接订单列表
            fetchPendingOrdersList();
          } else {
            // 业务失败，消息已由拦截器弹出，无需重复提示
            console.warn('接单业务失败:', res.msg);
          }
        } catch (error) {
          // 请求本身发生错误，消息已由拦截器弹出
          console.error('接单请求失败:', error);
          // 可以根据需要判断是否是特定错误，如订单已被接取等，进行更精细的处理
        }
      })
      .catch((action) => {
        // 用户点击取消
        if (action === 'cancel') {
          ElMessage.info('已取消接单操作。');
        }
      });
  };

  // === 组件挂载后，首次加载数据 ===
  onMounted(() => {
    fetchPendingOrdersList(); // Initial fetch of orders
    // fetchTruckTypesForFilter(); // 已移除：不再需要获取所有车辆类型列表
    fetchServiceCategories(); // Fetch service categories for filter
  });
</script>

<template>
  <div class="page-container">
    <h3>待接订单</h3>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <div class="input-items-group">
        <el-form-item label="预约日期">
          <el-date-picker v-model="searchForm.reservationTimeRange" type="daterange"
                          range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"
                          value-format="YYYY-MM-DD" :clearable="true"
                          :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
                          style="width: 240px;"></el-date-picker>
        </el-form-item>

        <el-form-item label="服务类型">
          <el-select v-model="searchForm.serviceCategoryId" placeholder="请选择服务类型" clearable
                     style="width: 150px;">
            <el-option v-for="item in serviceCategories" :key="item.value" :label="item.label"
                       :value="item.value"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="所需工人数量">
          <el-input-number v-model="searchForm.numberOfHelpers" :min="0" placeholder="工人数量"
                           :clearable="true" style="width: 160px;"></el-input-number>
        </el-form-item>

      </div>

      <el-form-item class="button-group">
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearchForm">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="orderNumber" label="订单号" align="center" width="220"></el-table-column>
      <el-table-column prop="reservationTime" label="预约时间" align="center"
                       width="180"></el-table-column>
      <el-table-column prop="movingOrigin" label="出发地" align="left" min-width="150"
                       show-overflow-tooltip></el-table-column>
      <el-table-column prop="movingDestination" label="目的地" align="left" min-width="150"
                       show-overflow-tooltip></el-table-column>
      <el-table-column prop="serviceName" label="服务项目" align="center" min-width="180"
                       show-overflow-tooltip></el-table-column>
      <el-table-column prop="truckTypeName" label="所需车型" align="center" width="170"
                       show-overflow-tooltip></el-table-column>

      <el-table-column label="人员需求" align="center" width="200">
        <template #default="scope">
          {{ `所需 ${scope.row.numberOfHelpers || 0} 人 / 已接单 ${scope.row.currentAssignedMovers || 0} 人` }}
        </template>
      </el-table-column>
      <el-table-column prop="movingPrice" label="订单金额" align="center" width="120"></el-table-column>

      <el-table-column prop="driverName" label="已派司机" align="center" width="120"></el-table-column>
      <el-table-column prop="vehiclePlateNumber" label="已派车辆" align="center"
                       width="120"></el-table-column>

      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="showDetails(scope.row)"
                     class="detail-button">详情</el-button>
          <el-button size="small" type="success" style="margin-left: 10px"
                     @click="handleAcceptOrder(scope.row)">接单</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                   :current-page="pagination.page" :page-sizes="[10, 20, 50, 100]"
                   :page-size="pagination.pageSize" layout="total, sizes, prev, pager, next, jumper"
                   :total="pagination.total" background class="pagination"></el-pagination>

    <el-dialog v-model="detailDialogVisible" title="订单详情" width="600px">
      <el-form label-width="150px" v-if="currentRowDetails">
        <div class="detail-group">
          <h4>订单基本信息</h4>
          <el-form-item label="订单ID">{{ currentRowDetails.orderId }}</el-form-item>
          <el-form-item label="订单号">{{ currentRowDetails.orderNumber }}</el-form-item>
          <el-form-item label="订单状态">{{ currentRowDetails.orderStatusLabel }}</el-form-item>
          <el-form-item label="支付状态">{{ currentRowDetails.isPaidLabel }}</el-form-item>
          <el-form-item label="下单时间">{{ currentRowDetails.createTime }}</el-form-item>
        </div>

        <el-divider />
        <div class="detail-group">
          <h4>预约与地址信息</h4>
          <el-form-item label="预约时间">{{ currentRowDetails.reservationTime }}</el-form-item>
          <el-form-item label="出发地">{{ currentRowDetails.movingOrigin }}</el-form-item>
          <el-form-item label="目的地">{{ currentRowDetails.movingDestination }}</el-form-item>
        </div>

        <el-divider />
        <div class="detail-group">
          <h4>客户信息</h4>
          <el-form-item label="客户姓名">{{ currentRowDetails.customerName }}</el-form-item>
          <el-form-item label="客户电话">{{ currentRowDetails.customerPhone }}</el-form-item>
        </div>

        <el-divider />
        <div class="detail-group">
          <h4>服务及车辆详情</h4>
          <el-form-item label="服务项目">{{ currentRowDetails.serviceName }}</el-form-item>
          <el-form-item label="服务类型">{{ currentRowDetails.serviceCategoryName }}</el-form-item>
          <el-form-item label="服务描述">{{ currentRowDetails.serviceShortDescription }}</el-form-item>
          <el-form-item
                        label="载货量描述">{{ currentRowDetails.serviceLoadingCapacityDescription }}</el-form-item>
          <el-form-item label="所需车型">{{ currentRowDetails.truckTypeName }}</el-form-item>
          <el-form-item label="车型容量">{{ currentRowDetails.truckTypeCapacity }}</el-form-item>
          <el-form-item label="车型描述">{{ currentRowDetails.truckTypeDescription }}</el-form-item>
          <el-form-item label="已派司机">
            {{ currentRowDetails.driverName || '未指定司机' }}
            <span v-if="currentRowDetails.assignedVehicle"> (车牌:
              {{ currentRowDetails.assignedVehicle.licensePlateNumber }})</span>
          </el-form-item>
          <el-form-item label="车辆品牌">
            {{ currentRowDetails.assignedVehicle ? currentRowDetails.assignedVehicle.vehicleBrand : '未指定车辆' }}
          </el-form-item>

          <el-form-item label="所需工人数量">{{ currentRowDetails.numberOfHelpers }}</el-form-item>

          <el-form-item label="已派工人">
            <span
                  v-if="currentRowDetails.assignedMovers && currentRowDetails.assignedMovers.length > 0">
              {{ currentRowDetails.assignedMovers.map(mover => `${mover.name} (${mover.phone})`).join(', ') }}
              (共 {{ currentRowDetails.assignedMovers.length }} 人)
            </span>
            <span v-else>未指定工人</span>
          </el-form-item>
        </div>

        <el-divider />
        <div class="detail-group">
          <h4>费用明细</h4>
          <el-form-item label="预估总费用">{{ currentRowDetails.movingPrice }}</el-form-item>
          <el-form-item label="里程费用">{{ currentRowDetails.mileageCost }}</el-form-item>
          <el-form-item label="工人费用">{{ currentRowDetails.helperCost }}</el-form-item>
          <el-form-item
                        label="服务类型价格乘数">{{ currentRowDetails.categoryPriceMultiplier }}</el-form-item>
        </div>

        <el-divider />
        <div class="detail-group">
          <h4>订单备注</h4>
          <el-form-item label="备注">
            <div class="order-notes-display" style="margin-top:-5px">
              {{ currentRowDetails.notes || '无' }} </div>
          </el-form-item>
        </div>

      </el-form>
      <div v-else>正在加载订单详情...</div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<style scoped lang="less">
  /* 直接复用 AdminServiceTypeConfig.vue 和 DriverPendingOrders.vue 的样式 */
  .page-container {
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
      gap: 10px 20px;
      /* 行和列之间的间距 */

      .input-items-group {
        display: flex;
        flex-wrap: wrap; // 允许换行，以应对窄屏幕
        align-items: center;
        width: 100%; // 确保组占据一行以便均匀分布
        margin-bottom: 0; // 组下方添加垂直间距
        gap: 10px 20px;
        /* 组内的行和列间距 */

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
          display: flex;
          /* 使内部元素更好地对齐 */
          align-items: center;
        }

        // === 设置输入框、选择框、数字输入框、日期选择器组件的固定宽度 ===
        // 日期范围选择器宽度已在模板中单独设置，确保样式优先级

        // 如果需要根据车型或服务类型过滤，这里设置下拉选择的宽度
        :deep(.el-select) {
          width: 150px;
          /* 为下拉选择框设置宽度 */
        }

        :deep(.el-input-number) {
          width: 120px;
          /* 为数字输入框设置宽度 */
        }
      }

      .button-group {
        margin-left: auto;
        /* 将按钮组推到最右边 */
        margin-right: 0 !important;
        /* 确保没有右侧 margin */
        flex-shrink: 0; // 防止按钮组缩小

        .el-button {
          margin-left: 10px;
          // 按钮之间的水平间距
          &:first-child {
            margin-left: 0;
          }
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

        // 新增按钮的绿色风格 - 在这个页面用于“接单”按钮
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
        justify-content: center;
        /* 默认居中对齐 */
        word-break: break-word;
        /* 允许单词中断 */
        white-space: pre-wrap;
        /* 保留空白符，但允许换行 */
      }

      // 对齐特定列
      .el-table-column[prop="movingOrigin"],
      .el-table-column[prop="movingDestination"] :deep(.cell) {
        justify-content: flex-start;
        /* 出发地、目的地左对齐 */
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
        height: 28px;
        // Element Plus small size default height
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
        // 移除 ElForm 默认的底部外边距，由分组底部外边距控制
        margin-bottom: 0 !important;

        // 详情分组样式
        .detail-group {
          margin-bottom: 20px;
          /* 每个分组下方增加外边距 */

          h4 {
            font-size: 16px;
            color: #1890ff;
            /* 分组标题颜色 */
            border-bottom: 1px solid #eee;
            /* 分组标题下方分隔线 */
            padding-bottom: 8px;
            margin-bottom: 15px;
            /* 标题下方增加外边距 */
          }
        }

        // 调整详情弹窗内表单的样式
        .el-form-item {
          margin-bottom: 10px;
          /* 详情项下方间距可以小一些 */

          .el-form-item__label {
            font-weight: bold;
            color: #555;
            width: 120px !important;
            /* 标签宽度与弹窗表单一致 */
            text-align: right;
          }

          .el-form-item__content {
            color: #333;
            line-height: 1.5;
            word-break: break-all;
            flex-grow: 1;
            // 内容区域填充剩余空间
            margin-left: 0 !important;
            /* 取消 el-form-item__label-wrap 可能产生的左外边距 */

            .order-notes-display {
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

  /* 调整 ElDivider 的样式 */
  .el-divider {
    margin: 15px 0;
    /* 调整分隔线上下外边距 */
  }
</style>