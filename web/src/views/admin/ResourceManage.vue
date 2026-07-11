<template>
  <div class="res-mgmt">
    <el-alert v-if="newResourceAlert.hasNew" :title="'系统自动同步' + newResourceAlert.count + '份全新学习资料，点击查看'" type="success" show-icon closable @click="showNewResources = true" class="sync-alert" />
    <div class="page-header">
      <h3>资料管理</h3>
      <div class="header-actions">
        <el-button @click="showSyncLogs = true">同步日志</el-button>
        <el-button type="primary" @click="showUpload = true" :icon="Plus">上传资料</el-button>
      </div>
    </div>
    <el-row :gutter="12" class="filter-row">
      <el-col :span="4"><el-select v-model="filters.stage" placeholder="学段" clearable><el-option v-for="s in stages" :key="s" :label="s" :value="s" /></el-select></el-col>
      <el-col :span="4"><el-select v-model="filters.subject" placeholder="科目" clearable><el-option v-for="s in subjects" :key="s" :label="s" :value="s" /></el-select></el-col>
      <el-col :span="4"><el-select v-model="filters.auditStatus" placeholder="审核状态" clearable><el-option label="待审核" :value="0" /><el-option label="已通过" :value="1" /><el-option label="已驳回" :value="2" /></el-select></el-col>
      <el-col :span="4"><el-button type="primary" @click="loadResources">检索</el-button></el-col>
    </el-row>
    <el-table :data="resources" border stripe @selection-change="handleSelection">
      <el-table-column type="selection" width="50" />
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="resourceName" label="资料名称" />
      <el-table-column prop="stage" label="学段" width="70" />
      <el-table-column prop="subject" label="科目" width="70" />
      <el-table-column prop="resourceType" label="类型" width="90" />
      <el-table-column prop="source" label="来源" width="80"><template #default="{row}"><el-tag :type="row.source==='sync'?'warning':row.source==='init'?'info':''" size="small">{{ row.source === 'sync' ? '自动同步' : row.source === 'init' ? '预置' : '手动上传' }}</el-tag></template></el-table-column>
      <el-table-column prop="auditStatus" label="审核" width="80"><template #default="{row}"><el-tag :type="row.auditStatus===1?'success':row.auditStatus===0?'warning':'danger'" size="small">{{ row.auditStatus===1?'通过':row.auditStatus===0?'待审':'驳回' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{row}">
          <el-button size="small" type="success" @click="audit(row.id,1)" v-if="row.auditStatus!==1">通过</el-button>
          <el-button size="small" type="danger" @click="deleteResource(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="batch-actions" v-if="selectedIds.length > 0">
      <el-button type="danger" @click="batchDelete">批量删除({{ selectedIds.length }})</el-button>
    </div>

    <el-dialog v-model="showSyncLogs" title="同步日志" width="700px">
      <el-table :data="syncLogs" border size="small"><el-table-column prop="batchNo" label="批次号" /><el-table-column prop="newResourceCount" label="新增数量" /><el-table-column prop="operationType" label="类型" /><el-table-column prop="createTime" label="时间" /></el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import http from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const resources = ref([])
const selectedIds = ref([])
const filters = ref({ stage: '', subject: '', auditStatus: null })
const newResourceAlert = ref({ hasNew: false, count: 0 })
const showUpload = ref(false)
const showSyncLogs = ref(false)
const syncLogs = ref([])

const stages = ['小学', '初中', '高中', '大学']
const subjects = ['语文', '数学', '英语', '物理', '化学', '生物', '历史', '地理', '政治']

onMounted(() => { loadResources(); checkAlert() })

async function loadResources() {
  const res = await http.post('/admin/resources', { ...filters.value, page: 1, size: 20 })
  resources.value = res.data?.records || []
}
async function checkAlert() {
  const res = await http.get('/admin/alert/new-resources')
  newResourceAlert.value = res.data || { hasNew: false }
}
function handleSelection(sel) { selectedIds.value = sel.map(s => s.id) }
async function deleteResource(id) {
  await ElMessageBox.confirm('确认删除？', '提示', { type: 'warning' })
  await http.delete('/admin/resource/' + id)
  ElMessage.success('已删除')
  loadResources()
}
async function batchDelete() {
  await ElMessageBox.confirm('确认批量删除？', '提示', { type: 'warning' })
  await http.delete('/admin/resources/batch?ids=' + selectedIds.value.join(','))
  ElMessage.success('批量删除成功')
  loadResources()
}
async function audit(id, status) {
  await http.put('/admin/resource/' + id + '/audit?status=' + status)
  ElMessage.success('审核完成')
  loadResources()
}
</script>

<style scoped>
.sync-alert { margin-bottom: 16px; cursor: pointer; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.header-actions { display: flex; gap: 8px; }
.filter-row { margin-bottom: 16px; }
.batch-actions { margin-top: 12px; }
</style>
