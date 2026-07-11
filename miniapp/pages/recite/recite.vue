<template>
  <view class="recite">
    <view class="mode-tabs">
      <view :class="{active:mode===1}" @click="mode=1">文字批改</view>
      <view :class="{active:mode===2}" @click="mode=2">拍照批改</view>
      <view :class="{active:mode===3}" @click="mode=3">视频批改</view>
    </view>
    <view class="card"><text class="label">背诵原文</text>
      <textarea v-model="original" placeholder="粘贴背诵原文..." class="r-input" />
    </view>
    <view class="card" v-if="mode===1"><text class="label">默写作答</text>
      <textarea v-model="answer" placeholder="在此默写..." class="r-input" />
    </view>
    <view class="card" v-else-if="mode===2"><text class="label">拍照上传</text>
      <button class="photo-btn" @click="takePhoto">📷 拍摄手写内容</button>
    </view>
    <view class="card" v-else><text class="label">视频上传</text>
      <button class="photo-btn" @click="recordVideo">🎥 录制背诵视频</button>
    </view>
    <button class="submit-btn" @click="submit" :loading="loading">提交AI批改</button>

    <view class="result" v-if="result">
      <view class="score-big" :style="{background: result.score>=90?'#67c23a':result.score>=70?'#e6a23c':'#f56c6c'}">
        <text class="score-num">{{result.score}}</text><text class="score-unit">分</text>
      </view>
      <text class="suggestion">{{result.suggestion}}</text>
      <view class="error-item" v-for="(e,i) in result.errors" :key="i">
        <text>第{{e.line}}行: {{e.type==='missing'?'❌缺失':'⚠️错漏'}} - {{e.original}}</text>
      </view>
    </view>
  </view>
</template>

<script>
const api = require('@/utils/api')
module.exports = {
  data() { return { mode:1, original:'', answer:'', loading:false, result:null } },
  methods: {
    takePhoto() { uni.chooseImage({ count:1, success: (res) => { uni.showToast({title:'已选择图片'}) } }) },
    recordVideo() { uni.chooseVideo({ sourceType:['camera'], success: (res) => { uni.showToast({title:'已录制视频'}) } }) },
    async submit() {
      if (!this.original.trim()) return uni.showToast({title:'请输入原文',icon:'none'})
      this.loading = true
      try {
        const res = await api.post('/recite/submit', { originalText:this.original, userAnswer:this.answer, checkType:this.mode })
        this.result = res.data
      } finally { this.loading = false }
    }
  }
}
</script>

<style>
.recite { padding:20rpx; }
.mode-tabs { display:flex; gap:0; background:#fff; border-radius:16rpx; overflow:hidden; margin-bottom:20rpx; }
.mode-tabs view { flex:1; text-align:center; padding:20rpx; font-size:26rpx; color:#909399; }
.mode-tabs view.active { background:#667eea; color:#fff; }
.card { background:#fff; border-radius:16rpx; padding:24rpx; margin-bottom:16rpx; }
.label { font-size:28rpx; font-weight:600; color:#303133; margin-bottom:12rpx; display:block; }
.r-input { width:100%; height:200rpx; font-size:28rpx; padding:16rpx; border:2rpx solid #e4e7ed; border-radius:12rpx; }
.photo-btn { width:100%; height:160rpx; background:#f5f7fa; border-radius:12rpx; font-size:32rpx; color:#909399; display:flex; align-items:center; justify-content:center; border:2rpx dashed #d0d5dd; }
.submit-btn { width:100%; height:88rpx; background:linear-gradient(135deg,#667eea,#764ba2); color:#fff; border-radius:12rpx; font-size:32rpx; }
.result { background:#fff; border-radius:16rpx; padding:24rpx; margin-top:20rpx; text-align:center; }
.score-big { width:140rpx; height:140rpx; border-radius:50%; display:flex; flex-direction:column; align-items:center; justify-content:center; margin:0 auto 16rpx; }
.score-num { font-size:48rpx; font-weight:700; color:#fff; }
.score-unit { font-size:24rpx; color:rgba(255,255,255,0.8); }
.suggestion { font-size:26rpx; color:#909399; display:block; margin:12rpx 0; }
.error-item { font-size:26rpx; color:#f56c6c; padding:8rpx 0; text-align:left; }
</style>
