<script setup>
  import { ref } from 'vue';
  import router from '@/router';
  import { myStore } from '@/stores/store.js';
  import { userLoginApi } from '@/api/userApi.js'; // 假设 userLoginApi 映射到后台登录接口
  import { ElMessage } from 'element-plus';

  const loginForm = ref({
    role: 'admin', // 默认选择管理员，可以根据实际情况调整
  });
  const loginFormRef = ref(null);
  const rules = ref({
    username: { required: true, message: '请输入用户名', trigger: 'blur' },
    password: { required: true, message: '请输入密码', trigger: 'blur' },
    role: { required: true, message: '请选择角色', trigger: 'change' }, // 角色的 trigger 改为 'change' 更合适
  });

  const store = myStore(); // 获取 store 实例

  const login = (formEl) => {
    if (!formEl) return;
    formEl.validate(async (valid, fields) => {
      if (valid) {
        // 表单校验成功

        // 调用登录接口，发送整个 loginForm.value，包含 username, password, role
        try {
          const { data: res } = await userLoginApi(loginForm.value);

          // 检查后端返回的业务码
          if (res.code !== 1) {
            // 根据后端返回的 code 判断登录是否成功
            // request.js 已经在响应拦截器中统一处理了 res.msg 的弹窗提示，
            // 所以这里不需要再次弹窗 ElMessage.error(res.msg);
            // 但需要 return 中断后续的成功流程
            console.warn('登录业务失败:', res.msg);
            return; // 中断后续流程
          }

          // 登录成功 (res.code === 1)
          ElMessage.success('登录成功');

          // 调用 saveBackUserInfo 方法保存后台用户信息
          store.saveBackUserInfo({
            ...res.data, // 包含 token 等信息
            role: loginForm.value.role, // 保存登录的角色
          });

          // === 检查并使用 backRoutePath 进行重定向 ===
          const redirectPath = store.backRoutePath; // 从 store 中获取之前保存的路径
          if (redirectPath) {
            // 如果存在保存的路径，跳转到该路径
            console.log(`登录成功，重定向到之前访问的页面: ${redirectPath}`);
            router.push(redirectPath);
            // 重定向后清除保存的路径
            store.clearBackRoutePath();
          } else {
            // 如果没有保存的路径 (用户直接访问登录页登录)，跳转到默认后台主页
            console.log('登录成功，没有保存的跳转路径，重定向到默认后台主页');
            router.push('/admin'); // 假设后台主页是 /admin
          }
          // ==============================================

          // 清空表单
          loginForm.value = {
            username: '', // 清空用户名
            password: '', // 清空密码
            role: 'admin', // 角色可以保留默认或清空，根据需要决定
          };
          // 重置表单校验状态
          if (loginFormRef.value) {
            loginFormRef.value.resetFields();
          }


        } catch (error) {
          // API 请求失败 (网络错误或 HTTP 状态码错误，如 401/403)
          // request.js 的响应拦截器会处理这些错误并弹窗、重定向，
          // 所以这里通常不需要再次弹 ElMessage.error，只需记录错误
          console.error('登录请求或HTTP错误:', error);
        }
      } else {
        // 表单校验失败
        console.log('表单校验失败!', fields);
        // Element Plus 默认会在输入框下方显示错误信息，无需额外提示
      }
    });
  };

// TODO: 如果需要前台登录入口，可以在这里添加对应的按钮或链接
// 注意：此组件是 AdminLogin.vue，通常只用于后台登录

// 组件销毁前可以考虑清除一些状态，尽管对于登录页不一定必须
// import { onUnmounted } from 'vue';
// onUnmounted(() => {
//     // 例如，清除 backRoutePath，如果逻辑需要
//     // store.clearBackRoutePath();
// });

</script>

<template>
  <div class="container">
    <el-card style="width: 380px">
      <template #header>
        <div class="card-header">易搬家 - 后台登录</div>
      </template>
      <el-form ref="loginFormRef" :model="loginForm" status-icon :rules="rules" label-width="auto">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" autocomplete="off"
                    @keypress.enter="login(loginFormRef)" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"
                    autocomplete="off" show-password @keypress.enter="login(loginFormRef)" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="loginForm.role">
            <el-radio value="driver">司机</el-radio>
            <el-radio value="mover">工人</el-radio>
            <el-radio value="admin">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item style="margin-bottom: 0">
          <el-button type="primary" @click="login(loginFormRef)"
                     style="width: 100%; height: 45px; font-size: 16px">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style lang="less" scoped>
  .container {
    position: relative;
    width: 100%;
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;

    background-image: url(../../assets/img/bg.jpg);
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    overflow: hidden;

    &::before {
      content: "";
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.4);
      z-index: 0;
    }

    .el-card {
      position: relative;
      z-index: 1;

      width: 380px;
      padding: 30px;
      border-radius: 12px;
      box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
      background-color: #fff;

      :deep(.el-card__header) {
        border-bottom: none;
        padding-bottom: 0;
      }

      .card-header {
        font-size: 28px;
        font-weight: bold;
        text-align: center;
        color: #333;
        margin-bottom: 30px;
      }

      .el-form-item {
        margin-bottom: 22px;
      }

      .card-footer-link {
        text-align: center;
        padding-top: 20px;
        border-top: 1px solid #eee;

        .router-link-active {
          color: #409eff;
          text-decoration: none;
          font-size: 14px;
          &:hover {
            text-decoration: underline;
          }
        }
      }
    }
  }
</style>