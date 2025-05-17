// D:\Java\code\RelocateWeb\frontground\src\api\moverApi.js

import axios from "@/utils/request.js";

// 后台分页查询搬家工人列表接口
// params 应该包含: page, pageSize, username, name, phone, isBanned, createTimeStart, createTimeEnd, minAverageRating, maxAverageRating, minRatingCount, maxRatingCount
export const getMoverListPageApi = (params) => {
  // 您提供的API是 GET http://localhost:8080/back/mover/page
  return axios.get("/back/mover/page", { params });
};

// 后台封禁/解封搬家工人接口
// id: 搬家工人ID
// status: 0表示解封, 1表示封禁
export const updateMoverStatusApi = (id, status) => {
  // 您提供的API是 POST http://localhost:8080/back/mover/status/0?id=1
  // status 作为路径变量，id 作为查询参数
  return axios.post(`/back/mover/status/${status}`, null, {
    params: { id }
  });
};


// === 获取搬家工人详情 API ===
/**
 * 【后台搬家工人】根据ID查询搬家工人详情
 * GET /back/mover/{id}
 * @param {number} id - 搬家工人ID
 * @returns {Promise} axios promise
 */
export const getBackMoverDetailApi = (id) => {
  return axios.get(`/back/mover/${id}`);
};

// === 修改搬家工人信息 API ===
/**
 * 【后台搬家工人】修改搬家工人信息
 * PUT /back/mover
 * @param {object} data - 要更新的搬家工人信息数据，包含 id
 * @returns {Promise} axios promise
 */
export const updateBackMoverInfoApi = (data) => {
  // 后端接口是 PUT /back/mover，请求体是搬家工人信息的JSON对象
  // data 应该包含 id 和其他要修改的字段
  return axios.put("/back/mover", data);
};

// === 新增：修改搬家工人密码 API ===
/**
 * 【后台搬家工人】修改密码
 * PUT /back/mover/editPassword
 * @param {object} passwordData - 包含 oldPassword, newPassword, rePassword
 * @returns {Promise} axios promise
 */
export const changeBackMoverPasswordApi = (passwordData) => {
  // 根据您提供的API信息，是 PUT 请求到 /back/mover/editPassword，请求体包含密码数据
  return axios.put("/back/mover/editPassword", passwordData);
};


// TODO: 如果有其他搬家工人相关的后台 API，请在此处添加