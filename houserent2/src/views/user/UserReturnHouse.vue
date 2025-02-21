<template>
  <div class="user-return-house">
    <h2>退租管理</h2>
    <el-button type="primary" @click="openDialog">申请退租</el-button>

    <el-table :data="returnList" style="margin-top: 20px;" border>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="houseTitle" label="房屋"></el-table-column>
      <el-table-column prop="applyDate" label="申请日期"></el-table-column>
      <el-table-column prop="status" label="状态"></el-table-column>
    </el-table>

    <el-dialog title="退租申请" :visible.sync="dialogVisible" width="400px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="房屋标题">
          <el-input v-model="formData.houseTitle"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="formData.remark"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitReturn">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/api/request'

export default {
  name: 'UserReturnHouse',
  data() {
    return {
      returnList: [],
      dialogVisible: false,
      formData: {
        houseTitle: '',
        remark: ''
      }
    }
  },
  created() {
    this.getReturnList()
  },
  methods: {
    getReturnList() {
      request.get('/api/user/returnHouse/list').then(res => {
        if (res.code === 200) {
          this.returnList = res.data
        }
      })
    },
    openDialog() {
      this.dialogVisible = true
      this.formData = {
        houseTitle: '',
        remark: ''
      }
    },
    submitReturn() {
      request.post('/api/user/returnHouse/apply', this.formData).then(res => {
        if (res.code === 200) {
          this.$message.success('申请成功')
          this.dialogVisible = false
          this.getReturnList()
        }
      })
    }
  }
}
</script>
