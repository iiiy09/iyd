<template>
  <el-container class="admin-layout">
    <el-aside width="240px" class="admin-sidebar">
      <div class="admin-logo">🛡️ 管理后台</div>
      <el-menu :default-active="activeMenu" router background-color="#0a0a1a" text-color="#8890b5" active-text-color="#fff">
        <el-menu-item index="/admin/dashboard"><el-icon><Odometer /></el-icon>数据概览</el-menu-item>
        <el-menu-item index="/admin/users"><el-icon><User /></el-icon>用户管理</el-menu-item>
        
        <el-menu-item index="/admin/resources"><el-icon><Folder /></el-icon>资料管理</el-menu-item>
        <el-menu-item index="/home"><el-icon><Back /></el-icon>返回前台</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="admin-topbar">
        <span class="admin-title">{{ $route.meta.title }}</span>
        <el-tag type="success">管理员</el-tag>
      </el-header>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.admin-layout { height: 100vh; }
.admin-sidebar { background: #0a0a1a; }
.admin-logo { padding: 24px 20px; color: #fff; font-size: 18px; font-weight: 700; text-align: center; border-bottom: 1px solid rgba(255,255,255,0.08); }
.admin-topbar { background: #fff; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 1px 4px rgba(0,0,0,0.06); height: 56px; padding: 0 24px; }
.admin-title { font-size: 16px; font-weight: 600; color: #303133; }
.admin-main { background: #f0f2f5; padding: 20px; }
</style>
