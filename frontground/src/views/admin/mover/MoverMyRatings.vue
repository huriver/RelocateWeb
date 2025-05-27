<template>
  <div class="page-container">
    <h3>我的评价</h3>

    <el-form :model="searchForm" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNumber" placeholder="请输入订单号" clearable
                    style="width: 180px;"></el-input>
        </el-form-item>

        <el-form-item label="客户名称">
          <el-input v-model="searchForm.customerName" placeholder="请输入客户名称" clearable
                    style="width: 150px;"></el-input>
        </el-form-item>

        <el-form-item label="评分范围">
          <div style="display: flex; align-items: center; width: 180px;">
            <el-input v-model.number="searchForm.minRatingValue" type="number" :min="1" :max="5"
                      style="width: 80px;" placeholder="最低" @blur="validateMinRating"></el-input>
            <span style="margin: 0 5px;">至</span>
            <el-input v-model.number="searchForm.maxRatingValue" type="number" :min="1" :max="5"
                      style="width: 80px;" placeholder="最高" @blur="validateMaxRating"></el-input>
          </div>
        </el-form-item>

        <el-form-item label="评价日期">
          <el-date-picker v-model="searchForm.ratingTimeRange" type="daterange" range-separator="至"
                          start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD"
                          :clearable="true" style="width: 240px;"></el-date-picker>
        </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearchForm">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="orderNumber" label="订单号" align="center" width="220"></el-table-column>
      <el-table-column prop="customerName" label="客户名称" align="center"></el-table-column>

      <el-table-column prop="ratingValue" label="评分" align="center">
        <template #default="scope">
          <el-rate v-model="scope.row.ratingValue" disabled show-score text-color="#ff9900"
                   score-template="{value}"></el-rate>
        </template>
      </el-table-column>

      <el-table-column prop="commentSnippet" label="评价内容摘要" align="left"
                       show-overflow-tooltip></el-table-column>
      <el-table-column prop="ratingTime" label="评价时间" align="center" width="180"></el-table-column>

      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="showDetails(scope.row)"
                     class="detail-button">详情</el-button>
        </template>
      </el-table-column>
      ></el-table>

    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                   :current-page="pagination.page" :page-sizes="[10, 20, 50, 100]"
                   :page-size="pagination.pageSize" layout="total, sizes, prev, pager, next, jumper"
                   :total="pagination.total" background class="pagination"></el-pagination>

    <el-dialog v-model="detailDialogVisible" title="评价详情" width="600px">
      <el-form label-width="150px" v-if="currentRowDetails">
        <div class="detail-group">
          <h4>评价信息</h4>
          <el-form-item label="评价ID">{{ currentRowDetails.id }}</el-form-item>
          <el-form-item label="订单号">{{ currentRowDetails.orderNumber }}</el-form-item>
          <el-form-item label="评分">
            <el-rate v-model="currentRowDetails.ratingValue" disabled show-score
                     text-color="#ff9900" score-template="{value}"></el-rate>
          </el-form-item>
          <el-form-item label="评价时间">{{ currentRowDetails.ratingTime }}</el-form-item>
          <el-form-item label="评价内容">
            <div class="rating-comment-display" style="margin-top:-5px">
              {{ currentRowDetails.comment }}
            </div>
          </el-form-item>
        </div>

        <el-divider />
        <div class="detail-group">
          <h4>客户信息</h4>
          <el-form-item label="客户名称">{{ currentRowDetails.customerName }}</el-form-item>
          <el-form-item label="客户电话">{{ currentRowDetails.customerPhone }}</el-form-item>
          <el-form-item label="客户ID">{{ currentRowDetails.customerId }}</el-form-item>
        </div>

        <el-divider />
        <div class="detail-group">
          <h4>订单信息</h4>
          <el-form-item label="订单ID">{{ currentRowDetails.orderId }}</el-form-item>
          <el-form-item label="出发地">{{ currentRowDetails.movingOrigin }}</el-form-item>
          <el-form-item label="目的地">{{ currentRowDetails.movingDestination }}</el-form-item>
          <el-form-item label="服务项目">{{ currentRowDetails.serviceName }}</el-form-item>
          <el-form-item label="所需车型">{{ currentRowDetails.truckTypeName }}</el-form-item>
          <el-form-item label="所需工人数量">{{ currentRowDetails.numberOfHelpers }}</el-form-item>
          <el-form-item label="预估费用">{{ currentRowDetails.movingPrice }}</el-form-item>
        </div>
      </el-form>
      <div v-else>正在加载评价详情...</div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
  // D:\Java\code\RelocateWeb\frontground\src\views\admin\mover\MoverMyRatings.vue

  import { ref, onMounted } from 'vue';
  import {
    ElMessage,
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
    ElRate, // 导入 ElRate 组件
  } from 'element-plus';

  // 导入搬家工人评价相关的 API
  import {
    getMoverMyRatingsListApi, // 注意这里是搬家工人API
    getMoverMyRatingDetailApi, // 注意这里是搬家工人API
  } from '@/api/ratingApi.js'; // 确保路径正确

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
    // 初始值设置为 null，以便显示 placeholder
    minRatingValue: null, // 最低评分
    maxRatingValue: null, // 最高评分
    ratingTimeRange: null, // 评价时间范围 [start, end]
    orderNumber: '', // 订单号
    customerName: '', // 客户名称
  });

  // 详情弹窗状态和当前选中行详情数据
  const detailDialogVisible = ref(false); // 控制详情弹窗显示
  const currentRowDetails = ref(null); // 存储当前查看详情的行数据


  // === 评分范围输入验证 ===
  const validateMinRating = () => {
    let value = searchForm.value.minRatingValue;
    // 检查是否是数字
    if (typeof value === 'number') {
      // 强制限制在 1-5 范围内
      value = Math.max(1, Math.min(5, value));

      // 检查是否大于最高评分，如果是，将最低评分设为最高评分
      if (typeof searchForm.value.maxRatingValue === 'number' && value > searchForm.value.maxRatingValue) {
        value = searchForm.value.maxRatingValue;
      }
      searchForm.value.minRatingValue = value;

    } else {
      // 处理非数字输入或其他无效情况，清空并显示placeholder
      searchForm.value.minRatingValue = null;
    }
  };

  const validateMaxRating = () => {
    let value = searchForm.value.maxRatingValue;
    // 检查是否是数字
    if (typeof value === 'number') {
      // 强制限制在 1-5 范围内
      value = Math.max(1, Math.min(5, value));

      // 检查是否小于最低评分，如果是，将最高评分设为最低评分
      if (typeof searchForm.value.minRatingValue === 'number' && value < searchForm.value.minRatingValue) {
        value = searchForm.value.minRatingValue;
      }
      searchForm.value.maxRatingValue = value;

    } else {
      // 处理非数字输入或其他无效情况，清空并显示placeholder
      searchForm.value.maxRatingValue = null;
    }
  };


  // === 数据获取方法 ===
  /**
   * 获取搬家工人我的评价列表
   */
  const fetchMyRatingsList = async () => {
    loading.value = true;
    try {
      // 准备请求参数
      const params = {
        page: pagination.value.page,
        pageSize: pagination.value.pageSize,
        // 确保 min/maxRatingValue 是数字且在有效范围内 (在 validate 方法中已处理)
        // 这里只判断是否为数字，如果为 null 则不传递
        minRatingValue: typeof searchForm.value.minRatingValue === 'number'
          ? searchForm.value.minRatingValue
          : undefined,
        maxRatingValue: typeof searchForm.value.maxRatingValue === 'number'
          ? searchForm.value.maxRatingValue
          : undefined,
        orderNumber: searchForm.value.orderNumber || undefined, // 订单号
        customerName: searchForm.value.customerName || undefined, // 客户名称
        // 将日期范围分解为 startTime 和 endTime
        startTime: searchForm.value.ratingTimeRange
          ? searchForm.value.ratingTimeRange[0] + ' 00:00:00' // 开始时间通常取当天开始
          : undefined,
        endTime: searchForm.value.ratingTimeRange
          ? searchForm.value.ratingTimeRange[1] + ' 23:59:59' // 结束时间通常取当天结束
          : undefined,
      };

      // === 调用搬家工人我的评价列表 API ===
      const { data: res } = await getMoverMyRatingsListApi(params); // !!! 注意这里是搬家工人API

      // request.js 拦截器会处理 code !== 1 的错误提示
      if (res.code === 1) {
        tableData.value = res.data.records;
        pagination.value.total = res.data.total;
      } else {
        // 业务失败，消息已由拦截器弹出，这里只需清空数据
        tableData.value = [];
        pagination.value.total = 0;
        console.warn('获取搬家工人我的评价列表业务失败:', res.msg);
      }
    } catch (error) {
      // 请求本身发生错误 (网络问题, HTTP错误等)，消息已由拦截器弹出
      console.error('获取搬家工人我的评价列表请求失败:', error);
      tableData.value = [];
      pagination.value.total = 0;
    } finally {
      loading.value = false;
    }
  };

  // === 搜索相关方法 ===
  const handleSearch = () => {
    pagination.value.page = 1; // 从第一页开始搜索
    fetchMyRatingsList();
  };

  const resetSearchForm = () => {
    searchForm.value = {
      // 重置时也设置为 null，以便显示 placeholder
      minRatingValue: null,
      maxRatingValue: null,
      ratingTimeRange: null,
      orderNumber: '',
      customerName: '',
    };
    handleSearch(); // 重置后立即执行查询，回到第一页
  };

  // === 查看评价详情相关方法 ===
  const showDetails = async (row) => {
    detailDialogVisible.value = true;
    // 在加载新的详情数据前，清空旧的数据
    currentRowDetails.value = null;

    try {
      // === 调用搬家工人我的评价详情 API ===
      const { data: res } = await getMoverMyRatingDetailApi(row.id); // !!! 注意这里是搬家工人API, 使用 row.id (评价ID)

      // request.js 拦截器会处理错误提示
      if (res.code === 1 && res.data) {
        currentRowDetails.value = res.data; // 使用详情接口返回的数据更新 currentRowDetails
      } else {
        // 业务失败，消息已由拦截器弹出，这里只需关闭弹窗
        detailDialogVisible.value = false;
        console.warn('获取搬家工人我的评价详情业务失败:', res.msg);
      }
    } catch (error) {
      // 请求本身发生错误，消息已由拦截器弹出，这里只需关闭弹窗
      console.error('获取搬家工人我的评价详情请求失败:', error);
      detailDialogVisible.value = false;
    }
  };


  // === 分页变化方法 ===
  const handleSizeChange = (newSize) => {
    pagination.value.pageSize = newSize;
    fetchMyRatingsList();
  };

  const handleCurrentChange = (newPage) => {
    pagination.value.page = newPage;
    fetchMyRatingsList();
  };

  // === 组件挂载后，首次加载数据 ===
  onMounted(() => {
    fetchMyRatingsList(); // 首次加载我的评价列表
  });
