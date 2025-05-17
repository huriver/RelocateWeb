<template>
	<div class="admin-consumer-list-container">
		<h3>消费者管理</h3>

		<el-form :inline="true" :model="searchForm" class="search-form">
			<div class="input-items-group">
				<el-form-item label="用户名">
					<el-input v-model="searchForm.username" placeholder="请输入用户名" clearable></el-input>
				</el-form-item>
				<el-form-item label="姓名">
					<el-input v-model="searchForm.name" placeholder="请输入姓名" clearable></el-input>
				</el-form-item>
				<el-form-item label="手机号">
					<el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable></el-input>
				</el-form-item>
				<el-form-item label="状态">
					<el-select v-model="searchForm.isBanned" placeholder="请选择状态" clearable>
						<el-option label="正常" :value="false"></el-option>
						<el-option label="已封禁" :value="true"></el-option>
					</el-select>
				</el-form-item>
			</div>

			<el-form-item label="注册时间">
				<el-date-picker
					v-model="searchForm.createTimeRange"
					type="datetimerange"
					range-separator="至"
					start-placeholder="开始时间"
					end-placeholder="结束时间"
					value-format="YYYY-MM-DD HH:mm:ss"
				></el-date-picker>
			</el-form-item>

			<el-form-item class="button-group">
				<el-button type="primary" @click="handleSearch">查询</el-button>
				<el-button @click="resetSearchForm">重置</el-button>
			</el-form-item>
		</el-form>

		<el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
			<el-table-column prop="id" label="ID" align="center"></el-table-column>
			<el-table-column label="头像" align="center">
				<template #default="scope">
					<img
						v-if="scope.row.photoUrl"
						:src="scope.row.photoUrl"
						alt="用户头像"
						style="width: auto; height: 45px; border-radius: 50%; object-fit: cover"
					/>
					<span v-else>无头像</span>
				</template>
			</el-table-column>
			<el-table-column prop="username" label="用户名" align="center"></el-table-column>
			<el-table-column prop="name" label="姓名" align="center"></el-table-column>
			<el-table-column prop="phone" label="手机号" align="center"></el-table-column>
			<el-table-column label="状态" align="center">
				<template #default="scope">
					<el-tag :type="scope.row.isBanned ? 'danger' : 'success'">
						{{ scope.row.isBanned ? '已封禁' : '正常' }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column
				prop="createTime"
				label="注册时间"
				width="180"
				align="center"
			></el-table-column>
			<el-table-column label="操作" width="250" align="center">
				<template #default="scope">
					<el-button size="small" @click="showDetails(scope.row)" class="detail-button"
						>详情</el-button
					>
					<el-switch
						v-model="scope.row.isBanned"
						active-text="已封禁"
						inactive-text="正常"
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

		<el-dialog v-model="dialogVisible" title="消费者详情" width="600px">
			<el-form label-width="100px" v-if="currentRowDetails">
				<el-form-item label="ID">{{ currentRowDetails.id }}</el-form-item>
				<el-form-item label="用户名">{{ currentRowDetails.username }}</el-form-item>
				<el-form-item label="姓名">{{ currentRowDetails.name }}</el-form-item>
				<el-form-item label="性别">{{
					currentRowDetails.gender === 0 ? '女' : currentRowDetails.gender === 1 ? '男' : '未知'
				}}</el-form-item>
				<el-form-item label="手机号">{{ currentRowDetails.phone }}</el-form-item>
				<el-form-item label="邮箱">{{ currentRowDetails.email }}</el-form-item>
				<el-form-item label="身份证号">{{ currentRowDetails.idCard }}</el-form-item>
				<el-form-item label="家庭手机号">{{ currentRowDetails.familyPhone }}</el-form-item>
				<el-form-item label="头像">
					<img
						v-if="currentRowDetails.photoUrl"
						:src="currentRowDetails.photoUrl"
						alt="用户头像"
						style="width: 80px; height: 80px; border-radius: 50%; object-fit: cover"
					/>
					<span v-else>无头像</span>
				</el-form-item>
				<el-form-item label="状态">
					<el-tag :type="currentRowDetails.isBanned ? 'danger' : 'success'">
						{{ currentRowDetails.isBanned ? '已封禁' : '正常' }}
					</el-tag>
				</el-form-item>
				<el-form-item label="注册时间">{{ currentRowDetails.createTime }}</el-form-item>
				<el-form-item label="更新时间">{{ currentRowDetails.updateTime }}</el-form-item>
			</el-form>
			<template #footer>
				<div class="dialog-footer">
					<el-button @click="dialogVisible = false">关闭</el-button>
				</div>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
	import { ref, onMounted } from 'vue';
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
		ElAvatar,
	} from 'element-plus';
	import { getConsumerListPageApi, updateConsumerStatusApi } from '@/api/userApi.js';

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
		phone: '',
		isBanned: null, // null 表示不筛选状态
		createTimeRange: null, // 日期范围选择器的绑定值
		// createTimeStart 和 createTimeEnd 会从 createTimeRange 中提取
	});

	// 详情弹窗状态和当前选中行数据
	const dialogVisible = ref(false);
	const currentRowDetails = ref(null);

	// === 数据获取方法 ===
	const fetchConsumerList = async () => {
		loading.value = true;
		try {
			// 准备请求参数
			const params = {
				page: pagination.value.page,
				pageSize: pagination.value.pageSize,
				username: searchForm.value.username || undefined, // 如果为空字符串，不发送该参数
				name: searchForm.value.name || undefined,
				phone: searchForm.value.phone || undefined,
				isBanned: searchForm.value.isBanned !== null ? searchForm.value.isBanned : undefined, // null 不发送，true/false 发送
				createTimeStart: searchForm.value.createTimeRange
					? searchForm.value.createTimeRange[0]
					: undefined,
				createTimeEnd: searchForm.value.createTimeRange
					? searchForm.value.createTimeRange[1]
					: undefined,
			};

			const { data: res } = await getConsumerListPageApi(params);

			if (res.code === 1) {
				// 确保后端返回的 isBanned 是 boolean 类型，或者在这里转换
				tableData.value = res.data.records;
				pagination.value.total = res.data.total;
			} else {
				// 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || '获取消费者列表失败'); // <-- 移除此行，避免重复提示
				console.warn('获取消费者列表业务失败:', res.msg); // 可以保留日志
				tableData.value = [];
				pagination.value.total = 0;
			}
		} catch (error) {
			// 捕获真正的请求错误 (网络问题、HTTP错误等，不包括 code !== 1 的业务错误)
			console.error('获取消费者列表请求失败:', error);
			ElMessage.error('获取消费者列表失败，请稍后再试'); // <-- 这个用于网络或HTTP错误
			tableData.value = [];
			pagination.value.total = 0;
		} finally {
			loading.value = false;
		}
	};

	// === 搜索相关方法 ===
	const handleSearch = () => {
		pagination.value.page = 1; // 从第一页开始搜索
		fetchConsumerList();
	};

	const resetSearchForm = () => {
		searchForm.value = {
			username: '',
			name: '',
			phone: '',
			isBanned: null,
			createTimeRange: null,
		};
		handleSearch(); // 重置后立即执行查询，回到第一页
	};

	// === 分页相关方法 ===
	const handleSizeChange = (val) => {
		pagination.value.pageSize = val;
		pagination.value.page = 1; // 切换每页大小时回到第一页
		fetchConsumerList();
	};

	const handleCurrentChange = (val) => {
		pagination.value.page = val;
		fetchConsumerList();
	};

	// === 封禁/解封操作相关方法 ===

	// 在状态切换前进行确认
	const beforeStatusChange = (row) => {
		const confirmText = row.isBanned
			? `确定要解封用户 "${row.username}" 吗？`
			: `确定要封禁用户 "${row.username}" 吗？`;
		return ElMessageBox.confirm(confirmText, '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
		})
			.then(() => {
				// 用户点击确定，允许 switch 状态改变
				return true;
			})
			.catch(() => {
				// 用户点击取消或关闭，阻止 switch 状态改变
				ElMessage.info('操作已取消');
				return false;
			});
	};

	// 处理状态切换
	const handleStatusChange = async (row) => {
		// 注意：此时 row.isBanned 是 switch 切换后的新状态
		const newStatus = row.isBanned ? 1 : 0; // 1 表示封禁，0 表示解封 (对应后端接口)
		const userId = row.id;

		try {
			const { data: res } = await updateConsumerStatusApi(userId, newStatus);

			if (res.code === 1) {
				ElMessage.success(`${newStatus === 1 ? '封禁' : '解封'}成功！`);
				// 操作成功，无需刷新列表，因为 switch 状态已经是正确的
			} else {
				// 后端返回业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
				// ElMessage.error(res.msg || `${newStatus === 1 ? '封禁' : '解封'}失败`); // <-- 移除此行，避免重复提示
				console.warn(`更新消费者状态业务失败 (${newStatus === 1 ? '封禁' : '解封'}):`, res.msg); // 可以保留日志
				// === 关键：操作失败，手动将 switch 状态拨回原样 ===
				row.isBanned = !row.isBanned; // 恢复到操作前的状态
				// ==============================================
			}
		} catch (error) {
			// 网络错误或请求失败
			console.error('更新消费者状态请求失败:', error);
			// 捕获真正的请求错误，并弹出通用提示
			ElMessage.error(`${newStatus === 1 ? '封禁' : '解封'}失败，请稍后再试`); // <-- 这个用于网络或HTTP错误
			// === 关键：操作失败，手动将 switch 状态拨回原样 ===
			row.isBanned = !row.isBanned; // 恢复到操作前的状态
			// ==============================================
		}
	};

	// === 查询详情相关方法 ===
	const showDetails = (row) => {
		// 注意：这个详情是直接使用当前行的数据，没有调用详情API。
		// 如果详情需要更多数据，需要调用详情API。
		currentRowDetails.value = row; // 将当前行的数据赋值给详情状态变量
		dialogVisible.value = true; // 打开弹窗
	};

	// === 组件挂载后，首次加载数据 ===
	onMounted(() => {
		fetchConsumerList();
	});
