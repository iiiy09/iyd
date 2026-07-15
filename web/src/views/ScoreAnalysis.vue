<template>
  <div class="score-page">
    <div class="page-header">
      <h2>📊 成绩智能分析</h2>
      <el-button type="primary" @click="loadReports">刷新历史</el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="10">
        <el-card class="input-card">
          <template #header>
            <div class="card-header">
              <span>📝 录入成绩</span>
              <el-radio-group v-model="inputMode" size="small">
                <el-radio-button value="manual">手动输入</el-radio-button>
                <el-radio-button value="upload">上传文件</el-radio-button>
              </el-radio-group>
            </div>
          </template>

          <!-- Manual Input Mode -->
          <div v-if="inputMode === 'manual'">
            <div class="form-grid">
              <div v-for="sub in subjects" :key="sub.key" class="form-item">
                <label>{{ sub.label }}</label>
                <el-input v-model="form[sub.key]" placeholder="0-100" maxlength="3" clearable />
              </div>
            </div>
            <div class="form-actions">
              <el-button type="primary" size="large" :loading="submitting" @click="submitAndAnalyze" style="width:100%">
                提交并智能分析
              </el-button>
            </div>
          </div>

          <!-- Upload Mode -->
          <div v-else>
            <el-upload drag :auto-upload="false" accept=".xlsx,.xls" class="score-upload">
              <el-icon :size="48"><UploadFilled /></el-icon>
              <div>点击或拖拽Excel成绩文件</div>
              <div class="upload-hint">支持.xlsx/.xls格式</div>
            </el-upload>
            <el-button type="primary" size="large" @click="uploadScore" style="width:100%;margin-top:16px">
              上传并分析
            </el-button>
          </div>
        </el-card>

        <!-- Report History -->
        <el-card class="history-card" style="margin-top:16px">
          <template #header><span>📋 历史分析报告</span></template>
          <div v-if="reportList.length === 0" class="empty-hint">暂无历史报告</div>
          <div v-for="r in reportList" :key="r.id" class="history-item" :class="{ active: currentReport?.id === r.id }" @click="loadReport(r.id)">
            <div class="history-info">
              <span class="history-date">{{ formatDate(r.createTime) }}</span>
              <el-tag size="small" :type="r.originalFile === 'manual_input' ? '' : 'success'" effect="plain">
                {{ r.originalFile === 'manual_input' ? '手动' : '上传' }}
              </el-tag>
            </div>
            <el-icon><ArrowRight /></el-icon>
          </div>
        </el-card>
      </el-col>

      <el-col :span="14">
        <el-card v-if="currentReport" class="report-card">
          <template #header>
            <span class="report-title">📋 分析报告 #{{ currentReport.id }}</span>
          </template>

          <!-- Score Grid -->
          <div class="score-grid">
            <div v-for="(val, key) in scoreMap" :key="key" class="score-item" :class="getScoreLevel(val)">
              <span class="subject-name">{{ key }}</span>
              <span class="subject-score">{{ val || '-' }}</span>
            </div>
          </div>

          <el-divider />

          <!-- ECharts -->
          <div class="charts-row">
            <div class="chart-box">
              <div ref="radarChartRef" style="height:280px"></div>
            </div>
            <div class="chart-box">
              <div ref="barChartRef" style="height:280px"></div>
            </div>
          </div>

          <el-divider />

          <!-- AI Analysis Report -->
          <el-tabs v-model="activeTab">
            <el-tab-pane label="学情分析报告" name="report">
              <div class="ai-report" v-html="formattedReport"></div>
            </el-tab-pane>
            <el-tab-pane label="学习计划" name="plan">
              <div class="ai-report" v-html="formattedPlan"></div>
            </el-tab-pane>
          </el-tabs>
        </el-card>

        <el-empty v-else description="请录入成绩并点击分析" :image-size="100" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import http from '@/api'
