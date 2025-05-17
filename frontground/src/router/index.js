// D:\Java\code\RelocateWeb\frontground\src\router\index.js

import { createRouter, createWebHistory } from "vue-router";
import { ElMessage } from 'element-plus';
import { myStore } from '@/stores/store.js'; // 导入 Store

// 注意：组件使用懒加载方式导入，不需要在这里进行 import


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL), // 使用 import.meta.env.BASE_URL
  routes: [
    // 首页重定向 (保持不变)
    {
      path: '/',
      redirect: '/userHome/front',
    },
    // 前台用户端路由 (保持不变)
    {
      path: '/userHome',
      name: 'userHome',
      redirect: '/userHome/front',
      component: () => import('@/views/user/UserHome.vue'), // 懒加载
      children: [
        { path: 'front', name: 'userFront', component: () => import('@/views/user/UserFront.vue'), meta: { title: '首页' } }, // 懒加载
        {
          path: 'news',
          name: 'userNews',
          component: () => import('@/views/user/UserNews.vue'), // 懒加载
          meta: { title: '搬家新闻', requiresAuth: true },
        },
        { path: 'news/:id', name: 'userNewsDetail', component: () => import('@/views/user/UserNewsDetail.vue'), meta: { title: '新闻详情' } }, // 懒加载
        {
          path: 'notice',
          name: 'userNotice',
          component: () => import('@/views/user/UserNotice.vue'), // 懒加载
          meta: { title: '搬家须知', requiresAuth: true },
        },
        {
          path: 'notice/:id',
          name: 'userNoticeDetail',
          component: () => import('@/views/user/UserNoticeDetail.vue'), // 懒加载
          meta: { title: '须知详情' }
        },
        {
          path: 'services',
          name: 'userServices',
          component: () => import('@/views/user/UserServices.vue'), // 懒加载
          meta: { title: '搬家服务', requiresAuth: true },
        },
        {
          path: 'services/:id/comments',
          name: 'userServiceComments',
          component: () => import('@/views/user/UserServiceComments.vue'), // 懒加载
          meta: { title: '服务评论', requiresAuth: true },
        },
        {
          path: 'personal-center',
          name: 'userPersonalCenter',
          component: () => import('@/views/user/UserPersonalCenter.vue'), // 懒加载
          meta: { title: '个人中心', requiresAuth: true },
          redirect: '/userHome/personal-center/orders',
          children: [
            { path: 'orders', name: 'userMyOrders', component: () => import('@/views/user/personalCenter/UserMyOrders.vue'), meta: { title: '我的订单', requiresAuth: true } }, // 懒加载
            { path: 'ratings', name: 'userMyRatings', component: () => import('@/views/user/personalCenter/UserMyRatings.vue'), meta: { title: '我的评价', requiresAuth: true } }, // 懒加载
            { path: 'info', name: 'userPersonalInfo', component: () => import('@/views/user/personalCenter/UserPersonalInfo.vue'), meta: { title: '个人信息', requiresAuth: true } }, // 懒加载
            { path: 'password', name: 'userChangePassword', component: () => import('@/views/user/personalCenter/UserChangePassword.vue'), meta: { title: '修改密码', requiresAuth: true } }, // 懒加载
          ],
        },
      ],
    },
    { path: '/userOrder/:id?', name: 'userOrder', component: () => import('@/views/user/UserOrder.vue'), meta: { title: '订单详情', requiresAuth: true } }, // 懒加载
    { path: '/userOrder/:orderId/rate', name: 'userOrderRating', component: () => import('@/views/user/UserOrderRating.vue'), meta: { title: '评价订单', requiresAuth: true } }, // 懒加载


    // 后台管理端路由
    {
      path: '/admin',
      name: 'adminHome',
      component: () => import('@/views/admin/AdminHome.vue'), // 懒加载 AdminHome 组件中有 <router-view>
      meta: { requiresAuth: true, roles: ['admin', 'driver', 'mover'], isBackend: true }, // 需要认证，指定角色，标记为后台路由
      children: [
        // === 默认子路由，处理访问 /admin 时的情况 ===
        {
          path: '', // <-- 空路径，匹配 /admin
          name: 'adminDashboard', // <-- 添加 name 解决警告
          // 根据登录用户的角色重定向到各自的默认首页
          redirect: (to) => {
            const store = myStore();
            const role = store.backUserInfo?.role;
            // Admin 默认首页改为核心概览
            if (role === 'admin') return '/admin/data-report/core-overview';
            if (role === 'driver') return '/admin/driver/orders/pending'; // 司机默认首页
            if (role === 'mover') return '/admin/mover/orders/pending'; // 搬家工人默认首页
            return '/admin/login'; // 如果没有角色信息，重定向到登录页
          },
          meta: { title: '后台首页' } // 为这个默认路由设置 title
        },

        // === 新增：数据报表子路由 - 仅 Admin 可见 ===
        {
          path: 'data-report',
          name: 'adminDataReport', // 数据报表根路由名称
          redirect: '/admin/data-report/core-overview', // 默认重定向到核心概览
          meta: { title: '数据报表', requiresAuth: true, roles: ['admin'], isBackend: true }, // 仅 Admin 可见
          children: [
            {
              path: 'core-overview', // 完整路径 /admin/data-report/core-overview
              name: 'adminDataReportCoreOverview',
              component: () => import('@/views/admin/dataReport/DataReportCoreOverview.vue'),
              meta: { title: '核心概览', requiresAuth: true, roles: ['admin'], isBackend: true },
            },
            {
              path: 'trend-analysis', // 完整路径 /admin/data-report/trend-analysis
              name: 'adminDataReportTrendAnalysis',
              component: () => import('@/views/admin/dataReport/DataReportTrendAnalysis.vue'),
              meta: { title: '趋势分析', requiresAuth: true, roles: ['admin'], isBackend: true },
            },
            {
              path: 'distribution-analysis', // 完整路径 /admin/data-report/distribution-analysis
              name: 'adminDataReportDistributionAnalysis',
              component: () => import('@/views/admin/dataReport/DataReportDistributionAnalysis.vue'),
              meta: { title: '分布分析', requiresAuth: true, roles: ['admin'], isBackend: true },
            },
            {
              path: 'service-quality', // 完整路径 /admin/data-report/service-quality (保持不变)
              name: 'adminDataReportServiceQuality',
              component: () => import('@/views/admin/dataReport/DataReportServiceQuality.vue'),
              meta: { title: '服务质量排名', requiresAuth: true, roles: ['admin'], isBackend: true },
            },
          ],
        },


        // 用户管理子路由 (保持不变) - 仅 admin 可见
        {
          path: 'user',
          name: 'adminUserManagement',
          redirect: '/admin/user/consumer',
          meta: { title: '用户管理', requiresAuth: true, roles: ['admin'] },
          children: [
            { path: 'consumer', name: 'adminConsumerList', component: () => import('@/views/admin/user/AdminConsumerList.vue'), meta: { title: '消费者管理', requiresAuth: true, roles: ['admin'] } }, // 懒加载
            { path: 'driver', name: 'adminDriverList', component: () => import('@/views/admin/user/AdminDriverList.vue'), meta: { title: '司机管理', requiresAuth: true, roles: ['admin'] } }, // 懒加载
            { path: 'mover', name: 'adminMoverList', component: () => import('@/views/admin/user/AdminMoverList.vue'), meta: { title: '搬家工人管理', requiresAuth: true, roles: ['admin'] } }, // 懒加载
            { path: 'admin', name: 'adminAdminList', component: () => import('@/views/admin/user/AdminAdminList.vue'), meta: { title: '管理员管理', requiresAuth: true, roles: ['admin'] } }, // 懒加载
          ],
        },
        // 订单管理子路由 (保持不变) - 仅 admin 可见
        {
          path: 'order',
          name: 'adminOrderManagement',
          redirect: '/admin/order/list',
          meta: { title: '订单管理', requiresAuth: true, roles: ['admin'] },
          children: [
            {
              path: 'list',
              name: 'adminOrderList',
              component: () => import('@/views/admin/order/AdminOrderList.vue'), // 懒加载
              meta: { title: '所有订单', requiresAuth: true, roles: ['admin'] },
            },
            {
              path: 'ratings',
              name: 'adminOrderRatings',
              component: () => import('@/views/admin/order/AdminOrderRatings.vue'), // 懒加载
              meta: { title: '订单评价', requiresAuth: true, roles: ['admin'] },
            },
          ]
        },
        // 内容管理子路由 (保持不变) - 仅 admin 可见
        {
          path: 'content',
          name: 'adminContentManagement',
          redirect: '/admin/content/news',
          meta: { title: '内容管理', requiresAuth: true, roles: ['admin'] },
          children: [
            { path: 'news', name: 'adminNewsList', component: () => import('@/views/admin/content/AdminNewsList.vue'), meta: { title: '搬家新闻管理', requiresAuth: true, roles: ['admin'] } }, // 懒加载
            { path: 'notice', name: 'adminNoticeList', component: () => import('@/views/admin/content/AdminNoticeList.vue'), meta: { title: '搬家须知管理', requiresAuth: true, roles: ['admin'] } }, // 懒加载
          ],
        },
        // 服务资源管理子路由 (保持不变) - 仅 admin 可见
        {
          path: 'service-resource',
          name: 'adminServiceResourceManagement',
          redirect: '/admin/service-resource/service-type',
          meta: { title: '服务资源管理', requiresAuth: true, roles: ['admin'] },
          children: [
            {
              path: 'service-type',
              name: 'adminServiceTypeConfig',
              component: () => import('@/views/admin/service-resource/AdminServiceTypeConfig.vue'),
              meta: { title: '服务类型配置', requiresAuth: true, roles: ['admin'] }
            },
            {
              path: 'service-item',
              name: 'adminServiceItemSetting',
              component: () => import('@/views/admin/service-resource/AdminServiceItemSetting.vue'),
              meta: { title: '服务项设置', requiresAuth: true, roles: ['admin'] }
            },
            {
              path: 'vehicle-type',
              name: 'adminVehicleTypeConfig',
              component: () => import('@/views/admin/service-resource/AdminVehicleTypeConfig.vue'),
              meta: { title: '车辆类型配置', requiresAuth: true, roles: ['admin'] }
            },
            {
              path: 'vehicle',
              name: 'adminVehicleManagement',
              component: () => import('@/views/admin/service-resource/AdminVehicleManagement.vue'),
              meta: { title: '车辆管理', requiresAuth: true, roles: ['admin'] }
            },
            {
              path: 'driver-vehicle-type-assoc',
              name: 'adminDriverVehicleTypeAssocManagement',
              component: () => import('@/views/admin/service-resource/AdminDriverVehicleTypeAssocManagement.vue'),
              meta: { title: '司机车型关联管理', requiresAuth: true, roles: ['admin'] }
            },
          ],
        },
        // 评价管理子路由 (保持不变) - 仅 admin 可见
        {
          path: 'rating',
          name: 'adminRatingManagement',
          redirect: '/admin/rating/list',
          meta: { title: '评价管理', requiresAuth: true, roles: ['admin'] },
          children: [
            { path: 'list', name: 'adminRatingList', component: () => import('@/views/admin/order/AdminOrderRatings.vue'), meta: { title: '所有评价', requiresAuth: true, roles: ['admin'] } }, // 懒加载，指向订单评价组件
          ],
        },

        // === 后台个人中心路由 (对所有后台角色可见) ===
        {
          path: 'personal-center', // 父级路径，相对于 /admin
          name: 'adminPersonalCenter', // 父级路由名称
          component: () => import('@/views/admin/personalCenter/AdminPersonalCenter.vue'), // 懒加载，个人中心布局组件 (如果需要独立的布局)
          // 个人中心对所有后台角色可见
          meta: { title: '个人中心', requiresAuth: true, roles: ['admin', 'driver', 'mover'], isBackend: true },
          redirect: '/admin/personal-center/info', // 默认重定向到个人信息
          children: [
            {
              path: 'info', // 子级路径，相对于 /admin/personal-center
              name: 'adminPersonalInfo', // 子级路由名称
              component: () => import('@/views/admin/personalCenter/AdminPersonalInfo.vue'), // 懒加载，个人信息组件
              // 个人信息对所有后台角色可见
              meta: { title: '个人信息', requiresAuth: true, roles: ['admin', 'driver', 'mover'], isBackend: true }
            },
            {
              path: 'password', // 子级路径，相对于 /admin/personal-center
              name: 'adminChangePassword', // 子级路由名称
              component: () => import('@/views/admin/personalCenter/AdminChangePassword.vue'), // 懒加载，修改密码组件
              // 修改密码对所有后台角色可见
              meta: { title: '修改密码', requiresAuth: true, roles: ['admin', 'driver', 'mover'], isBackend: true }
            }
          ]
        },

        // === 司机 (Driver) 的子路由 ===
        {
          path: 'driver',
          name: 'driverRoot', // 司机根路由名称
          redirect: '/admin/driver/orders/pending', // 司机登录后的默认跳转到待接订单
          meta: { requiresAuth: true, roles: ['driver'], isBackend: true }, // 仅司机可见
          children: [
            // 订单管理子菜单 (分组)
            {
              path: 'orders', // 路径前缀 /admin/driver/orders
              name: 'driverOrderManagement', // 司机订单管理菜单组名称
              redirect: '/admin/driver/orders/pending', // 默认重定向到待接订单
              meta: { title: '订单管理', requiresAuth: true, roles: ['driver'], isBackend: true }, // 菜单组的元信息
              children: [
                {
                  path: 'pending', // 完整路径 /admin/driver/orders/pending
                  name: 'driverPendingOrders',
                  component: () => import('@/views/admin/driver/orders/DriverPendingOrders.vue'),
                  meta: { title: '待接订单', requiresAuth: true, roles: ['driver'], isBackend: true },
                },
                {
                  path: 'my', // 完整路径 /admin/driver/orders/my
                  name: 'driverMyOrders',
                  component: () => import('@/views/admin/driver/orders/DriverMyOrders.vue'),
                  meta: { title: '我的订单', requiresAuth: true, roles: ['driver'], isBackend: true },
                },
                {
                  path: 'history', // 完整路径 /admin/driver/orders/history
                  name: 'driverHistoryOrders',
                  component: () => import('@/views/admin/driver/orders/DriverHistoryOrders.vue'),
                  meta: { title: '历史订单', requiresAuth: true, roles: ['driver'], isBackend: true },
                },
              ]
            },
            // 我的车辆 (单个菜单项)
            {
              path: 'vehicles', // 完整路径 /admin/driver/vehicles
              name: 'driverMyVehicles',
              component: () => import('@/views/admin/driver/DriverMyVehicles.vue'),
              meta: { title: '我的车辆', requiresAuth: true, roles: ['driver'], isBackend: true },
            },
            // 我的评价 (单个菜单项)
            {
              path: 'ratings', // 完整路径 /admin/driver/ratings
              name: 'driverMyRatings',
              component: () => import('@/views/admin/driver/DriverMyRatings.vue'),
              meta: { title: '我的评价', requiresAuth: true, roles: ['driver'], isBackend: true },
            },
            // TODO: 搬家工人是否有其他特定菜单？例如工作安排等
          ]
        },

        // === 搬家工人 (Mover) 的子路由 ===
        {
          path: 'mover', // 搬家工人根路由名称
          name: 'moverRoot',
          redirect: '/admin/mover/orders/pending', // 搬家工人默认首页
          meta: { requiresAuth: true, roles: ['mover'], isBackend: true }, // 仅搬家工人可见
          children: [
            // 订单管理子菜单 (分组)
            {
              path: 'orders', // 路径前缀 /admin/mover/orders
              name: 'moverOrderManagement', // 搬家工人订单管理菜单组名称
              redirect: '/admin/mover/orders/pending', // 默认重定向到待接订单
              meta: { title: '订单管理', requiresAuth: true, roles: ['mover'], isBackend: true }, // 菜单组的元信息
              children: [
                {
                  path: 'pending', // 完整路径 /admin/mover/orders/pending
                  name: 'moverPendingOrders',
                  component: () => import('@/views/admin/mover/orders/MoverPendingOrders.vue'),
                  meta: { title: '待接订单', requiresAuth: true, roles: ['mover'], isBackend: true },
                },
                {
                  path: 'my', // 完整路径 /admin/mover/orders/my
                  name: 'moverMyOrders',
                  component: () => import('@/views/admin/mover/orders/MoverMyOrders.vue'),
                  meta: { title: '我的订单', requiresAuth: true, roles: ['mover'], isBackend: true },
                },
                {
                  path: 'history', // 完整路径 /admin/mover/orders/history
                  name: 'moverHistoryOrders',
                  component: () => import('@/views/admin/mover/orders/MoverHistoryOrders.vue'),
                  meta: { title: '历史订单', requiresAuth: true, roles: ['mover'], isBackend: true },
                },
              ]
            },
            // 我的评价 (单个菜单项)
            {
              path: 'ratings', // 完整路径 /admin/mover/ratings
              name: 'moverMyRatings',
              component: () => import('@/views/admin/mover/MoverMyRatings.vue'),
              meta: { title: '我的评价', requiresAuth: true, roles: ['mover'], isBackend: true },
            },
            // TODO: 搬家工人是否有其他特定菜单？例如工作安排等
          ]
        },


      ],
    },
    // ... 其他路由定义 ...

    // 认证相关路由 (保持不变)
    { path: '/login', name: 'userLogin', component: () => import('@/views/auth/UserLogin.vue'), meta: { title: '用户登录' } }, // 懒加载
    { path: '/register', name: 'userRegister', component: () => import('@/views/auth/UserRegister.vue'), meta: { title: '用户注册' } }, // 懒加载
    { path: '/admin/login', name: 'adminLogin', component: () => import('@/views/auth/AdminLogin.vue'), meta: { title: '管理端登录', isBackend: true } }, // 懒加载, 保留 isBackend 标记以便判断登录页类型

    // 404 页面 (保持不变)
    { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('@/views/NotFound.vue'), meta: { title: '页面未找到' } }, // 懒加载
  ],
});

