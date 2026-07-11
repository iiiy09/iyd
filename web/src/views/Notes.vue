<template>
  <div class="notes-page">
    <div class="page-header">
      <h2>🧠 思维导图笔记</h2>
      <div class="header-actions">
        <el-button type="primary" @click="showCreateDialog = true" :icon="Plus">新建笔记</el-button>
        <el-upload :auto-upload="false" accept="image/*" style="display:inline-block;margin-left:10px">
          <el-button :icon="Camera">拍照OCR录入</el-button>
        </el-upload>
      </div>
    </div>
    <el-row :gutter="20">
      <el-col :span="5">
        <el-card class="folder-sidebar">
          <template #header><span>📂 文件夹</span></template>
          <div class="folder-list">
            <div v-for="f in folders" :key="f.id" class="folder-item" :class="{ active: activeFolder === f.id }" @click="activeFolder = f.id">
              <span>📁 {{ f.folderName }}</span>
              <span class="folder-count">{{ f.noteCount || 0 }}</span>
            </div>
          </div>
          <el-button text @click="addFolder" style="margin-top:8px">+ 新建文件夹</el-button>
        </el-card>
      </el-col>
      <el-col :span="19">
        <div class="notes-grid">
          <div v-for="n in notes" :key="n.id" class="note-card" @click="viewNote(n)">
            <div class="note-cover" v-if="n.mindMapImage">
              <img :src="n.mindMapImage" alt="mindmap" />
            </div>
            <div class="note-body">
              <h4>{{ n.title || '未命名笔记' }}</h4>
              <p class="note-preview">{{ n.content?.substring(0, 80) || '点击编辑内容...' }}</p>
              <div class="note-footer">
                <el-tag size="small" :type="n.noteType === 3 ? 'warning' : ''">{{ n.noteType === 3 ? 'OCR录入' : '文本笔记' }}</el-tag>
                <span class="note-date">{{ formatDate(n.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-dialog v-model="showCreateDialog" title="新建笔记" width="600px">
      <el-form :model="noteForm">
        <el-form-item label="标题"><el-input v-model="noteForm.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="noteForm.content" type="textarea" :rows="6" /></el-form-item>
        <el-form-item label="文件夹"><el-select v-model="noteForm.folderId" placeholder="选择文件夹" style="width:100%"><el-option v-for="f in folders" :key="f.id" :label="f.folderName" :value="f.id" /></el-select></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="createNote">创建</el-button>
        <el-button type="success" @click="createAndGenerate">创建并生成思维导图</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showNoteDetail" title="笔记详情" width="700px">
      <div v-if="currentNote">
        <h3>{{ currentNote.title }}</h3>
        <div class="note-content">{{ currentNote.content }}</div>
        <div v-if="currentNote.mindMapImage" class="mindmap-preview">
          <h4>思维导图</h4>
          <img :src="currentNote.mindMapImage" alt="mindmap" style="max-width:100%;border-radius:8px" />
        </div>
      </div>
      <template #footer>
        <el-button @click="showNoteDetail = false">关闭</el-button>
        <el-button type="primary" @click="generateMindMap">生成思维导图</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import http from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Camera } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const showCreateDialog = ref(false)
const showNoteDetail = ref(false)
const currentNote = ref(null)
const activeFolder = ref(0)
const notes = ref([])
const folders = ref([])
const noteForm = ref({ title: '', content: '', folderId: 0 })

onMounted(() => { loadNotes(); loadFolders() })

async function loadNotes() {
  const res = await http.get('/note/list', { params: { folderId: activeFolder.value } })
  notes.value = res.data || []
}
async function loadFolders() {
  const res = await http.get('/note/folders')
  folders.value = res.data || []
}
async function createNote() {
  await http.post('/note', noteForm.value)
  ElMessage.success('笔记创建成功')
  showCreateDialog.value = false
  noteForm.value = { title: '', content: '', folderId: 0 }
  loadNotes()
}
async function createAndGenerate() {
  const res = await http.post('/note', noteForm.value)
  await http.post('/note/' + res.data.id + '/mindmap')
  ElMessage.success('笔记创建并生成思维导图成功')
  showCreateDialog.value = false
  noteForm.value = { title: '', content: '', folderId: 0 }
  loadNotes()
}
async function viewNote(note) {
  currentNote.value = note
  showNoteDetail.value = true
}
async function generateMindMap() {
  if (!currentNote.value) return
  const res = await http.post('/note/' + currentNote.value.id + '/mindmap')
  currentNote.value.mindMapImage = res.data.mindMapImage
  ElMessage.success('思维导图生成成功！')
}
async function addFolder() {
  const { value } = await ElMessageBox.prompt('文件夹名称', '新建文件夹')
  if (value) {
    await http.post('/note/folder', null, { params: { folderName: value } })
    loadFolders()
  }
}
function formatDate(d) { return d ? dayjs(d).format('MM-DD HH:mm') : '' }
</script>

<style scoped>
.notes-page { max-width: 1100px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; }
.header-actions { display: flex; gap: 8px; }
.folder-sidebar { border-radius: 14px; height: calc(100vh - 200px); }
.folder-item { display: flex; justify-content: space-between; padding: 8px 10px; border-radius: 8px; cursor: pointer; font-size: 14px; }
.folder-item:hover, .folder-item.active { background: #ecf0ff; color: #667eea; }
.folder-count { color: #c0c4cc; font-size: 12px; }
.notes-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.note-card { background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer; transition: all 0.3s; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.note-card:hover { transform: translateY(-3px); box-shadow: 0 6px 20px rgba(0,0,0,0.1); }
.note-cover { height: 140px; background: #f5f7fa; display: flex; align-items: center; justify-content: center; font-size: 40px; }
.note-cover img { width: 100%; height: 100%; object-fit: cover; }
.note-body { padding: 14px; }
.note-body h4 { font-size: 15px; color: #303133; margin-bottom: 6px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.note-preview { font-size: 13px; color: #909399; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.note-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; }
.note-date { font-size: 12px; color: #c0c4cc; }
.note-content { white-space: pre-wrap; font-size: 14px; line-height: 1.8; color: #606266; max-height: 300px; overflow-y: auto; }
.mindmap-preview { margin-top: 16px; }
</style>
