<script setup>
import { useRoute } from 'vue-router'
import { queryNewsDetailApi } from '@/api/common.js'
import { onMounted, ref } from 'vue'

const route = useRoute()
const news = ref({})

onMounted(async () => {
  const { data: res } = await queryNewsDetailApi(route.params.id)
  if (res.code !== 1) return ElMessage.error(res.msg)
  news.value = res.data
})
</script>

<template>
  <div class="new-detail-container home-container">
    <h1 class="title">{{ news.title }}</h1>
    <div class="info">
      <div class="publish-date">发布日期：{{ news.publishDate }}</div>
      <div class="update-time">编辑时间：{{ news.updateTime }}</div>
    </div>
    <div class="content">{{ news.content }}</div>
  </div>
</template>

<style scoped lang="less">
.title {
  text-align: center;
}

.info {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 15px 0 15px 0;
  font-size: 14px;

  .update-time {
    margin: 0 20px 0 20px;
  }
}
</style>