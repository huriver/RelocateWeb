<template>
	<div class="personal-info-container">
		<h2 class="page-title">个人信息</h2>

		<div class="info-section">
			<el-form
				:model="userInfoForm"
				:rules="userInfoRules"
				ref="userInfoFormRef"
				label-width="100px"
			>
				<el-row :gutter="20">
					<el-col :span="12">
						<el-form-item label="用户名" prop="username">
							<el-input v-model="userInfoForm.username" :disabled="!isEditingInfo"></el-input>
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="姓名" prop="name">
							<el-input v-model="userInfoForm.name" :disabled="!isEditingInfo"></el-input>
						</el-form-item>
					</el-col>
				</el-row>

				<el-row :gutter="20">
					<el-col :span="12">
						<el-form-item label="性别" prop="gender">
							<el-select
								v-model="userInfoForm.gender"
								placeholder="请选择性别"
								:disabled="!isEditingInfo"
								style="width: 100%"
							>
								<el-option label="男" value="男"></el-option>
								<el-option label="女" value="女"></el-option>
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="手机号" prop="phone">
							<el-input v-model="userInfoForm.phone" :disabled="!isEditingInfo"></el-input>
						</el-form-item>
					</el-col>
				</el-row>

				<el-row :gutter="20">
					<el-col :span="12">
						<el-form-item label="邮箱" prop="email">
							<el-input v-model="userInfoForm.email" :disabled="!isEditingInfo"></el-input>
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="身份证号" prop="idCard">
							<el-input v-model="userInfoForm.idCard" :disabled="!isEditingInfo"></el-input>
						</el-form-item>
					</el-col>
				</el-row>

				<el-row :gutter="20">
					<el-col :span="12">
						<el-form-item label="家庭电话" prop="familyPhone">
							<el-input v-model="userInfoForm.familyPhone" :disabled="!isEditingInfo"></el-input>
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="头像">
							<div class="avatar-upload-box">
								<img :src="userInfoForm.photoUrl || defaultAvatar" alt="头像" class="user-avatar" />
								<el-button
									type="primary"
									:icon="Upload"
									size="small"
									:disabled="!isEditingInfo"
									@click="handleAvatarUpload"
									style="margin-left: 20px"
								>
									上传头像
								</el-button>
								<input
									type="file"
									ref="avatarInput"
									style="display: none"
									@change="onFileChange"
									accept="image/*"
								/>
							</div>
						</el-form-item>
					</el-col>
				</el-row>

				<el-form-item class="form-action-buttons">
					<el-button type="primary" @click="toggleEditingInfo">{{
						isEditingInfo ? '取消编辑' : '编辑信息'
					}}</el-button>
					<el-button type="success" @click="saveUserInfo" :disabled="!isEditingInfo"
						>保存信息</el-button
					>
				</el-form-item>
			</el-form>
		</div>
	</div>
</template>

