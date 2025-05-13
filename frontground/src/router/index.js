// D:\Java\code\RelocateWeb\frontground\src\router\index.js

import { createRouter, createWebHistory } from "vue-router";
import { ElMessage } from 'element-plus';
import { myStore } from '@/stores/store.js';

// 导入主要视图组件
import UserHome from '@/views/user/UserHome.vue';
import UserPersonalCenter from '@/views/user/UserPersonalCenter.vue';

// 导入个人中心子组件
import UserMyOrders from '@/views/user/personalCenter/UserMyOrders.vue';
import UserMyRatings from '@/views/user/personalCenter/UserMyRatings.vue';
import UserPersonalInfo from '@/views/user/personalCenter/UserPersonalInfo.vue';
import UserChangePassword from '@/views/user/personalCenter/UserChangePassword.vue';


// 导入其他用户端页面组件
import UserFront from '@/views/user/UserFront.vue';
import UserNews from '@/views/user/UserNews.vue';
import UserNewsDetail from '@/views/user/UserNewsDetail.vue';
import UserNotice from '@/views/user/UserNotice.vue';
import UserNoticeDetail from '@/views/user/UserNoticeDetail.vue';
import UserServices from '@/views/user/UserServices.vue';
import UserServiceComments from '@/views/user/UserServiceComments.vue';
import UserOrder from '@/views/user/UserOrder.vue';
import UserOrderRating from '@/views/user/UserOrderRating.vue';

// 导入认证相关组件
import UserLogin from '@/views/auth/UserLogin.vue';
import UserRegister from '@/views/auth/UserRegister.vue';
import AdminLogin from '@/views/auth/AdminLogin.vue';
import AdminRegister from '@/views/auth/AdminRegister.vue';

// 导入管理端主页组件
import AdminHome from '@/views/admin/AdminHome.vue';

