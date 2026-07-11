<template>
  <div class="pk-page">
    <div class="pk-banner">
      <h2>⚔️ 刷题PK对战</h2>
      <el-button type="warning" size="large" @click="startMatch" :loading="matching" :icon="Sword">{{ matching ? '匹配中...' : '开始匹配' }}</el-button>
    </div>
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="pk-arena" v-if="battleStarted">
          <div class="arena-header">
            <div class="player me"><el-avatar :size="40">我</el-avatar><span>我的得分：{{ myScore }}</span></div>
            <div class="vs">VS</div>
            <div class="player opponent"><el-avatar :size="40">对</el-avatar><span>对手得分：{{ oppScore }}</span></div>
          </div>
          <div class="question-zone">
            <div class="timer">⏱ {{ timer }}s</div>
            <h3>第 {{ currentQ }} / {{ totalQ }} 题</h3>
            <p class="q-content">{{ currentQuestion?.content || '题目加载中...' }}</p>
            <div class="options">
              <div v-for="(opt, idx) in currentQuestion?.options || []" :key="idx" class="option-btn" @click="selectAnswer(idx)">{{ opt }}</div>
            </div>
          </div>
        </el-card>
        <el-empty v-else description="点击「开始匹配」进入对战" :image-size="120" />
      </el-col>
      <el-col :span="8">
        <el-card class="rank-card">
          <template #header><span>🏆 排行榜</span></template>
          <el-tabs v-model="rankTab">
            <el-tab-pane label="日榜" name="day"><div v-for="(r, i) in ranks" :key="i" class="rank-item"><span class="rank-num" :class="'top'+i">{{ i+1 }}</span><span>{{ r.name }}</span><span class="rank-pts">{{ r.pts }}分</span></div></el-tab-pane>
            <el-tab-pane label="周榜" name="week"><div v-for="(r, i) in ranks" :key="i" class="rank-item"><span class="rank-num" :class="'top'+i">{{ i+1 }}</span><span>{{ r.name }}</span><span class="rank-pts">{{ r.pts }}分</span></div></el-tab-pane>
            <el-tab-pane label="月榜" name="month"><div v-for="(r, i) in ranks" :key="i" class="rank-item"><span class="rank-num" :class="'top'+i">{{ i+1 }}</span><span>{{ r.name }}</span><span class="rank-pts">{{ r.pts }}分</span></div></el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '@/api'
import { Sword } from '@element-plus/icons-vue'

const matching = ref(false)
const battleStarted = ref(false)
const timer = ref(20)
const currentQ = ref(1)
const totalQ = ref(5)
const myScore = ref(0)
const oppScore = ref(0)
const currentQuestion = ref(null)
const rankTab = ref('day')

const ranks = [
  { name: '学习达人小王', pts: 980 },
  { name: '学霸小李', pts: 850 },
  { name: '刷题机器', pts: 720 },
  { name: '知识猎手', pts: 650 },
  { name: '逆袭选手', pts: 580 },
]

async function startMatch() {
  matching.value = true
  try {
    const res = await http.get('/question/pk/questions', { params: { stage: '初中' } })
    currentQuestion.value = res.data?.length > 0 ? res.data[0] : { content: '1+1=?', options: ['2', '3', '4', '5'] }
    battleStarted.value = true
    startTimer()
  } finally { matching.value = false }
}

function startTimer() {
  const interval = setInterval(() => {
    if (timer.value <= 0) { clearInterval(interval); nextQuestion() }
    else timer.value--
  }, 1000)
}

function selectAnswer(idx) {
  myScore.value += 20
  ElMessage.success('+20分！')
  nextQuestion()
}

function nextQuestion() {
  if (currentQ.value >= totalQ.value) {
    ElMessage.success('对战结束！你的得分：' + myScore.value)
    battleStarted.value = false
    return
  }
  currentQ.value++
  timer.value = 20
}
</script>

<style scoped>
.pk-page { max-width: 1000px; margin: 0 auto; }
.pk-banner { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); border-radius: 14px; padding: 28px; color: #fff; display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.pk-banner h2 { font-size: 22px; }
.pk-arena { border-radius: 14px; }
.arena-header { display: flex; align-items: center; justify-content: center; gap: 40px; margin-bottom: 20px; }
.player { text-align: center; display: flex; flex-direction: column; align-items: center; gap: 6px; font-size: 13px; }
.vs { font-size: 24px; font-weight: 700; color: #f5576c; }
.timer { text-align: center; font-size: 24px; color: #f56c6c; }
.question-zone { padding: 20px; }
.q-content { font-size: 16px; margin: 12px 0; color: #303133; }
.options { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.option-btn { padding: 14px; border: 2px solid #e4e7ed; border-radius: 10px; text-align: center; cursor: pointer; transition: all 0.2s; font-size: 15px; }
.option-btn:hover { border-color: #667eea; background: #ecf0ff; }
.rank-card { border-radius: 14px; height: 400px; }
.rank-item { display: flex; align-items: center; padding: 8px 0; border-bottom: 1px solid #f5f5f5; font-size: 14px; }
.rank-num { width: 24px; height: 24px; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-right: 10px; font-size: 12px; background: #f0f2f5; }
.rank-num.top0 { background: #ffd700; color: #fff; }
.rank-num.top1 { background: #c0c0c0; color: #fff; }
.rank-num.top2 { background: #cd7f32; color: #fff; }
.rank-pts { margin-left: auto; color: #909399; }
</style>
