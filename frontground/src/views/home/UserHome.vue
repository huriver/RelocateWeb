<!-- UserHome.vue 修改后 -->
<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { myStore } from '@/stores/store.js'

const store = myStore()
const route = useRoute()
const router = useRouter()

// 使用 ref 创建响应式变量
const activeRouter = ref(store.routePath || "/userHome/front")

// 监听路由变化
watch(
  () => route.path,
  (newPath) => {
    activeRouter.value = newPath
    store.saveRoutePath(newPath)
  },
  { immediate: true } // 初始化立即执行
)
</script>

<template>
  <div class="home-page">
    <el-container>
      <el-header>
        <img class="logo" src="../../assets/img/logo.png" @click="router.push('/userHome/front')">
        <el-menu :default-active="activeRouter" mode="horizontal" router :ellipsis="false">
          <el-menu-item index="/userHome/front">首页</el-menu-item>
          <el-menu-item index="/userHome/service">搬家服务</el-menu-item>
          <el-menu-item index="/userHome/notic">搬家须知</el-menu-item>
          <el-menu-item index="/userHome/news">搬家新闻</el-menu-item>
          <el-menu-item index="/userHome/my">个人中心</el-menu-item>
        </el-menu>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<style lang='less' scoped>
.logo {
  width: 80px;
  position: absolute;
  left: 20px;
  cursor: pointer;
}

.el-header {
  width: 100%;
  padding: 0;
  box-shadow: 0 5px 15px -5px rgba(0, 0, 0, 0.1);
  position: fixed;
  z-index: 999;
  background-color: #fff;

  .el-menu {
    width: 500px;
    margin: auto;
    border-bottom: none;
  }

  .el-menu-item {
    width: 100px;
  }
}
</style>