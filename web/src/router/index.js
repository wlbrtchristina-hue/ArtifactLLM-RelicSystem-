import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ArtifactsView from '../views/ArtifactsView.vue'
import SearchView from '../views/SearchView.vue'
import AboutView from '../views/AboutView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import ArtifactCreateView from '../views/ArtifactCreateView.vue'
import ArtifactSearchView from '../views/ArtifactSearchView.vue'
import UserManagementView from '../views/UserManagementView.vue'
import ModelReviewView from '../views/ModelReviewView.vue'
import AIAssistantView from '../views/AIAssistantView.vue'
import FeedbackManagementView from '../views/FeedbackManagementView.vue'
// 数据建模相关视图
import ModelingTypeCreateView from '../views/ModelingTypeCreateView.vue'
import ModelingEntityCreateView from '../views/ModelingEntityCreateView.vue'
import ModelingBatchImportView from '../views/ModelingBatchImportView.vue'
import ModelingMyModelsView from '../views/ModelingMyModelsView.vue'
// 文物详情页
import ArtifactDetailView from '../views/ArtifactDetailView.vue'
// 反馈页面
import FeedbackView from '../views/FeedbackView.vue'

const routes = [
  // 已有建模页面只允许高级用户和管理员访问
  {
    path: '/',
    name: 'Home',
    component: HomeView
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterView
  },
  {
    path: '/artifacts',
    name: 'Artifacts',
    component: ArtifactSearchView, // 修改为新的瀑布流页面
    meta: { requiresAuth: false }
  },
  {
    path: '/artifact/:id',
    name: 'ArtifactDetail',
    component: ArtifactDetailView,
    meta: { requiresAuth: false }
  },
  {
    path: '/artifact/create',
    name: 'ArtifactCreate',
    component: ArtifactCreateView,
    meta: { requiresAuth: true }
  },
  {
    path: '/artifact/search',
    name: 'ArtifactSearch',
    component: ArtifactSearchView,
    meta: { requiresAuth: true }
  },
  {
    path: '/search',
    name: 'Search',
    component: SearchView,
    meta: { requiresAuth: false }
  },
  {
    path: '/user/management',
    name: 'UserManagement',
    component: UserManagementView,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/model/review',
    name: 'ModelReview',
    component: ModelReviewView,
    meta: { requiresAuth: true, requiresAdvanced: true } // 高级用户和管理员
  },
  {
    path: '/ai/assistant',
    name: 'AIAssistant',
    component: AIAssistantView,
    meta: { requiresAuth: false }
  },
  {
    path: '/feedback/management',
    name: 'FeedbackManagement',
    component: FeedbackManagementView,
    meta: { requiresAuth: true }
  },
  {
    path: '/feedback',
    name: 'Feedback',
    component: FeedbackView,
    meta: { requiresAuth: true }
  },
  // 数据建模相关路由
  {
    path: '/modeling/type-create',
    name: 'ModelingTypeCreate',
    component: ModelingTypeCreateView,
    meta: { requiresAuth: true }
  },
  {
    path: '/modeling/entity-create',
    name: 'ModelingEntityCreate',
    component: ModelingEntityCreateView,
    meta: { requiresAuth: true }
  },
  {
    path: '/modeling/batch-import',
    name: 'ModelingBatchImport',
    component: ModelingBatchImportView,
    meta: { requiresAuth: true }
  },
  {
    path: '/modeling/my-models',
    name: 'ModelingMyModels',
    component: ModelingMyModelsView,
    meta: { requiresAuth: true, requiresAdvanced: true } // 只允许高级用户和管理员访问
  },
  {
    path: '/about',
    name: 'About',
    component: AboutView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
  const role = localStorage.getItem('role') || 'guest'
  
  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/login')
  } else if (to.meta.requiresAdmin && role !== 'admin') {
    next('/')
  } else if (to.meta.requiresAdvanced && !['admin', 'advanced'].includes(role)) {
    next('/')
  } else {
    next()
  }
})

export default router