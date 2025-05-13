<template>
	<div class="not-found-container">
		<h1>404</h1>
		<p>抱歉，您访问的页面不存在。</p>
		<el-button type="primary" @click="router.push('/')">返回首页</el-button>
	</div>
</template>

<script setup>
	import { useRouter } from 'vue-router';
	const router = useRouter();
</script>

<style scoped lang="less">
	.not-found-container {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		min-height: 100vh;
		background-color: #f0f2f5;
		color: #606266;
		font-size: 20px;

		h1 {
			font-size: 80px;
			color: #409eff;
			margin-bottom: 20px;
		}

		p {
			margin-bottom: 30px;
		}
	}
</style>
