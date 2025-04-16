import axios from "@/utils/request.js";

export const userLoginApi = (form) => {
  return axios.post("/front/customer/login", form);
};
