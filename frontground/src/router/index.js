import { createRouter, createWebHistory } from "vue-router";
import { ElMessage } from 'element-plus'; // ① 确保导入 ElMessage 用于提示

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      redirect: "/userHome",
    },
    {
      path: "/userHome",
      name: "userHome",
      redirect: "/userHome/front", // 默认重定向到首页
      component: () => import("@/views/home/UserHome.vue"),
      children: [
        {
          path: "front", // 首页，无需登录，公开访问
          name: "front",
          component: () => import("@/components/user/page/Front.vue"),
          // 无 meta.requiresAuth
        },
        {
          path: "news", // 搬家新闻列表页，需要登录
          name: "news",
          component: () => import("@/components/user/page/News.vue"),
          meta: { requiresAuth: true } // ② **需要登录才能访问**
        },
        {
          path: "news/:id", // 新闻详情页，无需登录（可从首页进入）
          name: "newsDetail",
          component: () => import("@/components/user/page/NewsDetail.vue"),
          // 无 meta.requiresAuth，以满足在未登录下从首页点击详情后可以展示的需求
        },
        {
          path: "notic", // 搬家须知列表页，需要登录
          name: "notic",
          component: () => import("@/components/user/page/Notic.vue"),
          meta: { requiresAuth: true } // ② **需要登录才能访问**
        },
        {
          path: "notic/:id", // 通知详情页，无需登录（可从首页进入）
          name: "noticDetail",
          component: () => import("@/components/user/page/NoticDetail.vue"),
          // 无 meta.requiresAuth
        },
        {
          path: "service", // 服务页面，假设需要登录
          name: "service",
          component: () => import("@/components/user/page/Service.vue"),
          meta: { requiresAuth: true } // **假设服务页面需要登录**
        },
        // *** 新增路由：服务评论列表页 ***
        {
          path: "serviceComments/:id", // 接收服务ID作为参数
          name: "serviceComments",
          component: () => import("@/components/user/page/ServiceComments.vue"), // 指向新的组件
          meta: { requiresAuth: true } // 假设评论页面也需要登录
        },
        {
          path: "my", // 我的页面，假设需要登录
          name: "my",
          component: () => import("@/components/user/page/My.vue"),
          meta: { requiresAuth: true } // **假设我的页面需要登录**
        },
      ],
    },
    {
      path: "/order/:id", // 订单详情页，假设需要登录
      name: "order",
      component: () => import("@/components/user/page/Order.vue"),
      meta: { requiresAuth: true } // **假设订单详情页需要登录**
    },
    {
      path: "/backHome", // 后台管理主页，假设需要登录
      name: "backHome",
      component: () => import("@/views/home/BackHome.vue"),
      meta: { requiresAuth: true } // **假设后台主页需要登录**
    },
    {
      path: "/userLogin", // 用户登录页，无需登录
      name: "userLogin",
      component: () => import("@/views/login/UserLogin.vue"),
    },
    {
      path: "/userRegister", // 用户注册页，无需登录
      name: "userRegister",
      component: () => import("@/views/register/UserRegister.vue"),
    }

  ],
});

// ③ 优化后的 beforeEach 守卫
router.beforeEach((to, from, next) => {
  const userInfo = JSON.parse(localStorage.getItem("userInfo"));
  const isLoggedIn = userInfo && userInfo.token; // 检查用户是否已登录

  // 情况1: 如果目标路由需要认证 (requiresAuth: true) 且用户未登录
  if (to.meta.requiresAuth && !isLoggedIn) {
    ElMessage.error("请先登录才能访问此页面！"); // 给出提示
    // 重定向到登录页，并传递原目标路径作为查询参数，以便登录后可以跳回
    next({ name: 'userLogin' });
  }
  // 情况2: 其他所有情况，允许导航
  else {
    next();
  }
});

export default router;