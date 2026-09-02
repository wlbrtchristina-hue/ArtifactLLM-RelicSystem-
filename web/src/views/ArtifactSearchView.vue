<template>
  <div class="artifact-browse-view">
    <!-- 顶部固定筛选卡片 -->
    <div class="filter-sticky">
      <el-card class="filter-card" shadow="hover">
        <template #header>
          <h2><i class="el-icon-view"></i> 文物浏览</h2>
        </template>

        <el-form :inline="true" :model="filters" class="filter-form">
          <el-form-item label="年代">
            <el-select v-model="filters.era" placeholder="全部年代" clearable style="width: 180px;">
              <el-option v-for="era in eraOptions" :key="era" :label="era" :value="era" />
            </el-select>
          </el-form-item>

          <el-form-item label="分类">
            <el-select v-model="filters.category" placeholder="全部分类" clearable style="width: 200px;">
              <el-option v-for="category in allCategories" :key="category" :label="category" :value="category"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="doFilter" size="default">筛选</el-button>
            <el-button @click="resetFilter" size="default">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 瀑布流卡片区 -->
    <div class="waterfall-container">
      <div class="waterfall-grid">
        <div v-for="item in displayArtifacts" :key="item.id" class="artifact-card">
          <div class="image-wrapper">
            <img v-if="item.image" :src="item.image" :alt="item.name" class="artifact-img" />
            <div v-else class="no-image-placeholder">此物品暂无图片</div>
          </div>
          <div class="card-info">
              <h3 class="artifact-title">{{ item.name }}</h3>
              <div class="tags">
                <el-tag size="small" type="success">{{ item.era }}</el-tag>
                <el-tag size="small" type="danger">{{ item.type }}</el-tag>
              </div>
              <div class="card-actions">
                <el-button type="primary" size="small" @click="goDetail(item.id)">
                  查看详情
                </el-button>
              </div>
            </div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading">加载中...</div>
      <div v-if="!loading && displayArtifacts.length === 0" class="empty">
        <el-empty description="暂无符合条件的文物" />
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="!loading && displayArtifacts.length > 0" class="pagination-container">
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getArtifacts, getRelicMaterials, getRelicTypes, getRelicEras } from '@/api/artifact'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

const router = useRouter()
const store = useStore()

// 不再需要locationOptions，已移除地址筛选功能

// TODO: 数据库接口 - 未来将使用真实数据替换假数据
// 以下是数据库连接接口示例（暂未启用）：
/*
import { getArtifactsFromDatabase } from '@/api/artifactDatabase'

// 数据库连接配置
const dbConfig = {
  host: process.env.VUE_APP_DB_HOST || 'localhost',
  port: process.env.VUE_APP_DB_PORT || 3306,
  username: process.env.VUE_APP_DB_USER || 'root',
  password: process.env.VUE_APP_DB_PASS || '',
  database: process.env.VUE_APP_DB_NAME || 'artifact_db'
}

// 获取文物数据的数据库接口
const fetchArtifactsFromDB = async (params) => {
  try {
    // 参数处理
    const queryParams = {
      pageNum: params.pageNum || 1,
      pageSize: params.pageSize || 20,
      era: params.era || null,
      material: params.material || null,
      type: params.type || null,
      discoverySite: params.discoverySite || null
    }
    
    // 调用后端API获取数据库数据
    const response = await getArtifactsFromDatabase(queryParams)
    return response
  } catch (error) {
    console.error('数据库查询错误:', error)
    throw error
  }
}
*/

// 筛选条件
const filters = ref({
  era: '',
  category: ''
})

// 当前展示的数据
const displayArtifacts = ref([])
const loading = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = 20
const eraOptions = ref([])
const materialsOptions = ref([])
const typeOptions = ref([])

// 合并材质和类型为分类
const allCategories = computed(() => {
  return [...new Set([...materialsOptions.value, ...typeOptions.value])]
})

// 分页相关计算属性
const total = ref(0)
const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize)
})