// 定义那些不需要在请求头中携带认证 token 的后端 API 接口路径前缀或精确路径 (保持不变)
export const publicApiPaths = [
  '/auth/login', // 用户登录接口
  '/auth/register', // 用户注册接口 (根据 request.js 逻辑，用于注册后端角色的需要后台 token)
  '/public/moving-tips/', // 搬家须知接口 (匹配前缀)
  '/public/moving-news/', // 搬家新闻接口 (匹配前缀)
  // 您可能需要根据实际后端接口，在此处添加管理端公开接口（如果有的话），例如：
  // '/admin/public/some-api',
];

// 路由守卫 (保持不变)
router.beforeEach((to, from, next) => {
  // 设置页面标题
  // 确保 to.meta 是一个对象，避免访问 undefined 的属性
  document.title = to.meta && to.meta.title ? `${to.meta.title} - 搬家服务平台` : '搬家服务平台';

  const store = myStore();
  const requiresAuth = to.meta && to.meta.requiresAuth; // 确保 meta 存在
  const isBackendRoute = to.meta && to.meta.isBackend; // 确保 meta 存在
  const requiredRoles = to.meta && to.meta.roles; // 确保 meta 存在

  // 获取当前用户（根据路由类型）的登录状态和用户信息
  const currentUserInfo = isBackendRoute ? store.backUserInfo : store.frontUserInfo;
  const isLoggedIn = !!currentUserInfo && !!currentUserInfo.token; // 检查用户对象和 token 是否存在

  // ====== 处理需要认证的路由 ======
  if (requiresAuth) {
    if (!isLoggedIn) {
      // 如果需要认证但用户未登录
      ElMessage.warning('请先登录以访问此页面');

      // 保存用户尝试访问的路径，登录成功后可以跳转回去 (可选功能)
      // 根据路由类型保存到对应的路径状态
      if (isBackendRoute) {
        store.saveBackRoutePath(to.fullPath);
        next('/admin/login'); // 重定向到后台登录页
      } else {
        store.saveFrontRoutePath(to.fullPath);
        next('/login'); // 重定向到用户登录页
      }
    } else {
      // 如果需要认证且用户已登录，检查角色权限 (仅后台路由需要检查角色)
      if (isBackendRoute && requiredRoles) {
        // 获取当前登录的后台用户的角色
        const userRole = currentUserInfo.role;
        // 检查用户角色是否在路由要求的角色列表中
        if (!userRole || !requiredRoles.includes(userRole)) {
          // 如果用户没有所需角色
          ElMessage.error('您没有权限访问此页面');
          // 重定向到后台主页或其他无权限提示页
          // 这里的重定向逻辑需要注意，如果用户是 driver/mover 登录后尝试访问只有 admin 角色的页面，应该重定向到他们自己的首页
          if (userRole === 'driver') {
            next('/admin/driver/orders/pending');
          } else if (userRole === 'mover') {
            next('/admin/mover/orders/pending'); // 假设mover首页也是待接订单
          }
          else { // 如果是 admin 尝试访问不存在或无权限的页面 (或其他未处理角色)
            next('/admin'); // 重定向到默认的后台首页
          }

        } else {
          // 角色匹配，允许导航
          next();
        }
      } else {
        // 前台需要认证的路由，且用户已登录 (无需角色检查)，允许导航
        next();
      }
    }
  } else {
    // 不需要认证的路由，直接允许导航
    next();
  }
});

export default router;