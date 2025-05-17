// D:\Java\code\RelocateWeb\frontground\src\api\driverTruckTypeApi.js

import axios from "@/utils/request.js";

/**
 * 【后台】分页查询司机与货车类型关联列表
 * GET /back/driverTruckType/page
 * params 包含: page, pageSize, driverName, truckTypeId
 * @param {object} params - 查询参数 { page, pageSize, driverName, truckTypeId }
 * @returns {Promise} axios promise
 */
export const getBackDriverTruckTypeListPageApi = (params) => {
  // 确保 truckTypeId 是数字或undefined，driverName 是字符串或undefined
  const queryParams = {
    page: params.page,
    pageSize: params.pageSize,
    driverName: params.driverName || undefined, // 如果为空字符串或null，则不传递
    truckTypeId: params.truckTypeId !== null && params.truckTypeId !== undefined ? params.truckTypeId : undefined, // 仅当有有效值时传递
  };
  return axios.get("/back/driverTruckType/page", { params: queryParams });
};

/**
 * 【后台】根据司机ID查询司机与货车类型关联详情
 * GET /back/driverTruckType/{driverId}
 * @param {number} driverId - 司机ID
 * @returns {Promise} axios promise
 */
export const getBackDriverTruckTypeDetailApi = (driverId) => {
  return axios.get(`/back/driverTruckType/${driverId}`);
};

/**
 * 【后台】新增司机与货车类型关联
 * POST /back/driverTruckType
 * data 包含: driverId, truckTypeIds[]
 * @param {object} data - 关联数据 { driverId, truckTypeIds: [id1, id2] }
 * @returns {Promise} axios promise
 */
export const addBackDriverTruckTypeApi = (data) => {
  // 确保 truckTypeIds 是数组且不为空
  if (!data.truckTypeIds || data.truckTypeIds.length === 0) {
    // 可以选择在这里抛出错误或者让后端处理空数组的情况
    console.warn("Attempted to add driver truck type association with empty truckTypeIds array.");
    // 仍然发送请求，让后端判断
    return axios.post("/back/driverTruckType", data);
  }
  return axios.post("/back/driverTruckType", data);
};

/**
 * 【后台】修改司机与货车类型关联
 * PUT /back/driverTruckType
 * data 包含: driverId, truckTypeIds[]
 * @param {object} data - 关联数据 { driverId, truckTypeIds: [id1, id2] }
 * @returns {Promise} axios promise
 */
export const updateBackDriverTruckTypeApi = (data) => {
  // 确保 truckTypeIds 是数组
  if (!Array.isArray(data.truckTypeIds)) {
    console.error("Attempted to update driver truck type association with non-array truckTypeIds.");
    return Promise.reject(new Error(" truckTypeIds 必须是数组")); // 拒绝Promise
  }
  return axios.put("/back/driverTruckType", data);
};


/**
 * 【后台】根据司机ID删除司机与货车类型关联
 * DELETE /back/driverTruckType?driverId={driverId}
 * @param {number} driverId - 司机ID
 * @returns {Promise} axios promise
 */
export const deleteBackDriverTruckTypeApi = (driverId) => {
  // 后端 API 设计是 query 参数 driverId
  return axios.delete("/back/driverTruckType", { params: { driverId } });
};