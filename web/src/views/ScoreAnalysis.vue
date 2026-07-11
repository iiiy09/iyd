<template>
  <div class="score-page">
    <div class="page-header"><h2>📊 成绩智能分析</h2></div>
    <el-row :gutter="20">
      <el-col :span="10">
        <el-card class="upload-card">
          <template #header><span>📤 上传成绩单</span></template>
          <el-upload drag :auto-upload="false" accept=".xlsx,.xls" class="score-upload">
            <el-icon :size="48"><UploadFilled /></el-icon>
            <div>点击或拖拽Excel成绩文件</div>
            <div class="upload-hint">支持.xlsx/.xls格式</div>
          </el-upload>
          <el-button type="primary" size="large" @click="uploadScore" style="width:100%;margin-top:16px">上传并分析</el-button>
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card v-if="currentReport" class="report-card">
          <template #header><span>📋 学情分析报告</span></template>
          <div class="score-grid">
            <div v-for="(val, key) in scoreMap" :key="key" class="score-item">
              <span class="subject-name">{{ key }}</span>
              <span class="subject-score" :class="getScoreColor(val)">{{ val || '-' }}</span>
            </div>
          </div>
          <el-divider />
          <div class="ai-report" v-html="formattedReport"></div>
        </el-card>
        <el-empty v-else description="请先上传成绩文件" :image-size="100" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import http from '@/api'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'

const currentReport = ref(null)
const reportId = ref(null)

async function uploadScore() {
  const res = await http.post('/score/upload', null, { params: { fileUrl: '/oss/score_test.xlsx' } })
  reportId.value = res.data.id
  const analyzeRes = await http.post('/score/' + reportId.value + '/analyze')
  currentReport.value = analyzeRes.data
  ElMessage.success('成绩分析完成！')
}

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

function getScoreColor(val) {
  const n = parseInt(val)
  if (isNaN(n)) return ''
  if (n >= 90) return 'score-high'
  if (n >= 70) return 'score-mid'
  return 'score-low'
}
</script>

<style scoped>
.score-page { max-width: 1000px; margin: 0 auto; }
.page-header h2 { font-size: 20px; margin-bottom: 16px; }
.upload-card { border-radius: 14px; }
.score-upload { width: 100%; }
.upload-hint { font-size: 12px; color: #c0c4cc; margin-top: 6px; }
.report-card { border-radius: 14px; }
.score-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.score-item { text-align: center; padding: 12px; background: #f8f9fa; border-radius: 10px; }
.subject-name { display: block; font-size: 13px; color: #909399; margin-bottom: 4px; }
.subject-score { font-size: 22px; font-weight: 700; }
.subject-score.score-high { color: #67c23a; }
.subject-score.score-mid { color: #e6a23c; }
.subject-score.score-low { color: #f56c6c; }
.ai-report { font-size: 14px; line-height: 1.9; color: #303133; white-space: pre-wrap; }
</style>
