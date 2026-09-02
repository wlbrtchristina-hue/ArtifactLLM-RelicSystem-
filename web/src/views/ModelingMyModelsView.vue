<template>
  <div class="modeling-my-models-view">
    <el-card class="models-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2><el-icon><Collection /></el-icon> 已有建模</h2>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="实体类型" name="types">
          <div class="toolbar">
            <el-button type="primary" @click="$router.push('/modeling/type-create')">
              <el-icon><Plus /></el-icon> 新增实体类型
            </el-button>
          </div>
          
          <el-table :data="paginatedEntityTypes" style="width: 100%" v-loading="loading">
            <el-table-column prop="name" label="模型名称" width="200"></el-table-column>
            <el-table-column prop="description" label="描述"></el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="200"></el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button size="mini" @click="viewTypeDetails(scope.row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 实体类型分页 -->
          <div class="pagination-container">
            <span class="pagination-total">共 {{ typeTotalPages }} 页</span>
            <el-pagination
              background
              layout="prev, pager, next"
              :current-page="currentTypePage"
              :page-size="pageSize"
              :total="totalTypes"
              @current-change="handleTypePageChange"
            />
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="实体列表" name="entities">
          <div class="toolbar">
            <el-button type="primary" @click="$router.push('/modeling/entity-create')">
              <el-icon><Plus /></el-icon> 手动添加实体
            </el-button>
          </div>
          
          <el-table :data="paginatedEntities" style="width: 100%" v-loading="loading">
            <el-table-column prop="name" label="实体名称">
              <template #default="scope">
                <el-button type="text" @click="viewEntityDetails(scope.row)">{{ scope.row.name }}</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="200"></el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button size="mini" @click="viewEntityDetails(scope.row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 实体列表分页 -->
          <div class="pagination-container">
            <span class="pagination-total">共 {{ entityTotalPages }} 页</span>
            <el-pagination
              background
              layout="prev, pager, next"
              :current-page="currentEntityPage"
              :page-size="pageSize"
              :total="totalEntities"
              @current-change="handleEntityPageChange"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
    
    <!-- 实体类型详情对话框 -->
    <el-dialog title="模型详情" v-model="typeDetailVisible" width="600px">
      <el-form label-width="100px">
        <el-form-item label="模型名称">
          <el-input v-model="currentType.name" disabled></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="currentType.description" type="textarea" disabled></el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="typeDetailVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 实体详情对话框 -->
    <el-dialog title="实体详情" v-model="entityDetailVisible" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="实体名称">{{ currentEntity.name }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentEntity.createTime }}</el-descriptions-item>
        <template v-if="currentEntity.data">
            <el-descriptions-item 
              v-for="(value, key) in parseEntityData(currentEntity.data)" 
              :key="key" 
              :label="key"
            >
              {{ value }}
            </el-descriptions-item>
        </template>
      </el-descriptions>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="entityDetailVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Upload, Collection } from '@element-plus/icons-vue'
import { listModels, listInstances } from '@/api/modeling'

export default {
  name: 'ModelingMyModelsView',
  components: { Plus, Upload, Collection },
  setup() {
    const router = useRouter()
    const activeTab = ref('types')
    const loading = ref(false)
    const typeDetailVisible = ref(false)
    const entityDetailVisible = ref(false)
    
    // 分页相关变量
    const pageSize = ref(10)
    
    // 实体类型（模型）数据
    const entityTypes = ref([])
    const currentTypePage = ref(1)
    const totalTypes = ref(0)
    const paginatedEntityTypes = computed(() => {
      // 如果后端没有分页，前端模拟分页
      const start = (currentTypePage.value - 1) * pageSize.value
      return entityTypes.value.slice(start, start + pageSize.value)
    })

    // 实体数据
    const entities = ref([])
    const currentEntityPage = ref(1)
    const totalEntities = ref(0)
    const paginatedEntities = computed(() => {
      const start = (currentEntityPage.value - 1) * pageSize.value
      return entities.value.slice(start, start + pageSize.value)
    })
    
    const currentType = ref({})
    const currentEntity = ref({})
    
    // 计算属性：总页数
    const typeTotalPages = computed(() => Math.ceil(totalTypes.value / pageSize.value))
    const entityTotalPages = computed(() => Math.ceil(totalEntities.value / pageSize.value))

    // 获取模型列表
    const fetchModels = async () => {
      loading.value = true
      try {
        const res = await listModels()
        entityTypes.value = res.data || []
        totalTypes.value = entityTypes.value.length
      } catch (err) {
        console.error(err)
        ElMessage.error('获取模型列表失败')
      } finally {
        loading.value = false
      }
    }

    // 获取实体列表
    const fetchEntities = async () => {
      loading.value = true
      try {
        const res = await listInstances()
        entities.value = res.data || []
        totalEntities.value = entities.value.length
      } catch (err) {
        console.error(err)
        ElMessage.error('获取实体列表失败')
      } finally {
        loading.value = false
      }
    }

    const handleTabChange = (tabName) => {
      if (tabName === 'types') {
        fetchModels()
      } else {
        fetchEntities()
      }
    }
    
    const handleTypePageChange = (page) => {
      currentTypePage.value = page
    }
    
    const handleEntityPageChange = (page) => {
      currentEntityPage.value = page
    }
    
    const viewTypeDetails = (type) => {
      currentType.value = type
      typeDetailVisible.value = true
    }
    
    const viewEntityDetails = (entity) => {
      currentEntity.value = entity
      entityDetailVisible.value = true
    }
    
    const parseEntityData = (jsonStr) => {
      try {
        return typeof jsonStr === 'string' ? JSON.parse(jsonStr) : jsonStr
      } catch (e) {
        return {}
      }
    }

    const deleteType = (type) => {
      ElMessageBox.confirm(
        `确定要删除模型 "${type.name}" 吗？删除后该模型下的所有数据也将被删除。`,
        '确认删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        // TODO: 调用删除API
        ElMessage.success('删除成功 (功能待实现)')
      }).catch(() => {})
    }
    
    const deleteEntity = (entity) => {
      ElMessageBox.confirm(
        `确定要删除实体 "${entity.name}" 吗？`,
        '确认删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        // TODO: 调用删除API
        ElMessage.success('删除成功 (功能待实现)')
      }).catch(() => {})
    }
    
    onMounted(() => {
      fetchModels()
    })
    
    return {
      activeTab,
      loading,
      typeDetailVisible,
      entityDetailVisible,
      currentTypePage,
      totalTypes,
      currentEntityPage,
      totalEntities,
      pageSize,
      paginatedEntityTypes,
      paginatedEntities,
      typeTotalPages,
      entityTotalPages,
      currentType,
      currentEntity,
      viewTypeDetails,
      viewEntityDetails,
      deleteType,
      deleteEntity,
      handleTypePageChange,
      handleEntityPageChange,
      handleTabChange,
      parseEntityData
    }
  }
}
</script>

<style scoped>
.modeling-my-models-view {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 18px;
  color: #303133;
}

.card-header i {
  margin-right: 8px;
}

.toolbar {
  margin-bottom: 20px;
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

.image-preview {
  text-align: center;
}

.no-image {
  text-align: center;
  color: #909399;
  padding: 20px 0;
}
</style>