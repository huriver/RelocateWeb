<script setup>
	import { queryServiceApi, getServiceDetailApi } from '@/api/serviceApi.js';
	import { getServiceRatingApi } from '@/api/ratingApi.js';
	import { getServiceCategoriesApi } from '@/api/serviceCategoryApi.js';
	import { ref, onMounted, watch, onDeactivated } from 'vue'; // **新增导入 onDeactivated**
	import { useRouter } from 'vue-router';
	import {
		ElMessage,
		ElTag,
		ElButton,
		ElCard,
		ElDescriptions,
		ElDescriptionsItem,
		ElDivider,
		ElRate,
		ElSkeleton,
		ElSkeletonItem,
		ElSelect,
		ElOption,
		ElPagination, // 导入 ElPagination
		ElEmpty, // 导入 ElEmpty
	} from 'element-plus';

	// **新增：明确定义组件的 name，以配合 keep-alive 的 include 属性**
	// 如果您的项目配置了 unplugin-vue-components 或 defineOptions 是全局可用的，可能不需要手动导入
	import { defineOptions } from 'vue';
	defineOptions({
		name: 'UserServices',
	});

	const router = useRouter();

	// 服务筛选和分页参数
	const serviceFilterPagination = ref({
		page: 1, // 更改为 1-indexed，与 el-pagination 保持一致
		pageSize: 10, // 设置一个默认的每页大小，例如 10
		categoryId: '', // 初始化为空字符串，表示查询所有类型
		total: 0, // 总记录数
	});

	// 服务记录列表
	const serviceRecords = ref([]);
	// 列表加载状态
	const isServiceListLoading = ref(false);

	// 服务类型相关状态
	const serviceCategories = ref([]); // 存储从后端获取的服务类型列表
	const selectedCategoryId = ref(''); // 存储当前选中的服务类型ID，默认为空字符串（表示全部）

	// 获取服务类型列表
	const fetchServiceCategories = async () => {
		try {
			const { data: res } = await getServiceCategoriesApi();
			if (res.code === 1) {
				serviceCategories.value = res.data;
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error('获取服务类型失败！'); // <-- 移除此行
				console.warn('获取服务类型业务失败:', res.msg); // 可以保留日志
			}
		} catch (error) {
			// 捕获真正的请求错误
			console.error('获取服务类型 API 调用失败:', error);
			ElMessage.error('获取服务类型失败，请检查网络。'); // <-- 这个用于网络或HTTP错误
		}
	};

	// 查询服务列表
	const queryServiceList = async () => {
		isServiceListLoading.value = true; // 设置列表加载状态为 true
		// **核心修改：确保 categoryId 始终为字符串**
		const categoryToSend =
			selectedCategoryId.value === undefined ||
			selectedCategoryId.value === null ||
			selectedCategoryId.value === ''
				? undefined // 如果 selectedCategoryId.value 是 undefined, null, 或空字符串，则发送 undefined 或不发送此参数
				: selectedCategoryId.value; // 否则，发送当前值

		try {
			const { data: res } = await queryServiceApi({
				page: serviceFilterPagination.value.page, // 直接使用当前页码
				pageSize: serviceFilterPagination.value.pageSize, // 直接使用当前每页大小
				categoryId: categoryToSend, // **使用处理后的 categoryToSend**
			});

			if (res.code !== 1) {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg); // <-- 移除此行
				console.warn('查询服务列表业务失败:', res.msg); // 可以保留日志
				serviceRecords.value = []; // 查询失败时清空数据
				serviceFilterPagination.value.total = 0; // 重置总数
				return; // 业务失败，提前返回
			}

			// 业务成功 (res.code === 1)
			serviceRecords.value = res.data.records || []; // 替换数据
			serviceFilterPagination.value.total = res.data.total || 0; // 更新总数
		} catch (error) {
			// 捕获真正的请求错误
			console.error('查询服务失败:', error);
			ElMessage.error('查询服务失败，请稍后再试。'); // <-- 这个用于网络或HTTP错误
			serviceRecords.value = []; // 发生错误时清空数据
			serviceFilterPagination.value.total = 0; // 重置总数
		} finally {
			isServiceListLoading.value = false; // 完成加载，无论成功或失败
		}
	};

	// 处理页码改变事件
	const handlePageChange = (newPage) => {
		serviceFilterPagination.value.page = newPage;
		queryServiceList();
	};

	// 处理每页显示数量改变事件
	const handleSizeChange = (newSize) => {
		serviceFilterPagination.value.pageSize = newSize;
		serviceFilterPagination.value.page = 1; // 每页数量改变时，回到第一页
		queryServiceList();
	};

	// 展开状态管理：键为服务ID，值为布尔型，表示详情是否展开
	const isServiceDetailExpandedMap = ref({});
	// 切换详情方法
	const toggleServiceDetail = (serviceId) => {
		isServiceDetailExpandedMap.value = {
			...isServiceDetailExpandedMap.value,
			[serviceId]: !isServiceDetailExpandedMap.value[serviceId],
		};
	};

	// 存储已加载的服务详细数据：键为服务ID，值为服务详细对象 (包含 rating)
	const serviceDetailsMap = ref({});
	// 存储服务详情加载状态：键为服务ID，值为布尔型
	const isServiceDetailLoadingMap = ref({});

	// 修改后的详情方法
	const getServiceDetail = async (serviceId) => {
		const isExpanded = isServiceDetailExpandedMap.value[serviceId]; // 获取当前展开状态
		toggleServiceDetail(serviceId); // 先切换展开状态

		// 如果现在是收起状态，或者详情数据已经存在且不在加载中，则不执行后续加载逻辑
		if (
			!isServiceDetailExpandedMap.value[serviceId] ||
			(serviceDetailsMap.value[serviceId] && !isServiceDetailLoadingMap.value[serviceId])
		) {
			return;
		}

		// 只有在确定要展开且数据不存在时才加载
		if (isServiceDetailExpandedMap.value[serviceId] && !serviceDetailsMap.value[serviceId]) {
			isServiceDetailLoadingMap.value[serviceId] = true;
			try {
				// === 获取服务详情 ===
				const { data: serviceRes } = await getServiceDetailApi(serviceId);
				if (serviceRes.code !== 1 || !serviceRes.data) {
					// 业务失败 (code !== 1) 或数据不存在，request.js 已经弹窗提示了后端 msg
					// ElMessage.error(serviceRes.msg || '获取服务详情失败'); // <-- 移除此行
					console.warn('获取服务详情业务失败:', serviceRes.msg); // 可以保留日志
					// 失败时自动收起详情
					isServiceDetailExpandedMap.value[serviceId] = false;
					isServiceDetailLoadingMap.value[serviceId] = false; // 确保加载状态也被重置
					return; // 获取详情失败，不再尝试获取评价
				}
				// 获取服务详情成功，存储数据
				serviceDetailsMap.value[serviceId] = serviceRes.data;

				// === 获取服务评价 ===
				// 注意：这里即使获取评价失败，也不影响服务详情的展示
				try {
					const { data: ratingRes } = await getServiceRatingApi(serviceId);
					if (ratingRes.code === 1 && ratingRes.data && ratingRes.data.length > 0) {
						// 评价获取成功且有数据
						serviceDetailsMap.value[serviceId].rating = ratingRes.data[0]; // 假设只需要第一条或最新一条评价
					} else {
						// 评价业务失败 (code !== 1) 或无数据，request.js 已经弹窗提示了后端 msg
						// ElMessage.error(ratingRes.msg || '获取服务评价失败'); // <-- 移除此行
						console.warn('获取服务评价业务失败:', ratingRes.msg); // 可以保留日志
						serviceDetailsMap.value[serviceId].rating = null; // 没有评价或获取失败
					}
				} catch (ratingError) {
					// 捕获获取评价的请求错误
					console.error('获取服务评价请求失败:', ratingError);
					ElMessage.error('获取服务评价失败'); // <-- 这个用于网络或HTTP错误
					serviceDetailsMap.value[serviceId].rating = null; // 获取评价失败
				}
			} catch (error) {
				// 捕获获取服务详情的请求错误 (request.js 抛出的错误)
				console.error('加载详情失败:', error);
				ElMessage.error('加载详情失败，请稍后再试。'); // <-- 这个用于网络或HTTP错误
				// 失败时自动收起详情
				isServiceDetailExpandedMap.value[serviceId] = false; // <-- 增加此行
			} finally {
				isServiceDetailLoadingMap.value[serviceId] = false;
			}
		}
	};

	const rateColors = ref(['#99A9BF', '#F7BA2A', '#FF9900']);

	const selectServiceForOrder = (serviceId) => {
		// 打开新窗口跳转到订单创建页
		const route = router.resolve({ path: `/userOrder/${serviceId}` });
		window.open(route.href, '_blank');
	};

	// *** 新增方法：查看所有评论 ***
	const viewAllComments = (serviceId) => {
		router.push({ name: 'userServiceComments', params: { id: serviceId } }); // 更改为新的路由名称
	};

	// 首次加载数据和分类
	onMounted(() => {
		fetchServiceCategories(); // 首次加载服务类型
		queryServiceList(); // 首次加载服务列表
	});

	// 监听 selectedCategoryId 的变化，当筛选条件改变时重置分页并重新查询
	watch(selectedCategoryId, (newCategoryId, oldCategoryId) => {
		// 只有当值真正改变时才执行
		// 由于 selectedCategoryId 默认为空字符串，这里 watch 会在 mounted 后触发一次，
		// 但如果初始值为 ''，新值还是 ''，下面的 if 判断 newCategoryId !== oldCategoryId 应该能阻止重复执行。
		// 如果初始值为 null 或 undefined，而设置为 ''，则会触发。
		// 为了更稳健，可以检查旧值是否已初始化或者确保初始值为 ''。
		if (newCategoryId !== oldCategoryId) {
			// **新增代码：切换类别时，将所有服务详情的展开状态重置为关闭**
			isServiceDetailExpandedMap.value = {};
			// serviceDetailsMap.value = {}; // 可以选择性地清空已加载的详情数据
			serviceFilterPagination.value.page = 1; // 重置页码到第一页
			queryServiceList(); // 重新查询服务列表
		}
	});

	// **新增生命周期钩子：当组件被停用时触发**
	onDeactivated(() => {
		// 获取当前路由（即即将进入的路由）
		const toRoute = router.currentRoute.value;

		// 如果即将进入的路由不是服务评论页面 (userServiceComments)，
		// 说明用户导航到了其他主菜单项（如新闻、须知、个人中心等），
		// 此时应重置服务详情的展开状态，使其在下次进入时默认关闭。
		if (toRoute.name !== 'userServiceComments' && toRoute.name !== 'userOrder') {
			// 将所有服务详情的展开状态重置为关闭
			isServiceDetailExpandedMap.value = {};
			// 您也可以选择性地清空已加载的服务详情数据（serviceDetailsMap），
			// 但通常保留数据以便用户快速重新展开同一服务是更好的用户体验。
			// serviceDetailsMap.value = {};
		}
	});
