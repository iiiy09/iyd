<template>
  <div class="user-mgmt">
    <div class="page-header"><h3>用户管理</h3><el-input v-model="keyword" placeholder="搜索手机号/昵称" style="width:240px" clearable @clear="loadUsers" @keyup.enter="loadUsers" /></div>
    <el-table :data="users" border stripe style="width:100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column prop="nickname" label="昵称" width="140" />
      <el-table-column prop="stage" label="学段" width="80" />
      <el-table-column prop="role" label="角色" width="80"><template #default="{row}"><el-tag :type="row.role==='admin'?'danger':''" size="small">{{ row.role }}</el-tag></template></el-table-column>
      <el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'info'" size="small">{{ row.status===1?'正常':'禁用' }}</el-tag></template></el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="170" />
      <el-table-column label="操作">
        <template #default="{row}">
          <el-button size="small" @click="toggleStatus(row)" :type="row.status===1?'warning':'success'">{{ row.status===1?'禁用':'启用' }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination layout="prev,pager,next" :total="total" :page-size="20" @current-change="loadUsers" class="pagination" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import http from '@/api'
import { ElMessage } from 'element-plus'

const users = ref([])
const total = ref(0)
const keyword = ref('')

onMounted(() => loadUsers())
async function loadUsers(p = 1) {
  const res = await http.get('/admin/users', { params: { page: p, size: 20, keyword: keyword.value } })
  users.value = res.data?.records || []
  total.value = res.data?.total || 0
}
async function toggleStatus(row) {
  await http.put('/admin/user/' + row.id + '/status?status=' + (row.status === 1 ? 0 : 1))
  ElMessage.success('状态已更新')
  loadUsers()
}
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { font-size: 18px; color: #303133; }
.pagination { margin-top: 16px; display: flex; justify-content: center; }
</style>
