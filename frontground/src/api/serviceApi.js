// D:\Java\code\RelocateWeb\frontground\src\api\serviceApi.js

import axios from "@/utils/request.js";

/**
 * 【前台】分页查询服务项列表
 * GET /front/service/page
 * params 包含: page, pageSize, categoryId (可能还支持其他字段，根据前端用户页面需求来)
 * @param {object} params - 查询参数
 * @returns {Promise} axios promise
 */
export const queryServiceApi = (params) => {
  return axios.get("/front/service/page", { params: params });
};


/**
 * 【前台】根据ID查询服务项详情
 * GET /front/service/serviceDetail/{id}
 * @param {number} id - 服务项ID
 * @returns {Promise} axios promise
 */
export const getServiceDetailApi = (id) => {
  return axios.get(`/front/service/serviceDetail/${id}`);
};


// ==================== 后台服务项 API ====================

/**
 * 【后台】分页查询服务项列表
 * GET /back/service/page
 * params 包含: page, pageSize, serviceName, categoryId, truckTypeId, minAverageRating, maxAverageRating, minRatingCount, maxRatingCount, status, createTimeStart, createTimeEnd
 * @param {object} params - 查询参数
 * @returns {Promise} axios promise
 */
export const getBackServiceListPageApi = (params) => {
  return axios.get("/back/service/page", { params });
};

/**
 * 【后台】根据ID查询服务项详情
 * GET /back/service/{id}
 * @param {number} id - 服务项ID
 * @returns {Promise} axios promise
 */
export const getBackServiceDetailApi = (id) => {
  return axios.get(`/back/service/${id}`);
};

/**
 * 【后台】新增服务项
 * POST /back/service
 * data 包含: categoryId, truckTypeId, serviceName, shortDescription, loadingCapacityDescription, perHelperCost (根据详情接口推测可能有 perHelperCost)
 * @param {object} data - 服务项数据
 * @returns {Promise} axios promise
 */
export const addBackServiceApi = (data) => {
  return axios.post("/back/service", data);
};

/**
 * 【后台】修改服务项
 * PUT /back/service
 * data 包含: id, categoryId, truckTypeId, serviceName, shortDescription, loadingCapacityDescription, perHelperCost (根据详情接口推测可能有 perHelperCost)
 * @param {object} data - 服务项数据 (必须包含 ID)
 * @returns {Promise} axios promise
 */
export const updateBackServiceApi = (data) => {
  return axios.put("/back/service", data);
};

/**
 * 【后台】根据ID删除服务项
 * DELETE /back/service?id={id}
 * @param {number} id - 服务项ID
 * @returns {Promise} axios promise
 */
export const deleteBackServiceApi = (id) => {
  // 注意：DELETE 请求通过 query 参数发送 id
  return axios.delete("/back/service", { params: { id } });
};

/**
 * 【后台】停售/起售服务项
 * PUT /back/service/status/{status}?id={id}
 * status: 0 (停售), 1 (起售)
 * @param {number} id - 服务项ID
 * @param {number} status - 状态 (0 或 1)
 * @returns {Promise} axios promise
 */
export const updateBackServiceStatusApi = (id, status) => {
  // 注意：PUT 请求通过 path 参数发送 status，query 参数发送 id
  // null is used as request body since it's a PUT with path/query params
  return axios.put(`/back/service/status/${status}`, null, { params: { id } });
};