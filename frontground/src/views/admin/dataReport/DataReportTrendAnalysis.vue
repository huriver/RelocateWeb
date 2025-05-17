<script setup>
  import { ref, reactive, onMounted, watch, computed, onBeforeUnmount, nextTick } from 'vue';
  import {
    ElCard, ElRow, ElCol, ElDatePicker, ElSelect, ElOption, ElMessage
  } from 'element-plus';
  import {
    getOrderTrendApi,
    getRevenueTrendApi,
    getUserGrowthTrendApi,
  } from '@/api/adminDashboardApi'; // 导入趋势相关的 API
  import { format } from 'date-fns';
  import * as echarts from 'echarts'; // 引入 Echarts

  // === 数据状态 ===
  const loading = reactive({
    orderTrend: false,
    revenueTrend: false,
    userGrowthTrend: false,
  });

  // 趋势图表数据和配置
  const filterParams = reactive({
    dateRange: [null, null], // 默认无日期过滤，或设置默认近一个月等
    timeUnit: 'DAY', // 默认按天统计
    userType: 'CUSTOMER', // 用户增长趋势默认统计消费者
  });

  const orderTrendData = ref([]);
  const revenueTrendData = ref([]);
  const userGrowthTrendData = ref([]);

  // 图表DOM引用
  const orderTrendChartRef = ref(null);
  const revenueTrendChartRef = ref(null);
  const userGrowthTrendChartRef = ref(null);

  let orderTrendChart = null;
  let revenueTrendChart = null;
  let userGrowthTrendChart = null;

  // 控制 Watcher 首次触发的标志位
  const isInitialFetchComplete = ref(false);


  // === 计算属性/配置项 ===

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

  // 时间粒度选项
  const timeUnitOptions = [
    { label: '天', value: 'DAY' },
    { label: '周', value: 'WEEK' },
    { label: '月', value: 'MONTH' },
  ];

  // 用户类型选项 (用于用户增长趋势图)
  const userTypeOptions = [
    { label: '消费者', value: 'CUSTOMER' },
    { label: '司机', value: 'DRIVER' },
    { label: '搬家工人', value: 'MOVER' },
  ];

  // 禁用未来日期
  const disabledDate = (time) => {
    return time.getTime() > Date.now();
  };


  // === 方法 ===

  // 格式化日期范围参数
  const formatDatePickerDate = (date) => {
    return date ? format(date, 'yyyy-MM-dd') : null;
  };

  // 获取订单趋势数据
  const fetchOrderTrendData = async () => {
    const [startDate, endDate] = (filterParams.dateRange || []).map(formatDatePickerDate);
    // 在这里进行日期有效性检查，无效则直接清空数据和图表
    if (!startDate || !endDate) {
      console.warn("日期范围无效或未选择，跳过订单趋势数据获取.");
      orderTrendData.value = [];
      updateOrderTrendChart(); // 清空数据后也要更新图表显示无数据状态
      return;
    }

    loading.orderTrend = true;
    try {
      const { data: res } = await getOrderTrendApi({
        startDate,
        endDate,
        timeUnit: filterParams.timeUnit
      });
      if (res.code === 1) {
        orderTrendData.value = res.data || []; // 确保是数组
      } else {
        ElMessage.error(res.msg || '获取订单趋势数据失败');
        orderTrendData.value = []; // 获取失败也清空数据
      }
    } catch (error) {
      console.error('获取订单趋势数据请求失败:', error);
      ElMessage.error('获取订单趋势数据请求异常');
      orderTrendData.value = []; // 请求异常也清空数据
    } finally {
      loading.orderTrend = false;
      updateOrderTrendChart(); // 无论成功失败，都尝试更新图表
    }
  };

  // 获取营收趋势数据
  const fetchRevenueTrendData = async () => {
    const [startDate, endDate] = (filterParams.dateRange || []).map(formatDatePickerDate);
    // 在这里进行日期有效性检查
    if (!startDate || !endDate) {
      console.warn("日期范围无效或未选择，跳过营收趋势数据获取.");
      revenueTrendData.value = [];
      updateRevenueTrendChart();
      return;
    }

    loading.revenueTrend = true;
    try {
      const { data: res } = await getRevenueTrendApi({
        startDate,
        endDate,
        timeUnit: filterParams.timeUnit
      });
      if (res.code === 1) {
        revenueTrendData.value = res.data || [];
      } else {
        ElMessage.error(res.msg || '获取营收趋势数据失败');
        revenueTrendData.value = [];
      }
    } catch (error) {
      console.error('获取营收趋势数据请求失败:', error);
      ElMessage.error('获取营收趋势数据请求异常');
      revenueTrendData.value = [];
    } finally {
      loading.revenueTrend = false;
      updateRevenueTrendChart();
    }
  };

  // 获取用户增长趋势数据
  const fetchUserGrowthTrendData = async () => {
    const [startDate, endDate] = (filterParams.dateRange || []).map(formatDatePickerDate);
    // 在这里进行日期有效性检查
    if (!startDate || !endDate) {
      console.warn("日期范围无效或未选择，跳过用户增长趋势数据获取.");
      userGrowthTrendData.value = [];
      updateUserGrowthTrendChart();
      return;
    }

    // 确保 userType 不为空，如果为空则使用默认值 'CUSTOMER'
    const currentUserType = filterParams.userType || 'CUSTOMER';


    loading.userGrowthTrend = true;
    try {
      const { data: res } = await getUserGrowthTrendApi({
        startDate,
        endDate,
        timeUnit: filterParams.timeUnit,
        userType: currentUserType // 使用确保不为空的 userType
      });
      if (res.code === 1) {
        userGrowthTrendData.value = res.data || [];
      } else {
        ElMessage.error(res.msg || '获取用户增长趋势数据失败');
        userGrowthTrendData.value = [];
      }
    } catch (error) {
      console.error('获取用户增长趋势数据请求失败:', error);
      ElMessage.error('获取用户增长趋势数据请求异常');
      userGrowthTrendData.value = [];
    } finally {
      loading.userGrowthTrend = false;
      updateUserGrowthTrendChart();
    }
  };

  // 统一的数据获取函数
  const fetchData = () => {
    console.log("开始获取趋势数据...");
    // 调用所有需要根据日期范围和时间粒度变化而更新的数据获取函数
    fetchOrderTrendData();
    fetchRevenueTrendData();
    // 用户增长趋势数据也需要根据日期和粒度变化，同时也受 userType 影响
    // 这里统一触发，fetchUserGrowthTrendData 内部会使用最新的 userType
    fetchUserGrowthTrendData();
  };


  // 初始化 Echarts 实例
  const initCharts = () => {
    console.log("实际执行图表实例初始化...");

    // 添加日志，检查 DOM 元素尺寸 (验证 CSS 是否生效)
    // 此时理论上 DOM 已经渲染，并且 CSS 应该已应用
    console.log("检查图表容器尺寸 (Order):", "clientWidth:", orderTrendChartRef.value?.clientWidth, "clientHeight:", orderTrendChartRef.value?.clientHeight);
    console.log("检查图表容器尺寸 (Revenue):", "clientWidth:", revenueTrendChartRef.value?.clientWidth, "clientHeight:", revenueTrendChartRef.value?.clientHeight);
    console.log("检查图表容器尺寸 (UserGrowth):", "clientWidth:", userGrowthTrendChartRef.value?.clientWidth, "clientHeight:", userGrowthTrendChartRef.value?.clientHeight);

    // 检查ref是否存在，避免在 DOM 未完全加载前初始化
    // 同时也检查图表实例是否已经创建，避免重复初始化
    if (orderTrendChartRef.value && !orderTrendChart) {
      orderTrendChart = echarts.init(orderTrendChartRef.value);
    }
    if (revenueTrendChartRef.value && !revenueTrendChart) {
      revenueTrendChart = echarts.init(revenueTrendChartRef.value);
    }
    if (userGrowthTrendChartRef.value && !userGrowthTrendChart) {
      userGrowthTrendChart = echarts.init(userGrowthTrendChartRef.value);
    }

    // 初始化后，立即尝试根据当前（可能为空）数据更新一次图表
    // 如果数据还没有加载回来，图表会显示“暂无数据”
    // 数据加载回来后，fetchData 中的 finally 块会再次调用 updateChart 来显示数据
    updateOrderTrendChart();
    updateRevenueTrendChart();
    updateUserGrowthTrendChart();
  };

  // 更新订单趋势图表
  const updateOrderTrendChart = () => {
    // 确保图表实例已初始化
    if (!orderTrendChart) {
      //console.warn("Order trend chart instance not initialized when trying to update.");
      return; // 如果实例不存在，退出
    }

    const hasData = orderTrendData.value && orderTrendData.value.length > 0;

    const option = {
      title: {
        text: '订单量趋势',
        left: 'center' // 标题居中
      },
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        data: hasData ? orderTrendData.value.map(item => item.dateLabel) : [],
        axisLabel: {
          interval: 0,
          rotate: hasData && orderTrendData.value.length > 10 ? 30 : 0 // 数据多时旋转标签
        }
      },
      yAxis: { type: 'value', axisLabel: { formatter: '{value}' } },
      series: [{
        name: '订单量',
        type: 'line',
        data: hasData ? orderTrendData.value.map(item => item.count) : [],
        smooth: true,
        emphasis: { focus: 'series' }
      }],
      // 添加数据为空时的提示
      graphic: hasData ? [] : [{
        type: 'text',
        left: 'center',
        top: 'middle',
        style: { text: '暂无数据', fill: '#999', fontSize: 16 }
      }]
    };
    orderTrendChart.setOption(option, true); // Use true to merge options correctly
  };

  // 更新营收趋势图表
  const updateRevenueTrendChart = () => {
    if (!revenueTrendChart) {
      //console.warn("Revenue trend chart instance not initialized when trying to update.");
      return;
    }
    const hasData = revenueTrendData.value && revenueTrendData.value.length > 0;
    const option = {
      title: {
        text: '营收趋势',
        left: 'center' // 标题居中
      },
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        data: hasData ? revenueTrendData.value.map(item => item.dateLabel) : [],
        axisLabel: {
          interval: 0,
          rotate: hasData && revenueTrendData.value.length > 10 ? 30 : 0
        }
      },
      yAxis: { type: 'value', axisLabel: { formatter: '¥{value}' } },
      series: [{
        name: '营收金额',
        type: 'line',
        data: hasData ? revenueTrendData.value.map(item => item.amount) : [],
        smooth: true,
        emphasis: { focus: 'series' }
      }],
      graphic: hasData ? [] : [{
        type: 'text',
        left: 'center',
        top: 'middle',
        style: { text: '暂无数据', fill: '#999', fontSize: 16 }
      }]
    };
    revenueTrendChart.setOption(option, true);
  };

  // 更新用户增长趋势图表
  const updateUserGrowthTrendChart = () => {
    if (!userGrowthTrendChart) {
      //console.warn("User growth trend chart instance not initialized when trying to update.");
      return;
    }
    const hasData = userGrowthTrendData.value && userGrowthTrendData.value.length > 0;
    // 根据当前选中的 userType 查找对应的 label 作为图表标题
    // 注意：这里使用 filterParams.userType，如果它被 watcher 重置了，这里也会反映
    const userTypeLabel = userTypeOptions.find(opt => opt.value === filterParams.userType)?.label || '用户';

    const option = {
      title: {
        text: `${userTypeLabel}增长趋势`,
        left: 'center' // 标题居中
      },
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        data: hasData ? userGrowthTrendData.value.map(item => item.dateLabel) : [],
        axisLabel: {
          interval: 0,
          rotate: hasData && userGrowthTrendData.value.length > 10 ? 30 : 0
        }
      },
      yAxis: { type: 'value', axisLabel: { formatter: '{value}' } },
      series: [{
        name: '新增用户数',
        type: 'line',
        data: hasData ? userGrowthTrendData.value.map(item => item.count) : [],
        smooth: true,
        emphasis: { focus: 'series' }
      }],
      graphic: hasData ? [] : [{
        type: 'text',
        left: 'center',
        top: 'middle',
        style: { text: '暂无数据', fill: '#999', fontSize: 16 }
      }]
    };
    userGrowthTrendChart.setOption(option, true);
  };

  // 响应式处理图表大小
  const resizeCharts = () => {
    orderTrendChart && orderTrendChart.resize();
    revenueTrendChart && revenueTrendChart.resize();
    userGrowthTrendChart && userGrowthTrendChart.resize();
  };

  // === 计算属性，用于 Watcher 追踪依赖 ===
  // 日期范围和时间粒度变化会影响所有图表
  const commonFilterDependencies = computed(() => {
    const [startDate, endDate] = (filterParams.dateRange || []).map(formatDatePickerDate);
    return {
      startDate: startDate,
      endDate: endDate,
      timeUnit: filterParams.timeUnit,
    };
  });

  // 用户类型变化只影响用户增长趋势图
  const userTypeDependency = computed(() => filterParams.userType);


  // === 生命周期钩子 ===
  onMounted(() => {
    console.log("组件挂载...");
    // 1. 设置默认日期，通常是最近一个月
    console.log("设置默认日期...");
    const end = new Date();
    const start = new Date();
    start.setMonth(start.getMonth() - 1);
    // 确保默认结束日期不超过今天，且开始日期不晚于结束日期
    if (start.getTime() > end.getTime()) {
      start.setMonth(end.getMonth() - 1);
      // 极端情况下可能需要进一步调整，例如设置为结束日期前一个月的第一天等
    }
    filterParams.dateRange = [start, end];

    // 2. **立即触发首次数据获取**，基于上面设置的默认参数
    console.log("设置默认日期后，立即触发首次数据获取...");
    fetchData();

    // 3. 首次数据获取已经触发，设置标志位为 true
    // 这里使用 nextTick 确保在 Watcher 可能触发之前设置标志位
    // 但更稳妥的做法是等 fetch 完毕在 finally 里设置标志位，不过这样 Watcher 首次触发时仍然是 false
    // 简单的先设置标志位通常能解决大部分因为同步设置默认值导致 Watcher 立即触发的问题
    nextTick(() => {
      isInitialFetchComplete.value = true;
      console.log("首次数据获取触发标志位已设置:", isInitialFetchComplete.value);
    });


    // 4. 等待 DOM 更新，然后初始化图表实例
    // 在数据加载的同时，DOM 会渲染出来
    nextTick(() => {
      console.log("DOM 已更新，开始初始化图表...");
      initCharts(); // 初始化图表实例

      // 监听窗口大小变化，重新绘制图表
      window.addEventListener('resize', resizeCharts);
    });
  });

  // 监听日期范围和时间粒度变化，重新获取所有图表数据
  // immediate: false 意味着在组件初始化时不会立即触发此 watcher
  watch(commonFilterDependencies, (newDeps, oldDeps) => {
    console.log("通用筛选条件改变触发 watcher.", newDeps);

    // **检查标志位**：如果是初始化阶段 Watcher 被动触发，则跳过数据获取
    if (!isInitialFetchComplete.value) {
      console.log("Watcher 触发，但首次加载未完成，跳过重复获取.");
      return;
    }

    // 只有当有有效日期范围且值实际改变时才触发数据获取
    // 避免在 dateRange 初始化为 [null, null] 时触发，也避免参数不变时重复触发
    // 注意：有了上面的标志位判断，这里的条件主要处理用户后续操作
    if (newDeps.startDate && newDeps.endDate) {
      // 检查新旧值是否实际不同
      if (newDeps.startDate !== (oldDeps?.startDate) || // 使用 ?. 防止 oldDeps 为 null
        newDeps.endDate !== (oldDeps?.endDate) ||
        newDeps.timeUnit !== (oldDeps?.timeUnit)) {
        console.log("筛选条件有效变化，触发数据获取.");
        fetchData(); // 获取所有数据
      } else {
        console.log("筛选条件值未改变，跳过数据获取.");
      }
    } else {
      // 如果日期范围被清空，则清空图表数据
      console.warn("日期范围被清空，清空图表数据.");
      orderTrendData.value = [];
      revenueTrendData.value = [];
      userGrowthTrendData.value = [];
      updateOrderTrendChart();
      updateRevenueTrendChart();
      updateUserGrowthTrendChart();
    }
  }, { immediate: false }); // <-- 确保这里设置了 immediate: false

  // 监听用户类型变化，只重新获取用户增长趋势数据
  watch(userTypeDependency, (newValue, oldValue) => {
    console.log("用户类型改变触发 watcher.", newValue);

    // **检查标志位**：如果是初始化阶段 Watcher 被动触发，则跳过数据获取
    if (!isInitialFetchComplete.value) {
      console.log("用户类型 Watcher 触发，但首次加载未完成，跳过重复获取.");
      return;
    }

    // 检查新值是否为 null 或空字符串 (Element Plus 清除后的值)
    if (newValue === null || newValue === '') {
      console.log("用户类型被清除，重置为消费者.");
      // 将 userType 重置为默认值 'CUSTOMER'
      filterParams.userType = 'CUSTOMER';
      // 注意：上面的赋值会再次触发 watcher，下一次触发时 newValue 就是 'CUSTOMER' 了
      return; // 当前这次触发不执行数据获取
    }

    // 只有当新值与旧值不同时才触发数据获取
    if (newValue !== oldValue) {
      console.log("用户类型有效变化，触发数据获取.");
      // 调用获取用户增长趋势数据的函数
      fetchUserGrowthTrendData();
    } else {
      console.log("用户类型值未改变，跳过数据获取.");
    }
  }, { immediate: false }); // userType 也有默认值，避免初始化时触发


  // 在组件卸载前销毁图表实例和事件监听
  onBeforeUnmount(() => {
    console.log("趋势分析组件卸载，销毁图表实例和事件监听");
    orderTrendChart && orderTrendChart.dispose();
    revenueTrendChart && revenueTrendChart.dispose();
    userGrowthTrendChart && userGrowthTrendChart.dispose();
    window.removeEventListener('resize', resizeCharts);
  });

