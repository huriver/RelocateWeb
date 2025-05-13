<template>
	<div class="my-orders-container">
		<h2 class="page-title">我的订单</h2>

		<div class="filter-section">
			<el-select
				v-model="orderFilterPagination.orderStatus"
				placeholder="筛选订单状态"
				clearable
				size="default"
				style="width: 200px"
				@change="handleStatusChange"
			>
				<el-option label="全部状态" value=""></el-option>
				<el-option
					v-for="status in orderStatusOptions"
					:key="status.code"
					:label="status.description"
					:value="status.code"
				>
				</el-option>
			</el-select>
		</div>

		<el-table
			v-loading="isLoading"
			:data="orders"
			style="width: 100%"
			border
			stripe
			empty-text="暂无订单数据"
			header-align="center"
			class="order-table"
		>
			<el-table-column prop="orderNumber" label="订单号" width="188" align="center" />
			<el-table-column prop="serviceName" label="服务名称" min-width="180" align="center" />
			<el-table-column prop="reservationTime" label="预约时间" width="160" align="center" />
			<el-table-column
				prop="movingOrigin"
				label="起始地点"
				min-width="200"
				align="center"
				show-overflow-tooltip
			/>
			<el-table-column
				prop="movingDestination"
				label="目的地点"
				min-width="200"
				align="center"
				show-overflow-tooltip
			/>
			<el-table-column prop="movingPrice" label="订单金额 (元)" width="120" align="center">
				<template #default="{ row }">
					<span class="price-display">￥{{ row.movingPrice.toFixed(2) }}</span>
				</template>
			</el-table-column>
			<el-table-column prop="isPaidDescription" label="支付状态" width="100" align="center" />
			<el-table-column prop="orderStatusDescription" label="订单状态" width="120" align="center">
				<template #default="{ row }">
					<el-tag :type="getOrderStatusTagType(row.orderStatus)">{{
						row.orderStatusDescription
					}}</el-tag>
				</template>
			</el-table-column>
			<el-table-column label="操作" width="250" fixed="right" align="center">
				<template #default="{ row }">
					<el-button link type="primary" @click="viewOrderDetail(row.id)"> 查看详情 </el-button>
					<el-button
						v-if="canPayOrder(row)"
						link
						type="success"
						@click="handlePayOrder(row.id, row.orderNumber)"
					>
						去支付
					</el-button>
					<el-button
						v-if="canCancelOrder(row.orderStatus)"
						link
						type="danger"
						@click="showCancelOrderDialog(row.id)"
					>
						取消订单
					</el-button>
				</template>
			</el-table-column>
		</el-table>

		<div class="pagination-footer">
			<el-pagination
				v-model:current-page="orderFilterPagination.page"
				v-model:page-size="orderFilterPagination.pageSize"
				:page-sizes="[5, 10, 15, 20]"
				layout="total, sizes, prev, pager, next, jumper"
				:total="orderFilterPagination.total"
				@size-change="handleSizeChange"
				@current-change="handlePageChange"
			/>
		</div>

		<el-dialog
			v-model="showDetailDialog"
			title="订单详情"
			width="60%"
			class="order-detail-dialog"
			:before-close="handleDetailDialogClose"
		>
			<el-skeleton :rows="5" animated v-if="!currentOrderDetail && isDetailLoading" />
			<div v-else-if="currentOrderDetail">
				<el-descriptions :column="2" border>
					<el-descriptions-item label="订单号">{{
						currentOrderDetail.orderNumber
					}}</el-descriptions-item>
					<el-descriptions-item label="服务名称">{{
						currentOrderDetail.serviceName
					}}</el-descriptions-item>
					<el-descriptions-item label="预约时间">{{
						currentOrderDetail.reservationTime
					}}</el-descriptions-item>
					<el-descriptions-item label="订单状态">
						<el-tag :type="getOrderStatusTagType(currentOrderDetail.orderStatus)">
							{{ currentOrderDetail.orderStatusDescription }}
						</el-tag>
					</el-descriptions-item>
					<el-descriptions-item label="起始地点">{{
						currentOrderDetail.movingOrigin
					}}</el-descriptions-item>
					<el-descriptions-item label="目的地点">{{
						currentOrderDetail.movingDestination
					}}</el-descriptions-item>
					<el-descriptions-item label="订单金额">
						<span class="price-display">￥{{ currentOrderDetail.movingPrice.toFixed(2) }}</span>
					</el-descriptions-item>
					<el-descriptions-item label="支付状态">{{
						currentOrderDetail.isPaidDescription
					}}</el-descriptions-item>
					<el-descriptions-item label="支付方式">{{
						currentOrderDetail.payMethodDescription
					}}</el-descriptions-item>
					<el-descriptions-item label="付款时间">{{
						currentOrderDetail.paymentTime || 'N/A'
					}}</el-descriptions-item>
					<el-descriptions-item label="工人数量"
						>{{ currentOrderDetail.numberOfHelpers }}人</el-descriptions-item
					>
					<el-descriptions-item label="里程费用"
						>{{ currentOrderDetail.mileageCost.toFixed(2) }}元</el-descriptions-item
					>
					<el-descriptions-item label="工人费用"
						>{{ currentOrderDetail.helperCost.toFixed(2) }}元</el-descriptions-item
					>
					<el-descriptions-item label="类型价格乘数">{{
						currentOrderDetail.categoryPriceMultiplier.toFixed(2)
					}}</el-descriptions-item>
					<el-descriptions-item label="是否已评价">{{
						currentOrderDetail.isReviewed ? '是' : '否'
					}}</el-descriptions-item>
					<el-descriptions-item label="司机姓名" v-if="currentOrderDetail.driverName">{{
						currentOrderDetail.driverName
					}}</el-descriptions-item>
					<el-descriptions-item label="司机电话" v-if="currentOrderDetail.driverPhone">{{
						currentOrderDetail.driverPhone
					}}</el-descriptions-item>
					<el-descriptions-item label="车牌号" v-if="currentOrderDetail.vehiclePlateNumber">{{
						currentOrderDetail.vehiclePlateNumber
					}}</el-descriptions-item>
					<el-descriptions-item label="搬运工">
						<span v-if="currentOrderDetail.moverList && currentOrderDetail.moverList.length > 0">
							{{ currentOrderDetail.moverList.map((m) => m.name).join(', ') }}
						</span>
						<span v-else>无</span>
					</el-descriptions-item>
					<el-descriptions-item label="客户备注">{{
						currentOrderDetail.notes || '无'
					}}</el-descriptions-item>
					<el-descriptions-item label="取消原因" v-if="currentOrderDetail.cancelReason">{{
						currentOrderDetail.cancelReason
					}}</el-descriptions-item>
					<el-descriptions-item label="取消时间" v-if="currentOrderDetail.cancelTime">{{
						currentOrderDetail.cancelTime
					}}</el-descriptions-item>
					<el-descriptions-item label="创建时间">{{
						currentOrderDetail.createTime
					}}</el-descriptions-item>
					<el-descriptions-item label="更新时间">{{
						currentOrderDetail.updateTime
					}}</el-descriptions-item>
				</el-descriptions>
			</div>
			<div v-else class="dialog-empty">
				<el-empty description="无法加载订单详情或无数据" />
			</div>
			<template #footer>
				<span class="dialog-footer">
					<el-button
						v-if="currentOrderDetail && canPayOrder(currentOrderDetail)"
						link
						type="success"
						@click="handlePayOrder(currentOrderDetail.id, currentOrderDetail.orderNumber)"
					>
						去支付
					</el-button>
					<el-button
						v-if="currentOrderDetail && canCancelOrder(currentOrderDetail.orderStatus)"
						link
						type="danger"
						@click="showCancelOrderDialog(currentOrderDetail.id)"
					>
						取消订单
					</el-button>
					<el-button
						v-if="currentOrderDetail && canReviewOrder(currentOrderDetail)"
						link
						type="primary"
						@click="handleReviewOrder(currentOrderDetail.id)"
					>
						立即评价
					</el-button>
					<el-button @click="showDetailDialog = false">关闭</el-button>
				</span>
			</template>
		</el-dialog>

		<el-dialog
			v-model="showCancelDialog"
			title="取消订单"
			width="400px"
			class="cancel-order-dialog"
			:before-close="handleCancelDialogClose"
		>
			<el-form>
				<el-form-item>
					<el-input
						v-model="cancelReason"
						type="textarea"
						:rows="4"
						placeholder="请输入取消原因（选填）"
						maxlength="200"
						show-word-limit
						class="cancel-reason-input"
					></el-input>
				</el-form-item>
			</el-form>
			<template #footer>
				<div class="dialog-footer">
					<el-button @click="showCancelDialog = false" :disabled="isCancelling" class="cancel-btn">
						取消
					</el-button>
					<el-button
						type="danger"
						@click="confirmCancelOrder"
						:loading="isCancelling"
						class="confirm-btn"
					>
						确认取消
					</el-button>
				</div>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
	import { ref, onMounted, watch } from 'vue';
	import { useRouter } from 'vue-router';
	import {
		queryHistoryOrdersApi,
		getOrderDetailApi,
		cancelOrderApi,
		getOrderStatusApi,
	} from '@/api/orderApi.js'; // 确保路径正确
	import {
		ElMessage,
		ElMessageBox,
		ElTable,
		ElTableColumn,
		ElPagination,
		ElButton,
		ElSelect,
		ElOption,
		ElTag,
		ElDialog,
		ElDescriptions,
		ElDescriptionsItem,
		ElSkeleton,
		ElEmpty,
		ElForm,
		ElFormItem,
		ElInput,
	} from 'element-plus';

	// 定义组件名称，用于 keep-alive 等场景
	import { defineOptions } from 'vue';
	defineOptions({
		name: 'UserMyOrders',
	});

	const router = useRouter();

	// --- 订单列表和分页状态 ---
	const orders = ref([]);
	const isLoading = ref(false); // 列表加载状态
	const orderFilterPagination = ref({
		page: 1,
		pageSize: 10,
		total: 0,
		orderStatus: '', // 筛选状态，默认为空字符串（全部）
	});
	const orderStatusOptions = ref([]); // 订单状态下拉选项

	// --- 订单详情对话框状态 ---
	const showDetailDialog = ref(false);
	const currentOrderDetail = ref(null); // 当前查看的订单详情
	const isDetailLoading = ref(false); // 详情加载状态

	// --- 取消订单对话框状态 ---
	const showCancelDialog = ref(false);
	const cancelReason = ref('');
	const currentOrderIdToCancel = ref(null); // 要取消的订单ID
	const isCancelling = ref(false); // 取消操作加载状态

	// --- 常量定义：订单状态对应的ElTag类型 ---
	const ORDER_STATUS_TAG_TYPES = {
		0: 'info', // 待接单
		1: 'warning', // 司机已接单，等待搬运工人
		2: 'warning', // 已接单（团队已确认）
		3: '', // 进行中 (默认蓝色)
		4: 'success', // 已完成
		5: 'danger', // 已取消
	};

	// --- 生命周期钩子 ---
	onMounted(() => {
		fetchOrderStatusOptions(); // 获取订单状态列表
		queryOrders(); // 首次加载订单列表
	});

	// --- 订单状态筛选变化 ---
	const handleStatusChange = () => {
		orderFilterPagination.value.page = 1; // 筛选条件改变时重置页码
		queryOrders();
	};

	// --- 获取订单状态列表 ---
	const fetchOrderStatusOptions = async () => {
		try {
			const { data: res } = await getOrderStatusApi();
			if (res.code === 1) {
				orderStatusOptions.value = res.data;
			} else {
				ElMessage.error(res.msg || '获取订单状态失败！');
			}
		} catch (error) {
			console.error('获取订单状态 API 调用失败:', error);
			ElMessage.error('获取订单状态失败，请检查网络。');
		}
	};

	// --- 查询订单列表 ---
	const queryOrders = async () => {
		isLoading.value = true;
		try {
			const params = {
				page: orderFilterPagination.value.page,
				pageSize: orderFilterPagination.value.pageSize,
			};
			if (orderFilterPagination.value.orderStatus !== '') {
				params.orderStatus = orderFilterPagination.value.orderStatus;
			}

			const { data: res } = await queryHistoryOrdersApi(params);
			if (res.code === 1) {
				orders.value = res.data.records || [];
				orderFilterPagination.value.total = res.data.total || 0;
			} else {
				ElMessage.error(res.msg || '查询订单失败！');
				orders.value = [];
				orderFilterPagination.value.total = 0;
			}
		} catch (error) {
			console.error('查询订单 API 调用失败:', error);
			ElMessage.error('查询订单失败，请检查网络或稍后再试。');
			orders.value = [];
			orderFilterPagination.value.total = 0;
		} finally {
			isLoading.value = false;
		}
	};

	// --- 分页处理 ---
	const handlePageChange = (newPage) => {
		orderFilterPagination.value.page = newPage;
		queryOrders();
	};

	const handleSizeChange = (newSize) => {
		orderFilterPagination.value.pageSize = newSize;
		orderFilterPagination.value.page = 1; // 每页大小改变时重置到第一页
		queryOrders();
	};

	// --- 订单状态对应的 ElTag 类型 ---
	const getOrderStatusTagType = (status) => {
		return ORDER_STATUS_TAG_TYPES[status] || 'info'; // Fallback to 'info' if status is unknown
	};

	// --- 查看订单详情 ---
	const viewOrderDetail = async (orderId) => {
		showDetailDialog.value = true;
		isDetailLoading.value = true;
		currentOrderDetail.value = null; // 清空上次的详情
		try {
			const { data: res } = await getOrderDetailApi(orderId);
			if (res.code === 1) {
				currentOrderDetail.value = res.data;
				// ====== 添加这两行 DEBUG 代码 ======
				console.log('获取到的订单详情数据:', currentOrderDetail.value);
				console.log('尝试获取的订单ID (currentOrderDetail.id):', currentOrderDetail.value.id);
				// ================================
			} else {
				ElMessage.error(res.msg || '获取订单详情失败！');
			}
		} catch (error) {
			console.error('获取订单详情 API 调用失败:', error);
			ElMessage.error('获取订单详情失败，请检查网络或稍后再试。');
		} finally {
			isDetailLoading.value = false;
		}
	};

	const handleDetailDialogClose = () => {
		currentOrderDetail.value = null; // 关闭时清空详情
		showDetailDialog.value = false;
	};

	// --- 判断是否可以支付订单 ---
	const canPayOrder = (order) => {
		// 订单未支付 (isPaid 为 false) 且订单状态为 待接单(0)时可以支付
		// 这样可以避免已完成或已取消的订单显示支付按钮
		return !order.isPaid && [0].includes(order.orderStatus);
	};

	// --- 处理支付订单操作 ---
	const handlePayOrder = (orderId, orderNumber) => {
		ElMessage.info(`正在跳转至支付页面，订单号: ${orderNumber}。`);
		// 跳转到 UserOrder.vue 页面，并传递订单ID
		router.push({
			name: 'userOrder', // 确保 'userOrder' 是您 UserOrder.vue 文件的路由名称
			query: { existingOrderId: orderId }, // 将订单 ID 作为查询参数传递
		});
	};

	// --- 判断是否可以取消订单 ---
	const canCancelOrder = (orderStatus) => {
		// 待接单 (0), 司机已接单 (1), 已接单 (2) 可以取消
		return [0, 1, 2].includes(orderStatus);
	};

	// --- 显示取消订单对话框 ---
	const showCancelOrderDialog = (orderId) => {
		currentOrderIdToCancel.value = orderId;
		cancelReason.value = ''; // 清空上次的取消原因
		showCancelDialog.value = true;
	};

	const handleCancelDialogClose = () => {
		currentOrderIdToCancel.value = null;
		showCancelDialog.value = false;
	};

	// --- 确认取消订单 ---
	const confirmCancelOrder = async () => {
		if (!currentOrderIdToCancel.value) {
			ElMessage.error('订单ID缺失，无法取消。');
			return;
		}

		try {
			isCancelling.value = true;
			// 如果没有输入取消原因，后端可能需要空字符串或 null
			const reason = cancelReason.value.trim() === '' ? null : cancelReason.value.trim();
			const { data: res } = await cancelOrderApi(currentOrderIdToCancel.value, {
				cancelReason: reason,
			});

			if (res.code === 1) {
				ElMessage.success('订单已成功取消！');
				showCancelDialog.value = false;
				queryOrders(); // 取消成功后刷新订单列表
			} else {
				ElMessage.error(res.msg || '取消订单失败，请稍后再试。');
			}
		} catch (error) {
			console.error('取消订单 API 调用失败:', error);
			ElMessage.error('取消订单失败，请检查网络或稍后再试。');
		} finally {
			isCancelling.value = false;
		}
	};

	// --- 判断是否可以评价订单 ---
	const canReviewOrder = (order) => {
		// 订单状态为“已完成” (4) 且 isReviewed 为 false 时可以评价
		return order.orderStatus === 4 && !order.isReviewed;
	};

	// --- 处理评价订单操作 ---
	const handleReviewOrder = (orderId) => {
		// 这里我们选择跳转到新的评价页面，并传递订单ID作为查询参数
		router.push({
			name: 'userOrderRating', // <-- 修改这里，跳转到新的评价路由
			params: { orderId: orderId }, // <-- **将 query 改为 params**，传递订单ID作为路径参数
		});
		ElMessage.info(`即将跳转到评价页面对订单 ${orderId} 进行评价。`);
	};

	// 监听分页或筛选条件变化，重新查询订单
	watch(
		() => [orderFilterPagination.value.page, orderFilterPagination.value.pageSize],
		() => {
			queryOrders();
		},
		{ deep: true } // 深度监听对象内部属性变化
	);
