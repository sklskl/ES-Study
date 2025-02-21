<template>
  <div>
    <h1>{{ msg }}</h1>
    <p>房屋中介系统</p>
  </div>
</template>

<script>
export default {
  name: 'HelloWorld',
  props: {
    msg: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      count: 0
    }
  }
}
</script>

<style scoped>
</style>