</script>

<template>
  <div class="data-report-trend-analysis">
    <div class="page-title-container">
      <h2 class="page-title">趋势分析</h2>
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
        <el-col :span="12">
          <div class="filter-item">
            <label>时间粒度:</label>
            <el-select v-model="filterParams.timeUnit" placeholder="选择粒度" clearable>
              <el-option v-for="item in timeUnitOptions" :key="item.value" :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="15" class="data-cards-row mb-4">
      <el-col :span="12">
        <el-card shadow="hover" v-loading="loading.orderTrend" class="chart-card">
          <div ref="orderTrendChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" v-loading="loading.revenueTrend" class="chart-card">
          <div ref="revenueTrendChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="15" class="data-cards-row mb-4">
      <el-col :span="24">
        <el-card shadow="hover" v-loading="loading.userGrowthTrend" class="chart-card">
          <div class="chart-filter-area">
            <div class="filter-item">
              <label><strong>用户类型:</strong></label> <el-select v-model="filterParams.userType"
                         placeholder="选择用户类型" clearable>
                <el-option v-for="item in userTypeOptions" :key="item.value" :label="item.label"
                           :value="item.value">
                </el-option>
              </el-select>
            </div>
          </div>
          <div ref="userGrowthTrendChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

  </div>
</template>