</script>

<style scoped lang="less">
  /* 直接复用 DriverMyOrders.vue 的样式 */
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

        // ElRate 评分组件的样式调整
        :deep(.el-rate) {
          // 确保评分组件在 flex 布局中居中
          align-items: center;
          // 调整评分组件的行高，使其与表单项对齐
          height: 32px;
          line-height: 32px;
          // 调整星星图标的大小和间距
          .el-rate__icon {
            font-size: 16px; /* 调整星星大小 */
            margin-right: 4px; /* 调整星星间距 */
          }
          .el-rate__text {
            font-size: 14px; /* 调整分数文字大小 */
          }
        }
      }

      .el-form-item {
        margin-bottom: 0;
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
      .el-table-column[prop="commentSnippet"] :deep(.cell) {
        justify-content: flex-start; /* 评价内容摘要左对齐 */
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

            // 评价内容显示区域样式
            .rating-comment-display {
              width: 100%;
              box-sizing: border-box;
              padding: 5px 0;
            }

            // ElRate 评分组件在详情中的样式调整
            .el-rate {
              align-items: center;
              height: auto; // 在详情中高度自适应
              line-height: normal; // 在详情中行高恢复正常
              .el-rate__icon {
                font-size: 18px; /* 调整星星大小 */
                margin-right: 4px; /* 调整星星间距 */
              }
              .el-rate__text {
                font-size: 16px; /* 调整分数文字大小 */
              }
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