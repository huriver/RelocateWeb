// D:\Java\code\RelocateWeb\frontground\src\api\driverApi.js

import axios from "@/utils/request.js";

// 后台分页查询司机列表接口
// params 应该包含: page, pageSize, username, name, phone, isBanned, createTimeStart, createTimeEnd,
//           minDrivingYears, maxDrivingYears, minAverageRating, maxAverageRating, minRatingCount, maxRatingCount
export const getDriverListPageApi = (params) => {
  return axios.get("/back/driver/page", { params });
};

// 后台封禁/解封司机接口
// id: 司机ID
// status: 0表示解封, 1表示封禁
export const updateDriverStatusApi = (id, status) => {
  // 按照后端接口设计是 POST /back/driver/status/{status}?id={id}
  return axios.post(`/back/driver/status/${status}`, null, {
    params: { id }, // 将 id 作为查询参数传递
  });
};

/**
 * 【后台】根据姓名模糊查询司机列表 (用于远程搜索)
 * GET /back/driver/list?name={name}
 * @param {object} params - 查询参数，包含 name (司机姓名关键词)
 * @returns {Promise} axios promise
 */
export const getBackDriverListByNameApi = (name) => {
  const params = {};
  // 仅当name非空时才添加到params，排除 null, undefined, ''
  if (name) { // if (name !== null && name !== undefined && name !== '') {
    params.name = name;
  }
  // 如果name为空字符串，params将是空对象 {}，Axios会忽略
  return axios.get(`/back/driver/list`, { params: params });
};

// TODO: 如果后台司机管理还需要其他接口，例如删除等，需要在此处添加
// export const deleteDriverApi = (id) => axios.delete(`/back/driver/${id}`);


/**
 * 【后台司机】获取我的车型与车辆信息
 * GET /back/driver/type-vehicles
 * @returns {Promise} axios promise
 */
export const getDriverTypeAndVehiclesApi = () => {
  return axios.get("/back/driver/type-vehicles");
};

// === 新增：获取司机详情 API ===
/**
 * 【后台司机】根据ID查询司机详情
 * GET /back/driver/{id}
 * @param {number} id - 司机ID
 * @returns {Promise} axios promise
 */
export const getBackDriverDetailApi = (id) => {
  return axios.get(`/back/driver/${id}`);
};

// === 新增：修改司机信息 API ===
/**
 * 【后台司机】修改司机信息
 * PUT /back/driver
 * @param {object} data - 要更新的司机信息数据，包含 id
 * @returns {Promise} axios promise
 */
export const updateBackDriverInfoApi = (data) => {
  // 后端接口是 PUT /back/driver，请求体是司机信息的JSON对象
  // data 应该包含 id 和其他要修改的字段
  return axios.put("/back/driver", data);
};

/**
 * 【后台司机】修改当前登录司机密码
 * PUT /back/driver/editPassword
 * @param {object} data - 密码修改数据 { oldPassword, newPassword, rePassword }
 * @returns {Promise} axios promise
 */
export const changeBackDriverPasswordApi = (data) => {
  return axios.put("/back/driver/editPassword", data);
};
