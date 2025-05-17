<template>
	<div class="admin-news-list-container">
		<h3>搬家新闻管理</h3>

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
				<el-button type="success" @click="handleAddNews" class="add-button-separate"
					>新增新闻</el-button
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
			<el-table-column prop="publishDate" label="发布日期" width="120" align="center">
				<template #default="scope">
					{{ scope.row.isPublished ? scope.row.publishDate || '未设置日期' : '未发布' }}
				</template>
			</el-table-column>
			<el-table-column
				prop="createUserName"
				label="作者"
				width="120"
				align="center"
			></el-table-column>
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
						@click="handleEditNews(scope.row)"
						>编辑</el-button
					>
					<el-button
						size="small"
						type="danger"
						style="margin-left: 10px"
						@click="handleDeleteNews(scope.row)"
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
					<el-input v-model="editForm.title" placeholder="请输入新闻标题"></el-input>
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
						placeholder="请输入新闻内容"
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

		<el-dialog v-model="detailDialogVisible" title="新闻详情" width="700px">
			<el-form label-width="100px" v-if="currentRowDetails">
				<el-form-item label="ID">{{ currentRowDetails.id }}</el-form-item>
				<el-form-item label="新闻标题">{{ currentRowDetails.title }}</el-form-item>
				<el-form-item label="作者">{{ currentRowDetails.createUserName }}</el-form-item>
				<el-form-item label="状态">
					<el-tag :type="currentRowDetails.isPublished ? 'success' : 'info'">
						{{ currentRowDetails.isPublished ? '已发布' : '草稿' }}
					</el-tag>
				</el-form-item>
				<el-form-item label="发布日期">{{
					currentRowDetails.publishDate || '未设置日期'
				}}</el-form-item>
				<el-form-item label="创建时间">{{ currentRowDetails.createTime }}</el-form-item>
				<el-form-item label="更新时间">{{ currentRowDetails.updateTime }}</el-form-item>
				<el-form-item label="创建人">{{ currentRowDetails.createUserName }}</el-form-item>
				<el-form-item label="更新人">{{ currentRowDetails.updateUserName }}</el-form-item>
				<el-form-item label="新闻内容">
					<div class="news-content-display" v-html="currentRowDetails.content"></div>
				</el-form-item>
			</el-form>
			<div v-else>正在加载新闻详情...</div>

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
		// 如果需要，导入其他组件如 ElInputNumber, ElRate 等
	} from 'element-plus';

	// 导入后台新闻 API
	import {
		getBackNewsListPageApi,
		getBackNewsDetailApi, // <-- 导入详情 API
		addBackNewsApi,
		updateBackNewsApi,
		deleteBackNewsApi,
		updateBackNewsStatusApi,
	} from '@/api/newsApi.js'; // <-- 导入新闻 API

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
		isPublished: null, // 发布状态 (null 表示全部)
		publishDateRange: null, // 发布日期范围 [start, end]
		// createTimeRange: null, // 创建时间范围 [start, end] // 如果需要
	});

	// 新增/修改弹窗状态和表单数据
	const editDialogVisible = ref(false); // 控制新增/修改弹窗显示
	const editDialogTitle = computed(() => (editForm.value.id ? '修改新闻' : '新增新闻')); // 弹窗标题
	const editFormRef = ref(null); // 新增/修改表单的引用
	const editForm = ref({
		id: null, // ID 为 null 表示新增
		title: '',
		content: '',
		publishDate: null, // 使用 publishDate 字段
	});
	// 新增/修改表单校验规则 - 发布日期设为必填
	const editFormRules = ref({
		title: [{ required: true, message: '请输入新闻标题', trigger: 'blur' }],
		content: [{ required: true, message: '请输入新闻内容', trigger: 'blur' }],
		publishDate: [{ required: true, message: '请选择发布日期', trigger: 'change' }], // <--- 发布日期设为必填
	});

	// 详情弹窗状态和当前选中行数据
	const detailDialogVisible = ref(false); // 控制详情弹窗显示
	const currentRowDetails = ref(null); // 存储当前查看详情的行数据

	// === 数据获取方法 ===
	const fetchNewsList = async () => {
		loading.value = true;
		try {
			// 准备请求参数
			const params = {
				page: pagination.value.page,
				pageSize: pagination.value.pageSize,
				title: searchForm.value.title || undefined, // 添加标题参数
				content: searchForm.value.content || undefined, // 添加内容参数
				isPublished:
					searchForm.value.isPublished !== null ? searchForm.value.isPublished : undefined, // 添加发布状态参数
				publishDateStart: searchForm.value.publishDateRange
					? searchForm.value.publishDateRange[0]
					: undefined, // 添加发布日期开始参数
				publishDateEnd: searchForm.value.publishDateRange
					? searchForm.value.publishDateRange[1]
					: undefined, // 添加发布日期结束参数
				// createTimeStart: searchForm.value.createTimeRange ? searchForm.value.createTimeRange[0] : undefined, // 如果需要
				// createTimeEnd: searchForm.value.createTimeRange ? searchForm.value.createTimeRange[1] : undefined, // 如果需要
			};

			const { data: res } = await getBackNewsListPageApi(params); // 调用后台新闻分页 API

			if (res.code === 1) {
				tableData.value = res.data.records;
				pagination.value.total = res.data.total;
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || '获取新闻列表失败'); // <-- 移除此行，避免重复提示
				console.warn('获取新闻列表业务失败:', res.msg); // 可以保留日志
				tableData.value = [];
				pagination.value.total = 0;
			}
		} catch (error) {
			// 捕获真正的请求错误 (网络问题、HTTP错误等，不包括 code !== 1 的业务错误)
			console.error('获取新闻列表请求失败:', error);
			ElMessage.error('获取新闻列表失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
			tableData.value = [];
			pagination.value.total = 0;
		} finally {
			loading.value = false;
		}
	};

	// === 搜索相关方法 ===
	const handleSearch = () => {
		pagination.value.page = 1; // 从第一页开始搜索
		fetchNewsList();
	};

	const resetSearchForm = () => {
		searchForm.value = {
			title: '',
			content: '',
			isPublished: null,
			publishDateRange: null,
			// createTimeRange: null, // 如果需要
		};
		handleSearch(); // 重置后立即执行查询，回到第一页
	};

	// === 分页相关方法 ===
	const handleSizeChange = (val) => {
		pagination.value.pageSize = val;
		pagination.value.page = 1; // 切换每页大小时回到第一页
		fetchNewsList();
	};

	const handleCurrentChange = (val) => {
		pagination.value.page = val;
		fetchNewsList();
	};

	// === 新增/修改新闻相关方法 ===

	// 打开新增新闻弹窗
	const handleAddNews = () => {
		resetEditForm(); // 打开前先重置表单，确保是新增模式
		editDialogVisible.value = true;
	};

	// 打开修改新闻弹窗
	const handleEditNews = (row) => {
		// 将当前行的数据填充到表单，进入修改模式
		editForm.value = { ...row };
		// TODO: 如果后端返回的 publishDate 是日期字符串且编辑时需要 Date 对象，可能需要转换
		// editForm.value.publishDate = row.publishDate ? new Date(row.publishDate) : null;
		editDialogVisible.value = true;
	};

	// 提交新增/修改表单
	const submitEditForm = async () => {
		// 进行表单校验
		const valid = await editFormRef.value.validate();
		if (!valid) {
			// 前端校验失败，Element Plus 会在输入框下方提示错误信息
			ElMessage.error('请填写完整且符合要求的表单项'); // 额外弹出提示
			return;
		}

		// 前端校验通过，判断是新增还是修改
		const isAdding = !editForm.value.id; // 根据是否有 ID 判断模式

		try {
			const apiCall = isAdding ? addBackNewsApi : updateBackNewsApi;
			// 注意：updateBackNewsApi 期望整个包含 id 的对象
			const { data: res } = await apiCall(editForm.value);

			if (res.code === 1) {
				// 后端返回成功
				ElMessage.success(isAdding ? '新闻添加成功！' : '新闻修改成功！');
				editDialogVisible.value = false; // 关闭弹窗
				resetEditForm(); // 重置表单数据
				fetchNewsList(); // 刷新新闻列表
			} else {
				// 后端返回业务错误 (code !== 1)
				// 尝试显示后端返回的具体错误消息，如果后端返回了 msg 字段
				// ElMessage.error(res.msg || (isAdding ? '新闻添加失败' : '新闻修改失败')); // <-- 移除此行，避免重复提示
				console.warn('提交新闻业务失败:', res.msg); // 可以保留日志
				// 保持弹窗打开，以便用户根据后端错误信息修改输入
			}
		} catch (error) {
			// 捕获真正的请求错误 (网络问题, CORS, request.js 拦截器抛出错误等)
			console.error('提交新闻请求失败:', error);

			// 尝试从 error 对象或其响应中提取更具体的错误信息
			// 这取决于你的 request.js 如何封装错误，但现在 request.js 已经在 rejection 时弹窗了
			// 这里的 ElMessage 主要用于兜底或者对 request.js 拦截器未处理的错误
			// const specificErrorMessage =
			// 	error.message || (error.response && error.response.data && error.response.data.msg);

			const errorMessageToDisplay = isAdding
				? '新增新闻失败，请稍后再试'
				: '修改新闻失败，请稍后再试';

			// 如果 request.js 已经弹窗了，这里可以不再弹，或者只弹一个通用的兜底
			// 考虑到 request.js 对 HTTP 和特定业务错误的 reject 处理，这里弹出的错误更倾向于网络或未被拦截器详细处理的错误
			ElMessage.error(errorMessageToDisplay);

			// 如果是新增操作失败，并且弹窗应该保持打开以修改，这里不需要关闭弹窗和重置
			// 如果希望失败后弹窗自动关闭并清空，可以在这里调用 resetEditForm() 和设置 editDialogVisible.value = false;
		}
	};

	// 重置新增/修改表单
	const resetEditForm = () => {
		if (editFormRef.value) {
			// resetFields 会将字段重置为 initialValue 或 undefined/null
			// 这里的规则会清空绑定的值并移除校验提示
			editFormRef.value.resetFields();
		}
		// 手动确保 id 为 null，以正确判断新增模式，并清空其他字段值
		editForm.value = {
			id: null,
			title: '',
			content: '',
			publishDate: null,
		};
	};

	// === 删除新闻相关方法 ===
	const handleDeleteNews = (row) => {
		ElMessageBox.confirm(`确定要删除新闻《${row.title}》吗？`, '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
		})
			.then(async () => {
				// 当 request.js 不再 reject 业务错误时，这里不会因为 code !== 1 进入 catch
				try {
					// 仍然保留 try...catch 用于捕获真正的网络错误或异常
					const { data: res } = await deleteBackNewsApi(row.id); // 调用删除 API
					if (res.code === 1) {
						ElMessage.success('新闻删除成功！');
						// 删除成功后判断是否需要留在当前页或跳转到上一页
						if (tableData.value.length === 1 && pagination.value.page > 1) {
							pagination.value.page--;
						}
						fetchNewsList(); // 刷新列表
					} else {
						// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
						// ElMessage.error(res.msg || '新闻删除失败'); // <-- 移除此行，避免重复提示
						console.warn('新闻删除业务失败:', res.msg); // 可以保留日志
					}
				} catch (error) {
					// 捕获真正的请求错误 (网络问题、HTTP错误等，不包括 code !== 1 的业务错误)
					console.error('删除新闻请求失败:', error);
					ElMessage.error('删除新闻失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
				}
			})
			.catch(() => {
				// ElMessageBox.confirm 的 catch，处理用户点击取消
				ElMessage.info('已取消删除');
			});
	};

	// === 发布/取消发布相关方法 ===

	// 在状态切换前进行确认
	const beforeStatusChange = (row) => {
		const confirmText = row.isPublished
			? `确定要取消发布新闻《${row.title}》吗？`
			: `确定要发布新闻《${row.title}》吗？`;
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
		// row.isPublished 在 beforeStatusChange 确认后已经更新为期望的新状态 (true 或 false)
		// 后端 API 需要 0 或 1
		const newStatus = row.isPublished ? 1 : 0; // 1 表示发布，0 表示取消发布

		try {
			// 注意：updateBackNewsStatusApi 期望 id 和 status
			const { data: res } = await updateBackNewsStatusApi(row.id, newStatus); // 调用更新状态 API

			if (res.code === 1) {
				ElMessage.success(`${newStatus === 1 ? '发布' : '取消发布'}成功！`);
				// 状态已由 switch 自动更新，无需手动修改 row.isPublished
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || `${newStatus === 1 ? '发布' : '取消发布'}失败`); // <-- 移除此行，避免重复提示
				console.warn('更新新闻状态业务失败:', res.msg); // 可以保留日志
				// 操作失败，手动将 switch 状态拨回原样
				row.isPublished = !row.isPublished;
			}
		} catch (error) {
			// 捕获真正的请求错误 (网络问题、HTTP错误等)
			console.error('更新新闻状态请求失败:', error);
			ElMessage.error(`${newStatus === 1 ? '发布' : '取消发布'}失败，请稍后再试`); // <-- 这个用于网络或HTTP错误
			// 操作失败，手动将 switch 状态拨回原样
			row.isPublished = !row.isPublished;
		}
	};

	// === 查看新闻详情相关方法 ===
	// 修改 showDetails 函数，调用详情接口获取完整数据
	const showDetails = async (row) => {
		detailDialogVisible.value = true;
		// 在加载新的详情数据前，清空旧的数据，以免显示陈旧信息
		currentRowDetails.value = null;

		try {
			// 调用后台新闻详情 API 根据新闻ID获取完整的详情数据
			const { data: res } = await getBackNewsDetailApi(row.id); // <-- 调用详情 API

			if (res.code === 1 && res.data) {
				currentRowDetails.value = res.data; // 使用详情接口返回的数据更新 currentRowDetails
			} else {
				// 业务失败 (code !== 1) 或数据不存在，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || '获取新闻详情失败'); // <-- 移除此行，避免重复提示
				console.warn('获取新闻详情业务失败:', res.msg); // 可以保留日志
				detailDialogVisible.value = false; // 获取失败则关闭弹窗
			}
		} catch (error) {
			// 捕获真正的请求错误 (网络问题、HTTP错误等)
			console.error('获取新闻详情请求失败:', error);
			ElMessage.error('获取新闻详情失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
			detailDialogVisible.value = false; // 请求失败则关闭弹窗
		}
	};

	// === 组件挂载后，首次加载数据 ===
	onMounted(() => {
		fetchNewsList(); // 获取新闻列表
	});
</script>

<style scoped lang="less">
	.admin-news-list-container {
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

			// 调整 search-form 直接子元素的 el-form-item 的垂直间距
			// 这会影响第一个 input-items-group 下方的 el-form-item (如 发布日期)
			> .el-form-item {
				flex-shrink: 0; // 防止日期选择器或按钮组缩小
				// 如果希望它独占一行，可以加 width: 100%;
			}

			// === 风格统一的表单项样式 ===
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
					// 确保内容区域不会拉伸，宽度由内部元素决定 (默认行为)
					flex-grow: 0;
					width: auto;
					display: flex; /* 使内部元素更好地对齐 */
					align-items: center;
				}

				// === 设置输入框、选择框、数字输入框、日期选择器组件的固定宽度 ===
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
					width: auto; /* 数字输入框宽度由内容或父级flex决定 */
				}
			}

			// === 针对第一个分组的均匀分布样式 ===
			.evenly-distributed-group {
				.el-form-item {
					flex-grow: 1; // 让 form item 增长
					flex-basis: 0; // 使其起始基准为 0，更容易实现均匀分布
					min-width: 150px; // 设置一个最小宽度防止挤压过窄

					:deep(.el-form-item__content) {
						flex-grow: 1; // 内容区域也增长
						width: 100%; // 让内部 input/select 填充
					}

					:deep(.el-input),
					:deep(.el-select) {
						width: 100% !important; // input/select 填充父级内容区域
						min-width: auto; // 移除通用最小宽度限制
					}
				}
			}

			.button-group {
				margin-left: auto; /* 将按钮组推到最右边 */
				margin-right: 0 !important; /* 确保没有右侧 margin */
				flex-shrink: 0; // 防止按钮组缩小

				.el-button {
					margin-left: 10px; // 按钮之间的水平间距
					&:first-child {
						margin-left: 0;
					}
				}

				// === 新增按钮的特定样式，增加左外边距 ===
				.add-button-separate {
					margin-left: 30px; // 设置一个更大的左外边距，例如 30px
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

				// 新增按钮的绿色风格
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
				justify-content: center; /* 默认居中对齐 */
				word-break: break-word; /* 允许单词中断 */
				white-space: pre-wrap; /* 保留空白符，但允许换行 */

				// 如果某些列需要左对齐，可以添加 class 并修改样式
				// 例如：.el-table-column.align-left :deep(.cell) { justify-content: flex-start; }
			}
			// 对齐标题列
			.el-table-column[prop='title'] :deep(.cell) {
				justify-content: flex-start; /* 标题左对齐 */
			}

			.detail-button {
				background-color: #f0f0f0 !important;
				border: 1px solid #dcdcdc !important;
				color: #333 !important;
				border-radius: 2px !important;
				margin-right: 5px;
				/* 按钮之间留点间距 */
			}
			.el-button + .el-button {
				/* 按钮之间的默认间距 */
				margin-left: 5px;
			}

			// 调整操作列按钮与 switch 的间距
			.el-button + .el-switch {
				margin-left: 10px;
			}
			.el-switch + .el-button {
				margin-left: 10px; // unlikely but just in case
			}
			.el-button {
				// 确保操作按钮高度一致
				height: 28px; // Element Plus small size default height
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
				// 调整详情弹窗内表单的样式
				.el-form-item {
					margin-bottom: 10px;
					/* 详情项下方间距可以小一些 */
					.el-form-item__label {
						font-weight: bold;
						color: #555;
						width: 100px !important; /* 标签宽度与弹窗表单一致 */
						text-align: right;
					}
					.el-form-item__content {
						color: #333;
						line-height: 1.5;
						word-break: break-all;
						flex-grow: 1; // 内容区域填充剩余空间
						margin-left: 0 !important; /* 取消 el-form-item__label-wrap 可能产生的左外边距 */

						img {
							vertical-align: middle;
						}
						.el-tag {
							vertical-align: middle;
						}
						.news-content-display {
							// 确保内容展示区域宽度正确
							width: 100%;
							box-sizing: border-box; /* 包含 padding 和 border 在内 */
							padding: 5px 0; // 示例内边距
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
