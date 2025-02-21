<template>
  <div class="user-repair">
    <h2>报修管理</h2>
    <el-button type="primary" @click="openDialog">申请报修</el-button>

    <el-table :data="repairList" style="margin-top: 20px;" border>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="houseTitle" label="房屋"></el-table-column>
      <el-table-column prop="content" label="报修内容"></el-table-column>
      <el-table-column prop="status" label="状态"></el-table-column>
    </el-table>

    <el-dialog title="报修申请" :visible.sync="dialogVisible" width="400px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="房屋">
          <el-input v-model="formData.houseTitle"></el-input>
        </el-form-item>
        <el-form-item label="内容">
          <el-input type="textarea" v-model="formData.content"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitRepair">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/api/request'

export default {
  name: 'UserRepair',
  data() {
    return {
      repairList: [],
      dialogVisible: false,
      formData: {
        houseTitle: '',
        content: ''
      }
    }
  },
  created() {
    this.getRepairList()
  },
  methods: {
    getRepairList() {
      request.get('/api/user/repair/list').then(res => {
        if (res.code === 200) {
          this.repairList = res.data
        }
      })
    },
    openDialog() {
      this.dialogVisible = true
      this.formData = {
        houseTitle: '',
        content: ''
      }
    },
    submitRepair() {
      request.post('/api/user/repair/apply', this.formData).then(res => {
        if (res.code === 200) {
          this.$message.success('已提交报修')
          this.dialogVisible = false
          this.getRepairList()
        }
      })
    }
  }
}
</script>
