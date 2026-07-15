<template>
  <div class="pk-page">
    <div class="pk-banner">
      <h2>⚔️ 刷题PK对战</h2>
      <div class="banner-info">
        <span class="rank-badge">🏆 {{ userRank }}段位</span>
        <span class="score-badge">⭐ {{ totalScore }}总积分</span>
      </div>
      <el-button type="warning" size="large" round @click="startMatch" :loading="matching" :disabled="battleStarted && timer > 0">
        {{ matching ? '匹配中...' : battleStarted ? '重新匹配' : '开始匹配' }}
      </el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="16">
        <!-- Battle Arena -->
        <el-card class="pk-arena" shadow="hover" v-if="battleStarted">
          <div class="arena-header">
            <div class="player me">
              <el-avatar :size="48" :style="{ backgroundColor: '#667eea' }">我</el-avatar>
              <div class="player-info">
                <span class="player-name">我</span>
                <span class="player-score">{{ myScore }}分</span>
              </div>
            </div>
            <div class="vs-section">
              <span class="vs-text">VS</span>
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
              </div>
              <span class="q-indicator">{{ currentQ }} / {{ totalQ }}</span>
            </div>
            <div class="player opponent">
              <el-avatar :size="48" :style="{ backgroundColor: '#f5576c' }">AI</el-avatar>
              <div class="player-info">
                <span class="player-name">{{ opponentName }}</span>
                <span class="player-score">{{ oppScore }}分</span>
              </div>
            </div>
          </div>

          <div class="question-zone">
            <div class="timer-ring" :class="{ urgent: timer <= 5 }">
              <svg class="timer-svg" viewBox="0 0 100 100">
                <circle cx="50" cy="50" r="42" fill="none" stroke="#e4e7ed" stroke-width="6"/>
                <circle cx="50" cy="50" r="42" fill="none" :stroke="timer <= 5 ? '#f56c6c' : '#667eea'" stroke-width="6"
                  stroke-linecap="round" :stroke-dasharray="2 * Math.PI * 42"
                  :stroke-dashoffset="2 * Math.PI * 42 * (1 - timer / 20)"
                  transform="rotate(-90 50 50)" style="transition: stroke-dashoffset 1s linear, stroke 0.3s"/>
              </svg>
              <div class="timer-inner">
                <span class="timer-num">{{ timer }}</span>
                <span class="timer-label">秒</span>
              </div>
            </div>

            <div class="q-header">
              <el-tag type="info" size="small">第{{ currentQ }}题</el-tag>
              <el-tag type="warning" size="small">{{ currentQuestion?.score || 20 }}分</el-tag>
              <el-tag v-if="currentQuestion?.subject" type="success" size="small">{{ currentQuestion.subject }}</el-tag>
            </div>

            <p class="q-content">{{ currentQuestion?.content || '题目加载中...' }}</p>

            <div class="options">
              <div
                v-for="(opt, idx) in currentQuestion?.options || []"
                :key="idx"
                class="option-btn"
                :class="{
                  selected: selectedAnswer === idx,
                  correct: answerRevealed && idx === currentQuestion?.answer,
                  wrong: answerRevealed && selectedAnswer === idx && idx !== currentQuestion?.answer
                }"
                @click="selectAnswer(idx)"
              >
                <span class="opt-letter">{{ ['A', 'B', 'C', 'D'][idx] }}</span>
                <span class="opt-text">{{ opt }}</span>
                <span v-if="answerRevealed && idx === currentQuestion?.answer" class="opt-icon">✓</span>
                <span v-if="answerRevealed && selectedAnswer === idx && idx !== currentQuestion?.answer" class="opt-icon wrong-icon">✗</span>
              </div>
            </div>

            <div v-if="answerRevealed" class="answer-feedback">
              <p v-if="selectedAnswer === currentQuestion?.answer" class="feedback-correct">🎉 回答正确！+{{ currentQuestion?.score || 20 }}分</p>
              <p v-else class="feedback-wrong">😞 回答错误！正确答案是 {{ ['A','B','C','D'][currentQuestion?.answer || 0] }}</p>
              <p class="feedback-tip">{{ currentQuestion?.tip || '' }}</p>
            </div>
          </div>
        </el-card>

        <!-- Empty State -->
        <el-card class="pk-empty" shadow="hover" v-else>
          <el-result icon="success" title="准备就绪" sub-title="点击上方【开始匹配】按钮，进入刷题PK对战！">
            <template #extra>
              <el-button type="primary" @click="startMatch" :loading="matching">开始匹配</el-button>
            </template>
          </el-result>
          <div class="rules-preview">
            <h4>📋 对战规则</h4>
            <ul>
              <li>每题限时<span class="highlight">20秒</span>，超时自动跳过</li>
              <li>答对得分，答错不扣分，共<span class="highlight">5题</span></li>
              <li>对手是AI机器人，同步模拟答题</li>
              <li>最终得分高者获胜，登上排行榜</li>
            </ul>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <!-- Battle Result -->
        <el-card class="result-card" shadow="hover" v-if="battleResult">
          <template #header><span>🏁 对战结果</span></template>
          <div class="result-content">
            <div class="result-icon">{{ battleResult.icon }}</div>
            <div class="result-title">{{ battleResult.title }}</div>
            <div class="result-scores">
              <div class="rs-item">
                <span class="rs-label">我的得分</span>
                <span class="rs-value me-val">{{ battleResult.myScore }}</span>
              </div>
              <div class="rs-item">
                <span class="rs-label">对手得分</span>
                <span class="rs-value opp-val">{{ battleResult.oppScore }}</span>
              </div>
            </div>
            <el-divider />
            <div class="result-stats">
              <div>✅ 正确：{{ battleResult.correctCount }}题</div>
              <div>❌ 错误：{{ battleResult.wrongCount }}题</div>
              <div>⏱️ 用时：{{ battleResult.totalTime }}秒</div>
            </div>
            <el-button type="primary" @click="startMatch" style="margin-top:12px;width:100%">再来一局</el-button>
          </div>
        </el-card>

        <!-- Ranking -->
        <el-card class="rank-card" shadow="hover" v-else>
          <template #header>
            <div class="rank-header">
              <span>🏆 排行榜</span>
              <el-switch v-model="rankTabIsDay" active-text="日榜" inactive-text="周榜" size="small" />
            </div>
          </template>
          <div v-if="rankData.length === 0" class="rank-empty">
            <p>暂无排行数据</p>
            <p>快去PK获取排名吧！</p>
          </div>
          <div v-for="(r, i) in rankData" :key="i" class="rank-item">
            <span class="rank-num" :class="'top' + i">{{ i + 1 }}</span>
            <el-avatar :size="30" :style="{ backgroundColor: i === 0 ? '#ffd700' : i === 1 ? '#c0c0c0' : i === 2 ? '#cd7f32' : '#e4e7ed' }">
              {{ r.name.charAt(0) }}
            </el-avatar>
            <span class="rank-name">{{ r.name }}</span>
            <span class="rank-pts">{{ r.pts }}分</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onBeforeUnmount } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'

