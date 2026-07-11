<template>
  <div class="courses-page">
    <div class="page-header">
      <h2>🎬 精品网课</h2>
      <div class="filters">
        <el-select v-model="stage" placeholder="学段" style="width:120px"><el-option v-for="s in stages" :key="s" :label="s" :value="s" /></el-select>
        <el-select v-model="grade" placeholder="年级" style="width:120px"><el-option v-for="g in grades" :key="g" :label="g" :value="g" /></el-select>
        <el-select v-model="subject" placeholder="科目" style="width:120px"><el-option v-for="s in subjects" :key="s" :label="s" :value="s" /></el-select>
      </div>
    </div>
    <div class="course-grid">
      <div v-for="c in courses" :key="c.id" class="course-card" @click="playCourse(c)">
        <div class="course-cover">
          <img src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='280' height='160'%3E%3Crect fill='%23667eea' width='280' height='160'/%3E%3Ctext fill='white' x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' font-size='28'%3E🎬%3C/text%3E%3C/svg%3E" alt="course" />
          <div class="play-btn">▶</div>
        </div>
        <div class="course-info">
          <h4>{{ c.courseName }}</h4>
          <div class="course-meta">
            <span>{{ c.teacherName }}</span>
            <span>{{ formatDuration(c.duration) }}</span>
          </div>
          <div class="course-tags">
            <el-tag size="small">{{ c.category }}</el-tag>
            <span class="play-count">{{ c.playCount }}次播放</span>
          </div>
        </div>
      </div>
    </div>
    <el-pagination v-if="total > 12" layout="prev,pager,next" :total="total" :page-size="12" @current-change="loadCourses" class="pagination" />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import http from '@/api'
import { ElMessage } from 'element-plus'

const stage = ref('')
const grade = ref('')
const subject = ref('')
const courses = ref([])
const total = ref(0)
const page = ref(1)

const stages = ['小学', '初中', '高中', '大学']
const grades = ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级', '七年级', '八年级', '九年级', '高一', '高二', '高三', '大一', '大二', '大三']
const subjects = ['语文', '数学', '英语', '物理', '化学', '生物', '历史', '地理', '政治', '计算机']

onMounted(() => loadCourses())
watch([stage, grade, subject], () => loadCourses())

async function loadCourses(p = 1) {
  page.value = p
  const res = await http.get('/course/list', { params: { page: p, size: 12, stage: stage.value, grade: grade.value, subject: subject.value } })
  if (res.data?.records) { courses.value = res.data.records; total.value = res.data.total }
}

function playCourse(c) { ElMessage.info('正在跳转课程播放页：' + c.courseName) }
function formatDuration(s) { return s ? Math.floor(s/60) + '分钟' : '45分钟' }
</script>

<style scoped>
.courses-page { max-width: 1100px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; color: #303133; }
.filters { display: flex; gap: 10px; }
.course-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.course-card { background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer; transition: transform 0.3s, box-shadow 0.3s; }
.course-card:hover { transform: translateY(-4px); box-shadow: 0 8px 25px rgba(0,0,0,0.1); }
.course-cover { position: relative; height: 160px; overflow: hidden; }
.course-cover img { width: 100%; height: 100%; object-fit: cover; }
.play-btn {
  position: absolute; inset: 0; display: flex; align-items: center; justify-content: center; font-size: 40px; color: #fff; background: rgba(0,0,0,0.3); opacity: 0; transition: opacity 0.3s;
}
.course-card:hover .play-btn { opacity: 1; }
.course-info { padding: 14px; }
.course-info h4 { font-size: 15px; color: #303133; margin-bottom: 6px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.course-meta { font-size: 12px; color: #909399; display: flex; justify-content: space-between; }
.course-tags { display: flex; justify-content: space-between; align-items: center; margin-top: 8px; }
.play-count { font-size: 12px; color: #c0c4cc; }
.pagination { margin-top: 24px; justify-content: center; display: flex; }
</style>
