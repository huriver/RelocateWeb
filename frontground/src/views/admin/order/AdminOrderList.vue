<template>
	<div class="admin-order-list-container">
		<h3>订单列表</h3>

		<el-form :inline="true" :model="searchForm" class="search-form">
			<div class="input-items-group">
				<el-form-item label="订单号">
					<el-input
						v-model="searchForm.orderNumber"
						placeholder="请输入订单号"
						clearable
					></el-input>
				</el-form-item>
				<el-form-item label="订单状态">
					<el-select v-model="searchForm.orderStatus" placeholder="请选择状态" clearable>
						<el-option
							v-for="status in orderStatusOptions"
							:key="status.code"
							:label="status.description"
							:value="status.code"
						></el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="支付状态">
					<el-select v-model="searchForm.isPaid" placeholder="请选择支付状态" clearable>
						<el-option
							v-for="status in paymentStatusOptions"
							:key="status.code"
							:label="status.description"
							:value="status.code"
						></el-option>
					</el-select>
				</el-form-item>
			</div>

			<div class="input-items-group">
				<el-form-item label="客户信息">
					<el-input
						v-model="searchForm.customerKeyword"
						placeholder="姓名/手机号"
						clearable
					></el-input>
				</el-form-item>
				<el-form-item label="司机姓名">
					<el-input
						v-model="searchForm.driverName"
						placeholder="请输入司机姓名"
						clearable
					></el-input>
				</el-form-item>
				<el-form-item label="服务项名称">
					<el-input
						v-model="searchForm.serviceItemName"
						placeholder="请输入服务项名称"
						clearable
					></el-input>
				</el-form-item>
			</div>

			<div class="input-items-group">
				<el-form-item label="服务类别">
					<el-select v-model="searchForm.categoryId" placeholder="请选择服务类别" clearable>
						<el-option
							v-for="category in serviceCategoryOptions"
							:key="category.id"
							:label="category.typeName"
							:value="category.id"
						></el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="货车类型">
					<el-select v-model="searchForm.truckTypeId" placeholder="请选择货车类型" clearable>
						<el-option
							v-for="truckType in truckTypeOptions"
							:key="truckType.id"
							:label="truckType.typeName"
							:value="truckType.id"
						></el-option>
					</el-select>
				</el-form-item>
			</div>

			<el-form-item label="创建时间">
				<el-date-picker
					v-model="searchForm.createTimeRange"
					type="datetimerange"
					range-separator="至"
					start-placeholder="开始时间"
					end-placeholder="结束时间"
					value-format="YYYY-MM-DD HH:mm:ss"
				></el-date-picker>
			</el-form-item>

			<el-form-item label="预约时间">
				<el-date-picker
					v-model="searchForm.reservationTimeRange"
					type="datetimerange"
					range-separator="至"
					start-placeholder="开始时间"
					end-placeholder="结束时间"
					value-format="YYYY-MM-DD HH:mm:ss"
				></el-date-picker>
			</el-form-item>

			<el-form-item class="button-group">
				<el-button type="primary" @click="handleSearch">查询</el-button>
				<el-button @click="resetSearchForm">重置</el-button>
			</el-form-item>
		</el-form>

		<el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
			<el-table-column prop="id" label="ID" align="center"></el-table-column>
			<el-table-column
				prop="orderNumber"
				label="订单号"
				align="center"
				width="200"
			></el-table-column>
			<el-table-column prop="orderStatusDescription" label="订单状态" align="center" width="110">
				<template #default="scope">
					<el-tag :type="getOrderStatusTagType(scope.row.orderStatus)">
						{{ scope.row.orderStatusDescription }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="isPaidDescription" label="支付状态" align="center" width="110">
				<template #default="scope">
					<el-tag :type="getPaymentStatusTagType(scope.row.isPaid)">
						{{ scope.row.isPaidDescription }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column
				prop="customerName"
				label="客户姓名"
				align="center"
				width="110"
			></el-table-column>
			<el-table-column
				prop="customerPhone"
				label="客户手机号"
				align="center"
				width="120"
			></el-table-column>
			<el-table-column
				prop="serviceName"
				label="服务名称"
				align="center"
				width="200"
			></el-table-column>
			<el-table-column
				prop="truckTypeName"
				label="车型"
				align="center"
				width="120"
			></el-table-column>
			<el-table-column
				prop="reservationTime"
				label="预约时间"
				align="center"
				width="180"
			></el-table-column>
			<el-table-column
				prop="movingOrigin"
				label="出发地"
				align="center"
				min-width="150"
			></el-table-column>
			<el-table-column
				prop="movingDestination"
				label="目的地"
				align="center"
				min-width="150"
			></el-table-column>
			<el-table-column
				prop="movingPrice"
				label="订单金额"
				align="center"
				width="110"
			></el-table-column>
			<el-table-column
				prop="driverName"
				label="司机姓名"
				align="center"
				width="110"
			></el-table-column>
			<el-table-column
				prop="vehiclePlateNumber"
				label="车牌号"
				align="center"
				width="110"
			></el-table-column>
			<el-table-column
				prop="createTime"
				label="创建时间"
				align="center"
				width="180"
			></el-table-column>
			<el-table-column label="操作" width="350" align="center" fixed="right">
				<template #default="scope">
					<el-button size="small" @click="showDetails(scope.row)" class="detail-button"
						>详情</el-button
					>
					<el-button
						size="small"
						type="danger"
						style="margin-left: 20px"
						@click="handleCancelOrder(scope.row)"
						:disabled="scope.row.orderStatus === 4 || scope.row.orderStatus === 5"
					>
						取消订单
					</el-button>
					<el-button
						size="small"
						type="success"
						style="margin-left: 10px"
						@click="handleForceComplete(scope.row)"
						:disabled="scope.row.orderStatus !== 3"
						>强制完成</el-button
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

		<el-dialog v-model="dialogVisible" title="订单详情" width="600px">
			<el-form label-width="120px" v-if="currentRowDetails">
				<el-form-item label="订单ID">{{ currentRowDetails.id }}</el-form-item>
				<el-form-item label="订单号">{{ currentRowDetails.orderNumber }}</el-form-item>
				<el-form-item label="订单状态">
					<el-tag :type="getOrderStatusTagType(currentRowDetails.orderStatus)">
						{{ currentRowDetails.orderStatusDescription }}
					</el-tag>
				</el-form-item>
				<el-form-item label="是否已评价">
					<el-tag :type="currentRowDetails.isReviewed ? 'success' : 'info'">
						{{ currentRowDetails.isReviewed ? '是' : '否' }}
					</el-tag>
				</el-form-item>
				<el-form-item label="客户姓名">{{ currentRowDetails.customerName }}</el-form-item>
				<el-form-item label="客户手机号">{{ currentRowDetails.customerPhone }}</el-form-item>
				<el-form-item label="服务类别">{{
					currentRowDetails.serviceCategoryName || 'N/A'
				}}</el-form-item>
				<el-form-item label="服务项目">{{ currentRowDetails.serviceName }}</el-form-item>
				<el-form-item label="预约时间">{{ currentRowDetails.reservationTime }}</el-form-item>
				<el-form-item label="开始时间">{{
					currentRowDetails.movingStartTime || '未开始'
				}}</el-form-item>
				<el-form-item label="结束时间">{{
					currentRowDetails.movingEndTime || '未结束'
				}}</el-form-item>
				<el-form-item label="出发地">{{ currentRowDetails.movingOrigin }}</el-form-item>
				<el-form-item label="目的地">{{ currentRowDetails.movingDestination }}</el-form-item>
				<el-form-item label="司机姓名">{{ currentRowDetails.driverName || '待分配' }}</el-form-item>
				<el-form-item label="司机手机号">{{
					currentRowDetails.driverPhone || '待分配'
				}}</el-form-item>
				<el-form-item label="车辆类型">{{ currentRowDetails.truckTypeName }}</el-form-item>
				<el-form-item label="车辆牌号">{{
					currentRowDetails.vehiclePlateNumber || '待分配'
				}}</el-form-item>
				<el-form-item label="搬运工数量">{{ currentRowDetails.numberOfHelpers }}</el-form-item>
				<el-form-item label="搬运工人">
					<span v-if="currentRowDetails.moverList && currentRowDetails.moverList.length > 0">
						<span v-for="(mover, index) in currentRowDetails.moverList" :key="mover.id">
							{{ mover.name }} ({{ mover.phone }}){{
								index < currentRowDetails.moverList.length - 1 ? ', ' : ''
							}}
						</span>
					</span>
					<span v-else>待分配</span>
				</el-form-item>
				<el-form-item label="订单金额">{{ currentRowDetails.movingPrice }}</el-form-item>
				<el-form-item label="支付状态">
					<el-tag :type="getPaymentStatusTagType(currentRowDetails.isPaid)">
						{{ currentRowDetails.isPaidDescription }}
					</el-tag>
				</el-form-item>
				<el-form-item label="支付时间">{{
					currentRowDetails.paymentTime || '未支付'
				}}</el-form-item>
				<el-form-item label="支付方式">{{
					currentRowDetails.payMethodDescription || '未支付'
				}}</el-form-item>
				<el-form-item label="里程费用">{{ currentRowDetails.mileageCost }}</el-form-item>
				<el-form-item label="搬运工费用">{{ currentRowDetails.helperCost }}</el-form-item>
				<el-form-item label="价格系数">{{
					currentRowDetails.categoryPriceMultiplier
				}}</el-form-item>
				<el-form-item label="备注">{{ currentRowDetails.notes || '无' }}</el-form-item>
				<el-form-item label="创建时间">{{ currentRowDetails.createTime }}</el-form-item>
				<el-form-item label="更新时间">{{ currentRowDetails.updateTime }}</el-form-item>
				<el-form-item label="取消原因">{{ currentRowDetails.cancelReason || '无' }}</el-form-item>
				<el-form-item label="取消时间">{{ currentRowDetails.cancelTime || '未取消' }}</el-form-item>
			</el-form>
			<div v-else>正在加载订单详情...</div>

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
		ElOption, // 导入 ElOption
	} from 'element-plus';
	// 导入后台订单 API，包括分页查询、详情、取消和强制完成接口
	import {
		getBackOrderListPageApi,
		getBackOrderStatusApi,
		getBackPaymentStatusApi,
		getBackOrderDetailApi,
		adminCancelOrderApi,
		forceCompleteOrderApi, // <-- 导入强制完成 API
	} from '@/api/orderApi.js';

	// 导入服务类别和货车类型 API
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
		orderNumber: '',
		orderStatus: null, // null 表示不筛选
		isPaid: null, // null 表示不筛选
		customerKeyword: '', // 用户名/姓名/手机号
		driverName: '',
		serviceItemName: '',
		categoryId: null, // 服务类别ID
		truckTypeId: null, // 车辆类型ID
		createTimeRange: null, // 日期范围选择器的绑定值 [start, end]
		reservationTimeRange: null, // 预约时间范围选择器的绑定值 [start, end]
	});

	// 订单状态和支付状态选项 (用于下拉框)
	const orderStatusOptions = ref([]);
	const paymentStatusOptions = ref([]);
	// 服务类别和货车类型选项 (用于下拉框)
	const serviceCategoryOptions = ref([]);
	const truckTypeOptions = ref([]);

	// 详情弹窗状态和当前选中行数据
	const dialogVisible = ref(false); // 控制详情弹窗显示
	const currentRowDetails = ref(null); // 存储当前查看详情的行数据

	// === 数据获取方法 ===

	// 获取订单状态列表
	const fetchOrderStatusOptions = async () => {
		try {
			const { data: res } = await getBackOrderStatusApi();
			if (res.code === 1 && res.data) {
				orderStatusOptions.value = res.data;
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || '获取订单状态失败'); // <-- 移除此行
				console.warn('获取订单状态业务失败:', res.msg); // 可以保留日志
			}
		} catch (error) {
			// 捕获真正的请求错误
			console.error('获取订单状态请求失败:', error);
			ElMessage.error('获取订单状态失败'); // <-- 这个用于网络或HTTP错误
		}
	};

	// 获取支付状态列表
	const fetchPaymentStatusOptions = async () => {
		try {
			const { data: res } = await getBackPaymentStatusApi();
			if (res.code === 1 && res.data) {
				paymentStatusOptions.value = res.data;
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || '获取支付状态失败'); // <-- 移除此行
				console.warn('获取支付状态业务失败:', res.msg); // 可以保留日志
			}
		} catch (error) {
			// 捕获真正的请求错误
			console.error('获取支付状态请求失败:', error);
			ElMessage.error('获取支付状态失败'); // <-- 这个用于网络或HTTP错误
		}
	};

	// 获取服务类别列表
	const fetchServiceCategoryOptions = async () => {
		try {
			const { data: res } = await getBackServiceCategoryListApi();
			if (res.code === 1 && res.data) {
				serviceCategoryOptions.value = res.data;
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || '获取服务类别失败'); // <-- 移除此行
				console.warn('获取服务类别业务失败:', res.msg); // 可以保留日志
			}
		} catch (error) {
			// 捕获真正的请求错误
			console.error('获取服务类别请求失败:', error);
			ElMessage.error('获取服务类别失败'); // <-- 这个用于网络或HTTP错误
		}
	};

	// 获取货车类型列表
	const fetchTruckTypeOptions = async () => {
		try {
			const { data: res } = await getBackTruckTypeListApi();
			if (res.code === 1 && res.data) {
				truckTypeOptions.value = res.data;
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || '获取货车类型失败'); // <-- 移除此行
				console.warn('获取货车类型业务失败:', res.msg); // 可以保留日志
			}
		} catch (error) {
			// 捕获真正的请求错误
			console.error('获取货车类型请求失败:', error);
			ElMessage.error('获取货车类型失败'); // <-- 这个用于网络或HTTP错误
		}
	};

	const fetchOrderList = async () => {
		loading.value = true;
		try {
			// 准备请求参数
			const params = {
				page: pagination.value.page,
				pageSize: pagination.value.pageSize,
				orderNumber: searchForm.value.orderNumber || undefined,
				orderStatus:
					searchForm.value.orderStatus !== null ? searchForm.value.orderStatus : undefined,
				isPaid: searchForm.value.isPaid !== null ? searchForm.value.isPaid : undefined,
				customerKeyword: searchForm.value.customerKeyword || undefined,
				driverName: searchForm.value.driverName || undefined,
				serviceItemName: searchForm.value.serviceItemName || undefined,
				categoryId: searchForm.value.categoryId || undefined,
				truckTypeId: searchForm.value.truckTypeId || undefined,
				createTimeStart: searchForm.value.createTimeRange
					? searchForm.value.createTimeRange[0]
					: undefined,
				createTimeEnd: searchForm.value.createTimeRange
					? searchForm.value.createTimeRange[1]
					: undefined,
				reservationTimeStart: searchForm.value.reservationTimeRange
					? searchForm.value.reservationTimeRange[0]
					: undefined,
				reservationTimeEnd: searchForm.value.reservationTimeRange
					? searchForm.value.reservationTimeRange[1]
					: undefined,
			};

			const { data: res } = await getBackOrderListPageApi(params); // 调用后台订单分页 API

			if (res.code === 1) {
				tableData.value = res.data.records;
				pagination.value.total = res.data.total;
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || '获取订单列表失败'); // <-- 移除此行
				console.warn('获取订单列表业务失败:', res.msg); // 可以保留日志
				tableData.value = [];
				pagination.value.total = 0;
			}
		} catch (error) {
			// 捕获真正的请求错误
			console.error('获取订单列表请求失败:', error);
			ElMessage.error('获取订单列表失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
			tableData.value = [];
			pagination.value.total = 0;
		} finally {
			loading.value = false;
		}
	};

	// === 搜索相关方法 ===
	const handleSearch = () => {
		pagination.value.page = 1; // 从第一页开始搜索
		fetchOrderList();
	};

	const resetSearchForm = () => {
		searchForm.value = {
			orderNumber: '',
			orderStatus: null,
			isPaid: null,
			customerKeyword: '',
			driverName: '',
			serviceItemName: '',
			categoryId: null,
			truckTypeId: null,
			createTimeRange: null,
			reservationTimeRange: null,
		};
		handleSearch(); // 重置后立即执行查询，回到第一页
	};

	// === 分页相关方法 ===
	const handleSizeChange = (val) => {
		pagination.value.pageSize = val;
		pagination.value.page = 1; // 切换每页大小时回到第一页
		fetchOrderList();
	};

	const handleCurrentChange = (val) => {
		pagination.value.page = val;
		fetchOrderList();
	};

	// === 订单详情相关方法 ===
	const showDetails = async (row) => {
		dialogVisible.value = true;
		// 在加载新的详情数据前，清空旧的数据，以免显示陈旧信息
		currentRowDetails.value = null;

		try {
			// 调用后台订单详情 API 根据订单ID获取完整的详情数据
			const { data: res } = await getBackOrderDetailApi(row.id);

			if (res.code === 1 && res.data) {
				currentRowDetails.value = res.data; // 使用详情接口返回的数据更新 currentRowDetails
			} else {
				// 业务失败 (code !== 1) 或数据不存在，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || '获取订单详情失败'); // <-- 移除此行
				console.warn('获取订单详情业务失败:', res.msg); // 可以保留日志
				dialogVisible.value = false; // 获取失败则关闭弹窗
			}
		} catch (error) {
			// 捕获真正的请求错误
			console.error('获取订单详情请求失败:', error);
			ElMessage.error('获取订单详情失败'); // <-- 这个用于网络或HTTP错误
			dialogVisible.value = false; // 请求失败则关闭弹窗
		}
	};

	// === 辅助方法：获取订单状态和支付状态对应的 Tag 类型 ===
	const getOrderStatusTagType = (status) => {
		switch (status) {
			case 0:
				return 'info'; // 待接单
			case 1:
				return ''; // 司机已接单 (默认颜色)
			case 2:
				return 'primary'; // 已接单（团队） - 可以考虑不同的颜色
			case 3:
				return 'warning'; // 进行中
			case 4:
				return 'success'; // 已完成
			case 5:
				return 'danger'; // 已取消
			default:
				return 'info';
		}
	};

	const getPaymentStatusTagType = (status) => {
		switch (status) {
			case 0:
				return 'danger'; // 未支付
			case 1:
				return 'success'; // 已支付
			case 2:
				return 'info'; // 已退款 (如果支持退款状态)
			default:
				return 'info';
		}
	};

	// === 取消订单相关方法 ===
	const handleCancelOrder = (row) => {
		// 检查订单状态，如果已完成或已取消则不能再次取消
		if (row.orderStatus === 4) {
			ElMessage.warning('订单已完成，无法取消。');
			return;
		}
		if (row.orderStatus === 5) {
			ElMessage.warning('订单已取消，无需重复操作。');
			return;
		}

		ElMessageBox.prompt(`请输入取消订单【订单号：${row.orderNumber}】的原因`, '取消订单', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			inputType: 'textarea', // 使用文本域方便输入多行原因
			inputPlaceholder: '请输入取消原因 (必填)',
			inputValidator: (value) => {
				// 校验输入是否为空
				if (!value || value.trim() === '') {
					return '取消原因不能为空';
				}
				return true;
			},
		})
			.then(async ({ value }) => {
				// 用户点击确定并输入了原因
				try {
					const { data: res } = await adminCancelOrderApi(row.id, value);

					if (res.code === 1) {
						ElMessage.success(`订单 ${row.orderNumber} 已成功取消！`);
						fetchOrderList(); // 刷新列表以显示最新的订单状态
						// 如果当前详情弹窗打开且是该订单，刷新详情
						if (
							dialogVisible.value &&
							currentRowDetails.value &&
							currentRowDetails.value.id === row.id
						) {
							showDetails(row); // 刷新详情数据
						}
					} else {
						// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
						// ElMessage.error(res.msg || '取消订单失败，请稍后再试'); // <-- 移除此行
						console.warn('取消订单业务失败:', res.msg); // 可以保留日志
					}
				} catch (error) {
					// 捕获真正的请求错误
					console.error('取消订单请求失败:', error);
					ElMessage.error('取消订单失败，请检查网络或权限'); // <-- 这个用于网络或HTTP错误
				}
			})
			.catch(() => {
				// 用户点击取消或关闭对话框
				ElMessage.info('取消操作已取消');
			});
	};

	// === 强制完成订单的方法 ===
	const handleForceComplete = (row) => {
		// 检查订单状态，只有未完成且未取消的订单才能强制完成
		if (row.orderStatus === 4) {
			ElMessage.warning('订单已是已完成状态，无需重复操作。');
			return;
		}
		if (row.orderStatus === 5) {
			ElMessage.warning('订单已取消，无法强制完成。');
			return;
		}
		// 还可以根据需要添加其他状态的检查，比如是否已接单等，但这取决于业务逻辑。
		// 如果订单还在待接单(0)或已接单(1, 2)状态就强制完成，可能会有业务上的问题。
		// 假设这里允许对进行中(3)的订单进行强制完成。
		// 根据你的模板代码，按钮只在状态为 3 时启用，这符合假设。

		ElMessageBox.confirm(
			`确定要强制完成订单【订单号：${row.orderNumber}】吗？强制完成后订单将变为已完成状态。`,
			'强制完成订单',
			{
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning', // 使用警告类型弹窗
			}
		)
			.then(async () => {
				// 用户确认强制完成
				try {
					const { data: res } = await forceCompleteOrderApi(row.id); // 调用强制完成 API
					if (res.code === 1) {
						ElMessage.success(`订单 ${row.orderNumber} 已成功强制完成！`);
						fetchOrderList(); // 刷新列表以显示最新的订单状态
						// 如果当前详情弹窗打开且是该订单，刷新详情
						if (
							dialogVisible.value &&
							currentRowDetails.value &&
							currentRowDetails.value.id === row.id
						) {
							// 刷新详情数据，可能需要重新获取详情才能显示最新的状态和完成时间等信息
							showDetails(row);
						}
					} else {
						// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
						// ElMessage.error(res.msg || '强制完成订单失败'); // <-- 移除此行
						console.warn('强制完成订单业务失败:', res.msg); // 可以保留日志
					}
				} catch (error) {
					// 捕获真正的请求错误
					console.error('强制完成订单请求失败:', error);
					ElMessage.error('强制完成订单失败，请检查网络或权限'); // <-- 这个用于网络或HTTP错误
				}
			})
			.catch(() => {
				// 用户点击取消或关闭对话框
				ElMessage.info('已取消强制完成操作');
			});
	};

	// === 组件挂载后，首次加载数据和选项 ===
	onMounted(() => {
		fetchOrderStatusOptions(); // 获取订单状态选项
		fetchPaymentStatusOptions(); // 获取支付状态选项
		fetchServiceCategoryOptions(); // 获取服务类别选项
		fetchTruckTypeOptions(); // 获取货车类型选项
		fetchOrderList(); // 获取订单列表
	});
