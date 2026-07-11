<template>
  <view class="courses">
    <view class="filters">
      <picker mode="selector" :range="stages" @change="e=>stage=stages[e.detail.value]"><view class="filter-item">{{stage||'学段'}}</view></picker>
      <picker mode="selector" :range="subjects" @change="e=>subject=subjects[e.detail.value]"><view class="filter-item">{{subject||'科目'}}</view></picker>
    </view>
    <view class="course-list">
      <view class="course-card" v-for="c in courses" :key="c.id" @click="play(c)">
        <image src="/static/course-bg.png" mode="aspectFill" class="c-cover" />
        <view class="c-info">
          <text class="c-name">{{ c.courseName }}</text>
          <text class="c-teacher">{{ c.teacherName }}</text>
          <text class="c-duration">{{ Math.floor(c.duration/60) }}分钟</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
const api = require('@/utils/api')
module.exports = {
  data() { return { courses:[], stage:'', subject:'', stages:['小学','初中','高中','大学'], subjects:['语文','数学','英语','物理','化学'] } },
  onShow() { this.loadCourses() },
  methods: {
    async loadCourses() { const res = await api.get('/course/list',{stage:this.stage,subject:this.subject,page:1,size:20}); this.courses=res.data?.records||[] },
    play(c) { uni.showToast({title:'播放:'+c.courseName}) }
  }
}
</script>

<style>
.courses { padding:20rpx; }
.filters { display:flex; gap:16rpx; margin-bottom:20rpx; }
.filter-item { background:#fff; padding:16rpx 24rpx; border-radius:10rpx; font-size:26rpx; color:#606266; }
.course-list { display:grid; grid-template-columns:1fr 1fr; gap:16rpx; }
.course-card { background:#fff; border-radius:16rpx; overflow:hidden; }
.c-cover { width:100%; height:180rpx; background:#e4e7ed; }
.c-info { padding:16rpx; }
.c-name { font-size:28rpx; font-weight:600; color:#303133; }
.c-teacher { font-size:24rpx; color:#909399; margin:6rpx 0; display:block; }
.c-duration { font-size:22rpx; color:#c0c4cc; }
</style>
