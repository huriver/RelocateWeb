<script setup>
	import { ref } from 'vue';
	import router from '@/router';
	import { myStore } from '@/stores/store.js'; // 如果注册接口不需要 token，其实可以不导入和使用 store
	import { userRegisterApi } from '@/api/userApi.js';
	import { ElMessage } from 'element-plus'; // 确保导入 ElMessage

	const registerForm = ref({});
	const registerFormRef = ref(null);

	// 密码确认校验规则
	const validatePass = (rule, value, callback) => {
		if (value === '') {
			callback(new Error('请输入密码'));
		} else {
			if (registerForm.value.confirmPassword !== '') {
				if (!registerFormRef.value) return; // 确保 ref 存在
				registerFormRef.value.validateField('confirmPassword'); // 触发表单中确认密码的验证
			}
			callback();
		}
	};
	const validatePass2 = (rule, value, callback) => {
		if (value === '') {
			callback(new Error('请再次输入密码'));
		} else if (value !== registerForm.value.password) {
			callback(new Error('两次输入的密码不一致!'));
		} else {
			callback();
		}
	};

	const rules = ref({
		username: { required: true, message: '请输入用户名', trigger: 'blur' },
		password: [
			{ required: true, validator: validatePass, trigger: 'blur' },
			{ min: 4, message: '密码长度不能少于4位', trigger: 'blur' }, // 添加密码长度校验
		],
		confirmPassword: [{ required: true, validator: validatePass2, trigger: 'blur' }],
	});

	// const store = myStore(); // 如果注册接口不需要 token，可以移除

	const registerUser = (formEl) => {
		if (!formEl) return; // 如果表单引用不存在则返回
		formEl.validate(async (valid, fields) => {
			if (valid) {
				// 表单校验成功

				try {
					// 根据 request.js 中对 /auth/register 的特殊处理逻辑
					// role: 'customer' 的 POST 请求不需要 token
					const { data: res } = await userRegisterApi(
						{
							username: registerForm.value.username,
							password: registerForm.value.password,
							role: 'customer', // 消费者注册，角色固定为 customer
						}
						// { // 根据 request.js 的逻辑，对于 role: 'customer' 的注册请求，不需要在 headers 里加 role 和 token
						// 	headers: {
						// 		role: 'customer', // 这个 'role' 是在请求头 (header) 中
						// 	},
						// } // 移除 headers 部分，依赖 request.js 的请求体判断
					);

					if (res.code === 1) {
						// 后端返回成功
						ElMessage.success('注册成功！请登录。'); // 注册成功提示
						registerForm.value = {}; // 清空表单
						router.push('/login'); // 注册成功后跳转到登录页
					} else {
						// 后端返回业务错误 (code !== 1)
						// request.js 拦截器已经弹窗提示了后端 msg
						console.warn('注册业务失败:', res.msg); // 可以保留日志
						// 这里不需要再次 ElMessage.error
					}
				} catch (error) {
					// 捕获真正的请求错误 (网络错误或其他未在 res.code 中处理的异常，如 HTTP 错误)
					console.error('注册请求发送失败:', error);
					// request.js 已经在这些错误时弹窗了，这里的 ElMessage 可以作为兜底
					ElMessage.error('注册请求发送失败，请检查网络或联系管理员。'); // <-- 这个用于网络或HTTP错误
				}
			} else {
				// 表单校验失败，Element Plus 会自动在表单字段下方显示错误信息
				console.log('表单校验失败!', fields);
			}
		});
	};
</script>

<template>
	<div class="container">
		<el-card style="width: 380px">
			<template #header>
				<div class="card-header">加入易搬家</div>
			</template>
			<el-form
				ref="registerFormRef"
				:model="registerForm"
				status-icon
				:rules="rules"
				label-width="auto"
			>
				<el-form-item label="用户名" prop="username">
					<el-input
						v-model="registerForm.username"
						placeholder="请输入用户名"
						autocomplete="off"
						@keypress.enter="registerUser(registerFormRef)"
					/>
				</el-form-item>
				<el-form-item label="密码" prop="password">
					<el-input
						v-model="registerForm.password"
						type="password"
						placeholder="请输入密码"
						autocomplete="off"
						show-password
						@keypress.enter="registerUser(registerFormRef)"
					/>
				</el-form-item>
				<el-form-item label="确认密码" prop="confirmPassword">
					<el-input
						v-model="registerForm.confirmPassword"
						type="password"
						placeholder="请确认密码"
						autocomplete="off"
						show-password
						@keypress.enter="registerUser(registerFormRef)"
					/>
				</el-form-item>
				<el-form-item style="margin-bottom: 0">
					<el-button
						type="primary"
						@click="registerUser(registerFormRef)"
						style="width: 100%; height: 45px; font-size: 16px"
					>
						确定注册
					</el-button>
				</el-form-item>
			</el-form>
			<template #footer>
				<div class="card-footer-link">
					<router-link to="/login">有账号？去登录</router-link>
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
		display: flex; // 使用 flexbox 替代 transform 进行居中
		justify-content: center;
		align-items: center;

		// 保留背景图片
		background-image: url(../../assets/img/bg.jpg);
		background-size: cover;
		background-position: center;
		background-repeat: no-repeat; // 确保背景图片不重复
		overflow: hidden; // 防止背景图片溢出导致滚动条

		// 添加半透明蒙层
		&::before {
			content: '';
			position: absolute;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			// 调整这里的颜色和透明度来达到您想要的效果
			// 0.4 的黑色蒙层可以有效降低图片亮度，增强对比度
			background-color: rgba(0, 0, 0, 0.4);
			z-index: 0; // 确保蒙层在背景图片之上，在卡片之下
		}

		.el-card {
			position: relative; // 确保卡片位于蒙层之上
			z-index: 1; // 提升卡片的层级

			width: 380px; // 统一卡片宽度
			padding: 30px; // 增加内边距
			border-radius: 12px; // 增加圆角
			box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15); // 更明显但柔和的阴影
			background-color: #fff; // 确保卡片背景为白色

			// 移除 Element Plus 默认的头部下边框和内边距
			:deep(.el-card__header) {
				border-bottom: none;
				padding-bottom: 0;
			}

			.card-header {
				font-size: 28px; // 标题字体更大
				font-weight: bold;
				text-align: center;
				color: #333; // 更深沉的标题颜色
				margin-bottom: 30px; // 标题下方留出更多空间
			}

			.el-form-item {
				margin-bottom: 22px; // 保持表单项间距一致
			}

			.card-footer-link {
				// 新增这个类来统一底部链接样式
				text-align: center;
				padding-top: 20px;
				border-top: 1px solid #eee;

				.router-link-active {
					// 或者直接 router-link
					color: #409eff; // Element Plus 默认的蓝色链接颜色
					text-decoration: none; // 移除下划线
					font-size: 14px; // 链接字体大小
					&:hover {
						text-decoration: underline; // 鼠标悬停时显示下划线
					}
				}
			}
		}
	}
</style>
