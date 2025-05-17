<template>
  <div class="admin-order-ratings-container">
    <h3>订单评价管理</h3>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <div class="input-items-group">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNumber" placeholder="请输入订单号" clearable></el-input>
        </el-form-item>
        <el-form-item label="评价类型">
          <el-select v-model="searchForm.ratingType" placeholder="请选择类型" clearable>
            <el-option v-for="typeOption in ratingTypeOptions" :key="typeOption.value"
                       :label="typeOption.label" :value="typeOption.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="被评价对象">
          <el-input v-model="searchForm.rateeName" placeholder="被评价者名称" clearable></el-input>
        </el-form-item>
      </div>

      <div class="input-items-group range-filters-group">
        <el-form-item label="评分值">
          <div class="range-inputs">
            <el-input-number v-model="searchForm.ratingValueMin" :min="1" :max="5" :precision="0"
                             controls-position="right" placeholder="最低" clearable
                             style="width: 100px"></el-input-number>
            <span class="range-separator">至</span>
            <el-input-number v-model="searchForm.ratingValueMax"
                             :min="searchForm.ratingValueMin || 1" :max="5" :precision="0"
                             controls-position="right" placeholder="最高" clearable
                             style="width: 100px"></el-input-number>
          </div>
        </el-form-item>
      </div>

      <el-form-item class="button-group">
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearchForm">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" align="center" width="80"></el-table-column>
      <el-table-column prop="orderNumber" label="订单号" width="180" align="center"></el-table-column>
      <el-table-column prop="ratingTime" label="评价时间" align="center" width="180"></el-table-column>
      <el-table-column prop="customerName" label="评价客户" width="120"
                       align="center"></el-table-column>
      <el-table-column prop="ratingType" label="评价类型" width="110" align="center">
        <template #default="scope">
          {{ formatRatingType(scope.row.ratingType) }}
        </template>
      </el-table-column>
      <el-table-column prop="rateeName" label="被评价对象" width="150" align="center"></el-table-column>

      <el-table-column prop="ratingValue" label="评分" width="160" align="center"
                       class-name="rating-column">
        <template #default="scope">
          <el-rate v-model="scope.row.ratingValue" disabled show-score text-color="#ff9900"
                   score-template="{value}"></el-rate>
        </template>
      </el-table-column>
      <el-table-column prop="comment" label="评价内容" align="center" min-width="200"
                       show-overflow-tooltip></el-table-column>

      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="showDetails(scope.row)"
                     class="detail-button">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                   :current-page="pagination.page" :page-sizes="[10, 20, 50, 100]"
                   :page-size="pagination.pageSize" layout="total, sizes, prev, pager, next, jumper"
                   :total="pagination.total" background class="pagination"></el-pagination>

    <el-dialog v-model="dialogVisible" title="评价详情" width="600px">
      <el-form label-width="120px" v-if="currentRowDetails">
        <el-form-item label="评价ID">{{ currentRowDetails.id }}</el-form-item>
        <el-form-item label="订单ID">{{ currentRowDetails.orderId }}</el-form-item>

        <el-form-item label="订单号">{{ currentRowDetails.orderNumber }}</el-form-item>
        <el-form-item label="客户ID">{{ currentRowDetails.customerId }}</el-form-item>

        <el-form-item label="客户姓名">{{ currentRowDetails.customerName }}</el-form-item>
        <el-form-item label="评价类型">{{
					formatRatingType(currentRowDetails.ratingType)
				}}</el-form-item>
        <el-form-item label="被评价对象ID">{{ currentRowDetails.rateeId }}</el-form-item>

        <el-form-item label="被评价对象">{{ currentRowDetails.rateeName }}</el-form-item>

        <el-form-item label="评分">
          <el-rate v-model="currentRowDetails.ratingValue" disabled show-score text-color="#ff9900"
                   score-template="{value}"></el-rate>
        </el-form-item>
        <el-form-item label="评价内容">{{ currentRowDetails.comment || '无评论' }}</el-form-item>
        <el-form-item label="评价时间">{{ currentRowDetails.ratingTime }}</el-form-item>
        <el-form-item label="创建时间">{{ currentRowDetails.createTime }}</el-form-item>
        <el-form-item label="更新时间">{{ currentRowDetails.updateTime }}</el-form-item>
      </el-form>
      <div v-else>正在加载评价详情...</div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
  import { ref, onMounted } from 'vue';
  import {
    ElMessage,
    ElDialog,
    ElForm,
    ElFormItem,
    ElTag, // 评价列表没有tag，但详情里可能有需要，保留
    ElInput,
    ElInputNumber, // 导入数字输入框
    ElSelect,
    ElOption, // 导入 ElOption
    ElRate, // 导入 ElRate
    ElDatePicker, // 如果需要日期筛选，保留
    ElButton,
    ElTable,
    ElTableColumn,
    ElPagination,
  } from 'element-plus';
  // 导入后台评价 API
  import { getBackRatingListPageApi, getBackRatingDetailApi } from '@/api/ratingApi.js'; // <-- 导入后台评价 API

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
    ratingValueMin: null, // 评分最低
    ratingValueMax: null, // 评分最高
    orderNumber: '', // 订单号
    ratingType: null, // 评价类型 (null 表示全部)
    rateeName: '', // 被评价对象姓名
    // 评价管理不需要时间范围筛选，如果需要再添加
    // createTimeRange: null,
  });

  // 评价类型选项 (用于下拉框) - 硬编码，因为 API 没有提供获取评价类型的接口
  const ratingTypeOptions = ref([
    { value: 'MOVER', label: '搬运工评价' },
    { value: 'SERVICE', label: '服务项评价' },
    { value: 'DRIVER', label: '司机评价' },
    // 不添加 "全部" 选项，直接让 value 为 null 表示全部，clearable 按钮可以实现清空
  ]);

  // 详情弹窗状态和当前选中行数据
  const dialogVisible = ref(false); // 控制详情弹窗显示
  const currentRowDetails = ref(null); // 存储当前查看详情的行数据

  // === 数据获取方法 ===
  const fetchRatingList = async () => {
    loading.value = true;
    try {
      // 准备请求参数
      const params = {
        page: pagination.value.page,
        pageSize: pagination.value.pageSize,
        ratingValueMin: searchForm.value.ratingValueMin || undefined, // 添加评分范围参数
        ratingValueMax: searchForm.value.ratingValueMax || undefined, // 添加评分范围参数
        orderNumber: searchForm.value.orderNumber || undefined, // 添加订单号参数
        ratingType: searchForm.value.ratingType || undefined, // 添加评价类型参数
        rateeName: searchForm.value.rateeName || undefined, // 添加被评价对象参数
        // createTimeStart: searchForm.value.createTimeRange ? searchForm.value.createTimeRange[0] : undefined,
        // createTimeEnd: searchForm.value.createTimeRange ? searchForm.value.createTimeRange[1] : undefined,
      };

      const { data: res } = await getBackRatingListPageApi(params); // 调用后台评价分页 API

      if (res.code === 1) {
        tableData.value = res.data.records;
        pagination.value.total = res.data.total;
      } else {
        // 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
        // ElMessage.error(res.msg || '获取评价列表失败'); // <-- 移除此行
        console.warn('获取评价列表业务失败:', res.msg); // 可以保留日志
        tableData.value = [];
        pagination.value.total = 0;
      }
    } catch (error) {
      // 捕获真正的请求错误 (网络问题、HTTP错误等，不包括 code !== 1 的业务错误)
      console.error('获取评价列表请求失败:', error);
      ElMessage.error('获取评价列表失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
      tableData.value = [];
      pagination.value.total = 0;
    } finally {
      loading.value = false;
    }
  };

  // === 搜索相关方法 ===
  const handleSearch = () => {
    pagination.value.page = 1; // 从第一页开始搜索
    fetchRatingList();
  };

  const resetSearchForm = () => {
    searchForm.value = {
      ratingValueMin: null,
      ratingValueMax: null,
      orderNumber: '',
      ratingType: null,
      rateeName: '',
      // createTimeRange: null,
    };
    handleSearch(); // 重置后立即执行查询，回到第一页
  };

  // === 分页相关方法 ===
  const handleSizeChange = (val) => {
    pagination.value.pageSize = val;
    pagination.value.page = 1; // 切换每页大小时回到第一页
    fetchRatingList();
  };

  const handleCurrentChange = (val) => {
    pagination.value.page = val;
    fetchRatingList();
  };

  // === 评价详情相关方法 ===
  // 修改 showDetails 函数，调用详情接口获取完整数据
  const showDetails = async (row) => {
    dialogVisible.value = true;
    // 在加载新的详情数据前，清空旧的数据，以免显示陈旧信息
    currentRowDetails.value = null;

    try {
      // 调用后台评价详情 API 根据评价ID获取完整的详情数据
      const { data: res } = await getBackRatingDetailApi(row.id);

      if (res.code === 1 && res.data) {
        currentRowDetails.value = res.data; // 使用详情接口返回的数据更新 currentRowDetails
      } else {
        // 业务失败 (code !== 1) 或数据不存在，request.js 已经弹窗提示了后端 msg
        // ElMessage.error(res.msg || '获取评价详情失败'); // <-- 移除此行
        console.warn('获取评价详情业务失败:', res.msg); // 可以保留日志
        dialogVisible.value = false; // 获取失败则关闭弹窗
      }
    } catch (error) {
      // 捕获真正的请求错误
      console.error('获取评价详情请求失败:', error);
      ElMessage.error('获取评价详情失败'); // <-- 这个用于网络或HTTP错误
      dialogVisible.value = false; // 请求失败则关闭弹窗
    }
  };

  // === 辅助方法：格式化评价类型 ===
  const formatRatingType = (type) => {
    switch (type) {
      case 'MOVER':
        return '搬运工';
      case 'SERVICE':
        return '服务项';
      case 'DRIVER':
        return '司机';
      default:
        return '未知';
    }
  };

  // === 组件挂载后，首次加载数据 ===
  onMounted(() => {
    fetchRatingList(); // 获取评价列表
  });
</script>

<style scoped lang="less">
  .admin-order-ratings-container {
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

        // 使子元素（el-form-item 或其他直接子元素）在水平方向上均匀分布
        // justify-content: space-between; /* 如果使用 gap，可以不需要 justify-content: space-between; */

        // 移除或调整原有的 margin-right 和 margin-bottom 规则，因为 gap 会创建间隔
        .el-form-item {
          margin: 0 !important; /* 确保不被其他规则影响 */
          flex-grow: 0; // 不拉伸
          flex-shrink: 1; // 允许缩小
          flex-basis: auto; // 根据内容确定大小
        }
      }

      .range-filters-group {
        width: auto; /* 范围筛选组不需要占据整行 */
        flex-basis: auto;
        // 使内部元素不拉伸，紧凑排列
        .el-form-item__content {
          flex-grow: 0;
          width: auto;
        }
      }

      .range-inputs {
        display: flex; // 使得范围输入框和分隔符在同一行
        align-items: center; // 垂直居中对齐
        gap: 5px; // 使用 gap 控制内部间距
      }

      .range-separator {
        margin: 0 !important; // 移除原有的 margin
        color: #606266; // 保持文字颜色一致
      }

      // 调整 search-form 直接子元素的 el-form-item 的垂直间距 (如果使用了 gap，这些可能不再需要显式设置)
      > .el-form-item {
        flex-shrink: 0; // 防止按钮组缩小
        margin: 0 !important; // 确保不被其他规则影响
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
          flex-shrink: 0; // 防止标签被挤压
        }
        .el-form-item__content {
          line-height: 32px;
          // 确保内容区域不会拉伸，宽度由内部元素决定
          flex-grow: 0;
          width: auto;
          display: flex; /* 使内部元素（如 ElRate）更好地对齐 */
          align-items: center;
          // 移除或调整默认 margin-left
          margin-left: 0 !important;
        }

        // === 设置输入框、选择框、数字输入框、日期选择器组件的固定宽度 ===
        :deep(.el-input),
        :deep(.el-select) {
          width: 200px; /* 基础宽度 */
        }
        :deep(.el-input-number) {
          width: 100px; /* 评分范围输入框的特定宽度 */
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
          // 通用设置
        }
      }

      .button-group {
        margin-left: auto; /* 将按钮组推到最右边 */
        margin-right: 0 !important; /* 确保没有右侧 margin */
        flex-shrink: 0; // 防止按钮组缩小
        margin: 0 !important; // 确保不被其他规则影响

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

        // 如果某些列需要左对齐，可以添加 class 并修改样式
        // &.align-left {
        //     justify-content: flex-start;
        // }
      }
      // 评分列的单元格内容靠左一些，因为星星是左对齐的
      .el-table-column.rating-column :deep(.cell) {
        justify-content: flex-start;
      }

      .detail-button {
        background-color: #f0f0f0 !important;
        border: 1px solid #dcdcdc !important;
        color: #333 !important;
        border-radius: 2px !important;
        margin-right: 5px;
        /* 按钮之间留点间距 */
      }
      .el-button + .el-button {
        /* 按钮之间的默认间距 */
        margin-left: 5px;
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
            width: 120px !important;
            /* 与父级 el-form 的 label-width 一致 */
            text-align: right;
            /* 右对齐 */
          }
          .el-form-item__content {
            color: #333;
            line-height: 1.5;
            word-break: break-all;
            img {
              vertical-align: middle;
            }
            .el-tag {
              vertical-align: middle;
            }
            // 确保内容占据剩余宽度
            flex-grow: 1;
            margin-left: 0 !important; /* 取消 el-form-item__label-wrap 可能产生的左外边距 */
          }
          // 针对包含 ElRate 的 form-item 内容区域，使其内容靠左对齐
          .el-form-item__content .el-rate {
            justify-content: flex-start;
          }
        }
        .el-form-item__content .el-tag {
          vertical-align: middle;
        }
        .el-form-item__content img {
          vertical-align: middle;
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