// 导入 404 页面
import NotFound from '@/views/NotFound.vue';


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      redirect: "/userHome/front",
    },
    {
      path: "/userHome",
      name: "userHome",
      redirect: "/userHome/front",
      component: UserHome,
      children: [
        {
          path: "front",
          name: "userFront",
          component: UserFront,
          meta: { title: '首页' } // 首页，未登录可访问，不需要 requiresAuth: true
        },
        {
          path: "news",
          name: "userNews",
          component: UserNews,
          meta: { title: '搬家新闻', requiresAuth: true }, // 搬家新闻列表，未登录不可访问，需要 requiresAuth: true
        },
        {
          path: "news/:id",
          name: "userNewsDetail",
          component: UserNewsDetail,
          meta: { title: '新闻详情' }, // 新闻详情，未登录可访问，不需要 requiresAuth: true
        },
        {
          path: "notice",
          name: "userNotice",
          component: UserNotice,
          meta: { title: '搬家须知', requiresAuth: true }, // 搬家须知列表，未登录不可访问，需要 requiresAuth: true
        },
        {
          path: "notice/:id",
          name: "userNoticeDetail",
          component: UserNoticeDetail,
          meta: { title: '须知详情' }, // 须知详情，未登录可访问，不需要 requiresAuth: true
        },
        {
          path: "services",
          name: "userServices",
          component: UserServices,
          meta: { title: '搬家服务', requiresAuth: true }, // 搬家服务列表，未登录不可访问
        },
        {
          path: "services/:id/comments",
          name: "userServiceComments",
          component: UserServiceComments,
          meta: { title: '服务评论', requiresAuth: true }, // 服务评论页，未登录不可访问
        },
        {
          path: 'personal-center',
          name: 'userPersonalCenter',
          component: UserPersonalCenter,
          meta: { title: '个人中心', requiresAuth: true }, // 个人中心，未登录不可访问
          redirect: '/userHome/personal-center/orders',
          children: [
            {
              path: 'orders',
              name: 'userMyOrders',
              component: UserMyOrders,
              meta: { title: '我的订单', requiresAuth: true } // 个人中心子路由，继承父路由的 requiresAuth
            },
            {
              path: 'ratings',
              name: 'userMyRatings',
              component: UserMyRatings,
              meta: { title: '我的评价', requiresAuth: true } // 个人中心子路由
            },
            {
              path: 'info',
              name: 'userPersonalInfo',
              component: UserPersonalInfo,
              meta: { title: '个人信息', requiresAuth: true } // 个人中心子路由
            },
            {
              path: 'password',
              name: 'userChangePassword',
              component: UserChangePassword,
              meta: { title: '修改密码', requiresAuth: true } // 个人中心子路由
            },
          ]
        },
      ],
    },
    {
      path: "/userOrder/:id?",
      name: "userOrder",
      component: UserOrder,
      meta: { title: '订单详情', requiresAuth: true }, // 订单详情页，未登录不可访问
    },
    {
      path: "/userOrder/:orderId/rate",
      name: "userOrderRating",
      component: UserOrderRating,
      meta: { title: '评价订单', requiresAuth: true }, // 评价订单页，未登录不可访问
    },
    {
      path: "/admin",
      name: "adminHome",
      component: AdminHome,
      meta: { requiresAuth: true, roles: ['admin', 'driver', 'mover'] }, // 管理端主页，未登录不可访问
      children: [
        // 管理端路由 (请确保这里的子路由也配置了 requiresAuth: true 或继承父路由的 requiresAuth)
      ]
    },
    {
      path: "/login",
      name: "userLogin",
      component: UserLogin,
      meta: { title: '用户登录' }, // 登录页，未登录可访问
    },
    {
      path: "/register",
      name: "userRegister",
      component: UserRegister,
      meta: { title: '用户注册' }, // 注册页，未登录可访问
    },
    {
      path: "/admin/login",
      name: "adminLogin",
      component: AdminLogin,
      meta: { title: '管理端登录' }, // 管理端登录页，未登录可访问
    },
    {
      path: "/admin/register",
      name: "adminRegister",
      component: AdminRegister,
      meta: { title: '管理端注册' }, // 管理端注册页，未登录可访问
    },
    { path: '/:pathMatch(.*)*', name: 'NotFound', component: NotFound, meta: { title: '页面未找到' } },
  ],
});

// 定义那些不需要在请求头中携带认证 token 的后端 API 接口路径
// 这个列表通常用于 Axios 请求拦截器
// 已经根据您提供的 common.js 和 userApi.js 文件内容进行了更新
export const publicApiPaths = [
  // 认证相关的API
  '/auth/login',         // 用户登录接口
  '/auth/register',      // 用户注册接口
  // 注意：管理端登录/注册也可能需要在这里添加，取决于您的认证逻辑是否共享

  // 公共查询API (搬家须知和新闻)
  '/public/moving-tips/page',      // 查询须知列表接口
  '/public/moving-tips/',          // 查询须知详情接口 (匹配前缀)
  '/public/moving-news/page',      // 查询新闻列表接口
  '/public/moving-news/',          // 查询新闻详情接口 (匹配前缀)

  // 如果管理端也有公共接口，需要在此处添加
  // '/public/admin/some-public-api',
];


// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 搬家服务平台` : '搬家服务平台';

  const store = myStore();
  const requiresAuth = to.meta.requiresAuth; // 获取路由元信息中的 requiresAuth 属性

  // 检查路由是否需要认证且用户未登录
  if (requiresAuth && (!store.userInfo || !store.userInfo.token)) {
    // 如果需要认证但用户未登录，显示提示并重定向到登录页
    ElMessage.warning('请先登录以访问此页面');
    // 保存用户尝试访问的路径，登录成功后可以跳转回去 (可选功能，此处未实现)
    // store.setRedirectPath(to.fullPath);
    next('/login'); // 重定向到用户登录页面
  } else {
    // 否则，允许继续导航
    next();
  }
});

export default router;