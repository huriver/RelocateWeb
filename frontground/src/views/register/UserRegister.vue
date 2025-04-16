<script setup>
import { onMounted, ref } from 'vue'
import router from '../../router'
import { myStore } from '@/stores/store.js'


const loginForm = ref({})
const loginFormRef = ref(null)
const rules = ref({
  employeeNumber: { required: true, message: '请输入用户名', trigger: 'blur' },
  password: { required: true, message: '请输入密码', trigger: 'blur' },
  confirmPassword: { required: true, message: '请确认密码', trigger: 'blur' }
})

const store = myStore()

const login = (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid, fields) => {
    if (valid) {
      // 表单校验成功
      loginForm.value.clientId = store.clientId
      // const { data: res } = await loginApi(loginForm.value)
      // if (!res.success)
      //   return ElMessage.error('登录失败')
      // // 保存token信息
      // store.saveToken(res.data.tokenResponse)
      // // 保存用户信息
      // store.saveUserInfo(res.data.user)
      // loginForm.value = {}
      // router.push('/home/main')
    } else {
      // 表单校验失败
      console.log('error submit!', fields)
    }
  })
}
</script>

<template>
  <div class="container">
    <el-card style="width: 350px">
      <!-- 头部 -->
      <template #header>
        <div class="card-header">
          搬家系统注册
        </div>
      </template>
      <!-- 主体内容 -->
      <el-form ref="loginFormRef" :model="loginForm" status-icon :rules="rules" label-width="auto">
        <el-form-item label="用户名" prop="employeeNumber">
          <el-input v-model="loginForm.employeeNumber" placeholder="请输入用户名" autocomplete="off"
            @keypress.enter="login(loginFormRef)" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" autocomplete="off" show-password
            @keypress.enter="login(loginFormRef)" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="loginForm.confirmPassword" type="password" placeholder="请确认密码" autocomplete="off"
            show-password @keypress.enter="login(loginFormRef)" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login(loginFormRef)">
            确定
          </el-button>
        </el-form-item>
      </el-form>
      <!-- 底部 -->
      <template #footer>
        <router-link to="/userLogin">有账号？去登录</router-link>
      </template>
    </el-card>
  </div>
</template>

<style lang='less' scoped>
.container {
  position: relative;
  width: 100%;
  height: 100vh;
  background-image: url(../../assets/img/bg.jpg);

  .el-card {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);

    .card-header {
      height: 26px;
      line-height: 26px;
      text-align: center;
      color: #409eff;
      font-size: 24px;
    }
  }
}
</style>