</script>

<style scoped lang="less">
	.cancel-order-dialog {
		:deep(.el-dialog__header) {
			margin: 0;
			padding: 16px 20px;
			border-bottom: 1px solid #f0f0f0;

			.el-dialog__title {
				font-size: 16px;
				font-weight: 500;
				color: #333;
			}

			.el-dialog__headerbtn {
				top: 16px;
			}
		}

		:deep(.el-dialog__body) {
			padding: 20px;
		}

		.cancel-reason-input {
			:deep(.el-textarea__inner) {
				border-radius: 4px;
				border-color: #e0e0e0;
				box-shadow: none;
				transition: border-color 0.2s;

				&:focus {
					border-color: #f16622;
				}
			}

			:deep(.el-input__count) {
				background: transparent;
				bottom: 5px;
				right: 5px;
			}
		}

		.dialog-footer {
			display: flex;
			justify-content: flex-end;
			gap: 12px;
			padding: 10px 20px;
			border-top: 1px solid #f0f0f0;

			.cancel-btn {
				border: 1px solid #dcdfe6;
				background-color: #fff;
				color: #606266;

				&:hover {
					color: #f16622;
					border-color: #f8d5c6;
					background-color: #fff8f5;
				}
			}

			.confirm-btn {
				background-color: #f16622;
				border-color: #f16622;

				&:hover {
					background-color: #f57a3d;
					border-color: #f57a3d;
				}
			}
		}
	}

	.my-orders-container {
		padding: 20px;
		background-color: #fff;
		border-radius: 8px;
		box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);

		.page-title {
			font-size: 24px;
			color: #333;
			margin-bottom: 20px;
			text-align: center;
		}

		.filter-section {
			margin-bottom: 20px;
			text-align: right; /* 筛选器靠右对齐 */
		}

		.order-table {
			margin-top: 15px;
			font-size: 14px; /* 减小字体，适应更多内容 */

			:deep(.el-table__header) {
				th {
					background-color: #f8f8f8;
					color: #666;
					font-weight: bold;
				}
			}

			.price-display {
				font-weight: bold;
				color: #f16622;
			}

			.el-button + .el-button {
				margin-left: 8px; /* 调整按钮间距 */
			}
		}

		.pagination-footer {
			margin-top: 20px;
			display: flex;
			justify-content: center;
		}

		/* 订单详情对话框样式 */
		.order-detail-dialog {
			.el-descriptions {
				margin-top: 10px;
				background-color: #fcfcfc;
				border-radius: 4px;

				:deep(.el-descriptions__title) {
					font-size: 18px;
					font-weight: bold;
					color: #333;
					margin-bottom: 15px;
				}

				:deep(.el-descriptions__header) {
					padding: 10px 20px;
					background-color: #f2f2f2;
					border-bottom: 1px solid #ebebeb;
					border-top-left-radius: 4px;
					border-top-right-radius: 4px; /* 修正：从 44px 改为 4px */
				}

				:deep(.el-descriptions__body) {
					padding: 15px 20px;
				}

				.el-descriptions-item {
					padding: 10px 0;
					border-color: #ebebeb;

					:deep(.el-descriptions__label) {
						font-weight: bold;
						color: #666;
						background-color: #f9f9f9;
					}

					:deep(.el-descriptions__content) {
						color: #444;
					}
				}
			}

			.dialog-empty {
				text-align: center;
				padding: 50px;
			}
		}

		/* 取消订单对话框样式 */
		.cancel-order-dialog {
			.el-form-item {
				// margin-bottom: 0; /* 移除此行，恢复默认间距，使表单更舒展 */
			}
			.el-textarea {
				width: 100%;
			}
		}

		/* 调整操作列的宽度以适应按钮，确保不拥挤 */
		.order-table .el-table-column[label='操作'] {
			width: 250px;
		}

		/* 调整 dialog-footer 中按钮的间距和样式 */
		.dialog-footer {
			display: flex;
			justify-content: flex-end; /* 按钮靠右对齐 */
			gap: 10px; /* 按钮之间的间距 */

			.el-button {
				min-width: 80px; /* 统一按钮最小宽度 */
				padding: 8px 15px; /* 调整内边距 */
				/* font-size 保持不变，因为已经移除了 size="small" */
			}
		}
	}
</style>
