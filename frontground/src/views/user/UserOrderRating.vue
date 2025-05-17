<template>
	<div class="order-rating-container">
		<el-card class="rating-card" v-if="isOrderLoading">
			<template #header>
				<div class="card-header-title">加载订单信息...</div>
			</template>
			<el-skeleton :rows="5" animated />
		</el-card>

		<el-card class="rating-card" v-else-if="orderToRate">
			<template #header>
				<div class="card-header-title">评价订单：{{ orderToRate.orderNumber }}</div>
				<p class="card-subtitle">请对本次服务进行评价，您的反馈是我们进步的动力！</p>
			</template>

			<el-form :model="ratingForm" ref="ratingFormRef" label-width="auto" class="rating-form">
				<el-form-item
					label="服务项评价"
					prop="serviceRating.rating_value"
					:rules="[
						{
							required: true,
							type: 'number',
							min: 1,
							message: '请对服务项评分',
							trigger: 'change',
						},
					]"
				>
					<div class="rating-item-content">
						<span class="rating-label">服务项: {{ orderToRate.serviceName }}</span>
						<el-rate
							v-model="ratingForm.serviceRating.rating_value"
							:texts="['非常差', '差', '一般', '好', '非常好']"
							show-text
							class="custom-rate"
						/>
						<el-input
							v-model="ratingForm.serviceRating.comment"
							type="textarea"
							:rows="3"
							placeholder="请填写您对服务项的评价 (选填)"
							maxlength="200"
							show-word-limit
							class="comment-textarea"
						/>
					</div>
				</el-form-item>

				<el-form-item
					label="司机评价"
					v-if="orderToRate.driverName"
					prop="driverRating.rating_value"
					:rules="[
						{ required: true, type: 'number', min: 1, message: '请对司机评分', trigger: 'change' },
					]"
				>
					<div class="rating-item-content">
						<span class="rating-label">司机: {{ orderToRate.driverName }}</span>
						<el-rate
							v-model="ratingForm.driverRating.rating_value"
							:texts="['非常差', '差', '一般', '好', '非常好']"
							show-text
							class="custom-rate"
						/>
						<el-input
							v-model="ratingForm.driverRating.comment"
							type="textarea"
							:rows="3"
							placeholder="请填写您对司机的评价 (选填)"
							maxlength="200"
							show-word-limit
							class="comment-textarea"
						/>
					</div>
				</el-form-item>

				<template v-if="orderToRate.moverList && orderToRate.moverList.length > 0">
					<el-form-item
						v-for="(mover, index) in ratingForm.moverRatings"
						:key="mover.moverId"
						:label="`搬运工评价`"
						:prop="`moverRatings.${index}.rating_value`"
						:rules="[
							{
								required: true,
								type: 'number',
								min: 1,
								message: '请对搬运工评分',
								trigger: 'change',
							},
						]"
					>
						<div class="rating-item-content">
							<span class="rating-label">搬运工: {{ mover.name }}</span>
							<el-rate
								v-model="mover.rating_value"
								:texts="['非常差', '差', '一般', '好', '非常好']"
								show-text
								class="custom-rate"
							/>
							<el-input
								v-model="mover.comment"
								type="textarea"
								:rows="3"
								placeholder="请填写您对该搬运工的评价 (选填)"
								maxlength="200"
								show-word-limit
								class="comment-textarea"
							/>
						</div>
					</el-form-item>
				</template>

				<el-form-item class="submit-buttons" label-width="0px">
					<el-button type="primary" @click="submitRating" :loading="isSubmitting"
						>提交评价</el-button
					>
					<el-button @click="goBack">返回</el-button>
				</el-form-item>
			</el-form>
		</el-card>

		<el-card class="rating-card" v-else>
			<template #header>
				<div class="card-header-title">订单信息加载失败</div>
			</template>
			<el-empty description="无法加载订单详情或该订单不符合评价条件，请检查订单ID或网络。" />
			<div style="text-align: center; margin-top: 20px">
				<el-button type="primary" @click="goBack">返回我的订单</el-button>
			</div>
		</el-card>
	</div>
</template>

