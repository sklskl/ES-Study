<template>
  <div class="user-appointment">
    <h2>我的预约</h2>

    <!-- 新增预约按钮 -->
    <el-button type="primary" @click="openDialog(null)">新增预约</el-button>

    <!-- 预约列表表格 -->
    <el-table :data="appointmentList" style="margin-top: 20px;" border>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="houseTitle" label="房屋标题"></el-table-column>
      <el-table-column prop="appointmentTime" label="预约时间"></el-table-column>
      <el-table-column prop="status" label="状态"></el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="viewDetail(scope.row)">详情</el-button>
          <el-button size="mini" @click="openDialog(scope.row)">修改</el-button>
          <el-button type="danger" size="mini" @click="cancelAppointment(scope.row.id)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 预约详情弹窗 -->
    <el-dialog title="预约详情" :visible.sync="detailVisible" width="500px">
      <el-form :model="detailForm" label-width="80px">
        <el-form-item label="房屋">
          <el-input v-model="detailForm.houseTitle" disabled></el-input>
        </el-form-item>
        <el-form-item label="时间">
          <el-input v-model="detailForm.appointmentTime"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="detailForm.remark"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAppointment">保存</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import request from '@/api/request' // 你的 axios 封装

export default {
  name: 'UserAppointment',
  data() {
    return {
      appointmentList: [],
      detailVisible: false,
      detailForm: {
        id: null,
        houseTitle: '',
        appointmentTime: '',
        remark: ''
      }
    }
  },
  created() {
    this.getAppointmentList()
  },
  methods: {
    // 获取预约列表
    getAppointmentList() {
      request.get('/api/user/appointment/list').then(res => {
        if (res.code === 200) {
          this.appointmentList = res.data
        }
      })
    },
    // 查看/修改 预约
    openDialog(row) {
      this.detailVisible = true
      if (row) {
        // 编辑模式
        this.detailForm = { ...row }
      } else {
        // 新增模式
        this.detailForm = {
          id: null,
          houseTitle: '',
          appointmentTime: '',
          remark: ''
        }
      }
    },
    // 保存预约(新增或修改)
    saveAppointment() {
      if (this.detailForm.id) {
        // 更新
        request.put('/api/user/appointment/update', this.detailForm).then(res => {
          if (res.code === 200) {
            this.$message.success('更新成功')
            this.detailVisible = false
            this.getAppointmentList()
          }
        })
      } else {
        // 新增
        request.post('/api/user/appointment/add', this.detailForm).then(res => {
          if (res.code === 200) {
            this.$message.success('新增成功')
            this.detailVisible = false
            this.getAppointmentList()
          }
        })
      }
    },
    // 查看详情
    viewDetail(row) {
      this.$message.info(`查看预约详情，ID=${row.id}`)
      // 你也可以新开一个弹窗或者路由跳转
    },
    // 取消预约
    cancelAppointment(id) {
      this.$confirm('确定要取消该预约吗？', '提示', { type: 'warning' }).then(() => {
        request.delete(`/api/user/appointment/${id}`).then(res => {
          if (res.code === 200) {
            this.$message.success('已取消')
            this.getAppointmentList()
          }
        })
      })
    }
  }
}
</script>

<style scoped>
</style>
