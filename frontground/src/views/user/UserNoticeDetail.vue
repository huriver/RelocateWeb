<script setup>
	import { useRoute } from 'vue-router';
	import { queryNoticDetailApi } from '@/api/common.js'; // 假设这里是正确的 API 导入路径
	import { onMounted, ref } from 'vue';
	// **新增：导入 Element Plus 的消息提示组件 ElMessage**
	import { ElMessage } from 'element-plus';

	const route = useRoute();
	const notic = ref({});

	// **优化：增加 try...catch 块处理 API 请求错误，并完善错误提示**
	onMounted(async () => {
		try {
			const { data: res } = await queryNoticDetailApi(route.params.id);
			if (res.code === 1) {
				// 检查响应码是否为成功
				notic.value = res.data || {}; // 确保 res.data 存在，避免 undefined
			} else {
				// 处理 API 业务逻辑错误 (code !== 1)
				// request.js 已经弹窗提示了后端 msg，这里无需重复提示
				// ElMessage.error(res.msg || '获取通知详情失败'); // <-- 移除此行，避免重复提示
				console.warn('获取通知详情业务失败:', res.msg); // 可以保留日志
				notic.value = {}; // 请求失败时清空数据
			}
		} catch (error) {
			// 处理网络错误或其他请求异常 (request.js 拒绝 Promise 的情况)
			console.error('Error fetching notice detail:', error);
			ElMessage.error('获取通知详情失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
			notic.value = {}; // 请求失败时清空数据
		}
	});
</script>

<template>
	<div class="notic-detail-container">
		<h1 class="title">{{ notic.title }}</h1>
		<div class="info">
			<div class="publish-date">发布日期：{{ notic.publishDate }}</div>
			<div class="update-time">编辑时间：{{ notic.updateTime }}</div>
			<el-tag class="category" type="primary" v-if="notic.category">{{ notic.category }}</el-tag>
		</div>
		<div class="content" v-html="notic.content"></div>
	</div>
</template>

<style scoped lang="less">
	.notic-detail-container {
		max-width: 900px; /* 限制内容区域最大宽度，提升可读性，比列表页容器略窄 */
		margin: 20px auto; /* 居中显示，上下留出边距 */
		padding: 30px 40px; /* 内部填充，尤其是左右增加，让内容有更多呼吸空间 */
		background-color: #fff; /* 白色背景，与父级页面背景形成对比，作为内容卡片 */
		border-radius: 8px; /* 圆角 */
		box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05); /* 轻微阴影，增加立体感和精致感 */
	}

	.title {
		text-align: center;
		font-size: 28px; /* 标题字体更大，更醒目 */
		font-weight: bold;
		color: #333; /* 标题颜色更深 */
		margin-bottom: 25px; /* 标题下方更多空间，与信息区域分隔 */
	}

	.info {
		display: flex;
		justify-content: center; /* 信息项水平居中 */
		align-items: center; /* 信息项垂直居中对齐 */
		margin-bottom: 30px; /* 信息区块下方更多空间，与内容区域分隔 */
		font-size: 14px;
		color: #999; /* 信息文本颜色更柔和，不抢内容主体 */
		padding-bottom: 15px; /* 信息区块底部内边距 */
		border-bottom: 1px solid #eee; /* 添加底部细分隔线，视觉上与内容区域分开 */

		.publish-date {
			margin-right: 20px; /* 发布日期和编辑时间之间的间距 */
		}

		.update-time {
			margin-right: 20px; /* 编辑时间和类别标签之间的间距 */
		}

		.category {
			/* Element Plus 的 el-tag 默认样式已经很好看，这里不需要额外调整，
         因为它会随着其他元素而自然排列。 */
		}
	}

	.content {
		font-size: 16px; /* 内容字体大小适中，提升阅读舒适度 */
		line-height: 1.8; /* 增加行高，显著提升文本可读性 */
		color: #555; /* 内容文本颜色，比标题略浅，比信息略深 */
		white-space: pre-wrap; /* 保留文本中的换行符和空格，特别是如果 content 是从富文本编辑器来的 */
		word-break: break-word; /* 防止长单词或 URL 溢出容器 */
		// margin-bottom: 20px; /* 如果内容下方还有其他元素，可以添加底部外边距 */
	}
</style>
