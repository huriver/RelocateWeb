// D:\Java\code\RelocateWeb\frontground\src\stores\store.js
import { ref } from "vue";
import { defineStore } from "pinia";

export const myStore = defineStore("myStore", () => {
  // == 前台用户信息状态 ==
  const frontUserInfo = ref(JSON.parse(localStorage.getItem("frontUserInfo")) || null);
  // == 后台用户信息状态 ==
  const backUserInfo = ref(JSON.parse(localStorage.getItem("backUserInfo")) || null);

  // == 前台路由路径状态 ==
  const frontRoutePath = ref(localStorage.getItem("frontRoutePath") || null); // 新增状态和 key
  // == 后台路由路径状态 ==
  const backRoutePath = ref(localStorage.getItem("backRoutePath") || null); // 新增状态和 key

  // == 保存前台用户信息的方法 ==
  const saveFrontUserInfo = (newUserInfo) => {
    frontUserInfo.value = newUserInfo;
    localStorage.setItem("frontUserInfo", JSON.stringify(newUserInfo));
  };

  // == 保存后台用户信息的方法 ==
  const saveBackUserInfo = (newBackUserInfo) => {
    backUserInfo.value = newBackUserInfo;
    localStorage.setItem("backUserInfo", JSON.stringify(newBackUserInfo));
  };

  // == 保存前台路由路径的方法 ==
  const saveFrontRoutePath = (newRoutePath) => {
    frontRoutePath.value = newRoutePath;
    localStorage.setItem("frontRoutePath", newRoutePath);
  };

  // == 保存后台路由路径的方法 ==
  const saveBackRoutePath = (newRoutePath) => {
    backRoutePath.value = newRoutePath;
    localStorage.setItem("backRoutePath", newRoutePath);
  };

  // == 清除前台消费者会话 ==
  const clearFrontSession = () => {
    frontUserInfo.value = null;
    frontRoutePath.value = null;
    localStorage.removeItem("frontUserInfo");
    localStorage.removeItem("frontRoutePath");
  };

  // == 清除后台工作人员会话 ==
  const clearBackSession = () => {
    backUserInfo.value = null;
    backRoutePath.value = null;
    localStorage.removeItem("backUserInfo");
    localStorage.removeItem("backRoutePath");
  };

  // == 清除所有会话 (可选保留或重命名) ==
  const clearAllSessions = () => {
    frontUserInfo.value = null;
    backUserInfo.value = null;
    frontRoutePath.value = null;
    backRoutePath.value = null;

    localStorage.removeItem("frontUserInfo");
    localStorage.removeItem("backUserInfo");
    localStorage.removeItem("frontRoutePath");
    localStorage.removeItem("backRoutePath");

    // 如果还有其他需要保存在 localStorage 的用户相关状态，这里也要对应清除
  };

  return {
    // 导出状态
    frontUserInfo,
    backUserInfo,
    frontRoutePath,
    backRoutePath,

    // 导出方法
    saveFrontUserInfo,
    saveBackUserInfo,
    saveFrontRoutePath,
    saveBackRoutePath,
    clearFrontSession,
    clearBackSession,
    clearAllSessions,
  };
});