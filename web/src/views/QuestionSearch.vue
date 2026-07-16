<template>
  <div class="question-page">
    <el-card class="search-card">
      <div class="search-type">
        <el-radio-group v-model="searchMode" size="large">
          <el-radio-button label="text">📝 文字搜题</el-radio-button>
          <el-radio-button label="photo">📸 拍照搜题</el-radio-button>
        </el-radio-group>
      </div>
      <div class="search-area" v-if="searchMode === 'text'">
        <el-input v-model="questionText" type="textarea" :rows="4" placeholder="输入题目内容或学习问题，AI将为您详细解答..." />
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
      <template #header><span>📡 AI解析结果</span></template>
      <div class="result-section">
        <el-tag type="success" size="large" class="answer-tag">💡 解答</el-tag>
      </div>
      <div class="answer-content">{{ result.answer }}</div>
      <div class="result-section" v-if="result.knowledgePoints && result.knowledgePoints.length" style="margin-top:16px">
        <span class="section-label">📌 知识点：</span>
        <el-tag v-for="kp in result.knowledgePoints" :key="kp" type="info" size="small" style="margin-right:8px">{{ kp }}</el-tag>
      </div>
      <div class="result-section" v-if="result.relatedConcepts && result.relatedConcepts.length" style="margin-top:12px">
        <span class="section-label">🔗 拓展概念：</span>
        <el-tag v-for="c in result.relatedConcepts" :key="c" type="" size="small" style="margin-right:8px">{{ c }}</el-tag>
      </div>
    </el-card>
    <el-empty v-else-if="!searching && questionText && !result" description="输入题目后点击AI智能解析开始分析" :image-size="80" style="margin-top:40px" />
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
  const text = questionText.value.trim()
  if (!text) return ElMessage.warning('请输入题目内容')
  searching.value = true
  result.value = null
  try {
    const res = await http.post('/ai/generate', {
      generateType: 6,
      inputContent: text,
      topic: searchSubject.value
    })
    result.value = { answer: res.data.content, steps: [], knowledgePoints: [], relatedConcepts: [] }
  } catch (e) {
    ElMessage.error('请求失败，请检查网络连接')
  } finally {
    searching.value = false
  }
}
</script>

<style scoped>
.question-page { max-width: 900px; margin: 0 auto; }
.search-card { border-radius: 14px; margin-bottom: 20px; }
.search-type { margin-bottom: 6px; }
.search-area { margin-top: 16px; }
.search-filters { display: flex; gap: 12px; margin-top: 12px; }
.photo-upload { width: 100%; }
.upload-text { font-size: 14px; color: #909399; margin-top: 8px; }
.result-card { border-radius: 14px; }
.result-section { display: flex; align-items: flex-start; gap: 8px; }
.answer-tag { margin-bottom: 8px; }
.answer-content {
  font-size: 15px;
  color: #303133;
  line-height: 1.8;
  white-space: pre-wrap;
  word-wrap: break-word;
  background: #f8f9fa;
  padding: 16px 20px;
  border-radius: 10px;
  margin-top: 4px;
}
.section-label { font-size: 13px; color: #909399; white-space: nowrap; margin-top: 2px; }
</style>
