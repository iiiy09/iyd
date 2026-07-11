<template>
  <view class="login-page">
    <view class="login-logo">📚</view>
    <view class="login-title">iyd学习</view>
    <view class="login-subtitle">学生智能学习系统</view>
    <view class="login-form">
      <input class="input" v-model="phone" placeholder="请输入手机号" type="number" maxlength="11" />
      <input class="input" v-model="password" placeholder="请输入密码" password v-if="tab==='password'" />
      <input class="input" v-model="code" placeholder="验证码" v-else />
      <view class="code-btn" v-if="tab==='code'" @click="sendCode">{{ codeText }}</view>
      <picker v-if="isRegister" mode="selector" :range="stages" @change="onStageChange">
        <view class="picker">{{ stage || '选择学段' }}</view>
      </picker>
      <button class="login-btn" @click="handleSubmit" :loading="loading">{{ isRegister ? '注册' : '登录' }}</button>
      <view class="switch-text" @click="isRegister=!isRegister">{{ isRegister ? '已有账号？去登录' : '没有账号？去注册' }}</view>
    </view>
    <view class="tab-switch">
      <text :class="{active:tab==='code'}" @click="tab='code'">验证码登录</text>
      <text :class="{active:tab==='password'}" @click="tab='password'">密码登录</text>
    </view>
  </view>
</template>

<script>
const api = require('@/utils/api')
module.exports = {
  data() { return { phone:'', password:'', code:'', tab:'code', isRegister:false, stage:'初中', loading:false, codeText:'获取验证码', stages:['小学','初中','高中','大学'] } },
  methods: {
    onStageChange(e) { this.stage = this.stages[e.detail.value] },
    sendCode() { this.codeText='60s'; uni.showToast({title:'验证码已发送'}); setTimeout(()=>{this.codeText='获取验证码'},60000) },
    async handleSubmit() {
      if (!this.phone) return uni.showToast({ title: '请输入手机号', icon: 'none' })
      this.loading = true
      try {
        const url = this.isRegister ? '/user/register' : '/user/login'
        const data = { phone: this.phone, password: this.password || '123456', stage: this.stage, platform: 'miniapp' }
        const res = await api.post(url, data)
        uni.setStorageSync('token', res.data.token)
        uni.setStorageSync('userInfo', JSON.stringify(res.data))
        uni.switchTab({ url: '/pages/home/home' })
      } catch(e) {} finally { this.loading = false }
    }
  }
}
</script>

<style>
.login-page { min-height:100vh; background:linear-gradient(135deg,#667eea,#764ba2); display:flex; flex-direction:column; align-items:center; justify-content:center; padding:40rpx; }
.login-logo { font-size:80rpx; margin-bottom:20rpx; }
.login-title { font-size:48rpx; color:#fff; font-weight:700; }
.login-subtitle { font-size:28rpx; color:rgba(255,255,255,0.7); margin-bottom:60rpx; }
.login-form { width:100%; background:rgba(255,255,255,0.95); border-radius:20rpx; padding:40rpx; }
.input { height:88rpx; border:2rpx solid #e4e7ed; border-radius:12rpx; padding:0 20rpx; margin-bottom:20rpx; font-size:30rpx; }
.code-btn { position:absolute; right:60rpx; color:#667eea; font-size:26rpx; }
.picker { height:88rpx; border:2rpx solid #e4e7ed; border-radius:12rpx; padding:0 20rpx; margin-bottom:20rpx; line-height:88rpx; color:#909399; }
.login-btn { width:100%; height:88rpx; background:linear-gradient(135deg,#667eea,#764ba2); color:#fff; border-radius:12rpx; font-size:32rpx; margin-top:10rpx; }
.switch-text { text-align:center; margin-top:20rpx; color:#667eea; font-size:26rpx; }
.tab-switch { margin-top:30rpx; display:flex; gap:30rpx; }
.tab-switch text { color:rgba(255,255,255,0.6); font-size:28rpx; }
.tab-switch text.active { color:#fff; font-weight:600; }
</style>
