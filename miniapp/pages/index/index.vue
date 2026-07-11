<template>
  <view class="index">
    <text>Loading...</text>
  </view>
</template>

<script>
module.exports = {
  onLoad() {
    const token = uni.getStorageSync('token')
    if (token) {
      uni.switchTab({ url: '/pages/home/home' })
    } else {
      uni.reLaunch({ url: '/pages/login/login' })
    }
  }
}
</script>
