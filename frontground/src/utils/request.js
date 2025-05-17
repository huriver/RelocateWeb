// D:\Java\code\RelocateWeb\frontground\src\utils\request.js

import axios from "axios";
import { ElMessage, ElLoading } from "element-plus";
import { nextTick } from "vue";
import router from "../router";
import { myStore } from "@/stores/store.js"; // 导入 Pinia Store
import { publicApiPaths } from '@/router/index.js'; // 导入公共 API 路径列表

const request = axios.create({
  baseURL: "http://localhost:8080",
  // timeout: 10000
});

// ======================== 加载动画管理逻辑 ========================
let loadingInstance = null;
let requestCount = 0;

function showLoading () {
  if (requestCount === 0 && !loadingInstance) {
    loadingInstance = ElLoading.service({
      lock: true,
      text: "加载中...",
      background: "rgba(0, 0, 0, 0.7)",
    });
  }
  requestCount++;
}

function hideLoading () {
  requestCount--;
  if (requestCount <= 0) {
    requestCount = 0;
    if (loadingInstance) {
      nextTick(() => {
        loadingInstance.close();
        loadingInstance = null;
      });
    }
  }
}

// 请求拦截器
request.interceptors.request.use(
  function (config) {
    // 加载动画逻辑
    // 您可以根据需要调整哪些请求不显示加载动画
    // 注意：对于 /auth/register 接口，无论是否添加 token，只要发起了请求，加载动画都会显示/隐藏
    if (
      !config.url.includes("/front/service") && // 示例：不显示加载动画的接口
      !config.url.includes("/front/rating") && // 示例：不显示加载动画的接口
      !config.url.includes("/auth/back/logout") && // 后台注销请求不显示加载动画
      !config.url.includes("/auth/front/logout") // 前台注销也不显示加载动画
    ) {
      showLoading();
    } else {
      // 如果不显示加载动画，也需要计数，以便 hideLoading 判断
      requestCount++;
    }


    const store = myStore();
    const backToken = store.backUserInfo ? store.backUserInfo.token : null; // 获取后台 token
    const frontToken = store.frontUserInfo ? store.frontUserInfo.token : null; // 获取前台 token

    // 判断是否是公共 API (只根据路径判断)
    // 检查 config.url 是否精确匹配 publicApiPaths 中的某个路径
    const isGeneralPublicRequest = publicApiPaths.some(path => config.url === path);


    // === 核心修改点：针对 /auth/register 接口，根据 role 字段判断是否需要 token ===
    if (config.url === '/auth/register' && config.method.toLowerCase() === 'post') {
      // 这是用户注册接口，检查请求体中的 role 字段
      const requestPayload = config.data;
      if (typeof requestPayload === 'object' && requestPayload !== null && 'role' in requestPayload) {
        const registrationRole = requestPayload.role;
        // 如果注册的是 admin, driver, mover，添加后台 token
        if (['admin', 'driver', 'mover'].includes(registrationRole)) {
          if (backToken) {
            config.headers["token"] = backToken; // 添加后台 Token
            console.log(`Request Interceptor: Adding backend token for /auth/register (role: ${registrationRole})`);
          } else {
            console.warn(`Request Interceptor: Attempted to register role ${registrationRole} via /auth/register without backend token.`);
            // 如果注册这些角色需要 token 但没有，后端应该返回 401，我们依赖响应拦截器处理
          }
        } else if (registrationRole === 'consumer') {
          // 注册消费者，此接口此时视为公共接口，不添加 token
          console.log(`Request Interceptor: Public /auth/register for role: ${registrationRole}. No token added.`);
        } else {
          // role 字段值不是预期的四种，可能是前端错误或后端需要处理的异常情况
          console.warn(`Request Interceptor: /auth/register payload has unexpected role value: ${registrationRole}. No token added based on role rules.`);
          // 默认不加 token，让后端处理错误
        }
      } else {
        // /auth/register POST 请求，但请求体结构 unexpected (没有 role 字段或不是对象)
        console.warn(`Request Interceptor: /auth/register POST request has unexpected data structure. No token added.`);
        // 数据结构不对，不加 token，让后端返回错误
      }
      // 针对 /auth/register 的处理已经完成，返回 config
      return config;
    }
    // === /auth/register 接口处理结束 ===


    // === 处理除了 /auth/register 以外的其他接口 ===
    // 需要后台 Token 的请求：以 /back/ 开头的接口，后台注销接口 (不包含 /auth/register)
    const needsBackendToken = config.url.startsWith('/back/') || config.url === '/auth/back/logout';

    // 需要前台 Token 的请求：以 /front/ 开头的接口，以及前台注销接口
    const needsFrontendToken = config.url.startsWith('/front/') || config.url === '/auth/front/logout';


    // 如果不是通用公共请求，则根据需要添加后台或前台 Token
    if (!isGeneralPublicRequest) {
      if (needsBackendToken) {
        if (backToken) {
          config.headers["token"] = backToken;
          console.log(`Request Interceptor: Adding backend token for backend API: ${config.url}`);
        } else {
          console.warn('Request Interceptor: Attempted to access backend API', config.url, 'without backend token.');
          // 依赖响应拦截器处理 401
        }
      } else if (needsFrontendToken) {
        if (frontToken) {
          config.headers["authentication"] = frontToken;
          console.log(`Request Interceptor: Adding frontend token for frontend API: ${config.url}`);
        } else {
          console.warn('Request Interceptor: Attempted to access frontend API', config.url, 'without frontend token.');
          // 依赖响应拦截器处理 401
        }
      } else {
        // 请求既不是公共请求，也不匹配已知的后台或前台API前缀，也不是 /auth/register
        // 这可能是需要认证但未明确配置的接口，或路径错误。不添加 token。
        console.warn(`Request Interceptor: Request to ${config.url} is not marked as public, doesn't match known API prefixes, and isn't /auth/register. No token added based on rules.`);
      }
    } else {
      // 请求被标记为通用公共请求 (根据路径判断)，不添加任何 Token
      console.log(`Request Interceptor: General Public API, no token added: ${config.url}`);
    }


    // 确保在所有 Token 处理逻辑之后返回 config
    return config;
  },
  function (error) {
    // 请求发送前的错误处理
    hideLoading();
    ElMessage.error("请求发送失败");
    return Promise.reject(error);
  }
);

