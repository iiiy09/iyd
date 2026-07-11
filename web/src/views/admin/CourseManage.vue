<template>
  <div class="course-mgmt">
    <div class="page-header"><h3>课程管理</h3><el-button type="primary" @click="showUpload=true" :icon="Plus">上传课程</el-button></div>
    <el-table :data="courses" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="courseName" label="课程名称" />
      <el-table-column prop="stage" label="学段" width="80" />
      <el-table-column prop="grade" label="年级" width="80" />
      <el-table-column prop="subject" label="科目" width="80" />
      <el-table-column prop="playCount" label="播放量" width="80" />
      <el-table-column label="操作" width="120"><template><el-button size="small" type="danger">删除</el-button></template></el-table-column>
    </el-table>

    <el-dialog v-model="showUpload" title="上传课程" width="500px">
      <el-form :model="courseForm">
        <el-form-item label="课程名称"><el-input v-model="courseForm.name" /></el-form-item>
        <el-form-item label="学段"><el-select v-model="courseForm.stage"><el-option v-for="s in stages" :key="s" :label="s" :value="s" /></el-select></el-form-item>
        <el-form-item label="年级"><el-select v-model="courseForm.grade"><el-option v-for="g in grades" :key="g" :label="g" :value="g" /></el-select></el-form-item>
        <el-form-item label="科目"><el-select v-model="courseForm.subject"><el-option v-for="s in subjects" :key="s" :label="s" :value="s" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="showUpload=false">取消</el-button><el-button type="primary" @click="uploadCourse">上传</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import http from '@/api'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const showUpload = ref(false)
const courses = ref([])
const courseForm = ref({ name: '', stage: '初中', grade: '七年级', subject: '数学' })
const stages = ['小学', '初中', '高中', '大学']
const grades = ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级', '七年级', '八年级', '九年级']
const subjects = ['语文', '数学', '英语', '物理', '化学', '生物', '历史', '地理', '政治']

async function uploadCourse() {
  const fd = new FormData()
  fd.append('courseName', courseForm.value.name)
  fd.append('stage', courseForm.value.stage)
  fd.append('grade', courseForm.value.grade)
  fd.append('subject', courseForm.value.subject)
  await http.post('/admin/course/upload', fd)
  ElMessage.success('课程上传成功')
  showUpload.value = false
}
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h3 { font-size: 18px; }
</style>
