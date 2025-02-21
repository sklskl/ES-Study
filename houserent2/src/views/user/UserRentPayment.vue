<template>
  <div class="user-rent-payment">
    <h2>房租缴纳</h2>

    <el-table :data="billList" style="margin-top: 20px;" border>
      <el-table-column prop="id" label="账单ID" width="80"></el-table-column>
      <el-table-column prop="houseTitle" label="房屋标题"></el-table-column>
      <el-table-column prop="rentAmount" label="租金"></el-table-column>
      <el-table-column prop="deadline" label="缴费截止"></el-table-column>
      <el-table-column prop="status" label="状态"></el-table-column>
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="pay(scope.row)">去支付</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import request from '@/api/request'

export default {
  name: 'UserRentPayment',
  data() {
    return {
      billList: []
    }
  },
  created() {
    this.getBillList()
  },
  methods: {
    getBillList() {
      request.get('/api/user/rentPayment/list').then(res => {
        if (res.code === 200) {
          this.billList = res.data
        }
      })
    },
    pay(row) {
      this.$confirm('确认立即支付该账单吗？', '提示', { type: 'warning' }).then(() => {
        // 模拟支付
        request.post('/api/user/rentPayment/pay', { billId: row.id }).then(res => {
          if (res.code === 200) {
            this.$message.success('支付成功')
            this.getBillList()
          } else {
            this.$message.error('支付失败：' + res.message)
          }
        })
      })
    }
  }
}
</script>

<style scoped>
</style>
