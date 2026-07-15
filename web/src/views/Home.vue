<template>
  <div class="home">
    <div class="welcome-card">
      <div class="welcome-left">
        <h2>👋 欢迎回来，{{ userStore.userInfo?.nickname || '同学' }}</h2>
        <p class="motto">千里之行，始于足下。今日学习，此刻启程。</p>
      </div>
      <div class="welcome-right">
        <div class="stat-item">
          <span class="stat-num">{{ stats.learnHours }}</span>
          <span class="stat-label">今日任务</span>
        </div>
        <div class="stat-item">
          <span class="stat-num">{{ stats.streak }}天</span>
          <span class="stat-label">连续打卡</span>
        </div>
        <div class="stat-item">
          <span class="stat-num">{{ stats.totalHours }}h</span>
          <span class="stat-label">累计学习</span>
        </div>
      </div>
    </div>
    <el-row :gutter="20" class="cards-row">
      <el-col :span="8">
        <div class="func-card card-purple" @click="$router.push('/recite')">
          <div class="card-icon">🎤</div>
          <h3>AI背诵检测</h3>
          <p>文字/拍照/视频三种模式</p>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="func-card card-blue" @click="$router.push('/question')">
          <div class="card-icon">🔍</div>
          <h3>AI刷题答疑</h3>
          <p>拍照搜题 · 分步解析</p>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="func-card card-green" @click="$router.push('/notes')">
          <div class="card-icon">🧠</div>
          <h3>思维导图笔记</h3>
          <p>OCR提取 · 一键生成导图</p>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="20" class="cards-row">
      <el-col :span="8">
        <div class="func-card card-orange" @click="$router.push('/words')">
          <div class="card-icon">🔤</div>
          <h3>单词背诵</h3>
          <p>发音测评 · 艾宾浩斯复习</p>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="func-card card-red" @click="$router.push('/ai')">
          <div class="card-icon">✍️</div>
          <h3>AI文案创作</h3>
          <p>作文 · PPT · 论文大纲</p>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="func-card card-teal" @click="$router.push('/chat')">
          <div class="card-icon">🤖</div>
          <h3>AI智能助手</h3>
          <p>24h在线 · 学习问答</p>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="section-card">
          <template #header><span>📋 今日待复习</span></template>
          <el-empty v-if="reviewList.length === 0" description="暂无复习任务，去学习吧～" :image-size="80" />
          <div v-for="item in reviewList" :key="item.id" class="review-item">
            <el-tag size="small" :type="item.tagType">{{ item.tag }}</el-tag>
            <span class="review-text">{{ item.content }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="section-card">
          <template #header><span>⚡ 快捷入口</span></template>
          <div class="quick-grid">

            <div class="quick-item" @click="$router.push('/score')"><span class="quick-icon">📊</span><span>成绩分析</span></div>
            <div class="quick-item" @click="$router.push('/resources')"><span class="quick-icon">📁</span><span>学习资料</span></div>

            <div class="quick-item" @click="$router.push('/errors')"><span class="quick-icon">📝</span><span>错题本</span></div>
            <div class="quick-item" @click="$router.push('/pk')"><span class="quick-icon">⚔️</span><span>刷题PK</span></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const stats = reactive({ learnHours: 6, streak: 12, totalHours: 86 })

const reviewList = [
  { id: 1, tag: '单词', tagType: 'primary', content: 'Unit 5 核心词汇复习（12个单词待巩固）' },
  { id: 2, tag: '错题', tagType: 'danger', content: '数学-二次函数 错题复盘（3道）' },
  { id: 3, tag: '背诵', tagType: 'warning', content: '《岳阳楼记》第三段背诵检测' },
]
</script>

<style scoped>
.home { max-width: 1200px; margin: 0 auto; }
.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px; padding: 28px 36px; color: #fff; display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;
}
.welcome-left h2 { font-size: 22px; margin-bottom: 6px; }
.motto { opacity: 0.85; font-size: 14px; }
.welcome-right { display: flex; gap: 32px; }
.stat-item { text-align: center; }
.stat-num { display: block; font-size: 28px; font-weight: 700; }
.stat-label { font-size: 13px; opacity: 0.8; }
.cards-row { margin-bottom: 20px; }
.func-card {
  background: #fff; border-radius: 14px; padding: 24px; cursor: pointer; transition: all 0.3s; border: 2px solid transparent; height: 130px; display: flex; flex-direction: column; justify-content: center;
}
.func-card:hover { transform: translateY(-4px); }
.func-card h3 { font-size: 17px; margin: 8px 0 4px; color: #303133; }
.func-card p { font-size: 13px; color: #909399; margin: 0; }
.card-icon { font-size: 28px; }
.card-purple:hover { border-color: #a78bfa; box-shadow: 0 8px 25px rgba(167,139,250,0.2); }
.card-blue:hover { border-color: #60a5fa; box-shadow: 0 8px 25px rgba(96,165,250,0.2); }
.card-green:hover { border-color: #34d399; box-shadow: 0 8px 25px rgba(52,211,153,0.2); }
.card-orange:hover { border-color: #fbbf24; box-shadow: 0 8px 25px rgba(251,191,36,0.2); }
.card-red:hover { border-color: #f87171; box-shadow: 0 8px 25px rgba(248,113,113,0.2); }
.card-teal:hover { border-color: #2dd4bf; box-shadow: 0 8px 25px rgba(45,212,191,0.2); }
.section-card { border-radius: 12px; height: 280px; }
.review-item { display: flex; align-items: center; gap: 10px; padding: 10px 0; border-bottom: 1px solid #f0f0f0; }
.review-item:last-child { border-bottom: none; }
.review-text { font-size: 14px; color: #606266; }
.quick-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.quick-item {
  display: flex; flex-direction: column; align-items: center; gap: 6px; padding: 16px 8px; border-radius: 10px; cursor: pointer; transition: all 0.2s; background: #f8f9fa; font-size: 13px; color: #606266;
}
.quick-item:hover { background: #ecf0ff; color: #667eea; }
.quick-icon { font-size: 22px; }
</style>