</script>

<template>
	<div class="service-main-container">
		<div class="filter-section">
			<el-select
				v-model="selectedCategoryId"
				placeholder="选择服务类型"
				clearable
				size="default"
				style="width: 220px"
			>
				<el-option label="全部服务类型" value=""></el-option>
				<el-option
					v-for="category in serviceCategories"
					:key="category.id"
					:label="category.typeName"
					:value="category.id"
				>
				</el-option>
			</el-select>
		</div>

		<div class="data-list-box">
			<el-skeleton
				v-if="isServiceListLoading && serviceRecords.length === 0"
				animated
				:count="serviceFilterPagination.pageSize"
				style="margin-bottom: 20px"
			>
				<template #template>
					<el-skeleton-item
						variant="rect"
						style="width: 100%; height: 200px; margin-bottom: 20px"
					/>
				</template>
			</el-skeleton>

			<template v-else>
				<div class="service-item" v-for="item in serviceRecords" :key="item.id">
					<el-card shadow="hover" class="service-card">
						<div class="card-body-content">
							<div class="card-header">
								<h2 class="service-name">{{ item.serviceName }}</h2>
								<el-tag type="info" effect="light" size="small">{{ item.categoryName }}</el-tag>
							</div>
							<p class="short-description">{{ item.shortDescription }}</p>

							<div class="summary-info">
								<span class="truck-type-name">{{ item.truckTypeName }}</span>
								<span class="base-price-display-aligned"
									>起步价（5公里以内）：<strong class="price-value"
										>{{ item.basePrice }}元</strong
									></span
								>

								<div class="service-rating-summary">
									<el-rate
										:model-value="item.averageRating"
										:colors="rateColors"
										disabled
										show-score
										text-color="#ff9900"
										score-template="{value}"
									/>
									<span class="rating-count">（{{ item.ratingCount }} 条评价）</span>
								</div>

								<el-button
									type="primary"
									link
									@click="getServiceDetail(item.id)"
									class="toggle-detail-btn"
								>
									{{ isServiceDetailExpandedMap[item.id] ? '收起详情' : '查看详情' }}
								</el-button>
							</div>

							<transition name="el-fade-in-linear">
								<div v-if="isServiceDetailExpandedMap[item.id]" class="detail-section">
									<el-skeleton v-if="isServiceDetailLoadingMap[item.id]" animated>
										<template #template>
											<el-skeleton-item variant="p" style="width: 100%" />
											<el-skeleton-item variant="p" style="width: 100%" />
											<el-skeleton-item variant="p" style="width: 80%" />
										</template>
									</el-skeleton>
									<div v-else-if="serviceDetailsMap[item.id]" class="detail-content-loaded">
										<template v-if="serviceDetailsMap[item.id]">
											<el-descriptions
												title="装载能力"
												:column="1"
												border
												class="detail-descriptions"
											>
												<el-descriptions-item label="描述">
													{{ serviceDetailsMap[item.id].loadingCapacityDescription || '暂无描述' }}
												</el-descriptions-item>
											</el-descriptions>
											<el-divider />

											<el-descriptions
												title="货车信息"
												:column="2"
												border
												class="detail-descriptions"
											>
												<el-descriptions-item label="货车描述">{{
													serviceDetailsMap[item.id].truckType?.description || '暂无'
												}}</el-descriptions-item>
												<el-descriptions-item label="货车规格">{{
													serviceDetailsMap[item.id].truckType?.capacity || '暂无'
												}}</el-descriptions-item>
												<el-descriptions-item label="起步价（5公里以内）"
													>{{
														serviceDetailsMap[item.id].truckType?.baseFare
													}}元</el-descriptions-item
												>
											</el-descriptions>
											<el-divider />

											<el-descriptions
												title="服务评价"
												:column="1"
												border
												class="detail-descriptions"
											>
												<el-descriptions-item label="评论">
													{{ serviceDetailsMap[item.id].rating?.comment || '暂无评论' }}
													<el-button
														v-if="serviceDetailsMap[item.id].rating?.comment"
														link
														type="primary"
														size="small"
														style="margin-left: 10px"
														@click="viewAllComments(item.id)"
														>查看更多评论</el-button
													>
												</el-descriptions-item>
												<el-descriptions-item label="评分值">
													<el-rate
														:model-value="serviceDetailsMap[item.id].rating?.ratingValue"
														:colors="rateColors"
														disabled
														show-score
														text-color="#ff9900"
														score-template="{value}"
													/>
												</el-descriptions-item>
											</el-descriptions>

											<div class="select-service-action">
												<el-button
													type="danger"
													size="large"
													@click="selectServiceForOrder(item.id)"
													>立即选择此服务</el-button
												>
											</div>
										</template>
										<div v-else style="text-align: center; padding: 20px; color: #909399">
											加载详情数据失败或暂无数据。
										</div>
									</div>
									<div v-else style="text-align: center; padding: 20px; color: #909399">
										加载详情数据失败或暂无数据。
									</div>
								</div>
							</transition>
						</div>
					</el-card>
				</div>
				<el-empty
					v-if="serviceRecords.length === 0 && !isServiceListLoading"
					description="暂无服务数据"
				/>
			</template>
		</div>

		<div class="pagination-footer">
			<el-pagination
				v-model:current-page="serviceFilterPagination.page"
				v-model:page-size="serviceFilterPagination.pageSize"
				:page-sizes="[5, 10, 15, 20]"
				layout="total, sizes, prev, pager, next, jumper"
				:total="serviceFilterPagination.total"
				@size-change="handleSizeChange"
				@current-change="handlePageChange"
			/>
		</div>
	</div>
