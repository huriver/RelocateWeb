<script setup>
import { useRoute } from 'vue-router'
import { queryNoticDetailApi } from '@/api/common.js'
import { onMounted, ref } from 'vue'

const route = useRoute()
const notic = ref({})

onMounted(async () => {
  const { data: res } = await queryNoticDetailApi(route.params.id)
  if (res.code !== 1) return ElMessage.error(res.msg)
  notic.value = res.data
})
</script>

<template>
  <div class="notic-detail-container home-container">
    <h1 class="title">{{ notic.title }}</h1>
    <div class="info">
      <div class="publish-date">发布日期：{{ notic.publishDate }}</div>
      <div class="update-time">编辑时间：{{ notic.updateTime }}</div>
      <el-tag class="category" type="primary">{{ notic.category }}</el-tag>
    </div>
    <div class="content">{{ notic.content }}</div>
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