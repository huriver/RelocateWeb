import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      redirect: "/userLogin",
    },
    {
      path: "/userHome",
      name: "userHome",
      component: () => import("@/views/home/UserHome.vue"),
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
  const token = JSON.parse(localStorage.getItem("token"));
  const path = ["/userLogin", "/userRegister"];
  if (path.includes(to.path) || token) {
    // 去的地方不需要校验，或者有token
    return true;
  }
  ElMessage.error("请先登录");
  return "/userLogin";
});

export default router;