const matching = ref(false)
const battleStarted = ref(false)
const timer = ref(20)
const currentQ = ref(1)
const totalQ = ref(5)
const myScore = ref(0)
const oppScore = ref(0)
const currentQuestion = ref(null)
const selectedAnswer = ref(-1)
const answerRevealed = ref(false)
const battleResult = ref(null)
const rankTabIsDay = ref(true)
const correctCount = ref(0)
const wrongCount = ref(0)
const totalTime = ref(0)
let timerInterval = null
let oppInterval = null
let questionStartTime = 0

// Rich question bank covering multiple subjects
const questionBank = [
  {
    id: 1,
    content: '光合作用的主要产物是什么？',
    options: ['氧气', '氮气', '二氧化碳', '氢气'],
    score: 20,
    answer: 0,
    subject: '生物',
    tip: '光合作用利用光能，将二氧化碳和水转化为有机物并释放氧气。'
  },
  {
    id: 2,
    content: '中国最长的河流是哪一条？',
    options: ['黄河', '长江', '珠江', '淮河'],
    score: 20,
    answer: 1,
    subject: '地理',
    tip: '长江全长约6300千米，是中国第一长河，世界第三长河。'
  },
  {
    id: 3,
    content: '圆的面积公式是什么？',
    options: ['πr', '2πr', 'πr²', '2πr²'],
    score: 20,
    answer: 2,
    subject: '数学',
    tip: 'S = πr²，其中 r 为圆的半径，π 约等于3.14159。'
  },
  {
    id: 4,
    content: '"床前明月光"的作者是谁？',
    options: ['杜甫', '白居易', '李白', '王维'],
    score: 20,
    answer: 2,
    subject: '语文',
    tip: '这首诗是李白的《静夜思》，表达了对故乡的思念之情。'
  },
  {
    id: 5,
    content: '水的化学式是什么？',
    options: ['H₂O', 'CO₂', 'O₂', 'NaCl'],
    score: 20,
    answer: 0,
    subject: '化学',
    tip: '水由两个氢原子和一个氧原子组成，化学式为H₂O。'
  },
  {
    id: 6,
    content: '地球绕太阳一周大约需要多长时间？',
    options: ['一个月', '一年', '一天', '十年'],
    score: 20,
    answer: 1,
    subject: '地理',
    tip: '地球的公转周期约为365.25天，即一年。'
  },
  {
    id: 7,
    content: '勾股定理中，直角三角形的斜边平方等于什么？',
    options: ['两直角边之和', '两直角边之积', '两直角边平方和', '两直角边平方差'],
    score: 20,
    answer: 2,
    subject: '数学',
    tip: '勾股定理：a² + b² = c²，其中c为斜边。'
  },
  {
    id: 8,
    content: '"春蚕到死丝方尽"的下一句是什么？',
    options: ['蜡炬成灰泪始干', '化作春泥更护花', '不破楼兰终不还', '一片冰心在玉壶'],
    score: 20,
    answer: 0,
    subject: '语文',
    tip: '出自李商隐《无题》，下一句是"蜡炬成灰泪始干"。'
  },
  {
    id: 9,
    content: '牛顿第一定律又称为什么定律？',
    options: ['万有引力定律', '惯性定律', '加速度定律', '作用力与反作用力定律'],
    score: 20,
    answer: 1,
    subject: '物理',
    tip: '牛顿第一定律：一切物体在不受外力作用时，总保持匀速直线运动状态或静止状态。'
  },
  {
    id: 10,
    content: 'Which of the following is the past tense of "go"?',
    options: ['goed', 'went', 'gone', 'going'],
    score: 20,
    answer: 1,
    subject: '英语',
    tip: '"go"的过去式是"went"，这是一个不规则变化。'
  },
  {
    id: 11,
    content: '人体的正常体温大约是多少摄氏度？',
    options: ['35°C', '36.5°C', '38°C', '40°C'],
    score: 20,
    answer: 1,
    subject: '生物',
    tip: '人体正常体温约为36.0°C - 37.0°C，平均36.5°C左右。'
  },
  {
    id: 12,
    content: '世界上最大的海洋是哪一个？',
    options: ['大西洋', '印度洋', '太平洋', '北冰洋'],
    score: 20,
    answer: 2,
    subject: '地理',
    tip: '太平洋面积约1.65亿平方公里，占地球海洋总面积的46%。'
  },
  {
    id: 13,
    content: '化学元素周期表中，Fe代表什么元素？',
    options: ['氟', '铁', '锌', '铜'],
    score: 20,
    answer: 1,
    subject: '化学',
    tip: 'Fe来自拉丁文Ferrum，是铁的元素符号，原子序数26。'
  },
  {
    id: 14,
    content: '抗日战争全面爆发的标志性事件是什么？',
    options: ['九一八事变', '七七事变（卢沟桥事变）', '西安事变', '南京大屠杀'],
    score: 20,
    answer: 1,
    subject: '历史',
    tip: '1937年7月7日卢沟桥事变，标志着全面抗日战争的开始。'
  },
  {
    id: 15,
    content: '在C语言中，int类型通常占用几个字节？',
    options: ['1字节', '2字节', '4字节', '8字节'],
    score: 20,
    answer: 2,
    subject: '计算机',
    tip: '在大多数现代32/64位系统中，int类型占用4个字节（32位）。'
  },
]

