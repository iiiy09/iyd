<template>
  <view class="resources">
    <view class="filters">
      <picker mode="selector" :range="stages" @change="e=>stage=stages[e.detail.value]"><view class="f-item">{{stage||'学段'}}</view></picker>
      <picker mode="selector" :range="subjects" @change="e=>subject=subjects[e.detail.value]"><view class="f-item">{{subject||'科目'}}</view></picker>
      <picker mode="selector" :range="types" @change="e=>resType=types[e.detail.value]"><view class="f-item">{{resType||'类型'}}</view></picker>
    </view>
    <view class="res-list">
      <view class="res-item" v-for="r in list" :key="r.id">
        <text class="res-name">{{ r.resourceName }}</text>
        <view class="res-meta">
          <text class="res-tag">{{ r.stage }}</text>
          <text class="res-tag2">{{ r.subject }}</text>
          <text class="res-type">{{ r.resourceType }}</text>
        </view>
        <view class="res-actions">
          <button class="act-btn" @click="download(r)">下载</button>
          <button class="act-btn save" @click="collect(r)">{{r.collected?'已收藏':'收藏'}}</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
const api = require('@/utils/api')
module.exports = {
  data() { return { list:[], stage:'', subject:'', resType:'', stages:['小学','初中','高中','大学'], subjects:['语文','数学','英语'], types:['课件','真题','复习提纲','答案'] } },
  onShow() { this.search() },
  methods: {
    async search() { const res=await api.post('/resource/search',{stage:this.stage,subject:this.subject,resourceType:this.resType,page:1,size:20}); this.list=res.data?.records||[] },
    download(r) { uni.showToast({title:'开始下载'}) },
    async collect(r) { await api.post('/resource/'+r.id+'/collect'); r.collected=!r.collected; uni.showToast({title:r.collected?'已收藏':'已取消'}) }
  }
}
</script>

<style>
.resources { padding:20rpx; }
.filters { display:flex; gap:12rpx; margin-bottom:16rpx; }
.f-item { background:#fff; padding:14rpx 20rpx; border-radius:10rpx; font-size:24rpx; color:#606266; }
.res-item { background:#fff; border-radius:16rpx; padding:24rpx; margin-bottom:14rpx; }
.res-name { font-size:30rpx; font-weight:600; color:#303133; }
.res-meta { display:flex; gap:10rpx; margin:12rpx 0; }
.res-tag { font-size:22rpx; color:#fff; background:#667eea; padding:4rpx 12rpx; border-radius:6rpx; }
.res-tag2 { font-size:22rpx; color:#fff; background:#67c23a; padding:4rpx 12rpx; border-radius:6rpx; }
.res-type { font-size:22rpx; color:#909399; background:#f0f2f5; padding:4rpx 12rpx; border-radius:6rpx; }
.res-actions { display:flex; gap:12rpx; }
.act-btn { flex:1; height:60rpx; font-size:24rpx; border-radius:8rpx; text-align:center; line-height:60rpx; background:#f0f2f5; }
.act-btn.save { background:#fff7e6; color:#e6a23c; }
</style>
