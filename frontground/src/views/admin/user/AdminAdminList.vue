<template>
	<div class="admin-admin-list-container">
		<h3>管理员管理</h3>

		<el-form :inline="true" :model="searchForm" class="search-form">
			<el-form-item label="用户名">
				<el-input v-model="searchForm.username" placeholder="请输入用户名" clearable></el-input>
			</el-form-item>
			<el-form-item label="姓名">
				<el-input v-model="searchForm.name" placeholder="请输入姓名" clearable></el-input>
			</el-form-item>
			<el-form-item label="状态">
				<el-select v-model="searchForm.isBanned" placeholder="请选择状态" clearable>
					<el-option label="正常" :value="false"></el-option>
					<el-option label="已封禁" :value="true"></el-option>
				</el-select>
			</el-form-item>

			<el-form-item class="button-group">
				<el-button type="primary" @click="handleSearch">查询</el-button>
				<el-button @click="resetSearchForm">重置</el-button>
				<el-button type="success" @click="handleAddAdmin" class="add-button-separate"
					>新增管理员</el-button
				>
			</el-form-item>
		</el-form>

		<el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
			<el-table-column prop="id" label="ID" align="center" width="80"></el-table-column>
			<el-table-column label="头像" width="100" align="center">
				<template #default="scope">
					<img
						v-if="scope.row.photoUrl"
						:src="scope.row.photoUrl"
						alt="管理员头像"
						style="width: auto; height: 45px; border-radius: 50%; object-fit: cover"
					/>
					<span v-else>无头像</span>
				</template>
			</el-table-column>
			<el-table-column prop="username" label="用户名" width="120" align="center"></el-table-column>
			<el-table-column prop="name" label="姓名" align="center" width="120"></el-table-column>
			<el-table-column label="状态" align="center" width="100">
				<template #default="scope">
					<el-tag :type="scope.row.isBanned ? 'danger' : 'success'">
						{{ scope.row.isBanned ? '已封禁' : '正常' }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column
				prop="createTime"
				label="创建时间"
				width="180"
				align="center"
			></el-table-column>
			<el-table-column
				prop="updateTime"
				label="更新时间"
				width="180"
				align="center"
			></el-table-column>

			<el-table-column label="操作" width="350" align="center" fixed="right">
				<template #default="scope">
					<el-button size="small" @click="showDetails(scope.row)" class="detail-button"
						>详情</el-button
					>
					<el-button
						size="small"
						type="warning"
						@click="handleResetPassword(scope.row)"
						style="margin-left: 15px"
						:disabled="scope.row.id === 1 || scope.row.id === currentAdminId"
					>
						重置密码
					</el-button>
					<el-switch
						v-model="scope.row.isBanned"
						active-text="已封禁"
						inactive-text="正常"
						:active-value="true"
						:inactive-value="false"
						@change="handleStatusChange(scope.row)"
						:before-change="() => beforeStatusChange(scope.row)"
						style="margin-left: 20px"
						:disabled="scope.row.id === 1 || scope.row.id === currentAdminId"
					></el-switch>
				</template>
			</el-table-column>
		</el-table>

		<el-pagination
			@size-change="handleSizeChange"
			@current-change="handleCurrentChange"
			:current-page="pagination.page"
			:page-sizes="[10, 20, 50, 100]"
			:page-size="pagination.pageSize"
			layout="total, sizes, prev, pager, next, jumper"
			:total="pagination.total"
			background
			class="pagination"
		></el-pagination>

		<el-dialog v-model="dialogVisible" title="管理员详情" width="500px">
			<el-form label-width="100px" v-if="currentRowDetails">
				<el-form-item label="ID">{{ currentRowDetails.id }}</el-form-item>
				<el-form-item label="用户名">{{ currentRowDetails.username }}</el-form-item>
				<el-form-item label="姓名">{{ currentRowDetails.name }}</el-form-item>
				<el-form-item label="头像">
					<img
						v-if="currentRowDetails.photoUrl"
						:src="currentRowDetails.photoUrl"
						alt="管理员头像"
						style="width: 60px; height: 60px; border-radius: 50%; object-fit: cover"
					/>
					<span v-else>无头像</span>
				</el-form-item>
				<el-form-item label="状态">
					<el-tag :type="currentRowDetails.isBanned ? 'danger' : 'success'">
						{{ currentRowDetails.isBanned ? '已封禁' : '正常' }}
					</el-tag>
				</el-form-item>
				<el-form-item label="创建时间">{{ currentRowDetails.createTime }}</el-form-item>
				<el-form-item label="更新时间">{{ currentRowDetails.updateTime }}</el-form-item>
				<el-form-item label="创建人ID">{{ currentRowDetails.createUser }}</el-form-item>
				<el-form-item label="更新人ID">{{ currentRowDetails.updateUser }}</el-form-item>
			</el-form>
			<template #footer>
				<div class="dialog-footer">
					<el-button @click="dialogVisible = false">关闭</el-button>
				</div>
			</template>
		</el-dialog>

		<el-dialog v-model="addDialogVisible" title="新增管理员" width="400px" @close="resetAddForm">
			<el-form :model="addForm" :rules="addFormRules" ref="addFormRef" label-width="80px">
				<el-form-item label="用户名" prop="username">
					<el-input v-model="addForm.username" placeholder="请输入用户名"></el-input>
				</el-form-item>
				<el-form-item label="密码" prop="password">
					<el-input
						v-model="addForm.password"
						type="password"
						placeholder="请输入密码"
						show-password
					></el-input>
				</el-form-item>
			</el-form>
			<template #footer>
				<div class="dialog-footer">
					<el-button @click="addDialogVisible = false">取消</el-button>
					<el-button type="primary" @click="submitAddForm">确定</el-button>
				</div>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
	import { ref, onMounted, computed } from 'vue';
	import {
		ElMessage,
		ElMessageBox,
		ElDialog,
		ElForm,
		ElFormItem,
		ElTag,
		ElInput,
		ElSelect,
		ElButton,
		ElTable,
		ElTableColumn,
		ElPagination,
		ElSwitch,
		ElAvatar,
	} from 'element-plus';
	// 导入管理员 API
	import {
		getAdminListPageApi,
		updateAdminStatusApi,
		resetAdminPasswordApi,
	} from '@/api/adminApi.js'; // 导入 resetAdminPasswordApi
	// 导入用户注册 API (通用接口)
	import { userRegisterApi } from '@/api/userApi.js';
	// 导入 store
	import { myStore } from '@/stores/store.js'; // 导入 myStore

	const store = myStore(); // 获取 store 实例
	// 获取当前登录管理员的 ID
	const currentAdminId = computed(() => store.backUserInfo?.id || null); // 使用计算属性获取当前登录用户的ID

	// 表格数据和加载状态
	const tableData = ref([]);
	const loading = ref(false);

	// 分页数据
	const pagination = ref({
		page: 1,
		pageSize: 10,
		total: 0,
	});

	// 搜索表单数据
	const searchForm = ref({
		username: '',
		name: '',
		isBanned: null, // null 表示不筛选状态
		// 管理员搜索条件比较简单，没有其他字段
	});

	// 详情弹窗状态和当前选中行数据
	const dialogVisible = ref(false); // 控制详情弹窗显示
	const currentRowDetails = ref(null); // 存储当前查看详情的行数据

	// === 新增管理员功能相关状态和方法 ===
	const addDialogVisible = ref(false); // 控制新增弹窗显示
	const addFormRef = ref(null); // 新增表单的引用
	const addForm = ref({
		username: '',
		password: '',
		// 移除name字段
		// name: '',
	});
	// 新增表单校验规则
	const addFormRules = ref({
		username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
		password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
		// 移除name校验规则
		// name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
	});

	// 打开新增管理员弹窗
	const handleAddAdmin = () => {
		resetAddForm(); // 打开前先重置表单
		addDialogVisible.value = true;
	};

	// 重置新增表单
	const resetAddForm = () => {
		if (addFormRef.value) {
			addFormRef.value.resetFields(); // 重置表单字段和校验状态
		} else {
			addForm.value = {
				username: '',
				password: '',
				// 移除name字段清空
				// name: '',
			};
		}
	};

	// 提交新增表单
	const submitAddForm = async () => {
		const valid = await addFormRef.value.validate();
		if (!valid) {
			ElMessage.error('请填写完整且符合要求的表单项');
			return;
		}

		try {
			const registrationData = {
				username: addForm.value.username,
				password: addForm.value.password,
				// 移除name到请求体，只发送 username, password, role
				// name: addForm.value.name,
				role: 'admin', // 指定角色为 admin
			};
			const { data: res } = await userRegisterApi(registrationData); // 调用通用用户注册 API

			if (res.code === 1) {
				ElMessage.success('管理员添加成功！');
				addDialogVisible.value = false; // 关闭弹窗
				resetAddForm(); // 重置表单数据
				fetchAdminList(); // 刷新管理员列表
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || '管理员添加失败'); // <-- 移除此行
				console.warn('新增管理员业务失败:', res.msg); // 可以保留日志
			}
		} catch (error) {
			// 捕获真正的请求错误
			console.error('新增管理员请求失败:', error);
			ElMessage.error('新增管理员失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
		}
	};

	// === 数据获取方法 ===
	const fetchAdminList = async () => {
		loading.value = true;
		try {
			const params = {
				page: pagination.value.page,
				pageSize: pagination.value.pageSize,
				username: searchForm.value.username || undefined,
				name: searchForm.value.name || undefined,
				isBanned: searchForm.value.isBanned !== null ? searchForm.value.isBanned : undefined,
				// 管理员搜索条件比较简单，没有其他字段
			};

			const { data: res } = await getAdminListPageApi(params);

			if (res.code === 1) {
				tableData.value = res.data.records;
				pagination.value.total = res.data.total;
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || '获取管理员列表失败'); // <-- 移除此行
				console.warn('获取管理员列表业务失败:', res.msg); // 可以保留日志
				tableData.value = [];
				pagination.value.total = 0;
			}
		} catch (error) {
			// 捕获真正的请求错误
			console.error('获取管理员列表请求失败:', error);
			ElMessage.error('获取管理员列表失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
			tableData.value = [];
			pagination.value.total = 0;
		} finally {
			loading.value = false;
		}
	};

	// === 搜索相关方法 ===
	const handleSearch = () => {
		pagination.value.page = 1;
		fetchAdminList();
	};

	const resetSearchForm = () => {
		searchForm.value = {
			username: '',
			name: '',
			isBanned: null,
		};
		handleSearch();
	};

	// === 分页相关方法 ===
	const handleSizeChange = (val) => {
		pagination.value.pageSize = val;
		pagination.value.page = 1;
		fetchAdminList();
	};

	const handleCurrentChange = (val) => {
		pagination.value.page = val;
		fetchAdminList();
	};

	// === 封禁/解封操作相关方法 ===
	const beforeStatusChange = (row) => {
		// 确保不能封禁/解封当前登录管理员或超级管理员 (ID 1)
		if (row.id === currentAdminId.value) {
			ElMessage.warning('不能修改自己的状态！');
			return false;
		}
		if (row.id === 1) {
			ElMessage.warning('不能修改超级管理员的状态！');
			return false;
		}

		const confirmText = row.isBanned
			? `确定要解封管理员 "${row.username}" 吗？`
			: `确定要封禁管理员 "${row.username}" 吗？`;
		return ElMessageBox.confirm(confirmText, '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
		})
			.then(() => {
				return true;
			})
			.catch(() => {
				ElMessage.info('操作已取消');
				return false;
			});
	};

	const handleStatusChange = async (row) => {
		const newStatus = row.isBanned ? 1 : 0;
		const adminId = row.id;

		try {
			const { data: res } = await updateAdminStatusApi(adminId, newStatus);

			if (res.code === 1) {
				ElMessage.success(`${newStatus === 1 ? '封禁' : '解封'}成功！`);
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || `${newStatus === 1 ? '封禁' : '解封'}失败`); // <-- 移除此行
				console.warn('更新管理员状态业务失败:', res.msg); // 可以保留日志
				row.isBanned = !row.isBanned; // 操作失败，手动将 switch 状态拨回原样
			}
		} catch (error) {
			// 捕获真正的请求错误
			console.error('更新管理员状态请求失败:', error);
			ElMessage.error(`${newStatus === 1 ? '封禁' : '解封'}失败，请稍后再试`); // <-- 这个用于网络或HTTP错误
			row.isBanned = !row.isBanned; // 操作失败，手动将 switch 状态拨回原样
		}
	};

	// === 查询详情相关方法 ===
	const showDetails = (row) => {
		// 注意：这个详情展示直接使用了当前行的数据，没有调用详情 API，所以无需修改 API 响应处理
		currentRowDetails.value = row;
		dialogVisible.value = true;
	};

	// === 重置密码相关方法 ===
	const handleResetPassword = (row) => {
		// 检查是否是当前登录管理员
		if (row.id === currentAdminId.value) {
			ElMessage.warning('不能重置自己的密码！');
			return;
		}
		// 检查是否是超级管理员 (ID 1)
		if (row.id === 1) {
			ElMessage.warning('不能重置超级管理员的密码！');
			return;
		}

		ElMessageBox.confirm(`确定重置管理员 "${row.username}" 的密码吗？`, '警告', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
		})
			.then(async () => {
				// 用户点击确定
				try {
					const { data: res } = await resetAdminPasswordApi(row.id);

					if (res.code === 1) {
						ElMessage.success(`管理员 "${row.username}" 的密码已重置为默认密码！`);
						// 重置密码成功后通常不需要刷新列表，但可以根据需求决定
						// fetchAdminList();
					} else {
						// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
						// ElMessage.error(res.msg || '密码重置失败，请稍后再试'); // <-- 移除此行
						console.warn('重置密码业务失败:', res.msg); // 可以保留日志
					}
				} catch (error) {
					// 捕获真正的请求错误
					console.error('重置密码请求失败:', error);
					ElMessage.error('密码重置失败，请检查网络或权限'); // <-- 这个用于网络或HTTP错误
				}
			})
			.catch(() => {
				// 用户点击取消
				ElMessage.info('密码重置操作已取消');
			});
	};

	// === 组件挂载后，首次加载数据 ===
	onMounted(() => {
		// 获取当前管理员ID（这个逻辑需要您根据实际情况实现）
		// 例如，从 Pinia 或 Vuex store 中获取
		// 在这里调用您的获取当前管理员ID的函数
		// fetchCurrentAdminId(); // 如果是异步获取，需要await

		// fetchAdminList() 在获取到 currentAdminId 后调用，确保权限判断正确
		// 如果 currentAdminId 是同步从 store 获取，可以在这里直接调用 fetchAdminList
		// 如果是异步获取，需要等待获取完成后再调用 fetchAdminList
		// 示例：假设 currentAdminId 是同步获取
		fetchAdminList(); // 这里直接调用，假设 currentAdminId 已经可用
	});

	// 注意：computed 属性 currentAdminId 会在 store 数据变化时自动更新，
	// 如果当前登录用户 ID 是异步加载到 store 的，这里会自动响应式更新。
	// 如果是同步加载到 store，mounted 钩子里不需要异步等待。
	// 如果 currentAdminId 需要异步获取并且是在组件 mounted 后才获取，
	// 那么依赖它的地方（如 disabled 属性）会响应式更新，但列表数据获取
	// 如果依赖它作为请求参数，则需要在获取到 ID 后再触发。
	// 在此代码中 currentAdminId 只用于 disabled 属性，fetchAdminList 不依赖它作为查询参数，
	// 所以 mounted 中直接调用 fetchAdminList 是合理的。
