<template>
  <view class="tasks">
    <view class="header"><text class="title">📅 自律打卡</text><button class="add-btn" @click="showAdd=true">+ 新任务</button></view>
    <view class="task-list">
      <view class="task-item" v-for="t in tasks" :key="t.id" :class="{done:t.status===1}">
        <view class="t-check" @click="checkin(t)">{{ t.status===1 ? '✅' : '⬜' }}</view>
        <view class="t-info">
          <text class="t-content" :class="{completed:t.status===1}">{{ t.taskContent }}</text>
          <text class="t-time">{{ t.estimatedMinutes }}分钟</text>
        </view>
        <button class="t-del" @click="delTask(t.id)">🗑</button>
      </view>
    </view>
    <view class="summary"><text class="s-item">打卡12天</text><text class="s-item">学习86h</text><text class="s-item">完成45个</text></view>

    <view class="modal" v-if="showAdd">
      <view class="modal-content">
        <text class="m-title">新建任务</text>
        <input v-model="form.taskContent" placeholder="任务内容" class="m-input" />
        <input v-model="form.estimatedMinutes" type="number" placeholder="预计时长(分钟)" class="m-input" />
        <view class="m-btns"><button @click="showAdd=false">取消</button><button class="primary" @click="addTask">创建</button></view>
      </view>
    </view>
  </view>
</template>

<script>
const api = require('@/utils/api')
module.exports = {
  data() { return { tasks:[], showAdd:false, form:{taskContent:'',estimatedMinutes:30} } },
  onShow() { this.loadTasks() },
  methods: {
    async loadTasks() { const res=await api.get('/task/list'); this.tasks=res.data||[] },
    async addTask() { await api.post('/task',this.form); this.showAdd=false; this.form={taskContent:'',estimatedMinutes:30}; this.loadTasks(); uni.showToast({title:'创建成功'}) },
    async checkin(t) { if(t.status===1) return; await api.post('/task/'+t.id+'/checkin'); uni.showToast({title:'打卡成功！'}); this.loadTasks() },
    async delTask(id) { await api.delete('/task/'+id); this.loadTasks() }
  }
}
</script>

<style>
.tasks { padding:20rpx; }
.header { display:flex; justify-content:space-between; align-items:center; margin-bottom:20rpx; }
.title { font-size:36rpx; font-weight:600; }
.add-btn { background:#667eea; color:#fff; font-size:26rpx; padding:10rpx 24rpx; border-radius:8rpx; }
.task-list { background:#fff; border-radius:16rpx; padding:10rpx; }
.task-item { display:flex; align-items:center; gap:16rpx; padding:20rpx; border-bottom:1rpx solid #f5f5f5; }
.task-item.done { opacity:0.5; }
.t-check { font-size:36rpx; }
.t-info { flex:1; }
.t-content { font-size:28rpx; color:#303133; display:block; }
.t-content.completed { text-decoration:line-through; color:#c0c4cc; }
.t-time { font-size:24rpx; color:#909399; }
.t-del { font-size:32rpx; background:none; }
.summary { background:#fff; border-radius:16rpx; padding:24rpx; margin-top:20rpx; display:flex; justify-content:space-around; }
.s-item { font-size:28rpx; color:#667eea; font-weight:600; }
.modal { position:fixed; inset:0; background:rgba(0,0,0,0.5); display:flex; align-items:center; justify-content:center; z-index:100; padding:40rpx; }
.modal-content { background:#fff; border-radius:20rpx; padding:40rpx; width:100%; }
.m-title { font-size:34rpx; font-weight:600; margin-bottom:24rpx; display:block; }
.m-input { height:70rpx; border:2rpx solid #e4e7ed; border-radius:10rpx; padding:0 16rpx; margin-bottom:16rpx; }
.m-btns { display:flex; gap:16rpx; }
.m-btns button { flex:1; }
.primary { background:linear-gradient(135deg,#667eea,#764ba2); color:#fff; }
</style>