</script>

<style scoped lang="less">
	.admin-consumer-list-container {
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

			// 使用 Flexbox 布局控制主要行（输入组，日期，按钮）
			display: flex;
			flex-wrap: wrap; // 允许主要行换行
			align-items: center; // 垂直居中对齐主要行

			// 风格统一的表单项样式 (适用于所有 el-form-item 内的控件)
			.el-form-item {
				// === Style overrides for input/select/date-picker wrappers ===
				// 目标：边框 #ccc, 1px 宽度，去掉高光
				:deep(.el-input__wrapper),
				:deep(.el-select__wrapper),
				:deep(.el-date-editor .el-input__wrapper) {
					border-color: #ccc !important;
					border-width: 1px !important;
					border-style: solid !important;
					box-shadow: none !important; /* 移除默认阴影 */
				}

				// 额外确保 focus 状态下也没有阴影
				:deep(.el-input__wrapper.is-focus),
				:deep(.el-select__wrapper.is-focus),
				:deep(.el-date-editor .el-input__wrapper.is-focus) {
					box-shadow: none !important;
				}

				// === Style overrides for placeholder text ===
				// 目标：提示文字颜色 #999，字体大小 14px
				:deep(.el-input__inner::placeholder),
				:deep(.el-select__placeholder),
				:deep(.el-range-input::placeholder) {
					color: #999;
				}

				// === Style overrides for input/select text ===
				// 目标：字体大小 14px
				:deep(.el-input__inner),
				:deep(.el-select__inner),
				:deep(.el-range-input) {
					font-size: 14px;
				}

				// 标签样式调整 - 保持原样或微调对齐
				.el-form-item__label {
					padding-right: 8px;
					line-height: 32px; // 与输入框/按钮高度对齐
				}

				// 内容区域样式 - 确保输入框等控件垂直居中
				.el-form-item__content {
					line-height: 32px; // 与输入框/按钮高度对齐
					display: flex; // 使用 flex 让内部控件垂直居中
					align-items: center; // 垂直居中
				}

				// === 设置输入框、选择框、日期选择器组件的固定宽度 ===
				// 这可以防止输入文本时表单项变宽
				:deep(.el-input),
				:deep(.el-select) {
					width: 200px; /* 为输入框和选择框设置固定宽度 */
				}

				// 单独为日期范围选择器设置宽度
				:deep(.el-date-editor--datetimerange) {
					width: 380px; /* 为日期范围选择器设置固定宽度 */
				}

				// 确保组件内部的 input/wrapper 填充其父容器 (el-input/el-select/el-date-editor)
				:deep(.el-input__inner),
				:deep(.el-select__inner),
				:deep(.el-range-input),
				:deep(.el-input__wrapper),
				:deep(.el-select__wrapper),
				:deep(.el-date-editor .el-input__wrapper) {
					width: 100% !important; /* 确保内部元素填充其父组件的宽度 */
				}
			}

			// === 输入项组 (.input-items-group) 的布局样式 ===
			.input-items-group {
				display: flex; // 使组内的 el-form-item 成为 Flex Items
				justify-content: space-between; // 在组内项目之间均匀分布空间
				align-items: center; // 垂直居中对齐组内项目
				width: 100%; // 使该组占据一行，强制后续元素换行
				margin-bottom: 20px; // 在组下方添加 20px 垂直间距

				// 组内的 el-form-item 样式
				.el-form-item {
					flex-grow: 0; // 不允许项目放大
					flex-shrink: 0; // 不允许项目缩小
					min-width: 180px; // 确保组内每个输入项的最小宽度
					margin-right: 0; // 组内项目之间间距由 justify-content 决定，这里不需要 margin-right
					margin-bottom: 0; // 组内项目下方不需要垂直间距，间距在组下方统一添加
				}
			}

			// === search-form 直接子元素的垂直间距和水平间距 ===
			// 这会影响到日期选择器 el-form-item 和 按钮组 el-form-item
			> .el-form-item {
				margin-bottom: 20px; // 在日期选择器和按钮组下方添加 20px 垂直间距
				margin-right: 15px; // 在日期选择器后添加 15px 水平间距 (如果按钮组跟它同行)
			}

			// 针对日期选择器调整宽度
			.el-date-editor--datetimerange {
				width: 380px; // 根据需要调整宽度
			}

			// === 按钮组 (.button-group) 的布局样式 ===
			.button-group {
				margin-right: 0 !important; // 移除右侧外边距，确保靠右
				margin-left: auto; // 推到最右侧
				// margin-bottom 由上面的 > .el-form-item 控制

				.el-button {
					margin-left: 10px; // 按钮之间的间距
					&:first-child {
						margin-left: 0; // 第一个按钮左侧没有间距
					}
				}
			}

			// === Style overrides for Buttons ===
			.el-button {
				height: 32px; // 按钮高度
				border-radius: 4px; // 按钮圆角
			}

			// 查询按钮样式
			.el-button--primary {
				background-color: #1890ff !important;
				border-color: #1890ff !important;
				color: #fff !important;
				font-weight: bold;
			}

			// 重置按钮样式
			.el-button:not(.el-button--primary) {
				background-color: #f0f0f0 !important;
				border-color: #dcdcdc !important; // 添加一个浅边框
				color: #333 !important;
			}
		}

		.el-table {
			margin-bottom: 20px;

			// === 增加单元格内边距 ===
			// 使用 :deep() 穿透到 Element Plus 组件内部的 td 和 th 元素
			:deep(.el-table__cell) {
				padding: 8px 10px; /* 应用 8px 垂直内边距和 10px 水平内边距 */
			}

			// 调整表格头部样式
			:deep(.el-table__header-wrapper th) {
				background-color: #f5f7fa;
				color: #606266;
				font-weight: bold;
				// 确保表头单元格也应用了内边距
				padding: 8px 10px; // 与数据单元格内边距一致
			}

			// === 统一单元格内容的水平和垂直居中对齐 ===
			// :deep(.el-table__cell .cell) 是单元格内容容器
			:deep(.el-table__cell .cell) {
				display: flex;
				align-items: center; // 垂直居中对齐 flex items
				justify-content: center; // 水平居中对齐 flex items
				// text-align is handled by el-table-column align="center" and flexbox overrides it for items
			}

			// Style for the detail button in the operation column
			.detail-button {
				background-color: #f0f0f0 !important; /* 浅灰色背景 */
				border: 1px solid #dcdcdc !important; /* 浅边框 */
				color: #333 !important; /* 文字颜色 */
				border-radius: 2px !important; /* 2px 圆角 */
				// 保持默认 small size 的 padding 和 height
			}
		}

		.pagination {
			justify-content: flex-end; // 分页组件右对齐
			margin-top: 20px; // 分页与表格之间的间距
		}

		// === 详情弹窗内的样式优化 ===
		.el-dialog {
			.el-form {
				padding: 0 20px; // 弹窗内容内边距保持一致
			}
			.el-form-item {
				margin-bottom: 10px; // 减少底部外边距，使表单项更紧凑
				.el-form-item__label {
					font-weight: bold; // 标签加粗
					color: #555; // 标签颜色
				}
				.el-form-item__content {
					color: #333; // 内容颜色设为较深的灰色，更易读
					line-height: 1.5; // 设置行高，保持文本垂直居中感
					img {
						vertical-align: middle; // 头像垂直居中对齐
					}
					.el-tag {
						vertical-align: middle; // ElTag 垂直居中对齐
					}
				}
				// 调整标签和内容之间的间距
				.el-form-item__label-wrap {
					margin-right: 12px; // 保持标签和内容之间的间距
				}
			}
			.dialog-footer {
				text-align: right;
				padding-top: 15px; // 增加与上方内容的间距
				border-top: 1px solid #eee; // 添加一条细分割线，增强底部区域的区分
			}
		}
	}
</style>
