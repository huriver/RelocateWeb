<script setup>
  import { queryNoticApi } from '@/api/common.js'
  import { useRouter } from 'vue-router'
  import { onMounted, ref } from 'vue'
  import { ElMessage } from 'element-plus' // 确保导入 ElMessage

  const router = useRouter()

  const noticForm = ref({
    page: 1,
    pageSize: 10,
    title: '',
    content: '',
    category: '',
    total: 0,
  })
  const noticData = ref([])

  // 修正 queryNotic 函数，增加错误处理和健壮性
  const queryNotic = async () => {
    try {
      const { data: res } = await queryNoticApi(noticForm.value)
      if (res.code === 1) {
        noticForm.value.total = res.data && res.data.total ? res.data.total : 0; // 确保 total 不会是 undefined
        noticData.value = res.data && res.data.records ? res.data.records : []; // 确保 records 始终是数组
      } else {
        console.error('Failed to fetch notic data:', res.msg || 'Unknown error');
        ElMessage.error(res.msg || '获取通知数据失败'); // 使用 ElMessage 进行错误提示
        noticData.value = [];
        noticForm.value.total = 0;
      }
    } catch (error) {
      console.error('Network or API error fetching notic data:', error);
      ElMessage.error('网络错误或请求失败，请稍后再试'); // 使用 ElMessage 进行错误提示
      noticData.value = [];
      noticForm.value.total = 0;
    }
  }

  onMounted(() => queryNotic())

  const reset = () => {
    noticForm.value = {
      page: 1,
      pageSize: 10,
      title: '',
      content: '',
      category: '',
      total: 0,
    }
    queryNotic()
  }
  const gotoDetail = (id) => {
    router.push({
      path: `/userHome/notic/${id}`
    })
  }
</script>

<template>
  <div class="notic-container">
    <div class="search-panel">
      <el-form :inline="true" :model="noticForm" class="search-form">
        <el-form-item label="标题">
          <el-input v-model="noticForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="noticForm.content" placeholder="请输入内容" clearable></el-input>
        </el-form-item>
        <el-form-item label="类别">
          <el-input v-model="noticForm.category" placeholder="请输入类别" clearable></el-input>
        </el-form-item>
        <el-form-item class="btn-group"> <el-button type="primary"
                     @click="queryNotic">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="data-list-box">
      <div class="data-item" v-for="item in noticData" :key="item.id" @click="gotoDetail(item.id)">
        <div class="title">{{ item.title }}</div>
        <div class="content">
          {{ item.content }}
        </div>
        <div class="bottom">
          <el-tag class="category" type="primary" v-if="item.category">{{ item.category }}</el-tag>
          <div class="updateTime">{{ item.updateTime }}</div>
        </div>
      </div>
      <el-empty v-if="noticData.length === 0" description="暂无数据" />
    </div>
    <div class="pagination-footer">
      <el-pagination v-model:current-page="noticForm.page" v-model:page-size="noticForm.pageSize"
                     :page-sizes="[10, 20, 30, 40]" layout="total, sizes, prev, pager, next, jumper"
                     :total="noticForm.total" @size-change="queryNotic"
                     @current-change="queryNotic" />
    </div>
  </div>
</template>

<style scoped lang="less">
  // 整个页面的内容容器，与 News.vue 保持一致的简约卡片风格
  .notic-container {
    max-width: 1200px; // 限制页面内容的最大宽度
    margin: 20px auto; // 上下外边距 20px，左右自动居中
    padding: 30px; // 内部填充，让内容有更多呼吸空间
    background-color: #fff; // 白色背景，使其在父级浅灰色背景上浮现
    border-radius: 8px; // 圆角
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05); // 轻微的阴影，增加立体感
  }

  // 搜索表单区域
  .search-panel {
    padding-bottom: 20px; // 底部内边距，与下方列表分隔
    margin-bottom: 20px; // 底部外边距，与下方列表分隔
    border-bottom: 1px solid #eee; // 底部细边框，作为视觉分隔
  }

  // 为按钮组添加样式，使其靠右显示
  .btn-group {
    margin-left: auto; // 利用 Flexbox 的特性，将按钮组推到最右侧
    display: flex;
    gap: 12px; // 按钮之间的间距
  }

  .search-form {
    display: flex; // 使用 Flexbox 布局
    gap: 20px; // 表单项之间的水平间距
    align-items: center; // 垂直居中对齐所有表单项

    .el-form-item {
      margin-bottom: 0; // 移除 Element Plus 默认的 margin-bottom
      display: flex; // 确保内部也使用 Flexbox 来对齐 label 和 input
      align-items: center;

      // 使用 :deep() 穿透作用域样式，直接作用于 el-input 组件
      :deep(.el-form-item__label) {
        width: auto; // 标签宽度自适应
        margin-right: 8px; // 标签与输入框之间的间距
      }

      :deep(.el-input) {
        width: 200px; // 输入框的固定宽度，与 News.vue 保持一致
      }
    }
  }

  // 数据列表区域
  .data-list-box {
    padding-top: 20px; // 顶部内边距，与搜索区域分隔

    .data-item {
      padding: 18px 20px; // 增大内边距，内容有更多呼吸空间
      margin-bottom: 20px; // 数据项之间的外边距
      cursor: pointer;
      border: 1px solid rgba(0, 0, 0, 0.08); // 更柔和的边框颜色
      border-radius: 8px;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03); // 添加轻微的初始阴影，增强立体感
      transition: all 0.3s ease-in-out; // 添加过渡效果，使悬停平滑

      &:hover {
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08); // 悬停时阴影加深
        transform: translateY(-3px); // 悬停时轻微上移
      }

      .title {
        color: var(
          --el-color-primary,
          rgb(64, 158, 255)
        ); // 使用 Element Plus 主色变量
        font-size: 20px; // 标题字体大小
        font-weight: bold; // 加粗
        margin-bottom: 8px; // 标题与内容之间间隔
      }

      .content {
        margin: 10px 0; // 内容上下外边距
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 3; // 限制显示 3 行内容
        overflow: hidden;
        text-overflow: ellipsis; // 超出部分显示省略号
        word-break: break-word; // 防止长单词溢出
        line-height: 1.6; // 增加行高，提高可读性
        color: #666; // 内容文本颜色更柔和
      }

      .bottom {
        display: flex;
        justify-content: space-between; // 内容左右两端对齐
        align-items: center; // 垂直居中对齐

        .category {
          font-size: 12px; // 确保字体大小合适
        }

        .updateTime {
          text-align: right;
          color: #999; // 时间文本颜色更柔和
          font-size: 13px; // 时间文本字体大小
        }
      }
    }

    .data-item:hover .title {
      text-decoration: underline;
      text-decoration-color: var(
        --el-color-primary,
        rgb(64, 158, 255)
      ); // 使用 Element Plus 主色变量
      text-underline-offset: 4px;
    }
  }

  // 分页区域
  .pagination-footer {
    margin-top: 20px; // 顶部外边距
    display: flex;
    justify-content: center; // 水平居中分页组件
  }
</style>