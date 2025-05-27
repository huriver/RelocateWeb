<template>
  <div class="admin-vehicle-management-container">
    <h3>车辆管理</h3>

    <el-form :model="searchForm" class="search-form">
        <el-form-item label="车牌号">
          <el-input v-model="searchForm.licensePlateNumber" placeholder="请输入车牌号关键词"
                    clearable></el-input>
        </el-form-item>
        <el-form-item label="司机姓名">
          <el-input v-model="searchForm.driverName" placeholder="请输入司机姓名关键词" clearable></el-input>
        </el-form-item>
        <el-form-item label="车辆类型">
          <el-select v-model="searchForm.truckTypeId" placeholder="请选择车辆类型" clearable>
            <el-option v-for="item in truckTypes" :key="item.id" :label="item.typeName"
                       :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="车辆品牌">
          <el-input v-model="searchForm.vehicleBrand" placeholder="请输入车辆品牌关键词" clearable></el-input>
        </el-form-item>

        <el-form-item label="创建日期">
          <el-date-picker v-model="searchForm.createTimeRange" type="daterange" range-separator="至"
                          start-placeholder="开始日期" end-placeholder="结束日期"
                          value-format="YYYY-MM-DD HH:mm:ss" :clearable="true"
                          :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"></el-date-picker>
        </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearchForm">重置</el-button>
        <el-button type="success" @click="handleAddVehicle"
                   class="add-button-separate">新增车辆</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" align="center"></el-table-column>
      <el-table-column prop="licensePlateNumber" label="车牌号" align="center"></el-table-column>
      <el-table-column prop="vehicleBrand" label="车辆品牌" align="left"
                       show-overflow-tooltip></el-table-column>
      <el-table-column prop="truckTypeName" label="车辆类型" align="left"
                       show-overflow-tooltip></el-table-column>
      <el-table-column prop="driverName" label="司机姓名" align="center"></el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="180" align="center"></el-table-column>
      <el-table-column prop="updateUserName" label="更新人" align="center"></el-table-column>

      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="showDetails(scope.row)"
                     class="detail-button">详情</el-button>
          <el-button size="small" type="primary" style="margin-left: 10px"
                     @click="handleEditVehicle(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" style="margin-left: 10px"
                     @click="handleDeleteVehicle(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                   :current-page="pagination.page" :page-sizes="[10, 20, 50, 100]"
                   :page-size="pagination.pageSize" layout="total, sizes, prev, pager, next, jumper"
                   :total="pagination.total" background class="pagination"></el-pagination>

    <el-dialog v-model="editDialogVisible" :title="editDialogTitle" width="600px"
               @close="resetEditForm">
      <el-form :model="editForm" :rules="editFormRules" ref="editFormRef" label-width="100px">
        <el-form-item label="司机" prop="driverId">
          <el-select v-model="editForm.driverId" filterable remote reserve-keyword
                     placeholder="请输入司机姓名关键词搜索" :remote-method="remoteSearchDrivers"
                     :loading="driverSearching" style="width: 100%" clearable>
            <el-option v-for="item in driverOptions" :key="item.id" :label="item.name"
                       :value="item.id"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="车辆类型" prop="truckTypeId" style="margin-top: 20px">
          <el-select v-model="editForm.truckTypeId" placeholder="请选择车辆类型" style="width: 100%">
            <el-option v-for="item in dialogTruckTypes" :key="item.id" :label="item.typeName"
                       :value="item.id"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="车牌号" prop="licensePlateNumber" style="margin-top: 20px">
          <el-input v-model="editForm.licensePlateNumber" placeholder="请输入车牌号"></el-input>
        </el-form-item>

        <el-form-item label="车辆品牌" prop="vehicleBrand" style="margin-top: 20px">
          <el-input v-model="editForm.vehicleBrand" placeholder="请输入车辆品牌"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="车辆详情" width="600px">
      <el-form label-width="100px" v-if="currentRowDetails">
        <el-form-item label="ID">{{ currentRowDetails.id }}</el-form-item>
        <el-form-item label="车牌号">{{ currentRowDetails.licensePlateNumber }}</el-form-item>
        <el-form-item label="车辆品牌">{{ currentRowDetails.vehicleBrand }}</el-form-item>
        <el-form-item label="车辆类型">{{ currentRowDetails.truckTypeName }}</el-form-item>
        <el-form-item label="司机姓名">{{ currentRowDetails.driverName }}</el-form-item>
        <el-form-item label="创建时间">{{ currentRowDetails.createTime }}</el-form-item>
        <el-form-item label="创建人">{{ currentRowDetails.createUserName }}</el-form-item>
        <el-form-item label="更新时间">{{ currentRowDetails.updateTime }}</el-form-item>
        <el-form-item label="更新人">{{ currentRowDetails.updateUserName }}</el-form-item>
      </el-form>
      <div v-else>正在加载车辆详情...</div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
  import { ref, onMounted, computed, watch } from 'vue';
  import {
    ElMessage,
    ElMessageBox,
    ElDialog,
    ElForm,
    ElFormItem,
    ElInput,
    ElSelect,
    ElDatePicker,
    ElButton,
    ElTable,
    ElTableColumn,
    ElPagination,
    ElOption,
  } from 'element-plus';

  // 导入后台车辆 API
  import {
    getBackVehicleListPageApi,
    getBackVehicleDetailApi,
    addBackVehicleApi,
    updateBackVehicleApi,
    deleteBackVehicleApi,
  } from '@/api/vehicleApi.js';

  // 导入后台车辆类型 API，用于选择车辆类型
  import { getBackTruckTypeListApi } from '@/api/truckTypeApi.js';

  // 导入后台司机 API
  import { getBackDriverListByNameApi } from '@/api/driverApi.js';

  // 导入司机与货车类型关联 API
  import { getBackDriverTruckTypeDetailApi } from '@/api/driverTruckTypeApi.js'; // <-- 导入新的 API

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
    licensePlateNumber: '', // 车牌号关键词
    driverName: '', // 司机姓名关键词 (文本框)
    truckTypeId: null, // 车辆类型ID (下拉选择)
    vehicleBrand: '', // 车辆品牌关键词
    createTimeRange: null, // 创建时间范围 [start, end]
  });

  // 货车类型列表，用于搜索下拉（完整列表）
  const truckTypes = ref([]);

  // 货车类型列表，用于新增/修改弹窗中的下拉（根据司机过滤）
  const dialogTruckTypes = ref([]); // <-- 新增变量

  // 新增/修改弹窗状态和表单数据
  const editDialogVisible = ref(false); // 控制新增/修改弹窗显示
  const editDialogTitle = computed(() => (editForm.value.id ? '修改车辆信息' : '新增车辆')); // 弹窗标题
  const editFormRef = ref(null); // 新增/修改表单的引用
  const editForm = ref({
    id: null, // ID 为 null 表示新增
    driverId: null, // 司机ID (number)
    truckTypeId: null, // 车辆类型ID (number)
    licensePlateNumber: '', // 车牌号
    vehicleBrand: '', // 车辆品牌
  });

  // 新增/修改表单校验规则
  const editFormRules = ref({
    driverId: [{ required: true, message: '请选择司机', trigger: 'change' }],
    truckTypeId: [{ required: true, message: '请选择车辆类型', trigger: 'change' }],
    licensePlateNumber: [{ required: true, message: '请输入车牌号', trigger: 'blur' }],
    vehicleBrand: [{ required: true, message: '请输入车辆品牌', trigger: 'blur' }],
  });

  // 司机搜索相关数据
  const driverOptions = ref([]); // 司机选项列表
  const driverSearching = ref(false); // 司机搜索加载状态

  // 详情弹窗状态和当前选中行数据
  const detailDialogVisible = ref(false); // 控制详情弹窗显示
  const currentRowDetails = ref(null); // 存储当前查看详情的行数据

  // === 监听 driverId 变化，根据司机ID查询关联的货车类型 ===
  watch(() => editForm.value.driverId, async (newDriverId, oldDriverId) => {
    // 当 driverId 改变时触发
    if (newDriverId !== oldDriverId) {
      // 清空当前选中的车辆类型
      editForm.value.truckTypeId = null;

      if (newDriverId !== null && newDriverId !== undefined) {
        // 如果选中了司机，则根据司机ID查询关联的货车类型
        try {
          const { data: res } = await getBackDriverTruckTypeDetailApi(newDriverId);
          // 根据后台返回的实际数据结构进行处理
          if (res.code === 1 && res.data && res.data.truckTypeSimpleVOList && res.data.truckTypeSimpleVOList.length > 0) {
            // 直接使用返回的列表，并将其字段映射为 { id, typeName }
            dialogTruckTypes.value = res.data.truckTypeSimpleVOList.map(item => ({
              id: item.truckTypeId,
              typeName: item.truckTypeName
            }));

            // 检查当前编辑的车辆类型是否在新的列表中，如果不在则清空
            if (editForm.value.id && editForm.value.truckTypeId) {
              const currentTruckTypeExists = dialogTruckTypes.value.some(type => type.id === editForm.value.truckTypeId);
              if (!currentTruckTypeExists) {
                editForm.value.truckTypeId = null;
              }
            }

          } else {
            // 司机没有关联任何货车类型，或者接口返回数据为空
            dialogTruckTypes.value = [];
            console.warn(`司机ID ${newDriverId} 没有关联任何货车类型或查询结果为空`);
          }
        } catch (error) {
          // 请求失败，request.js 拦截器已处理 ElMessage
          console.error(`查询司机 ${newDriverId} 关联货车类型请求失败:`, error);
          dialogTruckTypes.value = []; // 查询失败则清空车辆类型选项
        }
      } else {
        // 如果 driverId 被清空，则显示所有货车类型选项 (用于新增时的初始状态或编辑时清除司机)
        dialogTruckTypes.value = [...truckTypes.value];
      }
    }
  }, { immediate: true }); // immediate: true 确保在组件加载且弹窗打开时，如果 editForm.driverId 有初始值，也能触发监听

  // === 数据获取方法 ===
  const fetchVehicleList = async () => {
    loading.value = true;
    try {
      // 准备请求参数
      const params = {
        page: pagination.value.page,
        pageSize: pagination.value.pageSize,
        licensePlateNumber: searchForm.value.licensePlateNumber || undefined,
        driverName: searchForm.value.driverName || undefined, // 司机姓名作为文本框搜索参数
        truckTypeId: searchForm.value.truckTypeId || undefined, // 车辆类型ID作为下拉选择参数
        vehicleBrand: searchForm.value.vehicleBrand || undefined,
        createTimeStart: searchForm.value.createTimeRange
          ? searchForm.value.createTimeRange[0]
          : undefined,
        createTimeEnd: searchForm.value.createTimeRange
          ? searchForm.value.createTimeRange[1]
          : undefined,
      };

      const { data: res } = await getBackVehicleListPageApi(params); // 调用后台车辆分页 API

      if (res.code === 1) {
        tableData.value = res.data.records;
        pagination.value.total = res.data.total;
      } else {
        // 业务错误，使用 request.js 拦截器处理 ElMessage
        tableData.value = [];
        pagination.value.total = 0;
      }
    } catch (error) {
      // 请求失败，使用 request.js 拦截器处理 ElMessage
      console.error('获取车辆列表请求失败:', error);
      tableData.value = [];
      pagination.value.total = 0;
    } finally {
      loading.value = false;
    }
  };

  // 获取货车类型列表 (用于搜索下拉和填充初始弹窗下拉)
  const fetchTruckTypes = async () => {
    try {
      const { data: res } = await getBackTruckTypeListApi();
      if (res.code === 1 && res.data) {
        truckTypes.value = res.data;
        // 首次加载时，将所有货车类型赋值给弹窗用的列表
        dialogTruckTypes.value = [...truckTypes.value];
      } else {
        // 业务错误，使用 request.js 拦截器处理 ElMessage
        console.warn('获取货车类型列表业务失败:', res.msg);
      }
    } catch (error) {
      // 请求失败，使用 request.js 拦截器已处理 ElMessage
      console.error('获取货车类型列表请求失败:', error);
    }
  };

  // === 司机远程搜索方法 ===
  const remoteSearchDrivers = async (query) => {
    if (query) {
      driverSearching.value = true;
      try {
        const { data: res } = await getBackDriverListByNameApi(query); // 调用司机搜索 API
        if (res.code === 1 && res.data) {
          // 过滤掉不需要的字段，只保留 id 和 name
          driverOptions.value = res.data.map(driver => ({ id: driver.id, name: driver.name }));

          // === 处理编辑时已选中的司机 ===
          // 如果是编辑模式且当前 editForm.driverId 有值，
          // 检查当前已选中的司机是否在搜索结果中，如果不在，则手动添加到选项列表的顶部
          // 这样即使当前司机不在搜索结果中也能显示
          if (editForm.value.id && editForm.value.driverId) {
            const currentDriverInOptions = driverOptions.value.find(
              option => option.id === editForm.value.driverId
            );
            if (!currentDriverInOptions) {
              // 如果不在，查找表格数据或详情数据中的司机信息并添加到选项
              const selectedVehicle = tableData.value.find(v => v.id === editForm.value.id);
              if (selectedVehicle && selectedVehicle.driverId === editForm.value.driverId) {
                driverOptions.value.unshift({
                  id: selectedVehicle.driverId,
                  name: selectedVehicle.driverName,
                });
              }
            }
          }
          // ===========================

        } else {
          driverOptions.value = []; // 搜索失败则清空选项
          console.warn('司机搜索业务失败:', res.msg);
        }
      } catch (error) {
        driverOptions.value = []; // 请求失败则清空选项
        console.error('司机搜索请求失败:', error);
      } finally {
        driverSearching.value = false;
      }
    } else {
      // 如果查询字符串为空，清空选项列表，但不清空 editForm.driverId，因为可能是编辑模式且已经选中
      // editDialogVisible 的 watcher 会处理清空的情况
      driverOptions.value = [];
    }
  };

  // === 搜索相关方法 ===
  const handleSearch = () => {
    pagination.value.page = 1; // 从第一页开始搜索
    fetchVehicleList();
  };

  const resetSearchForm = () => {
    searchForm.value = {
      licensePlateNumber: '',
      driverName: '',
      truckTypeId: null,
      vehicleBrand: '',
      createTimeRange: null,
    };
    handleSearch(); // 重置后立即执行查询，回到第一页
  };

  // === 分页相关方法 ===
  const handleSizeChange = (val) => {
    pagination.value.pageSize = val;
    pagination.value.page = 1; // 切换每页大小时回到第一页
    fetchVehicleList();
  };

  const handleCurrentChange = (val) => {
    pagination.value.page = val;
    fetchVehicleList();
  };

  // === 新增/修改车辆相关方法 ===

  // 打开新增车辆弹窗
  const handleAddVehicle = () => {
    resetEditForm(); // 打开前先重置表单，确保是新增模式
    // 重置表单时已经将 dialogTruckTypes 设置为全部货车类型
    driverOptions.value = []; // 清空司机选项，防止显示上次编辑的司机或搜索结果
    editDialogVisible.value = true;
  };

  // 打开修改车辆弹窗 - 需要获取详情以填充ID和名称以在下拉框中显示
  const handleEditVehicle = async (row) => {
    // 获取详情以获取 driverId 和 truckTypeId
    try {
      const { data: res } = await getBackVehicleDetailApi(row.id);
      if (res.code === 1 && res.data) {
        // 使用详情数据填充表单
        editForm.value = {
          id: res.data.id,
          driverId: res.data.driverId,
          truckTypeId: res.data.truckTypeId,
          licensePlateNumber: res.data.licensePlateNumber,
          vehicleBrand: res.data.vehicleBrand,
        };

        // === 在编辑时，手动将当前司机添加到下拉选项中，以便正确显示 ===
        // 这样即使当前司机不在搜索结果中也能显示
        if (res.data.driverId && res.data.driverName) {
          driverOptions.value = [{
            id: res.data.driverId,
            name: res.data.driverName
          }];
        } else {
          driverOptions.value = []; // 如果没有司机信息，清空选项
        }
        // ========================================================

        // 弹窗打开后，editForm.driverId 的变化会触发 watch，自动更新 dialogTruckTypes

        editDialogVisible.value = true;
      } else {
        // 业务错误，request.js 拦截器已处理 ElMessage
        console.warn('获取车辆详情业务失败:', res.msg);
      }
    } catch (error) {
      // 请求失败，request.js 拦截器已处理 ElMessage
      console.error('获取车辆详情请求失败:', error);
    }
  };


  // 提交新增/修改表单
  const submitEditForm = async () => {
    // 进行表单校验
    const valid = await editFormRef.value.validate();
    if (!valid) {
      ElMessage.error('请填写完整且符合要求的表单项');
      return;
    }

    const isAdding = !editForm.value.id; // 根据是否有 ID 判断模式

    try {
      const apiCall = isAdding ? addBackVehicleApi : updateBackVehicleApi;
      // 调用新增或修改 API
      const { data: res } = await apiCall(editForm.value);

      if (res.code === 1) {
        ElMessage.success(isAdding ? '车辆添加成功！' : '车辆修改成功！');
        editDialogVisible.value = false; // 关闭弹窗
        // resetEditForm(); // submit 成功后不再需要手动调用 resetEditForm，@close 会触发
        fetchVehicleList(); // 刷新列表
      } else {
        // 后端返回业务错误 (code !== 1)，request.js 拦截器已处理 ElMessage
        console.warn('提交车辆表单业务失败:', res.msg);
        // 保持弹窗打开，以便用户根据后端错误信息修改输入
      }
    } catch (error) {
      // 请求本身发生错误，request.js 拦截器已处理 ElMessage
      console.error('提交车辆请求失败:', error);
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
      driverId: null,
      truckTypeId: null,
      licensePlateNumber: '',
      vehicleBrand: '',
    };
    driverOptions.value = []; // 清空司机选项列表

    // 重置时，将弹窗的货车类型选项设置为全部
    dialogTruckTypes.value = [...truckTypes.value];
  };

  // === 删除车辆相关方法 ===
  const handleDeleteVehicle = (row) => {
    ElMessageBox.confirm(`确定要删除车牌号为《${row.licensePlateNumber}》的车辆吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        // 在 .then 块中处理异步操作结果
        try {
          const { data: res } = await deleteBackVehicleApi(row.id); // 调用删除 API

          if (res.code === 1) {
            ElMessage.success('车辆删除成功！');
            // 删除成功后判断是否需要留在当前页或跳转到上一页
            if (tableData.value.length === 1 && pagination.value.page > 1) {
              pagination.value.page--;
            }
            fetchVehicleList(); // 刷新列表
          } else {
            // 业务失败，request.js 拦截器已处理 ElMessage
            console.warn('车辆删除业务失败:', res.msg);
          }
        } catch (error) {
          // 请求失败，request.js 拦截器已处理 ElMessage
          console.error('车辆删除请求失败:', error);
          // ElMessage.error('删除车辆失败，请稍后再试'); // request.js 已处理
        }
      })
      .catch(() => {
        // 这是 ElMessageBox.confirm 的 catch，处理用户点击取消
        ElMessage.info('已取消删除');
      });
  };

  // === 查看车辆详情相关方法 ===
  const showDetails = async (row) => {
    detailDialogVisible.value = true;
    // 在加载新的详情数据前，清空旧的数据，以免显示陈旧信息
    currentRowDetails.value = null;

    try {
      // 调用后台车辆详情 API 根据ID获取完整的详情数据
      const { data: res } = await getBackVehicleDetailApi(row.id);

      if (res.code === 1 && res.data) {
        currentRowDetails.value = res.data; // 使用详情接口返回的数据更新 currentRowDetails
      } else {
        // 业务失败，request.js 拦截器已处理 ElMessage
        console.warn('获取车辆详情业务失败:', res.msg);
        detailDialogVisible.value = false; // 获取失败则关闭弹窗
      }
    } catch (error) {
      // 请求失败，request.js 拦截器已处理 ElMessage
      console.error('获取车辆详情请求失败:', error);
      detailDialogVisible.value = false; // 请求失败则关闭弹窗
    }
  };

  // === 组件挂载后，首次加载数据 ===
  onMounted(() => {
    fetchVehicleList(); // 获取车辆列表
    fetchTruckTypes(); // 获取货车类型列表用于下拉选择 (会同时初始化 dialogTruckTypes)
  });
</script>

<style scoped lang="less">
  /* 样式复用自 AdminServiceTypeConfig.vue，仅修改容器类名 */
  .admin-vehicle-management-container {
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
              :deep(.el-input-number__input-wrap), // Add this for number inputs
              :deep(.el-date-editor .el-input__wrapper) {
          border-color: #ccc !important;
          border-width: 1px !important;
          border-style: solid !important;
          box-shadow: none !important;
        }
        :deep(.el-input__wrapper.is-focus),
              :deep(.el-select__wrapper.is-focus),
              :deep(.el-input-number__input-wrap.is-focus), // Add this for number inputs
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
          // text-align: left; // Keep default text-align for number input
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
        :deep(.el-select),
        :deep(.el-input-number) {
          /* Add input-number here */
          width: 200px; /* 基础宽度 */
        }

        :deep(.el-date-editor--daterange) {
          width: 240px; /* 日期范围选择器宽度 */
        }
        // Specific width for standalone input-number in dialog
        .el-dialog :deep(.el-input-number) {
          width: 100%; /* Make standalone input-number fill container in dialog */
        }
      }

      .el-form-item {
        margin-bottom: 0;
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
          :deep(.el-select),
          :deep(.el-input-number) {
            /* Add input-number here */
            width: 100% !important; // input/select/input-number 填充父级内容区域
            min-width: auto; // 移除通用最小宽度限制
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
      .el-table-column[prop="vehicleBrand"],
      .el-table-column[prop="truckTypeName"] :deep(.cell) {
        justify-content: flex-start; /* 品牌、类型左对齐 */
      }
      .el-table-column[prop="licensePlateNumber"],
      .el-table-column[prop="driverName"] :deep(.cell) {
        justify-content: center; /* 车牌号、司机姓名居中对齐 */
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