import { ref } from "vue";
import { defineStore } from "pinia";

export const myStore = defineStore("myStore", () => {
  const userInfo = ref(JSON.parse(localStorage.getItem("userInfo")) || null);

  const saveUserInfo = (newUserInfo) => {
    userInfo.value = newUserInfo;
    localStorage.setItem("userInfo", JSON.stringify(newUserInfo));
  };

  const clear = () => {
    clientId.value = null;
    userInfo.value = {};
    forecastData.value = {};
    localStorage.clear();
  };

  return {
    userInfo,
    saveUserInfo,
    clear,
  };
});
