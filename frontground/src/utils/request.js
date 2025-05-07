import axios from "axios";
import { ElMessage } from "element-plus";
import { nextTick } from "vue";
import router from "../router";
import { myStore } from "@/stores/store.js";

const store = myStore();

const request = axios.create({
  baseURL: "http://localhost:8080",
  // timeout: 10000
});

// 请求拦截器
let loading = null;
request.interceptors.request.use(
  function (config) {
    if (
      !config.url.includes("/front/service") &&
      !config.url.includes("/front/rating")
    ) {
      loading = ElLoading.service({
        lock: true,
        text: "加载中...",
        background: "rgba(0, 0, 0, 0.7)",
      });
    }
    // 获取token
    const { token } = localStorage.getItem("userInfo")
      ? JSON.parse(localStorage.getItem("userInfo"))
      : "";
    if (token) config.headers["authentication"] = token;
    return config;
  },
  function (error) {
    nextTick(() => {
      // 关闭请求动画
      loading.close();
    });
    // 弹出错误信息
    ElMessage.error("请求失败");
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  function (response) {
    // 关闭请求动画
    nextTick(() => {
      loading.close();
    });
    if (
      response.data.errorMessage &&
      response.data.errorMessage["业务错误"] === "登录过期"
    ) {
      ElMessage.error("登录过期");
      localStorage.clear();
      getClientId();
      setTimeout(() => {
        router.push("/login");
      }, 2000);
    }
    return response;
  },
  function (error) {
    // 关闭请求动画
    nextTick(() => {
      loading.close();
    });
    console.log(error.response.status == 401);
    if (error.response.status == 401) {
      ElMessage.error("登录过期，请重新登录");
      router.push("/userLogin");
      return;
    }
    ElMessage.error("请求失败");
    return Promise.reject(error);
  }
);

export default request;
