<script setup>
	import { ref, watch, onMounted, computed } from 'vue';
	import { useRoute, useRouter } from 'vue-router';
	import { myStore } from '@/stores/store.js';
	import { ElMessage, ElMessageBox } from 'element-plus';
	import { ArrowDown } from '@element-plus/icons-vue';

	import { userLogoutApi } from '@/api/userApi.js';

	const store = myStore();
	const route = useRoute();
	const router = useRouter();

	// 使用计算属性确保响应式地从 store 读取路由路径
	const activeRouter = computed(() => store.frontRoutePath || '/userHome/front');

	watch(
		() => route.path,
		(newPath) => {
			// activeRouter.value = newPath; // 不再需要直接修改 ref，使用 computed
			// 仅当路由不是后台路由时才保存前台路径
			if (!newPath.startsWith('/admin')) {
				store.saveFrontRoutePath(newPath);
			}
		},
		{ immediate: true }
	);

	const isFrontUserLoggedIn = computed(() => {
		return !!store.frontUserInfo && !!store.frontUserInfo.token;
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
				// 用户点击“确定”
				try {
					// 调用后端注销 API
					const { data: res } = await userLogoutApi();

					if (res.code === 1) {
						ElMessage.success('已成功注销！');
						// === 关键修改：只在后端成功时执行清除状态和跳转 ===
						store.clearFrontSession(); // 清除前台会话
						router.push('/login'); // 跳转到登录页
						// ===============================================
					} else {
						// 后端返回业务失败 (code !== 1)
						// request.js 已经在拦截器中弹出了错误信息，这里无需重复弹窗
						// ElMessage.warning(res.msg || '注销失败，请重试。'); // <-- 移除此行
						console.warn('注销业务失败:', res.msg); // 可以保留日志
						// TODO: 如果后端错误码表示会话无效，可以在这里也清除本地状态并跳转 (可选，取决于业务需求)
					}
				} catch (error) {
					// 网络错误或请求失败 (request.js 明确 reject 的情况)
					console.error('注销请求失败:', error);
					ElMessage.error('网络错误或注销请求失败，请检查网络。'); // <-- 这个用于网络或HTTP错误等
					// TODO: 根据需要决定网络错误时是否清除本地状态并跳转 (可选，取决于业务需求)
				}
				// 移除 API 调用的 finally 块，将清除和跳转逻辑放到成功分支 (已在原代码中完成)
			})
			.catch((action) => {
				// 用户点击“取消”或关闭对话框
				if (action === 'cancel') {
					ElMessage.info('已取消注销。');
				}
				// 确认对话框取消时不进行任何其他操作
			});
	};
</script>

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

				<div class="user-info-section" v-if="isFrontUserLoggedIn">
					<el-dropdown trigger="click" @command="handleCommand">
						<span class="el-dropdown-link">
							<el-avatar
								:size="40"
								:src="
									store.frontUserInfo?.photoUrl ||
									'https://cube.elemecdn.com/0/88/03fa4b90ddb33084d674608e9499dpng.png'
								"
								class="user-avatar"
							></el-avatar>
							<span class="user-nickname">{{
								store.frontUserInfo?.name || store.frontUserInfo?.username || '用户'
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
		padding-top: 60px; /* 抵消固定头部的高度 */
	}
</style>
