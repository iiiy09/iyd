<template>
  <el-container class="layout">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <span class="logo-text">📚 iyd学习</span>
      </div>
      <el-menu :default-active="activeMenu" router background-color="#1d1e2c" text-color="#a8b0c8" active-text-color="#fff" class="side-menu">
        <el-menu-item index="/home"><el-icon><HomeFilled /></el-icon><span>学习首页</span></el-menu-item>
        <el-sub-menu index="study">
          <template #title><el-icon><Reading /></el-icon><span>学习中心</span></template>
          <el-menu-item index="/question">🔍 AI刷题答疑</el-menu-item>
          <el-menu-item index="/errors">📝 智能错题本</el-menu-item>
          <el-menu-item index="/pk">⚔️ 刷题PK对战</el-menu-item>
          <el-menu-item index="/courses">🎬 精品网课</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="memorize">
          <template #title><el-icon><Notebook /></el-icon><span>背诵记忆</span></template>
          <el-menu-item index="/recite">🎤 AI背诵检测</el-menu-item>
          <el-menu-item index="/words">🔤 单词背诵</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="tools">
          <template #title><el-icon><MagicStick /></el-icon><span>AI工具</span></template>
          <el-menu-item index="/notes">🧠 思维导图笔记</el-menu-item>
          <el-menu-item index="/ai">✍️ AI文案创作</el-menu-item>
          <el-menu-item index="/chat">🤖 AI智能助手</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/score"><el-icon><DataAnalysis /></el-icon><span>成绩分析</span></el-menu-item>
        <el-menu-item index="/resources"><el-icon><FolderOpened /></el-icon><span>学习资料</span></el-menu-item>
        <el-menu-item index="/tasks"><el-icon><Calendar /></el-icon><span>自律打卡</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="topbar">
        <div class="topbar-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="topbar-right">
          <el-dropdown @command="handleCommand">
            <span class="user-avatar">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="nickname">{{ userStore.userInfo?.nickname || '用户' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const activeMenu = computed(() => route.path)

onMounted(async () => {
  if (userStore.token && !userStore.userInfo) {
    try { await userStore.fetchUserInfo() } catch (e) { /* ignore */ }
  }
})

function handleCommand(cmd) {
  if (cmd === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } else if (cmd === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.layout { height: 100vh; }
.sidebar { background: #1d1e2c; overflow-y: auto; }
.logo { padding: 20px; text-align: center; border-bottom: 1px solid rgba(255,255,255,0.08); }
.logo-text { color: #fff; font-size: 20px; font-weight: 700; letter-spacing: 2px; }
.side-menu { border-right: none !important; }
.topbar { background: #fff; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 1px 4px rgba(0,0,0,0.06); padding: 0 24px; height: 56px; }
.topbar-right { display: flex; align-items: center; gap: 16px; }
.user-avatar { display: flex; align-items: center; gap: 8px; cursor: pointer; }
.nickname { font-size: 14px; color: #303133; }
.main-content { background: #f0f2f5; padding: 20px; min-height: calc(100vh - 56px); }
</style>
