<template>
	<div class="admin-notice-list-container">
		<h3>搬家须知管理</h3>

		<el-form :inline="true" :model="searchForm" class="search-form">
			<div class="input-items-group evenly-distributed-group">
				<el-form-item label="标题">
					<el-input v-model="searchForm.title" placeholder="请输入标题关键词" clearable></el-input>
				</el-form-item>
				<el-form-item label="内容">
					<el-input
						v-model="searchForm.content"
						placeholder="请输入内容关键词"
						clearable
					></el-input>
				</el-form-item>
				<el-form-item label="分类">
					<el-input v-model="searchForm.category" placeholder="请输入分类" clearable></el-input>
				</el-form-item>
				<el-form-item label="状态">
					<el-select v-model="searchForm.isPublished" placeholder="请选择状态" clearable>
						<el-option label="已发布" :value="true"></el-option>
						<el-option label="未发布" :value="false"></el-option>
					</el-select>
				</el-form-item>
			</div>

			<div class="input-items-group">
				<el-form-item label="发布日期">
					<el-date-picker
						v-model="searchForm.publishDateRange"
						type="daterange"
						range-separator="至"
						start-placeholder="开始日期"
						end-placeholder="结束日期"
						value-format="YYYY-MM-DD"
						:clearable="true"
					></el-date-picker>
				</el-form-item>
			</div>

			<el-form-item class="button-group">
				<el-button type="primary" @click="handleSearch">查询</el-button>
				<el-button @click="resetSearchForm">重置</el-button>
				<el-button type="success" @click="handleAddNotice" class="add-button-separate"
					>新增须知</el-button
				>
			</el-form-item>
		</el-form>

		<el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
			<el-table-column prop="id" label="ID" align="center" width="80"></el-table-column>
			<el-table-column
				prop="title"
				label="标题"
				align="left"
				min-width="200"
				show-overflow-tooltip
			></el-table-column>
			<el-table-column prop="category" label="分类" align="center" width="140"></el-table-column>
			<el-table-column
				prop="createUserName"
				label="作者"
				width="120"
				align="center"
			></el-table-column>
			<el-table-column prop="publishDate" label="发布日期" width="120" align="center">
				<template #default="scope">
					{{ scope.row.isPublished ? scope.row.publishDate || '未设置日期' : '未发布' }}
				</template>
			</el-table-column>

			<el-table-column label="状态" align="center" width="100">
				<template #default="scope">
					<el-tag :type="scope.row.isPublished ? 'success' : 'info'">
						{{ scope.row.isPublished ? '已发布' : '草稿' }}
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
						type="primary"
						style="margin-left: 10px"
						@click="handleEditNotice(scope.row)"
						>编辑</el-button
					>
					<el-button
						size="small"
						type="danger"
						style="margin-left: 10px"
						@click="handleDeleteNotice(scope.row)"
						>删除</el-button
					>
					<el-switch
						v-model="scope.row.isPublished"
						active-text="发布"
						inactive-text="草稿"
						:active-value="true"
						:inactive-value="false"
						@change="handleStatusChange(scope.row)"
						:before-change="() => beforeStatusChange(scope.row)"
						style="margin-left: 20px"
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

		<el-dialog
			v-model="editDialogVisible"
			:title="editDialogTitle"
			width="600px"
			@close="resetEditForm"
		>
			<el-form :model="editForm" :rules="editFormRules" ref="editFormRef" label-width="100px">
				<el-form-item label="标题" prop="title">
					<el-input v-model="editForm.title" placeholder="请输入须知标题"></el-input>
				</el-form-item>
				<el-form-item label="分类" prop="category" style="margin-top: 20px">
					<el-input v-model="editForm.category" placeholder="请输入须知分类"></el-input>
				</el-form-item>
				<el-form-item label="发布日期" style="margin-top: 20px" prop="publishDate">
					<el-date-picker
						v-model="editForm.publishDate"
						type="date"
						placeholder="选择发布日期"
						value-format="YYYY-MM-DD"
						style="width: 100%"
						clearable
					></el-date-picker>
				</el-form-item>
				<el-form-item label="内容" style="margin-top: 20px" prop="content">
					<el-input
						v-model="editForm.content"
						type="textarea"
						:rows="8"
						placeholder="请输入须知内容"
					></el-input>
				</el-form-item>
			</el-form>
			<template #footer>
				<div class="dialog-footer">
					<el-button @click="editDialogVisible = false">取消</el-button>
					<el-button type="primary" @click="submitEditForm">确定</el-button>
				</div>
			</template>
		</el-dialog>

		<el-dialog v-model="detailDialogVisible" title="须知详情" width="700px">
			<el-form label-width="100px" v-if="currentRowDetails">
				<el-form-item label="ID">{{ currentRowDetails.id }}</el-form-item>
				<el-form-item label="标题">{{ currentRowDetails.title }}</el-form-item>
				<el-form-item label="分类">{{ currentRowDetails.category }}</el-form-item>
				<el-form-item label="状态">
					<el-tag :type="currentRowDetails.isPublished ? 'success' : 'info'">
						{{ currentRowDetails.isPublished ? '已发布' : '草稿' }}
					</el-tag>
				</el-form-item>
				<el-form-item label="发布日期">{{
					currentRowDetails.publishDate || '未设置日期'
				}}</el-form-item>
				<el-form-item label="须知内容">
					<div
						class="notice-content-display"
						style="margin-top: -5px"
						v-html="currentRowDetails.content"
					></div>
				</el-form-item>
				<el-form-item label="创建人">{{ currentRowDetails.createUserName }}</el-form-item>
				<el-form-item label="创建时间">{{ currentRowDetails.createTime }}</el-form-item>
				<el-form-item label="更新人">{{ currentRowDetails.updateUserName }}</el-form-item>
				<el-form-item label="更新时间">{{ currentRowDetails.updateTime }}</el-form-item>
			</el-form>
			<div v-else>正在加载须知详情...</div>

			<template #footer>
				<div class="dialog-footer">
					<el-button @click="detailDialogVisible = false">关闭</el-button>
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
		ElDatePicker,
		ElButton,
		ElTable,
		ElTableColumn,
		ElPagination,
		ElSwitch,
		ElOption,
	} from 'element-plus';

	// 导入后台搬家须知 API
	import {
		getBackNoticeListPageApi,
		getBackNoticeDetailApi, // <-- 导入详情 API
		addBackNoticeApi,
		updateBackNoticeApi,
		deleteBackNoticeApi,
		updateBackNoticeStatusApi,
	} from '@/api/noticeApi.js'; // <-- 导入须知 API

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
		title: '', // 标题关键词
		content: '', // 内容关键词
		category: '', // 添加分类搜索字段
		isPublished: null, // 发布状态 (null 表示全部)
		publishDateRange: null, // 发布日期范围 [start, end]
	});

	// 新增/修改弹窗状态和表单数据
	const editDialogVisible = ref(false); // 控制新增/修改弹窗显示
	const editDialogTitle = computed(() => (editForm.value.id ? '修改须知' : '新增须知')); // 弹窗标题
	const editFormRef = ref(null); // 新增/修改表单的引用
	const editForm = ref({
		id: null, // ID 为 null 表示新增
		title: '',
		content: '',
		category: '', // 添加分类字段
		publishDate: null, // 使用 publishDate 字段
	});
	// 新增/修改表单校验规则 - 发布日期设为必填
	const editFormRules = ref({
		title: [{ required: true, message: '请输入须知标题', trigger: 'blur' }],
		content: [{ required: true, message: '请输入须知内容', trigger: 'blur' }],
		category: [{ required: true, message: '请输入须知分类', trigger: 'blur' }], // 添加分类校验
		publishDate: [{ required: true, message: '请选择发布日期', trigger: 'change' }],
	});

	// 详情弹窗状态和当前选中行数据
	const detailDialogVisible = ref(false); // 控制详情弹窗显示
	const currentRowDetails = ref(null); // 存储当前查看详情的行数据

	// === 数据获取方法 ===
	const fetchNoticeList = async () => {
		loading.value = true;
		try {
			// 准备请求参数
			const params = {
				page: pagination.value.page,
				pageSize: pagination.value.pageSize,
				title: searchForm.value.title || undefined, // 添加标题参数
				content: searchForm.value.content || undefined, // 添加内容参数
				category: searchForm.value.category || undefined, // 添加分类参数
				isPublished:
					searchForm.value.isPublished !== null ? searchForm.value.isPublished : undefined, // 添加发布状态参数
				publishDateStart: searchForm.value.publishDateRange
					? searchForm.value.publishDateRange[0]
					: undefined, // 添加发布日期开始参数
				publishDateEnd: searchForm.value.publishDateRange
					? searchForm.value.publishDateRange[1]
					: undefined, // 添加发布日期结束参数
			};

			const { data: res } = await getBackNoticeListPageApi(params); // 调用后台须知分页 API

			if (res.code === 1) {
				tableData.value = res.data.records;
				pagination.value.total = res.data.total;
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || '获取须知列表失败'); // <-- 移除此行，避免重复提示
				console.warn('获取须知列表业务失败:', res.msg); // 可以保留日志
				tableData.value = [];
				pagination.value.total = 0;
			}
		} catch (error) {
			// 捕获真正的请求错误 (网络问题、HTTP错误等，不包括 code !== 1 的业务错误)
			console.error('获取须知列表请求失败:', error);
			ElMessage.error('获取须知列表失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
			tableData.value = [];
			pagination.value.total = 0;
		} finally {
			loading.value = false;
		}
	};

	// === 搜索相关方法 ===
	const handleSearch = () => {
		pagination.value.page = 1; // 从第一页开始搜索
		fetchNoticeList();
	};

	const resetSearchForm = () => {
		searchForm.value = {
			title: '',
			content: '',
			category: '', // 重置分类
			isPublished: null,
			publishDateRange: null,
		};
		handleSearch(); // 重置后立即执行查询，回到第一页
	};

	// === 分页相关方法 ===
	const handleSizeChange = (val) => {
		pagination.value.pageSize = val;
		pagination.value.page = 1; // 切换每页大小时回到第一页
		fetchNoticeList();
	};

	const handleCurrentChange = (val) => {
		pagination.value.page = val;
		fetchNoticeList();
	};

	// === 新增/修改须知相关方法 ===

	// 打开新增须知弹窗
	const handleAddNotice = () => {
		resetEditForm(); // 打开前先重置表单，确保是新增模式
		editDialogVisible.value = true;
	};

	// 打开修改须知弹窗
	const handleEditNotice = (row) => {
		// 将当前行的数据填充到表单，进入修改模式
		editForm.value = { ...row };
		editDialogVisible.value = true;
	};

	// 提交新增/修改表单
	const submitEditForm = async () => {
		// 进行表单校验
		const valid = await editFormRef.value.validate();
		if (!valid) {
			ElMessage.error('请填写完整且符合要求的表单项');
			return;
		}

		// 前端校验通过，判断是新增还是修改
		const isAdding = !editForm.value.id; // 根据是否有 ID 判断模式

		try {
			const apiCall = isAdding ? addBackNoticeApi : updateBackNoticeApi;
			const { data: res } = await apiCall(editForm.value);

			if (res.code === 1) {
				ElMessage.success(isAdding ? '须知添加成功！' : '须知修改成功！');
				editDialogVisible.value = false; // 关闭弹窗
				resetEditForm(); // 重置表单数据
				fetchNoticeList(); // 刷新须知列表
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || (isAdding ? '须知添加失败' : '须知修改失败')); // <-- 移除此行，避免重复提示
				console.warn('提交须知业务失败:', res.msg); // 可以保留日志
				// 保持弹窗打开，以便用户根据后端错误信息修改输入
			}
		} catch (error) {
			// 捕获真正的请求错误 (网络问题, CORS, request.js 拦截器抛出错误等)
			console.error('提交须知请求失败:', error);
			// request.js 已经在 rejection 时弹窗了，这里弹出的错误更倾向于网络或未被拦截器详细处理的错误
			const errorMessageToDisplay = isAdding
				? '新增须知失败，请稍后再试'
				: '修改须知失败，请稍后再试';
			ElMessage.error(errorMessageToDisplay);
		}
	};

	// 重置新增/修改表单
	const resetEditForm = () => {
		if (editFormRef.value) {
			editFormRef.value.resetFields();
		}
		editForm.value = {
			id: null,
			title: '',
			content: '',
			category: '', // 重置分类
			publishDate: null,
		};
	};

	// === 删除须知相关方法 ===
	const handleDeleteNotice = (row) => {
		ElMessageBox.confirm(`确定要删除须知《${row.title}》吗？`, '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
		})
			.then(async () => {
				try {
					const { data: res } = await deleteBackNoticeApi(row.id); // 调用删除 API
					if (res.code === 1) {
						ElMessage.success('须知删除成功！');
						// 删除成功后判断是否需要留在当前页或跳转到上一页
						if (tableData.value.length === 1 && pagination.value.page > 1) {
							pagination.value.page--;
						}
						fetchNoticeList(); // 刷新列表
					} else {
						// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
						// ElMessage.error(res.msg || '须知删除失败'); // <-- 移除此行，避免重复提示
						console.warn('须知删除业务失败:', res.msg); // 可以保留日志
					}
				} catch (error) {
					// 捕获真正的请求错误 (网络问题、HTTP错误等，不包括 code !== 1 的业务错误)
					console.error('删除须知请求失败:', error);
					ElMessage.error('删除须知失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
				}
			})
			.catch(() => {
				ElMessage.info('已取消删除');
			});
	};

	// === 发布/取消发布相关方法 ===

	// 在状态切换前进行确认
	const beforeStatusChange = (row) => {
		const confirmText = row.isPublished
			? `确定要取消发布须知《${row.title}》吗？`
			: `确定要发布须知《${row.title}》吗？`;
		return ElMessageBox.confirm(confirmText, '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
		})
			.then(() => {
				return true; // 用户点击确定，允许切换
			})
			.catch(() => {
				ElMessage.info('操作已取消');
				return false; // 用户点击取消，阻止切换
			});
	};

	// 处理状态切换
	const handleStatusChange = async (row) => {
		const newStatus = row.isPublished ? 1 : 0; // 1 表示发布，0 表示取消发布

		try {
			const { data: res } = await updateBackNoticeStatusApi(row.id, newStatus); // 调用更新状态 API

			if (res.code === 1) {
				ElMessage.success(`${newStatus === 1 ? '发布' : '取消发布'}成功！`);
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || `${newStatus === 1 ? '发布' : '取消发布'}失败`); // <-- 移除此行，避免重复提示
				console.warn('更新须知状态业务失败:', res.msg); // 可以保留日志
				row.isPublished = !row.isPublished; // 操作失败，手动将 switch 状态拨回原样
			}
		} catch (error) {
			// 捕获真正的请求错误 (网络问题、HTTP错误等)
			console.error('更新须知状态请求失败:', error);
			ElMessage.error(`${newStatus === 1 ? '发布' : '取消发布'}失败，请稍后再试`); // <-- 这个用于网络或HTTP错误
			row.isPublished = !row.isPublished; // 操作失败，手动将 switch 状态拨回原样
		}
	};

	// === 查看须知详情相关方法 ===
	const showDetails = async (row) => {
		detailDialogVisible.value = true;
		currentRowDetails.value = null; // 清空旧数据

		try {
			// 调用后台须知详情 API 根据须知ID获取完整的详情数据
			const { data: res } = await getBackNoticeDetailApi(row.id); // <-- 调用详情 API

			if (res.code === 1 && res.data) {
				currentRowDetails.value = res.data; // 使用详情接口返回的数据更新 currentRowDetails
			} else {
				// 业务失败 (code !== 1) 或数据不存在，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || '获取须知详情失败'); // <-- 移除此行，避免重复提示
				console.warn('获取须知详情业务失败:', res.msg); // 可以保留日志
				detailDialogVisible.value = false; // 获取失败则关闭弹窗
			}
		} catch (error) {
			// 捕获真正的请求错误 (网络问题、HTTP错误等)
			console.error('获取须知详情请求失败:', error);
			ElMessage.error('获取须知详情失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
			detailDialogVisible.value = false; // 请求失败则关闭弹窗
		}
	};

	// === 组件挂载后，首次加载数据 ===
	onMounted(() => {
		fetchNoticeList(); // 获取须知列表
	});
</script>

<style scoped lang="less">
	/* 可以完全复用 AdminNewsList.vue 中的样式 */
	/* 从 AdminNewsList.vue 复制粘贴这里的 <style scoped> 内容即可 */
	.admin-notice-list-container {
		padding: 20px; // 整体内边距
		background-color: #fff; // 整体白色背景
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
			background-color: #f8f8f8; // 搜索区域背景色
			border-radius: 6px;

			display: flex;
			flex-wrap: wrap; // 允许换行
			align-items: center;
			gap: 10px 20px; /* 行和列之间的间距 */

			.input-items-group {
				display: flex;
				flex-wrap: wrap; // 允许换行，以应对窄屏幕
				align-items: center;
				width: 100%; // 确保组占据一行以便均匀分布
				margin-bottom: 0; // 组下方添加垂直间距
				gap: 10px 20px; /* 组内的行和列间距 */

				.el-form-item {
					margin-right: 0 !important; // 确保不被其他规则影响
					margin-bottom: 0 !important; // 确保不被其他规则影响，使用 gap 控制垂直间距
					flex-grow: 0; // 不拉伸
					flex-shrink: 1; // 允许缩小
					flex-basis: auto; // 根据内容确定初始大小
				}
			}

			> .el-form-item {
				flex-shrink: 0; // 防止日期选择器或按钮组缩小
			}

			.el-form-item {
				:deep(.el-input__wrapper),
				:deep(.el-select__wrapper),
				:deep(.el-input-number__input-wrap),
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
					font-weight: bold; // 标签文字加粗
					color: #555; // 标签文字颜色
				}
				.el-form-item__content {
					line-height: 32px;
					flex-grow: 0;
					width: auto;
					display: flex;
					align-items: center;
				}

				:deep(.el-input),
				:deep(.el-select) {
					width: 200px; /* 基础宽度 */
				}

				:deep(.el-date-editor--daterange) {
					width: 240px; /* 日期范围选择器宽度 */
				}
				:deep(.el-date-editor--datetimerange) {
					width: 380px; /* 日期时间范围选择器宽度 */
				}
				:deep(.el-input-number) {
					width: auto;
				}
			}

			.evenly-distributed-group {
				.el-form-item {
					flex-grow: 1;
					flex-basis: 0;
					min-width: 150px;

					:deep(.el-form-item__content) {
						flex-grow: 1;
						width: 100%;
					}

					:deep(.el-input),
					:deep(.el-select) {
						width: 100% !important;
						min-width: auto;
					}
				}
			}

			.button-group {
				margin-left: auto;
				margin-right: 0 !important;
				flex-shrink: 0;

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
				word-break: break-word;
				white-space: pre-wrap;
			}
			.el-table-column[prop='title'] :deep(.cell),
			.el-table-column[prop='category'] :deep(.cell) {
				justify-content: flex-start; /* 标题和分类左对齐 */
			}

			.detail-button {
				background-color: #f0f0f0 !important;
				border: 1px solid #dcdcdc !important;
				color: #333 !important;
				border-radius: 2px !important;
				margin-right: 5px;
			}
			.el-button + .el-button {
				margin-left: 5px;
			}

			.el-button + .el-switch {
				margin-left: 10px;
			}
			.el-switch + .el-button {
				margin-left: 10px;
			}
			.el-button {
				height: 28px;
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
				padding-bottom: 10px;
			}

			.el-form {
				.el-form-item {
					margin-bottom: 10px;
					.el-form-item__label {
						font-weight: bold;
						color: #555;
						width: 100px !important;
						text-align: right;
					}
					.el-form-item__content {
						color: #333;
						line-height: 1.5;
						word-break: break-all;
						flex-grow: 1;
						margin-left: 0 !important;

						img {
							vertical-align: middle;
						}
						.el-tag {
							vertical-align: middle;
						}
						.notice-content-display {
							/* 修改类名 */
							width: 100%;
							box-sizing: border-box;
							padding: 5px 0;
						}
					}
				}
				.el-form-item__content .el-tag {
					vertical-align: middle;
				}
				.el-form-item__content img {
					vertical-align: middle;
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
