<template>
  <view class="tasks">
    <view class="header-actions">
      <button class="add-btn" @click="showAddDialog = true">➕ 新建任务</button>
    </view>

    <view class="task-list">
      <view class="task-item" v-for="task in taskList" :key="task.id" :class="{done:task.status===1}">
        <view class="task-info">
          <text class="task-content">{{ task.taskContent }}</text>
          <text class="task-time" v-if="task.estimatedMinutes">预计{{ task.estimatedMinutes }}分钟</text>
          <text class="task-checkin-time" v-if="task.checkinTime">✅ {{ task.checkinTime }}</text>
        </view>
        <view class="task-actions">
          <button class="checkin-btn" v-if="task.status===0" @click="doCheckin(task.id)">打卡</button>
          <button class="del-btn" @click="doDelete(task.id)">删除</button>
        </view>
      </view>
      <view class="empty" v-if="taskList.length===0">
        <text>暂无任务，点击上方按钮新建任务</text>
      </view>
    </view>

    <!-- 新建任务弹窗 -->
    <view class="mask" v-if="showAddDialog" @click="showAddDialog=false"></view>
    <view class="dialog" v-if="showAddDialog">
      <view class="dialog-title">新建任务</view>
      <input class="dialog-input" v-model="newTaskContent" placeholder="输入任务内容..." />
      <input class="dialog-input" v-model="newTaskMinutes" placeholder="预计用时（分钟）" type="number" />
      <view class="dialog-actions">
        <button class="cancel-btn" @click="showAddDialog=false">取消</button>
        <button class="confirm-btn" @click="addTask">确认添加</button>
      </view>
    </view>
  </view>
</template>

<script>
const api = require("../../utils/api")
module.exports = {
  data() {
    return {
      taskList: [],
      showAddDialog: false,
      newTaskContent: "",
      newTaskMinutes: "30"
    }
  },
  onShow() {
    this.loadTasks()
  },
  methods: {
    async loadTasks() {
      try {
        const res = await api.get("/task/list")
        this.taskList = res.data || []
      } catch(e) {
        uni.showToast({ title: "加载失败", icon: "none" })
      }
    },
    async addTask() {
      if (!this.newTaskContent.trim()) {
        return uni.showToast({ title: "请输入任务内容", icon: "none" })
      }
      try {
        await api.post("/task", {
          taskContent: this.newTaskContent,
          estimatedMinutes: parseInt(this.newTaskMinutes) || 30
        })
        uni.showToast({ title: "创建成功" })
        this.showAddDialog = false
        this.newTaskContent = ""
        this.newTaskMinutes = "30"
        this.loadTasks()
      } catch(e) {
        uni.showToast({ title: "创建失败", icon: "none" })
      }
    },
    async doCheckin(taskId) {
      try {
        await api.post("/task/" + taskId + "/checkin")
        uni.showToast({ title: "打卡成功" })
        this.loadTasks()
      } catch(e) {
        uni.showToast({ title: "打卡失败", icon: "none" })
      }
    },
    async doDelete(taskId) {
      uni.showModal({
        title: "确认删除",
        content: "确定要删除这个任务吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              await api.delete("/task/" + taskId)
              uni.showToast({ title: "已删除" })
              this.loadTasks()
            } catch(e) {
              uni.showToast({ title: "删除失败", icon: "none" })
            }
          }
        }
      })
    }
  }
}
</script>

<style>
.tasks { padding: 20rpx; }
.header-actions { margin-bottom: 20rpx; }
.add-btn { width: 100%; height: 88rpx; background: linear-gradient(135deg, #667eea, #764ba2); color: #fff; border-radius: 12rpx; font-size: 32rpx; line-height: 88rpx; text-align: center; }
.task-list { background: #fff; border-radius: 16rpx; overflow: hidden; }
.task-item { display: flex; align-items: center; justify-content: space-between; padding: 24rpx; border-bottom: 1rpx solid #f5f5f5; }
.task-item.done { opacity: 0.6; }
.task-info { flex: 1; display: flex; flex-direction: column; gap: 6rpx; }
.task-content { font-size: 30rpx; color: #303133; font-weight: 500; }
.task-time { font-size: 24rpx; color: #909399; }
.task-checkin-time { font-size: 24rpx; color: #67c23a; }
.task-actions { display: flex; gap: 12rpx; }
.checkin-btn { background: #67c23a; color: #fff; border-radius: 8rpx; font-size: 26rpx; padding: 8rpx 20rpx; }
.del-btn { background: #f56c6c; color: #fff; border-radius: 8rpx; font-size: 26rpx; padding: 8rpx 20rpx; }
.empty { text-align: center; padding: 60rpx; font-size: 28rpx; color: #909399; }
.mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.4); z-index: 99; }
.dialog { position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); background: #fff; border-radius: 16rpx; padding: 40rpx; width: 560rpx; z-index: 100; }
.dialog-title { font-size: 34rpx; font-weight: 600; color: #303133; margin-bottom: 24rpx; text-align: center; }
.dialog-input { width: 100%; height: 72rpx; border: 2rpx solid #e4e7ed; border-radius: 10rpx; font-size: 28rpx; padding: 0 16rpx; margin-bottom: 16rpx; }
.dialog-actions { display: flex; gap: 20rpx; margin-top: 20rpx; }
.cancel-btn { flex: 1; height: 72rpx; background: #f0f2f5; color: #606266; border-radius: 10rpx; font-size: 28rpx; }
.confirm-btn { flex: 1; height: 72rpx; background: linear-gradient(135deg, #667eea, #764ba2); color: #fff; border-radius: 10rpx; font-size: 28rpx; }
</style>
