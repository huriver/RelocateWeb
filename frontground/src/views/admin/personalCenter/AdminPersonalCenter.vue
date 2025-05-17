<template>

  <div class="personal-center-layout">

    <el-container class="main-container">

      <el-aside width="200px" class="sidebar-menu">

        <el-menu :default-active="activeSubMenu" router class="personal-center-nav"
                 :unique-opened="true">

          <el-menu-item index="/admin/personal-center/info">

            <el-icon>
              <User />
            </el-icon>
            <span>个人信息</span>
          </el-menu-item>

          <el-menu-item index="/admin/personal-center/password">

            <el-icon>
              <Setting />
            </el-icon>
            <span>修改密码</span>
          </el-menu-item>

        </el-menu>

      </el-aside>

      <el-main class="content-area"> <router-view /> </el-main>
    </el-container>

  </div>
</template>

<script setup>
  import { ref, watch, onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { defineOptions } from 'vue';

  // 导入 Element Plus 图标
  import { User, Setting } from '@element-plus/icons-vue'; // 导入所需图标

  // 定义组件名称
  defineOptions({
    name: 'AdminPersonalCenter',
  });

  const route = useRoute();
  const router = useRouter();

  // 响应式变量，用于控制侧边菜单的激活状态
  const activeSubMenu = ref('');

  // 监听路由变化，更新激活的菜单项
  watch(
    () => route.path,
    (newPath) => {
      // console.log('Current path:', newPath); // 调试用
      activeSubMenu.value = newPath;
    },
    { immediate: true } // 立即执行一次，确保组件加载时设置正确的激活状态
  );

  // 在组件挂载时处理可能的重定向
  onMounted(() => {
    // 当直接访问 /admin/personal-center (父路由) 时，重定向到默认子页面 /admin/personal-center/info
    // 确保初次访问时内容被加载
    if (route.path === '/admin/personal-center' || route.path === '/admin/personal-center/') {
      router.replace('/admin/personal-center/info');
    }
    activeSubMenu.value = route.path; // 设置激活菜单，确保即使是重定向后也能正确激活
  });
</script>

<style scoped lang="less">
  /* 复用 UserPersonalCenter.vue 的样式 */
  .personal-center-layout {
    /* 根据您的实际布局，减去头部导航的高度 (假设头部高度为 60px) */
    min-height: calc(100vh - 60px);
    width: 100%; /* 确保占据其父容器的全部宽度 */
    background-color: #f0f2f5; /* 与 AdminHome 的主内容区域背景色一致 */
    padding: 20px; /* 页面内边距 */
    box-sizing: border-box; /* 内边距和边框包含在元素的总宽度和高度内 */
    display: flex; /* 使用 Flexbox 布局，用于水平居中 main-container */
    justify-content: center; /* 水平居中其子元素 (main-container) */
    align-items: flex-start; /* 垂直顶部对齐，防止内容从中间开始 */
    overflow: auto; /* 如果内容超出此容器，允许滚动 */
  }

  .main-container {
    width: 100%; /* 占据其父容器 (personal-center-layout) 内容区的全部宽度 */
    max-width: 1200px; /* 限制内容最大宽度 */
    background-color: #fff; /* 容器背景色 */
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    min-height: 600px; /* 最小高度 */
  }

  .sidebar-menu {
    padding-top: 20px; /* 菜单顶部留白 */
    padding-right: 10px; /* 菜单右侧留白 */
    border-right: 1px solid #eee; /* 菜单右侧分隔线 */
    box-sizing: border-box; /* 确保 padding 和 border 包含在宽度内 */
    flex-shrink: 0; /* **关键：防止侧边栏收缩，保持其 200px 的宽度** */
  }

  .personal-center-nav {
    border-right: none; /* 移除 Element Plus 默认的右边框 */
    width: 100%; /* 确保菜单占据侧边栏的全部宽度 */
    height: 100%; /* 确保菜单占据侧边栏的全部高度 */

    .el-menu-item {
      height: 50px;
      line-height: 50px;
      font-size: 16px;
      color: #333;
      border-radius: 6px;
      margin-bottom: 5px;
      transition: all 0.3s ease;

      &:hover {
        background-color: #f0f8ff;
        color: var(--el-color-primary);
      }

      &.is-active {
        background-color: var(--el-color-primary-light-9);
        color: var(--el-color-primary) !important;
        font-weight: bold;
      }

      .el-icon {
        margin-right: 8px;
      }
    }
  }

  .content-area {
    padding: 20px; /* 内容区域内边距 */
    min-height: 600px; /* 确保内容区域有足够的最小高度 */
    flex: 1; /* **关键：让内容区域填充剩余空间** */
    box-sizing: border-box; /* 确保 padding 和 border 包含在宽度内 */
    overflow-x: auto; /* **关键：如果内容溢出，添加水平滚动条** */
    overflow-y: auto; /* 如果内容垂直溢出，添加垂直滚动条 */
    /* max-width: calc(100% - 200px); 如果设置了 main-container max-width，这里可以不设置 */
  }
</style>
