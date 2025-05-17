<template>
	<div class="admin-vehicle-type-container">
		<h3>车辆类型配置</h3>

		<el-form :inline="true" :model="searchForm" class="search-form">
			<div class="input-items-group evenly-distributed-group">
				<el-form-item label="类型名称">
					<el-input
						v-model="searchForm.typeName"
						placeholder="请输入类型名称关键词"
						clearable
					></el-input>
				</el-form-item>
				<el-form-item label="基础运费范围">
					<div class="base-fare-range">
						<el-input-number
							v-model="searchForm.minBaseFare"
							:precision="2"
							:step="10"
							:min="0"
							placeholder="最小"
							controls-position="right"
							class="range-input-number"
						></el-input-number>
						<span class="range-separator">至</span>
						<el-input-number
							v-model="searchForm.maxBaseFare"
							:precision="2"
							:step="10"
							:min="0"
							placeholder="最大"
							controls-position="right"
							class="range-input-number"
						></el-input-number>
					</div>
				</el-form-item>
			</div>

			<div class="input-items-group">
				<el-form-item label="创建日期">
					<el-date-picker
						v-model="searchForm.createTimeRange"
						type="daterange"
						range-separator="至"
						start-placeholder="开始日期"
						end-placeholder="结束日期"
						value-format="YYYY-MM-DD HH:mm:ss"
						:clearable="true"
						:default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
					></el-date-picker>
				</el-form-item>
			</div>

			<el-form-item class="button-group">
				<el-button type="primary" @click="handleSearch">查询</el-button>
				<el-button @click="resetSearchForm">重置</el-button>
				<el-button type="success" @click="handleAddVehicleType" class="add-button-separate"
					>新增货车类型</el-button
				>
			</el-form-item>
		</el-form>

		<el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
			<el-table-column prop="id" label="ID" align="center"></el-table-column>
			<el-table-column
				prop="typeName"
				label="车辆类型名称"
				align="left"
				show-overflow-tooltip
			></el-table-column>
			<el-table-column
				prop="capacity"
				label="容积/尺寸"
				align="left"
				show-overflow-tooltip
			></el-table-column>
			<el-table-column prop="baseFare" label="基础运费" align="center"></el-table-column>
			<el-table-column prop="updateTime" label="更新时间" align="center"></el-table-column>
			<el-table-column prop="updateUserName" label="更新人" align="center"></el-table-column>

			<el-table-column label="操作" width="200" align="center" fixed="right">
				<template #default="scope">
					<el-button size="small" @click="showDetails(scope.row)" class="detail-button"
						>详情</el-button
					>
					<el-button
						size="small"
						type="primary"
						style="margin-left: 10px"
						@click="handleEditVehicleType(scope.row)"
						>编辑</el-button
					>
					<el-button
						size="small"
						type="danger"
						style="margin-left: 10px"
						@click="handleDeleteVehicleType(scope.row)"
						>删除</el-button
					>
				</template>
			</el-table-column>
		</el-table>

		<el-pagination
			@size-change="handleSizeChange"
			@current-change="handleCurrentChange"
			:current-page="pagination.page"
			:page-sizes="[10, 20, 50, 100]"
			:page-size="pagination.pageSize"
			layout="total, sizes, prev, pager, next, jumper"
			:total="pagination.total"
			background
			class="pagination"
		></el-pagination>

		<el-dialog
			v-model="editDialogVisible"
			:title="editDialogTitle"
			width="600px"
			@close="resetEditForm"
		>
			<el-form :model="editForm" :rules="editFormRules" ref="editFormRef" label-width="120px">
				<el-form-item label="类型名称" prop="typeName">
					<el-input v-model="editForm.typeName" placeholder="请输入货车类型名称"></el-input>
				</el-form-item>
				<el-form-item label="载货容积/尺寸" prop="capacity">
					<el-input
						v-model="editForm.capacity"
						placeholder="请输入货车容积或尺寸，如 1.8*1.3*1.1m"
					></el-input>
				</el-form-item>
				<el-form-item label="描述" prop="description">
					<el-input
						v-model="editForm.description"
						type="textarea"
						:rows="4"
						placeholder="请输入货车类型描述"
					></el-input>
				</el-form-item>
				<el-form-item label="基础运费" prop="baseFare">
					<el-input-number
						v-model="editForm.baseFare"
						:precision="2"
						:step="10"
						:min="0"
						placeholder="请输入基础运费"
						style="width: 100%"
						controls-position="right"
					></el-input-number>
				</el-form-item>
				<el-form-item label="价格/km (T1)" prop="pricePerKmTier1">
					<el-input-number
						v-model="editForm.pricePerKmTier1"
						:precision="2"
						:step="0.5"
						:min="0"
						placeholder="请输入阶梯1价格/km"
						style="width: 100%"
						controls-position="right"
					></el-input-number>
				</el-form-item>
				<el-form-item label="价格/km (T2)" prop="pricePerKmTier2">
					<el-input-number
						v-model="editForm.pricePerKmTier2"
						:precision="2"
						:step="0.5"
						:min="0"
						placeholder="请输入阶梯2价格/km"
						style="width: 100%"
						controls-position="right"
					></el-input-number>
				</el-form-item>
				<el-form-item label="价格/km (T3)" prop="pricePerKmTier3">
					<el-input-number
						v-model="editForm.pricePerKmTier3"
						:precision="2"
						:step="0.5"
						:min="0"
						placeholder="请输入阶梯3价格/km"
						style="width: 100%"
						controls-position="right"
					></el-input-number>
				</el-form-item>
				<el-form-item label="价格/km (T4)" prop="pricePerKmTier4">
					<el-input-number
						v-model="editForm.pricePerKmTier4"
						:precision="2"
						:step="0.5"
						:min="0"
						placeholder="请输入阶梯4价格/km"
						style="width: 100%"
						controls-position="right"
					></el-input-number>
				</el-form-item>
				<el-form-item label="价格/km (T5)" prop="pricePerKmTier5">
					<el-input-number
						v-model="editForm.pricePerKmTier5"
						:precision="2"
						:step="0.5"
						:min="0"
						placeholder="请输入阶梯5价格/km"
						style="width: 100%"
						controls-position="right"
					></el-input-number>
				</el-form-item>
			</el-form>
			<template #footer>
				<div class="dialog-footer">
					<el-button @click="editDialogVisible = false">取消</el-button>
					<el-button type="primary" @click="submitEditForm">确定</el-button>
				</div>
			</template>
		</el-dialog>

		<el-dialog v-model="detailDialogVisible" title="货车类型详情" width="600px">
			<el-form label-width="auto" v-if="currentRowDetails">
				<el-form-item label="ID">{{ currentRowDetails.id }}</el-form-item>
				<el-form-item label="车辆类型名称">{{ currentRowDetails.typeName }}</el-form-item>
				<el-form-item label="容积/尺寸">{{ currentRowDetails.capacity }}</el-form-item>
				<el-form-item label="描述">
					<div class="vehicle-type-description-display" style="margin-top: -5px">
						{{ currentRowDetails.description }}
					</div>
				</el-form-item>
				<el-form-item label="基础运费">
					{{
						currentRowDetails.baseFare !== undefined && currentRowDetails.baseFare !== null
							? `￥${currentRowDetails.baseFare.toFixed(2)}`
							: 'N/A'
					}}
				</el-form-item>

				<el-form-item label="5-25公里每公里价格">
					{{
						currentRowDetails.pricePerKmTier1 !== undefined &&
						currentRowDetails.pricePerKmTier1 !== null
							? `￥${currentRowDetails.pricePerKmTier1.toFixed(2)}`
							: 'N/A'
					}}
				</el-form-item>

				<el-form-item label="25-30公里每公里价格">
					{{
						currentRowDetails.pricePerKmTier2 !== undefined &&
						currentRowDetails.pricePerKmTier2 !== null
							? `￥${currentRowDetails.pricePerKmTier2.toFixed(2)}`
							: 'N/A'
					}}
				</el-form-item>

				<el-form-item label="30-50公里每公里价格">
					{{
						currentRowDetails.pricePerKmTier3 !== undefined &&
						currentRowDetails.pricePerKmTier3 !== null
							? `￥${currentRowDetails.pricePerKmTier3.toFixed(2)}`
							: 'N/A'
					}}
				</el-form-item>

				<el-form-item label="50-80公里每公里价格">
					{{
						currentRowDetails.pricePerKmTier4 !== undefined &&
						currentRowDetails.pricePerKmTier4 !== null
							? `￥${currentRowDetails.pricePerKmTier4.toFixed(2)}`
							: 'N/A'
					}}
				</el-form-item>

				<el-form-item label="超过80公里每公里价格">
					{{
						currentRowDetails.pricePerKmTier5 !== undefined &&
						currentRowDetails.pricePerKmTier5 !== null
							? `￥${currentRowDetails.pricePerKmTier5.toFixed(2)}`
							: 'N/A'
					}}
				</el-form-item>
				<el-form-item label="创建时间">{{ currentRowDetails.createTime }}</el-form-item>
				<el-form-item label="创建人">{{ currentRowDetails.createUserName }}</el-form-item>
				<el-form-item label="更新时间">{{ currentRowDetails.updateTime }}</el-form-item>
				<el-form-item label="更新人">{{ currentRowDetails.updateUserName }}</el-form-item>
			</el-form>
			<div v-else>正在加载货车类型详情...</div>

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
		ElInput,
		ElDatePicker,
		ElButton,
		ElTable,
		ElTableColumn,
		ElPagination,
		ElInputNumber, // Needed for fares and prices
	} from 'element-plus';

	// 导入后台货车类型 API
	import {
		getBackTruckTypeListPageApi,
		getBackTruckTypeDetailApi,
		addBackTruckTypeApi,
		updateBackTruckTypeApi,
		deleteBackTruckTypeApi,
		// getBackTruckTypeListApi, // 导入非分页列表API，如果需要
	} from '@/api/truckTypeApi.js'; // <-- 导入货车类型 API

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
		typeName: '', // 类型名称关键词
		minBaseFare: null, // 最小基础运费
		maxBaseFare: null, // 最大基础运费
		createTimeRange: null, // 创建时间范围 [start, end]
	});

	// 新增/修改弹窗状态和表单数据
	const editDialogVisible = ref(false); // 控制新增/修改弹窗显示
	const editDialogTitle = computed(() => (editForm.value.id ? '修改货车类型' : '新增货车类型')); // 弹窗标题
	const editFormRef = ref(null); // 新增/修改表单的引用
	const editForm = ref({
		id: null, // ID 为 null 表示新增
		typeName: '',
		capacity: '',
		description: '',
		baseFare: null, // 使用 baseFare 字段
		pricePerKmTier1: null,
		pricePerKmTier2: null,
		pricePerKmTier3: null,
		pricePerKmTier4: null,
		pricePerKmTier5: null,
	});
	// 新增/修改表单校验规则
	const editFormRules = ref({
		typeName: [{ required: true, message: '请输入货车类型名称', trigger: 'blur' }],
		capacity: [{ required: true, message: '请输入载货容积或尺寸', trigger: 'blur' }],
		description: [{ required: true, message: '请输入货车类型描述', trigger: 'blur' }],
		baseFare: [
			{ required: true, message: '请输入基础运费', trigger: 'change' }, // Trigger on change for input-number
			{
				type: 'number',
				min: 0, // 基础运费 >= 0
				message: '基础运费必须是非负数字',
				trigger: ['blur', 'change'],
			},
		],
		pricePerKmTier1: [
			{ required: true, message: '请输入阶梯1价格/km', trigger: 'change' },
			{ type: 'number', min: 0, message: '价格/km(T1)必须是非负数字', trigger: ['blur', 'change'] },
		],
		pricePerKmTier2: [
			{ required: true, message: '请输入阶梯2价格/km', trigger: 'change' },
			{ type: 'number', min: 0, message: '价格/km(T2)必须是非负数字', trigger: ['blur', 'change'] },
		],
		pricePerKmTier3: [
			{ required: true, message: '请输入阶梯3价格/km', trigger: 'change' },
			{ type: 'number', min: 0, message: '价格/km(T3)必须是非负数字', trigger: ['blur', 'change'] },
		],
		pricePerKmTier4: [
			{ required: true, message: '请输入阶梯4价格/km', trigger: 'change' },
			{ type: 'number', min: 0, message: '价格/km(T4)必须是非负数字', trigger: ['blur', 'change'] },
		],
		pricePerKmTier5: [
			{ required: true, message: '请输入阶梯5价格/km', trigger: 'change' },
			{ type: 'number', min: 0, message: '价格/km(T5)必须是非负数字', trigger: ['blur', 'change'] },
		],
	});

	// 详情弹窗状态和当前选中行数据
	const detailDialogVisible = ref(false); // 控制详情弹窗显示
	const currentRowDetails = ref(null); // 存储当前查看详情的行数据

	// === 数据获取方法 ===
	const fetchVehicleTypeList = async () => {
		loading.value = true;
		try {
			// 准备请求参数
			const params = {
				page: pagination.value.page,
				pageSize: pagination.value.pageSize,
				typeName: searchForm.value.typeName || undefined,
				minBaseFare: searchForm.value.minBaseFare || undefined,
				maxBaseFare: searchForm.value.maxBaseFare || undefined,
				createTimeStart: searchForm.value.createTimeRange
					? searchForm.value.createTimeRange[0]
					: undefined,
				createTimeEnd: searchForm.value.createTimeRange
					? searchForm.value.createTimeRange[1]
					: undefined,
			};

			const { data: res } = await getBackTruckTypeListPageApi(params); // 调用后台货车类型分页 API

			if (res.code === 1) {
				tableData.value = res.data.records;
				pagination.value.total = res.data.total;
			} else {
				// ElMessage.error(res.msg || '获取货车类型列表失败');
				tableData.value = [];
				pagination.value.total = 0;
			}
		} catch (error) {
			console.error('获取货车类型列表请求失败:', error);
			ElMessage.error('获取货车类型列表失败，请稍后再试');
			tableData.value = [];
			pagination.value.total = 0;
		} finally {
			loading.value = false;
		}
	};

	// === 搜索相关方法 ===
	const handleSearch = () => {
		pagination.value.page = 1; // 从第一页开始搜索
		fetchVehicleTypeList();
	};

	const resetSearchForm = () => {
		searchForm.value = {
			typeName: '',
			minBaseFare: null,
			maxBaseFare: null,
			createTimeRange: null,
		};
		handleSearch(); // 重置后立即执行查询，回到第一页
	};

	// === 分页相关方法 ===
	const handleSizeChange = (val) => {
		pagination.value.pageSize = val;
		pagination.value.page = 1; // 切换每页大小时回到第一页
		fetchVehicleTypeList();
	};

	const handleCurrentChange = (val) => {
		pagination.value.page = val;
		fetchVehicleTypeList();
	};

	// === 新增/修改货车类型相关方法 ===

	// 打开新增货车类型弹窗
	const handleAddVehicleType = () => {
		resetEditForm(); // 打开前先重置表单，确保是新增模式
		editDialogVisible.value = true;
	};

	// 打开修改货车类型弹窗
	const handleEditVehicleType = (row) => {
		// 将当前行的数据填充到表单，进入修改模式
		// 使用 row 数据填充，因为API示例的PUT需要这些字段
		editForm.value = { ...row };
		editDialogVisible.value = true;
	};

	// 提交新增/修改表单
	const submitEditForm = async () => {
		// 进行表单校验
		const valid = await editFormRef.value.validate();
		if (!valid) {
			// 前端校验失败，Element Plus 会在输入框下方提示错误信息
			ElMessage.error('请填写完整且符合要求的表单项'); // 额外弹出提示
			return;
		}

		// 前端校验通过，判断是新增还是修改
		const isAdding = !editForm.value.id; // 根据是否有 ID 判断模式

		try {
			const apiCall = isAdding ? addBackTruckTypeApi : updateBackTruckTypeApi;
			// 注意：updateBackTruckTypeApi 期望整个包含 id 的对象
			const { data: res } = await apiCall(editForm.value);

			if (res.code === 1) {
				// 后端返回成功
				ElMessage.success(isAdding ? '货车类型添加成功！' : '货车类型修改成功！');
				editDialogVisible.value = false; // 关闭弹窗
				resetEditForm(); // 重置表单数据
				fetchVehicleTypeList(); // 刷新列表
			} else {
				// 后端返回业务错误 (code !== 1)
				// ElMessage.error(res.msg || (isAdding ? '货车类型添加失败' : '货车类型修改失败'));
				// 保持弹窗打开，以便用户根据后端错误信息修改输入
			}
		} catch (error) {
			// 请求本身发生错误 (网络问题, CORS, request.js 拦截器抛出错误等)
			console.error('提交货车类型请求失败:', error);

			// 尝试从错误响应中提取更具体的错误信息
			const specificErrorMessage =
				error.message || (error.response && error.response.data && error.response.data.msg);

			const errorMessageToDisplay =
				specificErrorMessage &&
				specificErrorMessage !== '未知错误' &&
				specificErrorMessage !== 'Internal Server Error'
					? `操作失败: ${specificErrorMessage}`
					: isAdding
					? '新增货车类型失败，请稍后再试'
					: '修改货车类型失败，请稍后再试';

			ElMessage.error(errorMessageToDisplay);

			// 如果是新增操作失败，并且弹窗应该保持打开以修改，这里不需要关闭弹窗和重置
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
			typeName: '',
			capacity: '',
			description: '',
			baseFare: null,
			pricePerKmTier1: null,
			pricePerKmTier2: null,
			pricePerKmTier3: null,
			pricePerKmTier4: null,
			pricePerKmTier5: null,
		};
	};

	// === 删除货车类型相关方法 ===
	const handleDeleteVehicleType = (row) => {
		ElMessageBox.confirm(`确定要删除货车类型《${row.typeName}》吗？`, '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
		})
			.then(async () => {
				try {
					const { data: res } = await deleteBackTruckTypeApi(row.id); // 调用删除 API

					// 在 .then 块中处理业务结果
					if (res.code === 1) {
						ElMessage.success('货车类型删除成功！');
						// 删除成功后判断是否需要留在当前页或跳转到上一页
						if (tableData.value.length === 1 && pagination.value.page > 1) {
							pagination.value.page--;
						}
						fetchVehicleTypeList(); // 刷新列表
					} else {
						// res.code !== 1，业务失败。错误提示通常已在 request.js 拦截器中弹出。
						// 但这里为了确保，仍然可以加上一个提示，但要注意避免重复。
						// 如果 request.js 拦截器已经统一处理并弹出了，这里可以只打印日志。
						console.warn('货车类型删除业务失败:', res.msg);
						// ElMessage.error(res.msg || '货车类型删除失败'); // 这行取决于 request.js 如何处理非 code=1 的情况
					}
				} catch (error) {
					// 捕获真正的请求错误（网络中断、超时等非业务错误）
					console.error('货车类型删除请求失败:', error);
					ElMessage.error('删除货车类型失败，请稍后再试');
				}
			})
			.catch(() => {
				// 这是 ElMessageBox.confirm 的 catch，处理用户点击取消
				ElMessage.info('已取消删除');
			});
	};

	// === 查看货车类型详情相关方法 ===
	const showDetails = async (row) => {
		detailDialogVisible.value = true;
		// 在加载新的详情数据前，清空旧的数据，以免显示陈旧信息
		currentRowDetails.value = null;

		try {
			// 调用后台货车类型详情 API 根据ID获取完整的详情数据
			const { data: res } = await getBackTruckTypeDetailApi(row.id);

			if (res.code === 1 && res.data) {
				currentRowDetails.value = res.data; // 使用详情接口返回的数据更新 currentRowDetails
			} else {
				// ElMessage.error(res.msg || '获取货车类型详情失败');
				detailDialogVisible.value = false; // 获取失败则关闭弹窗
			}
		} catch (error) {
			console.error('获取货车类型详情请求失败:', error);
			ElMessage.error('获取货车类型详情失败，请稍后再试');
			detailDialogVisible.value = false; // 请求失败则关闭弹窗
		}
	};

	// === 组件挂载后，首次加载数据 ===
	onMounted(() => {
		fetchVehicleTypeList(); // 获取货车类型列表
	});
