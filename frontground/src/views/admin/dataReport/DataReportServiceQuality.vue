<script setup>
  import { ref, reactive, onMounted, watch, onBeforeUnmount } from 'vue'; // 导入 onBeforeUnmount
  import {
    ElCard, ElRow, ElCol, ElTable, ElTableColumn, ElMessage, ElSkeleton,
    ElInputNumber, ElButton, ElForm, ElFormItem
  } from 'element-plus';
  import {
    getTopDriversByRatingApi,
    getTopMoversByRatingApi,
    getTopServicesByRatingApi
  } from '@/api/adminDashboardApi'; // 导入相关的 API

  // === 数据状态 ===
  const loading = reactive({
    topDrivers: false,
    topMovers: false,
    topServices: false,
  });

  // 排名数据
  const topDrivers = ref([]);
  const topMovers = ref([]);
  const topServices = ref([]);

  // 筛选参数
  const filterParams = reactive({
    limit: 10, // 显示前几名
    minRatingCount: 1, // 最低评价次数
  });

  // === 方法 ===

  // 获取评分靠前的司机列表
  const fetchTopDrivers = async () => {
    loading.topDrivers = true;
    try {
      const { data: res } = await getTopDriversByRatingApi({
        limit: filterParams.limit,
        minRatingCount: filterParams.minRatingCount
      });
      if (res.code === 1) {
        topDrivers.value = res.data;
      } else {
        ElMessage.error(res.msg || '获取评分靠前司机列表失败');
        topDrivers.value = [];
      }
    } catch (error) {
      console.error('获取评分靠前司机列表请求失败:', error);
      ElMessage.error('获取评分靠前司机列表请求异常');
      topDrivers.value = [];
    } finally {
      loading.topDrivers = false;
    }
  };

  // 获取评分靠前的搬家工人列表
  const fetchTopMovers = async () => {
    loading.topMovers = true;
    try {
      const { data: res } = await getTopMoversByRatingApi({
        limit: filterParams.limit,
        minRatingCount: filterParams.minRatingCount
      });
      if (res.code === 1) {
        topMovers.value = res.data;
      } else {
        ElMessage.error(res.msg || '获取评分靠前搬家工人列表失败');
        topMovers.value = [];
      }
    } catch (error) {
      console.error('获取评分靠前搬家工人列表请求失败:', error);
      ElMessage.error('获取评分靠前搬家工人列表请求异常');
      topMovers.value = [];
    } finally {
      loading.topMovers = false;
    }
  };

  // 获取评分靠前的服务项列表
  const fetchTopServices = async () => {
    loading.topServices = true;
    try {
      const { data: res } = await getTopServicesByRatingApi({
        limit: filterParams.limit,
        minRatingCount: filterParams.minRatingCount
      });
      if (res.code === 1) {
        topServices.value = res.data;
      } else {
        ElMessage.error(res.msg || '获取评分靠前服务项列表失败');
        topServices.value = [];
      }
    } catch (error) {
      console.error('获取评分靠前服务项列表请求失败:', error);
      ElMessage.error('获取评分靠前服务项列表请求异常');
      topServices.value = [];
    } finally {
      loading.topServices = false;
    }
  };

  // 获取所有排名数据
  const fetchAllRankings = () => {
    fetchTopDrivers();
    fetchTopMovers();
    fetchTopServices();
  };

  // 处理筛选条件变化
  const handleFilterChange = () => {
    fetchAllRankings(); // 筛选条件变化时重新获取数据
  };


  // === 生命周期钩子 ===
  onMounted(() => {
    fetchAllRankings(); // 页面加载时获取数据
  });

  // 监听筛选参数变化，重新获取数据
  watch(() => filterParams, handleFilterChange, { deep: true });


  // 在组件卸载前清理 (这里没有图表需要销毁，但保留结构)
  onBeforeUnmount(() => {
    // 清理工作（如果未来有需要）
  });

</script>

