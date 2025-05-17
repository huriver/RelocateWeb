// D:\Java\code\RelocateWeb\frontground\src\api\serviceCategoryApi.js

import axios from "@/utils/request.js"; // 明确命名为 axios

/**
 * 【前台】获取所有服务类型列表 (可能用于下拉选择等)
 * GET /front/service-category
 * @returns {Promise} axios promise
 */
export const getServiceCategoriesApi = () => { // 使用 export const 和箭头函数
  return axios.get(`/front/service-category`); // 直接使用 axios.get()
};

/**
 * 【后台】获取所有服务类别列表 (可能用于下拉选择等，非分页)
 * GET /back/serviceCategory/list
 * @returns {Promise} axios promise
 */
export const getBackServiceCategoryListApi = () => {
  return axios.get(`/back/serviceCategory/list`);
};

/**
 * 【后台】分页查询服务类型列表
 * GET /back/serviceCategory/page
 * params 包含: page, pageSize, typeName, minPriceMultiplier, maxPriceMultiplier, createTimeStart, createTimeEnd
 * @param {object} params - 查询参数
 * @returns {Promise} axios promise
 */
export const getBackServiceCategoryListPageApi = (params) => {
  return axios.get("/back/serviceCategory/page", { params });
};

/**
 * 【后台】根据ID查询服务类型详情
 * GET /back/serviceCategory/{id}
 * @param {number} id - 服务类型ID
 * @returns {Promise} axios promise
 */
export const getBackServiceCategoryDetailApi = (id) => {
  return axios.get(`/back/serviceCategory/${id}`);
};

/**
 * 【后台】新增服务类型
 * POST /back/serviceCategory
 * data 包含: typeName, description, priceMultiplier
 * @param {object} data - 服务类型数据
 * @returns {Promise} axios promise
 */
export const addBackServiceCategoryApi = (data) => {
  return axios.post("/back/serviceCategory", data);
};

/**
 * 【后台】修改服务类型
 * PUT /back/serviceCategory
 * data 包含: id, typeName, description, priceMultiplier
 * @param {object} data - 服务类型数据 (必须包含 ID)
 * @returns {Promise} axios promise
 */
export const updateBackServiceCategoryApi = (data) => {
  return axios.put("/back/serviceCategory", data);
};

/**
 * 【后台】根据ID删除服务类型
 * DELETE /back/serviceCategory?id={id}
 * @param {number} id - 服务类型ID
 * @returns {Promise} axios promise
 */
export const deleteBackServiceCategoryApi = (id) => {
  // 注意：DELETE 请求通常通过 params 或 body 发送数据，这里后端 API 设计是 query 参数
  return axios.delete("/back/serviceCategory", { params: { id } });
};