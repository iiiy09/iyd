<template>
  <div class="profile-page">
    <div class="profile-header">
      <el-avatar :size="80" icon="UserFilled" />
      <h2>{{ userStore.userInfo?.nickname || '同学' }}</h2>
      <el-tag size="large">{{ userStore.userInfo?.stage || '未设置学段' }}</el-tag>
    </div>
    <el-row :gutter="20">
      <el-col :span="8" v-for="card in statCards" :key="card.label">
        <div class="stat-card" :style="{ borderTopColor: card.color }">
          <div class="stat-icon">{{ card.icon }}</div>
          <div class="stat-body"><span class="stat-val">{{ card.value }}</span><span class="stat-label">{{ card.label }}</span></div>
        </div>
      </el-col>
    </el-row>
    <el-card class="settings-card">
      <template #header><span>⚙️ 个人设置</span></template>
      <el-form label-width="80px">
        <el-form-item label="昵称"><el-input v-model="nickname" /></el-form-item>
        <el-form-item label="手机号"><el-input :model-value="userStore.userInfo?.phone" disabled /></el-form-item>
        <el-form-item label="学段"><el-select v-model="stage" style="width:200px"><el-option v-for="s in stages" :key="s" :label="s" :value="s" /></el-select></el-form-item>
        <el-form-item><el-button type="primary" @click="saveProfile">保存修改</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import http from '@/api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const nickname = ref(userStore.userInfo?.nickname || '')
const stage = ref(userStore.userInfo?.stage || '初中')
const stages = ['小学', '初中', '高中', '大学']

const statCards = [
  { icon: '📝', value: '156', label: '累计刷题', color: '#667eea' },
  { icon: '🎤', value: '23', label: '背诵记录', color: '#67c23a' },
  { icon: '📅', value: '45', label: '打卡天数', color: '#e6a23c' },
  { icon: '📁', value: '12', label: '收藏资料', color: '#f56c6c' },
  { icon: '🎬', value: '8', label: '观看课程', color: '#409eff' },
  { icon: '🧠', value: '6', label: '笔记数量', color: '#a78bfa' },
]

async function saveProfile() {
  await http.put('/user/profile', null, { params: { nickname: nickname.value } })
  await userStore.fetchUserInfo()
  ElMessage.success('保存成功')
}
</script>

<style scoped>
.profile-page { max-width: 800px; margin: 0 auto; }
.profile-header { text-align: center; padding: 30px 0; }
.profile-header h2 { margin: 12px 0 6px; color: #303133; }
.stat-card { background: #fff; border-radius: 12px; padding: 18px; display: flex; align-items: center; gap: 16px; border-top: 3px solid; margin-bottom: 16px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
.stat-icon { font-size: 32px; }
.stat-val { display: block; font-size: 22px; font-weight: 700; color: #303133; }
.stat-label { font-size: 13px; color: #909399; }
.settings-card { border-radius: 14px; }
</style>