<template>
  <div class="data-report-service-quality">
    <div class="page-title-container">
      <h2 class="page-title">服务质量排名</h2>
    </div>

    <el-card class="filter-card mb-4" shadow="never">
      <el-row :gutter="20" align="middle">
        <el-col :span="12">
          <div class="filter-item">
            <label>显示前几名:</label>
            <el-input-number v-model="filterParams.limit" :min="1" :max="50" controls-position="right">
            </el-input-number>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="filter-item">
            <label>最低评价次数:</label>
            <el-input-number v-model="filterParams.minRatingCount" :min="0" controls-position="right">
            </el-input-number>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="20" class="mb-4">
      <el-col :span="8">
        <el-card shadow="hover"> <template #header>
            <div class="card-header">
              <span>高分司机榜</span>
            </div>
          </template>
          <el-skeleton :loading="loading.topDrivers" animated :rows="5">
            <el-table :data="topDrivers" stripe style="width: 100%" max-height="400">
              <el-table-column prop="name" label="姓名"></el-table-column>
              <el-table-column prop="averageRating" label="平均评分" sortable>
                <template #default="{ row }">
                  {{ row.averageRating !== null ? row.averageRating.toFixed(2) : '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="ratingCount" label="评价次数" sortable></el-table-column>
            </el-table>
          </el-skeleton>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover"> <template #header>
            <div class="card-header">
              <span>高分搬家工人榜</span>
            </div>
          </template>
          <el-skeleton :loading="loading.topMovers" animated :rows="5">
            <el-table :data="topMovers" stripe style="width: 100%" max-height="400">
              <el-table-column prop="name" label="姓名"></el-table-column>
              <el-table-column prop="averageRating" label="平均评分" sortable>
                <template #default="{ row }">
                  {{ row.averageRating !== null ? row.averageRating.toFixed(2) : '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="ratingCount" label="评价次数" sortable></el-table-column>
            </el-table>
          </el-skeleton>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover"> <template #header>
            <div class="card-header">
              <span>高分服务项榜</span>
            </div>
          </template>
          <el-skeleton :loading="loading.topServices" animated :rows="5">
            <el-table :data="topServices" stripe style="width: 100%" max-height="400">
              <el-table-column prop="serviceName" width="170px" label="服务项"></el-table-column>
              <el-table-column prop="averageRating" label="平均评分" sortable>
                <template #default="{ row }">
                  {{ row.averageRating !== null ? row.averageRating.toFixed(2) : '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="ratingCount" label="评价次数" sortable></el-table-column>
            </el-table>
          </el-skeleton>
        </el-card>
      </el-col>
    </el-row>

  </div>
</template>

<style lang="less" scoped>
  .data-report-service-quality {
    padding: 20px;
    background-color: #f0f2f5;
    min-height: calc(100vh - 60px - 40px); // 减去 header 和 padding 的高度
    font-family: "Microsoft YaHei", sans-serif; // 使用与核心概览相同的字体

    // 页面标题容器 - 复用 distribution analysis 样式
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
      margin-bottom: 25px; // 复用 distribution analysis 的间距
    }

    // 筛选卡片 - 复用代码二的 .filter-card 样式，并调整内边距使其更矮
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
        }
    }

    // 通用 el-card 样式 - 复用 distribution analysis 样式 (确保覆盖 filter-card 的 box-shadow: none)
    .el-card {
      background-color: #fff;
      // padding: 20px; // 增加内边距
      border-radius: 8-px; // 圆角
      border: 1px solid #ebeef5;
      transition: all 0.3s ease;
      box-sizing: border-box;
      // box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); // 通用卡片阴影，filter-card 上的 hover 会覆盖
    }

    // 卡片头部样式 - 保持 DataReportServiceQuality.vue 原有样式，并微调
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 16px;
      font-weight: bold;
      color: #333;
      // padding-bottom: 10px; // 增加头部和表格之间的间距
      // border-bottom: 1px solid #eee; // 添加分隔线
      // margin-bottom: 15px; // 添加分隔线和表格之间的间距
    }

    // 表格样式 - 保持 DataReportServiceQuality.vue 原有样式
    .el-table {
      font-size: 14px;
      .el-table__cell {
        padding: 8px 0;
      }
    }

    // 以下样式是 DataReportDistributionAnalysis.vue 中与图表相关的，本项目不需要，不复制过来
    /*
                            .data-cards-row { }
                            .chart-card { } // 此类在 Code 1 中未使用，Code 1 使用 .el-card
                            .chart-filter-area { }
                            .chart { }
                            .chart-pie-large { }
                           */
  }
</style>