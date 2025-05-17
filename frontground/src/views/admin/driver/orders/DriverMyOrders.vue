<script setup>
  // D:\Java\code\RelocateWeb\frontground\src\views\admin\driver\orders\DriverMyOrders.vue

  import { ref, onMounted } from 'vue';
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
    ElDivider,
    ElSelect,
    ElOption,
    ElTag, // 导入 ElTag 组件
  } from 'element-plus';

  // 导入司机我的订单相关的 API
  import {
    getDriverMyOrdersListApi,
    getDriverMyOrderDetailApi,
    cancelDriverOrderApi,
    startDriverServiceApi,
    completeDriverServiceApi,
    // 原来的通用状态接口不再用于此筛选
    // getBackOrderStatusApi,
    // 导入司机/搬家工人我的订单专属状态接口
    getDriverMoverMyOrdersStatusesApi,
  } from '@/api/orderApi.js'; // 确保路径正确

  // 表格数据和加载状态
  const tableData = ref([]);
  const loading = ref(false);

  // 分页数据
  const pagination = ref({
    page: 1,
    pageSize: 10,
    total: 0,
  });

  // 订单状态列表 (用于筛选下拉框)
  const orderStatuses = ref([]);

  // 搜索表单数据
  const searchForm = ref({
    orderNumber: '', // 订单号
    orderStatus: null, // 订单状态 ID
    reservationTimeRange: null, // 预约时间范围 [start, end]
  });

  // 详情弹窗状态和当前选中行详情数据
  const detailDialogVisible = ref(false); // 控制详情弹窗显示
  const currentRowDetails = ref(null); // 存储当前查看详情的行数据

  // 取消订单弹窗和表单
  const cancelDialogVisible = ref(false); // 控制取消订单弹窗显示
  const cancelForm = ref({
    orderId: null,
    cancelReason: '',
  });
  const cancelFormRef = ref(null); // 用于表单验证的 ref

  // 取消原因验证规则
  const cancelRules = {
    cancelReason: [
      { required: true, message: '请输入取消原因', trigger: 'blur' },
    ],
  };


  // === 数据获取方法 ===
  /**
   * 获取司机我的订单列表
   */
  const fetchMyOrdersList = async () => {
    loading.value = true;
    try {
      // 准备请求参数
      const params = {
        page: pagination.value.page,
        pageSize: pagination.value.pageSize,
        orderNumber: searchForm.value.orderNumber || undefined, // 订单号
        // 订单状态 ID，如果是 null 则不传递此参数
        orderStatus: searchForm.value.orderStatus === null ? undefined : searchForm.value.orderStatus,
        // 将日期范围分解为 startDate 和 endDate
        startDate: searchForm.value.reservationTimeRange
          ? searchForm.value.reservationTimeRange[0]
          : undefined,
        endDate: searchForm.value.reservationTimeRange
          ? searchForm.value.reservationTimeRange[1]
          : undefined,
      };

      const { data: res } = await getDriverMyOrdersListApi(params); // 调用司机我的订单列表 API

      // request.js 拦截器会处理 code !== 1 的错误提示
      if (res.code === 1) {
        tableData.value = res.data.records;
        pagination.value.total = res.data.total;
      } else {
        // 业务失败，消息已由拦截器弹出，这里只需清空数据
        tableData.value = [];
        pagination.value.total = 0;
        console.warn('获取司机我的订单列表业务失败:', res.msg);
      }
    } catch (error) {
      // 请求本身发生错误 (网络问题, HTTP错误等)，消息已由拦截器弹出
      console.error('获取司机我的订单列表请求失败:', error);
      tableData.value = [];
      pagination.value.total = 0;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 获取订单状态列表 (用于筛选下拉框) - 使用司机/搬家工人专属接口
   */
  const fetchOrderStatuses = async () => {
    try {
      // 调用司机/搬家工人专属的订单状态接口
      const { data: res } = await getDriverMoverMyOrdersStatusesApi();

      if (res.code === 1 && Array.isArray(res.data)) {
        // 将后端返回的状态列表转换为 { value, label } 格式
        // 新接口返回的是 code 和 description
        orderStatuses.value = res.data.map(status => ({
          value: status.code, // 状态码作为 value
          label: status.description // 状态描述作为 label (使用 description 字段)
        }));
      } else {
        console.warn('获取司机/搬家工人订单状态列表业务失败或数据格式错误:', res.msg, res.data);
        // 失败时只显示空列表或默认状态（如果有）
        orderStatuses.value = []; // 清空或设置为包含默认状态
      }
    } catch (error) {
      console.error('获取司机/搬家工人订单状态列表请求失败:', error);
      // 请求失败时只显示空列表或默认状态（如果有）
      orderStatuses.value = []; // 清空或设置为包含默认状态
    }
  };

  // === 辅助方法：获取订单状态对应的 Tag 类型 (从 AdminOrderList.vue 复制) ===
  const getOrderStatusTagType = (status) => {
    switch (status) {
      case 0:
        return 'info'; // 待接单
      case 1:
        return ''; // 司机已接单 (默认颜色)
      case 2:
        return 'primary'; // 已接单（团队） - 可以考虑不同的颜色
      case 3:
        return 'warning'; // 进行中
      case 4:
        return 'success'; // 已完成
      case 5:
        return 'danger'; // 已取消
      default:
        return 'info';
    }
  };


  // === 搜索相关方法 ===
  const handleSearch = () => {
    pagination.value.page = 1; // 从第一页开始搜索
    fetchMyOrdersList();
  };

  const resetSearchForm = () => {
    searchForm.value = {
      orderNumber: '',
      orderStatus: null, // 重置为 null
      reservationTimeRange: null,
    };
    handleSearch(); // 重置后立即执行查询，回到第一页
  };

  // === 分页相关方法 ===
  const handleSizeChange = (val) => {
    pagination.value.pageSize = val;
    pagination.value.page = 1; // 切换每页大小时回到第一页
    fetchMyOrdersList();
  };

  const handleCurrentChange = (val) => {
    pagination.value.page = val;
    fetchMyOrdersList();
  };

  // === 查看订单详情相关方法 ===
  const showDetails = async (row) => {
    detailDialogVisible.value = true;
    // 在加载新的详情数据前，清空旧的数据
    currentRowDetails.value = null;

    try {
      // 调用司机我的订单详情 API 根据ID获取完整的详情数据
      const { data: res } = await getDriverMyOrderDetailApi(row.orderId); // 使用 row.orderId

      // request.js 拦截器会处理错误提示
      if (res.code === 1 && res.data) {
        currentRowDetails.value = res.data; // 使用详情接口返回的数据更新 currentRowDetails
      } else {
        // 业务失败，消息已由拦截器弹出，这里只需关闭弹窗
        detailDialogVisible.value = false;
        console.warn('获取司机我的订单详情业务失败:', res.msg);
      }
    } catch (error) {
      // 请求本身发生错误，消息已由拦截器弹出，这里只需关闭弹窗
      console.error('获取司机我的订单详情请求失败:', error);
      detailDialogVisible.value = false;
    }
  };

  // === 取消订单相关方法 ===
  const openCancelDialog = (row) => {
    cancelForm.value.orderId = row.orderId;
    cancelForm.value.cancelReason = ''; // 清空取消原因
    cancelDialogVisible.value = true;
  };

  const submitCancel = async () => {
    // 验证表单
    const valid = await cancelFormRef.value.validate().catch(() => false);
    if (!valid) {
      ElMessage.warning('请填写有效的取消原因');
      return;
    }

    try {
      // 调用取消订单 API
      const { data: res } = await cancelDriverOrderApi({
        orderId: cancelForm.value.orderId,
        cancelReason: cancelForm.value.cancelReason,
      });

      // request.js 拦截器会处理业务失败的错误提示
      if (res.code === 1) {
        ElMessage.success('订单取消成功！');
        cancelDialogVisible.value = false; // 关闭弹窗
        fetchMyOrdersList(); // 刷新我的订单列表
      } else {
        // 业务失败，消息已由拦截器弹出
        console.warn('订单取消业务失败:', res.msg);
      }
    } catch (error) {
      // 请求本身发生错误，消息已由拦截器弹出
      console.error('订单取消请求失败:', error);
    }
  };

  // === 开始搬运服务相关方法 ===
  const handleStartService = (row) => {
    ElMessageBox.confirm(`确定要对订单号为《${row.orderNumber}》的订单开始搬运服务吗？`, '开始服务确认', {
      confirmButtonText: '确定开始',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        try {
          // 调用开始服务 API
          const { data: res } = await startDriverServiceApi(row.orderId);

          if (res.code === 1) {
            ElMessage.success('服务已成功开始！');
            fetchMyOrdersList(); // 刷新列表
          } else {
            console.warn('开始服务业务失败:', res.msg);
          }
        } catch (error) {
          console.error('开始服务请求失败:', error);
        }
      })
      .catch((action) => {
        if (action === 'cancel') {
          ElMessage.info('已取消开始服务操作。');
        }
      });
  };

  // === 完成搬运服务相关方法 ===
  const handleCompleteService = (row) => {
    ElMessageBox.confirm(`确定要对订单号为《${row.orderNumber}》的订单完成搬运服务吗？`, '完成服务确认', {
      confirmButtonText: '确定完成',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        try {
          // 调用完成服务 API
          const { data: res } = await completeDriverServiceApi(row.orderId);

          if (res.code === 1) {
            ElMessage.success('服务已成功完成！');
            fetchMyOrdersList(); // 刷新列表
          } else {
            console.warn('完成服务业务失败:', res.msg);
          }
        } catch (error) {
          console.error('完成服务请求失败:', error);
        }
      })
      .catch((action) => {
        if (action === 'cancel') {
          ElMessage.info('已取消完成服务操作。');
        }
      });
  };

  // === 组件挂载后，首次加载数据 ===
  onMounted(() => {
    fetchMyOrdersList(); // 首次加载我的订单列表
    fetchOrderStatuses(); // 加载订单状态列表用于筛选
  });
</script>

<template>
  <div class="page-container">
    <h3>我的订单</h3>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <div class="input-items-group">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNumber" placeholder="请输入订单号" clearable
                    style="width: 180px;"></el-input>
        </el-form-item>

        <el-form-item label="订单状态">
          <el-select v-model="searchForm.orderStatus" placeholder="请选择订单状态" clearable
                     style="width: 150px;">
            <el-option v-for="item in orderStatuses" :key="item.value" :label="item.label"
                       :value="item.value"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="预约日期">
          <el-date-picker v-model="searchForm.reservationTimeRange" type="daterange"
                          range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"
                          value-format="YYYY-MM-DD" :clearable="true"
                          :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
                          style="width: 240px;"></el-date-picker>
        </el-form-item>
      </div>

      <el-form-item class="button-group">
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearchForm">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="orderNumber" label="订单号" align="center" width="220"></el-table-column>

      <el-table-column prop="orderStatusLabel" label="订单状态" align="center" width="230">
        <template #default="scope">
          <el-tag :type="getOrderStatusTagType(scope.row.orderStatus)">
            {{ scope.row.orderStatusLabel }}
          </el-tag>
        </template>
      </el-table-column>

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
      <el-table-column prop="numberOfHelpers" label="工人数量" align="center" width="120"
                       show-overflow-tooltip></el-table-column>
      <el-table-column prop="movingPrice" label="预估费用" align="center" width="120"></el-table-column>

      <el-table-column label="操作" width="250" align="center" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="showDetails(scope.row)"
                     class="detail-button">详情</el-button>

          <el-button v-if="scope.row.orderStatus === 1" size="small" type="danger"
                     style="margin-left: 10px" @click="openCancelDialog(scope.row)">取消订单</el-button>

          <el-button v-if="scope.row.orderStatus === 2" size="small" type="primary"
                     style="margin-left: 10px"
                     @click="handleStartService(scope.row)">开始服务</el-button>

          <el-button v-if="scope.row.orderStatus === 3" size="small" type="success"
                     style="margin-left: 10px"
                     @click="handleCompleteService(scope.row)">完成服务</el-button>

        </template>
      </el-table-column>
      ></el-table>

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
          <el-form-item label="订单状态">
            <el-tag :type="getOrderStatusTagType(currentRowDetails.orderStatus)">
              {{ currentRowDetails.orderStatusLabel }}
            </el-tag>
          </el-form-item>
          <el-form-item label="支付状态">{{ currentRowDetails.isPaidLabel }}</el-form-item>
          <el-form-item label="预约时间">{{ currentRowDetails.reservationTime }}</el-form-item>
          <el-form-item label="下单时间">{{ currentRowDetails.createTime }}</el-form-item>
          <el-form-item label="支付时间">{{ currentRowDetails.paymentTime || 'N/A' }}</el-form-item>
          <el-form-item label="开始时间">{{ currentRowDetails.movingStartTime || 'N/A' }}</el-form-item>
          <el-form-item label="完成时间">{{ currentRowDetails.movingEndTime || 'N/A' }}</el-form-item>
        </div>

        <el-divider />
        <div class="detail-group">
          <h4>地址信息</h4>
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
          <el-form-item label="所需工人数量">{{ currentRowDetails.numberOfHelpers }}</el-form-item>

          <el-form-item label="指派车辆">
            <span v-if="currentRowDetails.assignedVehicle">
              车牌号: {{ currentRowDetails.assignedVehicle.licensePlateNumber }} (品牌:
              {{ currentRowDetails.assignedVehicle.vehicleBrand }})
            </span>
            <span v-else>未指派</span>
          </el-form-item>

          <el-form-item label="指派搬运工">
            <div
                 v-if="currentRowDetails.assignedMovers && currentRowDetails.assignedMovers.length > 0">
              <div v-for="mover in currentRowDetails.assignedMovers" :key="mover.id">
                {{ mover.name }} ({{ mover.phone }})
              </div>
            </div>
            <span v-else>无</span>
          </el-form-item>
        </div>

        <el-divider />
        <div class="detail-group">
          <h4>费用明细</h4>
          <el-form-item label="预估总费用">{{ currentRowDetails.movingPrice }}</el-form-item>
          <el-form-item label="车型基础费用">{{ currentRowDetails.truckTypeBaseFare }}</el-form-item>
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
              {{ currentRowDetails.notes }}
            </div>
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

    <el-dialog v-model="cancelDialogVisible" title="取消订单" width="400px">
      <el-form :model="cancelForm" :rules="cancelRules" ref="cancelFormRef" label-width="90px">
        <el-form-item label="取消原因" prop="cancelReason">
          <el-input v-model="cancelForm.cancelReason" type="textarea" :rows="3"
                    placeholder="请输入取消原因"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitCancel">确定取消</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<style scoped lang="less">
  /* 直接复用 DriverPendingOrders.vue 的样式 */
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
        // 日期范围选择器宽度已在模板中单独设置，确保样式优先级

        // 如果需要根据车型或服务类型过滤，这里设置下拉选择的宽度
        :deep(.el-select) {
          width: 150px; /* 为下拉选择框设置宽度 */
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

        // 新增按钮的绿色风格 - 在这个页面用于“完成服务”按钮
        .el-button--success {
          background-color: #67c23a !important;
          border-color: #67c23a !important;
          color: #fff !important;
        }
        // 危险按钮的红色风格 - 在这个页面用于“取消订单”按钮
        .el-button--danger {
          background-color: #f56c6c !important;
          border-color: #f56c6c !important;
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
      .el-table-column[prop="movingOrigin"],
      .el-table-column[prop="movingDestination"] :deep(.cell) {
        justify-content: flex-start; /* 出发地、目的地左对齐 */
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
        // 移除 ElForm 默认的底部外边距，由分组底部外边距控制
        margin-bottom: 0 !important;

        // 详情分组样式
        .detail-group {
          margin-bottom: 20px; /* 每个分组下方增加外边距 */
          h4 {
            font-size: 16px;
            color: #1890ff; /* 分组标题颜色 */
            border-bottom: 1px solid #eee; /* 分组标题下方分隔线 */
            padding-bottom: 8px;
            margin-bottom: 15px; /* 标题下方增加外边距 */
          }
        }

        // 调整详情弹窗内表单的样式
        .el-form-item {
          margin-bottom: 10px;
          /* 详情项下方间距可以小一些 */
          .el-form-item__label {
            font-weight: bold;
            color: #555;
            /* 根据 el-form 的 label-width 调整，这里保持 auto 让 el-form 的设置生效 */
            /* width: 120px !important; */ /* 删除或注释此行 */
            text-align: right;
          }
          .el-form-item__content {
            color: #333;
            line-height: 1.5;
            word-break: break-all;
            flex-grow: 1; // 内容区域填充剩余空间
            margin-left: 0 !important; /* 取消 el-form-item__label-wrap 可能产生的左外边距 */

            // 新增样式，确保 ElTag 垂直居中对齐
            .el-tag {
              vertical-align: middle;
            }

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
    margin: 15px 0; /* 调整分隔线上下外边距 */
  }
</style>