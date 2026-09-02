<template>
  <div class="artifact-detail-view">
    <el-card class="artifact-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-button 
            type="default" 
            size="small" 
            @click="goBack"
            style="margin-right: 10px;"
          >
            <i class="el-icon-arrow-left"></i> 返回
          </el-button>
          <h2 v-if="!isEditing"><i class="el-icon-tickets"></i> {{ artifact.name }}</h2>
          <h2 v-else><i class="el-icon-edit"></i> 编辑文物信息</h2>
          <div class="actions">
            <div v-if="!isEditing">
              <!-- 修改和删除功能已隐藏 -->
            </div>
            <div v-else>
              <!-- 编辑功能已隐藏 -->
            </div>
          </div>
        </div>
      </template>
      
      <div v-if="!isEditing">
        <el-row :gutter="20">
          <!-- 左侧：文物图片 -->
          <el-col :span="8">
            <el-card class="image-card" shadow="never">
              <div slot="header">
                <h3>文物图片</h3>
              </div>
              <div class="image-container">
                <el-image
                  v-if="artifact.images && artifact.images.length > 0"
                  :src="artifact.images[0]"
                  class="artifact-image"
                  fit="contain"
                  :preview-src-list="artifact.images"
                ></el-image>
                <div v-else class="no-image">
                  <i class="el-icon-picture-outline"></i>
                  <p>此物品暂无图片</p>
                </div>
                
                <div v-if="artifact.images && artifact.images.length > 1" class="image-thumbnails">
                  <el-image
                    v-for="(image, index) in artifact.images.slice(1)"
                    :key="index"
                    :src="image"
                    class="thumbnail"
                    fit="cover"
                    :preview-src-list="artifact.images"
                    :initial-index="index + 1"
                  ></el-image>
                </div>
              </div>
            </el-card>
          </el-col>
          
          <!-- 右侧：文物信息和知识图谱 -->
          <el-col :span="16">
            <!-- 文物基本信息 -->
            <el-card class="info-card" shadow="never" style="margin-bottom: 20px;">
              <div slot="header">
                <h3>文物简介</h3>
              </div>
              <el-descriptions :column="1" border>
                <el-descriptions-item label="名称">{{ artifact.name }}</el-descriptions-item>
                <el-descriptions-item label="年代">{{ artifact.era }}</el-descriptions-item>
                <el-descriptions-item label="材质">{{ artifact.material }}</el-descriptions-item>
                <el-descriptions-item label="类型">{{ artifact.type }}</el-descriptions-item>
                <el-descriptions-item label="详细描述">
                  {{ artifact.description }}
                </el-descriptions-item>
                <el-descriptions-item
                  v-for="(value, key) in artifact.customFields || {}"
                  :key="key"
                  :label="key"
                >
                  {{ value }}
                </el-descriptions-item>
              </el-descriptions>
            </el-card>
            
            <!-- 知识图谱 -->
            <el-card class="kg-card" shadow="never">
              <div slot="header">
                <h3>知识图谱</h3>
              </div>
              <div class="kg-container">
                <KnowledgeGraph :artifact-data="artifact" />
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <div v-else>
        <el-row :gutter="20">
          <!-- 左侧：文物图片编辑 -->
          <el-col :span="8">
            <el-card class="image-card" shadow="never">
              <div slot="header">
                <h3>文物图片</h3>
              </div>
              <div class="image-upload-container">
                <el-upload
                  class="image-uploader"
                  action=""
                  :auto-upload="false"
                  list-type="picture-card"
                  :file-list="editImages"
                  :on-change="handleImageUpload"
                  :on-remove="handleImageRemove"
                  multiple
                >
                  <i class="el-icon-plus"></i>
                </el-upload>
                <div class="el-upload__tip">可上传多张图片，支持 JPG/PNG 格式，每张大小不超过 2MB</div>
              </div>
            </el-card>
          </el-col>
          
          <!-- 右侧：文物信息和知识图谱编辑 -->
          <el-col :span="16">
            <!-- 文物基本信息编辑 -->
            <el-card class="info-card" shadow="never" style="margin-bottom: 20px;">
              <div slot="header">
                <h3>文物简介</h3>
              </div>
              <el-form :model="editForm" label-width="100px">
                <el-form-item label="名称">
                  <el-input v-model="editForm.name"></el-input>
                </el-form-item>
                <el-form-item label="年代">
                  <el-input v-model="editForm.era"></el-input>
                </el-form-item>
                <el-form-item label="材质">
                  <el-input v-model="editForm.material"></el-input>
                </el-form-item>
                <el-form-item label="类型">
                  <el-input v-model="editForm.type"></el-input>
                </el-form-item>
                <el-form-item label="详细描述">
                  <el-input v-model="editForm.description" type="textarea" :rows="4"></el-input>
                </el-form-item>
              </el-form>
            </el-card>
            
            <!-- 知识图谱编辑 -->
            <el-card class="kg-card" shadow="never">
              <div slot="header">
                <h3>知识图谱</h3>
              </div>
              <div class="kg-edit-container">
                <el-tabs v-model="kgActiveTab">
                  <el-tab-pane label="关联文物" name="relatedArtifacts">
                    <el-table :data="editForm.relatedArtifacts" style="width: 100%">
                      <el-table-column prop="name" label="文物名称">
                        <template #default="scope">
                          <el-input v-model="scope.row.name" placeholder="文物名称"></el-input>
                        </template>
                      </el-table-column>
                      <el-table-column prop="relation" label="关联类型">
                        <template #default="scope">
                          <el-input v-model="scope.row.relation" placeholder="关联类型"></el-input>
                        </template>
                      </el-table-column>
                      <el-table-column label="操作" width="80">
                        <template #default="scope">
                          <el-button
                            type="danger"
                            size="mini"
                            icon="el-icon-delete"
                            circle
                            @click="removeRelatedArtifact(scope.$index)"
                          ></el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                    <div style="margin-top: 10px;">
                      <el-button type="primary" @click="addRelatedArtifact">添加关联文物</el-button>
                    </div>
                  </el-tab-pane>
                  
                  <el-tab-pane label="相关事件" name="events">
                    <el-table :data="editForm.events" style="width: 100%">
                      <el-table-column prop="name" label="事件名称">
                        <template #default="scope">
                          <el-input v-model="scope.row.name" placeholder="事件名称"></el-input>
                        </template>
                      </el-table-column>
                      <el-table-column prop="time" label="时间">
                        <template #default="scope">
                          <el-input v-model="scope.row.time" placeholder="时间"></el-input>
                        </template>
                      </el-table-column>
                      <el-table-column label="操作" width="80">
                        <template #default="scope">
                          <el-button
                            type="danger"
                            size="mini"
                            icon="el-icon-delete"
                            circle
                            @click="removeEvent(scope.$index)"
                          ></el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                    <div style="margin-top: 10px;">
                      <el-button type="primary" @click="addEvent">添加相关事件</el-button>
                    </div>
                  </el-tab-pane>
                </el-tabs>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted, watch, computed } from 'vue'
