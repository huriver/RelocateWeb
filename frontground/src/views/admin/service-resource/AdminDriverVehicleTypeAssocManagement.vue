<template>
  <div class="admin-driver-truck-type-assoc-container">
    <h3>司机货车类型关联管理</h3>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <div class="input-items-group evenly-distributed-group">
        <el-form-item label="司机姓名">
          <el-input v-model="searchForm.driverName" placeholder="请输入司机姓名关键词" clearable></el-input>
        </el-form-item>
        <el-form-item label="货车类型">
          <el-select v-model="searchForm.truckTypeId" placeholder="请选择货车类型" clearable>
            <el-option label="全部货车类型" :value="undefined"></el-option>
            <el-option v-for="item in truckTypeOptions" :key="item.id" :label="item.typeName"
                       :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      </div>

      <el-form-item class="button-group">
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearchForm">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="driverName" label="司机姓名" align="left"
                       show-overflow-tooltip></el-table-column>
      <el-table-column label="关联货车类型" align="left" show-overflow-tooltip>
        <template #default="scope">
          <template
                    v-if="scope.row.truckTypeSimpleVOList && scope.row.truckTypeSimpleVOList.length > 0">
            <el-tag v-for="(truckType, index) in scope.row.truckTypeSimpleVOList"
                    :key="truckType.truckTypeId" size="small"
                    style="margin-right: 5px; margin-bottom: 5px">
              {{ truckType.truckTypeName }}
            </el-tag>
          </template>
          <span v-else>暂无关联货车类型</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="showDetails(scope.row)"
                     class="detail-button">详情</el-button>
          <el-button size="small" type="primary" style="margin-left: 10px"
                     @click="handleEditAssociation(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" style="margin-left: 10px"
                     @click="handleDeleteAssociation(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                   :current-page="pagination.page" :page-sizes="[10, 20, 50, 100]"
                   :page-size="pagination.pageSize" layout="total, sizes, prev, pager, next, jumper"
                   :total="pagination.total" background class="pagination"></el-pagination>

    <el-dialog v-model="editDialogVisible" :title="editDialogTitle" width="600px"
               @close="resetEditForm">
      <el-form :model="editForm" :rules="editFormRules" ref="editFormRef" label-width="120px">
        <el-form-item label="选择司机" prop="driverId">
          <el-select v-model="editForm.driverId" filterable remote reserve-keyword
                     placeholder="请输入司机姓名搜索并选择" :remote-method="remoteSearchDrivers"
                     :loading="driverSearching" disabled style="width: 100%" clearable
                     @change="handleDriverChange">
            <el-option v-for="item in driverOptions" :key="item.driverId"
                       :label="`${item.driverName} (ID: ${item.driverId})`"
                       :value="item.driverId"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="关联货车类型" prop="truckTypeIds">
          <el-select v-model="editForm.truckTypeIds" multiple filterable placeholder="请选择关联的货车类型"
                     style="width: 100%" clearable>
            <el-option v-for="item in truckTypeOptions" :key="item.id" :label="item.typeName"
                       :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="司机车型关联详情" width="600px">
      <el-form label-width="120px" v-if="currentRowDetails">
        <el-form-item label="司机ID">{{ currentRowDetails.driverId }}</el-form-item>
        <el-form-item label="司机姓名">{{ currentRowDetails.driverName }}</el-form-item>
        <el-form-item label="关联货车类型">
          <template v-if="
							currentRowDetails.truckTypeSimpleVOList &&
							currentRowDetails.truckTypeSimpleVOList.length > 0
						">
            <el-tag v-for="(truckType, index) in currentRowDetails.truckTypeSimpleVOList"
                    :key="truckType.truckTypeId" size="small"
                    style="margin-right: 5px; margin-bottom: 5px">
              {{ truckType.truckTypeName }}
            </el-tag>
          </template>
          <span v-else>暂无关联货车类型</span>
        </el-form-item>
      </el-form>
      <div v-else>正在加载详情...</div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
  import { ref, onMounted, computed } from 'vue';
  import {
    ElMessage,
    ElMessageBox,
    ElDialog,
    ElForm,
    ElFormItem,
    ElTag,
    ElInput,
    ElSelect,
    ElButton,
    ElTable,
    ElTableColumn,
    ElPagination,
    ElOption,
  } from 'element-plus';

  // 导入司机与货车类型关联 API
  import {
    getBackDriverTruckTypeListPageApi,
    getBackDriverTruckTypeDetailApi,
    // Removed addBackDriverTruckTypeApi
    updateBackDriverTruckTypeApi, // Will be used for edit (including adding initial associations)
    deleteBackDriverTruckTypeApi,
  } from '@/api/driverTruckTypeApi.js';

  // 导入货车类型 API (用于获取所有类型列表)
  import { getBackTruckTypeListApi } from '@/api/truckTypeApi.js';

  // 导入司机 API (用于在编辑弹窗中显示当前司机信息)
  import { getBackDriverListByNameApi } from '@/api/driverApi.js'; // 虽然用于搜索，但这里主要用来在编辑时填充driverOptions

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
    driverName: '', // 司机姓名关键词
    truckTypeId: undefined, // 货车类型ID (用于下拉选择过滤)
  });

  // isEditMode 标志，现在主要用于控制 UI 状态（弹窗标题，司机选择框是否禁用）
  // 由于只有编辑入口，此标志在弹窗打开时总是 true
  const isEditMode = ref(false);

  // 编辑弹窗状态和表单数据
  const editDialogVisible = ref(false); // 控制编辑弹窗显示
  // 弹窗标题固定为“修改司机车型关联”
  const editDialogTitle = computed(() => '修改司机车型关联'); // *** 修改点：固定标题 ***

  const editFormRef = ref(null); // 编辑表单的引用
  const editForm = ref({
    driverId: null, // 司机ID
    truckTypeIds: [], // 关联的货车类型ID数组
  });

  // 编辑表单校验规则
  const editFormRules = ref({
    // *** 修改点：移除 driverId 的必选校验 ***
    // driverId: [{ required: !isEditMode.value, message: '请选择司机', trigger: 'change' }], // 已移除
    // 关联货车类型是必选的，并且数组长度至少为 1
    truckTypeIds: [
      {
        type: 'array',
        required: true, // 数组本身是必选的
        message: '请选择关联的货车类型', // 更通用的消息
        trigger: 'change',
      },
      {
        // 自定义校验器：确保数组长度大于 0
        validator: (rule, value, callback) => {
          if (!value || value.length === 0) {
            callback(new Error('必须选择至少一种货车类型'));
          } else {
            callback();
          }
        },
        trigger: 'change',
      },
    ],
  });


  // 司机远程搜索相关数据 (主要用于在编辑弹窗中显示当前司机)
  const driverOptions = ref([]); // 远程搜索结果的司机列表
  const driverSearching = ref(false); // 司机远程搜索加载状态

  // 获取所有货车类型列表 (用于编辑弹窗中的多选下拉框)
  const truckTypeOptions = ref([]); // 所有货车类型列表

  // 详情弹窗状态和当前选中行数据
  const detailDialogVisible = ref(false); // 控制详情弹窗显示
  const currentRowDetails = ref(null); // 存储当前查看详情的行数据

  // === 数据获取方法 ===
  const fetchDriverTruckTypeList = async () => {
    loading.value = true;
    try {
      // 准备请求参数
      const params = {
        page: pagination.value.page,
        pageSize: pagination.value.pageSize,
        driverName: searchForm.value.driverName,
        truckTypeId: searchForm.value.truckTypeId,
      };

      const { data: res } = await getBackDriverTruckTypeListPageApi(params);

      if (res.code === 1) {
        tableData.value = res.data.records;
        pagination.value.total = res.data.total;
      } else {
        ElMessage.error(res.msg || '获取司机车型关联列表失败');
        tableData.value = [];
        pagination.value.total = 0;
      }
    } catch (error) {
      console.error('获取司机车型关联列表请求失败:', error);
      ElMessage.error('获取司机车型关联列表失败，请稍后再试');
      tableData.value = [];
      pagination.value.total = 0;
    } finally {
      loading.value = false;
    }
  };

  // 获取所有货车类型列表 (用于编辑弹窗中的多选下拉框)
  const fetchAllTruckTypes = async () => {
    try {
      const { data: res } = await getBackTruckTypeListApi();
      if (res.code === 1) {
        truckTypeOptions.value = res.data;
      } else {
        ElMessage.error(res.msg || '获取货车类型列表失败');
        truckTypeOptions.value = [];
      }
    } catch (error) {
      console.error('获取货车类型列表请求失败:', error);
      ElMessage.error('获取货车类型列表失败，请稍后再试');
      truckTypeOptions.value = [];
    }
  };

  // 司机远程搜索方法 (现在主要用于在编辑弹窗中显示当前司机信息)
  const remoteSearchDrivers = async (query) => {
    // 在编辑模式下，司机选择框是禁用的，此方法理论上不会被用户主动触发进行搜索。
    // 但为了填充已选司机信息，可能在 handleEditAssociation 中被调用一次。
    // 如果query为空，或者处于编辑模式，通常我们不执行搜索。
    if (isEditMode.value || !query) { // *** 修改点：在编辑模式或query为空时，不执行搜索 ***
      driverOptions.value = []; // 清空选项
      driverSearching.value = false;
      return;
    }

    driverSearching.value = true;
    try {
      const { data: res } = await getBackDriverListByNameApi(query);
      if (res.code === 1 && Array.isArray(res.data)) {
        driverOptions.value = res.data.map((item) => ({
          driverId: item.id,
          driverName: item.name,
        }));
      } else {
        ElMessage.error(res.msg || '搜索司机失败');
        driverOptions.value = [];
      }
    } catch (error) {
      console.error('搜索司机请求失败:', error);
      ElMessage.error('搜索司机失败，请稍后再试');
      driverOptions.value = [];
    } finally {
      driverSearching.value = false;
    }
  };


  // 当司机选择框值改变时触发 (现在此方法在编辑模式下，选择框被禁用，理论上不会触发)
  const handleDriverChange = (driverId) => {
    // 此方法在移除了新增入口后，实际不会被用户操作触发，可以考虑移除或保留作为冗余。
    console.log("Driver selected (should not happen in current UI if disabled):", driverId);
  };

  // === 搜索相关方法 ===
  const handleSearch = () => {
    pagination.value.page = 1; // 从第一页开始搜索
    fetchDriverTruckTypeList();
  };

  const resetSearchForm = () => {
    searchForm.value = {
      driverName: '',
      truckTypeId: undefined, // 重置为 undefined
    };
    handleSearch(); // 重置后立即执行查询，回到第一页
  };

  // === 分页相关方法 ===
  const handleSizeChange = (val) => {
    pagination.value.pageSize = val;
    pagination.value.page = 1; // 切换每页大小时回到第一页
    fetchDriverTruckTypeList();
  };

  const handleCurrentChange = (val) => {
    pagination.value.page = val;
    fetchDriverTruckTypeList();
  };

  // === 编辑关联相关方法 (现在作为唯一的管理入口) ===

  // 打开修改关联弹窗 (现在此函数处理所有关联管理入口)
  const handleEditAssociation = async (row) => {
    // 加载当前关联的详情数据
    try {
      const { data: res } = await getBackDriverTruckTypeDetailApi(row.driverId);
      if (res.code === 1 && res.data) {
        // Populate the form for editing
        editForm.value = {
          driverId: res.data.driverId,
          // Map the detailed truckTypeSimpleVOList to an array of truckTypeIds
          truckTypeIds: res.data.truckTypeSimpleVOList
            ? res.data.truckTypeSimpleVOList.map((item) => item.truckTypeId)
            : [],
        };
        isEditMode.value = true; // 设置为修改模式 (现在总是 true)
        editDialogVisible.value = true;
        // 在编辑模式下，司机选择框是禁用的。
        // 同时需要将当前的司机添加到 driverOptions，以便 el-select 正确显示已选中的值
        // 模拟一个选项，以便 el-select 能够显示 driverId 对应的 label
        driverOptions.value = [{ driverId: res.data.driverId, driverName: res.data.driverName }];
      } else {
        ElMessage.error(res.msg || '获取司机车型关联详情失败，无法编辑');
      }
    } catch (error) {
      console.error('获取司机车型关联详情请求失败:', error);
      ElMessage.error('获取司机车型关联详情失败，请稍后再试');
    }
  };

  // 提交编辑表单
  const submitEditForm = async () => {
    // 进行表单校验
    const valid = await editFormRef.value.validate();
    if (!valid) {
      ElMessage.error('请填写完整且符合要求的表单项');
      return;
    }

    // 由于现在只有编辑入口，driverId 总是存在，truckTypeIds 的长度校验已在 rules 中
    // 如果 truckTypeIds 为空，后端会清空该司机的关联，这取决于您的业务规则是否允许通过编辑来清空所有关联。
    // 如果不允许清空，需要在这里添加额外校验 if (editForm.value.truckTypeIds.length === 0) {...}

    try {
      // 统一调用更新 API
      const { data: res } = await updateBackDriverTruckTypeApi(editForm.value);

      if (res.code === 1) {
        // 后端返回成功，现在只显示修改成功消息
        ElMessage.success('司机车型关联修改成功！'); // *** 修改点：固定成功消息 ***
        editDialogVisible.value = false; // 关闭弹窗
        resetEditForm(); // 重置表单数据和状态
        fetchDriverTruckTypeList(); // 刷新列表
      } else {
        // 后端返回业务错误 (code !== 1)，现在只显示修改失败消息
        ElMessage.error(res.msg || '司机车型关联修改失败'); // *** 修改点：固定失败消息 ***
      }
    } catch (error) {
      // 请求本身发生错误
      console.error('提交司机车型关联请求失败:', error);
      const specificErrorMessage = error.message || (error.response && error.response.data && error.response.data.msg);
      // 直接显示修改失败消息
      const errorMessageToDisplay =
        specificErrorMessage && specificErrorMessage !== '未知错误' && specificErrorMessage !== 'Internal Server Error'
          ? `操作失败: ${specificErrorMessage}`
          : '修改司机车型关联失败，请稍后再试'; // *** 修改点：固定错误消息 ***

      ElMessage.error(errorMessageToDisplay);
    }
  };

  // 重置编辑表单 (在弹窗关闭时调用)
  const resetEditForm = () => {
    if (editFormRef.value) {
      editFormRef.value.resetFields();
    }
    // 手动确保字段值正确清空
    editForm.value = {
      driverId: null,
      truckTypeIds: [],
    };
    // 清空司机搜索选项
    driverOptions.value = [];
    driverSearching.value = false;
    // 重置 isEditMode 标志 (好习惯，尽管现在总是通过编辑入口打开)
    isEditMode.value = false;
  };

  // === 删除关联相关方法 ===
  const handleDeleteAssociation = (row) => {
    ElMessageBox.confirm(`确定要删除司机《${row.driverName}》的所有车型关联吗？此操作将清空该司机的可驾驶车型资质。`, '提示', { // *** 修改点：调整提示文本更清晰 ***
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        try {
          // 调用删除 API，传递司机ID
          const { data: res } = await deleteBackDriverTruckTypeApi(row.driverId);

          if (res.code === 1) {
            ElMessage.success('司机车型关联删除成功！');
            // 删除成功后判断是否需要留在当前页或跳转到上一页
            if (tableData.value.length === 1 && pagination.value.page > 1) {
              pagination.value.page--;
            }
            fetchDriverTruckTypeList(); // 刷新列表
          } else {
            console.warn('司机车型关联删除业务失败:', res.msg);
            // Request interceptor handles this, so no need for ElMessage.error here unless interceptor doesn't cover it
          }
        } catch (error) {
          console.error('司机车型关联删除请求失败:', error);
          const errorMessage = error.message || (error.response && error.response.data && error.response.data.msg);
          if (!errorMessage || (errorMessage !== '未知错误' && errorMessage !== 'Internal Server Error' /* Add other specific error messages if needed */)) {
            ElMessage.error('删除司机车型关联失败，请稍后再试');
          }
        }
      })
      .catch(() => {
        // 这是 ElMessageBox.confirm 的 catch，处理用户点击取消
        ElMessage.info('已取消删除');
      });
  };

  // === 查看关联详情相关方法 ===
  const showDetails = async (row) => {
    detailDialogVisible.value = true;
    // 在加载新的详情数据前，清空旧的数据，以免显示陈旧信息
    currentRowDetails.value = null;

    try {
      // 调用后台关联详情 API 根据司机ID获取完整的详情数据
      const { data: res } = await getBackDriverTruckTypeDetailApi(row.driverId);

      if (res.code === 1 && res.data) {
        currentRowDetails.value = res.data; // 使用详情接口返回的数据更新 currentRowDetails
      } else {
        ElMessage.error(res.msg || '获取司机车型关联详情失败');
        detailDialogVisible.value = false; // 获取失败则关闭弹窗
      }
    } catch (error) {
      console.error('获取司机车型关联详情请求失败:', error);
      ElMessage.error('获取司机车型关联详情失败，请稍后再试');
      detailDialogVisible.value = false; // 请求失败则关闭弹窗
    }
  };

  // === 组件挂载后，首次加载数据及所有货车类型 ===
  onMounted(() => {
    fetchDriverTruckTypeList(); // 获取司机车型关联列表
    fetchAllTruckTypes(); // 获取所有货车类型列表
  });
