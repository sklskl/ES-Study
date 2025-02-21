<template>
  <div>
    <h2>普通用户 - 我的房屋管理</h2>
    <el-table :data="houseList" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="title" label="房屋标题"></el-table-column>
      <el-table-column prop="status" label="状态"></el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="viewDetail(scope.row)">详情</el-button>
          <el-button type="danger" size="mini" @click="deleteHouse(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import request from '@/api/request'

export default {
  name: 'UserHouseManage',
  data() {
    return {
      houseList: []
    }
  },
  created() {
    this.getHouseList()
  },
  methods: {
    getHouseList() {
      request.get('/api/user/houses').then(res => {
        if(res.code === 200) {
          this.houseList = res.data
        }
      })
    },
    viewDetail(row) {
      this.$message.info('查看详情，房屋ID：' + row.id)
      // 可以跳转到详情页
    },
    deleteHouse(id) {
      this.$confirm('确认删除该房屋记录吗？', '提示', {
        type: 'warning'
      }).then(() => {
        request.delete(`/api/user/houses/${id}`).then(res => {
          if(res.code === 200) {
            this.$message.success('删除成功')
            this.getHouseList()
          } else {
            this.$message.error('删除失败：' + res.message)
          }
        })
      }).catch(() => {})
    }
  }
}
</script>
