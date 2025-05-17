// D:\Java\code\RelocateWeb\frontground\src\api\newsApi.js

import axios from "@/utils/request.js";

/**
 * 后台分页查询搬家新闻列表
 * GET http://localhost:8080/back/moving-news/page
 * params 包含: page, pageSize, title, content, isPublished, publishDateStart, publishDateEnd
 * @param {object} params - 查询参数
 * @returns {Promise} axios promise
 */
export const getBackNewsListPageApi = (params) => {
  return axios.get("/back/moving-news/page", { params });
};

/**
 * 后台根据ID查询搬家新闻详情
 * GET http://localhost:8080/back/moving-news/{id}
 * @param {number} id - 新闻ID
 * @returns {Promise} axios promise
 */
export const getBackNewsDetailApi = (id) => {
  return axios.get(`/back/moving-news/${id}`);
};

/**
 * 后台新增搬家新闻
 * POST http://localhost:8080/back/moving-news
 * data 包含: title, content, publishDate
 * @param {object} data - 新闻数据
 * @returns {Promise} axios promise
 */
export const addBackNewsApi = (data) => {
  return axios.post("/back/moving-news", data);
};

/**
 * 后台修改搬家新闻
 * PUT http://localhost:8080/back/moving-news
 * data 包含: id, title, content, publishDate
 * @param {object} data - 新闻数据
 * @returns {Promise} axios promise
 */
export const updateBackNewsApi = (data) => {
  return axios.put("/back/moving-news", data);
};

/**
 * 后台根据ID删除搬家新闻
 * DELETE http://localhost:8080/back/moving-news?id={id}
 * @param {number} id - 新闻ID
 * @returns {Promise} axios promise
 */
export const deleteBackNewsApi = (id) => {
  // 注意：DELETE 请求通常通过 params 或 body 发送数据，这里后端 API 设计是 query 参数
  return axios.delete("/back/moving-news", { params: { id } });
};

/**
 * 后台发布/取消发布搬家新闻
 * POST http://localhost:8080/back/moving-news/status/{status}?id={id}
 * status: 0 (取消发布), 1 (发布)
 * @param {number} id - 新闻ID
 * @param {number} status - 状态 (0 或 1)
 * @returns {Promise} axios promise
 */
export const updateBackNewsStatusApi = (id, status) => {
  // 注意：POST 请求通过 path 和 query 参数发送数据
  return axios.post(`/back/moving-news/status/${status}`, null, { params: { id } });
};