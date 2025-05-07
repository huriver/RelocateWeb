<script setup>
import { queryNoticApi } from '@/api/common.js'
import { useRouter } from 'vue-router'

const router = useRouter()

const noticForm = ref({
  page: 1,
  pageSize: 10,
  title: '',
  content: '',
  category: '',
})
const noticData = ref([])
const queryNotic = async () => {
  const { data: res } = await queryNoticApi(noticForm.value)
  if (res.code === 1) {
    noticForm.value.total = res.data.total
    noticData.value = res.data.records
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
  <div class="notic-container home-container">
    <div class="search">
      <el-form :inline="true" :model="noticForm" class="search-form">
        <el-form-item label="标题">
          <el-input v-model="noticForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="noticForm.content" placeholder="请输入内容"></el-input>
        </el-form-item>
        <el-form-item label="类别">
          <el-input v-model="noticForm.category" placeholder="请输入类别"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="queryNotic">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="box">
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
    </div>
    <el-pagination v-model:current-page="noticForm.page" v-model:page-size="noticForm.pageSize"
      :page-sizes="[10, 20, 30, 40]" :size="size" :disabled="disabled" layout="total, sizes, prev, pager, next, jumper"
      :total="noticForm.total" @size-change="queryNotic" @current-change="queryNotic" />
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