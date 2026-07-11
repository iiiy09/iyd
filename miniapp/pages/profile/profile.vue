<template>
  <view class="profile">
    <view class="user-card">
      <view class="avatar">👤</view>
      <text class="nickname">{{ userInfo?.nickname || '同学' }}</text>
      <text class="stage">{{ userInfo?.stage || '未设置' }}</text>
    </view>
    <view class="menu-list">
      <view class="menu-item" v-for="m in menus" :key="m.label" @click="navigate(m.url)">
        <text class="m-icon">{{ m.icon }}</text>
        <text class="m-label">{{ m.label }}</text>
        <text class="m-arrow">›</text>
      </view>
    </view>
    <button class="logout-btn" @click="logout">退出登录</button>
  </view>
</template>

<script>
module.exports = {
  data() {
    return {
      userInfo: null,
      menus: [
        { icon:'📝', label:'错题本', url:'/pages/question/question' },
        { icon:'🎬', label:'观看记录', url:'/pages/courses/courses' },
        { icon:'📁', label:'我的收藏', url:'/pages/resources/resources' },
        { icon:'📊', label:'成绩报告', url:'/pages/profile/profile' },
        { icon:'📋', label:'使用帮助', url:'' },
      ]
    }
  },
  onShow() {
    const info = uni.getStorageSync('userInfo')
    if (info) this.userInfo = JSON.parse(info)
  },
  methods: {
    navigate(url) { if(url) uni.navigateTo({url}) },
    logout() {
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
      uni.reLaunch({ url: '/pages/login/login' })
    }
  }
}
</script>

<style>
.profile { padding:20rpx; }
.user-card { background:linear-gradient(135deg,#667eea,#764ba2); border-radius:20rpx; padding:50rpx; text-align:center; color:#fff; margin-bottom:20rpx; }
.avatar { font-size:80rpx; margin-bottom:16rpx; }
.nickname { font-size:36rpx; font-weight:700; display:block; }
.stage { font-size:26rpx; opacity:0.8; margin-top:8rpx; display:block; }
.menu-list { background:#fff; border-radius:16rpx; overflow:hidden; }
.menu-item { display:flex; align-items:center; padding:24rpx 30rpx; border-bottom:1rpx solid #f5f5f5; }
.m-icon { font-size:32rpx; margin-right:20rpx; }
.m-label { font-size:30rpx; color:#303133; flex:1; }
.m-arrow { font-size:36rpx; color:#c0c4cc; }
.logout-btn { width:90%; height:88rpx; background:#fff; color:#f56c6c; border-radius:12rpx; margin:40rpx auto; font-size:30rpx; }
</style>
