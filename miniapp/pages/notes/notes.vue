<template>
  <view class="notes">
    <view class="header">
      <text class="title">🧠 思维导图笔记</text>
      <button class="add-btn" @click="showAdd=true">+ 新建</button>
    </view>
    <view class="notes-grid">
      <view class="note-card" v-for="n in notes" :key="n.id" @click="viewNote(n)">
        <view class="n-cover" v-if="n.mindMapImage">
          <image :src="n.mindMapImage" mode="aspectFill" class="cover-img" />
        </view>
        <view class="n-body">
          <text class="n-title">{{ n.title || '未命名笔记' }}</text>
          <text class="n-preview">{{ (n.content||'').substring(0,50) }}</text>
          <text class="n-tag">{{ n.noteType===3?'OCR录入':'文本笔记' }}</text>
        </view>
      </view>
    </view>

    <view class="modal" v-if="showAdd">
      <view class="modal-content">
        <text class="m-title">新建笔记</text>
        <input v-model="form.title" placeholder="标题" class="m-input" />
        <textarea v-model="form.content" placeholder="内容" class="m-textarea" />
        <view class="m-btns">
          <button @click="showAdd=false">取消</button>
          <button class="primary" @click="createNote">创建</button>
        </view>
      </view>
    </view>

    <view class="modal" v-if="showDetail">
      <view class="modal-content">
        <text class="m-title">{{ currentNote?.title }}</text>
        <text class="detail-content">{{ currentNote?.content }}</text>
        <image v-if="currentNote?.mindMapImage" :src="currentNote.mindMapImage" mode="widthFix" class="mindmap" />
        <button class="primary mg-btn" @click="genMindMap">生成思维导图</button>
        <button @click="showDetail=false">关闭</button>
      </view>
    </view>
  </view>
</template>

<script>
const api = require('@/utils/api')
module.exports = {
  data() { return { notes:[], showAdd:false, showDetail:false, currentNote:null, form:{title:'',content:''} } },
  onShow() { this.loadNotes() },
  methods: {
    async loadNotes() { const res = await api.get('/note/list'); this.notes = res.data || [] },
    async createNote() {
      await api.post('/note', this.form); this.showAdd=false; this.form={title:'',content:''}; this.loadNotes()
      uni.showToast({title:'创建成功'})
    },
    viewNote(n) { this.currentNote = n; this.showDetail = true },
    async genMindMap() {
      const res = await api.post('/note/'+this.currentNote.id+'/mindmap')
      this.currentNote.mindMapImage = res.data.mindMapImage
      uni.showToast({title:'思维导图已生成'})
    }
  }
}
</script>

<style>
.notes { padding:20rpx; }
.header { display:flex; justify-content:space-between; align-items:center; margin-bottom:20rpx; }
.title { font-size:36rpx; font-weight:600; }
.add-btn { background:#667eea; color:#fff; font-size:26rpx; padding:10rpx 24rpx; border-radius:8rpx; }
.notes-grid { display:grid; grid-template-columns:1fr 1fr; gap:16rpx; }
.note-card { background:#fff; border-radius:16rpx; overflow:hidden; }
.n-cover { height:180rpx; background:#f5f7fa; }
.cover-img { width:100%; height:100%; }
.n-body { padding:16rpx; }
.n-title { font-size:28rpx; font-weight:600; color:#303133; }
.n-preview { font-size:24rpx; color:#909399; margin:8rpx 0; display:block; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.n-tag { font-size:22rpx; color:#667eea; background:#ecf0ff; padding:4rpx 12rpx; border-radius:6rpx; }
.modal { position:fixed; inset:0; background:rgba(0,0,0,0.5); display:flex; align-items:center; justify-content:center; z-index:100; padding:40rpx; }
.modal-content { background:#fff; border-radius:20rpx; padding:40rpx; width:100%; }
.m-title { font-size:34rpx; font-weight:600; margin-bottom:24rpx; display:block; }
.m-input { height:70rpx; border:2rpx solid #e4e7ed; border-radius:10rpx; padding:0 16rpx; margin-bottom:16rpx; }
.m-textarea { height:200rpx; border:2rpx solid #e4e7ed; border-radius:10rpx; padding:16rpx; margin-bottom:16rpx; }
.m-btns { display:flex; gap:16rpx; }
.m-btns button { flex:1; }
.primary { background:linear-gradient(135deg,#667eea,#764ba2); color:#fff; }
.detail-content { font-size:28rpx; color:#606266; line-height:1.8; margin:16rpx 0; white-space:pre-wrap; }
.mindmap { width:100%; border-radius:12rpx; margin:16rpx 0; }
.mg-btn { margin-bottom:16rpx; }
</style>
