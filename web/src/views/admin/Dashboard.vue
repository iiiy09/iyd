<template>
  <div class="dashboard">
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6" v-for="s in statList" :key="s.label">
        <div class="dash-stat" :style="{ background: s.bg }">
          <div class="dash-icon">{{ s.icon }}</div>
          <div><span class="dash-num">{{ s.value }}</span><span class="dash-label">{{ s.label }}</span></div>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card class="dash-card"><template #header>📊 今日活跃数据</template>
          <div class="chart-placeholder">
            <div v-for="d in dailyData" :key="d.label" class="bar-row">
              <span class="bar-label">{{ d.label }}</span>
              <div class="bar-track"><div class="bar-fill" :style="{ width: d.percent + '%', background: d.color }"></div></div>
              <span class="bar-val">{{ d.value }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="dash-card"><template #header>📈 平台趋势</template>
          <div class="chart-placeholder">
            <div v-for="t in trends" :key="t.label" class="trend-row">
              <span>{{ t.label }}</span>
              <span :style="{ color: t.up ? '#67c23a' : '#f56c6c' }">{{ t.up ? '↑' : '↓' }} {{ t.percent }}%</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
const statList = [
  { icon: '👥', value: '12,580', label: '总用户数', bg: 'linear-gradient(135deg,#667eea,#764ba2)' },
  { icon: '📝', value: '5,230', label: '今日刷题', bg: 'linear-gradient(135deg,#f093fb,#f5576c)' },
  { icon: '🎤', value: '1,845', label: '今日背诵', bg: 'linear-gradient(135deg,#4facfe,#00f2fe)' },
  { icon: '📁', value: '892', label: '资料同步', bg: 'linear-gradient(135deg,#43e97b,#38f9d7)' },
]
const dailyData = [
  { label: 'AI刷题', value: '5,230次', percent: 85, color: '#667eea' },
  { label: '背诵检测', value: '1,845次', percent: 60, color: '#67c23a' },
  
  { label: '资料下载', value: '892次', percent: 30, color: '#e6a23c' },
]
const trends = [
  { label: '新增用户', up: true, percent: 12.5 },
  { label: '刷题量', up: true, percent: 8.3 },
  { label: '背诵打卡', up: false, percent: 2.1 },
  
  { label: '资料同步', up: true, percent: 4 },
]
</script>

<style scoped>
.stat-row { margin-bottom: 16px; }
.dash-stat { border-radius: 12px; padding: 20px; color: #fff; display: flex; align-items: center; gap: 14px; }
.dash-icon { font-size: 36px; }
.dash-num { display: block; font-size: 24px; font-weight: 700; }
.dash-label { font-size: 13px; opacity: 0.85; }
.dash-card { border-radius: 12px; }
.chart-placeholder { padding: 8px 0; }
.bar-row { display: flex; align-items: center; gap: 10px; margin-bottom: 14px; }
.bar-label { width: 70px; font-size: 13px; color: #606266; }
.bar-track { flex: 1; height: 8px; background: #f0f2f5; border-radius: 4px; overflow: hidden; }
.bar-fill { height: 100%; border-radius: 4px; transition: width 0.5s; }
.bar-val { font-size: 12px; color: #909399; width: 50px; text-align: right; }
.trend-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #f5f5f5; font-size: 14px; }
</style>