</template>

<style scoped lang="less">
	// 整体页面的内容容器，与 News.vue 保持一致的简约卡片风格
	.service-main-container {
		max-width: 1200px; // 限制页面内容的最大宽度，保持一致
		margin: 20px auto; // 上下外边距 20px，左右自动居中
		padding: 30px; // 内部填充，让内容有更多呼吸空间
		background-color: #fff; // 白色背景，使其在父级浅灰色背景上浮现
		border-radius: 8px; // 圆角
		box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05); // 轻微的阴影，增加立体感
	}

	// 新增：骨架屏容器样式
	.loading-skeleton {
		padding: 20px;
		min-height: 400px; /* 确保加载区域有足够的高度，防止内容跳动 */
		/* 为 skeleton-item 提供一个默认宽度 */
		:deep(.el-skeleton__item) {
			width: 100%;
		}
	}

	.skeleton-form-item {
		margin-bottom: 20px;
		:deep(.el-skeleton__item) {
			height: 32px; /* 模拟输入框和按钮高度 */
			margin-right: 10px; /* 模拟表单项间距 */
		}
	}

	.skeleton-pagination {
		margin-top: 30px;
		:deep(.el-skeleton__item) {
			width: 60% !important; /* 模拟分页组件宽度 */
			height: 32px; /* 模拟分页组件高度 */
			margin: 0 auto; /* 居中 */
		}
	}

	// 筛选区域，样式与 News.vue 的 .search-panel 保持一致
	.filter-section {
		padding-bottom: 20px; // 底部内边距，与下方列表分隔
		margin-bottom: 20px; // 底部外边距，与下方列表分隔
		border-bottom: 1px solid #eee; // 底部细边框，作为视觉分隔
		display: flex;
		align-items: center;
		justify-content: flex-start; // 筛选器靠左对齐
	}

	// 数据列表区域 (取代原来的 infinite-list)
	.data-list-box {
		padding-top: 20px; // 顶部内边距，与筛选区域分隔
		display: flex;
		flex-direction: column;
		gap: 20px; // 每个服务卡片之间的间距，与 News.vue 的 .data-item 的 margin-bottom 保持一致
		min-height: 300px; // 确保即使没有数据也有一定的区域高度，防止内容跳动
	}

	// 服务项样式 (与 News.vue 的 .data-item 保持高度一致)
	.service-item {
		.el-card {
			height: auto; // 允许卡片高度根据内容自适应
			min-height: 200px; // 设置一个合理的最小高度，以避免卡片过短
			padding: 0; // 移除 el-card 自身的内边距，由内部的 .card-body-content 控制
			border: 1px solid rgba(0, 0, 0, 0.08);
			border-radius: 8px;
			box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03);
			transition: all 0.3s ease-in-out;
			overflow: hidden;
			cursor: pointer;

			&:hover {
				box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
				transform: translateY(-3px);
			}

			// 将 el-card__body 设置为弹性容器
			:deep(.el-card__body) {
				display: flex;
				flex-direction: column;
				height: 100%; // 确保其占据 el-card 的全部高度
				padding: 0 !important; // 重置 ElCard 的 padding
			}
		}

		// 新增的用于包裹卡片内容的容器，处理 padding 和 flex 布局
		.card-body-content {
			display: flex;
			flex-direction: column;
			height: 100%; // 确保它占据 el-card__body 的全部高度
			padding: 18px 20px 12px 20px; // 顶部、左右 18px/20px，底部调整为 12px
		}

		.card-header {
			flex-shrink: 0; // 防止头部内容收缩
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 10px;

			.service-name {
				color: var(--el-color-primary, rgb(64, 158, 255));
				font-size: 20px;
				font-weight: bold;
				margin: 0;
			}

			.el-tag {
				font-size: 12px;
				height: 24px;
				line-height: 22px;
				padding: 0 8px;
			}
		}

		.short-description {
			color: #666;
			font-size: 14px;
			margin: 0 0 5px 0; // 底部外边距从 10px 减小到 5px，进一步减小间距
			line-height: 1.6; // 提高行高，增加可读性
			// 固定高度以确保下方元素对齐
			min-height: calc(1.6em * 3); // 至少3行高度
			max-height: calc(1.6em * 3); // 最多3行高度
			overflow: hidden;
			text-overflow: ellipsis;
			word-break: break-word;
			display: -webkit-box;
			-webkit-box-orient: vertical;
			-webkit-line-clamp: 3; // 限制显示 3 行内容
			flex-grow: 0; // 不允许弹性增长，防止内容短时下方元素被向下推
		}

		.summary-info {
			display: grid; // 使用 Grid 布局
			// 调整列宽以实现“起步价”对齐:
			// Column 1: truck-type-name (最大宽度180px，超出部分省略)
			// Column 2: base-price-display-aligned (固定宽度，其起始位置将对齐)
			// Column 3: service-rating-summary (固定宽度，用于评分显示)
			// Column 4: toggle-detail-btn (自适应宽度)
			grid-template-columns: 180px 300px 280px auto;
			align-items: center; // 垂直居中对齐
			gap: 25px; // 列之间的间距
			font-size: 14px;
			color: #555;
			flex-shrink: 0;
			margin-top: 0; // 顶部外边距从 5px 减小到 0px，进一步减小间距

			.truck-type-name {
				font-weight: 500;
				color: #409eff;
				max-width: 100%; // 限制在网格列宽内
				overflow: hidden; // 隐藏超出部分
				text-overflow: ellipsis; // 显示省略号
				white-space: nowrap; // 不换行
				justify-self: start; // 在其网格单元格内左对齐
			}

			.base-price-display-aligned {
				color: #888;
				justify-self: start; // 确保在网格单元格内左对齐，从而实现起始位置的统一
			}

			.price-value {
				color: #e6a23c;
				font-weight: bold;
				font-size: 15px;
			}

			.service-rating-summary {
				display: flex;
				align-items: center;
				justify-content: center; // 在其列中居中
			}

			.rating-count {
				margin-left: 5px; // 评分星级和评价数量的间距
			}

			.toggle-detail-btn {
				font-size: 14px;
				justify-self: end; // 将按钮对齐到其网格单元格的右侧
				flex-shrink: 0;
			}
		}

		.detail-section {
			margin-top: 20px;
			padding-top: 20px;
			border-top: 1px dashed #eee;
			flex-shrink: 0; // 防止详情区域收缩

			.el-descriptions {
				margin-bottom: 15px;

				:deep(.el-descriptions__header) {
					margin-bottom: 10px;

					.el-descriptions__title {
						font-size: 16px;
						font-weight: bold;
						color: #333;
					}
				}

				:deep(.el-descriptions__body) {
					background-color: #f8f8f8;
					border-radius: 4px;
					padding: 10px;
				}

				:deep(.el-descriptions__label) {
					font-weight: 500;
					color: #555;
					min-width: 120px;
				}

				:deep(.el-descriptions__content) {
					color: #666;
				}

				.el-rate {
					vertical-align: middle;
				}
			}

			.el-divider {
				margin: 15px 0;
			}

			.select-service-action {
				display: flex;
				justify-content: flex-end;
				margin-top: 20px;

				.el-button {
					padding: 12px 25px;
					font-size: 16px;
					border-radius: 6px;
				}
			}
		}
	}

	// 分页区域
	.pagination-footer {
		margin-top: 20px; // 顶部外边距
		display: flex;
		justify-content: center; // 水平居中分页组件
	}
</style>