const opponentNames = ['学霸小李', '刷题达人小王', '知识猎手', '逆袭选手', '学习机器']
const opponentName = ref(opponentNames[Math.floor(Math.random() * opponentNames.length)])
const userRank = ref('青铜')
const totalScore = ref(0)

const rankData = ref([
  { name: '学霸小李', pts: 980 },
  { name: '学习达人小王', pts: 850 },
  { name: '刷题机器', pts: 720 },
  { name: '知识猎手', pts: 650 },
  { name: '逆袭选手', pts: 580 },
])

const progressPercent = computed(() => {
  return ((currentQ.value - 1) / totalQ.value) * 100
})

function shuffleArray(arr) {
  const a = [...arr]
  for (let i = a.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [a[i], a[j]] = [a[j], a[i]]
  }
  return a
}

async function startMatch() {
  if (matching.value) return

  matching.value = true
  battleResult.value = null
  battleStarted.value = false
  answerRevealed.value = false

  try {
    // Try API first
    let questions = []
    try {
      const res = await fetch('/api/question/pk/questions')
      const json = await res.json()
      if (json.code === 200 && json.data && json.data.length > 0) {
        questions = json.data
      }
    } catch (e) {
      // Fallback to built-in bank
    }

    // Use built-in bank if API returns empty or fails
    if (!questions || questions.length === 0) {
      questions = shuffleArray(questionBank).slice(0, 5)
    }

    // Ensure options are in correct format
    questions = questions.map((q, idx) => ({
      ...q,
      id: q.id || idx + 1,
      options: q.options || ['A', 'B', 'C', 'D'].map(l => l + '. 选项' + l),
      score: q.score || 20,
      subject: q.subject || '通用',
      tip: q.tip || '',
    }))

    currentQ.value = 1
    totalQ.value = questions.length
    currentQuestion.value = questions[0]
    myScore.value = 0
    oppScore.value = 0
    correctCount.value = 0
    wrongCount.value = 0
    totalTime.value = 0
    battleStarted.value = true
    selectedAnswer.value = -1
    answerRevealed.value = false
    opponentName.value = opponentNames[Math.floor(Math.random() * opponentNames.length)]

    console.log('PK Battle started with questions:', questions)
    startTimer()
    simulateOpponent()
  } catch (err) {
    ElMessage.error('匹配失败，请重试')
    console.error('Match error:', err)
  } finally {
    matching.value = false
  }
}

