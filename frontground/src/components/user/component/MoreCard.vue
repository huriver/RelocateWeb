<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
const props = defineProps({
  title: {
    type: String,
    required: true
  },
  data: {
    type: Array,
    required: true
  },
  routeLink: {
    type: String,
    required: true
  }
})
const gotoDetail = (id) => {
  router.push({
    path: `${props.routeLink}/${id}`
  })
}
</script>

<template>
  <div class="more-card-container" v-show="data.length > 0">
    <div class="head">
      <div class="title">{{ title }}</div>
      <el-button type="primary" style="width: 60px;" @click="router.push(routeLink)">更多</el-button>
    </div>
    <div class="box">
      <div class="data-item" v-for="item in data" :key="item.id" @click="gotoDetail(item.id)">
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
  </div>
</template>

<style scoped lang="less">
.more-card-container {
  margin-top: 12px;

  .head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 50px;
    line-height: 50px;

    .title {
      box-sizing: border-box;
      width: 120px;
      position: relative;
      padding-left: 16px;
      color: #fff;
      background-color: rgb(247, 168, 39);
      font-size: 18px;
    }

    .title::after {
      content: "";
      position: absolute;
      right: -20px;
      top: -20px;
      transform: rotate(45deg);
      display: inline-block;
      width: 40px;
      height: 40px;
      background-color: #fff;
    }

    .title::before {
      content: "";
      position: absolute;
      right: -20px;
      bottom: -20px;
      transform: rotate(45deg);
      display: inline-block;
      width: 40px;
      height: 40px;
      background-color: #fff;
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
}
</style>