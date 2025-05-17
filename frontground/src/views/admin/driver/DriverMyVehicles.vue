<script setup>
  // D:\Java\code\RelocateWeb\frontground\src\views\admin\driver\DriverMyVehicles.vue

  import { ref, onMounted } from 'vue';
  import {
    ElMessage,
    ElTable,
    ElTableColumn,
    ElDivider,
    // ElCard, // 不再需要 ElCard
  } from 'element-plus';

  // 导入司机我的车型与车辆相关的 API
  import {
    getDriverTypeAndVehiclesApi,
  } from '@/api/driverApi.js'; // 确保路径正确

  // 数据状态
  const truckTypes = ref([]); // 存储所有车型列表，其中包含对应的车辆信息（如果已指派）
  const loading = ref(false); // 加载状态

  // === 数据获取方法 ===
  /**
   * 获取司机我的车型与车辆列表
   */
  const fetchTypeAndVehicles = async () => {
    loading.value = true;
    truckTypes.value = []; // 清空之前的车型列表

    try {
      const { data: res } = await getDriverTypeAndVehiclesApi(); // 调用 API

      // request.js 拦截器会处理 code !== 1 的错误提示
      if (res.code === 1 && Array.isArray(res.data)) {
        // 直接将返回的数据赋值给 truckTypes
        // 遍历数据，为有 assignedVehicle 的项添加标记，以便高亮
        truckTypes.value = res.data.map(item => {
          if (item.assignedVehicle !== null) {
            return { ...item, isAssignedTruckType: true };
          }
          return item;
        });

      } else {
        // 业务失败，消息已由拦截器弹出，这里只需清空数据
        truckTypes.value = [];
        console.warn('获取司机车型与车辆业务失败:', res.msg);
      }
    } catch (error) {
      // 请求本身发生错误 (网络问题, HTTP错误等)，消息已由拦截器弹出
      console.error('获取司机车型与车辆请求失败:', error);
      truckTypes.value = [];
    } finally {
      loading.value = false;
    }
  };

  // === 辅助方法：判断是否是指派的车型所在的行 ===
  const isAssignedRow = ({ row }) => {
    // 根据我们在 fetchTypeAndVehicles 中添加的 isAssignedTruckType 标记来判断
    return row.isAssignedTruckType ? 'assigned-row' : '';
  };


  // === 组件挂载后，首次加载数据 ===
  onMounted(() => {
    fetchTypeAndVehicles(); // 首次加载司机车型与车辆信息
  });

</script>

<template>
  <div class="page-container">
    <h3>我的车型与车辆</h3>

    <el-table :data="truckTypes" v-loading="loading" border stripe style="width: 100%"
              :row-class-name="isAssignedRow">
      <el-table-column prop="truckTypeName" label="车型名称" align="center" min-width="150"
                       show-overflow-tooltip></el-table-column>
      <el-table-column prop="truckTypeCapacity" label="车型容量" align="center" width="120"
                       show-overflow-tooltip></el-table-column>
      <el-table-column prop="truckTypeBaseFare" label="基础费用" align="center" width="120">
        <template #default="scope">
          ¥{{ scope.row.truckTypeBaseFare ? scope.row.truckTypeBaseFare.toFixed(2) : '0.00' }}
        </template>
      </el-table-column>
      <el-table-column prop="truckTypeDescription" label="车型描述" align="left" min-width="200"
                       show-overflow-tooltip></el-table-column>

      <el-table-column label="指派车辆信息" align="center" min-width="200">
        <template #default="scope">
          <div v-if="scope.row.assignedVehicle">
            车牌号: {{ scope.row.assignedVehicle.licensePlateNumber }} <br />
            品牌: {{ scope.row.assignedVehicle.vehicleBrand }}
          </div>
          <div v-else>
            未配车
          </div>
        </template>
      </el-table-column>

      <el-table-column label="是否已配车" align="center" width="120">
        <template #default="scope">
          <span v-if="scope.row.assignedVehicle">✅ 是</span>
          <span v-else>❌ 否</span>
        </template>
      </el-table-column>

    </el-table>

  </div>
</template>

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

    /* h4 可以根据需要保留或移除 */
    // h4 {
    //   font-size: 18px;
    //   margin-bottom: 15px;
    //   color: #1890ff; /* 分组标题颜色 */
    //   border-bottom: 1px solid #eee;
    //   padding-bottom: 10px;
    // }

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
        // align-items: center; /* 如果内容有多行，可能不需要居中 */
        justify-content: center;
        /* 默认居中对齐 */
        word-break: break-word;
        /* 允许单词中断 */
        white-space: pre-wrap;
        /* 保留空白符，但允许换行 */
        line-height: 1.5; /* 增加行高 */
      }

      // 左对齐特定列
      .el-table-column[prop="truckTypeDescription"] :deep(.cell),
      .el-table-column[label="指派车辆信息"] :deep(.cell) {
        justify-content: flex-start;
        /* 描述和车辆信息左对齐 */
        align-items: flex-start; /* 多行内容顶部对齐 */
      }

      // === 高亮指派车型所在的行 ===
      .assigned-row {
        background-color: #eaf4ff !important;
        /* 浅蓝色背景 */
        font-weight: bold;
        /* 文字加粗 */
        // 您还可以添加其他样式，例如边框颜色等
      }
    }

    /* 调整 ElDivider 的样式 */
    .el-divider {
      margin: 20px 0;
      /* 调整分隔线上下外边距 */
    }
  }
</style>