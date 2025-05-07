<script setup>
import { queryNewsApi } from '@/api/common.js'
import { useRouter } from 'vue-router'

const router = useRouter()

const newsForm = ref({
  page: 1,
  pageSize: 10,
  title: '',
  content: '',
  category: '',
})
const newsData = ref([])
const queryNews = async () => {
  const { data: res } = await queryNewsApi(newsForm.value)
  if (res.code === 1) {
    newsForm.value.total = res.data.total
    newsData.value = res.data.records
  }
}

onMounted(() => queryNews())

const reset = () => {
  newsForm.value = {
    page: 1,
    pageSize: 10,
    title: '',
    content: '',
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
  <div class="news-container home-container">
    <div class="search">
      <el-form :inline="true" :model="newsForm" class="search-form">
        <el-form-item label="标题">
          <el-input v-model="newsForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="newsForm.content" placeholder="请输入内容"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="queryNews">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="box">
      <div class="data-item" v-for="item in newsData" :key="item.id" @click="gotoDetail(item.id)">
        <div class="title">{{ item.title }}</div>
        <div class="content">
          {{ item.content }}
        </div>
        <div class="bottom">
          <el-tag class="category" type="primary" v-if="item.category">{{ item.category }}</el-tag>
          <div class="updateTime">{{ item.updateTime }}</div>
        </div>
      </div>
    </div>
    <el-pagination v-model:current-page="newsForm.page" v-model:page-size="newsForm.pageSize"
      :page-sizes="[10, 20, 30, 40]" :size="size" :disabled="disabled" layout="total, sizes, prev, pager, next, jumper"
      :total="newsForm.total" @size-change="queryNews" @current-change="queryNews" />
  </div>
</template>

<style scoped lang="less">
.search-form {
  text-align: center;

  .el-form-item {
    margin-bottom: 0;
  }
}

.box {
  padding-top: 28px;

  .data-item {
    padding: 15px;
    margin-bottom: 28px;
    cursor: pointer;
    border: 1px solid #ccc;
    border-radius: 8px;

    .title {
      color: rgb(64, 158, 255);
      font-size: 24px;
    }

    .content {
      margin: 15px 0 15px 0;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 3;
      overflow: hidden;
      text-overflow: ellipsis;
      word-break: break-word;
    }

    .bottom {
      display: flex;
      justify-content: space-between;

      .updateTime {
        width: 100%;
        text-align: right;
      }
    }
  }

  .data-item:hover .title {
    text-decoration: underline;
    text-decoration-color: rgb(64, 158, 255);
    text-underline-offset: 4px;
  }
}
</style>