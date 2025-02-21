<template>
  <div class="landlord-return-house-manage">
    <h2>房主 - 房屋退租管理</h2>
    <el-table :data="returnList" border>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="houseTitle" label="房屋"></el-table-column>
      <el-table-column prop="tenantName" label="租客"></el-table-column>
      <el-table-column prop="applyDate" label="申请日期"></el-table-column>
      <el-table-column prop="status" label="状态"></el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="approve(scope.row, true)">同意</el-button>
          <el-button size="mini" type="danger" @click="approve(scope.row, false)">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import request from '@/api/request'

export default {
  name: 'LandlordReturnHouseManage',
  data() {
    return {
      returnList: []
    }
  },
  created() {
    this.getReturnList()
  },
  methods: {
    getReturnList() {
      request.get('/api/landlord/returnHouse/list').then(res => {
        if (res.code === 200) {
          this.returnList = res.data
        }
      })
    },
    approve(row, isAgree) {
      const action = isAgree ? '同意' : '拒绝'
      this.$confirm(`确定要${action}退租请求吗？`, '提示', { type: 'warning' }).then(() => {
        request.post('/api/landlord/returnHouse/approve', {
          id: row.id,
          isAgree
        }).then(res => {
          if (res.code === 200) {
            this.$message.success(`${action}成功`)
            this.getReturnList()
          }
        })
      })
    }
  }
}
</script>
