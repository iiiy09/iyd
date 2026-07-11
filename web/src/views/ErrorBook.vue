<template>
  <div class="error-page">
    <div class="page-header">
      <h2>📝 智能错题本</h2>
      <el-radio-group v-model="filter" size="small"><el-radio-button label="all">全部</el-radio-button><el-radio-button label="unmastered">未掌握</el-radio-button><el-radio-button label="mastered">已掌握</el-radio-button></el-radio-group>
    </div>
    <div class="error-grid">
      <div v-for="item in errors" :key="item.id" class="error-card">
        <div class="error-header">
          <el-tag :type="item.errorReason === '概念混淆' ? 'warning' : 'danger'" size="small">{{ item.errorReason || '计算失误' }}</el-tag>
          <el-tag type="info" size="small">{{ item.knowledgePoint || '知识点' }}</el-tag>
          <span class="error-count">错{{ item.errorCount }}次</span>
        </div>
        <p class="error-question">{{ item.questionContent || '题目内容...' }}</p>
        <div class="error-footer">
          <el-button size="small" @click="reAnswer(item)">重新作答</el-button>
          <el-button size="small" type="success" @click="markMastered(item)" v-if="!item.mastered">标记掌握</el-button>
        </div>
      </div>
    </div>
    <el-empty v-if="errors.length === 0" description="暂无错题，继续保持！" />
    <el-pagination v-if="total > 10" layout="prev,pager,next" :total="total" :page-size="10" @current-change="loadErrors" class="pagination" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import http from '@/api'
import { ElMessage } from 'element-plus'

const filter = ref('all')
const errors = ref([])
const total = ref(0)
const page = ref(1)

onMounted(() => loadErrors())

async function loadErrors(p = 1) {
  page.value = p
  const res = await http.get('/question/errors', { params: { page: p, size: 10 } })
  errors.value = res.data.records
  total.value = res.data.total
}

async function markMastered(item) {
  await http.put('/question/error/' + item.id + '/mastered')
  ElMessage.success('已标记为掌握')
  loadErrors(page.value)
}

function reAnswer(item) {
  ElMessage.info('进入重新刷题模式')
}
</script>

<style scoped>
.error-page { max-width: 1000px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; color: #303133; }
.error-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.error-card { background: #fff; border-radius: 12px; padding: 18px; box-shadow: 0 2px 12px rgba(0,0,0,0.06); }
.error-header { display: flex; gap: 8px; align-items: center; margin-bottom: 10px; }
.error-count { margin-left: auto; font-size: 12px; color: #f56c6c; }
.error-question { font-size: 14px; color: #606266; line-height: 1.6; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; }
.error-footer { display: flex; gap: 8px; margin-top: 12px; }
.pagination { margin-top: 20px; justify-content: center; display: flex; }
</style>
