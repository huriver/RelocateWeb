// D:\Java\code\RelocateWeb\frontground\src\utils\request.js
import axios from "axios";
import { ElMessage, ElLoading } from "element-plus"; // 明确导入 ElLoading
import { nextTick } from "vue";
import router from "../router";
import { myStore } from "@/stores/store.js";

const store = myStore();

const request = axios.create({
  baseURL: "http://localhost:8080",
  // timeout: 10000 // 原始代码中注释掉，保持不变
});

// ======================== 新增/修改的加载动画管理逻辑 ========================
let loadingInstance = null; // 将 'loading' 重命名为 'loadingInstance'，更清晰
let requestCount = 0;       // 新增：记录当前正在进行的请求数量

// 您代码中使用的 getClientId() 函数未在此文件内定义或导入。
// 假设它是一个全局函数或从其他地方导入。
// 如果它确实不存在，此处的调用将导致错误。请确保此函数可用。
// 如果您不确定，可以暂时注释掉其调用或提供其定义。
// 例如：
// function getClientId() {
//   // ... 您的 getClientId 实现 ...
// }
// =========================================================================

// 请求拦截器
request.interceptors.request.use(
  function (config) {
    requestCount++; // 每次发出请求，请求数量加1

    // 仅当 URL 不包含 /front/service 或 /front/rating 时才显示加载动画
    // 并且只有当当前没有激活的 loadingInstance 时才创建它
    if (
      !config.url.includes("/front/service") &&
      !config.url.includes("/front/rating")
    ) {
      if (!loadingInstance) { // 如果 loadingInstance 为 null (表示没有激活的加载动画)
        loadingInstance = ElLoading.service({
          lock: true,
          text: "加载中...",
          background: "rgba(0, 0, 0, 0.7)",
        });
      }
    }

    // 获取token并设置请求头
    const userInfo = localStorage.getItem("userInfo");
    const token = userInfo ? JSON.parse(userInfo).token : "";
    if (token) config.headers["authentication"] = token;

    return config;
  },
  function (error) {
    requestCount--; // 请求失败时，请求数量减1
    // 如果所有请求都已完成，并且存在加载实例，则关闭它
    if (requestCount === 0 && loadingInstance) {
      nextTick(() => {
        loadingInstance.close();
        loadingInstance = null; // 关闭后将实例置为 null
      });
    }
    // 弹出错误信息
    ElMessage.error("请求失败");
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  function (response) {
    requestCount--; // 响应成功时，请求数量减1
    // 如果所有请求都已完成，并且存在加载实例，则关闭它
    if (requestCount === 0 && loadingInstance) {
      nextTick(() => {
        loadingInstance.close();
        loadingInstance = null; // 关闭后将实例置为 null
      });
    }

    // 业务错误处理：登录过期
    if (
      response.data.errorMessage &&
      response.data.errorMessage["业务错误"] === "登录过期"
    ) {
      ElMessage.error("登录过期");
      localStorage.clear();
      // 确保 getClientId 函数可用，否则可能导致错误
      if (typeof getClientId === 'function') {
        getClientId(); // 调用获取客户端 ID 的函数
      } else {
        console.warn('getClientId() 函数未定义或不可用，已跳过调用。');
      }

      setTimeout(() => {
        router.push("/login");
      }, 2000);
      // 登录过期属于业务错误，通常不应继续 Promise.resolve()，而应该中断后续操作
      return Promise.reject(new Error("登录过期"));
    }
    return response;
  },
  function (error) {
    requestCount--; // 响应失败 (例如 HTTP 错误) 时，请求数量减1
    // 如果所有请求都已完成，并且存在加载实例，则关闭它
    if (requestCount === 0 && loadingInstance) {
      nextTick(() => {
        loadingInstance.close();
        loadingInstance = null; // 关闭后将实例置为 null
      });
    }

    // 处理 HTTP 401 状态码 (未授权)
    // 务必检查 error.response 是否存在，因为有些网络错误可能没有 response 对象
    if (error.response && error.response.status === 401) {
      ElMessage.error("登录过期，请重新登录");
      router.push("/userLogin"); // 跳转到用户登录页
      return Promise.reject(error); // 拒绝 Promise，中断后续操作
    }

    // 其他请求失败情况
    ElMessage.error("请求失败");
    return Promise.reject(error); // 拒绝 Promise，中断后续操作
  }
);

export default request;