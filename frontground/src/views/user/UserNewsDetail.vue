<script setup>
	import { useRoute } from 'vue-router';
	import { queryNewsDetailApi } from '@/api/common.js';
	import { onMounted, ref } from 'vue';
	import { ElMessage } from 'element-plus'; // 确保导入 ElMessage

	// 建议添加这行
	defineOptions({
		name: 'UserNewsDetail', // 组件名称，与文件名保持一致，驼峰命名
	});

	const route = useRoute();
	const news = ref({});

	onMounted(async () => {
		try {
			const { data: res } = await queryNewsDetailApi(route.params.id);
			if (res.code !== 1) {
				ElMessage.error(res.msg || '获取新闻详情失败'); // 确保有默认错误信息
				return;
			}
			news.value = res.data;
		} catch (error) {
			console.error('Network or API error fetching news detail:', error);
			ElMessage.error('网络错误或请求失败，请稍后再试'); // 增加网络错误提示
		}
	});
</script>

<template>
	<div class="news-detail-container home-container">
		<h1 class="title">{{ news.title }}</h1>
		<div class="info">
			<div class="publish-date">发布日期：{{ news.publishDate }}</div>
			<div class="update-time">编辑时间：{{ news.updateTime }}</div>
		</div>
		<div class="content" v-html="news.content"></div>
	</div>
</template>

<style scoped lang="less">
	// 整个页面的内容容器，保持与列表页的简约卡片风格一致
	.news-detail-container {
		max-width: 1000px; // 详情页可以稍窄一些，利于阅读
		margin: 20px auto; // 上下外边距 20px，左右自动居中
		padding: 30px; // 内部填充，让内容有更多呼吸空间
		background-color: #fff; // 白色背景，使其在父级浅灰色背景上浮现
		border-radius: 8px; // 圆角
		box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05); // 轻微的阴影，增加立体感
	}

	.title {
		text-align: center;
		font-size: 28px; // 标题字体加大
		font-weight: bold;
		color: #333; // 标题颜色深一些，更突出
		margin-bottom: 20px; // 标题下方留出空间
	}

	.info {
		display: flex;
		justify-content: center; // 水平居中信息
		align-items: center;
		margin: 0 0 20px 0; // 上下外边距，下方与内容区隔开
		font-size: 14px;
		color: #999; // 信息文本颜色更柔和
		padding-bottom: 15px; // 信息下方留出内边距
		border-bottom: 1px dashed #eee; // 添加虚线分隔，美观且分隔效果好

		.publish-date,
		.update-time {
			white-space: nowrap; // 防止日期时间换行
		}

		.update-time {
			margin-left: 20px; // 编辑时间与发布日期之间留出间距
		}
	}

	.content {
		font-size: 16px; // 内容字体大小，易于阅读
		line-height: 1.8; // 行高增加，提高可读性
		color: #333; // 内容文本颜色
		padding: 0 10px; // 左右内边距，避免内容紧贴边缘
		white-space: pre-wrap; // 保留内容中的换行符和空格，适合显示来自富文本编辑器的内容
		word-wrap: break-word; // 自动换行，防止长单词或URL溢出容器

		// 如果内容可能包含多个段落，可以为 p 标签添加样式以增加间距和首行缩进
		:deep(p) {
			margin-bottom: 1em; // 段落之间间距
			text-indent: 2em; // 首行缩进
		}
	}
</style>
