import { createRouter, createWebHistory } from "vue-router";

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
      redirect: "/userHome/front",
      component: () => import("@/views/home/UserHome.vue"),
      children: [
        {
          path: "front",
          name: "front",
          component: () => import("@/components/user/page/Front.vue"),
        },
        {
          path: "news",
          name: "news",
          component: () => import("@/components/user/page/News.vue"),
        },
        {
          path: "news/:id",
          name: "newsDetail",
          component: () => import("@/components/user/page/NewsDetail.vue"),
        },
        {
          path: "notic",
          name: "notic",
          component: () => import("@/components/user/page/Notic.vue"),
        },
        {
          path: "notic/:id",
          name: "noticDetail",
          component: () => import("@/components/user/page/NoticDetail.vue"),
        },
        {
          path: "service",
          name: "service",
          component: () => import("@/components/user/page/Service.vue"),
        },
        {
          path: "my",
          name: "my",
          component: () => import("@/components/user/page/My.vue"),
        },
      ],
    },
    {
      path: "/backHome",
      name: "backHome",
      component: () => import("@/views/home/BackHome.vue"),
    },
    {
      path: "/userLogin",
      name: "userLogin",
      component: () => import("@/views/login/UserLogin.vue"),
    },
    {
      path: "/userRegister",
      name: "userRegister",
      component: () => import("@/views/register/UserRegister.vue"),
    },
  ],
});

router.beforeEach((to, from) => {
  const userInfo = JSON.parse(localStorage.getItem("userInfo"));
  const path = [
    "/userLogin",
    "/userRegister",
    "/userHome/front",
    "/userHome/news",
    "/userHome/notic",
  ];
  if (path.includes(to.path) || (userInfo && userInfo.token)) {
    // 去的地方不需要校验，或者有token
    return true;
  }
  ElMessage.error("请先登录");
  return "/userLogin";
});

export default router;