<script setup>
	import { ref, reactive, onMounted } from 'vue';
	import { defineOptions } from 'vue';
	// useRoute 未使用到，如果您的组件逻辑不需要，可以移除
	// import { useRoute } from 'vue-router';
	import { ElMessage, ElLoading } from 'element-plus';
	import { myStore } from '@/stores/store.js'; // 导入 store
	// 导入所有需要的 API 函数，包括上传API
	import { getUserInfoApi, updateUserInfoApi, uploadAvatarApi } from '@/api/userApi';
	import { Upload } from '@element-plus/icons-vue'; // 导入图标

	defineOptions({
		name: 'UserPersonalInfo',
	});

	const store = myStore();

	// 个人信息表单数据
	const userInfoForm = reactive({
		id: null, // 用户ID，从store获取或通过API返回
		username: '',
		name: '',
		gender: '',
		phone: '',
		email: '',
		idCard: '',
		familyPhone: '',
		photoUrl: '', // 用户头像 URL
	});

	// 个人信息表单验证规则
	const userInfoRules = reactive({
		username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
		name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
		gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
		phone: [
			{ required: true, message: '请输入手机号', trigger: 'blur' },
			{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' },
		],
		email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
		idCard: [
			{
				pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
				message: '身份证号格式不正确',
				trigger: 'blur',
			},
		],
		familyPhone: [
			{
				pattern: /^\d{3}-\d{8}$|^\d{4}-\d{7}$|^(\d{3,4}-)?\d{7,8}$/,
				message: '家庭电话格式不正确',
				trigger: 'blur',
			},
		],
	});

	// Element Plus 表单 Ref
	const userInfoFormRef = ref(null);
	const avatarInput = ref(null); // 文件输入框的 ref

	// 编辑状态控制
	const isEditingInfo = ref(false);
	// 用于取消编辑时恢复原始数据
	let originalUserInfo = {};

	// 默认头像 URL
	const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d4dafd62fb4fc7fd09c3d7313png.png'; // 替换为你的默认头像图片路径

	// 获取用户信息的函数
	const fetchUserInfo = async () => {
		// 假设后端 GET /front/customer 通过 token 获取当前用户，不需要传ID
		// 如果后端需要ID，请修改此处的API调用和userApi.js中的API定义
		// const userId = store.frontUserInfo?.id;
		// if (!userId) {
		//   ElMessage.error('无法获取用户信息，用户ID不存在！');
		//   return;
		// }

		const loading = ElLoading.service({
			lock: true,
			text: '加载中...',
			background: 'rgba(0, 0, 0, 0.7)',
		});

		try {
			// 调用获取用户信息API (不传ID)
			const res = await getUserInfoApi();
			if (res.data.code === 1) {
				// 填充表单数据
				Object.assign(userInfoForm, res.data.data);
				// 存储原始数据用于取消编辑
				originalUserInfo = { ...res.data.data };

				// !!! 关键：获取最新信息后更新 Pinia store 中的用户信息，供全局（如头部）使用 !!!
				// 假设您的 store 有一个直接修改 userInfo 对象的方法或允许直接修改
				// 如果 store.frontUserInfo 是 reactive 包裹的，直接赋值属性即可
				if (store.frontUserInfo) {
					// 更安全的做法是检查属性是否存在再赋值，或者使用 store 提供的方法
					Object.assign(store.frontUserInfo, res.data.data);
				} else {
					// 如果 store.frontUserInfo 是 null 或 undefined，可能需要初始化或处理登录状态
					console.warn('Pinia store 中的 userInfo 对象不存在或未初始化');
				}
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.data.msg || '获取用户信息失败'); // <-- 移除此行
				console.warn('获取用户信息业务失败:', res.data.msg); // 可以保留日志
			}
		} catch (error) {
			// 捕获真正的请求错误
			ElMessage.error('获取用户信息失败，请稍后重试！'); // <-- 这个用于网络或HTTP错误
			console.error('获取用户信息API调用失败:', error);
			// 获取失败时，可能需要清空本地表单数据或给出明确提示
			Object.assign(userInfoForm, {
				id: null,
				username: '',
				name: '',
				gender: '',
				phone: '',
				email: '',
				idCard: '',
				familyPhone: '',
				photoUrl: '',
			});
			originalUserInfo = {};
		} finally {
			loading.close();
		}
	};

	// 切换编辑状态
	const toggleEditingInfo = () => {
		isEditingInfo.value = !isEditingInfo.value;
		if (!isEditingInfo.value) {
			// 取消编辑时恢复原始数据
			Object.assign(userInfoForm, originalUserInfo);
			// 清除表单验证状态
			userInfoFormRef.value?.clearValidate();
		} else {
			// 进入编辑状态时，复制一份当前数据作为原始数据
			originalUserInfo = { ...userInfoForm };
		}
	};

	// 保存用户信息
	const saveUserInfo = async () => {
		if (!userInfoFormRef.value) return;

		userInfoFormRef.value.validate(async (valid) => {
			if (valid) {
				const loading = ElLoading.service({
					lock: true,
					text: '保存中...',
					background: 'rgba(0, 0, 0, 0.7)',
				});

				try {
					// 构造要发送的数据，包含所有可编辑字段，包括 username 和上传后的 photoUrl
					const updateData = {
						id: userInfoForm.id, // 必须包含用户ID
						username: userInfoForm.username,
						name: userInfoForm.name,
						gender: userInfoForm.gender,
						phone: userInfoForm.phone,
						email: userInfoForm.email,
						idCard: userInfoForm.idCard,
						familyPhone: userInfoForm.familyPhone,
						// 发送当前 userInfoForm 中的 photoUrl (已经通过上传接口更新)
						photoUrl: userInfoForm.photoUrl,
						// 不要发送 password 字段！
					};
					const res = await updateUserInfoApi(updateData); // 调用更新用户信息API

					if (res.data.code === 1) {
						ElMessage.success('用户信息更新成功！');
						isEditingInfo.value = false; // 保存成功后退出编辑状态

						// !!! 关键：用户信息更新成功后，重新获取最新数据并更新 store !!!
						// fetchUserInfo 会重新拉取后端数据并更新本地表单和 store
						// 此时 Store 中的 photoUrl 会被更新为后端保存的最终值
						await fetchUserInfo();
					} else {
						// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
						// ElMessage.error(res.data.msg || '用户信息更新失败'); // <-- 移除此行
						console.warn('用户信息更新业务失败:', res.data.msg); // 可以保留日志
					}
				} catch (error) {
					// 捕获真正的请求错误
					ElMessage.error('用户信息更新失败，请稍后重试！'); // <-- 这个用于网络或HTTP错误
					console.error('更新用户信息API调用失败:', error);
				} finally {
					loading.close();
				}
			} else {
				ElMessage.warning('请检查填写的信息');
			}
		});
	};

	// 触发头像文件选择
	const handleAvatarUpload = () => {
		// 只有在编辑状态下才允许上传
		if (isEditingInfo.value) {
			avatarInput.value?.click();
		}
	};

	// 处理文件选择后的逻辑 (实现实际的上传API调用)
	const onFileChange = async (event) => {
		const file = event.target.files[0];
		if (!file) return;

		// 检查文件大小和类型 (可选，但推荐)
		// const allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];
		// const maxSize = 2 * 1024 * 1024; // 2MB
		// if (!allowedTypes.includes(file.type)) {
		//   ElMessage.error('只能上传 JPG, PNG, GIF 格式的图片！');
		//   event.target.value = '';
		//   return;
		// }
		// if (file.size > maxSize) {
		//   ElMessage.error('图片大小不能超过 2MB！');
		//   event.target.value = '';
		//   return;
		// }

		const loading = ElLoading.service({
			// 添加加载状态
			lock: true,
			text: '正在上传头像...',
			background: 'rgba(0, 0, 0, 0.7)',
		});

		try {
			// 1. 创建 FormData 对象，将文件放进去
			const formData = new FormData();
			formData.append('file', file); // 这里的 'file' 对应后端接收文件的字段名

			// 2. 调用头像上传 API
			const uploadRes = await uploadAvatarApi(formData);

			if (uploadRes.data.code === 1) {
				ElMessage.success('头像上传成功！');
				// 3. 更新 userInfoForm.photoUrl 为后端返回的新头像URL
				// 根据您的API文档，成功时返回的URL在 res.data.data
				const newPhotoUrl = uploadRes.data.data;
				userInfoForm.photoUrl = newPhotoUrl; // !!! 只更新本地表单状态 !!!

				// !!! 根据您的要求，这里不再直接更新 Store. Store 的更新将依赖于 saveUserInfo 成功后的 fetchUserInfo !!!
				// if (store.frontUserInfo) {
				//   store.frontUserInfo.photoUrl = newPhotoUrl;
				// } else {
				//   console.warn('Pinia store 中的 userInfo 对象不存在或未初始化，无法更新头像 URL');
				// }

				// 4. 更新原始数据，防止在未保存个人信息时取消编辑导致头像回退
				originalUserInfo.photoUrl = newPhotoUrl;

				// 注意：此时 Store 中的 photoUrl 仍是旧值。
				// 它将在用户点击"保存信息"，并且 saveUserInfo 成功后，通过 fetchUserInfo 统一更新。
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(uploadRes.data.msg || '头像上传失败'); // <-- 移除此行
				console.warn('头像上传业务失败:', uploadRes.data.msg); // 可以保留日志
				// 上传失败时，将 photoUrl 恢复到上传前的原始值或默认值
				userInfoForm.photoUrl = originalUserInfo.photoUrl || defaultAvatar;
				// 此时 Store 中的 photoUrl 不需要恢复，因为它压根没在这里更新过
			}
		} catch (error) {
			// 捕获真正的请求错误
			ElMessage.error('头像上传失败，请稍后重试！'); // <-- 这个用于网络或HTTP错误
			console.error('头像上传API调用失败:', error);
			// 上传失败时，将 photoUrl 恢复到上传前的原始值或默认值
			userInfoForm.photoUrl = originalUserInfo.photoUrl || defaultAvatar;
			// 此时 Store 中的 photoUrl 不需要恢复
		} finally {
			loading.close();
			// 清空文件输入框，以便再次选择同一个文件
			event.target.value = '';
		}
	};

	// 组件挂载时获取用户信息
	onMounted(() => {
		fetchUserInfo();
	});
