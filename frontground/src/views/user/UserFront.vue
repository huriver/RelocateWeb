<script setup>
	import { queryNoticApi, queryNewsApi } from '@/api/common.js';
	import { onMounted, ref } from 'vue';
	import MoreCard from '@/components/user/component/MoreCard.vue';

	const noticForm = ref({
		page: 1,
		pageSize: 3,
		title: '',
		content: '',
		category: '',
	});
	const noticData = ref([]); // 确保初始化为数组
	const queryNotic = async () => {
		try {
			const { data: res } = await queryNoticApi(noticForm.value);
			if (res.code === 1) {
				// 确保 res.data.records 即使为 undefined 也能被赋值为 []
				noticData.value = res.data && res.data.records ? res.data.records : [];
			} else {
				// API 逻辑错误时，也确保数据为数组
				console.error('Failed to fetch notic data:', res.msg || 'Unknown error');
				noticData.value = [];
			}
		} catch (error) {
			// 网络或意外错误时，确保数据为数组
			console.error('Network or API error fetching notic data:', error);
			noticData.value = [];
		}
	};

	const newsForm = ref({
		page: 1,
		pageSize: 3,
		title: '',
		content: '',
		category: '',
	});
	const newsData = ref([]); // 确保初始化为数组
	const queryNews = async () => {
		try {
			const { data: res } = await queryNewsApi(newsForm.value);
			if (res.code === 1) {
				// 确保 res.data.records 即使为 undefined 也能被赋值为 []
				newsData.value = res.data && res.data.records ? res.data.records : [];
			} else {
				// API 逻辑错误时，也确保数据为数组
				console.error('Failed to fetch news data:', res.msg || 'Unknown error');
				newsData.value = [];
			}
		} catch (error) {
			// 网络或意外错误时，确保数据为数组
			console.error('Network or API error fetching news data:', error);
			newsData.value = [];
		}
	};

	onMounted(() => {
		queryNotic();
		queryNews();
	});
</script>

<template>
	<div class="front-container home-container">
		<more-card :title="'搬家须知'" :data="noticData" :routeLink="'/userHome/notice'"></more-card>
		<more-card :title="'搬家新闻'" :data="newsData" :routeLink="'/userHome/news'"></more-card>
	</div>
</template>

<style scoped lang="less">
	.front-container {
		max-width: 1200px;
		margin: 20px auto;
		padding: 20px;
		// ... (其他样式保持不变，您可以根据需要调整) ...
	}
</style>
