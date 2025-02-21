<template>
  <div class="landlord-profile">
    <h2>房主 - 个人中心</h2>
    <el-form :model="profile" label-width="80px" style="width: 400px;">
      <el-form-item label="用户名">
        <el-input v-model="profile.username" disabled></el-input>
      </el-form-item>
      <el-form-item label="手机">
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

export default {
  name: 'LandlordProfile',
  data() {
    return {
      profile: {
        username: '',
        phone: '',
        email: ''
      }
    }
  },
  created() {
    this.getProfile()
  },
  methods: {
    getProfile() {
      request.get('/api/landlord/profile').then(res => {
        if (res.code === 200) {
          this.profile = res.data
        }
      })
    },
    updateProfile() {
      request.put('/api/landlord/profile', this.profile).then(res => {
        if (res.code === 200) {
          this.$message.success('更新成功')
        }
      })
    }
  }
}
</script>
