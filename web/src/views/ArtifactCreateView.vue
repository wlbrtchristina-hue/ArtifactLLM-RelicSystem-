<template>
  <div class="artifact-create-view">
    <el-card class="create-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2><i class="el-icon-plus"></i> 增加文物建模</h2>
        </div>
      </template>
      
      <el-form :model="artifact" label-width="120px" :rules="rules" ref="artifactForm">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="文物名称" prop="name">
              <el-input v-model="artifact.name" placeholder="请输入文物名称" size="large"></el-input>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="年代" prop="era">
              <el-input v-model="artifact.era" placeholder="请输入文物年代" size="large"></el-input>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="材质" prop="material">
              <el-input v-model="artifact.material" placeholder="请输入文物材质" size="large"></el-input>
            </el-form-item>
          </el-col>
          
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input 
                type="textarea" 
                v-model="artifact.description" 
                placeholder="请输入文物描述"
                :rows="4"
                size="large"
              ></el-input>
            </el-form-item>
          </el-col>
          
          <el-col :span="24">
            <el-form-item label="多模态资源">
              <el-upload
                action="/api/upload"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :file-list="fileList"
                multiple
                drag
              >
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                <div class="el-upload__tip" slot="tip">
                  只能上传jpg/png文件，且不超过500kb
                </div>
              </el-upload>
            </el-form-item>
          </el-col>
          
          <el-col :span="24">
            <el-form-item>
              <el-button type="primary" @click="saveAsDraft" :loading="loading" size="large">
                保存草稿
              </el-button>
              <el-button type="success" @click="submitForReview" :loading="loading" size="large" style="margin-left: 20px;">
                提交审核
              </el-button>
              <el-button @click="resetForm" size="large" style="margin-left: 20px;">
                重置
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

export default {
  name: 'ArtifactCreateView',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const fileList = ref([])
    const artifactForm = ref(null)
    
    const artifact = reactive({
      name: '',
      era: '',
      material: '',
      description: '',
      resourcePaths: [],
      status: 'draft' // draft, pending, approved, rejected
    })
    
    const rules = {
      name: [
        { required: true, message: '请输入文物名称', trigger: 'blur' }
      ],
      era: [
        { required: true, message: '请输入文物年代', trigger: 'blur' }
      ],
      material: [
        { required: true, message: '请输入文物材质', trigger: 'blur' }
      ],
      description: [
        { required: true, message: '请输入文物描述', trigger: 'blur' }
      ]
    }
    
    const handleUploadSuccess = (response, file, fileList) => {
      // 假设上传成功后返回文件路径
      artifact.resourcePaths.push(response.path)
      ElMessage.success('文件上传成功')
    }
    
    const handleUploadError = (err, file, fileList) => {
      ElMessage.error('文件上传失败')
    }
    
    const submitArtifact = async () => {
      if (!artifact.name) {
        ElMessage.warning('请输入文物名称')
        return
      }
      
      loading.value = true
      try {
        // 保存为草稿或提交审核
        const endpoint = artifact.status === 'draft' ? '/artifacts/draft' : '/artifacts'
        await axios.post(endpoint, artifact)
        
        if (artifact.status === 'draft') {
          ElMessage.success('已保存为草稿')
        } else {
          ElMessage.success('文物建模已提交，等待审核')
        }
        router.push('/artifact/search')
      } catch (err) {
        ElMessage.error('操作失败：' + (err.response?.data?.message || '未知错误'))
      } finally {
        loading.value = false
      }
    }
    
    const saveAsDraft = () => {
      artifact.status = 'draft'
      submitArtifact()
    }
    
    const submitForReview = () => {
      artifact.status = 'pending'
      submitArtifact()
    }
    
    const resetForm = () => {
      artifact.name = ''
      artifact.era = ''
      artifact.material = ''
      artifact.description = ''
      artifact.resourcePaths = []
      artifact.status = 'draft'
      fileList.value = []
    }
    
    return {
      artifact,
      loading,
      fileList,
      rules,
      artifactForm,
      handleUploadSuccess,
      handleUploadError,
      submitArtifact,
      saveAsDraft,
      submitForReview,
      resetForm
    }
  }
}
</script>

<style scoped>
.artifact-create-view {
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

.el-form-item {
  margin-bottom: 20px;
}
</style>