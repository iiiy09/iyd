<template>
  <div class="word-page">
    <div class="page-header">
      <h2>🔤 英语单词背诵</h2>
      <div class="filters">
        <el-select v-model="wordStage" placeholder="词库" style="width:140px">
          <el-option label="中考词汇" value="中考" /><el-option label="高考词汇" value="高考" /><el-option label="四级词汇" value="四级" /><el-option label="六级词汇" value="六级" />
        </el-select>
      </div>
    </div>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="flashcard">
          <div class="card-front" v-if="!flipped">
            <span class="word">{{ currentWord?.word || 'Loading' }}</span>
            <span class="phonetic">{{ currentWord?.phonetic }}</span>
          </div>
          <div class="card-back" v-else>
            <p class="meaning-title">释义</p>
            <p class="meaning">{{ currentWord?.meaning }}</p>
            <p class="example" v-if="currentWord?.example">📖 {{ currentWord.example }}</p>
          </div>
          <div class="card-actions">
            <el-button @click="flipped = !flipped" size="large" :icon="Switch">{{ flipped ? '显示单词' : '查看释义' }}</el-button>
            <div class="review-btns">
              <el-button type="danger" @click="review(false)">不认识</el-button>
              <el-button type="success" @click="review(true)">认识</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="progress-card">
          <template #header><span>📊 背诵进度</span></template>
          <div class="progress-stats">
            <div class="p-stat"><span class="p-num green">{{ stats.learned }}</span><span>已掌握</span></div>
            <div class="p-stat"><span class="p-num blue">{{ stats.reviewing }}</span><span>复习中</span></div>
            <div class="p-stat"><span class="p-num gray">{{ stats.remaining }}</span><span>待学习</span></div>
          </div>
          <el-progress :percentage="stats.percent" :stroke-width="12" :color="'#67c23a'" />
        </el-card>
        <el-card class="speech-card">
          <template #header><span>🎙️ 口语跟读评测</span></template>
          <div class="speech-area">
            <el-button type="primary" :icon="Microphone" circle size="large" @click="startSpeech" :loading="speaking" />
            <span class="speech-hint">点击麦克风，跟读单词发音</span>
          </div>
          <div v-if="speechResult" class="speech-result">
            <span>发音得分：{{ speechResult.score }}</span>
            <el-tag :type="speechResult.score > 80 ? 'success' : 'warning'" size="small">{{ speechResult.pronunciation }}</el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import http from '@/api'
import { ElMessage } from 'element-plus'
import { Switch, Microphone } from '@element-plus/icons-vue'

const wordStage = ref('中考')
const flipped = ref(false)
const currentWord = ref(null)
const speaking = ref(false)
const speechResult = ref(null)
const stats = reactive({ learned: 245, reviewing: 89, remaining: 2166, percent: 45 })

onMounted(() => loadWords())

async function loadWords() {
  const res = await http.get('/recite/words', { params: { stage: wordStage.value } })
  if (res.data?.length) currentWord.value = res.data[0]
}

async function review(known) {
  if (!currentWord.value) return
  await http.post('/recite/word/' + currentWord.value.id + '/review?known=' + known)
  ElMessage.success(known ? '已掌握！' : '已加入复习')
  flipped.value = false
  loadWords()
}

function startSpeech() {
  speaking.value = true
  setTimeout(() => {
    speaking.value = false
    speechResult.value = { score: 85.5, pronunciation: '良好' }
    ElMessage.success('评测完成')
  }, 2000)
}
</script>

<style scoped>
.word-page { max-width: 1000px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; color: #303133; }
.flashcard { border-radius: 14px; text-align: center; padding: 30px; min-height: 300px; display: flex; flex-direction: column; align-items: center; justify-content: center; background: linear-gradient(135deg, #f5f7fa, #c3cfe2); }
.word { font-size: 40px; font-weight: 700; color: #303133; }
.phonetic { display: block; font-size: 16px; color: #909399; margin-top: 8px; }
.meaning-title { font-size: 14px; color: #909399; }
.meaning { font-size: 22px; font-weight: 600; color: #303133; margin: 10px 0; }
.example { font-size: 14px; color: #606266; background: #fff; padding: 10px; border-radius: 8px; margin-top: 12px; }
.card-actions { margin-top: 20px; display: flex; flex-direction: column; gap: 12px; }
.review-btns { display: flex; gap: 12px; justify-content: center; }
.progress-card { border-radius: 14px; margin-bottom: 16px; }
.progress-stats { display: flex; justify-content: space-around; margin-bottom: 16px; }
.p-stat { text-align: center; font-size: 13px; color: #909399; }
.p-num { display: block; font-size: 24px; font-weight: 700; margin-bottom: 4px; }
.p-num.green { color: #67c23a; }
.p-num.blue { color: #409eff; }
.p-num.gray { color: #909399; }
.speech-card { border-radius: 14px; }
.speech-area { display: flex; flex-direction: column; align-items: center; gap: 12px; padding: 16px 0; }
.speech-hint { font-size: 13px; color: #909399; }
.speech-result { margin-top: 12px; text-align: center; display: flex; gap: 8px; align-items: center; justify-content: center; }
</style>
