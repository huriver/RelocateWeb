<template>
	<div class="my-ratings-container">
		<h2 class="page-title">我的评价</h2>
		<p class="page-description">这里展示您的历史评价。</p>

		<div v-if="loading" class="loading-state">
			<el-skeleton :rows="5" animated />
			<p>正在加载评价数据...</p>
		</div>

		<el-empty v-else-if="groupedRatingsList.length === 0" description="暂无历史评价" />

		<div v-else class="ratings-list">
			<el-card
				v-for="orderRatings in groupedRatingsList"
				:key="orderRatings.orderNumber"
				class="order-rating-item"
			>
				<template #header>
					<div class="order-rating-header">
						<span>订单号: {{ orderRatings.orderNumber }}</span>
						<span class="order-service-name">{{ orderRatings.orderServiceName }}</span>
						<span class="rating-time">{{ orderRatings.ratingTime }}</span>
					</div>
				</template>
				<div class="order-rating-body">
					<div class="entity-ratings">
						<div
							v-for="entityRating in orderRatings.entityRatings"
							:key="entityRating.ratedEntityType + '_' + entityRating.ratedEntityName"
							class="entity-rating-item"
						>
							<div class="entity-info">
								<span class="entity-name">
									{{ formatEntityType(entityRating.ratedEntityType) }} -
									{{ entityRating.ratedEntityName }}
								</span>
								<el-rate
									v-model="entityRating.ratingValue"
									disabled
									show-score
									text-color="#ff9900"
									score-template="{value} 星"
								/>
							</div>
							<p class="entity-comment">
								评论: <span class="comment-text">{{ entityRating.comment || '暂无评价内容' }}</span>
							</p>
						</div>
					</div>
				</div>
			</el-card>
		</div>
	</div>
</template>

<script setup>
	import { defineOptions, ref, onMounted } from 'vue';
	import { queryHistoryRatingsApi } from '@/api/ratingApi'; // 导入查询历史评价的API
	import { ElMessage } from 'element-plus'; // 导入 ElMessage 用于错误提示
	import { ElCard, ElEmpty, ElRate, ElSkeleton } from 'element-plus'; // 导入 Element Plus 组件

	defineOptions({
		name: 'UserMyRatings',
	});

	// 存储按订单号分组后的评价列表数据
	const groupedRatingsList = ref([]);
	const loading = ref(true); // 控制加载状态

	// 格式化评价对象类型
	const formatEntityType = (type) => {
		switch (type) {
			case 'DRIVER':
				return '司机';
			case 'MOVER':
				return '搬运工';
			case 'SERVICE':
				return '整体服务'; // 或者您希望在这里单独显示服务评价，取决于具体需求
			default:
				return type;
		}
	};

	// 获取并分组历史评价数据
	const fetchHistoryRatings = async () => {
		loading.value = true; // 开始加载
		try {
			const response = await queryHistoryRatingsApi();
			console.log('Axios Response:', response); // Log the full Axios response object

			// 检查Axios响应状态码和后端业务状态码
			if (response.status === 200 && response.data && response.data.code === 1) {
				const rawRatings = response.data.data || []; // 获取原始评价列表
				console.log('Raw Ratings Data:', rawRatings);

				// 对原始评价数据按订单号进行分组
				const ratingsMap = new Map();

				rawRatings.forEach((rating) => {
					const orderNumber = rating.orderNumber;

					if (!ratingsMap.has(orderNumber)) {
						// 如果是新的订单号，创建一个新的分组项
						ratingsMap.set(orderNumber, {
							orderNumber: orderNumber,
							orderServiceName: rating.orderServiceName,
							ratingTime: rating.ratingTime, // 假设同一订单评价时间相同
							// 不再有 overallComment
							entityRatings: [], // 存放该订单下的具体评价项
						});
					}

					// 将当前的评价项添加到对应订单的分组中，包括其独立的评论
					ratingsMap.get(orderNumber).entityRatings.push({
						ratedEntityType: rating.ratedEntityType,
						ratedEntityName: rating.ratedEntityName,
						ratingValue: rating.ratingValue,
						comment: rating.comment, // 包含该评价对象的独立评论
					});
				});

				// 将 Map 的值（分组后的评价对象）转换为数组，供模板渲染
				groupedRatingsList.value = Array.from(ratingsMap.values());
				console.log('Grouped Ratings List:', groupedRatingsList.value);
			} else {
				// 显示后端返回的错误消息，如果后端有提供的话
				ElMessage.error(response.data?.msg || '获取历史评价失败');
				groupedRatingsList.value = []; // 出错时清空列表
			}
		} catch (error) {
			console.error('获取历史评价请求出错:', error);
			ElMessage.error('获取历史评价请求异常'); // 通常是网络错误或 CORS 问题
			groupedRatingsList.value = []; // 出错时清空列表
		} finally {
			loading.value = false; // 加载结束
		}
	};

	// 组件挂载时调用接口获取数据
	onMounted(() => {
		fetchHistoryRatings();
	});
