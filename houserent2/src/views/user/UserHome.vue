<template>
  <div>
    <h2>欢迎来到用户首页，{{ username }}！</h2>
    <p>这里可以展示一些房屋资讯或公告等。</p>

  </div>
</template>

<script>
import { mapState } from 'vuex'
export default {
  name: 'UserHome',
  computed: {
    ...mapState({
      username: state => state.user.username
    })
  }
}
</script>
