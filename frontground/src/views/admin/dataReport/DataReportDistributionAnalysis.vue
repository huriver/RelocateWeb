<script setup>
  import { ref, reactive, onMounted, watch, computed, onBeforeUnmount, nextTick } from 'vue';
  import {
    ElCard, ElRow, ElCol, ElDatePicker, ElSelect, ElOption, ElMessage
  } from 'element-plus';
  import {
    getOrderStatusDistributionApi,
    getServiceResourceDistributionApi,
    getRatingDistributionApi,
  } from '@/api/adminDashboardApi'; // 导入分布相关的 API
  import { format } from 'date-fns';
  import * as echarts from 'echarts'; // 引入 Echarts

  // === 数据状态 ===
  const loading = reactive({
    orderStatusDistribution: false,
    serviceResourceDistribution: false,
    ratingDistribution: false,
  });

  // 分布图表数据和配置
  const filterParams = reactive({
    dateRange: [null, null], // 默认无日期过滤，或设置默认近一个月等
    resourceType: 'SERVICE', // 服务资源分布默认统计服务项
    ratingType: 'SERVICE', // 评分分布默认统计服务评分
  });

  const orderStatusDistributionData = ref([]);
  const serviceResourceDistributionData = ref([]);
  const ratingDistributionData = ref([]);


  // 图表DOM引用
  const orderStatusChartRef = ref(null);
  const serviceResourceChartRef = ref(null);
  const ratingDistributionChartRef = ref(null);

  let orderStatusChart = null;
  let serviceResourceChart = null;
  let ratingDistributionChart = null;

  // 控制 Watcher 首次触发的标志位，用于避免初始化时重复请求
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

  // 服务资源类型选项 (用于服务资源分布图)
  const resourceTypeOptions = [
    { label: '服务项', value: 'SERVICE' },
    { label: '货车类型', value: 'TRUCK_TYPE' },
  ];

  // 评分类型选项 (用于评分分布图)
  const ratingTypeOptions = [
    { label: '服务评价', value: 'SERVICE' },
    { label: '司机评价', value: 'DRIVER' },
    { label: '搬家工人评价', value: 'MOVER' },
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

  // 获取订单状态分布数据
  const fetchOrderStatusDistributionData = async () => {
    const [startDate, endDate] = (filterParams.dateRange || []).map(formatDatePickerDate);
    if (!startDate || !endDate) {
      console.warn("日期范围无效或未选择，跳过订单状态分布数据获取.");
      orderStatusDistributionData.value = [];
      updateOrderStatusChart();
      return;
    }

    loading.orderStatusDistribution = true;
    try {
      const { data: res } = await getOrderStatusDistributionApi({ startDate, endDate });
      if (res.code === 1) {
        orderStatusDistributionData.value = res.data || [];
      } else {
        ElMessage.error(res.msg || '获取订单状态分布数据失败');
        orderStatusDistributionData.value = [];
      }
    } catch (error) {
      console.error('获取订单状态分布数据请求失败:', error);
      ElMessage.error('获取订单状态分布数据请求异常');
      orderStatusDistributionData.value = [];
    } finally {
      loading.orderStatusDistribution = false;
      updateOrderStatusChart();
    }
  };

  // 获取服务资源分布数据
  const fetchServiceResourceDistributionData = async () => {
    const [startDate, endDate] = (filterParams.dateRange || []).map(formatDatePickerDate);
    if (!startDate || !endDate) {
      console.warn("日期范围无效或未选择，跳过服务资源分布数据获取.");
      serviceResourceDistributionData.value = [];
      updateServiceResourceChart();
      return;
    }
    const resourceType = filterParams.resourceType || 'SERVICE';

    loading.serviceResourceDistribution = true;
    try {
      const { data: res } = await getServiceResourceDistributionApi({
        startDate,
        endDate,
        type: resourceType
      });
      if (res.code === 1) {
        serviceResourceDistributionData.value = res.data || [];
      } else {
        ElMessage.error(res.msg || '获取服务资源分布数据失败');
        serviceResourceDistributionData.value = [];
      }
    } catch (error) {
      console.error('获取服务资源分布数据请求失败:', error);
      ElMessage.error('获取服务资源分布数据请求异常');
      serviceResourceDistributionData.value = [];
    } finally {
      loading.serviceResourceDistribution = false;
      updateServiceResourceChart();
    }
  };

  // 获取评分分布数据
  const fetchRatingDistributionData = async () => {
    const [startDate, endDate] = (filterParams.dateRange || []).map(formatDatePickerDate);
    if (!startDate || !endDate) {
      console.warn("日期范围无效或未选择，跳过评分分布数据获取.");
      ratingDistributionData.value = [];
      updateRatingDistributionChart();
      return;
    }
    const ratingType = filterParams.ratingType || 'SERVICE';

    loading.ratingDistribution = true;
    try {
      const { data: res } = await getRatingDistributionApi({
        startDate,
        endDate,
        ratingType: ratingType
      });
      if (res.code === 1) {
        ratingDistributionData.value = res.data || [];
      } else {
        ElMessage.error(res.msg || '获取评分分布数据失败');
        ratingDistributionData.value = [];
      }
    } catch (error) {
      console.error('获取评分分布数据请求失败:', error);
      ElMessage.error('获取评分分布数据请求异常');
      ratingDistributionData.value = [];
    } finally {
      loading.ratingDistribution = false;
      updateRatingDistributionChart();
    }
  };

  // 获取所有分布数据 (受日期范围变化影响)
  const fetchDateDependentData = () => {
    console.log("日期范围变化，触发数据获取...");
    fetchOrderStatusDistributionData();
    fetchServiceResourceDistributionData(); // 服务资源也受日期影响
    fetchRatingDistributionData(); // 评分也受日期影响
  };

  // 只获取服务资源数据 (受资源类型变化影响)
  const fetchServiceResourceOnly = () => {
    console.log("资源类型变化，触发服务资源数据获取...");
    fetchServiceResourceDistributionData();
  };

  // 只获取评分数据 (受评价类型变化影响)
  const fetchRatingOnly = () => {
    console.log("评价类型变化，触发评分数据获取...");
    fetchRatingDistributionData();
  };


  // 初始化 Echarts 实例
  const initCharts = () => {
    console.log("实际执行图表实例初始化...");
    nextTick(() => {
      if (orderStatusChartRef.value && !orderStatusChart) {
        orderStatusChart = echarts.init(orderStatusChartRef.value);
      }
      if (serviceResourceChartRef.value && !serviceResourceChart) {
        serviceResourceChart = echarts.init(serviceResourceChartRef.value);
      }
      if (ratingDistributionChartRef.value && !ratingDistributionChart) {
        ratingDistributionChart = echarts.init(ratingDistributionChartRef.value);
      }
      // 初始化后，尝试根据当前（可能为空）数据更新图表
      updateOrderStatusChart();
      updateServiceResourceChart();
      updateRatingDistributionChart();
    });
  };


  // 更新订单状态分布图表 (饼图)
  const updateOrderStatusChart = () => {
    if (!orderStatusChart) {
      return;
    }
    const hasData = orderStatusDistributionData.value && orderStatusDistributionData.value.length > 0;
    const option = {
      title: { text: '订单状态分布', left: 'center' },
      tooltip: { trigger: 'item' },
      legend: { orient: 'vertical', left: 'left' },
      series: [{
        name: '订单数量',
        type: 'pie',
        radius: '50%',
        // 将饼图圆心向右移动
        center: ['60%', '60%'], // 将水平位置从 50% 移到 60%
        data: hasData ? orderStatusDistributionData.value.map(item => ({
          value: item.count,
          name: item.statusName,
        })).sort((a, b) => b.value - a.value) : [],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
          },
        },
        label: {
          formatter: '{b}: {c} ({d}%)'
        },
        labelLine: {
          show: hasData
        }
      }],
      graphic: hasData ? [] : [{
        type: 'text',
        left: 'center',
        top: 'middle',
        style: { text: '暂无数据', fill: '#999', fontSize: 16 }
      }]
    };
    orderStatusChart.setOption(option, true);
  };

  // 更新服务资源分布图表 (柱状图)
  const updateServiceResourceChart = () => {
    if (!serviceResourceChart) {
      return;
    }
    const hasData = serviceResourceDistributionData.value && serviceResourceDistributionData.value.length > 0;
    const resourceTypeName = resourceTypeOptions.find(opt => opt.value === filterParams.resourceType)?.label || '资源';
    const option = {
      title: { text: `${resourceTypeName}使用分布`, left: 'center' },
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '4%', bottom: '10%', containLabel: true },
      xAxis: {
        type: 'category',
        data: hasData ? serviceResourceDistributionData.value.map(item => item.name) : [],
        axisLabel: {
          rotate: hasData && serviceResourceDistributionData.value.length > 5 ? 45 : 0,
          interval: 0, // 或者尝试 'auto'
        },
        axisTick: { alignWithLabel: true }
      },
      yAxis: { type: 'value' },
      series: [{
        name: '使用次数/订单数',
        type: 'bar',
        data: hasData ? serviceResourceDistributionData.value.map(item => item.count) : [],
        itemStyle: {
          color: '#5470C6'
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}'
        }
      }],
      graphic: hasData ? [] : [{
        type: 'text',
        left: 'center',
        top: 'middle',
        style: { text: '暂无数据', fill: '#999', fontSize: 16 }
      }]
    };
    serviceResourceChart.setOption(option, true);
  };

  // 更新评分分布图表 (柱状图)
  const updateRatingDistributionChart = () => {
    if (!ratingDistributionChart) {
      return;
    }
    const hasData = ratingDistributionData.value && ratingDistributionData.value.length > 0;
    // === 修改点: 在查找 ratingTypeName 时，如果 filterParams.ratingType 为空，使用 'SERVICE' 作为默认查找值 ===
    // 这样即使 watcher 的重置逻辑还没走完，或者其他原因导致 ratingType 为空，标题也会回退到服务评价
    const effectiveRatingType = filterParams.ratingType || 'SERVICE'; // 如果 filterParams.ratingType 为空，则使用 'SERVICE'
    const ratingTypeName = ratingTypeOptions.find(opt => opt.value === effectiveRatingType)?.label || '评价'; // 使用 effectiveRatingType 来查找标签

    const option = {
      title: { text: `${ratingTypeName}星级分布`, left: 'center' },
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '4%', bottom: '10%', containLabel: true },
      xAxis: {
        type: 'category',
        data: [1, 2, 3, 4, 5].map(star => star + '星'),
        axisTick: { alignWithLabel: true },
        axisLabel: { rotate: 0, interval: 0 }
      },
      yAxis: { type: 'value' },
      series: [{
        name: '评价数量',
        type: 'bar',
        data: [1, 2, 3, 4, 5].map(star => {
          const item = ratingDistributionData.value.find(d => d.ratingValue === star);
          return item ? item.count : 0;
        }),
        itemStyle: {
          color: (params) => {
            const ratingValue = params.dataIndex + 1;
            const colors = ['#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272'];
            return colors[ratingValue - 1] || '#999';
          }
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}'
        }
      }],
      graphic: hasData ? [] : [{
        type: 'text',
        left: 'center',
        top: 'middle',
        style: { text: '暂无数据', fill: '#999', fontSize: 16 }
      }]
    };
    ratingDistributionChart.setOption(option, true);
  };


  // 响应式处理图表大小
  const resizeCharts = () => {
    orderStatusChart && orderStatusChart.resize();
    serviceResourceChart && serviceResourceChart.resize();
    ratingDistributionChart && ratingDistributionChart.resize();
  };


  // === 生命周期钩子 ===
  onMounted(() => {
    console.log("组件挂载...");
    const end = new Date();
    const start = new Date();
    start.setMonth(start.getMonth() - 1);
    if (start.getTime() > end.getTime()) {
      start.setMonth(end.getMonth() - 1);
    }
    filterParams.dateRange = [start, end];

    console.log("设置默认日期后，立即触发首次数据获取...");
    // 首次获取数据时，所有依赖的参数 (日期范围、资源类型、评价类型) 都是默认值
    fetchDateDependentData(); // 触发日期范围影响的所有数据获取


    nextTick(() => {
      isInitialFetchComplete.value = true;
      console.log("首次数据获取触发标志位已设置:", isInitialFetchComplete.value);
    });


    nextTick(() => {
      console.log("DOM 已更新，开始初始化图表...");
      initCharts();

      window.addEventListener('resize', resizeCharts);
    });
  });


  // === 优化后的 Watchers ===

  // 监听日期范围变化，影响所有图表
  watch(() => filterParams.dateRange, (newRange) => {
    console.log("日期范围改变触发 watcher.", newRange);
    if (!isInitialFetchComplete.value) {
      console.log("日期范围 Watcher 触发，但首次加载未完成，跳过.");
      return;
    }

    const [startDate, endDate] = (newRange || []).map(formatDatePickerDate);

    if (startDate && endDate) {
      console.log("日期范围有效变化，触发日期范围影响的数据获取.");
      fetchDateDependentData(); // 日期变化，重新获取所有受日期影响的数据
    } else {
      // 如果日期范围被清空，则清空图表数据
      console.warn("日期范围被清空，清空图表数据.");
      orderStatusDistributionData.value = [];
      serviceResourceDistributionData.value = [];
      ratingDistributionData.value = [];
      updateOrderStatusChart();
      updateServiceResourceChart();
      updateRatingDistributionChart();
    }
  }, { deep: true });

  // 监听资源类型变化，只影响服务资源分布图
  watch(() => filterParams.resourceType, (newValue, oldValue) => {
    console.log("资源类型改变触发 watcher.", newValue, "旧值:", oldValue);

    // 处理清除资源类型的情况，重置为默认值 'SERVICE'
    if (newValue === undefined || newValue === null || newValue === '') {
      console.log("资源类型被清除，重置为服务项.");
      filterParams.resourceType = 'SERVICE';
      return; // 避免在当前 watcher 周期内获取数据
    }

    // 只有在首次加载完成后且值实际变化时才响应
    if (!isInitialFetchComplete.value || newValue === oldValue) {
      console.log("资源类型 Watcher 触发，但首次加载未完成或值未变，跳过.");
      return;
    }

    // 在获取数据前检查日期范围是否有效 (资源类型变化也需要依赖有效的日期范围)
    const [startDate, endDate] = (filterParams.dateRange || []).map(formatDatePickerDate);
    if (startDate && endDate) {
      console.log("资源类型有效变化，日期范围有效，触发服务资源数据获取.");
      fetchServiceResourceOnly(); // 只获取服务资源数据
    } else {
      console.log("资源类型改变，但日期范围无效，不触发数据获取.");
      // 日期范围监听器已经处理了数据清空，这里无需额外操作
    }
  });

  // 监听评价类型变化，只影响评分分布图
  watch(() => filterParams.ratingType, (newValue, oldValue) => {
    console.log("评价类型改变触发 watcher.", newValue, "旧值:", oldValue);
    // 在 watcher 的重置逻辑之前记录旧值，以备数据获取时检查日期范围
    const [startDate, endDate] = (filterParams.dateRange || []).map(formatDatePickerDate);


    if (newValue === undefined || newValue === null || newValue === '') { // 添加了 undefined 检查
      console.log("评价类型被清除，重置为服务评价.");
      filterParams.ratingType = 'SERVICE';
      // 注意：重置 ratingType 会再次触发 watcher，下一次触发时 newValue 将是 'SERVICE'
      // 在这个清除的周期，我们不需要获取数据或更新图表，因为重置后的 watcher 会处理
      return;
    }

    // 只有在首次加载完成后且值实际变化时才响应
    if (!isInitialFetchComplete.value || newValue === oldValue) {
      console.log("评价类型 Watcher 触发，但首次加载未完成或值未变，跳过.");
      return;
    }

    // 在获取数据前检查日期范围是否有效 (评价类型变化也需要依赖有效的日期范围)
    if (startDate && endDate) {
      console.log("评价类型有效变化，日期范围有效，触发评分分布数据获取.");
      fetchRatingOnly(); // 只获取评分数据
    } else {
      console.log("评价类型改变，但日期范围无效，不触发数据获取.");
      // 日期范围监听器已经处理了数据清空，这里无需额外操作
    }
  });


  // 在组件卸载前销毁图表实例和事件监听
  onBeforeUnmount(() => {
    console.log("分布分析组件卸载，销毁图表实例和事件监听");
    orderStatusChart && orderStatusChart.dispose();
    serviceResourceChart && serviceResourceChart.dispose();
    ratingDistributionChart && ratingDistributionChart.dispose();
    window.removeEventListener('resize', resizeCharts);
  });