</script>

<style scoped lang="less">
	.my-ratings-container {
		padding: 20px;
		background-color: #fff;
		border-radius: 8px;
		box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);

		.page-title {
			font-size: 24px;
			color: #333;
			margin-bottom: 10px;
			text-align: center;
		}

		.page-description {
			font-size: 14px;
			color: #666;
			margin-bottom: 20px;
			text-align: center;
		}

		.loading-state {
			text-align: center;
			padding: 40px 0;
		}

		.ratings-list {
			margin-top: 20px;

			/* 单个订单的评价卡片 */
			.order-rating-item {
				margin-bottom: 15px;
				border-radius: 6px;

				.order-rating-header {
					display: flex;
					justify-content: space-between;
					align-items: center;
					font-size: 15px;
					color: #555;

					.order-service-name {
						margin-left: 10px; /* 服务名称左侧间距 */
						font-weight: bold;
						flex-grow: 1; /* 让服务名称占据更多空间 */
					}

					.rating-time {
						font-size: 13px;
						color: #999;
						flex-shrink: 0; /* 评价时间不收缩 */
					}
				}

				.order-rating-body {
					font-size: 14px;
					color: #444;

					.entity-ratings {
						/* border-top: 1px dashed #eee; /* 不再需要整体评价分隔线 */
						padding-top: 5px; /* 调整上方内边距 */

						.entity-rating-item {
							/* display: flex; /* 不再是单行flex */
							/* justify-content: space-between; */
							/* align-items: center; */
							margin-bottom: 15px; /* 各评价项之间间距增大 */
							padding-bottom: 15px;
							border-bottom: 1px solid #f5f5f5; /* 各评价项底部细线 */

							&:last-child {
								border-bottom: none; /* 最后一个评价项无底部线 */
								padding-bottom: 0;
								margin-bottom: 0;
							}

							.entity-info {
								display: flex; /* 名称和星级在一行 */
								justify-content: space-between;
								align-items: center;
								margin-bottom: 8px; /* 信息和评论之间间距 */

								.entity-name {
									font-weight: bold;
									color: #666;
									flex-shrink: 0;
									margin-right: 10px;
								}

								.el-rate {
									flex-grow: 1;
									justify-content: flex-end;
								}
							}

							.entity-comment {
								/* margin-top: 8px; /* 评论上边距 */
								color: #333; /* 评论颜色 */
								line-height: 1.6; /* 行高 */
								padding-left: 10px; /* 评论内容稍微缩进 */
								border-left: 3px solid #409eff; /* 左侧蓝色指示条 */

								.comment-text {
									color: #555; /* 评论内容颜色 */
								}
							}
						}
					}
				}
			}
		}

		.el-empty {
			padding: 40px 0;
		}
	}
</style>