</script>

<style scoped lang="less">
	.admin-order-list-container {
		padding: 20px;
		background-color: #fff;
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
			background-color: #f8f8f8;
			border-radius: 6px;

			display: flex;
			flex-wrap: wrap; /* 允许换行 */
			align-items: center; /* 垂直居中所有表单项（包括日期选择器和按钮） */
			gap: 10px 20px; /* 行和列之间的间距 */

			.input-items-group {
				display: flex;
				flex-wrap: wrap;
				align-items: center;
				gap: 10px 20px; /* 组内的行和列间距 */
				// 如果希望每个组独立占据一行：
				flex-basis: 100%; // 让组占据整行宽度
				margin-bottom: 0; // 组间的垂直间距由 .search-form 的 gap 控制
			}

			.el-form-item {
				flex-grow: 0; // 不让表单项拉伸填充空间
				flex-shrink: 1; // 允许表单项缩小
				flex-basis: auto; // 根据内容确定初始大小
				margin: 0 !important; /* 清除 el-form-item 默认的 margin，使用 gap 控制间距 */

				// 确保标签不会挤压内容
				:deep(.el-form-item__label) {
					flex-shrink: 0; // 防止标签缩小
				}

				// === 让输入框、选择器等组件的宽度由内容决定 ===
				:deep(.el-form-item__content) {
					flex-grow: 0; // 内容区域也不拉伸
					width: auto; // 宽度由内部元素决定
					display: flex; // 使用 flex 布局，让 input/select 更好地根据内容自适应
					align-items: center; // 循环居中内容
				}

				:deep(.el-input),
				:deep(.el-select) {
					width: auto !important;
					/* 覆盖掉可能的 100% 宽度 */
					min-width: 150px;
					/* 设置一个最小宽度，防止过窄 */
				}

				/* 单独为 el-date-editor 设置固定宽度 */
				:deep(.el-date-editor--datetimerange) {
					width: 380px !important;
					/* 保持日期范围选择器的固定宽度 */
					min-width: 380px !important; // 确保 min-width 不会影响
				}
			}

			// 调整 search-form 直接子元素的 el-form-item 的垂直间距 (如果使用了 gap，这些可能不再需要显式设置)
			> .el-form-item {
				// margin-bottom: 10px; // 在日期选择器和按钮组下方添加 10px 垂直间距
				// margin-right: 20px; // 在日期选择器后添加 20px 水平间距
				flex-shrink: 0; // 防止日期选择器或按钮组缩小
			}

			.button-group {
				margin-left: auto;
				/* 将按钮组推到最右边 */
				margin-right: 0 !important;
				/* 确保没有右侧 margin */
				// margin-bottom: 10px; /* 如果使用了 gap，这里可能不需要 margin-bottom */
				flex-shrink: 0; // 防止按钮组缩小

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
				// 移除默认的 justify-content: center; 让内容根据 align 属性或默认行为对齐

				&.centered-cell {
					/* 示例：如果你想让某些列的内容居中 */
					justify-content: center;
				}
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
				.el-form-item {
					margin-bottom: 10px;
					/* 详情项下方间距可以小一些 */
					.el-form-item__label {
						font-weight: bold;
						color: #555;
						width: 120px !important;
						text-align: right;
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
					}
					.el-form-item__label-wrap {
						margin-left: 0 !important;
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
