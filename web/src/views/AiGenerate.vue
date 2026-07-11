<template>
  <div class="ai-page">
    <div class="page-header"><h2>✍️ AI智能创作</h2></div>
    <el-tabs v-model="activeTab" class="ai-tabs">
      <el-tab-pane label="📝 作文/文案" name="essay">
        <el-card class="ai-card">
          <el-form :model="essayForm">
            <el-form-item label="创作类型">
              <el-radio-group v-model="essayForm.type">
                <el-radio-button value="作文">作文</el-radio-button><el-radio-button value="周记">周记</el-radio-button><el-radio-button value="论文">论文</el-radio-button><el-radio-button value="报告">报告</el-radio-button><el-radio-button value="发言稿">发言稿</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="主题"><el-input v-model="essayForm.topic" placeholder="输入创作主题关键词" /></el-form-item>
            <el-form-item label="要求"><el-input v-model="essayForm.requirement" type="textarea" :rows="3" placeholder="可选：描述具体要求、字数、风格等" /></el-form-item>
          </el-form>
          <el-button type="primary" size="large" @click="generateEssay" :loading="genLoading" :icon="Edit">AI智能创作</el-button>
          <div v-if="essayResult" class="ai-output">
            <div class="output-header"><span>生成结果</span><el-button-group><el-button size="small" @click="rewrite">润色改写</el-button><el-button size="small" @click="expand">扩写</el-button><el-button size="small" @click="shorten">缩写</el-button></el-button-group></div>
            <div class="output-content">{{ essayResult }}</div>
          </div>
        </el-card>
      </el-tab-pane>
      <el-tab-pane label="📊 PPT大纲" name="ppt">
        <el-card class="ai-card">
          <el-form :model="pptForm">
            <el-form-item label="主题"><el-input v-model="pptForm.topic" placeholder="PPT主题" /></el-form-item>
            <el-form-item label="核心要点"><el-input v-model="pptForm.points" type="textarea" :rows="3" placeholder="列出核心知识点（可选）" /></el-form-item>
          </el-form>
          <el-button type="primary" size="large" @click="generatePpt" :loading="genLoading" :icon="Document">生成PPT大纲</el-button>
          <div v-if="pptResult" class="ppt-output">
            <div v-for="slide in pptResult.slides" :key="slide.page" class="ppt-slide">
              <span class="slide-num">P{{ slide.page }}</span>
              <div><strong>{{ slide.title }}</strong><p>{{ slide.content }}</p></div>
            </div>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import http from '@/api'
import { ElMessage } from 'element-plus'
import { Edit, Document } from '@element-plus/icons-vue'

const activeTab = ref('essay')
const genLoading = ref(false)
const essayForm = ref({ type: '作文', topic: '', requirement: '' })
const essayResult = ref('')
const pptForm = ref({ topic: '', points: '' })
const pptResult = ref(null)

async function generateEssay() {
  if (!essayForm.value.topic) return ElMessage.warning('请输入主题')
  genLoading.value = true
  try {
    const res = await http.post('/ai/generate', { generateType: 1, inputContent: essayForm.value.requirement, topic: essayForm.value.topic })
    essayResult.value = res.data.content
  } finally { genLoading.value = false }
}

async function generatePpt() {
  if (!pptForm.value.topic) return ElMessage.warning('请输入主题')
  genLoading.value = true
  try {
    const res = await http.post('/ai/ppt', null, { params: { topic: pptForm.value.topic, points: pptForm.value.points } })
    pptResult.value = res.data
  } finally { genLoading.value = false }
}

function rewrite() { ElMessage.info('正在润色改写...') }
function expand() { ElMessage.info('正在扩写内容...') }
function shorten() { ElMessage.info('正在缩写内容...') }
</script>

<style scoped>
.ai-page { max-width: 900px; margin: 0 auto; }
.page-header h2 { font-size: 20px; margin-bottom: 16px; }
.ai-card { border-radius: 14px; }
.ai-output { margin-top: 20px; padding: 16px; background: #f8f9fa; border-radius: 10px; }
.output-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; font-weight: 600; }
.output-content { white-space: pre-wrap; line-height: 1.9; font-size: 14px; color: #303133; }
.ppt-output { margin-top: 20px; }
.ppt-slide { display: flex; gap: 16px; padding: 12px; border-radius: 10px; background: #fff; margin-bottom: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.slide-num { font-weight: 700; color: #667eea; font-size: 16px; width: 44px; }
.ppt-slide p { font-size: 13px; color: #909399; margin-top: 4px; }
</style>