function startTimer() {
  clearTimer()
  timer.value = 20
  questionStartTime = Date.now()
  answerRevealed.value = false
  selectedAnswer.value = -1

  timerInterval = setInterval(() => {
    if (timer.value <= 1) {
      clearTimer()
      ElMessage.warning('时间到！自动进入下一题')
      handleTimeout()
    } else {
      timer.value--
    }
  }, 1000)
}

function clearTimer() {
  if (timerInterval) {
    clearInterval(timerInterval)
    timerInterval = null
  }
}

function selectAnswer(idx) {
  if (selectedAnswer.value >= 0 || answerRevealed.value) return

  selectedAnswer.value = idx
  clearTimer()
  answerRevealed.value = true

  const elapsed = Math.round((Date.now() - questionStartTime) / 1000)
  totalTime.value += Math.min(elapsed, 20)

  const q = currentQuestion.value
  const isCorrect = q.answer !== undefined ? idx === q.answer : Math.random() > 0.3

  if (isCorrect) {
    myScore.value += q.score || 20
    correctCount.value++
    ElMessage({ message: `✅ 正确！+${q.score || 20}分`, type: 'success', duration: 1200 })
  } else {
    wrongCount.value++
    ElMessage({ message: '❌ 回答错误', type: 'error', duration: 1200 })
  }

  setTimeout(() => nextQuestion(), 1500)
}