</script>

<style scoped lang="less">
	.admin-admin-list-container {
		padding: 20px;
		background-color: #fff;
		border-radius: 8px;
		box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

		h3 {
			font-size: 22px;
			margin-bottom: 20px;
			color: #333;
			border-bottom: 1px solid #eee;
			padding-bottom: 15px;
		}

		.search-form {
			margin-bottom: 20px;
			padding: 15px;
			background-color: #f8f8f8;
			border-radius: 6px;

			display: flex;
			flex-wrap: wrap;
			align-items: center;

			.el-form-item {
				margin-bottom: 10px;
				margin-right: 20px;

				:deep(.el-input__wrapper),
				:deep(.el-select__wrapper),
				:deep(.el-date-editor .el-input__wrapper) {
					border-color: #ccc !important;
					border-width: 1px !important;
					border-style: solid !important;
					box-shadow: none !important;
				}
				:deep(.el-input__wrapper.is-focus),
				:deep(.el-select__wrapper.is-focus),
				:deep(.el-input-number__input-wrap.is-focus),
				:deep(.el-date-editor .el-input__wrapper.is-focus) {
					box-shadow: none !important;
				}
				:deep(.el-input__inner::placeholder),
				:deep(.el-select__placeholder),
				:deep(.el-range-input::placeholder) {
					color: #999;
				}
				:deep(.el-input__inner),
				:deep(.el-select__inner),
				:deep(.el-range-input) {
					font-size: 14px;
				}
				:deep(.el-input-number__inner) {
					font-size: 14px;
					text-align: left;
				}

				.el-form-item__label {
					padding-right: 8px;
					line-height: 32px;
					font-weight: bold;
					color: #555;
				}
				.el-form-item__content {
					line-height: 32px;
				}

				:deep(.el-input),
				:deep(.el-select) {
					width: 200px;
				}

				:deep(.el-input__inner),
				:deep(.el-select__inner),
				:deep(.el-range-input),
				:deep(.el-input__wrapper),
				:deep(.el-select__wrapper),
				:deep(.el-input-number__input-wrap),
				:deep(.el-date-editor .el-input__wrapper) {
				}
			}

			.button-group {
				margin-right: 0 !important;
				margin-left: auto;

				.el-button {
					margin-left: 10px;
					&:first-child {
						margin-left: 0;
					}
				}

				.add-button-separate {
					margin-left: 30px;
				}

				.el-button {
					height: 32px;
					border-radius: 4px;
				}
				.el-button--primary {
					background-color: #1890ff !important;
					border-color: #1890ff !important;
					color: #fff !important;
					font-weight: bold;
				}

				.el-button--success {
					background-color: #67c23a !important;
					border-color: #67c23a !important;
					color: #fff !important;
				}
			}
		}
		.el-table {
			margin-bottom: 20px;

			:deep(.el-table__cell) {
				padding: 8px 10px;
			}
			:deep(.el-table__header-wrapper th) {
				background-color: #f5f7fa;
				color: #606266;
				font-weight: bold;
				padding: 8px 10px;
			}
			:deep(.el-table__cell .cell) {
				display: flex;
				align-items: center;
				justify-content: center;
			}

			.detail-button {
				background-color: #f0f0f0 !important;
				border: 1px solid #dcdcdc !important;
				color: #333 !important;
				border-radius: 2px !important;
			}
		}

		.pagination {
			justify-content: flex-end;
			margin-top: 20px;
		}

		.el-dialog {
			.el-dialog__header {
				border-bottom: 1px solid #eee;
				padding: 20px;
				.el-dialog__title {
					font-size: 18px;
					font-weight: bold;
				}
			}

			.el-dialog__body {
				padding: 20px;
			}

			.el-form {
				.el-form-item {
					margin-bottom: 15px;
					.el-form-item__label {
						font-weight: bold;
						color: #555;
					}
					.el-form-item__content {
						color: #333;
						line-height: 1.5;
						word-break: break-all;
						img {
							vertical-align: middle;
						}
						.el-tag {
							vertical-align: middle;
						}
					}
					.el-form-item__label-wrap {
						margin-right: 12px;
					}
				}
			}

			.el-dialog__footer {
				border-top: 1px solid #eee;
				padding: 15px 20px;
				.dialog-footer {
					text-align: right;
				}
			}
		}
	}
</style>