</script>

<style scoped lang="less">
	.personal-info-container {
		padding: 20px;
		background-color: #fff;
		border-radius: 8px;
		box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);

		.page-title {
			font-size: 24px;
			color: #333;
			margin-bottom: 20px;
			text-align: center; /* 标题居中 */
		}

		.info-section {
			margin-bottom: 30px;
			padding: 20px;
			border: 1px solid #eee;
			border-radius: 8px;
		}

		.avatar-upload-box {
			display: flex;
			align-items: center;
			.user-avatar {
				width: 100px; // 头像更大
				height: 100px; // 头像更大
				border-radius: 50%;
				object-fit: cover;
				border: 3px solid #eee; // 更明显的头像边框
				flex-shrink: 0; // 防止头像被压缩
			}
			.el-button {
				margin-left: 30px !important; // 调整按钮与头像的距离
			}
		}

		// 调整 Element Plus 表单项的样式
		:deep(.el-form-item__label) {
			font-weight: bold;
		}

		:deep(.el-input.is-disabled .el-input__inner) {
			color: #606266; /* Disabled input text color */
			-webkit-text-fill-color: #606266; /* For Chrome autofill */
			background-color: #f5f7fa; /* Disabled background color */
		}

		:deep(.el-select .el-input.is-disabled .el-input__inner) {
			// 调整 disabled select 的样式，确保文本颜色正确
			color: #606266;
			-webkit-text-fill-color: #606266;
		}

		/* 新增：按钮居中样式 */
		.form-action-buttons {
			display: flex;
			justify-content: center;
			margin-top: 30px;
			:deep(.el-form-item__content) {
				justify-content: center;
				margin-left: 0 !important;
			}
			.el-button {
				margin: 0 10px;
			}
		}
	}
</style>
