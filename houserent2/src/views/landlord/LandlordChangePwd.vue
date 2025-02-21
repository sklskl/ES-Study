<template>
  <div class="landlord-change-pwd">
    <h2>修改密码</h2>
    <el-form :model="form" label-width="80px" style="width: 300px;">
      <el-form-item label="旧密码">
        <el-input type="password" v-model="form.oldPwd"></el-input>
      </el-form-item>
      <el-form-item label="新密码">
        <el-input type="password" v-model="form.newPwd"></el-input>
      </el-form-item>
    </el-form>
    <el-button type="primary" @click="changePwd">确认修改</el-button>
  </div>
</template>

<script>
import request from '@/api/request'

export default {
  name: 'LandlordChangePwd',
  data() {
    return {
      form: {
        oldPwd: '',
        newPwd: ''
      }
    }
  },
  methods: {
    changePwd() {
      request.post('/api/landlord/changePwd', this.form).then(res => {
        if (res.code === 200) {
          this.$message.success('密码修改成功，请重新登录')
          // 也可跳转到登录页
        } else {
          this.$message.error('修改失败：' + res.message)
        }
      })
    }
  }
}
</script>
