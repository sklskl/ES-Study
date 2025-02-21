<template>
  <div>
    <h2>管理员 - 用户管理</h2>
    <el-table :data="userList" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="role" label="角色" />
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="editUser(scope.row)">编辑</el-button>
          <el-button type="danger" size="mini" @click="deleteUser(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 编辑弹窗 -->
    <el-dialog :visible.sync="dialogVisible" title="编辑用户">
      <el-form :model="editForm">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.role">
            <el-option label="普通用户" value="user"></el-option>
            <el-option label="房主" value="landlord"></el-option>
            <el-option label="管理员" value="admin"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="saveUser">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/api/request'

export default {
  data() {
    return {
      userList: [],
      dialogVisible: false,
      editForm: {
        id: null,
        username: '',
        role: ''
      }
    }
  },
  created() {
    this.getUserList()
  },
  methods: {
    getUserList() {
      request.get('/api/admin/users').then(res => {
        if (res.code === 200) {
          this.userList = res.data
        }
      })
    },
    editUser(row) {
      this.dialogVisible = true
      this.editForm = { ...row }
    },
    saveUser() {
      request.put(`/api/admin/users/${this.editForm.id}`, this.editForm).then(res => {
        if (res.code === 200) {
          this.$message.success('编辑成功')
          this.dialogVisible = false
          this.getUserList()
        }
      })
    },
    deleteUser(id) {
      this.$confirm('确认删除该用户吗？', '提示', { type: 'warning' })
          .then(() => {
            request.delete(`/api/admin/users/${id}`).then(res => {
              if (res.code === 200) {
                this.$message.success('删除成功')
                this.getUserList()
              }
            })
          })
          .catch(() => {})
    }
  }
}
</script>
