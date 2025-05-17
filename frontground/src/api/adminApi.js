// D:\Java\code\RelocateWeb\frontground\src\api\adminApi.js

import axios from "@/utils/request.js";

// 后台分页查询管理员列表接口 (保持不变)
// params 应该包含: page, pageSize, username, name, isBanned
export const getAdminListPageApi = (params) => {
  return axios.get("/back/admin/page", { params });
};

// 后台封禁/解封管理员接口 (保持不变)
// id: 管理员ID
// status: 0表示解封, 1表示封禁
export const updateAdminStatusApi = (id, status) => {
  return axios.post(`/back/admin/status/${status}`, null, {
    params: { id }
  });
};

// 后台重置管理员密码接口 (保持不变)
// id: 管理员ID
export const resetAdminPasswordApi = (id) => {
  return axios.put(`/back/admin/passwordReset/${id}`);
};


// ========================= 后台管理员个人中心相关 API =========================

/**
 * 后台根据ID查询管理员详情 (用于获取当前登录管理员信息)
 * GET http://localhost:8080/back/admin/{id}
 * @param {number} id - 管理员ID
 * @returns {Promise} axios promise
 */
export const getBackAdminDetailApi = (id) => {
  return axios.get(`/back/admin/${id}`);
};

/**
 * 后台编辑管理员信息 (用于修改当前登录管理员信息)
 * PUT http://localhost:8080/back/admin
 * data 包含: id, username, name, photoUrl (如果需要)
 * @param {object} data - 管理员信息数据
 * @returns {Promise} axios promise
 */
export const updateBackAdminInfoApi = (data) => {
  return axios.put("/back/admin", data);
};

/**
 * 后台修改当前登录管理员密码
 * PUT http://localhost:8080/back/admin/editPassword
 * data 包含: oldPassword, newPassword, rePassword
 * @param {object} data - 密码修改数据
 * @returns {Promise} axios promise
 */
export const changeBackAdminPasswordApi = (data) => {
  return axios.put("/back/admin/editPassword", data);
};

// ========================= 后台通用文件上传 API =========================

/**
 * 后台文件上传接口 (例如头像、其他图片等)
 * POST http://localhost:8080/back/upload
 * @param {FormData} formData - 包含文件的FormData对象
 * @returns {Promise} axios promise 成功时 data 中包含文件的 URL
 */
export const uploadBackFileApi = (formData) => {
  // 注意这里直接指定了上传路径 /back/upload
  // axios 实例会自动处理 baseURL (例如 http://localhost:8080)
  return axios.post("/back/upload", formData, {
    // 上传文件需要设置正确的 Content-Type
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};