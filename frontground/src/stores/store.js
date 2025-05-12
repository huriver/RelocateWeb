import { ref } from "vue";
import { defineStore } from "pinia";

export const myStore = defineStore("myStore", () => {
  // 从 localStorage 读取 userInfo，如果不存在则为 null
  // userInfo 应该是一个对象，其中包含 token
  const userInfo = ref(JSON.parse(localStorage.getItem("userInfo")) || null);

  // 从 localStorage 读取 routePath，路径通常是字符串，无需 JSON.parse
  const routePath = ref(localStorage.getItem("routePath") || null);

  // 保存用户信息到 store 和 localStorage
  const saveUserInfo = (newUserInfo) => {
    userInfo.value = newUserInfo;
    localStorage.setItem("userInfo", JSON.stringify(newUserInfo));
  };

  // 保存路由路径到 store 和 localStorage
  const saveRoutePath = (newRoutePath) => {
    routePath.value = newRoutePath;
    localStorage.setItem("routePath", newRoutePath); // 路径直接保存为字符串
  };

  // 清除所有相关数据：userInfo, routePath，并清空 localStorage
  const clear = () => {
    userInfo.value = null; // 清空用户信息，可以设为 null 或一个空对象 {}
    routePath.value = null; // 清空路由路径
    localStorage.clear(); // 清空浏览器本地缓存中的所有数据
  };

  return {
    userInfo,      // 用户信息，包含 token
    routePath,     // 路由路径
    saveUserInfo,  // 保存用户信息的方法
    saveRoutePath, // 保存路由路径的方法
    clear,         // 清除所有数据的方法 (用于注销)
  };
});