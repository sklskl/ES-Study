<template>
  <div class="landlord-house-manage">
    <h2>房主 - 房屋管理</h2>
    <el-button type="primary" @click="openDialog()">新增房屋</el-button>

    <el-table :data="houseList" border style="margin-top: 20px;">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="title" label="房屋标题"></el-table-column>
      <el-table-column prop="status" label="状态"></el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="openDialog(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="deleteHouse(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 弹窗 -->
    <el-dialog title="房屋信息" :visible.sync="dialogVisible">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="editForm.title"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="editForm.description"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" placeholder="选择状态">
            <el-option label="在租" value="在租"></el-option>
            <el-option label="待租" value="待租"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveHouse">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/api/request'

export default {
  name: 'LandlordHouseManage',
  data() {
    return {
      houseList: [],
      dialogVisible: false,
      editForm: {
        id: null,
        title: '',
        description: '',
        status: '待租'
      }
    }
  },
  created() {
    this.getHouseList()
  },
  methods: {
    getHouseList() {
      request.get('/api/landlord/house/list').then(res => {
        if (res.code === 200) {
          this.houseList = res.data
        }
      })
    },
    openDialog(row) {
      this.dialogVisible = true
      if (row) {
        this.editForm = { ...row }
      } else {
        this.editForm = {
          id: null,
          title: '',
          description: '',
          status: '待租'
        }
      }
    },
    saveHouse() {
      if (this.editForm.id) {
        request.put('/api/landlord/house/update', this.editForm).then(res => {
          if (res.code === 200) {
            this.$message.success('更新成功')
            this.dialogVisible = false
            this.getHouseList()
          }
        })
      } else {
        request.post('/api/landlord/house/add', this.editForm).then(res => {
          if (res.code === 200) {
            this.$message.success('新增成功')
            this.dialogVisible = false
            this.getHouseList()
          }
        })
      }
    },
    deleteHouse(id) {
      this.$confirm('确认删除房屋吗？', '提示', { type: 'warning' })
          .then(() => {
            request.delete(`/api/landlord/house/${id}`).then(res => {
              if (res.code === 200) {
                this.$message.success('删除成功')
                this.getHouseList()
              }
            })
          })
          .catch(() => {})
    }
  }
}
</script>
