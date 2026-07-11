<script>
export default {
  onLaunch() {
    console.log('iyd学习小程序启动')
    const token = uni.getStorageSync('token')
    if (!token) {
      uni.reLaunch({ url: '/pages/login/login' })
    }
  }
}
</script>

<style>
page { background: #f0f2f5; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; }
</style>
