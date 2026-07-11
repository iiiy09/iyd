<template>
  <view class="words">
    <view class="flashcard" @click="flipped=!flipped">
      <view v-if="!flipped">
        <text class="word">{{ currentWord?.word || 'Loading...' }}</text>
        <text class="phonetic">{{ currentWord?.phonetic }}</text>
      </view>
      <view v-else>
        <text class="meaning-label">释义</text>
        <text class="meaning">{{ currentWord?.meaning }}</text>
      </view>
    </view>
    <view class="card-actions">
      <button class="flip-btn" @click="flipped=!flipped">{{ flipped?'显示单词':'查看释义' }}</button>
      <view class="review-btns">
        <button class="no-btn" @click="review(false)">不认识</button>
        <button class="yes-btn" @click="review(true)">认识</button>
      </view>
    </view>
    <view class="progress-bar">
      <text>掌握: 245</text>
      <text>复习中: 89</text>
      <text>剩余: 2166</text>
    </view>
  </view>
</template>

<script>
const api = require('@/utils/api')
module.exports = {
  data() { return { flipped:false, currentWord:null } },
  onShow() { this.loadWord() },
  methods: {
    async loadWord() { const res = await api.get('/recite/words',{stage:'中考'}); if(res.data?.length) this.currentWord=res.data[0] },
    async review(known) {
      if(!this.currentWord) return
      await api.post('/recite/word/'+this.currentWord.id+'/review?known='+known)
      uni.showToast({title:known?'已掌握':'已加入复习'})
      this.flipped=false; this.loadWord()
    }
  }
}
</script>

<style>
.words { padding:40rpx; display:flex; flex-direction:column; align-items:center; }
.flashcard { width:600rpx; height:360rpx; background:linear-gradient(135deg,#f5f7fa,#c3cfe2); border-radius:24rpx; display:flex; align-items:center; justify-content:center; margin-bottom:30rpx; }
.word { font-size:60rpx; font-weight:700; color:#303133; }
.phonetic { display:block; font-size:28rpx; color:#909399; margin-top:12rpx; }
.meaning-label { font-size:26rpx; color:#909399; }
.meaning { font-size:40rpx; font-weight:600; color:#303133; }
.card-actions { width:100%; display:flex; flex-direction:column; gap:20rpx; }
.flip-btn { background:#f0f2f5; color:#606266; border-radius:12rpx; font-size:28rpx; }
.review-btns { display:flex; gap:20rpx; }
.no-btn { flex:1; background:#f56c6c; color:#fff; border-radius:12rpx; font-size:28rpx; }
.yes-btn { flex:1; background:#67c23a; color:#fff; border-radius:12rpx; font-size:28rpx; }
.progress-bar { margin-top:40rpx; background:#fff; border-radius:16rpx; padding:24rpx; width:100%; display:flex; justify-content:space-around; font-size:26rpx; color:#909399; }
</style>
