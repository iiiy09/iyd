<template>
  <div class="task-page">
    <div class="page-header">
      <h2>📅 自律打卡</h2>
      <el-button type="primary" @click="showAddDialog = true" :icon="Plus">新建任务</el-button>
    </div>
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="task-card">
          <template #header><span>📋 今日任务</span></template>
          <div v-for="t in tasks" :key="t.id" class="task-item" :class="{ done: t.status === 1 }">
            <el-checkbox v-model="t.checked" @change="handleCheckin(t)" :disabled="t.status === 1" />
            <div class="task-info">
              <span class="task-content" :class="{ completed: t.status === 1 }">{{ t.taskContent }}</span>
              <span class="task-time">{{ t.estimatedMinutes }}分钟 · 截止 {{ formatDate(t.deadline) }}</span>
            </div>
            <el-button text type="danger" @click="deleteTask(t.id)" :icon="Delete" size="small" />
          </div>
          <el-empty v-if="tasks.length === 0" description="暂无任务，去添加吧～" :image-size="80" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="summary-card">
          <template #header><span>📊 本月统计</span></template>
          <div class="summary-grid">
            <div class="s-item"><span class="s-num purple">{{ summary?.checkinDays || 0 }}</span><span>打卡天数</span></div>
            <div class="s-item"><span class="s-num blue">{{ summary?.totalMinutes || 0 }}</span><span>学习时长(min)</span></div>
            <div class="s-item"><span class="s-num green">{{ summary?.totalTasks || 0 }}</span><span>完成任务</span></div>
            <div class="s-item"><span class="s-num orange">{{ summary?.avgDailyMinutes || 0 }}</span><span>日均(min)</span></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showAddDialog" title="新建任务" width="480px">
      <el-form :model="taskForm">
        <el-form-item label="任务内容"><el-input v-model="taskForm.taskContent" placeholder="例如：完成数学第5章练习题" /></el-form-item>
        <el-form-item label="预计时长(分钟)"><el-input-number v-model="taskForm.estimatedMinutes" :min="5" :max="480" /></el-form-item>
        <el-form-item label="截止时间"><el-date-picker v-model="taskForm.deadline" type="datetime" placeholder="选择时间" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="showAddDialog = false">取消</el-button><el-button type="primary" @click="addTask">创建</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import http from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const showAddDialog = ref(false)
const tasks = ref([])
const summary = ref(null)
const taskForm = ref({ taskContent: '', estimatedMinutes: 30, deadline: '' })

onMounted(() => { loadTasks(); loadSummary() })

async function loadTasks() {
  const res = await http.get('/task/list')
  tasks.value = (res.data || []).map(t => ({ ...t, checked: t.status === 1 }))
}
async function loadSummary() {
  const month = dayjs().format('YYYY-MM')
  const res = await http.get('/task/summary', { params: { month } })
  summary.value = res.data
}
async function addTask() {
  if (!taskForm.value.taskContent) return ElMessage.warning('请输入任务内容')
  await http.post('/task', taskForm.value)
  ElMessage.success('任务创建成功')
  showAddDialog.value = false
  taskForm.value = { taskContent: '', estimatedMinutes: 30, deadline: '' }
  loadTasks()
}
async function handleCheckin(task) {
  if (task.status === 1) return
  await http.post('/task/' + task.id + '/checkin')
  ElMessage.success('🎉 打卡成功！')
  loadTasks()
  loadSummary()
}
async function deleteTask(id) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await http.delete('/task/' + id)
  loadTasks()
}
function formatDate(d) { return d ? dayjs(d).format('MM-DD HH:mm') : '无截止' }
</script>

<style scoped>
.task-page { max-width: 1000px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; }
.task-card { border-radius: 14px; }
.task-item { display: flex; align-items: center; gap: 12px; padding: 12px 0; border-bottom: 1px solid #f5f5f5; }
.task-item.done { opacity: 0.6; }
.task-info { flex: 1; }
.task-content { font-size: 14px; color: #303133; display: block; }
.task-content.completed { text-decoration: line-through; color: #c0c4cc; }
.task-time { font-size: 12px; color: #909399; margin-top: 2px; display: block; }
.summary-card { border-radius: 14px; }
.summary-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.s-item { text-align: center; padding: 16px; background: #f8f9fa; border-radius: 10px; display: flex; flex-direction: column; gap: 4px; font-size: 13px; color: #909399; }
.s-num { font-size: 24px; font-weight: 700; }
.s-num.purple { color: #a78bfa; }
.s-num.blue { color: #60a5fa; }
.s-num.green { color: #34d399; }
.s-num.orange { color: #fbbf24; }
</style>
