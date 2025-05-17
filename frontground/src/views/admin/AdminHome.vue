<script setup>
  import { computed, ref, watch, nextTick } from 'vue'; // 导入 watch 和 nextTick
  import { useRouter, useRoute } from 'vue-router';
  import { myStore } from '@/stores/store.js';
  import { ElMessage, ElMessageBox } from 'element-plus';
  import { ArrowDown, UserFilled } from '@element-plus/icons-vue';
  // 假设后台注销 API 仍然在 userApi.js 中
  import { backLogoutApi } from '@/api/userApi.js';

  const store = myStore();
  const router = useRouter();
  const route = useRoute();

  // 获取当前登录的后台用户角色和信息
  const backUserInfo = computed(() => store.backUserInfo);
  const currentUserRole = computed(() => (backUserInfo.value ? backUserInfo.value.role : null));
  const currentUserName = computed(() =>
    backUserInfo.value ? backUserInfo.value.username : '工作人员'
  );

  // 获取用户的头像 URL (现在可以根据角色从 store 获取 photoUrl)
  const userAvatarUrl = computed(() => {
    // backUserInfo 已经包含 photoUrl 字段 (在 AdminPersonalInfo.vue 中获取并更新)
    return backUserInfo.value ? backUserInfo.value.photoUrl : null;
  });


  const menuItems = computed(() => {
    const role = currentUserRole.value;
    if (!role) return [];

    const menus = {
      admin: [
        // === 数据报表菜单 (新的子菜单) ===
        {
          index: '/admin/data-report',
          title: '数据报表',
          children: [
            { index: '/admin/data-report/core-overview', title: '核心概览' },
            { index: '/admin/data-report/trend-analysis', title: '趋势分析' },
            { index: '/admin/data-report/distribution-analysis', title: '分布分析' },
            { index: '/admin/data-report/service-quality', title: '服务质量排名' },
          ],
        },
        // ========================

        // 用户管理子路由 - 仅 admin 可见
        {
          index: '/admin/user',
          title: '用户管理',
          children: [
            { index: '/admin/user/consumer', title: '消费者管理' },
            { index: '/admin/user/driver', title: '司机管理' },
            { index: '/admin/user/mover', title: '搬家工人管理' },
            { index: '/admin/user/admin', title: '管理员管理' },
          ],
        },
        // 订单管理子路由 - 仅 admin 可见
        {
          index: '/admin/order',
          title: '订单管理',
          children: [
            { index: '/admin/order/list', title: '所有订单' },
            { index: '/admin/order/ratings', title: '订单评价' },
          ],
        },
        // 内容管理子路由 - 仅 admin 可见
        {
          index: '/admin/content',
          title: '内容管理',
          children: [
            { index: '/admin/content/news', title: '搬家新闻管理' },
            { index: '/admin/content/notice', title: '搬家须知管理' },
          ],
        },
        // 服务资源管理子路由 - 仅 admin 可见
        {
          index: '/admin/service-resource',
          title: '服务资源管理',
          children: [
            { index: '/admin/service-resource/service-type', title: '服务类型配置' },
            { index: '/admin/service-resource/service-item', title: '服务项设置' },
            { index: '/admin/service-resource/vehicle-type', title: '车辆类型配置' },
            { index: '/admin/service-resource/driver-vehicle-type-assoc', title: '司机车型关联管理' },
            { index: '/admin/service-resource/vehicle', title: '车辆管理' },
          ],
        },
      ],
      // 司机菜单结构 (保持不变)
      driver: [
        {
          index: '/admin/driver/orders', // 对应路由中的父级路径
          title: '订单管理',
          children: [
            { index: '/admin/driver/orders/pending', title: '待接订单' },
            { index: '/admin/driver/orders/my', title: '我的订单' },
            { index: '/admin/driver/orders/history', title: '历史订单' },
          ],
        },
        {
          index: '/admin/driver/vehicles', // 对应路由路径
          title: '我的车型与车辆',
        },
        {
          index: '/admin/driver/ratings', // 对应路由路径
          title: '我的评价',
        },
      ],
      // 搬家工人菜单结构 (保持不变)
      mover: [
        {
          index: '/admin/mover/orders', // 对应路由中的父级路径
          title: '订单管理',
          children: [
            { index: '/admin/mover/orders/pending', title: '待接订单' },
            { index: '/admin/mover/orders/my', title: '我的订单' },
            { index: '/admin/mover/orders/history', title: '历史订单' },
          ],
        },
        {
          index: '/admin/mover/ratings', // 对应路由路径
          title: '我的评价',
        },
      ],
    };

    // 返回当前角色的菜单
    return menus[role] || [];
  });

  // 计算默认激活的菜单项 - 直接使用当前路由路径
  const defaultActiveMenu = computed(() => {
    // 对于 /admin 路径，根据角色重定向到各自的默认子页面，并激活对应的菜单项
    // 此逻辑已在 router/index.js 中处理，这里只需确保初始激活状态正确
    return route.path;
  });

  // 计算默认展开的菜单项 - 根据当前路由路径找到对应的父级菜单
  const defaultOpenMenus = computed(() => {
    const openKeys = [];
    const currentPath = route.path;

    // 遍历所有顶级菜单项
    for (const item of menuItems.value) {
      // 如果顶级菜单项有子菜单
      if (item.children && item.children.length > 0) {
        // 检查当前路由路径是否是此顶级菜单项的子路径
        // 使用 startsWith 检查当前路径是否以父级菜单 index 开头
        if (currentPath.startsWith(item.index)) {
          openKeys.push(item.index);
          // break; // 如果 unique-opened 是 true，找到第一个匹配的父级就可以停止了
          // 移除了 break，让所有匹配的父级都被加入 openKeys，虽然 unique-opened=true 最终只会展开一个，
          // 但这样逻辑更清晰，且如果 unique-opened=false 则可以展开多个。
        }
      }
    }
    return openKeys;
  });


  // handleCommand 方法保持不变，已正确处理个人中心和注销
  const handleCommand = (command) => {
    if (command === 'personalCenter') {
      // 导航到后台个人中心默认页 (个人信息)
      // 直接导航到个人信息子路由
      router.push('/admin/personal-center/info');
    } else if (command === 'logout') {
      logout();
    }
  };

  const logout = () => {
    ElMessageBox.confirm('确定要注销登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        try {
          // 调用后台注销 API
          const { data: res } = await backLogoutApi();
          if (res.code === 1) {
            ElMessage.success('已成功注销！');
          } else {
            console.warn('后台注销业务失败:', res.msg); // 可以保留日志
          }
        } catch (error) {
          // 捕获真正的请求错误
          console.error('后台注销请求发生错误:', error);
          ElMessage.error('注销请求异常，请稍后再试。'); // <-- 这个用于网络或HTTP错误
        } finally {
          // 无论注销API调用成功、业务失败还是请求失败，最终都执行以下操作
          // 清除本地存储的后台登录状态
          store.clearBackSession();
          // 重定向到后台登录页面
          router.push('/admin/login');
        }
      })
      .catch((action) => {
        // 用户点击取消
        if (action === 'cancel') {
          ElMessage.info('已取消注销。');
        }
      });
  };

  // === 添加滚动到顶部的逻辑 ===
  // 创建一个 ref 来引用 <el-main> 元素
  const mainContentRef = ref(null);

  // 监听路由路径的变化
  watch(
    () => route.path,
    (newPath, oldPath) => {
      // 仅当新路径是数据报表子路由时滚动到顶部
      if (newPath.startsWith('/admin/data-report/')) {
        console.log(`路由切换到 ${newPath}，尝试滚动内容区域到顶部.`);
        // 使用 nextTick 确保 DOM 更新完成后再滚动
        nextTick(() => {
          // **重要：尝试通过 $el 获取实际的 DOM 元素**
          const scrollElement = mainContentRef.value ? mainContentRef.value.$el : null;

          if (scrollElement) {
            // ==== 添加的调试日志 ====
            console.log('滚动目标元素 ($el):', scrollElement); // 打印通过 $el 获取的元素
            console.log('滚动属性 (设置前):', {
              scrollTop: scrollElement.scrollTop,
              scrollHeight: scrollElement.scrollHeight, // 元素内容的实际高度
              clientHeight: scrollElement.clientHeight, // 元素可见区域的高度
              offsetHeight: scrollElement.offsetHeight // 元素的整体高度 (包含 padding/border)
            });
            // =====================

            // 判断是否需要滚动 (内容高度大于可见高度且不在顶部)
            // 检查 scrollHeight 和 clientHeight 是否是有效的数字
            if (scrollElement.scrollHeight !== undefined && scrollElement.clientHeight !== undefined &&
              scrollElement.scrollHeight > scrollElement.clientHeight && scrollElement.scrollTop !== 0) {

              scrollElement.scrollTop = 0;
              console.log('滚动执行: <el-main> (通过 $el) 已设置 scrollTop = 0.');

              // 可选：再次检查滚动后的位置 (可能需要一个微小延时)
              // setTimeout(() => {
              //   console.log('滚动属性 (设置后):', {
              //     scrollTop: scrollElement.scrollTop
              //   });
              // }, 50); // 50ms 延时
            } else {
              console.log('滚动跳过: 内容未溢出、已在顶部或滚动属性无效.', {
                scrollHeight: scrollElement.scrollHeight,
                clientHeight: scrollElement.clientHeight,
                scrollTop: scrollElement.scrollTop
              });
            }


          } else {
            console.warn("滚动目标元素 ($el) 不存在或 ref 为 null，无法滚动.");
          }
        });
      }
    }
  );
