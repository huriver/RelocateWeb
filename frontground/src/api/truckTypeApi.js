// D:\Java\code\RelocateWeb\frontground\src\api\truckTypeApi.js

import axios from "@/utils/request.js"; // 明确命名为 axios

/**
 * 【后台】获取所有货车类型列表 (可能用于下拉选择等，非分页)
 * GET /back/truckType/list
 * @returns {Promise} axios promise
 */
export const getBackTruckTypeListApi = () => {
  return axios.get(`/back/truckType/list`);
};

/**
 * 【后台】分页查询货车类型列表
 * GET /back/truckType/page
 * params 包含: page, pageSize, typeName, minBaseFare, maxBaseFare, createTimeStart, createTimeEnd
 * @param {object} params - 查询参数
 * @returns {Promise} axios promise
 */
export const getBackTruckTypeListPageApi = (params) => {
  return axios.get("/back/truckType/page", { params });
};

/**
 * 【后台】根据ID查询货车类型详情
 * GET /back/truckType/{id}
 * @param {number} id - 货车类型ID
 * @returns {Promise} axios promise
 */
export const getBackTruckTypeDetailApi = (id) => {
  return axios.get(`/back/truckType/${id}`);
};

/**
 * 【后台】新增货车类型
 * POST /back/truckType
 * data 包含: typeName, capacity, description, baseFare, pricePerKmTier1-5
 * @param {object} data - 货车类型数据
 * @returns {Promise} axios promise
 */
export const addBackTruckTypeApi = (data) => {
  return axios.post("/back/truckType", data);
};

/**
 * 【后台】修改货车类型
 * PUT /back/truckType
 * data 包含: id, typeName, capacity, description, baseFare, pricePerKmTier1-5
 * @param {object} data - 货车类型数据 (必须包含 ID)
 * @returns {Promise} axios promise
 */
export const updateBackTruckTypeApi = (data) => {
  return axios.put("/back/truckType", data);
};

/**
 * 【后台】根据ID删除货车类型
 * DELETE /back/truckType?id={id}
 * @param {number} id - 货车类型ID
 * @returns {Promise} axios promise
 */
export const deleteBackTruckTypeApi = (id) => {
  // 注意：DELETE 请求通常通过 params 或 body 发送数据，这里后端 API 设计是 query 参数
  return axios.delete("/back/truckType", { params: { id } });
};