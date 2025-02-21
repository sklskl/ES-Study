<template>
  <div class="register-container">
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
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onRegister">注册</el-button>
        <el-button type="text" @click="toLogin">去登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import request from '@/api/request'

export default {
  name: 'Register',
  data() {
    return {
      form: {
        username: '',
        password: '',
        role: 'user'
      }
    }
  },
  methods: {
    onRegister() {
      request.post('/api/register', this.form).then(res => {
        if (res.code === 200) {
          this.$message.success('注册成功，请登录')
          this.$router.push('/login')
        } else {
          this.$message.error('注册失败：' + res.message)
        }
      }).catch(err => {
        console.error(err)
      })
    },
    toLogin() {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.register-container {
  width: 400px;
  margin: 100px auto;
}
</style>