</script>

<template>
  <div class="data-report-distribution-analysis">
    <div class="page-title-container">
      <h2 class="page-title">分布分析</h2>
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
            <label>评价类型:</label>
            <el-select v-model="filterParams.ratingType" placeholder="选择评价类型" clearable>
              <el-option v-for="item in ratingTypeOptions" :key="item.value" :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="15" class="data-cards-row mb-4">
      <el-col :span="13"> <el-card shadow="hover" v-loading="loading.orderStatusDistribution">
          <div ref="orderStatusChartRef" class="chart chart-pie-large"></div>
        </el-card>
      </el-col>
      <el-col :span="11"> <el-card shadow="hover" v-loading="loading.ratingDistribution"
                 class="chart-card">
          <div ref="ratingDistributionChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="15" class="data-cards-row mb-4">
      <el-col :span="24"> <el-card shadow="hover" v-loading="loading.serviceResourceDistribution"
                 class="chart-card">
          <div class="chart-filter-area">
            <div class="filter-item">
              <label><strong>资源类型:</strong></label>
              <el-select v-model="filterParams.resourceType" placeholder="选择资源类型" clearable>
                <el-option v-for="item in resourceTypeOptions" :key="item.value" :label="item.label"
                           :value="item.value">
                </el-option>
              </el-select>
            </div>
          </div>
          <div ref="serviceResourceChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

  </div>
