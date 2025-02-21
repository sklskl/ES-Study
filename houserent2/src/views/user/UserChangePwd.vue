<template>
  <div>
    <h3>修改密码</h3>
    <el-form :model="form">
      <el-form-item label="旧密码">
        <el-input type="password" v-model="form.oldPwd" />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input type="password" v-model="form.newPwd" />
      </el-form-item>
      <el-form-item label="确认密码">
        <el-input type="password" v-model="form.confirmPwd" />
      </el-form-item>
    </el-form>
    <el-button type="primary" @click="changePassword">确认修改</el-button>
  </div>
</template>

<script>
import request from '@/api/request'

export default {
  name: 'UserChangePwd',
  data() {
    return {
      form: {
        oldPwd: '',
        newPwd: '',
        confirmPwd: ''
      }
    }
  },
  methods: {
    changePassword() {
      if(!this.form.oldPwd || !this.form.newPwd || !this.form.confirmPwd) {
        return this.$message.error('请填写完整')
      }
      if(this.form.newPwd !== this.form.confirmPwd) {
        return this.$message.error('两次密码输入不一致')
      }
      request.post('/api/user/changePwd', {
        oldPwd: this.form.oldPwd,
        newPwd: this.form.newPwd
      }).then(res => {
        if(res.code === 200) {
          this.$message.success('密码修改成功，请重新登录')
          this.$router.push('/login')
        } else {
          this.$message.error(res.message)
        }
      })
    }
  }
}
</script>
