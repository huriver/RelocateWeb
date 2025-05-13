// D:\Java\code\RelocateWeb\frontground\src\utils\request.js
import axios from "axios";
import { ElMessage, ElLoading } from "element-plus"; // 明确导入 ElLoading
import { nextTick } from "vue";
import router from "../router";
import { myStore } from "@/stores/store.js"; // 导入 Pinia Store

import { publicApiPaths } from '@/router/index.js';

const request = axios.create({
  baseURL: "http://localhost:8080", // 保持您代码中的 baseURL
  // timeout: 10000 // 原始代码中注释掉，保持不变
});

// ======================== 新增/修改的加载动画管理逻辑 ========================
let loadingInstance = null; // 将 'loading' 重命名为 'loadingInstance'，更清晰
let requestCount = 0;       // 新增：记录当前正在进行的请求数量

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
    const store = myStore();

    // ======================== 修改点：使用导入的 publicApiPaths 进行判断 ========================
    // 检查当前请求的 URL（相对于 baseURL）是否在公共 API 路径列表中
    // 使用 some() 和 includes() 来判断 config.url 是否包含公共路径之一
    const isPublicRequest = publicApiPaths.some(path => config.url.includes(path));

    // 如果不是公共请求 (即需要认证的请求)，才尝试获取并添加 token
    if (!isPublicRequest) {
      // 确保 store.userInfo 存在且不为 null 再访问 token 属性，避免 null 错误
      const token = store.userInfo ? store.userInfo.token : '';
      if (token) {
        config.headers["authentication"] = token;
      } else {
        // 如果是非公共请求但 store 中没有 token，说明用户未登录或登录信息丢失
        // 在这里可以添加处理逻辑，例如重定向到登录页
        console.warn('尝试访问需要认证的接口', config.url, '但用户未登录或token丢失');
        router.push('/login'); // 重定向到用户登录页
        // throw new Error('用户未登录，无法访问需要认证的接口'); // 或者抛出错误中断请求
        // 默认情况下，如果不添加 authentication 头部，后端会处理未授权请求
      }
    }


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
      // 在这里获取 store 实例，并清除数据
      const store = myStore();
      store.clear(); // 清除 Pinia Store 和 localStorage 中的数据

      // 确保 getClientId 函数可用，否则可能导致错误
      if (typeof getClientId === 'function') {
        getClientId(); // 调用获取客户端 ID 的函数
      } else {
        console.warn('getClientId() 函数未定义或不可用，已跳过调用。');
      }

      setTimeout(() => {
        router.push("/login"); // 跳转到用户登录页
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
      // 在这里获取 store 实例，并清除数据
      const store = myStore();
      store.clear(); // 清除 Pinia Store 和 localStorage 中的数据
      router.push("/userLogin"); // 跳转到用户登录页
      return Promise.reject(error); // 拒绝 Promise，中断后续操作
    }

    // 其他请求失败情况
    ElMessage.error("请求失败");
    return Promise.reject(error); // 拒绝 Promise，中断后续操作
  }
);

export default request;