import axios from "@/utils/request.js";

// 查询服务评价
export const getServiceRatingApi = (id) => {
  return axios.get(`/front/rating/service/${id}`);
};

// 评价订单
export const reviewOrderApi = (data) => {
  return axios.post("/front/rating/review", data);
};

// 查询历史评价
export const queryHistoryRatingsApi = () => {
  return axios.get("/front/rating/history");
};