// 分页变化处理
const handlePageChange = (page) => {
  currentPage.value = page
  loadData()
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 构造筛选参数
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize
    }
    
    // 添加筛选条件
    if (filters.value.era) params.era = filters.value.era
    if (filters.value.category) {
      // 将分类同时作为材质和类型的筛选条件
      params.material = filters.value.category
      params.type = filters.value.category
    }

    const res = await getArtifacts(params)
    
    const records = res.data.records || []
    const mappedRecords = records.map(item => ({
      id: item.relicsId,
      name: item.relicsName,
      era: item.era,
      material: item.material,
      type: item.typeName,
      location: item.discoverySite,
      image: item.images && item.images.length > 0 ? item.images[0] : ''
    }))

    displayArtifacts.value = mappedRecords
    
    // 更新总记录数
    total.value = res.data.total || 0

  } catch (error) {
    console.error('加载文物列表失败', error)
    // ElMessage.error('加载文物列表失败')
  } finally {
    loading.value = false
  }
}

// 执行筛选
const doFilter = () => {
  currentPage.value = 1
  loadData()
}

// 重置
const resetFilter = () => {
  filters.value = { era: '', category: '' }
  doFilter()
}

// 跳转详情
const goDetail = (id) => {
  store.commit('navigation/setFromRoute', '/artifacts')
  router.push(`/artifact/${id}`)
}

//预定义的静态数据
//const STATIC_MATERIALS = ['青铜', '玉石', '陶瓷', '金银', '书画', '竹木牙角']
//const STATIC_TYPES = ['礼器', '乐器', '兵器', '车马器', '生活用具', '装饰品']

// 初始化
onMounted(() => {
  Promise.all([
    getRelicEras().then(res => {
      const arr = Array.isArray(res.data) ? res.data : []
      const clean = Array.from(new Set(arr.filter(Boolean).map(s => (typeof s === 'string' ? s.trim() : '')))).filter(Boolean)
      eraOptions.value = clean
    }).catch(() => {
      eraOptions.value = []
    }),
    getRelicMaterials().then(res => {
      const arr = Array.isArray(res.data) ? res.data : []
      const clean = Array.from(new Set(arr.filter(Boolean).map(s => (typeof s === 'string' ? s.trim() : '')))).filter(Boolean)
      materialsOptions.value = clean.length > 0 ? clean : STATIC_MATERIALS
    }).catch(() => {
      materialsOptions.value = STATIC_MATERIALS
    }),
    getRelicTypes().then(res => {
      const arr = Array.isArray(res.data) ? res.data : []
      const clean = Array.from(new Set(arr.filter(Boolean).map(s => (typeof s === 'string' ? s.trim() : '')))).filter(Boolean)
      typeOptions.value = clean.length > 0 ? clean : STATIC_TYPES
    }).catch(() => {
      typeOptions.value = STATIC_TYPES
    })
  ]).finally(() => {
    doFilter()
  })
})
</script>

<style scoped>
.artifact-browse-view { padding: 20px 40px; background: #f5f7fa; min-height: 100vh; }
.filter-sticky { position: sticky; top: 0; z-index: 100; background: #f5f7fa; padding: 10px 0; }
.filter-card h2 { margin: 0; font-size: 24px; color: #303133; }
.filter-form { margin-top: 10px; justify-content: center; }
.filter-form .el-form-item { margin-right: 20px; margin-bottom: 0; }

.waterfall-container { margin-top: 30px; }
.waterfall-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* 固定4列，大屏专用 */
  gap: 24px;
}
.artifact-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transition: all 0.3s;
}
.artifact-card:hover { transform: translateY(-8px); box-shadow: 0 12px 30px rgba(0,0,0,0.15); }

.image-wrapper { position: relative; height: 320px; overflow: hidden; display: flex; align-items: center; justify-content: center; background-color: #f8f9fa; }
.artifact-img { width: 100%; height: 100%; object-fit: cover; }
.no-image-placeholder { color: #909399; font-size: 16px; }

.card-info { padding: 16px; }
.artifact-title { margin: 0 0 12px 0; font-size: 18px; color: #303133; }
.tags { margin-bottom: 8px; display: flex; gap: 8px; flex-wrap: wrap; }
.location { margin: 8px 0 0 0; color: #909399; font-size: 14px; }

.card-actions { 
  margin-top: 16px; 
  text-align: right;
}

.loading, .no-more, .empty { text-align: center; padding: 40px; color: #909399; font-size: 16px; }

/* 分页样式 */
.pagination-container {
  margin-top: 30px;
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
