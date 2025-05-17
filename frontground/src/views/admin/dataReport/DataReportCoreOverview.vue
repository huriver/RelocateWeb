<script setup>
  import { ref, reactive, onMounted, watch } from 'vue';
  import {
    ElCard, ElRow, ElCol, ElDatePicker, ElMessage, ElSkeleton
  } from 'element-plus';
  // 导入图标组件
  import {
    DocumentCopy, // 总订单数
    CircleCheckFilled, // 已完成订单
    CircleCloseFilled, // 已取消订单
    WalletFilled, // 总营收
    UserFilled, // 用户相关
    PriceTag // 平均价格
  } from '@element-plus/icons-vue';

  import {
    getBusinessDataApi,
  } from '@/api/adminDashboardApi';
  import { format } from 'date-fns';

  // === 数据状态 ===
  const loading = reactive({
    businessData: false,
  });

  // 核心运营数据
  const businessData = ref({
    totalOrderCount: 0,
    completedOrderCount: 0,
    cancelledOrderCount: 0,
    totalRevenue: 0,
    totalUserCount: 0,
    totalCustomerCount: 0,
    totalDriverCount: 0,
    totalMoverCount: 0,
    averageOrderPrice: 0,
  });

  // 筛选参数 (只保留日期范围)
  const filterParams = reactive({
    dateRange: [null, null],
  });

  // 日期选择器的快捷选项
  const shortcuts = [
    {
      text: '最近一周',
      value: () => {
        const end = new Date();
        const start = new Date();
        start.setDate(start.getDate() - 7);
        return [start, end];
      },
    },
    {
      text: '最近一个月',
      value: () => {
        const end = new Date();
        const start = new Date();
        start.setMonth(start.getMonth() - 1);
        return [start, end];
      },
    },
    {
      text: '最近三个月',
      value: () => {
        const end = new Date();
        const start = new Date();
        start.setMonth(start.getMonth() - 3);
        return [start, end];
      },
    },
    {
      text: '今年至今',
      value: () => {
        const end = new Date();
        const start = new Date(new Date().getFullYear(), 0, 1);
        return [start, end];
      },
    },
  ];

  // === 方法 ===

  // 禁用未来日期
  const disabledDate = (time) => {
    // time 是日期对象，需要比较其时间戳
    // Date.now() 获取当前时间的时间戳
    return time.getTime() > Date.now();
  };


  // 格式化日期范围参数
  const formatDatePickerDate = (date) => {
    return date ? format(date, 'yyyy-MM-dd') : null;
  };

  // 获取核心运营数据
  const fetchBusinessData = async () => {
    const [startDate, endDate] = filterParams.dateRange.map(formatDatePickerDate);

    loading.businessData = true;
    try {
      const { data: res } = await getBusinessDataApi({ startDate, endDate });
      if (res.code === 1) {
        businessData.value = res.data;
      } else {
        ElMessage.error(res.msg || '获取核心运营数据失败');
        // 清空数据
        businessData.value = {
          totalOrderCount: 0, completedOrderCount: 0, cancelledOrderCount: 0,
          totalRevenue: 0, totalUserCount: 0, totalCustomerCount: 0,
          totalDriverCount: 0, totalMoverCount: 0, averageOrderPrice: 0,
        };
      }
    } catch (error) {
      console.error('获取核心运营数据请求失败:', error);
      ElMessage.error('获取核心运营数据请求异常');
      // 清空数据
      businessData.value = {
        totalOrderCount: 0, completedOrderCount: 0, cancelledOrderCount: 0,
        totalRevenue: 0, totalUserCount: 0, totalCustomerCount: 0,
        totalDriverCount: 0, totalMoverCount: 0, averageOrderPrice: 0,
      };
    } finally {
      loading.businessData = false;
    }
  };

  // === 生命周期钩子 ===
  onMounted(() => {
    // 默认选择最近一个月，但要确保结束日期不超过今天
    const end = new Date();
    const start = new Date();
    start.setMonth(start.getMonth() - 1);

    // 调整开始日期，如果计算出来在今天之后，则设置为今天的前一个月
    if (start.getTime() > end.getTime()) {
      start.setFullYear(end.getFullYear());
      start.setMonth(end.getMonth() - 1);
      start.setDate(end.getDate());
    }

    filterParams.dateRange = [start, end];

    // fetchBusinessData(); // 由 watch 触发首次加载
  });

  // 监听日期范围变化，重新获取数据
  watch(() => filterParams.dateRange, (newRange, oldRange) => {
    const oldStart = Array.isArray(oldRange) ? oldRange[0] : null;
    const oldEnd = Array.isArray(oldRange) ? oldRange[1] : null;
    const newStart = Array.isArray(newRange) ? newRange[0] : null;
    const newEnd = Array.isArray(newRange) ? newRange[1] : null;

    const oldStartDateString = oldStart ? format(oldStart, 'yyyy-MM-dd') : null;
    const oldEndDateString = oldEnd ? format(oldEnd, 'yyyy-MM-dd') : null;
    const newStartDateString = newStart ? format(newStart, 'yyyy-MM-dd') : null;
    const newEndDateString = newEnd ? format(newEnd, 'yyyy-MM-dd') : null;


    if (oldStartDateString !== newStartDateString || oldEndDateString !== newEndDateString) {
      console.log("核心概览日期范围改变，重新获取数据:", newRange);
      fetchBusinessData();
    } else {
      console.log("核心概览日期范围未变化或无效，不重新获取数据.");
    }
  }, { deep: true, immediate: true }); // 深度监听，并在组件挂载后立即执行一次

  // helper to format currency
  const formatCurrency = (value) => {
    if (value === undefined || value === null || isNaN(value)) return '0.00';
    return parseFloat(value).toFixed(2); // 确保是数字再格式化
  };

