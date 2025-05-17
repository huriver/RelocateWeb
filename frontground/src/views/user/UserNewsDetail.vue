<script setup>
	import { useRoute } from 'vue-router';
	import { queryNewsDetailApi } from '@/api/common.js'; // 确保 common.js 导出了 queryNewsDetailApi
	import { onMounted, ref } from 'vue';
	import { ElMessage } from 'element-plus';

	// 建议添加这行
	defineOptions({
		name: 'UserNewsDetail', // 组件名称，与文件名保持一致，驼峰命名
	});

	const route = useRoute();
	const news = ref({});

	onMounted(async () => {
		try {
			const { data: res } = await queryNewsDetailApi(route.params.id);
			if (res.code === 1 && res.data) {
				// 判断业务成功并有数据
				news.value = res.data;
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || '获取新闻详情失败'); // <-- 移除此行，避免重复提示
				console.warn('获取新闻详情业务失败:', res.msg); // 可以保留日志
				// 可以根据需要添加其他失败处理，比如重定向到错误页或列表页
			}
		} catch (error) {
			// 捕获真正的请求错误 (网络问题, CORS, request.js 拦截器抛出错误等)
			console.error('Network or API error fetching news detail:', error);
			// request.js 已经在 rejection 时弹窗了，这里的 ElMessage 主要用于兜底
			// 确保导入 ElMessage 才能使用
			ElMessage.error('获取新闻详情失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
		}
	});
</script>

<template>
	<div class="news-detail-container home-container">
		<h1 class="title">{{ news.title }}</h1>
		<div class="info">
			<div class="publish-date" v-if="news.publishDate">发布日期：{{ news.publishDate }}</div>
			<div
				class="update-time"
				v-if="news.updateTime"
				:style="{ marginLeft: news.publishDate ? '20px' : '0' }"
			>
				编辑时间：{{ news.updateTime }}
			</div>
		</div>
		<div class="content" v-if="news.content" v-html="news.content"></div>
		<div
			v-else-if="!news.id && !news.title && !news.content"
			style="text-align: center; color: #999; margin-top: 50px"
		>
			正在加载新闻详情或新闻不存在...
		</div>
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
			// margin-left: 20px; // 编辑时间与发布日期之间留出间距 - 通过 V-bind style 控制
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
