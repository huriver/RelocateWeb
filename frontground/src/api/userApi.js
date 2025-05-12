import axios from "@/utils/request.js";

// 登录接口
export const userLoginApi = (form) => {
  return axios.post("/auth/login", form);
};

// 注册接口,这个 config 参数将直接传递给 axios.post 作为第三个参数，用于设置 headers 等
export const userRegisterApi = (data, config = {}) => {
  return axios.post("/auth/register", data, config);
};

// 用户注销接口
export const userLogoutApi = () => {
  return axios.post("/auth/front/logout");
};