// === 滚动到顶部的逻辑结束 ===

</script>

<template>
  <div class="admin-home">
    <el-container class="admin-container">
      <el-aside width="200px" class="admin-aside">
        <div class="site-title">
          <img src="../../assets/img/logo.png" alt="Logo" class="logo" />
          <span class="title-text">易搬家</span>
        </div>
        <el-menu :default-active="defaultActiveMenu" :default-openeds="defaultOpenMenus"
                 class="admin-menu" router unique-opened background-color="#2d3a4b"
                 text-color="#b3c0d1" active-text-color="#409eff" :collapse="false">
          <template v-for="item in menuItems" :key="item.index">
            <el-sub-menu v-if="item.children && item.children.length > 0" :index="item.index">
              <template #title>
                <span>{{ item.title }}</span>
              </template>
              <el-menu-item v-for="child in item.children" :key="child.index" :index="child.index">
                {{ child.title }}
              </el-menu-item>
            </el-sub-menu>
            <el-menu-item v-else :index="item.index">
              <span>{{ item.title }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>

      <el-container class="admin-main-container">
        <el-header class="admin-header">
          <div class="header-left">
            <div class="breadcrumb-placeholder">
            </div>
          </div>
          <div class="header-right">
            <div class="user-info">
              <el-dropdown trigger="click" @command="handleCommand">
                <div class="user-info-container">
                  <el-avatar :size="36" class="user-avatar" :src="userAvatarUrl">
                    <el-icon>
                      <UserFilled />
                    </el-icon>
                  </el-avatar>
                  <div class="user-details">
                    <span class="username">{{ currentUserName }}</span>
                    <span
                          class="user-role">{{ currentUserRole === 'admin' ? '管理员' : currentUserRole === 'driver' ? '司机' : currentUserRole === 'mover' ? '搬家工人' : '未知角色' }}</span>
                  </div>
                  <el-icon class="dropdown-icon"><arrow-down /></el-icon>
                </div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="personalCenter">个人中心</el-dropdown-item>
                    <el-dropdown-item command="logout" class="logout-item">
                      注销登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>

        <el-main class="admin-main" ref="mainContentRef">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style lang="less" scoped>
  /* 样式与之前相同，不再重复 */
  .admin-home {
    height: 100vh;
    overflow: hidden;
  }

  .admin-container {
    height: 100%;
  }

  .admin-aside {
    background-color: #2d3a4b;
    color: #b3c0d1;
    display: flex;
    flex-direction: column;

    .site-title {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0 20px;
      font-size: 20px;
      font-weight: bold;
      color: #fff;
      background-color: #3a475a;
      box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
      z-index: 1;

      .logo {
        height: 54px;
        margin-left: -42px;
        /* 调整logo位置，可能需要根据实际logo调整 */
        margin-right: 20px;
        object-fit: contain;
      }
      .title-text {
        white-space: nowrap;
      }
    }
    .admin-menu {
      flex-grow: 1;
      border-right: none;
      overflow-y: auto;
      overflow-x: hidden;

      &::-webkit-scrollbar {
        width: 6px;
        background-color: transparent;
      }
      &::-webkit-scrollbar-thumb {
        background-color: rgba(255, 255, 255, 0.1);
        border-radius: 3px;
      }
      &::-webkit-scrollbar-thumb:hover {
        background-color: rgba(255, 255, 255, 0.2);
      }

      .el-menu-item,
      :deep(.el-sub-menu__title) {
        /* 使用 :deep() 或 ::v-deep() 影响子组件样式 */
        height: 50px;
        line-height: 50px;
        display: flex;
        align-items: center;
        padding: 0 20px !important;

        &.is-active {
          background-color: #409eff !important;
          // Element Plus 激活背景色
          color: #fff !important;
        }
        &:hover {
          background-color: #3a475a !important;
          // 鼠标悬停背景色
          color: #fff;
        }
      }

      :deep(.el-menu--inline .el-menu-item) {
        /* 使用 :deep() 或 ::v-deep() 影响子组件样式 */
        background-color: #263445 !important;
        padding-left: 40px !important;
        // 子菜单缩进

        &:hover {
          background-color: #3a475a !important;
        }
        &.is-active {
          background-color: #409eff !important;
          color: #fff !important;
        }
      }

      span {
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        margin-left: 0;
        // 确保 span 不产生额外左边距
      }
    }
  }

  .admin-main-container {
    flex-direction: column;
    .admin-header {
      height: 60px;
      line-height: 60px;
      background-color: #fff;
      box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
      padding: 0 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-left {
        flex-grow: 1;
        .breadcrumb-placeholder {
          // 您可以在这里放置面包屑组件
        }
      }

      .header-right {
        .user-info {
          margin-left: auto;

          .user-info-container {
            display: flex;
            align-items: center;
            padding: 8px 12px;
            border-radius: 4px;
            cursor: pointer;
            transition: all 0.3s;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
            &:hover {
              background-color: #f5f7fa;
              .username {
                color: #409eff;
              }
              .dropdown-icon {
                color: #409eff;
              }
            }

            .user-avatar {
              background-color: #409eff;
              color: white;
              font-weight: bold;
              margin-right: 12px;
              display: flex;
              // 确保内部图标/文字居中
              align-items: center;
              justify-content: center;
            }

            .user-details {
              display: flex;
              flex-direction: column;
              margin-right: 12px;

              .username {
                font-size: 14px;
                font-weight: 500;
                color: #606266;
                line-height: 1;
                margin-bottom: 4px;
              }
              .user-role {
                font-size: 12px;
                color: #909399;
                line-height: 1;
              }
            }

            .dropdown-icon {
              color: #c0c4cc;
              font-size: 12px;
              transition: all 0.3s;
            }
          }
          /* 箭头旋转样式 */
          :deep(.el-dropdown__caret-button) {
            /* 使用 :deep() 影响 Element Plus 内部元素 */
            margin-left: 0;
            /* 移除 Element Plus 默认的 caret 边距 */
          }
          .el-dropdown.el-dropdown--visible .dropdown-icon {
            transform: rotate(180deg);
          }
        }
      }
    }
    .admin-main {
      flex-grow: 1;
      padding: 20px;
      background-color: #f0f2f5;
      overflow-y: auto;
      // 允许主内容区域滚动
    }
  }

  .logout-item {
    color: #f56c6c !important;
    &:hover {
      background-color: #fef0f0 !important;
    }
  }
</style>