import { ElMessage } from 'element-plus'
import { UploadFilled, ArrowRight } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const inputMode = ref('manual')
const submitting = ref(false)
const activeTab = ref('report')
const currentReport = ref(null)
const reportList = ref([])
const radarChartRef = ref(null)
const barChartRef = ref(null)
let radarChart = null
let barChart = null

const subjects = [
  { key: 'chinese', label: '语文' },
  { key: 'math', label: '数学' },
  { key: 'english', label: '英语' },
  { key: 'physics', label: '物理' },
  { key: 'chemistry', label: '化学' },
  { key: 'biology', label: '生物' },
  { key: 'history', label: '历史' },
  { key: 'geography', label: '地理' },
  { key: 'politics', label: '政治' },
]

const form = ref({})
subjects.forEach(s => form.value[s.key] = '')

const scoreMap = computed(() => {
  if (!currentReport.value) return {}
  const r = currentReport.value
  return {
    语文: r.chineseScore, 数学: r.mathScore, 英语: r.englishScore,
    物理: r.physicsScore, 化学: r.chemistryScore, 生物: r.biologyScore,
    历史: r.historyScore, 地理: r.geographyScore, 政治: r.politicsScore
  }
})

const formattedReport = computed(() => {
  return currentReport.value?.aiReport?.replace(/\n/g, '<br>') || ''
})