import { useStore } from 'vuex'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import KnowledgeGraph from '@/components/KnowledgeGraph.vue'
import { getArtifactDetail } from '@/api/artifact'

export default {
  name: 'ArtifactDetailView',
  components: {
    KnowledgeGraph
  },
  setup() {
    const store = useStore()
    const route = useRoute()
    const router = useRouter()
    
    const isEditing = ref(false)
    const deleting = ref(false)
    const saving = ref(false)
    const kgActiveTab = ref('relatedArtifacts')
    
    const artifact = ref({})
    const editForm = ref({
      name: '',
      era: '',
      material: '',
      type: '',
      description: '',
      images: [],
      relatedArtifacts: [],
      events: []
    })
    
    const editImages = ref([])

    // 获取用户登录状态和角色
    const isLoggedIn = computed(() => store.state.user.isLoggedIn)
    const userRole = computed(() => store.state.user.role)
    
    // 是否为管理员或高级用户
    const isAdminOrAdvanced = computed(() => 
      userRole.value === 'admin' || userRole.value === 'advanced'
    )

    const loadArtifactDetail = async (id) => {
      if (!id) {
        router.push('/artifacts')
        return
      }
      
      try {
        const res = await getArtifactDetail(id)
        const data = res.data
        
        if (!data) {
          ElMessage.error('未找到文物详情')
          router.push('/artifacts')
          return
        }
        
        const relations = data.relations || []
        const relatedArtifacts = relations.map(relation => ({
          id: relation.relationId,
          name: relation.targetString,
          type: relation.relationName,
          relation: relation.relationDescription || relation.relationName
        }))
        
        artifact.value = {
          id: data.relicsId,
          name: data.relicsName,
          era: data.era,
          material: data.material,
          type: data.typeName,
          description: data.description,
          images: data.images || [],
          relatedArtifacts,
          events: [],
          customFields: data.customFields || {}
        }
      } catch (err) {
        ElMessage.error('加载文物详情失败')
        router.push('/artifacts')
      }
    }
    
    // 开始编辑
    const startEdit = () => {
      // 初始化编辑表单
      editForm.value = JSON.parse(JSON.stringify(artifact.value))
      
      // 初始化图片列表
      editImages.value = []
      if (editForm.value.images && editForm.value.images.length > 0) {
        editImages.value = editForm.value.images.map((url, index) => ({
          uid: index,
          name: `image_${index}.jpg`,
          url: url
        }))
      }
      
      isEditing.value = true
    }
    
    // 取消编辑
    const cancelEdit = () => {
      isEditing.value = false
    }
    
    // 处理图片上传
    const handleImageUpload = (file, fileList) => {
      const isJPG = file.raw.type === 'image/jpeg' || file.raw.type === 'image/png'
      const isLt2M = file.raw.size / 1024 / 1024 < 2
      
      if (!isJPG) {
        ElMessage.error('上传图片只能是 JPG/PNG 格式!')
        // 从文件列表中移除无效文件
        const index = fileList.findIndex(item => item.uid === file.uid)
        if (index > -1) {
          fileList.splice(index, 1)
        }
        return
      }
      if (!isLt2M) {
        ElMessage.error('上传图片大小不能超过 2MB!')
        // 从文件列表中移除无效文件
        const index = fileList.findIndex(item => item.uid === file.uid)
        if (index > -1) {
          fileList.splice(index, 1)
        }
        return
      }
      
      // 更新editForm中的图片列表
      editForm.value.images = fileList.map(item => {
        if (item.raw) {
          // 新上传的文件
          return URL.createObjectURL(item.raw)
        } else {
          // 已存在的文件
          return item.url
        }
      })
    }
    
    // 处理图片移除
    const handleImageRemove = (file, fileList) => {
      editForm.value.images = fileList.map(item => {
        if (item.raw) {
          // 新上传的文件
          return URL.createObjectURL(item.raw)
        } else {
          // 已存在的文件
          return item.url
        }
      })
    }
    
    // 添加关联文物
    const addRelatedArtifact = () => {
      editForm.value.relatedArtifacts.push({
        id: Date.now(), // 临时ID
        name: '',
        type: '',
        relation: ''
      })
    }
    
    // 移除关联文物
    const removeRelatedArtifact = (index) => {
      editForm.value.relatedArtifacts.splice(index, 1)
    }
    
    // 添加相关事件
    const addEvent = () => {
      editForm.value.events.push({
        id: Date.now(), // 临时ID
        name: '',
        time: ''
      })
    }
    
    // 移除相关事件
    const removeEvent = (index) => {
      editForm.value.events.splice(index, 1)
    }
    
    // 提交修改审核
    const submitEdit = async () => {
      saving.value = true
      
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        if (isAdminOrAdvanced.value) {
          // 管理员或高级用户直接修改
          artifact.value = JSON.parse(JSON.stringify(editForm.value))
          isEditing.value = false
          ElMessage.success('文物信息修改成功')
        } else {
          // 普通用户提交修改申请，需要审核
          isEditing.value = false
          ElMessage.success('修改申请已提交，等待管理员审核')
          
          // 这里应该将修改请求发送到后端，保存到审核队列中
          // 示例代码：
          /*
          await api.submitArtifactModification({
            artifactId: artifact.value.id,
            originalData: artifact.value,
            modifiedData: editForm.value,
            creator: store.state.user.username
          })
          */
        }
      } catch (err) {
        ElMessage.error('提交失败：' + (err.response?.data?.message || '未知错误'))
      } finally {
        saving.value = false
      }
    }
    
    // 请求删除文物
    const requestDelete = () => {
      ElMessageBox.confirm(
        isAdminOrAdvanced.value 
          ? '确定要删除该文物吗？此操作不可恢复！' 
          : '确定要申请删除该文物吗？删除申请需要管理员审核通过后才会生效。',
        isAdminOrAdvanced.value ? '确认删除' : '申请删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: isAdminOrAdvanced.value ? 'error' : 'warning'
        }
      ).then(async () => {
        deleting.value = true
        
        try {
          // 模拟API调用
          await new Promise(resolve => setTimeout(resolve, 1000))
          
          if (isAdminOrAdvanced.value) {
            // 管理员或高级用户直接删除
            ElMessage.success('文物删除成功')
            router.push('/artifacts')
          } else {
            // 普通用户提交删除申请，需要审核
            ElMessage.success('删除申请已提交，等待管理员审核')
            
            // 这里应该将删除请求发送到后端，保存到审核队列中
            // 示例代码：
            /*
            await api.submitArtifactDeletion({
              artifactId: artifact.value.id,
              artifactData: artifact.value,
              creator: store.state.user.username
            })
            */
          }
        } catch (err) {
          ElMessage.error('操作失败：' + (err.response?.data?.message || '未知错误'))
        } finally {
          deleting.value = false
        }
      }).catch(() => {
        // 用户取消操作
      })
    }
    
    // 返回上一页
    const goBack = () => {
      const fromRoute = store.state.navigation.fromRoute
      
      if (fromRoute === '/search') {
        router.push('/search')
      } else {
        router.push('/artifacts')
      }
    }
    
    // 监听路由变化
    watch(() => route.params.id, (newId) => {
      loadArtifactDetail(newId)
      isEditing.value = false // 切换文物时退出编辑模式
    })
    
    // 模拟从路由参数获取文物ID并加载数据
    onMounted(() => {
      const artifactId = route.params.id
      if (artifactId) {
        loadArtifactDetail(artifactId)
      } else {
        // 如果没有ID参数，跳转到文物浏览页
        router.push('/artifacts')
      }
    })
    
    return {
      artifact,
      editForm,
      editImages,
      isEditing,
      deleting,
      saving,
      kgActiveTab,
      isLoggedIn,
      userRole,
      isAdminOrAdvanced,
      startEdit,
      cancelEdit,
      handleImageUpload,
      handleImageRemove,
      addRelatedArtifact,
      removeRelatedArtifact,
      addEvent,
      removeEvent,
      submitEdit,
      requestDelete,
      goBack
    }
  }
}
</script>

<style scoped>
.artifact-detail-view {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  color: #303133;
}

.card-header h2 {
  margin: 0;
}

.card-header i {
  margin-right: 8px;
}

.actions {
  display: flex;
  gap: 10px;
}

.image-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.artifact-image {
  max-width: 100%;
  max-height: 300px;
}

.no-image {
  text-align: center;
  color: #909399;
  padding: 20px;
}

.no-image i {
  font-size: 48px;
  margin-bottom: 10px;
}

.image-thumbnails {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.thumbnail {
  width: 80px;
  height: 80px;
  cursor: pointer;
}

.image-upload-container {
  padding: 10px;
}

:deep(.image-uploader .el-upload--picture-card) {
  width: 100px;
  height: 100px;
  line-height: 100px;
}

:deep(.image-uploader .el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}

.kg-container {
  height: 400px;
}

.kg-edit-container {
  min-height: 400px;
}
</style>
