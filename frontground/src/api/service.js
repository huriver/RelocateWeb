import axios from "@/utils/request.js";

export const queryServiceApi = (form) => {
  return axios.get(
    `/front/service/page?page=${form.page}&pageSize=${form.pageSize}&categoryId=${form.categoryId}`
  );
};

export const getServiceDetailApi = (id) => {
  return axios.get(`/front/service/serviceDetail/${id}`);
};
