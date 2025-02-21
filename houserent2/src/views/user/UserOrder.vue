<template>
  <div class="user-order">
    <h2>我的租赁订单</h2>
    <el-table :data="orderList" style="margin-top: 20px;" border>
      <el-table-column prop="id" label="订单ID" width="80"></el-table-column>
      <el-table-column prop="houseTitle" label="房屋标题"></el-table-column>
      <el-table-column prop="startDate" label="起租日期"></el-table-column>
      <el-table-column prop="endDate" label="到期日期"></el-table-column>
      <el-table-column prop="status" label="状态"></el-table-column>
      <el-table-column label="操作" width="250">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="viewDetail(scope.row)">详情</el-button>
          <el-button size="mini" @click="editOrder(scope.row)">编辑</el-button>
          <el-button type="danger" size="mini" @click="deleteOrder(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑弹窗 -->
    <el-dialog title="订单编辑" :visible.sync="dialogVisible" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="房屋">
          <el-input v-model="editForm.houseTitle"></el-input>
        </el-form-item>
        <el-form-item label="起租">
          <el-date-picker v-model="editForm.startDate" type="date"></el-date-picker>
        </el-form-item>
        <el-form-item label="到期">
          <el-date-picker v-model="editForm.endDate" type="date"></el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveOrder">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/api/request'

export default {
  name: 'UserOrder',
  data() {
    return {
      orderList: [],
      dialogVisible: false,
      editForm: {
        id: null,
        houseTitle: '',
        startDate: '',
        endDate: ''
      }
    }
  },
  created() {
    this.getOrderList()
  },
  methods: {
    // 获取订单列表
    getOrderList() {
      request.get('/api/user/order/list').then(res => {
        if (res.code === 200) {
          this.orderList = res.data
        }
      })
    },
    // 查看详情
    viewDetail(row) {
      this.$message.info('查看订单详情，订单ID=' + row.id)
      // 也可跳转或弹出详情
    },
    // 编辑
    editOrder(row) {
      this.dialogVisible = true
      this.editForm = { ...row }
    },
    // 保存(新增或更新)
    saveOrder() {
      if (this.editForm.id) {
        // 更新
        request.put('/api/user/order/update', this.editForm).then(res => {
          if (res.code === 200) {
            this.$message.success('更新成功')
            this.dialogVisible = false
            this.getOrderList()
          }
        })
      } else {
        // 新增(看你业务是否允许用户新增订单)
        request.post('/api/user/order/add', this.editForm).then(res => {
          if (res.code === 200) {
            this.$message.success('新增成功')
            this.dialogVisible = false
            this.getOrderList()
          }
        })
      }
    },
    // 删除订单
    deleteOrder(id) {
      this.$confirm('确定要删除此订单吗？', '提示', { type: 'warning' }).then(() => {
        request.delete(`/api/user/order/${id}`).then(res => {
          if (res.code === 200) {
            this.$message.success('删除成功')
            this.getOrderList()
          }
        })
      })
    }
  }
}
</script>
