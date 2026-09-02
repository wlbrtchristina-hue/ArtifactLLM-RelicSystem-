<template>
  <el-container class="app-container">
    <el-header class="app-header">
      <div class="nav-container">
        <!-- 左侧导航菜单 -->
        <el-menu 
          mode="horizontal" 
          :router="true" 
          :default-active="activeIndex"
          background-color="#2c3e50"
          text-color="#fff"
          active-text-color="#42b983"
          class="main-nav"
        >
          <el-menu-item index="/">首页</el-menu-item>
          
          <!-- 文物浏览对所有用户开放 -->
          <el-menu-item index="/artifacts">文物浏览</el-menu-item>
          
          <el-menu-item index="/search">智能检索</el-menu-item>
          <el-menu-item index="/ai/assistant">AI助手</el-menu-item>
          
          <!-- 数据建模菜单只对登录用户显示 -->
          <el-sub-menu v-if="isLoggedIn" index="modeling">
            <template #title>数据建模</template>
            <el-menu-item index="/modeling/type-create">新增实体类型</el-menu-item>
            <el-menu-item index="/modeling/entity-create">手动添加实体</el-menu-item>
            <!-- 批量导入功能已隐藏 -->
            <el-menu-item v-if="isAdvancedUser || isAdmin" index="/modeling/my-models">已有建模</el-menu-item>
          </el-sub-menu>
          
          <!-- 系统管理菜单只对高级用户和管理员显示 -->
          <el-sub-menu v-if="isAdvancedUser || isAdmin" index="management">
            <template #title>系统管理</template>
            <el-menu-item index="/model/review">建模审核</el-menu-item>
            <el-menu-item index="/feedback/management">反馈管理</el-menu-item>
            <el-menu-item v-if="isAdmin" index="/user/management">用户管理</el-menu-item>
          </el-sub-menu>
          
          <!-- 反馈菜单项，对普通用户和高级用户显示 -->
          <el-menu-item 
            v-if="isLoggedIn && (userRole === 'user' || userRole === 'advanced')" 
            index="/feedback"
          >
            反馈
          </el-menu-item>
        </el-menu>
        
        <!-- 右侧用户下拉菜单 -->
        <div class="user-dropdown-section">
          <el-dropdown @command="handleUserCommand" class="user-dropdown">
            <span class="user-dropdown-link">
              <i class="el-icon-user"></i>
              {{ isLoggedIn ? username : '用户' }}
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="!isLoggedIn" command="login">登录</el-dropdown-item>
                <el-dropdown-item v-if="!isLoggedIn" command="register">注册</el-dropdown-item>
                <el-dropdown-item v-if="isLoggedIn" command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    
    <el-main class="app-main">
      <router-view></router-view>
    </el-main>
    
    <el-footer class="app-footer">
      <p>&copy; 2025 文物资源知识管理系统. All rights reserved.</p>
    </el-footer>
  </el-container>
</template>

<script>
import { computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  name: 'App',
  setup() {
    const store = useStore()
    const router = useRouter()
    
    // 初始化用户状态
    onMounted(() => {
      store.commit('user/init')
    })
    
    const isLoggedIn = computed(() => store.state.user.isLoggedIn)
    const username = computed(() => store.state.user.username)
    const userRole = computed(() => store.state.user.role)
    
    const isAdmin = computed(() => userRole.value === 'admin')
    const isAdvancedUser = computed(() => userRole.value === 'advanced')
    
    const handleUserCommand = (command) => {
      switch (command) {
        case 'login':
          router.push('/login')
          break
        case 'register':
          router.push('/register')
          break
        case 'logout':
          store.commit('user/logout')
          router.push('/login')
          break
      }
    }
    
    return {
      isLoggedIn,
      username,
      userRole,
      isAdmin,
      isAdvancedUser,
      handleUserCommand,
      activeIndex: '/'
    }
  }
}
</script>

<style scoped>
.app-container {
  min-height: 100vh;
}

.app-header {
  padding: 0;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  height: auto;
}

.nav-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #2c3e50;
  width: 100%;
  padding: 0 20px;
}

.main-nav {
  flex: 1;
}

.user-dropdown-section {
  margin-left: auto;
}

.user-dropdown-link {
  display: flex;
  align-items: center;
  color: #fff;
  cursor: pointer;
  padding: 0 12px;
  height: 60px;
  transition: background-color 0.3s;
}

.user-dropdown-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.user-dropdown-link i {
  margin-right: 5px;
}

.app-main {
  background-color: #f5f7fa;
  padding: 0;
  min-height: calc(100vh - 120px);
}

.app-footer {
  background-color: #2c3e50;
  color: #ecf0f1;
  text-align: center;
  padding: 20px;
}

.app-footer p {
  margin: 0;
}

/* 确保菜单项没有下边框 */
:deep(.el-menu--horizontal) {
  border-bottom: none;
}

:deep(.el-menu--horizontal > .el-menu-item) {
  border-bottom: none;
}

:deep(.el-menu--horizontal > .el-sub-menu) {
  border-bottom: none;
}

:deep(.el-menu--horizontal > .el-sub-menu .el-sub-menu__title) {
  border-bottom: none;
}

/* 下拉菜单样式 */
:deep(.user-dropdown .el-dropdown-menu) {
  background-color: #2c3e50;
  border: none;
}

:deep(.user-dropdown .el-dropdown-menu__item) {
  color: #fff;
}

:deep(.user-dropdown .el-dropdown-menu__item:hover) {
  background-color: rgba(255, 255, 255, 0.1);
  color: #42b983;
}
</style>