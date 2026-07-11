<template>
  <view class="ai">
    <view class="tab-bar">
      <view :class="{active:tab==='essay'}" @click="tab='essay'">作文文案</view>
      <view :class="{active:tab==='ppt'}" @click="tab='ppt'">PPT大纲</view>
      <view :class="{active:tab==='chat'}" @click="tab='chat'">AI助手</view>
    </view>
    <view v-if="tab==='essay'">
      <input v-model="topic" placeholder="创作主题" class="ai-input" />
      <textarea v-model="req" placeholder="要求（可选）" class="ai-textarea" />
      <button class="gen-btn" @click="generate(1)" :loading="loading">AI智能创作</button>
      <view class="ai-result" v-if="result"><text>{{result}}</text></view>
    </view>
    <view v-else-if="tab==='ppt'">
      <input v-model="topic" placeholder="PPT主题" class="ai-input" />
      <textarea v-model="req" placeholder="核心知识点（可选）" class="ai-textarea" />
      <button class="gen-btn" @click="generatePpt" :loading="loading">生成PPT大纲</button>
      <view class="ppt-list" v-if="pptSlides.length">
        <view class="ppt-item" v-for="s in pptSlides" :key="s.page"><text class="p-num">P{{s.page}}</text><text>{{s.title}}</text></view>
      </view>
    </view>
    <view v-else class="chat">
      <scroll-view class="chat-msgs" scroll-y>
        <view v-for="(m,i) in msgs" :key="i" :class="'msg '+m.role">
          <text>{{m.content}}</text>
        </view>
      </scroll-view>
      <view class="chat-input-row">
        <input v-model="chatText" placeholder="输入问题..." class="chat-input" />
        <button class="send-btn" @click="sendChat">发送</button>
      </view>
    </view>
  </view>
</template>

<script>
const api = require('@/utils/api')
module.exports = {
  data() { return { tab:'essay', topic:'', req:'', result:'', pptSlides:[], msgs:[{role:'assistant',content:'你好！我是AI助手👋'}], chatText:'', loading:false } },
  methods: {
    async generate(type) {
      if(!this.topic) return uni.showToast({title:'请输入主题',icon:'none'})
      this.loading=true
      try { const res=await api.post('/ai/generate',{generateType:type,inputContent:this.req,topic:this.topic}); this.result=res.data.content }
      finally { this.loading=false }
    },
    async generatePpt() {
      if(!this.topic) return uni.showToast({title:'请输入主题',icon:'none'})
      this.loading=true
      try { const res=await api.post('/ai/ppt',null,{params:{topic:this.topic,points:this.req}}); this.pptSlides=res.data.slides }
      finally { this.loading=false }
    },
    async sendChat() {
      if(!this.chatText.trim()) return
      this.msgs.push({role:'user',content:this.chatText})
      const text=this.chatText; this.chatText=''
      try { const res=await api.post('/ai/chat',null,{params:{question:text}}); this.msgs.push({role:'assistant',content:res.data.answer}) }
      catch(e) { this.msgs.push({role:'assistant',content:'抱歉，请稍后重试'}) }
    }
  }
}
</script>

<style>
.ai { padding:20rpx; }
.tab-bar { display:flex; background:#fff; border-radius:16rpx; overflow:hidden; margin-bottom:20rpx; }
.tab-bar view { flex:1; text-align:center; padding:20rpx; font-size:28rpx; color:#909399; }
.tab-bar view.active { background:#667eea; color:#fff; }
.ai-input { height:80rpx; border:2rpx solid #e4e7ed; border-radius:12rpx; padding:0 20rpx; margin-bottom:16rpx; background:#fff; }
.ai-textarea { height:160rpx; border:2rpx solid #e4e7ed; border-radius:12rpx; padding:16rpx; margin-bottom:16rpx; background:#fff; }
.gen-btn { width:100%; height:88rpx; background:linear-gradient(135deg,#667eea,#764ba2); color:#fff; border-radius:12rpx; font-size:30rpx; }
.ai-result { background:#fff; border-radius:16rpx; padding:24rpx; margin-top:20rpx; font-size:28rpx; line-height:1.8; }
.ppt-list { margin-top:20rpx; }
.ppt-item { background:#fff; border-radius:12rpx; padding:20rpx; margin-bottom:12rpx; display:flex; gap:16rpx; font-size:26rpx; }
.p-num { color:#667eea; font-weight:700; }
.chat-msgs { height:600rpx; background:#fff; border-radius:16rpx; padding:20rpx; margin-bottom:16rpx; }
.msg { margin-bottom:16rpx; padding:14rpx 20rpx; border-radius:14rpx; font-size:28rpx; max-width:80%; }
.msg.user { background:linear-gradient(135deg,#667eea,#764ba2); color:#fff; margin-left:auto; }
.msg.assistant { background:#f0f2f5; color:#303133; }
.chat-input-row { display:flex; gap:12rpx; }
.chat-input { flex:1; height:70rpx; border:2rpx solid #e4e7ed; border-radius:12rpx; padding:0 16rpx; background:#fff; }
.send-btn { background:#667eea; color:#fff; border-radius:12rpx; font-size:26rpx; padding:0 30rpx; }
</style>
