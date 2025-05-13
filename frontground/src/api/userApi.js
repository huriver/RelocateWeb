// D:\Java\code\RelocateWeb\frontground\src\api\userApi.js
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

// 根据ID查询消费者信息接口
export const getUserInfoApi = () => {
  // 注意：根据提供的API文档，查询是 GET /front/customer/
  return axios.get("/front/customer");
};

// 上传头像接口
export const uploadAvatarApi = (formData) => {
  // 注意：上传文件通常需要设置 Content-Type 为 multipart/form-data
  // Axios 会自动处理 FormData 的 header，但有时需要显式设置或检查拦截器
  return axios.post("/front/upload", formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
      // 可能还需要携带其他认证信息，取决于您的全局 axios 配置或拦截器
    }
  });
};

// 编辑消费者信息接口
export const updateUserInfoApi = (data) => {
  return axios.put("/front/customer", data);
};

// 修改密码接口
export const changePasswordApi = (data) => {
  // data 应该包含 oldPassword, newPassword, rePassword
  return axios.put("/front/customer/editPassword", data);
};