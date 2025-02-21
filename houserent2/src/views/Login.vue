<template>
  <div class="login-container">
    <el-form :model="form" ref="form" label-width="80px">
      <el-form-item label="用户名">
        <el-input v-model="form.username"></el-input>
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" show-password></el-input>
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="form.role" placeholder="请选择角色">
          <el-option label="普通用户" value="user"></el-option>
          <el-option label="房主" value="landlord"></el-option>
          <el-option label="管理员" value="admin"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onLogin">登录</el-button>
        <el-button type="text" @click="toRegister">去注册</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {mapActions} from 'vuex'
import {loginApi} from '@/api/api'

export default {
  name: 'Login',
  data() {
    return {
      form: {
        username: '',
        password: '',
        role: '' // user / landlord / admin
      }
    }
  },
  methods: {
    ...mapActions(['login']),
    onLogin() {
      // 发起登录请求
      loginApi(this.form).then(res => {
        // res 中拿到token, role等信息
        if (res.code === 200) {
          // 假设后端返回 { token: 'xxx', role: 'user', username: 'xxx' }
          const userInfo = {
            token: res.data.token,
            role: res.data.role,
            username: res.data.username
          }
          // 存到 vuex
          this.login(userInfo)
          // 跳转到对应角色首页
          if (res.data.role === 'user') {
            this.$router.push('/user/home')
          } else if (res.data.role === 'landlord') {
            this.$router.push('/landlord/home')
          } else if (res.data.role === 'admin') {
            this.$router.push('/admin/home')
          }
        } else {
          this.$message.error('登录失败：' + res.message)
        }
      }).catch(err => {
        console.error(err)
      })
    },
    toRegister() {
      this.$router.push('/register')
    }
  }
}
</script>

<style scoped>
.login-container {
  width: 400px;
  margin: 100px auto;
}
</style>
