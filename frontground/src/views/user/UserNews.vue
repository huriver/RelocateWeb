<script setup>
  import { queryNewsApi } from '@/api/common.js'
  import { useRouter } from 'vue-router'
  import { ref, onMounted } from 'vue'
  import { ElMessage } from 'element-plus'

  const router = useRouter()

  const newsForm = ref({
    page: 1,
    pageSize: 10,
    title: '',
    content: '',
    // 数据库中没有 category 字段，因此已移除此筛选条件
    // category: '', 
    total: 0,
  })
  const newsData = ref([])

  const queryNews = async () => {
    try {
      const { data: res } = await queryNewsApi(newsForm.value)
      if (res.code === 1) { // 检查响应码是否为成功
        // 确保 total 和 records 不会是 undefined，进行防御性赋值
        newsForm.value.total = res.data && res.data.total ? res.data.total : 0;
        newsData.value = res.data && res.data.records ? res.data.records : [];
      } else {
        console.error('Failed to fetch news data:', res.msg || 'Unknown error');
        ElMessage.error(res.msg || '获取新闻数据失败'); // 用户友好提示
        newsData.value = [];
        newsForm.value.total = 0;
      }
    } catch (error) {
      console.error('Network or API error fetching news data:', error);
      ElMessage.error('网络错误或请求失败，请稍后再试'); // 用户友好提示
      newsData.value = [];
      newsForm.value.total = 0;
    }
  }

  onMounted(() => queryNews())

  const reset = () => {
    newsForm.value = {
      page: 1,
      pageSize: 10,
      title: '',
      content: '',
      // 重置时也移除 category 字段的赋值
      // category: '', 
      total: 0,
    }
    queryNews()
  }

  const gotoDetail = (id) => {
    router.push({
      path: `/userHome/news/${id}`
    })
  }
</script>

<template>
  <div class="news-container">
    <div class="search-panel">
      <el-form :inline="true" :model="newsForm" class="search-form">
        <el-form-item label="标题">
          <el-input v-model="newsForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="newsForm.content" placeholder="请输入内容" clearable></el-input>
        </el-form-item>
        <el-form-item class="btn-group">
          <el-button type="primary" @click="queryNews">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="data-list-box">
      <div class="data-item" v-for="item in newsData" :key="item.id" @click="gotoDetail(item.id)">
        <div class="title">{{ item.title }}</div>
        <div class="content">
          {{ item.content }}
        </div>
        <div class="bottom">
          <div class="updateTime">{{ item.updateTime }}</div>
        </div>
      </div>
      <el-empty v-if="newsData.length === 0" description="暂无数据" />
    </div>
    <div class="pagination-footer">
      <el-pagination v-model:current-page="newsForm.page" v-model:page-size="newsForm.pageSize"
                     :page-sizes="[10, 20, 30, 40]" layout="total, sizes, prev, pager, next, jumper"
                     :total="newsForm.total" @size-change="queryNews" @current-change="queryNews" />
    </div>
  </div>
</template>

<style scoped lang="less">
  // 整个页面的内容容器，与 Notic.vue 保持一致的简约卡片风格
  .news-container {
    max-width: 1200px; // 限制页面内容的最大宽度，保持与 Notic.vue 列表页一致
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

  .btn-group {
    margin-left: auto; // 利用 Flexbox 的特性，将按钮组推到右侧
    display: flex;
    gap: 12px; // 按钮之间的间距
  }

  .search-form {
    display: flex; // 使用 Flexbox 布局
    gap: 20px; // 表单项之间的水平间距
    align-items: center; // 垂直居中对齐所有表单项

    .el-form-item {
      margin-bottom: 0; // 移除 Element Plus 默认的 margin-bottom
      display: flex;
      align-items: center;

      :deep(.el-form-item__label) {
        width: auto; // 标签宽度自适应
        margin-right: 8px; // 标签与输入框之间的间距
      }

      :deep(.el-input) {
        width: 200px; // 输入框的固定宽度
      }
    }
    // 移除了冗余的 > .el-button 样式
  }

  // 数据列表区域
  .data-list-box {
    padding-top: 20px; // 顶部内边距，与搜索区域分隔

    .data-item {
      padding: 18px 20px; // 增大内边距，内容有更多呼吸空间
      margin-bottom: 20px; // 数据项之间的外边距
      cursor: pointer;
      border: 1px solid rgba(0, 0, 0, 0.08); // 柔和的边框颜色
      border-radius: 8px;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03); // 轻微的初始阴影
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
        font-size: 20px;
        font-weight: bold;
        margin-bottom: 8px;
      }

      .content {
        margin: 10px 0;
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