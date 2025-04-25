import { ref } from "vue";
import { defineStore } from "pinia";

export const myStore = defineStore("myStore", () => {
  const userInfo = ref(JSON.parse(localStorage.getItem("userInfo")) || null);
  const routePath = ref(JSON.parse(localStorage.getItem("routePath")) || null);

  const saveUserInfo = (newUserInfo) => {
    userInfo.value = newUserInfo;
    localStorage.setItem("userInfo", JSON.stringify(newUserInfo));
  };

  const saveRoutePath = (newRoutePath) => {
    routePath.value = newRoutePath;
    localStorage.setItem("routePath", JSON.stringify(newRoutePath));
  };

  const clear = () => {
    userInfo.value = {};
    routePath.value = null;
    localStorage.clear();
  };

  return {
    userInfo,
    routePath,
    saveUserInfo,
    saveRoutePath,
    clear,
  };
});
