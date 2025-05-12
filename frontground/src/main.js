import "@/assets/css/common.css"; // 您的公共 CSS 文件

import { createApp } from "vue";
import { createPinia } from "pinia";

import App from "./App.vue";
import router from "./router";

// --- Element Plus 相关的导入和配置开始 ---
import ElementPlus from 'element-plus'; // 导入 Element Plus 组件库
import 'element-plus/dist/index.css'; // 导入 Element Plus 的默认样式
import zhCn from 'element-plus/es/locale/lang/zh-cn'; // 导入 Element Plus 的简体中文语言包
// --- Element Plus 相关的导入和配置结束 ---

const app = createApp(App);

app.use(createPinia());

// 注册 Element Plus，并全局配置为中文语言
// 这样，所有 Element Plus 组件（包括分页）都会显示为中文
app.use(ElementPlus, {
  locale: zhCn, // 将语言配置为中文
});

app.use(router);

app.mount("#app");