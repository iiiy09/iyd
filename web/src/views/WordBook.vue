<template>
  <div class="word-page">
    <div class="page-header">
      <h2>🔤 英语单词背诵</h2>
      <div class="filters">
        <el-select v-model="wordStage" placeholder="词库" style="width:140px" @change="loadWords">
          <el-option label="中考词汇" value="中考" />
          <el-option label="高考词汇" value="高考" />
          <el-option label="四级词汇" value="四级" />
          <el-option label="六级词汇" value="六级" />
        </el-select>
      </div>
    </div>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="flashcard">
          <div v-if="loading" class="loading-state">
            <el-icon class="is-loading" :size="32"><Loading /></el-icon>
            <p>加载单词中...</p>
          </div>
          <template v-else-if="currentWord">
            <div class="card-front" v-if="!flipped">
              <span class="word">{{ currentWord.word }}</span>
              <span class="phonetic">{{ currentWord.phonetic }}</span>
            </div>
            <div class="card-back" v-else>
              <p class="meaning-title">释义</p>
              <p class="meaning">{{ currentWord.meaning }}</p>
              <p class="example" v-if="currentWord.example">📖 {{ currentWord.example }}</p>
            </div>
            <div class="card-actions">
              <el-button @click="flipped = !flipped" size="large" :icon="Switch">{{ flipped ? '显示单词' : '查看释义' }}</el-button>
              <div class="review-btns">
                <el-button type="danger" @click="review(false)">不认识</el-button>
                <el-button type="success" @click="review(true)">认识</el-button>
              </div>
            </div>
          </template>
          <el-empty v-else description="暂无单词数据" :image-size="80" />
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
          <el-progress :percentage="stats.percent" :stroke-width="12" color="#67c23a" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import http from '@/api'
import { ElMessage } from 'element-plus'
import { Switch, Loading } from '@element-plus/icons-vue'

const wordStage = ref('中考')
const flipped = ref(false)
const currentWord = ref(null)
const loading = ref(false)
const wordList = ref([])
let wordIndex = 0
const stats = reactive({ learned: 0, reviewing: 0, remaining: 0, percent: 0 })

onMounted(() => loadWords())

watch(wordStage, () => {
  wordIndex = 0
  currentWord.value = null
})

async function loadWords() {
  loading.value = true
  try {
    const res = await http.get('/recite/words', { params: { stage: wordStage.value } })
    const data = res.data || []
    wordList.value = data
    if (data.length > 0) {
      currentWord.value = data[0]
      wordIndex = 0
      updateStats(data)
    } else {
      currentWord.value = null
    }
  } catch (e) {
    ElMessage.error('加载单词失败')
  } finally {
    loading.value = false
  }
}

function updateStats(list) {
  const total = list.length
  const learned = list.filter(w => w.memoryStatus === 1).length
  const reviewing = list.filter(w => w.memoryStatus === 0 && w.reviewCount > 0).length
  const remaining = total - learned - reviewing
  stats.learned = learned
  stats.reviewing = reviewing
  stats.remaining = remaining
  stats.percent = total > 0 ? Math.round((learned + reviewing * 0.5) / total * 100) : 0
}

async function review(known) {
  if (!currentWord.value) return
  try {
    await http.post('/recite/word/' + currentWord.value.id + '/review?known=' + known)
    ElMessage.success(known ? '已掌握！' : '已加入复习')
    flipped.value = false
    wordIndex++
    if (wordIndex < wordList.value.length) {
      currentWord.value = wordList.value[wordIndex]
    } else {
      ElMessage.success('🎉 本轮单词全部背诵完成！')
      currentWord.value = null
    }
    updateStats(wordList.value)
  } catch (e) {
    ElMessage.error('操作失败')
  }
}
</script>

<style scoped>
.word-page { max-width: 1000px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; color: #303133; }
.flashcard { border-radius: 14px; text-align: center; padding: 30px; min-height: 300px; display: flex; flex-direction: column; align-items: center; justify-content: center; background: linear-gradient(135deg, #f5f7fa, #c3cfe2); }
.loading-state { text-align: center; color: #909399; }
.word { font-size: 40px; font-weight: 700; color: #303133; }
.phonetic { display: block; font-size: 16px; color: #909399; margin-top: 8px; }
.meaning-title { font-size: 14px; color: #909399; }
.meaning { font-size: 22px; font-weight: 600; color: #303133; margin: 10px 0; }
.example { font-size: 14px; color: #606266; background: #fff; padding: 10px; border-radius: 8px; margin-top: 12px; }
.card-actions { margin-top: 20px; display: flex; flex-direction: column; gap: 12px; width: 100%; }
.review-btns { display: flex; gap: 12px; justify-content: center; }
.progress-card { border-radius: 14px; margin-bottom: 16px; }
.progress-stats { display: flex; justify-content: space-around; margin-bottom: 16px; }
.p-stat { text-align: center; font-size: 13px; color: #909399; }
.p-num { display: block; font-size: 24px; font-weight: 700; margin-bottom: 4px; }
.p-num.green { color: #67c23a; }
.p-num.blue { color: #409eff; }
.p-num.gray { color: #909399; }
</style>