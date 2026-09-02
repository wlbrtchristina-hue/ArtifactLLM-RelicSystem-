<template>
  <div class="artifacts-view">
    <el-card class="filter-card" shadow="hover">
      <div slot="header">
        <h2><i class="el-icon-search"></i> 文物筛选</h2>
      </div>
      
      <el-form :inline="true" :model="filterForm" @submit.prevent>
        <el-form-item label="文物名称">
          <el-input v-model="filterForm.name" placeholder="请输入文物名称"></el-input>
        </el-form-item>
        <el-form-item label="文物类型">
          <el-select v-model="filterForm.type" placeholder="请选择文物类型" filterable clearable>
            <el-option label="全部" value=""></el-option>
            <el-option label="青铜器" value="bronze"></el-option>
            <el-option label="陶瓷" value="ceramic"></el-option>
            <el-option label="书画" value="painting"></el-option>
            <el-option label="玉器" value="jade"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="年代">
          <el-select v-model="filterForm.era" placeholder="请选择年代" filterable clearable>
            <el-option label="全部" value=""></el-option>
            <el-option label="商周" value="shang-zhou"></el-option>
            <el-option label="秦汉" value="qin-han"></el-option>
            <el-option label="隋唐" value="sui-tang"></el-option>
            <el-option label="宋元" value="song-yuan"></el-option>
            <el-option label="明清" value="ming-qing"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchArtifacts">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="results-card" shadow="hover">
      <div slot="header">
        <h2><i class="el-icon-collection"></i> 文物列表</h2>
      </div>
      
      <el-table :data="artifacts" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="文物名称">
          <template #default="scope">
            <el-button type="text" @click="viewArtifactDetail(scope.row)">{{ scope.row.name }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="120"></el-table-column>
        <el-table-column prop="era" label="年代" width="120"></el-table-column>
        <el-table-column prop="material" label="材质" width="120"></el-table-column>
        <el-table-column prop="location" label="出土地点" width="150"></el-table-column>
        <el-table-column prop="storageLocation" label="馆藏位置" width="150"></el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button size="mini" @click="viewArtifactDetail(scope.row)">查看详情</el-button>
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
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { getArtifacts } from '@/api/artifact'

export default {
  name: 'ArtifactsView',
  setup() {
    const router = useRouter()
    const store = useStore()
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    
    const filterForm = reactive({
      name: '',
      type: '',
      era: ''
    })
    
    const artifacts = ref([])
    
    const totalPages = computed(() => Math.ceil(total.value / pageSize.value))
    
    const loadArtifacts = async () => {
      loading.value = true
      try {
        const res = await getArtifacts({
          pageNum: currentPage.value,
          pageSize: pageSize.value,
          era: filterForm.era,
          type: filterForm.type
        })
        
        const pageData = res.data || {}
        const records = pageData.records || []
        
        const mappedRecords = records.map(item => ({
          id: item.relicsId,
          name: item.relicsName,
          type: item.typeName,
          era: item.era,
          material: item.material,
          location: item.discoverySite,
          storageLocation: item.storageLocation || '未知'
        }))
        
        const name = filterForm.name && filterForm.name.trim()
        const filteredRecords = name
          ? mappedRecords.filter(artifact => artifact.name && artifact.name.includes(name))
          : mappedRecords
        
        total.value = filteredRecords.length
        
        // 分页处理
        const start = (currentPage.value - 1) * pageSize.value
        artifacts.value = filteredRecords.slice(start, start + pageSize.value)
      } catch (err) {
        ElMessage.error('获取文物列表失败')
      } finally {
        loading.value = false
      }
    }
    
    const searchArtifacts = () => {
      currentPage.value = 1
      loadArtifacts()
    }
    
    const resetFilter = () => {
      filterForm.name = ''
      filterForm.type = ''
      filterForm.era = ''
      searchArtifacts()
    }
    
    const handlePageChange = (page) => {
      currentPage.value = page
      loadArtifacts()
    }
    
    const viewArtifactDetail = (artifact) => {
      store.commit('navigation/setFromRoute', '/artifacts')
      router.push(`/artifact/${artifact.id}`)
    }
    
    onMounted(() => {
      loadArtifacts()
    })
    
    return {
      filterForm,
      loading,
      currentPage,
      pageSize,
      total,
      totalPages,
      artifacts,
      searchArtifacts,
      resetFilter,
      handlePageChange,
      viewArtifactDetail
    }
  }
}
</script>

<style scoped>
.artifacts-view {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
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