// 响应拦截器 (保持不变)
request.interceptors.response.use(
  function (response) {
    // 在业务成功和失败时都要隐藏加载动画
    hideLoading();

    // 业务错误处理
    if (response.data && response.data.code !== 1) {
      // 假设 code === 1 表示业务成功
      if (response.data.msg === "登录过期" || response.data.code === 401) {
        // 后端返回的业务 code 是 401 或特定 msg 的情况
        ElMessage.error(response.data.msg || "登录过期，请重新登录");
        const store = myStore();
        store.clearAllSessions(); // 清除所有本地会话信息

        // 根据当前路由判断跳转到哪个登录页
        if (router.currentRoute.value.path.startsWith('/admin')) { // 根据路径前缀判断
          router.push('/admin/login');
        } else {
          router.push('/login');
        }

        return Promise.reject(new Error(response.data.msg || "登录过期"));
      } else {
        // 其他业务错误：只弹出错误信息，不拒绝 Promise
        ElMessage.error(response.data.msg || "发生业务错误");
        // 直接返回响应，让调用方在 .then 中处理 code
        return response; // <-- 修改点：这里不再 reject
      }
    }

    return response; // 业务成功时返回响应
  },
  function (error) {
    // HTTP 错误处理
    hideLoading();

    if (error.response) {
      if (error.response.status === 401) {
        // HTTP 状态码 401 (未授权)
        ElMessage.error("未授权或登录过期，请重新登录");
        const store = myStore();
        store.clearAllSessions(); // 清除所有本地会话信息

        // 根据当前路由判断跳转到哪个登录页
        // 判断当前路由是否是后台路由
        if (router.currentRoute.value.path.startsWith('/admin')) {
          router.push('/admin/login');
        } else {
          router.push('/login');
        }

        return Promise.reject(error);
      } else if (error.response.status === 403) {
        // HTTP 状态码 403 (禁止访问)
        ElMessage.error("您没有权限访问此资源");

        // 如果是后台路由且没有权限，可以跳转到后台主页或其他页面
        if (router.currentRoute.value.path.startsWith('/admin')) {
          // 可以考虑跳转到后台首页或者一个无权限提示页
          // router.push('/admin');
        } else {
          // 前台没有权限的情况
        }
        return Promise.reject(error);
      }

      // 其他 HTTP 错误
      ElMessage.error(`请求错误: ${error.response.status} - ${error.response.statusText || error.message}`);

    } else {
      // 网络错误等
      ElMessage.error("网络错误或服务器无响应");
    }

    return Promise.reject(error);
  }
);

export default request;