<template>
  <div class="question-page">
    <el-card class="search-card">
      <el-radio-group v-model="searchMode" size="large">
        <el-radio-button label="text">📝 文字搜题</el-radio-button>
        <el-radio-button label="photo">📷 拍照搜题</el-radio-button>
      </el-radio-group>
      <div class="search-area" v-if="searchMode === 'text'">
        <el-input v-model="questionText" type="textarea" :rows="4" placeholder="输入题目内容，AI将为您分步解析..." />
        <div class="search-filters">
          <el-select v-model="searchSubject" placeholder="科目" style="width:140px">
            <el-option v-for="s in subjectOptions" :key="s.key" :label="s.label" :value="s.key" />
          </el-select>
          <el-select v-model="searchStage" placeholder="学段" style="width:140px">
            <el-option v-for="s in stages" :key="s" :label="s" :value="s" />
          </el-select>
          <el-button type="primary" @click="searchQuestion" :loading="searching" :icon="Search">AI智能解析</el-button>
        </div>
      </div>
      <div class="search-area" v-else>
        <el-upload drag :auto-upload="false" class="photo-upload">
          <el-icon :size="48"><Camera /></el-icon>
          <div class="upload-text">点击或拖拽题目图片到此处</div>
        </el-upload>
      </div>
    </el-card>

    <el-card v-if="result" class="result-card">
      <template #header><span>🔬 AI解析结果</span></template>
      <div class="result-steps">
        <div v-for="(step, idx) in result.steps" :key="idx" class="step-item">
          <div class="step-num">{{ idx + 1 }}</div>
          <div class="step-content">{{ step }}</div>
        </div>
      </div>
      <el-divider />
      <div class="result-section">
        <el-tag type="success" size="large">答案</el-tag>
        <div class="answer-content">{{ result.answer }}</div>
      </div>
      <div class="result-section">
        <span class="section-label">知识点：</span>
        <el-tag v-for="kp in result.knowledgePoints" :key="kp" type="info" size="small" style="margin-right:8px">{{ kp }}</el-tag>
      </div>
      <el-alert :title="result.commonMistakes" type="warning" :closable="false" show-icon style="margin:12px 0" />
      <div class="result-section" v-if="result.relatedConcepts">
        <span class="section-label">拓展概念：</span>
        <el-tag v-for="c in result.relatedConcepts" :key="c" type="" size="small" style="margin-right:8px">{{ c }}</el-tag>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import http from '@/api'
import { ElMessage } from 'element-plus'
import { Search, Camera } from '@element-plus/icons-vue'

const searchMode = ref('text')
const questionText = ref('')
const searchSubject = ref('math')
const searchStage = ref('初中')
const searching = ref(false)
const result = ref(null)

const subjectOptions = [
  { key: 'math', label: '数学' },
  { key: 'physics', label: '物理' },
  { key: 'chemistry', label: '化学' },
  { key: 'chinese', label: '语文' },
  { key: 'english', label: '英语' },
  { key: 'history', label: '历史' },
  { key: 'geography', label: '地理' },
  { key: 'biology', label: '生物' },
]
const stages = ['小学', '初中', '高中', '大学']

async function searchQuestion() {
  if (!questionText.value.trim()) return ElMessage.warning('请输入题目内容')
  searching.value = true
  try {
    const res = await http.post('/question/search', {
      content: questionText.value,
      subject: searchSubject.value,
      stage: searchStage.value
    })
    result.value = res.data
  } finally {
    searching.value = false
  }
}
</script>

<style scoped>
.question-page { max-width: 900px; margin: 0 auto; }
.search-card { border-radius: 14px; margin-bottom: 20px; }
.search-area { margin-top: 20px; }
.search-filters { display: flex; gap: 12px; margin-top: 12px; }
.photo-upload { width: 100%; }
.upload-text { font-size: 14px; color: #909399; margin-top: 8px; }
.result-card { border-radius: 14px; }
.step-item { display: flex; gap: 14px; padding: 12px 0; border-bottom: 1px dashed #ebeef5; }
.step-num {
  width: 28px; height: 28px; background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 13px; flex-shrink: 0;
}
.step-content { font-size: 14px; color: #606266; line-height: 1.8; }
.result-section { margin: 12px 0; display: flex; align-items: flex-start; gap: 8px; }
.answer-content { font-size: 15px; font-weight: 500; color: #67c23a; line-height: 1.6; }
.section-label { font-size: 13px; color: #909399; white-space: nowrap; margin-top: 2px; }
</style>