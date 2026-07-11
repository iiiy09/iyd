<template>
  <div class="login-page">
    <div class="login-bg"></div>
    <div class="login-card">
      <div class="login-header">
        <h1>📚 iyd学习</h1>
        <p>学生智能学习系统</p>
      </div>
      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="验证码登录" name="code">
          <el-form :model="loginForm" :rules="rules" ref="formRef">
            <el-form-item prop="phone">
              <el-input v-model="loginForm.phone" placeholder="请输入手机号" size="large" />
            </el-form-item>
            <el-form-item prop="code">
              <el-input v-model="loginForm.code" placeholder="验证码" size="large">
                <template #append><el-button @click="sendCode">{{ codeText }}</el-button></template>
              </el-input>
            </el-form-item>
            <el-form-item prop="stage" v-if="isRegister">
              <el-select v-model="loginForm.stage" placeholder="选择学段" size="large" style="width:100%">
                <el-option label="小学" value="小学" />
                <el-option label="初中" value="初中" />
                <el-option label="高中" value="高中" />
                <el-option label="大学" value="大学" />
              </el-select>
            </el-form-item>
            <el-button type="primary" size="large" @click="handleSubmit" :loading="loading" class="login-btn">{{ isRegister ? '注册' : '登录' }}</el-button>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="密码登录" name="password">
          <el-form :model="loginForm" :rules="rules" ref="formRef2">
            <el-form-item prop="phone">
              <el-input v-model="loginForm.phone" placeholder="请输入手机号" size="large" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" size="large" show-password />
            </el-form-item>
            <el-form-item prop="stage" v-if="isRegister">
              <el-select v-model="loginForm.stage" placeholder="选择学段" size="large" style="width:100%">
                <el-option label="小学" value="小学" />
                <el-option label="初中" value="初中" />
                <el-option label="高中" value="高中" />
                <el-option label="大学" value="大学" />
              </el-select>
            </el-form-item>
            <el-button type="primary" size="large" @click="handleSubmit" :loading="loading" class="login-btn">{{ isRegister ? '注册' : '登录' }}</el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      <div class="login-footer">
        <el-link type="primary" @click="isRegister = !isRegister">{{ isRegister ? '已有账号？去登录' : '没有账号？去注册' }}</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('code')
const isRegister = ref(false)
const loading = ref(false)
const codeText = ref('获取验证码')
const loginForm = reactive({ phone: '', password: '', code: '', stage: '初中' })
const rules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

function sendCode() {
  codeText.value = '60s后重发'
  ElMessage.success('验证码已发送（演示模式：输入任意6位数字）')
  setTimeout(() => { codeText.value = '获取验证码' }, 60000)
}

async function handleSubmit() {
  loading.value = true
  try {
    if (isRegister.value) {
      await userStore.register(loginForm.phone, loginForm.password || '123456', loginForm.stage, 'web')
      ElMessage.success('注册成功！')
    } else {
      await userStore.login(loginForm.phone, loginForm.password || '', 'web')
    }
    router.push('/home')
  } catch (e) { /* handled by interceptor */ }
  finally { loading.value = false }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh; display: flex; align-items: center; justify-content: center; position: relative; overflow: hidden;
}
.login-bg {
  position: absolute; inset: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  z-index: 0;
}
.login-bg::after {
  content: ''; position: absolute; top: 0; left: 0; right: 0; bottom: 0;
  background: radial-gradient(circle at 20% 50%, rgba(255,255,255,0.1) 0%, transparent 50%),
              radial-gradient(circle at 80% 50%, rgba(255,255,255,0.1) 0%, transparent 50%);
}
.login-card {
  position: relative; z-index: 1; width: 420px; background: rgba(255,255,255,0.95);
  border-radius: 16px; padding: 40px; box-shadow: 0 24px 80px rgba(0,0,0,0.2);
  backdrop-filter: blur(10px);
}
.login-header { text-align: center; margin-bottom: 28px; }
.login-header h1 { font-size: 28px; color: #303133; margin-bottom: 6px; }
.login-header p { font-size: 14px; color: #909399; }
.login-tabs { margin-bottom: 8px; }
.login-btn { width: 100%; height: 44px; font-size: 16px; margin-top: 8px; }
.login-footer { text-align: center; margin-top: 16px; }
</style>
