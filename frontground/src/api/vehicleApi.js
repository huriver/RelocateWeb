// D:\Java\code\RelocateWeb\frontground\src\api\vehicleApi.js

import axios from "@/utils/request.js"; // 明确命名为 axios

/**
 * 【后台】分页查询车辆列表
 * GET /back/vehicle/page
 * params 包含: page, pageSize, licensePlateNumber, driverName, truckTypeId, vehicleBrand, createTimeStart, createTimeEnd
 * @param {object} params - 查询参数
 * @returns {Promise} axios promise
 */
export const getBackVehicleListPageApi = (params) => {
  return axios.get("/back/vehicle/page", { params });
};

/**
 * 【后台】根据ID查询车辆详情
 * GET /back/vehicle/{id}
 * @param {number} id - 车辆ID
 * @returns {Promise} axios promise
 */
export const getBackVehicleDetailApi = (id) => {
  return axios.get(`/back/vehicle/${id}`);
};

/**
 * 【后台】新增车辆
 * POST /back/vehicle
 * data 包含: driverId, truckTypeId, licensePlateNumber, vehicleBrand
 * @param {object} data - 车辆数据
 * @returns {Promise} axios promise
 */
export const addBackVehicleApi = (data) => {
  return axios.post("/back/vehicle", data);
};

/**
 * 【后台】修改车辆
 * PUT /back/vehicle
 * data 包含: id, driverId, truckTypeId, licensePlateNumber, vehicleBrand
 * @param {object} data - 车辆数据 (必须包含 ID)
 * @returns {Promise} axios promise
 */
export const updateBackVehicleApi = (data) => {
  return axios.put("/back/vehicle", data);
};

/**
 * 【后台】根据ID删除车辆
 * DELETE /back/vehicle?id={id}
 * @param {number} id - 车辆ID
 * @returns {Promise} axios promise
 */
export const deleteBackVehicleApi = (id) => {
  // 注意：DELETE 请求通常通过 params 或 body 发送数据，这里后端 API 设计是 query 参数
  return axios.delete("/back/vehicle", { params: { id } });
};