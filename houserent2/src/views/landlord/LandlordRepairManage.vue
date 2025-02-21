<template>
  <div class="landlord-repair-manage">
    <h2>房主 - 报修信息管理</h2>
    <el-table :data="repairList" border>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="houseTitle" label="房屋"></el-table-column>
      <el-table-column prop="tenantName" label="租客"></el-table-column>
      <el-table-column prop="content" label="报修内容"></el-table-column>
      <el-table-column prop="status" label="状态"></el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button size="mini" @click="assignWorker(scope.row)">派单</el-button>
          <el-button size="mini" @click="markDone(scope.row)">完成</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 派单弹窗 -->
    <el-dialog title="派单" :visible.sync="assignVisible">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="维修人员">
          <el-input v-model="assignForm.worker"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="assignVisible=false">取消</el-button>
        <el-button type="primary" @click="submitAssign">确认</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/api/request'

export default {
  name: 'LandlordRepairManage',
  data() {
    return {
      repairList: [],
      assignVisible: false,
      assignForm: {
        id: null,
        worker: ''
      }
    }
  },
  created() {
    this.getRepairList()
  },
  methods: {
    getRepairList() {
      request.get('/api/landlord/repair/list').then(res => {
        if (res.code === 200) {
          this.repairList = res.data
        }
      })
    },
    assignWorker(row) {
      this.assignVisible = true
      this.assignForm = { id: row.id, worker: '' }
    },
    submitAssign() {
      request.post('/api/landlord/repair/assign', this.assignForm).then(res => {
        if (res.code === 200) {
          this.$message.success('已派单')
          this.assignVisible = false
          this.getRepairList()
        }
      })
    },
    markDone(row) {
      this.$confirm('确认将此报修标记为完成吗？', '提示').then(() => {
        request.post('/api/landlord/repair/markDone', { id: row.id }).then(res => {
          if (res.code === 200) {
            this.$message.success('操作成功')
            this.getRepairList()
          }
        })
      })
    }
  }
}
</script>
