import axios from "@/utils/request.js";

export const getOrderPriceApi = (form) => {
  return axios.post(`/front/order/estimate`, form);
};
