<template>
  <div class="res-page">
    <div class="page-header"><h2>📁 学习资料库</h2></div>
    <el-card class="filter-card">
      <el-row :gutter="12">
        <el-col :span="4"><el-select v-model="filters.stage" placeholder="学段" clearable><el-option v-for="s in stages" :key="s" :label="s" :value="s" /></el-select></el-col>
        <el-col :span="4"><el-select v-model="filters.grade" placeholder="年级" clearable><el-option v-for="g in grades" :key="g" :label="g" :value="g" /></el-select></el-col>
        <el-col :span="4"><el-select v-model="filters.subject" placeholder="科目" clearable><el-option v-for="s in subjects" :key="s" :label="s" :value="s" /></el-select></el-col>
        <el-col :span="4"><el-select v-model="filters.resourceType" placeholder="类型" clearable><el-option label="课件" value="课件" /><el-option label="真题" value="真题" /><el-option label="复习提纲" value="复习提纲" /><el-option label="课后答案" value="答案" /></el-select></el-col>
        <el-col :span="4"><el-input v-model="filters.keyword" placeholder="搜索关键词" /></el-col>
        <el-col :span="4"><el-button type="primary" @click="search" :icon="Search">检索</el-button></el-col>
      </el-row>
    </el-card>

    <div class="res-grid">
      <div v-for="r in resources" :key="r.id" class="res-card">
        <div class="res-icon">📄</div>
        <div class="res-info">
          <h4>{{ r.resourceName }}</h4>
          <div class="res-meta">
            <el-tag size="small" type="info">{{ r.stage }}</el-tag>
            <el-tag size="small" type="success">{{ r.subject }}</el-tag>
            <el-tag size="small">{{ r.resourceType }}</el-tag>
          </div>
        </div>
        <div class="res-actions">
          <el-button size="small" @click="preview(r)">预览</el-button>
          <el-button size="small" type="primary" @click="download(r)">下载</el-button>
          <el-button size="small" :type="r.collected ? 'warning' : ''" @click="toggleCollect(r)" :icon="r.collected ? 'StarFilled' : 'Star'">{{ r.collected ? '已收藏' : '收藏' }}</el-button>
        </div>
      </div>
    </div>
    <el-empty v-if="resources.length === 0" description="未找到相关资料" />
    <el-pagination v-if="total > 12" layout="prev,pager,next" :total="total" :page-size="12" @current-change="loadPage" class="pagination" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import http from '@/api'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const stages = ['小学', '初中', '高中', '大学']
const grades = ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级', '七年级', '八年级', '九年级', '高一', '高二', '高三']
const subjects = ['语文', '数学', '英语', '物理', '化学', '生物', '历史', '地理', '政治']
const filters = ref({ stage: '', grade: '', subject: '', resourceType: '', keyword: '' })
const resources = ref([])
const total = ref(0)

onMounted(() => search())

async function search(p = 1) {
  const res = await http.post('/resource/search', { ...filters.value, page: p, size: 12 })
  resources.value = res.data?.records || []
  total.value = res.data?.total || 0
}
function loadPage(p) { search(p) }
function preview(r) { window.open(r.fileUrl, '_blank') }
async function download(r) {
  await http.get('/resource/' + r.id + '/download')
  ElMessage.success('开始下载：' + r.resourceName)
}
async function toggleCollect(r) {
  await http.post('/resource/' + r.id + '/collect')
  r.collected = !r.collected
  ElMessage.success(r.collected ? '已收藏' : '已取消收藏')
}
</script>

<style scoped>
.res-page { max-width: 1100px; margin: 0 auto; }
.page-header h2 { font-size: 20px; margin-bottom: 16px; }
.filter-card { border-radius: 14px; margin-bottom: 20px; }
.res-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; }
.res-card { background: #fff; border-radius: 12px; padding: 20px; display: flex; flex-direction: column; gap: 12px; transition: all 0.3s; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
.res-card:hover { transform: translateY(-3px); box-shadow: 0 6px 20px rgba(0,0,0,0.1); }
.res-icon { font-size: 32px; }
.res-info h4 { font-size: 15px; color: #303133; margin-bottom: 8px; }
.res-meta { display: flex; gap: 6px; flex-wrap: wrap; }
.res-actions { display: flex; gap: 6px; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>