function handleTimeout() {
  answerRevealed.value = true
  totalTime.value += 20
  wrongCount.value++
  ElMessage({ message: '⏰ 时间到！', type: 'warning', duration: 1200 })
  setTimeout(() => nextQuestion(), 1500)
}

function simulateOpponent() {
  if (oppInterval) clearInterval(oppInterval)
  oppInterval = setInterval(() => {
    if (!battleStarted.value) {
      clearInterval(oppInterval)
      oppInterval = null
      return
    }
    oppScore.value += Math.floor(Math.random() * 15) + 5
  }, 3000)
}

function nextQuestion() {
  selectedAnswer.value = -1
  answerRevealed.value = false

  if (currentQ.value >= totalQ.value) {
    // Battle ended
    clearTimer()
    if (oppInterval) { clearInterval(oppInterval); oppInterval = null }
    battleStarted.value = false

    const icon = myScore.value > oppScore.value ? '🎉' : myScore.value === oppScore.value ? '🤝' : '💪'
    const title = myScore.value > oppScore.value ? '恭喜获胜！' : myScore.value === oppScore.value ? '平局！' : '再接再厉！'

    battleResult.value = {
      icon,
      title,
      myScore: myScore.value,
      oppScore: oppScore.value,
      correctCount: correctCount.value,
      wrongCount: wrongCount.value,
      totalTime: totalTime.value,
    }

    const type = myScore.value >= oppScore.value ? 'success' : 'warning'
    ElNotification({
      title: '对战结束',
      message: `${title} 你的得分：${myScore.value} vs ${oppScore.value}`,
      type,
      duration: 5000,
    })

    // Update rank
    totalScore.value += myScore.value
    updateRank()
    return
  }

  currentQ.value++
  currentQuestion.value = getCurrentQuestion()
  startTimer()
}

function getCurrentQuestion() {
  const bank = questionBank.length >= currentQ.value ? questionBank : questionBank
  return bank[currentQ.value - 1] || questionBank[0]
}

function updateRank() {
  if (totalScore.value >= 2000) userRank.value = '王者'
  else if (totalScore.value >= 1000) userRank.value = '钻石'
  else if (totalScore.value >= 500) userRank.value = '黄金'
  else if (totalScore.value >= 200) userRank.value = '白银'
  else if (totalScore.value >= 50) userRank.value = '青铜'
}

onBeforeUnmount(() => {
  clearTimer()
  if (oppInterval) clearInterval(oppInterval)
})
</script>

<style scoped>
.pk-page {
  max-width: 1050px;
  margin: 0 auto;
  padding: 0 10px;
}

.pk-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 30px 36px;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.pk-banner h2 {
  font-size: 24px;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.banner-info {
  display: flex;
  gap: 16px;
}

.rank-badge, .score-badge {
  background: rgba(255,255,255,0.2);
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  backdrop-filter: blur(4px);
}

/* Arena */
.pk-arena {
  border-radius: 16px;
  min-height: 500px;
}

.arena-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 30px;
  margin-bottom: 24px;
  padding: 16px;
  background: #f8f9ff;
  border-radius: 12px;
}