const formattedPlan = computed(() => {
  return currentReport.value?.studyPlan?.replace(/\n/g, '<br>') || ''
})

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getMonth()+1}/${d.getDate()} ${d.getHours().toString().padStart(2,'0')}:${d.getMinutes().toString().padStart(2,'0')}`
}

function getScoreLevel(val) {
  const n = parseInt(val)
  if (isNaN(n)) return 'level-none'
  if (n >= 90) return 'level-high'
  if (n >= 70) return 'level-mid'
  if (n >= 60) return 'level-pass'
  return 'level-low'
}

function getChartData(report) {
  if (!report) return { labels: [], values: [] }
  const labels = []
  const values = []
  const map = {
    chineseScore: '语文', mathScore: '数学', englishScore: '英语',
    physicsScore: '物理', chemistryScore: '化学', biologyScore: '生物',
    historyScore: '历史', geographyScore: '地理', politicsScore: '政治'
  }
  for (const [key, label] of Object.entries(map)) {
    const v = parseInt(report[key])
    if (!isNaN(v) && v > 0) {
      labels.push(label)
      values.push(v)
    }
  }
  return { labels, values }
}

function renderCharts(report) {
  nextTick(() => {
    if (radarChart) radarChart.dispose()
    if (barChart) barChart.dispose()

    const { labels, values } = getChartData(report)
    if (labels.length === 0) return

    // Radar Chart
    if (radarChartRef.value) {
      radarChart = echarts.init(radarChartRef.value)
      radarChart.setOption({
        title: { text: '各科成绩雷达图', left: 'center', textStyle: { fontSize: 14 } },
        tooltip: {},
        radar: {
          indicator: labels.map(l => ({ name: l, max: 100 })),
          shape: 'polygon',
          splitNumber: 5,
          axisName: { color: '#606266', fontSize: 11 }
        },
        series: [{
          type: 'radar',
          data: [{ value: values, name: '成绩', areaStyle: { color: 'rgba(102,126,234,0.3)' }, lineStyle: { color: '#667eea', width: 2 }, itemStyle: { color: '#667eea' } }]
        }]
      })
      radarChart.resize()
    }

    // Bar Chart
    if (barChartRef.value) {
      barChart = echarts.init(barChartRef.value)
      const colors = values.map(v => {
        if (v >= 90) return '#67c23a'
        if (v >= 70) return '#e6a23c'
        if (v >= 60) return '#909399'
        return '#f56c6c'
      })
      barChart.setOption({
        title: { text: '各科成绩对比', left: 'center', textStyle: { fontSize: 14 } },
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: labels, axisLabel: { fontSize: 11 } },
        yAxis: { type: 'value', min: 0, max: 100, splitLine: { lineStyle: { type: 'dashed' } } },
        series: [{
          type: 'bar',
          data: values.map((v, i) => ({ value: v, itemStyle: { color: colors[i], borderRadius: [6,6,0,0] } })),
          barWidth: '45%',
          label: { show: true, position: 'top', fontWeight: 'bold', fontSize: 12 }
        }]
      })
      barChart.resize()
    }
  })
}

watch(currentReport, (val) => {
  if (val) renderCharts(val)
})

async function submitAndAnalyze() {
  // Validate
  const scores = {}
  let hasScore = false
  for (const sub of subjects) {
    const v = form.value[sub.key]
    if (v && v.trim()) {
      const n = parseInt(v)
      if (isNaN(n) || n < 0 || n > 100) {
        ElMessage.warning(`${sub.label}成绩请输入0-100之间的数字`)
        return
      }
      scores[sub.key] = v.trim()
      hasScore = true
    }
  }
  if (!hasScore) {
    ElMessage.warning('请至少输入一个科目的成绩')
    return
  }

  submitting.value = true
  try {
    const saveRes = await http.post('/score/manual', scores)
    const reportId = saveRes.data.id
    const analyzeRes = await http.post(`/score/${reportId}/analyze`)
    currentReport.value = analyzeRes.data
    ElMessage.success('成绩分析完成！')
    await loadReports()
  } catch (e) {
    ElMessage.error('分析失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

async function uploadScore() {
  ElMessage.info('文件上传功能开发中，请使用手动输入模式')
}

async function loadReports() {
  try {
    const res = await http.get('/score/reports')
    reportList.value = res.data || []
  } catch (e) { /* ignore */ }
}

async function loadReport(id) {
  try {
    const res = await http.get(`/score/report/${id}`)
    currentReport.value = res.data
  } catch (e) {
    ElMessage.error('加载报告失败')
  }
}

onMounted(() => {
  loadReports()
})
</script>

<style scoped>
.score-page { max-width: 1200px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { font-size: 20px; margin: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }

.input-card { border-radius: 14px; }
.form-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; margin-bottom: 16px; }
.form-item label { display: block; font-size: 13px; color: #606266; margin-bottom: 4px; font-weight: 500; }
.form-actions { margin-top: 4px; }
.score-upload { width: 100%; }
.upload-hint { font-size: 12px; color: #c0c4cc; margin-top: 6px; }

.history-card { border-radius: 14px; }
.empty-hint { text-align: center; color: #c0c4cc; padding: 20px; font-size: 14px; }
.history-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 10px 12px; border-radius: 8px; cursor: pointer; margin-bottom: 4px;
  transition: background 0.2s;
}
.history-item:hover { background: #f5f7fa; }
.history-item.active { background: #ecf0ff; }
.history-info { display: flex; align-items: center; gap: 8px; }
.history-date { font-size: 13px; color: #606266; }

.report-card { border-radius: 14px; }
.report-title { font-size: 15px; font-weight: 600; }

.score-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
}
.score-item {
  text-align: center;
  padding: 14px 8px;
  border-radius: 10px;
  transition: transform 0.2s;
}
.score-item:hover { transform: translateY(-2px); }
.level-high { background: #f0f9eb; }
.level-mid { background: #fdf6ec; }
.level-pass { background: #f4f4f5; }
.level-low { background: #fef0f0; }
.level-none { background: #f8f9fa; }
.subject-name { display: block; font-size: 13px; color: #909399; margin-bottom: 4px; }
.subject-score { font-size: 24px; font-weight: 700; }
.level-high .subject-score { color: #67c23a; }
.level-mid .subject-score { color: #e6a23c; }
.level-pass .subject-score { color: #909399; }
.level-low .subject-score { color: #f56c6c; }

.charts-row { display: flex; gap: 16px; }
.chart-box { flex: 1; background: #fafafa; border-radius: 10px; padding: 8px; min-width: 0; }

.ai-report { font-size: 14px; line-height: 2; color: #303133; white-space: pre-wrap; padding: 4px; }
</style>
