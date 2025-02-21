<template>
  <div class="admin-user-manage">
    <h2>用户管理</h2>
    <el-button type="primary" @click="openDialog()">新增用户</el-button>
    <el-table :data="userList" style="margin-top: 20px;" border>
      <el-table-column prop="id" label="用户ID" width="80"></el-table-column>
      <el-table-column prop="username" label="用户名"></el-table-column>
      <el-table-column prop="role" label="角色"></el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button size="mini" @click="openDialog(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="用户编辑" :visible.sync="dialogVisible">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.role">
            <el-option label="user" value="user"></el-option>
            <el-option label="landlord" value="landlord"></el-option>
            <el-option label="admin" value="admin"></el-option>
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
  name: 'AdminUserManage',
  data() {
    return {
      userList: [],
      dialogVisible: false,
      editForm: {
        id: null,
        username: '',
        role: 'user'
      }
    }
  },
  created() {
    this.getUserList()
  },
  methods: {
    getUserList() {
      request.get('/api/admin/user/list').then(res => {
        if (res.code === 200) {
          this.userList = res.data
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
          username: '',
          role: 'user'
        }
      }
    },
    saveUser() {
      if (this.editForm.id) {
        request.put('/api/admin/user/update', this.editForm).then(res => {
          if (res.code === 200) {
            this.$message.success('更新成功')
            this.dialogVisible = false
            this.getUserList()
          }
        })
      } else {
        request.post('/api/admin/user/add', this.editForm).then(res => {
          if (res.code === 200) {
            this.$message.success('新增成功')
            this.dialogVisible = false
            this.getUserList()
          }
        })
      }
    },
    deleteUser(id) {
      this.$confirm('确认删除该用户吗？', '提示', { type: 'warning' }).then(() => {
        request.delete(`/api/admin/user/${id}`).then(res => {
          if (res.code === 200) {
            this.$message.success('删除成功')
            this.getUserList()
          }
        })
      })
    }
  }
}
</script>