</script>

<style scoped lang="less">
	.admin-vehicle-type-container {
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
				:deep(.el-input),
				:deep(.el-select) {
					width: 200px; /* 基础宽度 */
				}

				// Specific width for fare range inputs
				.base-fare-range {
					display: flex;
					align-items: center;
					.range-input-number {
						width: 100px; // Adjust width for range inputs
					}
					.range-separator {
						margin: 0 8px; // Space between input numbers
						color: #606266;
					}
				}

				:deep(.el-date-editor--daterange) {
					width: 240px; /* 日期范围选择器宽度 */
				}
				// Specific width for standalone input-number in dialog
				:deep(.el-input-number) {
					width: 100%; /* Make standalone input-number fill container */
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

					.base-fare-range {
						width: 100%; // Make the range container fill its space
						.range-input-number {
							flex-grow: 1; // Allow range inputs to grow
							width: auto; // Reset specific width
							min-width: 80px; // Minimum width for range input
						}
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
			.el-table-column[prop='typeName'],
			.el-table-column[prop='capacity'],
			.el-table-column[prop='description'] :deep(.cell) {
				justify-content: flex-start; /* 名称、容量、描述左对齐 */
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
						width: 120px !important; /* 标签宽度与弹窗表单一致 */
						text-align: right;
					}
					.el-form-item__content {
						color: #333;
						line-height: 1.5;
						word-break: break-all;
						flex-grow: 1; // 内容区域填充剩余空间
						margin-left: 0 !important; /* 取消 el-form-item__label-wrap 可能产生的左外边距 */

						.vehicle-type-description-display {
							width: 100%;
							box-sizing: border-box;
							padding: 5px 0;
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
</style>
