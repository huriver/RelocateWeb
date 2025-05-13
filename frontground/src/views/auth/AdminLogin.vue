<script setup>
import { onMounted, ref } from 'vue'
import router from '@/router'
import { myStore } from '@/stores/store.js'
import { userLoginApi } from '@/api/userApi.js'

const loginForm = ref({})
const loginFormRef = ref(null)
const rules = ref({
  username: { required: true, message: '请输入用户名', trigger: 'blur' },
  password: { required: true, message: '请输入密码', trigger: 'blur' },
  role: { required: true, message: '请选择角色', trigger: 'blur' }
})

const store = myStore()

const login = (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid, fields) => {
    if (valid) {
      // 表单校验成功
      loginForm.value.clientId = store.clientId
      const { data: res } = await userLoginApi(loginForm.value)
      if (res.code !== 1)
        return ElMessage.error(res.msg)
      // 保存用户信息
      store.saveBackInfo({
        ...res.data,
        role: loginForm.value.role
      })
      loginForm.value = {}
      router.push('/backHome')
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
          搬家系统工作人员登录
        </div>
      </template>
      <!-- 主体内容 -->
      <el-form ref="loginFormRef" :model="loginForm" status-icon :rules="rules" label-width="auto">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" autocomplete="off"
            @keypress.enter="login(loginFormRef)" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" autocomplete="off" show-password
            @keypress.enter="login(loginFormRef)" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="loginForm.role">
            <el-radio value="driver">司机</el-radio>
            <el-radio value="mover">工人</el-radio>
            <el-radio value="admin">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item style="margin-bottom: 0;">
          <el-button type="primary" style="width: 100%;" @click="login(loginFormRef)">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <!-- 底部 -->
      <template #footer>
        <router-link to="/backRegister">员工注册</router-link>
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
