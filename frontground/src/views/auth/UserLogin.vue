<script setup>
	import { ref } from 'vue';
	import router from '@/router';
	import { myStore } from '@/stores/store.js';
	import { userLoginApi } from '@/api/userApi.js';

	const loginForm = ref({});
	const loginFormRef = ref(null);
	const rules = ref({
		username: { required: true, message: '请输入用户名', trigger: 'blur' },
		password: { required: true, message: '请输入密码', trigger: 'blur' },
	});

	const store = myStore();

	const login = (formEl) => {
		if (!formEl) return;
		formEl.validate(async (valid, fields) => {
			if (valid) {
				// 表单校验成功

				const { data: res } = await userLoginApi({
					username: loginForm.value.username,
					password: loginForm.value.password,
					role: 'customer', // 消费者登录，角色固定为 customer
				});
				if (res.code !== 1) {
					return ElMessage.error(res.msg);
				}
				// 保存用户信息
				store.saveUserInfo(res.data);
				loginForm.value = {};
				router.push('/userHome');
			} else {
				// 表单校验失败
				console.log('error submit!', fields);
			}
		});
	};
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

			width: 380px;
			padding: 30px;
			border-radius: 12px; // 增加圆角，使卡片更柔和
			box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15); // 更明显但柔和的阴影效果
			background-color: #fff; // 确保卡片背景为白色

			// 移除 Element Plus 默认的头部下边框和内边距
			:deep(.el-card__header) {
				border-bottom: none;
				padding-bottom: 0;
			}

			.card-header {
				font-size: 28px; // 标题字体更大一些
				font-weight: bold;
				text-align: center;
				color: #333; // 更深沉的标题颜色
				margin-bottom: 30px; // 标题下方留出更多空间
			}

			.el-form-item {
				margin-bottom: 22px; // 保持表单项间距一致
			}

			.card-footer-link {
				text-align: center;
				padding-top: 20px; // 底部链接区域顶部内边距
				border-top: 1px solid #eee; // 添加一条分割线，与 ElCard 默认 footer 样式更统一

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
