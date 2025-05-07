import axios from "@/utils/request.js";

export const queryNoticApi = (form) => {
  return axios.get(
    `/public/moving-tips/page?page=${form.page}&pageSize=${form.pageSize}&title=${form.title}&content=${form.content}&category=${form.category}`
  );
};

export const queryNewsApi = (form) => {
  return axios.get(
    `/public/moving-news/page?page=${form.page}&pageSize=${form.pageSize}&title=${form.title}&content=${form.content}&category=${form.category}`
  );
};

export const queryNoticDetailApi = (id) => {
  return axios.get(`/public/moving-tips/${id}`);
};

export const queryNewsDetailApi = (id) => {
  return axios.get(`/public/moving-news/${id}`);
};
