<template>
	<div class="personal-center-layout">
		<el-container class="main-container">
			<el-aside width="200px" class="sidebar-menu">
				<el-menu
					:default-active="activeSubMenu"
					router
					class="personal-center-nav"
					:unique-opened="true"
				>
					<el-menu-item index="/userHome/personal-center/orders">
						<el-icon><Document /></el-icon>
						<span>我的订单</span>
					</el-menu-item>
					<el-menu-item index="/userHome/personal-center/ratings">
						<el-icon><Star /></el-icon>
						<span>我的评价</span>
					</el-menu-item>
					<el-menu-item index="/userHome/personal-center/info">
						<el-icon><User /></el-icon>
						<span>个人信息</span>
					</el-menu-item>
					<el-menu-item index="/userHome/personal-center/password">
						<el-icon><Setting /></el-icon> <span>修改密码</span>
					</el-menu-item>
				</el-menu>
			</el-aside>

			<el-main class="content-area">
				<router-view />
			</el-main>
		</el-container>
	</div>
</template>

<script setup>
	import { ref, watch, onMounted } from 'vue';
	import { useRoute, useRouter } from 'vue-router';
	import { defineOptions } from 'vue';

	// 导入 Element Plus 图标，新增 Setting 图标
	import { Document, Star, User, Setting } from '@element-plus/icons-vue'; // <-- 新增 Setting

	// 定义组件名称，用于 keep-alive 等场景
	defineOptions({
		name: 'UserPersonalCenter',
	});

	const route = useRoute();
	const router = useRouter();

	// 响应式变量，用于控制侧边菜单的激活状态
	const activeSubMenu = ref('');

	// 监听路由变化，更新激活的菜单项
	watch(
		() => route.path,
		(newPath) => {
			console.log('Current path:', newPath);
			activeSubMenu.value = newPath;
		},
		{ immediate: true } // 立即执行一次，确保组件加载时设置正确的激活状态
	);

	// 在组件挂载时设置初始激活的菜单项
	onMounted(() => {
		// 默认跳转到“我的订单”页面，确保初次访问时“我的订单”内容被加载
		// 只有当当前路径是 '/userHome/personal-center' (即父路由) 时才重定向
		if (route.path === '/userHome/personal-center' || route.path === '/userHome/personal-center/') {
			router.replace('/userHome/personal-center/orders');
		}
		activeSubMenu.value = route.path; // 设置激活菜单，确保即使是重定向后也能正确激活
	});
</script>

<style scoped lang="less">
	/* **重要：确保 html, body, #app 占据全屏，且没有默认的边距** */
	/* 这段样式通常放在全局 CSS 文件（如 common.css）或 main.js 中更合适，
	   但为了确保效果，暂时放在这里，您可以后续考虑将其移到更恰当的位置。 */
	// html,
	// body,
	// #app {
	// 	height: 100%;
	// 	width: 100%;
	// 	margin: 0;
	// 	padding: 0;
	// 	/* 如果页面整体出现滚动条，可能是因为某些元素溢出，可以考虑设置 overflow: hidden */
	// 	/* overflow: hidden; */
	// }

	.personal-center-layout {
		/* 根据您的实际布局，减去头部导航的高度 (假设头部高度为 60px) */
		min-height: calc(100vh - 60px);
		width: 100%; /* 确保占据其父容器的全部宽度 */
		background-color: #f5f5f5; /* 页面背景色 */
		padding: 20px; /* 页面内边距 */
		box-sizing: border-box; /* 内边距和边框包含在元素的总宽度和高度内 */
		display: flex; /* 使用 Flexbox 布局，用于水平居中 main-container */
		justify-content: center; /* 水平居中其子元素 (main-container) */
		align-items: flex-start; /* 垂直顶部对齐，防止内容从中间开始 */
		overflow: auto; /* 如果内容超出此容器，允许滚动 */
	}

	.main-container {
		width: 100%; /* 占据其父容器 (personal-center-layout) 内容区的全部宽度 */
		max-width: 1200px; /* 限制内容最大宽度 */
		/* margin: 0 auto; 已由父级的 justify-content: center 处理，可移除 */
		background-color: #fff; /* 容器背景色 */
		border-radius: 8px;
		box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
		min-height: 600px; /* 最小高度 */
		/* el-container 默认就是 display: flex; flex-direction: row; */
		/* 如果布局依然有问题，尝试显式添加以下属性以确保其 Flexbox 行为 */
		// display: flex;
		// flex-direction: row;
	}

	.sidebar-menu {
		padding-top: 20px; /* 菜单顶部留白 */
		padding-right: 10px; /* 菜单右侧留白 */
		border-right: 1px solid #eee; /* 菜单右侧分隔线 */
		box-sizing: border-box; /* 确保 padding 和 border 包含在宽度内 */
		flex-shrink: 0; /* **关键：防止侧边栏收缩，保持其 200px 的宽度** */
	}

	.personal-center-nav {
		border-right: none; /* 移除 Element Plus 默认的右边框 */
		width: 100%; /* 确保菜单占据侧边栏的全部宽度 */
		height: 100%; /* 确保菜单占据侧边栏的全部高度 */

		.el-menu-item {
			height: 50px;
			line-height: 50px;
			font-size: 16px;
			color: #333;
			border-radius: 6px;
			margin-bottom: 5px;
			transition: all 0.3s ease;

			&:hover {
				background-color: #f0f8ff;
				color: var(--el-color-primary);
			}

			&.is-active {
				background-color: var(--el-color-primary-light-9);
				color: var(--el-color-primary) !important;
				font-weight: bold;
			}

			.el-icon {
				margin-right: 8px;
			}
		}
	}

	// .content-area {
	// 	padding: 20px 160px; /* 内容区域内边距 */
	// 	min-height: 600px; /* 确保内容区域有足够的最小高度 */
	// 	flex: 1; /* **关键：让内容区域填充剩余空间** */
	// 	box-sizing: border-box; /* 确保 padding 和 border 包含在宽度内 */
	// 	overflow-x: auto; /* **关键：如果内容溢出，添加水平滚动条** */
	// 	overflow-y: auto; /* 如果内容垂直溢出，添加垂直滚动条 */
	// 	max-width: calc(100% - 200px); /* 确保内容区域不会扩展到侧边栏下面 */
	// }
</style>
