<template>
  <view class="home">
    <view class="welcome-banner">
      <text class="greeting">馃憢 娆㈣繋鍥炴潵</text>
      <text class="name">{{ userInfo?.nickname || '鍚屽' }}</text>
    </view>
    <view class="stats-row">
      <view class="stat" v-for="s in stats" :key="s.label"><text class="s-num">{{ s.value }}</text><text class="s-label">{{ s.label }}</text></view>
    </view>
    <view class="section-title">鈿?鍔熻兘鍏ュ彛</view>
    <view class="func-grid">
      <view class="func-item" v-for="f in funcs" :key="f.label" @click="navigate(f.url)">
        <text class="f-icon">{{ f.icon }}</text>
        <text class="f-label">{{ f.label }}</text>
      </view>
    </view>
    <view class="section-title">馃搵 浠婃棩寰呭涔?/view>
    <view class="review-list">
      <view class="review-item" v-for="r in reviews" :key="r.id">
        <text class="r-tag">{{ r.tag }}</text>
        <text class="r-content">{{ r.content }}</text>
      </view>
    </view>
  </view>
</template>

<script>
module.exports = {
  data() {
    return {
      userInfo: null,
      stats: [
        { value: '5', label: '浠婃棩浠诲姟' },
        { value: '12澶?, label: '杩炵画鎵撳崱' },
        { value: '86h', label: '绱瀛︿範' },
      ],
      funcs: [
        { icon: '馃帳', label: 'AI鑳岃', url: '/pages/recite/recite' },
        { icon: '馃攳', label: 'AI鍒烽', url: '/pages/question/question' },
        { icon: '馃敜', label: '鍗曡瘝', url: '/pages/words/words' },
        { icon: '馃', label: '绗旇', url: '/pages/notes/notes' },
                { icon: '鉁嶏笍', label: 'AI鍒涗綔', url: '/pages/ai/ai' },
        { icon: '馃搧', label: '璧勬枡', url: '/pages/resources/resources' },
              ],
      reviews: [
        { id:1, tag:'鍗曡瘝', content:'Unit 5 鏍稿績璇嶆眹澶嶄範' },
        { id:2, tag:'閿欓', content:'鏁板-浜屾鍑芥暟閿欓澶嶇洏' },
        { id:3, tag:'鑳岃', content:'銆婂渤闃虫ゼ璁般€嬬涓夋' },
      ]
    }
  },
  onShow() {
    const info = uni.getStorageSync('userInfo')
    if (info) this.userInfo = JSON.parse(info)
  },
  methods: {
    navigate(url) { uni.navigateTo({ url }) }
  }
}
</script>

<style>
.home { padding:20rpx; }
.welcome-banner { background:linear-gradient(135deg,#667eea,#764ba2); border-radius:20rpx; padding:40rpx; color:#fff; margin-bottom:20rpx; }
.greeting { font-size:28rpx; opacity:0.8; display:block; }
.name { font-size:40rpx; font-weight:700; }
.stats-row { display:flex; justify-content:space-around; background:#fff; border-radius:16rpx; padding:24rpx; margin-bottom:20rpx; }
.stat { text-align:center; }
.s-num { display:block; font-size:36rpx; font-weight:700; color:#303133; }
.s-label { font-size:24rpx; color:#909399; }
.section-title { font-size:32rpx; font-weight:600; color:#303133; margin:20rpx 0 16rpx; }
.func-grid { display:grid; grid-template-columns:repeat(4,1fr); gap:16rpx; }
.func-item { background:#fff; border-radius:16rpx; padding:24rpx 12rpx; text-align:center; display:flex; flex-direction:column; gap:8rpx; }
.f-icon { font-size:40rpx; }
.f-label { font-size:24rpx; color:#606266; }
.review-list { background:#fff; border-radius:16rpx; padding:20rpx; }
.review-item { display:flex; gap:16rpx; padding:14rpx 0; border-bottom:1rpx solid #f5f5f5; }
.r-tag { font-size:24rpx; color:#667eea; font-weight:600; }
.r-content { font-size:28rpx; color:#606266; flex:1; }
</style>


