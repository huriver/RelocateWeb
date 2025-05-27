<template>
  <div class="admin-service-item-setting-container">
    <h3>服务项设置</h3>

    <el-form :model="searchForm" class="search-form">
        <el-form-item label="服务项名称">
          <el-input v-model="searchForm.serviceName" placeholder="请输入服务项名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="服务类别">
          <el-select v-model="searchForm.categoryId" placeholder="请选择服务类别" clearable>
            <el-option v-for="category in serviceCategories" :key="category.id"
                       :label="category.typeName" :value="category.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="车辆类型">
          <el-select v-model="searchForm.truckTypeId" placeholder="请选择车辆类型" clearable>
            <el-option v-for="truck in truckTypes" :key="truck.id" :label="truck.typeName"
                       :value="truck.id"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="平均评分">
          <div class="rating-range-input">
            <el-input-number v-model="searchForm.minAverageRating" :min="1" :max="5" :precision="1"
                             placeholder="最小" controls-position="right" clearable
                             style="width: 120px"></el-input-number>
            <span class="range-separator">-</span>
            <el-input-number v-model="searchForm.maxAverageRating" :min="1" :max="5" :precision="1"
                             placeholder="最大" controls-position="right" clearable
                             style="width: 120px"></el-input-number>
          </div>
        </el-form-item>
        <el-form-item label="评价次数">
          <div class="rating-count-range-input">
            <el-input-number v-model="searchForm.minRatingCount" :min="0" :precision="0"
                             placeholder="最小" controls-position="right" clearable
                             style="width: 120px"></el-input-number>
            <span class="range-separator">-</span>
            <el-input-number v-model="searchForm.maxRatingCount" :min="0" :precision="0"
                             placeholder="最大" controls-position="right" clearable
                             style="width: 120px"></el-input-number>
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="起售" :value="1"></el-option>
            <el-option label="停售" :value="0"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="创建时间">
          <el-date-picker v-model="searchForm.createTimeRange" type="datetimerange"
                          range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间"
                          value-format="YYYY-MM-DD HH:mm:ss" :clearable="true"></el-date-picker>
        </el-form-item>

      <el-form-item class="button-group">
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearchForm">重置</el-button>
        <el-button type="success" @click="handleAddServiceItem"
                   class="add-button-separate">新增服务项</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" align="center"></el-table-column>
      <el-table-column prop="serviceName" label="服务项名称" align="left" width="180px"
                       show-overflow-tooltip></el-table-column>
      <el-table-column prop="categoryName" label="服务类别" align="center"></el-table-column>
      <el-table-column prop="truckTypeName" label="车辆类型" align="center"></el-table-column>
      <el-table-column label="评分/次数" align="center">
        <template #default="scope">
          {{ scope.row.averageRating ? scope.row.averageRating.toFixed(1) : 'N/A' }} /
          {{ scope.row.ratingCount !== undefined ? scope.row.ratingCount : 'N/A' }}
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '起售' : '停售' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" align="center"></el-table-column>
      <el-table-column label="操作" width="350" align="center" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="showDetailsService(scope.row)"
                     class="detail-button">详情</el-button>
          <el-button size="small" type="primary" style="margin-left: 10px"
                     @click="handleEditServiceItem(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" style="margin-left: 10px"
                     @click="handleDeleteServiceItem(scope.row)">删除</el-button>
          <el-switch v-model="scope.row.status" active-text="起售" inactive-text="停售"
                     :active-value="1" :inactive-value="0" @change="handleStatusChange(scope.row)"
                     :before-change="() => beforeStatusChange(scope.row)"
                     style="margin-left: 20px"></el-switch>
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
        <el-form-item label="服务类别" prop="categoryId">
          <el-select v-model="editForm.categoryId" placeholder="请选择服务类别">
            <el-option v-for="category in serviceCategories" :key="category.id"
                       :label="category.typeName" :value="category.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="车辆类型" prop="truckTypeId">
          <el-select v-model="editForm.truckTypeId" placeholder="请选择车辆类型">
            <el-option v-for="truck in truckTypes" :key="truck.id" :label="truck.typeName"
                       :value="truck.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="服务项名称" prop="serviceName">
          <el-input v-model="editForm.serviceName" placeholder="请输入服务项名称"></el-input>
        </el-form-item>
        <el-form-item label="服务项简述" prop="shortDescription">
          <el-input v-model="editForm.shortDescription" type="textarea" :rows="3"
                    placeholder="请输入服务项简要描述"></el-input>
        </el-form-item>
        <el-form-item label="装载能力描述" prop="loadingCapacityDescription">
          <el-input v-model="editForm.loadingCapacityDescription" type="textarea" :rows="3"
                    placeholder="请输入装载能力描述"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="服务项详情" width="700px">
      <el-form label-width="170px" v-if="currentRowDetails">
        <el-form-item label="ID">{{ currentRowDetails.id }}</el-form-item>
        <el-form-item label="服务项名称">{{ currentRowDetails.serviceName }}</el-form-item>
        <el-form-item label="服务类别">{{ currentRowDetails.categoryName }}</el-form-item>
        <el-form-item label="车辆类型">{{ currentRowDetails.truckType.typeName }}</el-form-item>
        <el-form-item label="服务项简述">{{ currentRowDetails.shortDescription }}</el-form-item>
        <el-form-item label="装载能力描述">{{
					currentRowDetails.loadingCapacityDescription
				}}</el-form-item>
        <el-form-item label="平均评分">{{
					currentRowDetails.averageRating !== undefined
						? currentRowDetails.averageRating.toFixed(1)
						: 'N/A'
				}}</el-form-item>
        <el-form-item label="评价次数">{{
					currentRowDetails.ratingCount !== undefined ? currentRowDetails.ratingCount : 'N/A'
				}}</el-form-item>
        <el-form-item label="状态">
          <el-tag :type="currentRowDetails.status === 1 ? 'success' : 'info'">
            {{ currentRowDetails.status === 1 ? '起售' : '停售' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="每工人费用">{{
					currentRowDetails.perHelperCost !== undefined
						? `￥${currentRowDetails.perHelperCost.toFixed(2)}`
						: 'N/A'
				}}</el-form-item>
        <el-form-item label="服务类型价格乘数">{{
					currentRowDetails.categoryPriceMultiplier
				}}</el-form-item>
        <el-form-item label="创建时间">{{ currentRowDetails.createTime }}</el-form-item>
        <el-form-item label="更新时间">{{ currentRowDetails.updateTime }}</el-form-item>
        <el-form-item label="创建人ID">{{ currentRowDetails.createUser }}</el-form-item>
        <el-form-item label="更新人ID">{{ currentRowDetails.updateUser }}</el-form-item>

        <template v-if="currentRowDetails.truckType">
          <el-divider>关联车辆类型详细信息</el-divider>
          <el-form-item label="车辆类型ID">{{ currentRowDetails.truckType.id }}</el-form-item>
          <el-form-item label="车辆类型名称">{{
						currentRowDetails.truckType.typeName
					}}</el-form-item>
          <el-form-item label="容量">{{ currentRowDetails.truckType.capacity }}</el-form-item>
          <el-form-item label="描述">{{ currentRowDetails.truckType.description }}</el-form-item>
          <el-form-item label="基础运费">
            {{
							// 检查 truckType 对象及 baseFare 字段是否存在且非空
							currentRowDetails.truckType &&
							currentRowDetails.truckType.baseFare !== undefined &&
							currentRowDetails.truckType.baseFare !== null
								? `￥${currentRowDetails.truckType.baseFare.toFixed(2)}`
								: 'N/A'
						}}
          </el-form-item>

          <el-form-item label="5-25公里每公里价格">
            {{
							// 检查 truckType 对象及 pricePerKmTier1 字段是否存在且非空
							currentRowDetails.truckType &&
							currentRowDetails.truckType.pricePerKmTier1 !== undefined &&
							currentRowDetails.truckType.pricePerKmTier1 !== null
								? `￥${currentRowDetails.truckType.pricePerKmTier1.toFixed(2)}`
								: 'N/A'
						}}
          </el-form-item>

          <el-form-item label="25-30公里每公里价格">
            {{
							// 检查 truckType 对象及 pricePerKmTier2 字段是否存在且非空
							currentRowDetails.truckType &&
							currentRowDetails.truckType.pricePerKmTier2 !== undefined &&
							currentRowDetails.truckType.pricePerKmTier2 !== null
								? `￥${currentRowDetails.truckType.pricePerKmTier2.toFixed(2)}`
								: 'N/A'
						}}
          </el-form-item>

          <el-form-item label="30-50公里每公里价格">
            {{
							// 检查 truckType 对象及 pricePerKmTier3 字段是否存在且非空
							currentRowDetails.truckType &&
							currentRowDetails.truckType.pricePerKmTier3 !== undefined &&
							currentRowDetails.truckType.pricePerKmTier3 !== null
								? `￥${currentRowDetails.truckType.pricePerKmTier3.toFixed(2)}`
								: 'N/A'
						}}
          </el-form-item>

          <el-form-item label="50-80公里每公里价格">
            {{
							// 检查 truckType 对象及 pricePerKmTier4 字段是否存在且非空
							currentRowDetails.truckType &&
							currentRowDetails.truckType.pricePerKmTier4 !== undefined &&
							currentRowDetails.truckType.pricePerKmTier4 !== null
								? `￥${currentRowDetails.truckType.pricePerKmTier4.toFixed(2)}`
								: 'N/A'
						}}
          </el-form-item>

          <el-form-item label="超过80公里每公里价格">
            {{
							// 检查 truckType 对象及 pricePerKmTier5 字段是否存在且非空
							currentRowDetails.truckType &&
							currentRowDetails.truckType.pricePerKmTier5 !== undefined &&
							currentRowDetails.truckType.pricePerKmTier5 !== null
								? `￥${currentRowDetails.truckType.pricePerKmTier5.toFixed(2)}`
								: 'N/A'
						}}
          </el-form-item>
        </template>
      </el-form>
      <div v-else>正在加载服务项详情...</div>

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
    ElDatePicker,
    ElButton,
    ElTable,
    ElTableColumn,
    ElPagination,
    ElSwitch,
    ElOption,
    ElInputNumber, // 导入 InputNumber 组件
    ElDivider, // 导入分割线组件用于详情弹窗
  } from 'element-plus';

  // 导入后台服务项 API
  import {
    getBackServiceListPageApi,
    getBackServiceDetailApi,
    addBackServiceApi,
    updateBackServiceApi,
    deleteBackServiceApi,
    updateBackServiceStatusApi,
  } from '@/api/serviceApi.js'; // <-- 导入服务项 API

  // 导入后台服务类别 API 和 货车类型 API
  import { getBackServiceCategoryListApi } from '@/api/serviceCategoryApi.js';
  import { getBackTruckTypeListApi } from '@/api/truckTypeApi.js';

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
    serviceName: '', // 服务项名称
    categoryId: undefined, // 服务类别ID (使用 undefined 或 null 表示不筛选)
    truckTypeId: undefined, // 车辆类型ID (使用 undefined 或 null 表示不筛选)
    minAverageRating: undefined, // 最小平均评分
    maxAverageRating: undefined, // 最大平均评分
    minRatingCount: undefined, // 最小评价次数 (注意与 NewsList 的 0 默认值不同，因为后端API示例是 minRatingCount=0)
    maxRatingCount: undefined, // 最大评价次数 (注意与 NewsList 的 300 默认值不同)
    status: undefined, // 服务项状态 (0 停售, 1 起售, undefined/null 全部)
    createTimeRange: null, // 创建时间范围 [start, end]
  });

  // 下拉框数据源
  const serviceCategories = ref([]);
  const truckTypes = ref([]);

  // 新增/修改弹窗状态和表单数据
  const editDialogVisible = ref(false); // 控制新增/修改弹窗显示
  const editDialogTitle = computed(() => (editForm.value.id ? '修改服务项' : '新增服务项')); // 弹窗标题
  const editFormRef = ref(null); // 新增/修改表单的引用
  const editForm = ref({
    id: null, // ID 为 null 表示新增
    categoryId: undefined,
    truckTypeId: undefined,
    serviceName: '',
    shortDescription: '',
    loadingCapacityDescription: '',
  });
  // 新增/修改表单校验规则
  const editFormRules = ref({
    categoryId: [{ required: true, message: '请选择服务类别', trigger: 'change' }],
    truckTypeId: [{ required: true, message: '请选择车辆类型', trigger: 'change' }],
    serviceName: [{ required: true, message: '请输入服务项名称', trigger: 'blur' }],
    shortDescription: [{ required: true, message: '请输入服务项简述', trigger: 'blur' }],
    loadingCapacityDescription: [
      { required: true, message: '请输入装载能力描述', trigger: 'blur' },
    ],
  });

  // 详情弹窗状态和当前选中行数据
  const detailDialogVisible = ref(false); // 控制详情弹窗显示
  const currentRowDetails = ref(null); // 存储当前查看详情的行数据

  // === 数据获取方法 ===

  // 获取服务类别和车辆类型列表
  const fetchCategoryAndTruckTypes = async () => {
    try {
      const { data: categoryRes } = await getBackServiceCategoryListApi();
      if (categoryRes.code === 1) {
        serviceCategories.value = categoryRes.data;
      } else {
        console.warn('获取服务类别列表业务失败:', categoryRes.msg);
        // ElMessage.error(categoryRes.msg || '获取服务类别列表失败'); // request.js 已处理
      }

      const { data: truckTypeRes } = await getBackTruckTypeListApi();
      if (truckTypeRes.code === 1) {
        truckTypes.value = truckTypeRes.data;
      } else {
        console.warn('获取车辆类型列表业务失败:', truckTypeRes.msg);
        // ElMessage.error(truckTypeRes.msg || '获取车辆类型列表失败'); // request.js 已处理
      }
    } catch (error) {
      console.error('获取类别和车辆类型请求失败:', error);
      ElMessage.error('获取服务类别或车辆类型失败，请稍后再试'); // request.js 已处理
    }
  };

  const fetchServiceItemsList = async () => {
    loading.value = true;
    try {
      // 准备请求参数，注意筛选条件的 undefined 或 null 处理
      const params = {
        page: pagination.value.page,
        pageSize: pagination.value.pageSize,
        serviceName: searchForm.value.serviceName || undefined,
        categoryId: searchForm.value.categoryId || undefined,
        truckTypeId: searchForm.value.truckTypeId || undefined,
        minAverageRating: searchForm.value.minAverageRating || undefined,
        maxAverageRating: searchForm.value.maxAverageRating || undefined,
        minRatingCount:
          searchForm.value.minRatingCount !== undefined
            ? searchForm.value.minRatingCount
            : undefined, // 明确处理 0 的情况
        maxRatingCount:
          searchForm.value.maxRatingCount !== undefined
            ? searchForm.value.maxRatingCount
            : undefined, // 明确处理 undefined 的情况
        status: searchForm.value.status !== undefined ? searchForm.value.status : undefined, // 明确处理 0 的情况
        createTimeStart: searchForm.value.createTimeRange
          ? searchForm.value.createTimeRange[0]
          : undefined,
        createTimeEnd: searchForm.value.createTimeRange
          ? searchForm.value.createTimeRange[1]
          : undefined,
      };

      const { data: res } = await getBackServiceListPageApi(params); // 调用后台服务项分页 API

      if (res.code === 1) {
        tableData.value = res.data.records;
        pagination.value.total = res.data.total;
      } else {
        // 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
        console.warn('获取服务项列表业务失败:', res.msg); // 可以保留日志
        tableData.value = [];
        pagination.value.total = 0;
      }
    } catch (error) {
      // 捕获真正的请求错误 (网络问题、HTTP错误等，不包括 code !== 1 的业务错误)
      console.error('获取服务项列表请求失败:', error);
      ElMessage.error('获取服务项列表失败，请稍后再试'); // request.js 已处理
      tableData.value = [];
      pagination.value.total = 0;
    } finally {
      loading.value = false;
    }
  };

  // === 搜索相关方法 ===
  const handleSearch = () => {
    pagination.value.page = 1; // 从第一页开始搜索
    fetchServiceItemsList();
  };

  const resetSearchForm = () => {
    searchForm.value = {
      serviceName: '',
      categoryId: undefined,
      truckTypeId: undefined,
      minAverageRating: undefined,
      maxAverageRating: undefined,
      minRatingCount: undefined, // 重置为 undefined
      maxRatingCount: undefined, // 重置为 undefined
      status: undefined, // 重置为 undefined
      createTimeRange: null,
    };
    handleSearch(); // 重置后立即执行查询，回到第一页
  };

  // === 分页相关方法 ===
  const handleSizeChange = (val) => {
    pagination.value.pageSize = val;
    pagination.value.page = 1; // 切换每页大小时回到第一页
    fetchServiceItemsList();
  };

  const handleCurrentChange = (val) => {
    pagination.value.page = val;
    fetchServiceItemsList();
  };

  // === 新增/修改服务项相关方法 ===

  // 打开新增服务项弹窗
  const handleAddServiceItem = () => {
    resetEditForm(); // 打开前先重置表单，确保是新增模式
    editDialogVisible.value = true;
  };

  // 打开修改服务项弹窗
  const handleEditServiceItem = (row) => {
    // 将当前行的数据填充到表单，进入修改模式
    // 注意：浅拷贝可能导致问题，如果需要深拷贝嵌套对象，请使用结构化克隆或其他深拷贝方法
    editForm.value = { ...row };
    // 确保数值类型的字段正确加载，特别是 undefined/null 转为可编辑状态
    if (editForm.value.perHelperCost === null || editForm.value.perHelperCost === undefined) {
      editForm.value.perHelperCost = 0; // 或者设置为其他默认值
    }

    editDialogVisible.value = true;
  };

  // 提交新增/修改表单
  const submitEditForm = async () => {
    // 进行表单校验
    const valid = await editFormRef.value.validate();
    if (!valid) {
      // 前端校验失败
      // ElMessage.error('请填写完整且符合要求的表单项'); // Element Plus 会在输入框下方提示
      return;
    }

    // 前端校验通过，判断是新增还是修改
    const isAdding = !editForm.value.id; // 根据是否有 ID 判断模式

    try {
      const apiCall = isAdding ? addBackServiceApi : updateBackServiceApi;
      // 注意：updateBackServiceApi 期望整个包含 id 的对象
      const { data: res } = await apiCall(editForm.value);

      if (res.code === 1) {
        // 后端返回成功
        ElMessage.success(isAdding ? '服务项添加成功！' : '服务项修改成功！');
        editDialogVisible.value = false; // 关闭弹窗
        resetEditForm(); // 重置表单数据
        fetchServiceItemsList(); // 刷新服务项列表
      } else {
        // 后端返回业务错误 (code !== 1)
        // request.js 已经弹窗提示了后端 msg
        console.warn('提交服务项业务失败:', res.msg); // 可以保留日志
        // 保持弹窗打开，以便用户根据后端错误信息修改输入
      }
    } catch (error) {
      // 捕获真正的请求错误 (网络问题, CORS, request.js 拦截器抛出错误等)
      console.error('提交服务项请求失败:', error);
      ElMessage.error('提交服务项失败，请稍后再试');
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
      categoryId: undefined,
      truckTypeId: undefined,
      serviceName: '',
      shortDescription: '',
      loadingCapacityDescription: '',
    };
  };

  // === 删除服务项相关方法 ===
  const handleDeleteServiceItem = (row) => {
    ElMessageBox.confirm(`确定要删除服务项《${row.serviceName}》吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        try {
          const { data: res } = await deleteBackServiceApi(row.id); // 调用删除 API
          if (res.code === 1) {
            ElMessage.success('服务项删除成功！');
            // 删除成功后判断是否需要留在当前页或跳转到上一页
            if (tableData.value.length === 1 && pagination.value.page > 1) {
              pagination.value.page--;
            }
            fetchServiceItemsList(); // 刷新列表
          } else {
            // 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
            console.warn('服务项删除业务失败:', res.msg); // 可以保留日志
          }
        } catch (error) {
          // 捕获真正的请求错误
          console.error('删除服务项请求失败:', error);
          ElMessage.error('服务项删除失败，请稍后再试');
        }
      })
      .catch(() => {
        // 用户点击取消
        ElMessage.info('已取消删除');
      });
  };

  // === 停售/起售相关方法 ===

  // 在状态切换前进行确认
  const beforeStatusChange = (row) => {
    const confirmText =
      row.status === 1
        ? `确定要停售服务项《${row.serviceName}》吗？`
        : `确定要起售服务项《${row.serviceName}》吗？`;
    return ElMessageBox.confirm(confirmText, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(() => {
        return true; // 用户点击确定，允许切换
      })
      .catch(() => {
        ElMessage.info('操作已取消');
        return false; // 用户点击取消，阻止切换
      });
  };

  // 处理状态切换
  const handleStatusChange = async (row) => {
    // row.status 在 beforeStatusChange 确认后已经更新为期望的新状态 (0 或 1)
    const newStatus = row.status; // 0 停售, 1 起售

    try {
      // 注意：updateBackServiceStatusApi 期望 id 和 status
      const { data: res } = await updateBackServiceStatusApi(row.id, newStatus); // 调用更新状态 API

      if (res.code === 1) {
        ElMessage.success(`${newStatus === 1 ? '起售' : '停售'}成功！`);
        // 状态已由 switch 自动更新，无需手动修改 row.status
      } else {
        // 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
        console.warn('更新服务项状态业务失败:', res.msg); // 可以保留日志
        // 操作失败，手动将 switch 状态拨回原样
        row.status = newStatus === 1 ? 0 : 1;
      }
    } catch (error) {
      // 捕获真正的请求错误 (网络问题、HTTP错误等)
      console.error('更新服务项状态请求失败:', error);
      ElMessage.error('更新服务项状态失败，请稍后再试');
      // 操作失败，手动将 switch 状态拨回原样
      row.status = newStatus === 1 ? 0 : 1;
    }
  };

  // === 查看服务项详情相关方法 ===
  const showDetailsService = async (row) => {
    detailDialogVisible.value = true;
    // 在加载新的详情数据前，清空旧的数据，以免显示陈旧信息
    currentRowDetails.value = null;

    try {
      // 调用后台服务项详情 API 根据服务项ID获取完整的详情数据
      const { data: res } = await getBackServiceDetailApi(row.id); // <-- 调用详情 API

      if (res.code === 1 && res.data) {
        currentRowDetails.value = res.data; // 使用详情接口返回的数据更新 currentRowDetails
      } else {
        // 业务失败 (code !== 1) 或数据不存在，request.js 已经弹窗提示了后端 msg
        console.warn('获取服务项详情业务失败:', res.msg); // 可以保留日志
        detailDialogVisible.value = false; // 获取失败则关闭弹窗
      }
    } catch (error) {
      // 捕获真正的请求错误 (网络问题、HTTP错误等)
      console.error('获取服务项详情请求失败:', error);
      ElMessage.error('获取服务项详情失败，请检查网络。');
      detailDialogVisible.value = false; // 请求失败则关闭弹窗
    }
  };

  // === 组件挂载后，首次加载数据和下拉框数据 ===
  onMounted(() => {
    fetchCategoryAndTruckTypes(); // 获取服务类别和车辆类型
    fetchServiceItemsList(); // 获取服务项列表
  });
</script>

<style scoped lang="less">
  .admin-service-item-setting-container {
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
      gap: 10px 20px;
      /* 行和列之间的间距 */

      .input-items-group {
        display: flex;
        flex-wrap: wrap; // 允许换行
        align-items: center;
        width: 100%; // 确保组占据一行以便均匀分布
        gap: 10px 20px; /* 组内的行和列间距 */

        .el-form-item {
          margin-right: 0 !important; // 确保不被其他规则影响
          margin-bottom: 0 !important; // 确保不被其他规则影响，使用 gap 控制垂直间距
          flex-grow: 0; // 不拉伸
          flex-shrink: 1; // 允许缩小
          flex-basis: auto; // 根据内容确定初始大小
        }
      }

      // 调整 search-form 直接子元素的 el-form-item 的垂直间距 (如果存在非 group 的 items)
      > .el-form-item {
        flex-shrink: 0; // 防止按钮组缩小
        // 如果希望它独占一行，可以加 width: 100%;
        // display: flex; /* 确保内部元素如按钮能对齐 */
        // align-items: center;
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
        }
        .el-form-item__content {
          line-height: 32px;
          // 确保内容区域不会拉伸，宽度由内部元素决定 (默认行为)
          flex-grow: 0;
          width: auto;
          display: flex; /* 使内部元素更好地对齐 */
          align-items: center;
        }

        // === 设置输入框、选择框、数字输入框、日期选择器组件的宽度 ===
        :deep(.el-input),
        :deep(.el-select) {
          width: 200px; /* 基础宽度 */
        }

        :deep(.el-date-editor--daterange),
        :deep(.el-date-editor--datetimerange) {
          width: 360px; /* 日期范围选择器宽度 */
        }
        :deep(.el-input-number) {
          width: auto; /* 数字输入框宽度由内容或父级flex决定 */
        }

        .rating-range-input,
        .rating-count-range-input {
          display: flex;
          align-items: center;
          gap: 5px; /* 数字输入框之间的间距 */
          .range-separator {
            color: #606266;
            font-size: 14px;
          }
          .el-input-number {
            width: 100px; /* 为评分和次数输入框设置较小的宽度 */
          }
        }
      }

      .el-form-item {
        margin-bottom: 0;
      }

      // === 针对第一个分组的均匀分布样式 (如果需要，此处可以调整) ===
      // 当前修改为 flex-direction: column，evenly-distributed-group 的效果可能需要重新思考或调整
      // 比如让每个 input-items-group 内部的 form-item 均匀分布
      .input-items-group.evenly-distributed-group {
        flex-direction: row; // 组内元素横向排列
        justify-content: space-between; // 尝试均匀分布

        .el-form-item {
          flex-grow: 1; // 让 form item 增长
          flex-basis: 0; // 使其起始基准为 0，更容易实现均匀分布
          min-width: 200px; // 设置一个最小宽度防止挤压过窄

          :deep(.el-form-item__content) {
            flex-grow: 1; // 内容区域也增长
            width: 100%; // 让内部 input/select 填充
          }

          :deep(.el-input),
          :deep(.el-select) {
            width: 100% !important; // input/select 填充父级内容区域
            min-width: auto; // 移除通用最小宽度限制
          }
        }
      }

      .button-group {
       
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

        // 如果某些列需要左对齐，可以添加 class 并修改样式
        // 例如：.el-table-column.align-left :deep(.cell) { justify-content: flex-start; }
      }
      // 对齐需要左对齐的列
      .el-table-column[prop="serviceName"],
      .el-table-column[prop="shortDescription"],
      .el-table-column[prop="loadingCapacityDescription"] :deep(.cell) {
        justify-content: flex-start; /* 左对齐 */
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

      // 调整操作列按钮与 switch 的间距
      .el-button + .el-switch {
        margin-left: 10px;
      }
      .el-switch + .el-button {
        margin-left: 10px; // unlikely but just in case
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
        // 调整弹窗内表单的样式
        .el-form-item {
          margin-bottom: 15px; /* 默认表单项下方间距 */

          // 对详情弹窗内的表单项做调整，间距小一些
          &:not(.is-error) {
            // 避免影响校验错误时的布局
            &.el-form-item--default {
              // 默认尺寸的 form item
              margin-bottom: 10px;
            }
          }

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
            display: flex; /* 使内容对齐，特别是多行文本 */
            align-items: center; // 垂直居中对齐

            // 确保内部元素如 input, select 填充内容区域
            .el-input,
            .el-select,
            .el-textarea,
            .el-input-number {
              width: 100% !important;
            }
            .el-textarea {
              line-height: 1.5; // 保持与文本一致的行高
            }

            img {
              vertical-align: middle;
            }
            .el-tag {
              vertical-align: middle;
            }
          }
        }
        // 调整详情弹窗内的表单项内容区的对齐方式，特别是多行文本
        &.is-detail-form .el-form-item .el-form-item__content {
          align-items: flex-start; // 顶部对齐，适合多行文本
        }
      }

      .el-dialog__footer {
        border-top: 1px solid #eee;
        padding: 15px 20px;
        .dialog-footer {
          text-align: right;
        }
      }

      // 分割线样式
      .el-divider {
        margin: 20px 0;
        .el-divider__text {
          font-size: 16px;
          color: #606266;
        }
      }
    }
  }
</style>
