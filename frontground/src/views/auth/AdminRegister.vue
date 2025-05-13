<script setup>
import { onMounted, ref } from 'vue'
import router from '@/router'
import { myStore } from '@/stores/store.js'
import { userLoginApi } from '@/api/userApi.js'

const registerForm = ref({})
const registerFormRef = ref(null)
const rules = ref({
  username: { required: true, message: '请输入用户名', trigger: 'blur' },
  password: { required: true, message: '请输入密码', trigger: 'blur' },
  confirmPassword: { required: true, message: '请确认密码', trigger: 'blur' },
  role: { required: true, message: '请选择角色', trigger: 'blur' }
})

const store = myStore()

const register = (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid, fields) => {
    if (valid) {
      // 表单校验成功
      router.push('/backLogin')
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
          搬家系统工作人员注册
        </div>
      </template>
      <!-- 主体内容 -->
      <el-form ref="registerFormRef" :model="registerForm" status-icon :rules="rules" label-width="auto">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" autocomplete="off"
            @keypress.enter="register(registerFormRef)" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" autocomplete="off" show-password
            @keypress.enter="register(registerFormRef)" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" autocomplete="off"
            show-password @keypress.enter="register(registerFormRef)" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="registerForm.role">
            <el-radio value="driver">司机</el-radio>
            <el-radio value="mover">工人</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item style="margin-bottom: 0;">
          <el-button type="primary" style="width: 100%;" @click="register(registerFormRef)">
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <!-- 底部 -->
      <template #footer>
        <router-link to="/backLogin">有账号？去登录</router-link>
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
