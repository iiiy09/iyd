import { defineStore } from 'pinia'
import { ref } from 'vue'
import http from '@/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  async function login(phone, password, platform) {
    const res = await http.post('/user/login', { phone, password, platform })
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    userInfo.value = res.data
    return res
  }

  async function register(phone, password, stage, platform) {
    const res = await http.post('/user/register', { phone, password, stage, platform })
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    userInfo.value = res.data
    return res
  }

  async function fetchUserInfo() {
    const res = await http.get('/user/info')
    userInfo.value = res.data
    return res
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return { token, userInfo, login, register, fetchUserInfo, logout }
})