.player {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.player-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.player-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.player-score {
  font-size: 22px;
  font-weight: 700;
  color: #667eea;
}

.vs-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.vs-text {
  font-size: 28px;
  font-weight: 800;
  color: #f5576c;
  text-shadow: 0 2px 8px rgba(245, 87, 108, 0.3);
}

.progress-bar {
  width: 120px;
  height: 4px;
  background: #e4e7ed;
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #f5576c);
  transition: width 0.3s;
}

.q-indicator {
  font-size: 12px;
  color: #909399;
}

/* Question Zone */
.question-zone {
  padding: 10px 20px 20px;
  text-align: center;
}

.timer-ring {
  width: 80px;
  height: 80px;
  position: relative;
  margin: 0 auto 16px;
}

.timer-svg {
  width: 100%;
  height: 100%;
}

.timer-inner {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  line-height: 1;
}

.timer-num {
  font-size: 24px;
  font-weight: 800;
  color: #303133;
}

.timer-ring.urgent .timer-num {
  color: #f56c6c;
  animation: pulse 0.5s infinite;
}

.timer-label {
  font-size: 11px;
  color: #909399;
}

@keyframes pulse {
  50% { transform: translate(-50%, -50%) scale(1.15); }
}

.q-header {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 14px;
}

.q-content {
  font-size: 18px;
  margin: 16px 0 20px;
  color: #303133;
  font-weight: 500;
  line-height: 1.7;
}

/* Options */
.options {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  max-width: 600px;
  margin: 0 auto;
}

.option-btn {
  padding: 14px 18px;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
}

.option-btn:hover:not(.correct):not(.wrong) {
  border-color: #667eea;
  background: #f0f3ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.option-btn.selected {
  border-color: #667eea;
  background: #e8ecff;
}

.option-btn.correct {
  border-color: #67c23a;
  background: #f0f9eb;
}

.option-btn.wrong {
  border-color: #f56c6c;
  background: #fef0f0;
}

.opt-letter {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: #667eea;
  flex-shrink: 0;
}

.option-btn.correct .opt-letter {
  background: #67c23a;
  color: #fff;
}

.option-btn.wrong .opt-letter {
  background: #f56c6c;
  color: #fff;
}

.opt-text {
  color: #303133;
  flex: 1;
  text-align: left;
}

.opt-icon {
  font-size: 18px;
  font-weight: bold;
  color: #67c23a;
}

.opt-icon.wrong-icon {
  color: #f56c6c;
}

/* Answer feedback */
.answer-feedback {
  margin-top: 16px;
  padding: 12px;
  border-radius: 8px;
  background: #f8f9ff;
}

.feedback-correct {
  color: #67c23a;
  font-weight: 600;
  font-size: 15px;
  margin: 0;
}

.feedback-wrong {
  color: #f56c6c;
  font-weight: 600;
  font-size: 15px;
  margin: 0;
}

.feedback-tip {
  color: #909399;
  font-size: 13px;
  margin: 6px 0 0;
}

/* Empty state */
.pk-empty {
  border-radius: 16px;
  text-align: center;
}

.rules-preview {
  text-align: left;
  padding: 16px 40px;
  background: #f8f9ff;
  border-radius: 8px;
  margin-top: 12px;
}

.rules-preview h4 {
  margin: 0 0 8px;
  color: #303133;
}

.rules-preview ul {
  margin: 0;
  padding-left: 20px;
  color: #606266;
  font-size: 14px;
}

.rules-preview li {
  margin: 4px 0;
}

.highlight {
  color: #667eea;
  font-weight: 600;
}

/* Result card */
.result-card {
  border-radius: 16px;
}

.result-content {
  text-align: center;
}

.result-icon {
  font-size: 56px;
  margin-bottom: 8px;
}

.result-title {
  font-size: 20px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 16px;
}

.result-scores {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 8px;
}

.rs-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.rs-label {
  font-size: 12px;
  color: #909399;
}

.rs-value {
  font-size: 28px;
  font-weight: 800;
}

.me-val {
  color: #667eea;
}

.opp-val {
  color: #f5576c;
}

.result-stats {
  font-size: 14px;
  color: #606266;
  display: flex;
  justify-content: center;
  gap: 16px;
}

/* Rank card */
.rank-card {
  border-radius: 16px;
}

.rank-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rank-empty {
  text-align: center;
  padding: 30px;
  color: #909399;
}

.rank-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
  font-size: 14px;
  gap: 10px;
}

.rank-num {
  width: 26px;
  height: 26px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  background: #f0f2f5;
  color: #909399;
  flex-shrink: 0;
}

.rank-num.top0 {
  background: #ffd700;
  color: #fff;
}

.rank-num.top1 {
  background: #c0c0c0;
  color: #fff;
}

.rank-num.top2 {
  background: #cd7f32;
  color: #fff;
}

.rank-name {
  flex: 1;
  color: #303133;
  font-weight: 500;
}

.rank-pts {
  color: #667eea;
  font-weight: 700;
  font-size: 15px;
}
</style>