<template>
	<div class="change-password-container">
		<h2 class="page-title">修改密码</h2>

		<el-form
			:model="passwordForm"
			:rules="passwordRules"
			ref="passwordFormRef"
			label-width="100px"
			class="password-form"
		>
			<el-form-item label="原密码" prop="oldPassword">
				<el-input type="password" v-model="passwordForm.oldPassword" show-password></el-input>
			</el-form-item>
			<el-form-item label="新密码" prop="newPassword">
				<el-input type="password" v-model="passwordForm.newPassword" show-password></el-input>
			</el-form-item>
			<el-form-item label="确认新密码" prop="rePassword">
				<el-input type="password" v-model="passwordForm.rePassword" show-password></el-input>
			</el-form-item>
			<el-form-item>
				<el-button type="primary" @click="changePassword">修改密码</el-button>
				<el-button plain @click="resetPasswordForm">重置</el-button>
			</el-form-item>
		</el-form>
	</div>
</template>

<script setup>
	import { ref, reactive } from 'vue';
	import { defineOptions } from 'vue';
	import { useRouter } from 'vue-router'; // 用于跳转登录页
	import { ElMessage, ElLoading } from 'element-plus';
	import { myStore } from '@/stores/store.js'; // 导入 store
	import { changePasswordApi } from '@/api/userApi'; // 导入修改密码API

	defineOptions({
		name: 'UserChangePassword',
	});

	const router = useRouter();
	const store = myStore();

	// 修改密码表单数据
	const passwordForm = reactive({
		oldPassword: '',
		newPassword: '',
		rePassword: '',
	});

	// 修改密码表单验证规则
	const passwordRules = reactive({
		oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
		newPassword: [
			{ required: true, message: '请输入新密码', trigger: 'blur' },
			{ min: 4, max: 20, message: '密码长度在 4 到 20 个字符', trigger: 'blur' },
		],
		rePassword: [
			{ required: true, message: '请再次输入新密码', trigger: 'blur' },
			{
				validator: (rule, value, callback) => {
					if (value === '') {
						callback(new Error('请再次输入新密码'));
					} else if (value !== passwordForm.newPassword) {
						callback(new Error('两次输入的密码不一致!'));
					} else {
						callback();
					}
				},
				trigger: 'blur',
			},
		],
	});

	// Element Plus 表单 Ref
	const passwordFormRef = ref(null);

	// 修改密码
	const changePassword = async () => {
		if (!passwordFormRef.value) return;

		passwordFormRef.value.validate(async (valid) => {
			if (valid) {
				const loading = ElLoading.service({
					lock: true,
					text: '修改中...',
					background: 'rgba(0, 0, 0, 0.7)',
				});

				try {
					console.log('开始调用 changePasswordApi');
					const res = await changePasswordApi(passwordForm);
					console.log('changePasswordApi 调用成功，响应:', res);
					if (res.data.code === 1) {
						console.log('后端返回成功 code 1，显示成功消息');
						ElMessage.success('密码修改成功！请使用新密码重新登录');
						// ... 清除 store 和跳转 ...
						// 确保在显示成功消息和跳转之前，不会意外进入 catch
						// await new Promise(resolve => setTimeout(resolve, 100)); // 可选：加短暂延迟看是否影响
						store.clear();
						router.push({ name: 'userLogin' });
						console.log('已执行清除 store 和跳转');
					} else {
						console.log('后端返回非成功 code，显示后端错误消息');
						ElMessage.error(res.data.msg || '密码修改失败');
					}
				} catch (error) {
					console.error('changePasswordApi 调用捕获到异常:', error); // <--- 查看这里是否被执行
					ElMessage.error('密码修改失败，请稍后重试！'); // <--- 这个消息是从这里发出的吗？
				} finally {
					console.log('finally 块执行'); // <--- 确保 finally 块始终执行
					loading.close();
				}
			} else {
				console.log('表单验证失败');
				ElMessage.warning('请检查填写的密码信息');
			}
			console.log('changePassword 函数执行结束'); // <--- 确保函数执行结束
		});
	};

	// 重置密码表单
	const resetPasswordForm = () => {
		passwordFormRef.value?.resetFields();
		// resetFields 可能不会完全清空，手动再清空一次确保
		passwordForm.oldPassword = '';
		passwordForm.newPassword = '';
		passwordForm.rePassword = '';
	};
</script>

<style scoped lang="less">
	.change-password-container {
		padding: 20px;
		background-color: #fff;
		border-radius: 8px;
		box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
		max-width: 600px; /* 限制表单最大宽度 */
		margin: 20px auto; /* 居中显示 */

		.page-title {
			font-size: 24px;
			color: #333;
			margin-bottom: 20px;
			text-align: center;
		}

		.password-form {
			padding: 0 20px; /* 给表单一些内边距 */

			// 添加以下样式
			.el-form-item:last-child {
				display: flex;
				justify-content: center;

				:deep(.el-form-item__content) {
					margin-left: 0 !important;
					justify-content: center;
				}
			}
		}

		:deep(.el-form-item__label) {
			font-weight: bold;
		}
	}
</style>