</template>

<style lang="less" scoped>
  .data-report-distribution-analysis {
    padding: 20px;
    background-color: #f0f2f5;
    min-height: calc(100vh - 60px - 40px); // 减去 header 和 padding 的高度
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

    .mb-4 {
      margin-bottom: 25px;
    }

    .filter-card {
      margin-bottom: 25px;

      .filter-item {
        display: flex;
        align-items: center;
        justify-content: flex-start;
        width: 100%; // 主筛选区域的 filter-item 宽度占满
        label {
          margin-right: 15px;
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

    /* 应用到包含柱状图并带有内部筛选的卡片 */
    .chart-card {
      background-color: #fff;
      padding: 20px;
      border-radius: 8px;
      border: 1px solid #ebeef5;
      transition: all 0.3s ease;
      box-sizing: border-box;
      display: flex; /* 使用 flex 布局来安排内部筛选器和图表 */
      flex-direction: column; /* 垂直方向排列 */

      &:hover {
        border-color: #409eff;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      }
    }

    /* 饼图卡片不需要 flex column 布局，但保留其他样式 */
    .el-card {
      // 继承上面的通用 .el-card 样式
    }

    .chart-filter-area {
      margin-bottom: 15px; /* 筛选区域和图表之间的间距 */
      padding-bottom: 15px; /* 可选：添加底部内边距 */
      border-bottom: 1px solid #eee; /* 可选：添加分隔线 */
      display: flex; /* 内部 filter-item 使用 flex 布局 */
      align-items: center; /* 垂直居中 */
      .filter-item {
        /* chart-filter-area 已经用了 flex row，这里 filter-item 不再需要 width: 100% */
        display: flex;
        align-items: center;
        justify-content: flex-start;
        width: auto; /* 宽度根据内容自适应 */
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
      height: 350px; /* 柱状图和评分图使用这个高度，作为 flex item 时是最小高度 */
      width: 100%;
      flex-grow: 1; /* 让图表区域填充卡片内的剩余空间 */
    }

    .chart-pie-large {
      width: 100%;
      height: 400px; /* 饼图的高度 */
      // 饼图卡片没有 flex column 布局，所以图表高度就是这个值
    }
  }
</style>