<script setup>
	import { ref, onMounted } from 'vue';
	import { useRoute, useRouter } from 'vue-router';
	import { ElMessage } from 'element-plus';
	import { getFrontOrderDetailApi, submitOrderRatingApi } from '@/api/orderApi.js';

	const route = useRoute();
	const router = useRouter();

	const orderToRate = ref(null);
	const isOrderLoading = ref(false);
	const isSubmitting = ref(false);
	const ratingFormRef = ref(null);

	const ratingForm = ref({
		orderId: null,
		serviceRating: { rating_value: 0, comment: '' },
		driverRating: { rating_value: 0, comment: '' },
		moverRatings: [],
	});

	onMounted(() => {
		const orderId = route.params.orderId;
		if (orderId) {
			ratingForm.value.orderId = orderId;
			fetchOrderDetail(orderId);
		} else {
			ElMessage.error('未提供订单ID，无法进行评价。');
			goBack();
		}
	});

	const fetchOrderDetail = async (orderId) => {
		isOrderLoading.value = true;
		try {
			const { data: res } = await getFrontOrderDetailApi(orderId);
			if (res.code === 1 && res.data) {
				const order = res.data;
				// 客户端检查订单状态是否符合评价条件
				if (order.orderStatus === 4 && !order.isReviewed) {
					orderToRate.value = order;
					if (order.moverList?.length) {
						ratingForm.value.moverRatings = order.moverList.map((m) => ({
							moverId: m.id,
							name: m.name, // 存储姓名用于显示
							rating_value: 0,
							comment: '',
						}));
					}
				} else {
					// 订单不符合评价条件，给出警告并返回
					ElMessage.warning('订单已评价或不符合评价条件');
					goBack();
				}
			} else {
				// 业务失败 (code !== 1) 或数据不存在，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || '加载失败'); // <-- 移除此行
				console.warn('加载订单详情业务失败:', res.msg); // 可以保留日志
				goBack(); // 业务失败也返回
			}
		} catch (error) {
			// <-- 捕获真正的请求错误
			console.error('加载订单详情请求失败:', error);
			ElMessage.error('加载订单详情失败'); // <-- 这个用于网络或HTTP错误
			goBack(); // 请求失败也返回
		} finally {
			isOrderLoading.value = false;
		}
	};

	const submitRating = async () => {
		if (!ratingFormRef.value) return;

		// 使用单独的 try-catch 块来处理表单验证和 API 调用
		try {
			await ratingFormRef.value.validate(); // 表单验证

			// 验证通过后，再执行提交 API 调用
			isSubmitting.value = true;

			const ratings = [
				{
					// 注意：这里 ServiceRating 的 rateeId 应该使用 serviceId，如果订单数据中有的话
					// 如果 orderToRate 中有 serviceId 字段，建议使用 orderToRate.value.serviceId
					// 否则使用 orderToRate.value.id (订单ID) 可能不是后端期望的被评价对象ID
					rateeId: orderToRate.value.serviceId || orderToRate.value.id, // 假设评价对象是服务项，使用服务项ID
					ratingType: 'SERVICE',
					ratingValue: ratingForm.value.serviceRating.rating_value,
					comment: ratingForm.value.serviceRating.comment.trim() || null,
				},
			];

			if (orderToRate.value.driverId) {
				ratings.push({
					rateeId: orderToRate.value.driverId,
					ratingType: 'DRIVER',
					ratingValue: ratingForm.value.driverRating.rating_value,
					comment: ratingForm.value.driverRating.comment.trim() || null,
				});
			}

			for (const mover of ratingForm.value.moverRatings) {
				ratings.push({
					rateeId: mover.moverId, // 搬运工ID
					ratingType: 'MOVER',
					ratingValue: mover.rating_value,
					comment: mover.comment.trim() || null,
				});
			}

			try {
				// <-- 针对 API 调用的 try-catch
				const { data: res } = await submitOrderRatingApi({
					orderId: ratingForm.value.orderId,
					ratings,
				});

				if (res.code === 1) {
					ElMessage.success('评价成功！');
					// 评价成功后跳转到我的评价列表或订单列表
					router.replace({ name: 'userMyRatings' }); // 假设有我的评价页面
					// router.replace({ name: 'userMyOrders' }); // 或者返回我的订单
				} else {
					// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
					// ElMessage.error(res.msg || '提交失败'); // <-- 移除此行
					console.warn('提交评价业务失败:', res.msg); // 可以保留日志
					// 保持 loading 状态为 false
				}
			} catch (apiError) {
				// <-- 捕获 API 调用中发生的请求错误
				console.error('提交评价请求失败:', apiError);
				// request.js 已经在 rejection 时弹窗了 HTTP/网络错误，这里提供一个兜底或通用提示
				ElMessage.error('提交评价失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
			} finally {
				isSubmitting.value = false; // API 调用结束后设置 submitting 为 false
			}
		} catch (validationError) {
			// <-- 捕获表单验证错误
			// ElMessage.warning('请完善所有评分项'); // 这条警告是正确的，保留
			console.warn('表单验证失败:', validationError); // 可以保留日志
			// ElMessage 在 validate 方法失败时，字段下方会有提示，这里可以额外一个总提示
			ElMessage.warning('请完善所有必填评分项'); // 更明确的提示
			// 如果是验证失败，isSubmitting 未曾变为 true，所以这里不需要设置 isSubmitting.value = false;
		}
	};

	const goBack = () => router.replace({ name: 'userMyOrders' }); // 返回我的订单列表
</script>

<style scoped lang="less">
	.order-rating-container {
		display: flex;
		justify-content: center;
		align-items: flex-start; /* 顶部对齐 */
		min-height: calc(100vh - 60px);
		padding: 40px 0;
		background-color: #f7f9fc;
		overflow-y: auto; /* 页面可滚动 */
	}

	.rating-card {
		width: 680px;
		max-width: 90%;
		border-radius: 16px;
		box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
		background-color: #fff;
		padding: 30px 40px;
		max-height: calc(100vh - 100px); /* 限制最大高度 */
		overflow-y: auto; /* 卡片内部滚动 */
	}

	.card-header-title {
		font-size: 26px;
		font-weight: 700;
		text-align: center;
		color: #333;
		margin-bottom: 6px;
	}

	.card-subtitle {
		font-size: 14px;
		color: #666;
		text-align: center;
		margin-bottom: 20px;
	}

	.rating-form {
		:deep(.el-form-item__label) {
			font-weight: 600;
			font-size: 16px;
			color: #444;
			padding-bottom: 6px;
			text-align: right; /* 使得标签文本内容右对齐 */
		}

		:deep(.el-form-item) {
			margin-bottom: 24px;
		}

		.rating-item-content {
			display: flex;
			flex-direction: column;
			gap: 8px;
			width: 100%;
		}

		.comment-textarea {
			width: 100%;
		}

		.custom-rate :deep(.el-rate__text) {
			font-size: 14px; // Adjust size if needed
		}

		.submit-buttons {
			margin-top: 30px; // 保持与上方内容的间距

			:deep(.el-form-item__content) {
				display: flex; /* 确保flex布局 */
				justify-content: space-evenly; /* 让内部按钮水平居中 */
				align-items: center; /* 确保按钮在垂直方向上居中 */
			}

			:deep(.el-button) {
				min-width: 120px;
				margin: 0 10px;
			}
		}
	}
</style>
