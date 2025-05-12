// D:\Java\code\RelocateWeb\frontground\src\api\serviceCategoriesApi.js
import axios from "@/utils/request.js"; // 明确命名为 axios

/**
 * 获取所有服务类型列表
 * @returns {Promise} axios promise
 */
export const getServiceCategoriesApi = () => { // 使用 export const 和箭头函数
  return axios.get(`/front/service-category`); // 直接使用 axios.get()
};