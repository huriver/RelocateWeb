import axios from "@/utils/request.js";

export const getServiceRatingApi = (id) => {
  return axios.get(`/front/rating/service/${id}`);
};