</script>

<template>
  <div class="data-report-core-overview">
    <div class="page-title-container">
      <h2 class="page-title">核心概览</h2>
    </div>

    <el-card class="filter-card mb-4" shadow="never">
      <el-row :gutter="20" align="middle">
        <el-col :span="12">
          <div class="filter-item">
            <label>日期范围:</label>
            <el-date-picker v-model="filterParams.dateRange" type="daterange" range-separator="至"
                            start-placeholder="开始日期" end-placeholder="结束日期" :shortcuts="shortcuts"
                            value-format="YYYY-MM-DD" clearable :disabled-date="disabledDate" />
          </div>
        </el-col>
      </el-row>
    </el-card>

    <h3 class="data-group-title">订单与营收数据</h3>
    <el-row :gutter="15" class="data-cards-row mb-4" v-loading="loading.businessData">
      <el-col :span="6">
        <el-card shadow="hover" class="overview-card">
          <div class="card-content">
            <div class="card-icon">
              <el-icon size="24">
                <DocumentCopy />
              </el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">总订单数</div>
              <div class="card-value">{{ businessData.totalOrderCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="overview-card completed-orders">
          <div class="card-content">
            <div class="card-icon">
              <el-icon size="24">
                <CircleCheckFilled />
              </el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">已完成订单</div>
              <div class="card-value">{{ businessData.completedOrderCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="overview-card cancelled-orders">
          <div class="card-content">
            <div class="card-icon">
              <el-icon size="24">
                <CircleCloseFilled />
              </el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">已取消订单</div>
              <div class="card-value">{{ businessData.cancelledOrderCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="overview-card total-revenue">
          <div class="card-content">
            <div class="card-icon">
              <el-icon size="24">
                <WalletFilled />
              </el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">总营收金额</div>
              <div class="card-value currency">¥{{ formatCurrency(businessData.totalRevenue) }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <h3 class="data-group-title">用户数据</h3>
    <el-row :gutter="15" class="data-cards-row mb-4" v-loading="loading.businessData">
      <el-col :span="6">
        <el-card shadow="hover" class="overview-card total-users">
          <div class="card-content">
            <div class="card-icon">
              <el-icon size="24">
                <UserFilled />
              </el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">总用户数</div>
              <div class="card-value">{{ businessData.totalUserCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="overview-card customer-users">
          <div class="card-content">
            <div class="card-icon">
              <el-icon size="24">
                <UserFilled />
              </el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">消费者用户数</div>
              <div class="card-value">{{ businessData.totalCustomerCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="overview-card driver-users">
          <div class="card-content">
            <div class="card-icon">
              <el-icon size="24">
                <UserFilled />
              </el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">司机用户数</div>
              <div class="card-value">{{ businessData.totalDriverCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="overview-card mover-users">
          <div class="card-content">
            <div class="card-icon">
              <el-icon size="24">
                <UserFilled />
              </el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">搬家工人用户数</div>
              <div class="card-value">{{ businessData.totalMoverCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <h3 class="data-group-title">平均订单金额</h3>
    <el-row :gutter="15" class="data-cards-row mb-4" v-loading="loading.businessData">
      <el-col :span="6">
        <el-card shadow="hover" class="overview-card average-price">
          <div class="card-content">
            <div class="card-icon">
              <el-icon size="24">
                <PriceTag />
              </el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">平均订单金额</div>
              <div class="card-value currency">
                ¥{{ formatCurrency(businessData.averageOrderPrice) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="18"></el-col>
    </el-row>

  </div>
</template>

<style lang="less" scoped>
  .data-report-core-overview {
    padding: 20px;
    background-color: #f0f2f5;
    min-height: calc(100vh - 60px - 40px);
    font-family: "Microsoft YaHei", sans-serif;

    .page-title-container {
      margin-bottom: 30px;
      border-bottom: 1px solid #ddd;
      padding-bottom: 15px;
      .page-title {
        margin: 0;
        color: #333;
        font-size: 24px;
        font-weight: bold;
        text-align: left;
      }
    }

    .data-group-title {
      font-size: 18px;
      color: #555;
      margin-top: 25px;
      margin-bottom: 15px;
      padding-left: 10px;
      border-left: 4px solid #409eff;
    }

    .mb-4 {
      margin-bottom: 25px; /* 增加行之间的垂直间距 */
    }

    .filter-card {
      margin-bottom: 25px;
      .filter-item {
        display: flex;
        align-items: center;
        /* 优化日期范围选择器布局 */
        justify-content: flex-start; /* 内容靠左对齐 */
        /* width: 100%; 不需要这个，flex item 会根据内容和容器宽度自适应 */

        label {
          margin-right: 15px;
          font-weight: bold;
          color: #555;
          white-space: nowrap;
        }
        .el-date-editor {
          flex-grow: 0; /* 不允许日期选择器拉伸 */
          /* 保持 max-width 限制，例如 300px 到 400px 比较合适 */
          max-width: 350px; /* 适当调整最大宽度 */
          width: 100%; /* 在 max-width 范围内占满可用空间 */
        }
      }
    }

    .data-cards-row {
      // Element Plus 的 gutter 会处理列之间的间距，这里主要调整行间距
      // 垂直间距由 .mb-4 控制
    }

    .overview-card {
      text-align: center;
      background-color: #fff;
      padding: 20px;
      border-radius: 8px;
      border: 1px solid #ebeef5;
      transition: all 0.3s ease;
      height: 100%;
      box-sizing: border-box;

      &:hover {
        border-color: #409eff;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      }

      .card-content {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
        height: 100%;
      }

      .card-icon {
        margin-bottom: 10px;
        color: #409eff;
        .el-icon {
          // 图标本身的样式调整
        }
      }

      .card-info {
        text-align: center;
      }

      .card-title {
        font-size: 13px;
        color: #909399;
        margin-bottom: 6px;
        font-weight: normal;
      }
      .card-value {
        font-size: 24px;
        font-weight: bold;
        color: #303133;
      }

      .card-value.currency {
        color: #67c23a;
      }
    }

    /* 特殊卡片颜色区分 */
    .overview-card.completed-orders .card-value {
      color: #67c23a;
    }
    .overview-card.cancelled-orders .card-value {
      color: #f56c6c;
    }
    .overview-card.total-revenue .card-value {
      color: #e6a23c;
    }
    .overview-card.average-price .card-value {
      color: #e6a23c;
    }
    .overview-card.total-users .card-value {
      color: #409eff;
    }
    .overview-card.customer-users .card-value,
    .overview-card.driver-users .card-value,
    .overview-card.mover-users .card-value {
      color: #303133;
    }
  }
</style>