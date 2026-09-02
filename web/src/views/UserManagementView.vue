<template>
  <div class="user-management-container">
    <el-card>
      <div slot="header">
        <h2>用户管理</h2>
      </div>
      
      <el-form :inline="true" :model="searchForm" @submit.prevent="searchUsers">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchUsers">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="users" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column prop="role" label="角色">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'admin' ? 'danger' : (scope.row.role === 'advanced' ? 'warning' : 'success')">
              {{ getRoleLabel(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button 
              size="mini" 
              @click="editUser(scope.row)"
              :disabled="scope.row.id === currentUserId"
            >
              编辑
            </el-button>
            <el-button 
              size="mini" 
              type="danger" 
              @click="deleteUser(scope.row)"
              :disabled="scope.row.role === 'admin'"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <span class="pagination-total">共 {{ totalPages }} 页</span>
        <el-pagination
          background
          layout="prev, pager, next"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
    
    <!-- 编辑用户对话框 -->
    <el-dialog title="编辑用户" v-model="editDialogVisible" width="500px">
      <el-form :model="editingUser" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editingUser.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editingUser.email"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editingUser.role" style="width: 100%">
            <el-option 
              v-for="option in availableRoleOptions" 
              :key="option.value" 
              :label="option.label" 
              :value="option.value"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveUser">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { reactive, ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { getUserPage, updateUser, deleteUser as deleteUserApi, toggleUserStatus as toggleStatusApi } from '@/api/userManagement'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'UserManagementView',
  setup() {
    const store = useStore()
    const loading = ref(false)
    const editDialogVisible = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    
    const totalPages = computed(() => Math.ceil(total.value / pageSize.value))
    
    const searchForm = reactive({
      username: ''
    })
    
    const users = ref([])
    const editingUser = ref({})
    
    // 获取当前用户信息
    const currentUserId = computed(() => {
      const userInfoStr = localStorage.getItem('userInfo')
      if (!userInfoStr) return null
      try {
        const userInfo = JSON.parse(userInfoStr)
        return userInfo.id || null
      } catch (e) {
        return null
      }
    })
    
    const currentUserRole = computed(() => store.state.user.role)
    
    // 角色标签映射
    const getRoleLabel = (role) => {
      const roleLabels = {
        admin: '管理员',
        advanced: '高级用户',
        user: '普通用户'
      }
      return roleLabels[role] || '未知'
    }
    
    // 可选的角色选项
    const availableRoleOptions = [
      { label: '管理员', value: 'admin' },
      { label: '高级用户', value: 'advanced' },
      { label: '普通用户', value: 'user' }
    ]
    
    const loadUsers = async () => {
      loading.value = true
      try {
        const res = await getUserPage({
          username: searchForm.username,
          current: currentPage.value,
          size: pageSize.value
        })
        
        // Backend returns Result<Map>, so res.data is the Map { items: [], total: ... }
        const data = res.data
        users.value = data.items || []
        total.value = data.total || 0
      } catch (err) {
        console.error(err)
        // ElMessage.error('加载用户列表失败')
      } finally {
        loading.value = false
      }
    }
    
    const searchUsers = () => {
      currentPage.value = 1
      loadUsers()
    }
    
    const resetSearch = () => {
      searchForm.username = ''
      currentPage.value = 1
      loadUsers()
    }
    
    const handlePageChange = (page) => {
      currentPage.value = page
      loadUsers()
    }
    
    const toggleUserStatus = async (user) => {
      try {
        // Toggle status: 0 <-> 1
        const newStatus = user.status === 1 ? 0 : 1
        await toggleStatusApi(user.id, newStatus)
        user.status = newStatus
        ElMessage.success(`用户 ${user.username} 状态已更新`)
      } catch (error) {
        // ElMessage.error('更新状态失败')
      }
    }

    const editUser = (user) => {
      if (user.id === currentUserId.value) {
        return
      }
      editingUser.value = { ...user }
      editDialogVisible.value = true
    }

    const saveUser = async () => {
      try {
        await updateUser(editingUser.value.id, editingUser.value)
        ElMessage.success('更新成功')
        editDialogVisible.value = false
        loadUsers()
      } catch (error) {
        // ElMessage.error('更新失败')
      }
    }

    const deleteUser = (user) => {
      ElMessageBox.confirm(
        `确定要删除用户 ${user.username} 吗?`,
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      ).then(async () => {
        try {
          await deleteUserApi(user.id)
          ElMessage.success('删除成功')
          loadUsers()
        } catch (error) {
          // ElMessage.error('删除失败')
        }
      })
    }
    
    onMounted(() => {
      // 仅管理员可以访问该页面
      if (currentUserRole.value !== 'admin') {
        window.location.href = '/'
        return
      }
      loadUsers()
    })

    return {
      loading,
      editDialogVisible,
      currentPage,
      pageSize,
      total,
      totalPages,
      searchForm,
      users,
      editingUser,
      currentUserId,
      getRoleLabel,
      availableRoleOptions,
      searchUsers,
      resetSearch,
      handlePageChange,
      toggleUserStatus,
      editUser,
      saveUser,
      deleteUser
    }
  }
}
</script>

<style scoped>
.user-management-container {
  padding: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.pagination-total {
  margin-right: 10px;
  color: #606266;
  font-size: 14px;
}
</style>
