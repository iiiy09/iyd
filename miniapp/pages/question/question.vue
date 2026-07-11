<template>
  <view class="question">
    <view class="search-bar">
      <textarea v-model="questionText" placeholder="输入题目内容，AI分步解析..." class="q-input" />
      <button class="search-btn" @click="search" :loading="loading">AI智能解析</button>
    </view>
    <view class="result" v-if="result">
      <view class="steps">
        <view class="step" v-for="(step,i) in result.steps" :key="i">
          <text class="step-num">{{i+1}}</text>
          <text class="step-text">{{step}}</text>
        </view>
      </view>
      <view class="answer-box"><text class="a-label">答案：</text><text class="a-text">{{result.answer}}</text></view>
      <view class="kp-tags"><text class="kp-label">知识点：</text><text class="kp-tag" v-for="kp in result.knowledgePoints" :key="kp">{{kp}}</text></view>
    </view>
  </view>
</template>

<script>
const api = require('@/utils/api')
module.exports = {
  data() { return { questionText:'', loading:false, result:null } },
  methods: {
    async search() {
      if (!this.questionText.trim()) return uni.showToast({title:'请输入题目',icon:'none'})
      this.loading = true
      try {
        const res = await api.post('/question/search', { content: this.questionText, subject:'数学', stage:'初中' })
        this.result = res.data
      } finally { this.loading = false }
    }
  }
}
</script>

<style>
.question { padding:20rpx; }
.search-bar { background:#fff; border-radius:16rpx; padding:24rpx; }
.q-input { width:100%; height:200rpx; font-size:28rpx; padding:16rpx; border:2rpx solid #e4e7ed; border-radius:12rpx; margin-bottom:16rpx; }
.search-btn { width:100%; height:80rpx; background:linear-gradient(135deg,#667eea,#764ba2); color:#fff; border-radius:12rpx; font-size:30rpx; }
.result { background:#fff; border-radius:16rpx; padding:24rpx; margin-top:20rpx; }
.step { display:flex; gap:16rpx; padding:12rpx 0; border-bottom:1rpx dashed #ebeef5; }
.step-num { width:40rpx; height:40rpx; background:#667eea; color:#fff; border-radius:50%; text-align:center; line-height:40rpx; font-size:24rpx; flex-shrink:0; }
.step-text { font-size:26rpx; color:#606266; line-height:1.6; }
.answer-box { padding:16rpx 0; }
.a-label { font-size:26rpx; color:#67c23a; }
.a-text { font-size:30rpx; font-weight:600; color:#67c23a; }
.kp-label { font-size:24rpx; color:#909399; }
.kp-tag { font-size:24rpx; color:#667eea; margin-right:16rpx; }
</style>
