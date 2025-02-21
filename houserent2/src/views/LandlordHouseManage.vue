<template>
  <div>
    <h2>房主 - 房屋租赁管理</h2>
    <el-button type="primary" @click="addHouse">发布房屋</el-button>
    <el-table :data="houseList" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="title" label="房屋标题"></el-table-column>
      <el-table-column prop="status" label="状态"></el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="editHouse(scope.row)">编辑</el-button>
          <el-button type="danger" size="mini" @click="deleteHouse(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :visible.sync="dialogVisible" title="房屋信息">
      <el-form :model="editForm">
        <el-form-item label="标题">
          <el-input v-model="editForm.title"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="editForm.description"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="saveHouse">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/api/request'

export default {
  data() {
    return {
      houseList: [],
      dialogVisible: false,
      editForm: {
        id: null,
        title: '',
        description: '',
        status: 1
      }
    }
  },
  created() {
    this.getHouseList()
  },
  methods: {
    getHouseList() {
      request.get('/api/landlord/houses').then(res => {
        if(res.code === 200) {
          this.houseList = res.data
        }
      })
    },
    addHouse() {
      this.dialogVisible = true
      this.editForm = {
        id: null,
        title: '',
        description: ''
      }
    },
    editHouse(row) {
      this.dialogVisible = true
      this.editForm = {
        id: row.id,
        title: row.title,
        description: row.description
      }
    },
    saveHouse() {
      if(this.editForm.id) {
        // 更新
        request.put(`/api/landlord/houses/${this.editForm.id}`, this.editForm).then(res => {
          if(res.code === 200) {
            this.$message.success('更新成功')
            this.dialogVisible = false
            this.getHouseList()
          }
        })
      } else {
        // 新增
        request.post(`/api/landlord/houses`, this.editForm).then(res => {
          if(res.code === 200) {
            this.$message.success('新增成功')
            this.dialogVisible = false
            this.getHouseList()
          }
        })
      }
    },
    deleteHouse(id) {
      this.$confirm('确认删除该房屋？', '提示', {
        type: 'warning'
      }).then(() => {
        request.delete(`/api/landlord/houses/${id}`).then(res => {
          if(res.code === 200) {
            this.$message.success('删除成功')
            this.getHouseList()
          }
        })
      }).catch(() => {})
    }
  }
}
</script>
