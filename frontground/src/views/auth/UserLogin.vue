<script setup>
	import { ref } from 'vue';
	import router from '@/router';
	import { myStore } from '@/stores/store.js';
	import { userLoginApi } from '@/api/userApi.js';
	// 导入 ElMessage (虽然在这个函数里不直接使用，但在其他地方可能需要，保留导入)
	import { ElMessage } from 'element-plus';

	const loginForm = ref({});
	const loginFormRef = ref(null);
	const rules = ref({
		username: { required: true, message: '请输入用户名', trigger: 'blur' },
		password: { required: true, message: '请输入密码', trigger: 'blur' },
	});

	const store = myStore(); // 获取 store 实例

	const login = (formEl) => {
		if (!formEl) return;
		formEl.validate(async (valid, fields) => {
			if (valid) {
				// 表单校验成功

				try {
					// userLoginApi 的 Promise 如果是业务失败 (code !== 1) 但非登录过期/401，会 resolve 并返回响应
					// 如果是网络错误、HTTP 错误、登录过期/401 业务错误，会 reject
					const { data: res } = await userLoginApi({
						username: loginForm.value.username,
						password: loginForm.value.password,
						role: 'customer', // 消费者登录，角色固定为 customer
					});

					if (res.code !== 1) {
						// 业务失败 (code !== 1 且非登录过期/401)，request.js 已经弹窗提示了后端 msg
						// 只需要阻止后续业务成功逻辑的执行
						// return ElMessage.error(res.msg); // <-- 移除此行，避免重复提示
						console.warn('消费者登录业务失败:', res.msg); // 可以保留日志
						return; // <-- 保留 return，阻止继续执行成功后的逻辑
					}

					// 代码执行到这里，说明 res.code === 1 (业务成功)
					ElMessage.success('登录成功');

					// 调用 saveFrontUserInfo 方法保存前台用户信息
					// 假设后端返回的 res.data 包含了前台用户的所有信息，包括令牌等
					store.saveFrontUserInfo(res.data);

					// === 修改点：检查并使用 frontRoutePath 进行重定向 ===
					const redirectPath = store.frontRoutePath; // 从 store 中获取之前保存的路径
					// 清除保存的路径，避免下次直接访问登录页时仍然跳转到旧路径
					store.saveFrontRoutePath(null);

					if (redirectPath) {
						// 如果存在保存的路径，跳转到该路径
						console.log(`消费者登录成功，重定向到之前访问的页面: ${redirectPath}`);
						// 使用 replace 避免用户返回到登录页
						router.replace(redirectPath);
					} else {
						// 如果没有保存的路径 (用户直接访问登录页登录)，跳转到默认前台主页
						console.log('消费者登录成功，没有保存的跳转路径，重定向到默认前台主页');
						// 使用 replace 避免用户返回到登录页
						router.replace('/userHome/front'); // 默认前台首页路径
					}
					// ==============================================

					// 清空表单 (可以在跳转前或后，这里选择在跳转逻辑之后)
					// 注意：如果使用 replace 跳转，这行可能不会立即执行，但通常不是问题
					// 更好的做法可能是在跳转前清空，或者在组件 unmount 时清空
					// loginForm.value = {}; // 如果希望清空输入框，可以在跳转前调用

					// 移除原有的 router.push('/userHome');
				} catch (error) {
					// API 请求失败 (网络错误或其他非 401/403 错误，这些由响应拦截器处理并 reject)
					// request.js 响应拦截器会显示错误消息，这里不再重复 ElMessage.error
					// 如果响应拦截器处理了 401/403 并跳转，下面的代码不会执行
					console.error('消费者登录请求失败:', error);
					// 这里的 catch 主要用于捕获那些未被 request.js 拦截器中的 reject 逻辑精确处理的异常
					// 但根据你 request.js 的当前实现，主要错误类型都会被拦截并提示+reject
					// 所以这里不再需要额外的 ElMessage.error 提示。
				}
			} else {
				// 表单校验失败
				console.log('form validation error!', fields);
				// Element Plus 默认会在输入框下方显示错误信息
				// 如果需要额外的全局提示，可以在这里添加 ElMessage.error('请检查表单项');
			}
		});
	};

	// 可选：在组件销毁前清空表单或状态
	// onBeforeUnmount(() => {
	//    loginForm.value = {};
	// });
</script>

<template>
	<div class="container">
		<el-card style="width: 380px">
			<template #header>
				<div class="card-header">易搬家 - 登录</div>
			</template>
			<el-form ref="loginFormRef" :model="loginForm" status-icon :rules="rules" label-width="auto">
				<el-form-item label="用户名" prop="username">
					<el-input
						v-model="loginForm.username"
						placeholder="请输入用户名"
						autocomplete="off"
						@keypress.enter="login(loginFormRef)"
					/>
				</el-form-item>
				<el-form-item label="密码" prop="password">
					<el-input
						v-model="loginForm.password"
						type="password"
						placeholder="请输入密码"
						autocomplete="off"
						show-password
						@keypress.enter="login(loginFormRef)"
					/>
				</el-form-item>
				<el-form-item style="margin-bottom: 0">
					<el-button
						type="primary"
						@click="login(loginFormRef)"
						style="width: 100%; height: 45px; font-size: 16px"
					>
						登录
					</el-button>
				</el-form-item>
			</el-form>
			<template #footer>
				<div class="card-footer-link">
					<router-link to="/register">立即注册</router-link>
				</div>
			</template>
		</el-card>
	</div>
</template>

<style lang="less" scoped>
	.container {
		position: relative;
		width: 100%;
		height: 100vh;
		display: flex;
		justify-content: center;
		align-items: center;

		background-image: url(../../assets/img/bg.jpg);
		background-size: cover;
		background-position: center;
		background-repeat: no-repeat;
		overflow: hidden;

		&::before {
			content: '';
			position: absolute;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			background-color: rgba(0, 0, 0, 0.4);
			z-index: 0;
		}

		.el-card {
			position: relative;
			z-index: 1;

			width: 380px;
			padding: 30px;
			border-radius: 12px;
			box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
			background-color: #fff;

			:deep(.el-card__header) {
				border-bottom: none;
				padding-bottom: 0;
			}

			.card-header {
				font-size: 28px;
				font-weight: bold;
				text-align: center;
				color: #333;
				margin-bottom: 30px;
			}

			.el-form-item {
				margin-bottom: 22px;
			}

			.card-footer-link {
				text-align: center;
				padding-top: 20px;
				border-top: 1px solid #eee;

				.router-link-active {
					color: #409eff;
					text-decoration: none;
					font-size: 14px;
					&:hover {
						text-decoration: underline;
					}
				}
			}
		}
	}
</style>
