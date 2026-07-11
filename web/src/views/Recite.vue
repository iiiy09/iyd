<template>
  <div class="recite-page">
    <div class="page-header">
      <h2>🎤 AI智能背诵检测</h2>
      <el-radio-group v-model="checkType" size="default">
        <el-radio-button label="1">✏️ 文字批改</el-radio-button>
        <el-radio-button label="2">📷 拍照批改</el-radio-button>
        <el-radio-button label="3">🎥 视频批改</el-radio-button>
      </el-radio-group>
    </div>
    <el-row :gutter="20">
      <el-col :span="14">
        <el-card class="recite-card">
          <template #header><span>📖 背诵原文</span></template>
          <el-input v-model="originalText" type="textarea" :rows="8" placeholder="请输入或粘贴需要背诵的课文/知识点原文..." />
        </el-card>
        <el-card class="recite-card" v-if="checkType == 1">
          <template #header><span>✍️ 默写作答</span></template>
          <el-input v-model="userAnswer" type="textarea" :rows="8" placeholder="请在此默写背诵内容..." />
          <el-button type="primary" size="large" @click="submitRecite" :loading="submitting" class="submit-btn">提交批改</el-button>
        </el-card>
        <el-card class="recite-card" v-else-if="checkType == 2">
          <template #header><span>📷 拍照上传</span></template>
          <el-upload drag :auto-upload="false" class="upload-area">
            <el-icon :size="40"><Camera /></el-icon>
            <div>拍摄手写背诵内容</div>
          </el-upload>
          <el-button type="primary" size="large" @click="submitRecite" :loading="submitting" class="submit-btn">OCR识别批改</el-button>
        </el-card>
        <el-card class="recite-card" v-else>
          <template #header><span>🎥 上传背诵视频</span></template>
          <el-upload drag :auto-upload="false" class="upload-area">
            <el-icon :size="40"><VideoCamera /></el-icon>
            <div>上传现场背诵视频</div>
          </el-upload>
          <el-button type="primary" size="large" @click="submitRecite" :loading="submitting" class="submit-btn">AI视频解析批改</el-button>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card v-if="result" class="result-card">
          <template #header><span>📊 批改结果</span></template>
          <div class="score-circle" :class="getScoreClass(result.score)">
            <span class="score-num">{{ result.score }}</span>
            <span class="score-unit">分</span>
          </div>
          <div class="accuracy">{{ result.accuracy }}</div>
          <p class="suggestion">{{ result.suggestion }}</p>
          <div class="error-list" v-if="result.errors?.length">
            <div v-for="(err, idx) in result.errors" :key="idx" class="error-item">
              <span class="err-line">第{{ err.line }}行</span>
              <span class="err-type">{{ err.type === 'missing' ? '❌缺失' : '⚠️错漏' }}</span>
              <span class="err-text">原文：{{ err.original }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import http from '@/api'
import { ElMessage } from 'element-plus'

const checkType = ref('1')
const originalText = ref('')
const userAnswer = ref('')
const submitting = ref(false)
const result = ref(null)

async function submitRecite() {
  if (!originalText.value.trim()) return ElMessage.warning('请输入背诵原文')
  submitting.value = true
  try {
    const res = await http.post('/recite/submit', { originalText: originalText.value, userAnswer: userAnswer.value, checkType: Number(checkType.value) })
    result.value = res.data
  } finally { submitting.value = false }
}

function getScoreClass(score) {
  if (score >= 90) return 'score-excellent'
  if (score >= 70) return 'score-good'
  return 'score-poor'
}
</script>

<style scoped>
.recite-page { max-width: 1100px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; color: #303133; }
.recite-card { border-radius: 14px; margin-bottom: 16px; }
.submit-btn { margin-top: 16px; width: 100%; }
.upload-area { width: 100%; }
.result-card { border-radius: 14px; text-align: center; }
.score-circle {
  width: 100px; height: 100px; border-radius: 50%; display: flex; flex-direction: column; align-items: center; justify-content: center; margin: 0 auto 12px;
}
.score-circle.score-excellent { background: linear-gradient(135deg, #67c23a, #85ce61); }
.score-circle.score-good { background: linear-gradient(135deg, #e6a23c, #ebb563); }
.score-circle.score-poor { background: linear-gradient(135deg, #f56c6c, #f78989); }
.score-num { font-size: 32px; font-weight: 700; color: #fff; }
.score-unit { font-size: 14px; color: rgba(255,255,255,0.8); }
.accuracy { font-size: 14px; color: #606266; margin-bottom: 8px; }
.suggestion { font-size: 13px; color: #909399; margin: 8px 0; padding: 10px; background: #f8f9fa; border-radius: 8px; }
.error-list { text-align: left; margin-top: 12px; }
.error-item { padding: 8px 0; border-bottom: 1px dashed #ebeef5; font-size: 13px; }
.err-line { color: #667eea; margin-right: 8px; font-weight: 600; }
.err-type { color: #f56c6c; margin-right: 8px; }
.err-text { color: #606266; }
</style>
