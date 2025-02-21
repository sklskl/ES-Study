<template>
  <div class="landlord-rent-payment-manage">
    <h2>房主 - 房屋缴租管理</h2>
    <el-table :data="paymentList" style="margin-top:20px;" border>
      <el-table-column prop="id" label="账单ID" width="80"></el-table-column>
      <el-table-column prop="houseTitle" label="房屋"></el-table-column>
      <el-table-column prop="tenantName" label="租客"></el-table-column>
      <el-table-column prop="rentAmount" label="租金"></el-table-column>
      <el-table-column prop="status" label="状态"></el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="remindPay(scope.row)">催缴</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import request from '@/api/request'

export default {
  name: 'LandlordRentPaymentManage',
  data() {
    return {
      paymentList: []
    }
  },
  created() {
    this.getRentList()
  },
  methods: {
    getRentList() {
      request.get('/api/landlord/rentPayment/list').then(res => {
        if (res.code === 200) {
          this.paymentList = res.data
        }
      })
    },
    remindPay(row) {
      request.post('/api/landlord/rentPayment/remind', { billId: row.id }).then(res => {
        if (res.code === 200) {
          this.$message.success('已发送催缴通知')
        }
      })
    }
  }
}
</script>
