<template>
  <!-- 唯一根元素 -->
  <div id="app">

    <!-- 顶部导航栏 -->
    <header style="background: #f0f0f0; padding: 10px;">
      <nav>
        <ul style="list-style:none; margin:0; padding:0; display:flex;">
          <li style="margin-right:20px;">
            <router-link to="/">首页</router-link>
          </li>
          <li style="margin-right:20px;">
            <router-link to="/login">登录</router-link>
          </li>
          <li style="margin-right:20px;">
            <router-link to="/register">注册</router-link>
          </li>

          <!-- 仅在 user 角色或 landlord 角色下显示 -->
          <li v-if="role === 'user'" style="margin-right:20px;">
            <router-link to="/user/home">用户首页</router-link>
          </li>
          <li v-if="role === 'landlord'" style="margin-right:20px;">
            <router-link to="/landlord/home">房主首页</router-link>
          </li>

          <!-- 管理员才显示的入口 -->
          <li v-if="role === 'admin'" style="margin-right:20px;">
            <router-link to="/admin/home">管理员首页</router-link>
          </li>
        </ul>
      </nav>
    </header>

    <!-- 主体区：路由出口，根据 path 匹配不同组件 -->
    <main style="padding:10px;">
      <router-view />
    </main>

  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'App',
  computed: {
    // 从 Vuex 中获取当前用户角色
    ...mapState({
      role: state => state.user && state.user.role
    })
  }
}
</script>

<style scoped>
/* 可根据需求对导航栏做更多美化 */
a {
  text-decoration: none;
  color: #333;
}
.router-link-active {
  font-weight: bold;
}
</style>