<style lang="less" scoped>
  .data-report-trend-analysis {
    padding: 20px;
    background-color: #f0f2f5;
    min-height: calc(100vh - 60px - 40px); // 减去 header 和 padding 的高度
    font-family: "Microsoft YaHei", sans-serif; // 使用与核心概览相同的字体

    .page-title-container {
      margin-bottom: 30px;
      border-bottom: 1px solid #ddd;
      padding-bottom: 15px;

      .page-title {
        margin: 0;
        color: #333;
        font-size: 24px;
        font-weight: bold;
        text-align: left; // 保持左对齐
      }
    }

    .mb-4 {
      margin-bottom: 25px;
    }

    .filter-card {
      margin-bottom: 25px; // 筛选器卡片底部间距与核心概览一致

      .filter-item {
        display: flex;
        align-items: center;
        justify-content: flex-start; // 内容靠左对齐
        width: 100%;

        label {
          margin-right: 15px; // 增加标签右侧间距
          font-weight: bold;
          color: #555;
          white-space: nowrap;
        }

        .el-date-editor,
        .el-select {
          flex-grow: 1;
          max-width: 100%;
        }
      }
    }

    .data-cards-row {
      // Element Plus 的 gutter 会处理列之间的间距
    }

    .chart-card {
      background-color: #fff;
      padding: 20px; // 增加内边距
      border-radius: 8px; // 圆角
      border: 1px solid #ebeef5;
      transition: all 0.3s ease;
      /* 调整高度，为内部筛选器留出空间 */
      height: 410px; /* 可以在 360px - 400px 之间根据需要调整 */
      box-sizing: border-box;
      display: flex; // 使用 flex 布局来安排内部筛选器和图表
      flex-direction: column; // 垂直方向排列

      &:hover {
        border-color: #409eff;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      }
    }

    .chart-filter-area {
      margin-bottom: 15px; /* 筛选区域和图表之间的间距 */
      padding-bottom: 15px; /* 可以在这里添加下边框区分 */
      border-bottom: 1px solid #eee; /* 可选：添加分隔线 */
      .filter-item {
        /* chart-card 已经用了 flex column，这里 filter-item 保持 flex row */
        display: flex;
        align-items: center;
        justify-content: flex-start;
        width: auto; /* 宽度不再需要占满父容器 */
        label {
          margin-right: 15px;
          font-weight: normal; /* 内部筛选器的标签权重可以低一些 */
          color: #555;
        }
        .el-select {
          width: 180px; /* 给选择器一个固定或最大宽度 */
        }
      }
    }

    .chart {
      /* 解决 ECharts 初始化时获取不到高度的问题 */
      height: 300px; /* 给一个明确的高度 */
      /* 或者使用 min-height 如果希望图表内容决定高度，但需要保证最小值 */
      /* min-height: 300px; */

      width: 100%;
      flex-grow: 1; /* 让图表区域填充剩余空间 (在有明确高度的情况下，此规则可能不那么关键，但保留以配合 flex 布局) */
    }
  }
</style>