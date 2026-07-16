<template>
  <div class="chat-page">
    <div class="chat-container">
      <div class="chat-sidebar">
        <div class="chat-logo">🤖 AI助手</div>
        <div class="quick-questions">
          <div class="qq-title">💡 快捷提问</div>
          <div v-for="q in quickQuestions" :key="q" class="qq-item" @click="sendQuick(q)">{{ q }}</div>
        </div>
      </div>
      <div class="chat-main">
        <div class="chat-messages" ref="msgContainer">
          <div v-for="(msg, idx) in messages" :key="idx" class="msg-row" :class="msg.role">
            <div class="msg-avatar">{{ msg.role === 'user' ? '👤' : '🤖' }}</div>
            <div class="msg-bubble" :class="msg.role">{{ msg.content }}</div>
          </div>
          <div v-if="typing" class="msg-row assistant">
            <div class="msg-avatar">🤖</div><div class="msg-bubble assistant typing">正在思考...</div>
          </div>
        </div>
        <div class="chat-input">
          <el-input v-model="inputText" type="textarea" :rows="2" placeholder="输入你的问题..." @keydown.enter.exact.prevent="sendMessage" />
          <el-button type="primary" @click="sendMessage" :loading="typing" :icon="Promotion">发送</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import http from '@/api'
import { Promotion } from '@element-plus/icons-vue'

const messages = ref([{ role: 'assistant', content: '👋 你好！我是iyd学习AI助手，你可以问我任何学习问题，我会为你详细解答！' }])
const inputText = ref('')
const typing = ref(false)
const msgContainer = ref(null)

const quickQuestions = [
  '如何提高数学解题速度？',
  '帮我制定英语学习计划',
  '什么是二次函数？',
  '推荐高效记忆方法',
  '中考物理重点有哪些？'
]

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text) return
  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  typing.value = true
  await scrollBottom()

  try {
    // 使用与AI刷题答疑相同的AI引擎（generateType=6）
    const res = await http.post('/ai/generate', {
      generateType: 6,
      inputContent: text,
      topic: ''
    })
    const answer = res.data.content || '抱歉，暂时无法回答该问题，请换个方式提问。'
    messages.value.push({ role: 'assistant', content: answer })
  } catch (e) {
    messages.value.push({ role: 'assistant', content: '抱歉，网络问题，请稍后重试。' })
  } finally {
    typing.value = false
    await scrollBottom()
  }
}

function sendQuick(q) { inputText.value = q; sendMessage() }

async function scrollBottom() {
  await nextTick()
  if (msgContainer.value) msgContainer.value.scrollTop = msgContainer.value.scrollHeight
}
</script>

<style scoped>
.chat-page { max-width: 900px; margin: 0 auto; height: calc(100vh - 120px); }
.chat-container { display: flex; height: 100%; border-radius: 14px; overflow: hidden; box-shadow: 0 4px 24px rgba(0,0,0,0.08); }
.chat-sidebar { width: 200px; background: #1d1e2c; padding: 20px 16px; display: flex; flex-direction: column; gap: 20px; }
.chat-logo { color: #fff; font-size: 18px; font-weight: 600; text-align: center; }
.qq-title { color: #8890b5; font-size: 13px; margin-bottom: 10px; }
.qq-item { color: #a8b0c8; font-size: 13px; padding: 8px 10px; border-radius: 8px; cursor: pointer; line-height: 1.5; }
.qq-item:hover { background: rgba(255,255,255,0.1); color: #fff; }
.chat-main { flex: 1; display: flex; flex-direction: column; background: #fff; }
.chat-messages { flex: 1; overflow-y: auto; padding: 20px; }
.msg-row { display: flex; gap: 10px; margin-bottom: 16px; }
.msg-row.user { flex-direction: row-reverse; }
.msg-avatar { font-size: 28px; width: 36px; flex-shrink: 0; text-align: center; }
.msg-bubble { max-width: 70%; padding: 12px 16px; border-radius: 14px; font-size: 14px; line-height: 1.7; white-space: pre-wrap; word-wrap: break-word; }
.msg-bubble.user { background: linear-gradient(135deg, #667eea, #764ba2); color: #fff; border-top-right-radius: 4px; }
.msg-bubble.assistant { background: #f0f2f5; color: #303133; border-top-left-radius: 4px; }
.msg-bubble.typing { color: #909399; font-style: italic; }
.chat-input { display: flex; gap: 10px; padding: 14px 20px; border-top: 1px solid #ebeef5; }
</style>
