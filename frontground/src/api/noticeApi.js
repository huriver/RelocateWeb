// D:\Java\code\RelocateWeb\frontground\src\api\noticeApi.js

import axios from "@/utils/request.js";

/**
 * 后台分页查询搬家须知列表
 * GET http://localhost:8080/back/moving-tips/page
 * params 包含: page, pageSize, title, content, category, isPublished, publishDateStart, publishDateEnd
 * @param {object} params - 查询参数
 * @returns {Promise} axios promise
 */
export const getBackNoticeListPageApi = (params) => {
  return axios.get("/back/moving-tips/page", { params });
};

/**
 * 后台根据ID查询搬家须知详情
 * GET http://localhost:8080/back/moving-tips/{id}
 * @param {number} id - 须知ID
 * @returns {Promise} axios promise
 */
export const getBackNoticeDetailApi = (id) => {
  return axios.get(`/back/moving-tips/${id}`);
};

/**
 * 后台新增搬家须知
 * POST http://localhost:8080/back/moving-tips
 * data 包含: title, content, category, publishDate
 * @param {object} data - 须知数据
 * @returns {Promise} axios promise
 */
export const addBackNoticeApi = (data) => {
  return axios.post("/back/moving-tips", data);
};

/**
 * 后台修改搬家须知
 * PUT http://localhost:8080/back/moving-tips
 * data 包含: id, title, content, category, publishDate
 * @param {object} data - 须知数据
 * @returns {Promise} axios promise
 */
export const updateBackNoticeApi = (data) => {
  return axios.put("/back/moving-tips", data);
};

/**
 * 后台根据ID删除搬家须知
 * DELETE http://localhost:8080/back/moving-tips?id={id}
 * @param {number} id - 须知ID
 * @returns {Promise} axios promise
 */
export const deleteBackNoticeApi = (id) => {
  // 注意：DELETE 请求通常通过 params 或 body 发送数据，这里后端 API 设计是 query 参数
  return axios.delete("/back/moving-tips", { params: { id } });
};

/**
 * 后台发布/取消发布搬家须知
 * POST http://localhost:8080/back/moving-tips/status/{status}?id={id}
 * status: 0 (取消发布), 1 (发布)
 * @param {number} id - 须知ID
 * @param {number} status - 状态 (0 或 1)
 * @returns {Promise} axios promise
 */
export const updateBackNoticeStatusApi = (id, status) => {
  // 注意：POST 请求通过 path 和 query 参数发送数据
  return axios.post(`/back/moving-tips/status/${status}`, null, { params: { id } });
};