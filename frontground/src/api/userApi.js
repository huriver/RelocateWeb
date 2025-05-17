// D:\Java\code\RelocateWeb\frontground\src\api\userApi.js

import axios from "@/utils/request.js";

// 登录接口 (通用，根据 role 参数区分前后台和角色)
export const userLoginApi = (form) => {
  return axios.post("/auth/login", form);
};

// 注册接口 (用户注册)
export const userRegisterApi = (data, config = {}) => {
  return axios.post("/auth/register", data, config);
};

// 前台用户注销接口
export const userLogoutApi = () => {
  return axios.post("/auth/front/logout");
};

// 后台用户注销接口
export const backLogoutApi = () => {
  return axios.post("/auth/back/logout");
};


// 根据ID查询消费者信息接口 (前台用户)
export const getUserInfoApi = () => {
  return axios.get("/front/customer");
};

// 上传头像接口 (前台用户)
export const uploadAvatarApi = (formData) => {
  return axios.post("/front/upload", formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

// 编辑消费者信息接口 (前台用户)
export const updateUserInfoApi = (data) => {
  return axios.put("/front/customer", data);
};

// 修改密码接口 (前台用户)
export const changePasswordApi = (data) => {
  return axios.put("/front/customer/editPassword", data);
};


// ========================= 后台管理端消费者管理 API =========================

// 后台分页查询消费者列表接口
// params 应该包含: page, pageSize, username, name, phone, isBanned, createTimeStart, createTimeEnd
export const getConsumerListPageApi = (params) => {
  return axios.get("/back/customer/page", { params });
};

// 后台封禁/解封消费者接口
// id: 消费者ID
// status: 0表示解封, 1表示封禁
export const updateConsumerStatusApi = (id, status) => {
  // 注意后端接口设计是 POST /back/customer/status/{status}?id={id}
  return axios.post(`/back/customer/status/${status}`, null, {
    params: { id }
  });
};

// TODO: 如果后台用户也有自己的用户信息查询、编辑等接口，需要在这里添加，例如:
// export const getBackUserInfoApi = () => axios.get("/back/user/info");
// export const updateBackUserInfoApi = (data) => axios.put("/back/user/info", data);
// export const changeBackPasswordApi = (data) => axios.put("/back/user/editPassword", data);