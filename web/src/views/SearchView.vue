<template>
  <div class="search-view">
    <!-- 搜索框卡片 -->
    <el-card class="search-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2><i class="el-icon-search"></i> 智能检索</h2>
          <p style="margin: 5px 0 0 0; color: #606266; font-size: 14px;">基于关键词的文物关联检索系统</p>
        </div>
      </template>
      
      <div class="search-container">
        <el-form @submit.native.prevent>
          <el-row :gutter="20">
            <el-col :span="20">
              <el-form-item>
                <el-autocomplete
                  v-model="query"
                  :fetch-suggestions="querySearch"
                  :placeholder="inputPlaceholder"
                  size="large"
                  clearable
                  style="width: 100%;"
                  @select="handleSelect"
                  @keyup.enter="performSearch"
                  :trigger-on-focus="true"
                >
                  <template #prepend>
                    <i class="el-icon-search"></i>
                  </template>
                  <template #default="{ item }">
                    <div class="autocomplete-item">
                      <i class="el-icon-search" style="margin-right: 8px; color: #909399;"></i>
                      <span>{{ item.value }}</span>
                      <small style="margin-left: auto; color: #c0c4cc;" v-if="item.isHistory">历史搜索</small>
                    </div>
                  </template>
                </el-autocomplete>
              </el-form-item>
              
              <div style="margin-top: 10px; text-align: right;">
                <el-link type="danger" :underline="false" @click="clearSearchHistory" v-if="searchHistory.length > 0">
                  清空搜索历史
                </el-link>
              </div>
            </el-col>
            <el-col :span="4">
              <el-form-item>
                <el-button 
                  type="primary" 
                  size="large" 
                  @click="performSearch" 
                  :loading="searchLoading"
                  style="width: 100%;"
                >
                  <i class="el-icon-search"></i> 搜索
                </el-button>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="24">
              <div class="search-options">
                <el-checkbox v-model="relatedSearch" label="查找相关文物" @change="handleRelatedSearchChange" />
              </div>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </el-card>
    
    <!-- 结果显示 -->
    <div v-if="searchResults.length > 0" class="results-header">
      <h4>搜索结果</h4>
      <p>找到 {{ searchResults.length }} 个匹配的文物</p>
    </div>
    
    <el-table v-if="searchResults.length > 0" :data="paginatedResults" class="result-list" style="width: 100%;">
      <el-table-column prop="name" label="文物名称" min-width="200">
        <template #default="scope">
          <el-button type="text" @click="viewDetail(scope.row)">{{ scope.row.name }}</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="type" label="类型" min-width="120"></el-table-column>
      <el-table-column prop="era" label="年代" min-width="120"></el-table-column>
      <el-table-column label="操作" min-width="220" align="center">
        <template #default="scope">
          <div class="action-buttons">
            <el-button size="mini" @click="viewDetail(scope.row)">查看详情</el-button>
            <el-button size="mini" type="primary" @click="viewKG(scope.row.id)">知识图谱</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="relatedSearch && searchResults.length > 0" class="pagination-container">
      <span class="pagination-total">共 {{ totalPages }} 页</span>
      <el-pagination
        background
        layout="prev, pager, next"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="searchResults.length"
        @current-change="handlePageChange"
      />
    </div>
    
    <!-- 相关文物结果 -->
    <div v-if="relatedSearch && relatedResults.length > 0" class="results-header">
      <h4>相关文物</h4>
      <p>为 "{{ query }}" 找到 {{ relatedResults.length }} 条相关文物</p>
    </div>

    <el-table 
      v-if="relatedSearch && relatedResults.length > 0" 
      :data="paginatedRelatedResults" 
      class="result-list related-list"
      style="width: 100%;">
      <el-table-column prop="name" label="文物名称" min-width="200">
        <template #default="scope">
          <el-button type="text" @click="viewDetail(scope.row)">{{ scope.row.name }}</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="type" label="类型" min-width="120"></el-table-column>
      <el-table-column prop="era" label="年代" min-width="120"></el-table-column>
      <el-table-column prop="relevance" label="相关性" min-width="120"></el-table-column>
      <el-table-column label="操作" min-width="220" align="center">
        <template #default="scope">
          <div class="action-buttons">
            <el-button size="mini" @click="viewDetail(scope.row)">查看详情</el-button>
            <el-button size="mini" type="primary" @click="viewKG(scope.row.id)">知识图谱</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 相关文物分页 -->
    <div v-if="relatedSearch && relatedResults.length > 0" class="pagination-container">
      <span class="pagination-total">共 {{ relatedTotalPages }} 页</span>
      <el-pagination
        background
        layout="prev, pager, next"
        :current-page="relatedPage"
        :page-size="relatedPageSize"
        :total="relatedResults.length"
        @current-change="handleRelatedPageChange"
      />
    </div>
    
    <!-- 分页 -->
    <div v-if="searchResults.length > 0 && !relatedSearch" class="pagination-container">
      <span class="pagination-total">共 {{ totalPages }} 页</span>
      <el-pagination
        background
        layout="prev, pager, next"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="searchResults.length"
        @current-change="handlePageChange"
      />
    </div>
    
    <!-- 无结果提示 -->
    <div v-if="!searchLoading && searchResults.length === 0 && query" class="no-results">
      <i class="el-icon-search" style="font-size: 48px; margin-bottom: 10px;"></i>
      <p>未找到与 "{{ query }}" 相关的文物</p>
      <p style="font-size: 14px; color: #909399;">请尝试更换关键词或减少筛选条件</p>
    </div>
    
    <!-- 知识图谱弹窗 -->
    <el-dialog 
      title="文物知识图谱" 
      v-model="kgVisible" 
      width="50%"
      align-center
      class="kg-dialog"
      :append-to-body="true"
      destroy-on-close>
      <KnowledgeGraph :artifact-data="selectedArtifactData" />
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { searchRelics } from '@/api/artifact'
import KnowledgeGraph from '@/components/KnowledgeGraph.vue'

