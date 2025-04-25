<script setup>
import { queryNoticApi, queryNewsApi } from '@/api/common.js'
import { onMounted, ref } from 'vue'

const noticForm = ref({
  page: 1,
  pageSize: 3,
  title: '',
  content: '',
  category: ''
})
const noticData = ref([])
const queryNotic = async () => {
  const { data: res } = await queryNoticApi(noticForm.value)
  if (res.code === 1) {
    noticData.value = res.data.records
  }
}

const newsForm = ref({
  page: 1,
  pageSize: 3,
  title: '',
  content: '',
  category: ''
})
const newsData = ref([])
const queryNews = async () => {
  const { data: res } = await queryNewsApi(newsForm.value)
  if (res.code === 1) {
    newsData.value = res.data.records
  }
}
onMounted(() => {
  queryNotic()
  queryNews()
})
</script>

<template>
  <div class="front-container home-container">
    <more-card :title="'搬家须知'" :data="noticData" :routeLink="'/userHome/notic'"></more-card>
    <more-card :title="'搬家新闻'" :data="newsData" :routeLink="'/userHome/news'"></more-card>
  </div>
</template>

<style scoped></style>