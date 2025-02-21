<template>
  <div class="landlord-order-manage">
    <h2>房主 - 租赁订单管理</h2>
    <el-table :data="orderList" style="margin-top: 20px;" border>
      <el-table-column prop="id" label="订单ID" width="80"></el-table-column>
      <el-table-column prop="houseTitle" label="房屋标题"></el-table-column>
      <el-table-column prop="tenantName" label="租客"></el-table-column>
      <el-table-column prop="status" label="状态"></el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="viewDetail(scope.row)">详情</el-button>
          <el-button size="mini" @click="updateStatus(scope.row)">更改状态</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 状态更新弹窗 -->
    <el-dialog title="订单状态更新" :visible.sync="statusDialogVisible">
      <el-form :model="statusForm" label-width="80px">
        <el-form-item label="状态">
          <el-select v-model="statusForm.status">
            <el-option label="进行中" value="进行中"></el-option>
            <el-option label="已完成" value="已完成"></el-option>
            <el-option label="已取消" value="已取消"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveStatus">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/api/request'

export default {
  name: 'LandlordOrderManage',
  data() {
    return {
      orderList: [],
      statusDialogVisible: false,
      statusForm: {
        id: null,
        status: ''
      }
    }
  },
  created() {
    this.getOrderList()
  },
  methods: {
    getOrderList() {
      request.get('/api/landlord/order/list').then(res => {
        if (res.code === 200) {
          this.orderList = res.data
        }
      })
    },
    viewDetail(row) {
      this.$message.info('查看订单详情：ID=' + row.id)
    },
    updateStatus(row) {
      this.statusDialogVisible = true
      this.statusForm = { id: row.id, status: row.status }
    },
    saveStatus() {
      request.put('/api/landlord/order/updateStatus', this.statusForm).then(res => {
        if (res.code === 200) {
          this.$message.success('状态更新成功')
          this.statusDialogVisible = false
          this.getOrderList()
        }
      })
    }
  }
}
</script>
