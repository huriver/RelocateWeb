<template>
	<div class="home-page">
		<el-container>
			<el-header class="main-header">
				<img class="logo" src="../../assets/img/logo.png" @click="router.push('/userHome/front')" />
				<el-menu
					:default-active="activeRouter"
					mode="horizontal"
					router
					:ellipsis="false"
					class="main-nav-menu"
				>
					<el-menu-item index="/userHome/front">首页</el-menu-item>
					<el-menu-item index="/userHome/services">搬家服务</el-menu-item>
					<el-menu-item index="/userHome/notice">搬家须知</el-menu-item>
					<el-menu-item index="/userHome/news">搬家新闻</el-menu-item>
				</el-menu>

				<div class="user-info-section" v-if="store.userInfo && store.userInfo.token">
					<el-dropdown trigger="click" @command="handleCommand">
						<span class="el-dropdown-link">
							<el-avatar
								:size="40"
								:src="
									store.userInfo?.photoUrl ||
									'https://cube.elemecdn.com/0/88/03fa4b90ddb33084d674608e9499dpng.png'
								"
								class="user-avatar"
							></el-avatar>
							<span class="user-nickname">{{
								store.userInfo?.name || store.userInfo?.username || '用户'
							}}</span>
							<el-icon class="el-icon--right"><arrow-down /></el-icon>
						</span>
						<template #dropdown>
							<el-dropdown-menu>
								<el-dropdown-item command="personalCenter">个人中心</el-dropdown-item>
								<el-dropdown-item command="logout" class="logout-item">注销</el-dropdown-item>
							</el-dropdown-menu>
						</template>
					</el-dropdown>
				</div>
				<div class="login-register-section" v-else>
					<el-button type="primary" link @click="router.push('/login')">登录</el-button>
					<el-button type="primary" link @click="router.push('/register')">注册</el-button>
				</div>
			</el-header>
			<el-main class="main-content">
				<router-view v-slot="{ Component }">
					<keep-alive :include="['UserServices', 'UserMyOrders', 'UserMyRatings', 'UserOrder']">
						<component :is="Component" />
					</keep-alive>
				</router-view>
			</el-main>
		</el-container>
	</div>
</template>

<script setup>
	import { ref, watch, onMounted } from 'vue';
	import { useRoute, useRouter } from 'vue-router';
	import { myStore } from '@/stores/store.js'; // 确保路径正确
	import { ElMessage, ElMessageBox } from 'element-plus';
	import { ArrowDown } from '@element-plus/icons-vue'; // 导入 Element Plus 图标

	// 导入用户相关的 API
	import { userLogoutApi } from '@/api/userApi.js'; // 假设 userApi.js 存在

	const store = myStore(); // 实例化你的 Pinia store
	const route = useRoute();
	const router = useRouter();

	const activeRouter = ref(store.routePath || '/userHome/front');

	watch(
		() => route.path,
		(newPath) => {
			activeRouter.value = newPath;
			store.saveRoutePath(newPath);
		},
		{ immediate: true }
	);

	onMounted(() => {
		// 通常无需在此处手动加载用户信息，Pinia Store 初始化时已处理 localStorage 读取
	});

	const handleCommand = (command) => {
		if (command === 'personalCenter') {
			router.push('/userHome/personal-center');
		} else if (command === 'logout') {
			logout();
		}
	};

	// 注销登录功能
	const logout = () => {
		ElMessageBox.confirm('确定要注销登录吗?', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
		})
			.then(async () => {
				try {
					// 调用后端注销 API
					// 假设 userLogoutApi 返回 res.data.code 和 res.data.msg
					const { data: res } = await userLogoutApi();

					if (res.code === 1) {
						ElMessage.success('已成功注销！');
					} else {
						ElMessage.warning(res.msg || '注销失败，请重试。');
					}
				} catch (error) {
					console.error('注销请求失败:', error);
					ElMessage.error('网络错误或注销请求失败，请检查网络。');
				} finally {
					// 无论后端注销成功与否，都清除本地存储和 Pinia Store 中的用户数据
					// 并跳转到登录页面，确保客户端状态和页面显示正确
					store.clear();
					router.push('/login'); // 假设你的登录页面路由是 /login
				}
			})
			.catch(() => {
				ElMessage.info('已取消注销。');
			});
	};
</script>

<style lang="less" scoped>
	.home-page {
		height: 100vh;
		display: flex;
		flex-direction: column;
		background-color: #f0f2f5;
	}

	.el-container {
		flex: 1;
		display: flex;
		flex-direction: column;
	}

	.main-header {
		width: 100%;
		height: 60px;
		padding: 0 60px;
		box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
		position: fixed;
		top: 0;
		left: 0;
		z-index: 999;
		background-color: #fff;

		display: flex;
		align-items: center;
		justify-content: space-between; /* 将 logo 推到左侧，用户部分推到右侧，菜单在中间填充 */

		.logo {
			height: 60px;
			width: auto;
			cursor: pointer;
			margin-right: 20px; /* logo和菜单之间的间隔 */
		}

		.main-nav-menu {
			flex: 1; /* 让菜单占据 logo 和用户信息之间的大部分可用空间 */
			height: 100%;
			border-bottom: none;
			display: flex;
			justify-content: space-around; /* 关键：使菜单项在其占据的空间内平均分布，达到居中且有间隔的效果 */

			.el-menu-item {
				padding: 0 10px;
				font-size: 16px;
				color: #555;
				height: 100%; /* 确保菜单项填满头部高度 */
				display: flex;
				align-items: center; /* 垂直居中文本 */

				&:hover {
					background-color: #f5f5f5;
					color: var(--el-color-primary);
				}

				&.is-active {
					color: var(--el-color-primary) !important;
					background-color: #f0f8ff;
					border-bottom: none !important;
				}
			}

			/* 为激活菜单项添加底部线条 */
			:deep(.el-menu-item.is-active::after) {
				content: '';
				position: absolute;
				bottom: 0;
				left: 50%;
				transform: translateX(-50%);
				width: 70%;
				height: 2px;
				background-color: var(--el-color-primary);
			}
		}

		.user-info-section {
			display: flex;
			align-items: center;
			margin-left: 20px; /* 菜单和用户部分之间的间距 */

			.el-dropdown-link {
				display: flex;
				align-items: center;
				cursor: pointer;
				color: #555; /* 下拉菜单触发器文本颜色 */
				font-size: 16px;
				transition: color 0.3s; /* 添加过渡效果 */

				&:hover {
					color: var(--el-color-primary); /* 鼠标悬停时改变颜色 */
				}

				.user-avatar {
					margin-right: 8px;
				}

				.user-nickname {
					max-width: 100px; /* 限制昵称宽度，防止过长溢出 */
					white-space: nowrap;
					overflow: hidden;
					text-overflow: ellipsis;
				}

				.el-icon--right {
					margin-left: 5px;
					transition: transform 0.3s; /* 箭头旋转过渡 */
				}
			}

			/* 下拉菜单激活时，箭头旋转 */
			.el-dropdown.el-dropdown--visible .el-icon--right {
				transform: rotate(180deg);
			}
		}

		.login-register-section {
			margin-left: 20px;
			.el-button {
				font-size: 16px;
				margin-left: 10px;
			}
		}
	}

	/* 注销菜单项的特殊样式 */
	.logout-item {
		color: #f56c6c !important; /* 红色表示注销 */
		&:hover {
			background-color: #fef0f0 !important; /* 鼠标悬停时背景色 */
		}
	}

	.main-content {
		flex: 1;
		// padding-top: 60px; /* 抵消固定头部的高度 */
	}
</style>
