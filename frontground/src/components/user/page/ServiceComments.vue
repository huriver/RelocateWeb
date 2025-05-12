<script setup>
	import { ref, onMounted } from 'vue';
	import { useRoute, useRouter } from 'vue-router'; // 导入 useRouter
	import { getServiceRatingApi } from '@/api/rating.js'; // 确保路径正确
	import {
		ElMessage,
		ElCard,
		ElRate,
		ElEmpty,
		ElSkeleton,
		ElSkeletonItem,
		ElButton,
		ElIcon,
	} from 'element-plus';
	import { ArrowLeft } from '@element-plus/icons-vue'; // 导入左箭头图标

	const route = useRoute();
	const router = useRouter(); // 初始化 useRouter 实例

	const serviceId = ref('');
	const comments = ref([]);
	const isLoading = ref(true);
	const rateColors = ref(['#99A9BF', '#F7BA2A', '#FF9900']); // 与 Service.vue 保持一致

	const fetchServiceComments = async () => {
		isLoading.value = true;
		try {
			serviceId.value = route.params.id; // 从路由参数获取服务ID
			if (!serviceId.value) {
				ElMessage.error('服务ID缺失，无法加载评论。');
				isLoading.value = false;
				return;
			}

			const { data: res } = await getServiceRatingApi(serviceId.value);
			if (res.code === 1) {
				comments.value = res.data || [];
			} else {
				ElMessage.error(res.msg || '获取评论失败！');
				comments.value = [];
			}
		} catch (error) {
			console.error('获取服务评论 API 调用失败:', error);
			ElMessage.error('获取服务评论失败，请检查网络。');
			comments.value = [];
		} finally {
			isLoading.value = false;
		}
	};

	onMounted(() => {
		fetchServiceComments();
	});

	// 新增方法：返回服务详情页面
	const goBackToServiceDetail = () => {
		router.back(); // 使用 router.back() 返回上一个页面
	};
</script>

<template>
	<div class="service-comments-container">
		<div class="header-with-back-btn">
			<el-button type="primary" link @click="goBackToServiceDetail" class="back-btn">
				<el-icon><ArrowLeft /></el-icon> 返回服务详情
			</el-button>
			<h2>服务评论</h2>
			<div style="width: 120px"></div>
		</div>
		<el-divider />

		<el-skeleton v-if="isLoading" animated :rows="5">
			<template #template>
				<el-skeleton-item variant="rect" style="width: 100%; height: 120px; margin-bottom: 20px" />
				<el-skeleton-item variant="h3" style="width: 40%; margin-bottom: 10px" />
				<el-skeleton-item variant="text" style="width: 100%" />
			</template>
		</el-skeleton>

		<template v-else>
			<div v-if="comments.length > 0" class="comments-list">
				<el-card
					v-for="comment in comments"
					:key="comment.ratingTime + comment.customerName"
					class="comment-card"
				>
					<div class="comment-header">
						<span class="customer-name">{{ comment.customerName }}</span>
						<el-rate
							:model-value="comment.ratingValue"
							:colors="rateColors"
							disabled
							show-score
							text-color="#ff9900"
							score-template="{value}"
							class="comment-rating"
						/>
					</div>
					<p class="comment-content">{{ comment.comment }}</p>
					<span class="comment-time">{{ comment.ratingTime }}</span>
				</el-card>
			</div>
			<el-empty v-else description="暂无评论数据" />
		</template>
	</div>
</template>

<style scoped lang="less">
	.service-comments-container {
		max-width: 1000px;
		margin: 20px auto;
		padding: 30px;
		background-color: #fff;
		border-radius: 8px;
		box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);

		h2 {
			color: #333;
			font-size: 24px;
			margin-bottom: 15px;
			text-align: center;
		}

		.el-divider {
			margin-bottom: 30px;
		}

		.comments-list {
			display: flex;
			flex-direction: column;
			gap: 20px;
		}

		.comment-card {
			padding: 20px;
			border-radius: 8px;
			box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);

			.comment-header {
				display: flex;
				justify-content: space-between;
				align-items: center;
				margin-bottom: 10px;

				.customer-name {
					font-weight: bold;
					color: #409eff;
					font-size: 16px;
				}

				.comment-rating {
					:deep(.el-rate__item) {
						margin-right: 2px; // 调整星级图标间距
					}
				}
			}

			.comment-content {
				color: #666;
				font-size: 15px;
				line-height: 1.6;
				margin-bottom: 10px;
			}

			.comment-time {
				font-size: 12px;
				color: #999;
				text-align: right;
				display: block;
			}
		}
	}

	.header-with-back-btn {
		display: flex;
		align-items: center;
		justify-content: space-between; // 使返回按钮、标题、占位符分散对齐
		margin-bottom: 15px;

		h2 {
			flex-grow: 1; // 允许标题占据中间大部分空间
			text-align: center; // 标题居中显示
			margin: 0; // 移除h2默认的margin
		}

		.back-btn {
			font-size: 16px;
			padding: 0; // 移除默认按钮的padding，因为是link类型
			display: flex; // 确保图标和文字对齐
			align-items: center;
		}
	}
</style>
