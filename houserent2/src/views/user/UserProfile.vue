<template>
  <div>
    <h3>个人信息</h3>
    <el-form :model="profile">
      <el-form-item label="用户名">
        <el-input v-model="profile.username" disabled></el-input>
      </el-form-item>
      <el-form-item label="电话">
        <el-input v-model="profile.phone"></el-input>
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="profile.email"></el-input>
      </el-form-item>
    </el-form>
    <el-button type="primary" @click="updateProfile">更新信息</el-button>
  </div>
</template>

<script>
import request from '@/api/request'
import { mapState } from 'vuex'

export default {
  name: 'UserProfile',
  data() {
    return {
      profile: {
        username: '',
        phone: '',
        email: ''
      }
    }
  },
  computed: {
    ...mapState({
      token: state => state.user.token,
      username: state => state.user.username
    })
  },
  created() {
    this.getProfile()
  },
  methods: {
    getProfile() {
      request.get('/api/user/profile').then(res => {
        if(res.code === 200) {
          this.profile = res.data
        }
      })
    },
    updateProfile() {
      request.put('/api/user/profile', this.profile).then(res => {
        if(res.code === 200) {
          this.$message.success('更新成功')
        }
      })
    }
  }
}
</script>
