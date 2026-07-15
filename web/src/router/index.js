import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/components/Layout.vue'),
    redirect: '/home',
    children: [
      { path: 'home', name: 'Home', component: () => import('@/views/Home.vue'), meta: { title: '学习首页' } },
      { path: 'question', name: 'Question', component: () => import('@/views/QuestionSearch.vue'), meta: { title: 'AI刷题' } },
      { path: 'errors', name: 'Errors', component: () => import('@/views/ErrorBook.vue'), meta: { title: '错题本' } },
      { path: 'pk', name: 'PkBattle', component: () => import('@/views/PkBattle.vue'), meta: { title: '刷题PK' } },
      { path: 'recite', name: 'Recite', component: () => import('@/views/Recite.vue'), meta: { title: 'AI背诵' } },
      { path: 'words', name: 'Words', component: () => import('@/views/WordBook.vue'), meta: { title: '单词背诵' } },
      { path: 'notes', name: 'Notes', component: () => import('@/views/Notes.vue'), meta: { title: '思维导图笔记' } },
      { path: 'ai', name: 'AiGenerate', component: () => import('@/views/AiGenerate.vue'), meta: { title: 'AI创作' } },
      { path: 'score', name: 'Score', component: () => import('@/views/ScoreAnalysis.vue'), meta: { title: '成绩分析' } },
      { path: 'resources', name: 'Resources', component: () => import('@/views/Resources.vue'), meta: { title: '学习资料' } },
      { path: 'chat', name: 'AiChat', component: () => import('@/views/AiChat.vue'), meta: { title: 'AI助手' } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/Profile.vue'), meta: { title: '个人中心' } },
    ]
  },
  {
    path: '/admin',
    component: () => import('@/components/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requireAdmin: true },
    children: [
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('@/views/admin/Dashboard.vue'), meta: { title: '管理后台' } },
      { path: 'users', name: 'AdminUsers', component: () => import('@/views/admin/UserManage.vue'), meta: { title: '用户管理' } },
      { path: 'resources', name: 'AdminResources', component: () => import('@/views/admin/ResourceManage.vue'), meta: { title: '资料管理' } },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = (to.meta.title || 'iyd学习') + ' - 学生智能学习系统'
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router