</script>

<style scoped lang="less">
  // 复用 AdminServiceTypeConfig.vue 的样式，并根据需要进行调整
  .admin-driver-truck-type-assoc-container {
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
          // Added placeholder for input-number
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
        // 搜索表单中的输入框和选择框宽度
        :deep(.el-input),
        :deep(.el-select) {
          width: 200px; /* 基础宽度 */
        }

        :deep(.el-date-editor--daterange) {
          width: 240px; /* 日期范围选择器宽度 - 本页面不需要，但保留样式结构 */
        }
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
          :deep(.el-select) {
            width: 100% !important; // input/select 填充父级内容区域
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

        // 新增按钮样式已移除
        // .add-button-separate {
        //   margin-left: 30px;
        // }

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

        // 新增按钮的绿色风格已移除
        // .el-button--success {
        //   background-color: #67c23a !important;
        //   border-color: #67c23a !important;
        //   color: #fff !important;
        // }
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
      .el-table-column[prop="driverName"],
      .el-table-column[label="关联货车类型"] :deep(.cell) {
        justify-content: flex-start; /* 姓名、关联车型左对齐 */
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
          margin-bottom: 20px; /* 增加表单项之间的间距 */
          .el-form-item__label {
            font-weight: bold;
            color: #555;
            width: 120px !important; /* 标签宽度与弹窗表单一致 */
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