export default {
  name: 'SearchView',
  components: {
    KnowledgeGraph
  },
  setup() {
    const router = useRouter()
    const store = useStore()
    const query = ref('')
    const semanticSearch = ref(false)
    const relatedSearch = ref(false)
    const searchLoading = ref(false)
    const searchResults = ref([])
    const allResults = ref([])
    const relatedResults = ref([])
    const currentPage = ref(1)
    const pageSize = ref(10)
    const relatedPage = ref(1)
    const relatedPageSize = ref(10)
    const kgVisible = ref(false)
    const selectedArtifactData = ref(null)
    
    const inputPlaceholder = computed(() => {
      return relatedSearch.value ? '请输入文物名称' : '请输入文物名称、年代、类型等关键词'
    })
    
    const handleRelatedSearchChange = (value) => {
      // 默认不勾选相关文物；若用户勾选则进入相关文物模式
    }
    
    // 搜索历史（从 localStorage 加载）
    const searchHistory = ref([])
    
    // 加载历史记录
    const loadHistory = () => {
      const history = localStorage.getItem('artifactSearchHistory')
      if (history) {
        searchHistory.value = JSON.parse(history).slice(0, 10) // 只保留最近10条
      }
    }
    
    // 保存历史记录
    const saveHistory = (keyword) => {
      if (!keyword.trim()) return
      // 去重 + 放到最前面
      const history = searchHistory.value.filter(item => item !== keyword)
      history.unshift(keyword)
      // 最多保留10条
      const newHistory = history.slice(0, 10)
      searchHistory.value = newHistory
      localStorage.setItem('artifactSearchHistory', JSON.stringify(newHistory))
    }
    
    // 自动完成建议（返回历史记录）
    const querySearch = (queryString, cb) => {
      const results = queryString
        ? searchHistory.value.filter(item => item.toLowerCase().includes(queryString.toLowerCase()))
        : searchHistory.value // 输入为空时显示全部历史
      
      const formattedResults = results.map(item => ({ 
        value: item,
        isHistory: true
      }))
      
      cb(formattedResults)
    }
    
    // 选择历史记录时
    const handleSelect = (item) => {
      query.value = item.value
      performSearch()
    }
    
    // 清空搜索历史
    const clearSearchHistory = () => {
      searchHistory.value = []
      localStorage.removeItem('artifactSearchHistory')
    }
    
    // 计算属性：总页数
    const totalPages = computed(() => Math.ceil(searchResults.value.length / pageSize.value))
    const relatedTotalPages = computed(() => Math.ceil(relatedResults.value.length / relatedPageSize.value))
    
    // 计算属性：分页后的结果
    const paginatedResults = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      return searchResults.value.slice(start, end)
    })
    
    const paginatedRelatedResults = computed(() => {
      const start = (relatedPage.value - 1) * relatedPageSize.value
      const end = start + relatedPageSize.value
      return relatedResults.value.slice(start, end)
    })
    
    const mapArtifactRecord = (item) => ({
      id: item.relicsId,
      name: item.relicsName,
      type: item.typeName,
      era: item.era,
      material: item.material,
      location: item.discoverySite,
      description: item.description || '',
      relevance: item.relevance || ''
    })
    
    // 执行搜索（使用后端数据）
    const performSearch = async () => {
      const keyword = query.value.trim()
      if (!keyword) {
        ElMessage.warning('请输入搜索关键词')
        return
      }
      
      // 保存搜索历史
      saveHistory(keyword)
      
      searchLoading.value = true
      
      try {
        const res = await searchRelics({
          q: keyword,
          semantic: false,
          related: false,
          pageNum: 1,
          pageSize: 200
        })
        
        const pageData = res.data || {}
        const records = pageData.records || []
        const mappedRecords = records.map(mapArtifactRecord)
        
        const lowerKeyword = keyword.toLowerCase()
        const filtered = mappedRecords.filter(item => {
          const candidates = [
            item.name,
            item.era,
            item.material,
            item.location,
            item.type
          ]
          return candidates.some(field => field && field.toLowerCase().includes(lowerKeyword))
        })
        
        allResults.value = mappedRecords
        searchResults.value = filtered
        currentPage.value = 1

        if (relatedSearch.value) {
          try {
            const relatedRes = await searchRelics({
              q: keyword,
              semantic: false,
              related: true,
              pageNum: 1,
              pageSize: 200
            })
            const relatedData = relatedRes.data || {}
            const primaryRecords = (relatedData.primaryResults || []).map(mapArtifactRecord)
            const relatedPage = relatedData.relatedResults || {}
            const relatedRecords = (relatedPage.records || []).map(mapArtifactRecord)
            
            searchResults.value = primaryRecords
            const sortedRelated = relatedRecords.sort((a, b) => {
              const rank = v => (v.relevance === '同一类型' ? 0 : v.relevance === '同一年代' ? 1 : 2)
              return rank(a) - rank(b)
            })
            relatedResults.value = sortedRelated
            
            relatedPage.value = 1
          } catch (err) {
            console.error('获取相关文物失败', err)
            relatedResults.value = []
          }
        } else {
          relatedResults.value = []
        }
        
        if (searchResults.value.length === 0) {
          ElMessage.info('没有找到匹配的文物')
        }
      } catch (error) {
        console.error('搜索文物失败', error)
        ElMessage.error('搜索失败，请稍后重试')
        searchResults.value = []
        allResults.value = []
      } finally {
        searchLoading.value = false
      }
    }
    
    // 查看详情
    const viewDetail = (row) => {
      if (!row || !row.id) {
        ElMessage.warning('未找到文物ID，无法跳转详情')
        return
      }
      
      store.commit('navigation/setFromRoute', '/search')
      store.commit('navigation/setSearchState', {
        query: query.value,
        relatedSearch: relatedSearch.value,
        searchResults: searchResults.value,
        relatedResults: relatedResults.value,
        currentPage: currentPage.value,
        relatedPage: relatedPage.value
      })
      
      router.push(`/artifact/${row.id}`)
    }
    
    // 查看知识图谱
    const viewKG = (id) => {
      const target = [...searchResults.value, ...relatedResults.value].find(item => item.id === id)
      selectedArtifactData.value = target || null
      kgVisible.value = true
    }
    
    // 处理分页变化
    const handlePageChange = (page) => {
      currentPage.value = page
    }
    
    const handleRelatedPageChange = (page) => {
      relatedPage.value = page
    }
    
    onMounted(() => {
      loadHistory()
      
      const savedSearchState = store.state.navigation.searchState
      if (savedSearchState && store.state.navigation.fromRoute === '/search') {
        query.value = savedSearchState.query
        relatedSearch.value = savedSearchState.relatedSearch
        searchResults.value = savedSearchState.searchResults
        relatedResults.value = savedSearchState.relatedResults
        currentPage.value = savedSearchState.currentPage
        relatedPage.value = savedSearchState.relatedPage
      }
    })
    
    return {
      query,
      semanticSearch,
      relatedSearch,
      searchLoading,
      searchResults,
      allResults,
      currentPage,
      pageSize,
      totalPages,
      paginatedResults,
      kgVisible,
      selectedArtifactData,
      performSearch,
      viewDetail,
      viewKG,
      handlePageChange,
      handleRelatedPageChange,
      searchHistory,
      querySearch,
      handleSelect,
      clearSearchHistory,
      relatedResults,
      paginatedRelatedResults,
      relatedTotalPages,
      relatedPage,
      relatedPageSize,
      inputPlaceholder,
      handleRelatedSearchChange
    }
  }
}
</script>

<style scoped>
.search-view {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 18px;
  color: #303133;
}

.card-header i {
  margin-right: 8px;
}

.search-options {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.results-header {
  margin-bottom: 20px;
}

.results-header h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.results-header p {
  margin: 0;
  color: #606266;
}

.result-list {
  margin-top: 20px;
}

.no-results {
  text-align: center;
  padding: 30px;
  color: #909399;
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

.autocomplete-item {
  display: flex;
  align-items: center;
  width: 100%;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: nowrap;
}

.kg-dialog :deep(.el-dialog) {
  /* 居中大框：目标高约 75vh，保持 4:3 比例，且宽度不超 90vw */
  width: 75vw;
  max-width: 1200px;
  aspect-ratio: 4 / 3;
}

.kg-dialog :deep(.el-dialog__body) {
  /* 预留标题/按钮区域后，内容区接近 75vh */
  height: calc(75vh - 80px);
  min-height: calc(75vh - 80px);
  padding: 0 20px 20px 20px;
  box-sizing: border-box;
}
</style>
