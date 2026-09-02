<template>
  <div class="feedback-view">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2><i class="el-icon-chat-line-square"></i> 提交反馈</h2>
        </div>
      </template>
      
      <el-form :model="form" label-width="120px" ref="feedbackForm">
        <el-form-item label="反馈类型" prop="type" :rules="[{ required: true, message: '请选择反馈类型', trigger: 'change' }]">
          <el-select v-model="form.type" placeholder="请选择反馈类型">
            <el-option label="建议" value="suggestion"></el-option>
            <el-option label="问题" value="issue"></el-option>
            <el-option label="功能需求" value="feature"></el-option>
            <el-option label="其他" value="other"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="标题" prop="title" :rules="[{ required: true, message: '请输入标题', trigger: 'blur' }]">
          <el-input v-model="form.title" placeholder="请输入反馈标题"></el-input>
        </el-form-item>
        
        <el-form-item label="内容" prop="content" :rules="[{ required: true, message: '请输入反馈内容', trigger: 'blur' }]">
          <el-input 
            type="textarea" 
            v-model="form.content" 
            :rows="6" 
            placeholder="请详细描述您的反馈内容"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="form.contact" placeholder="可选，方便我们与您联系"></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitFeedback" :loading="loading">提交反馈</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <h3><i class="el-icon-info"></i> 反馈须知</h3>
        </div>
      </template>
      
      <div class="feedback-notes">
        <p><i class="el-icon-circle-check"></i> 我们会认真对待每一条反馈，努力改进系统功能和服务质量</p>
        <p><i class="el-icon-circle-check"></i> 如需技术支持，请尽量详细描述遇到的问题和操作步骤</p>
        <p><i class="el-icon-circle-check"></i> 功能建议请说明具体场景和预期效果</p>
        <p><i class="el-icon-circle-check"></i> 提交后可在"反馈管理"中查看处理进度</p>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { submitFeedback as submitFeedbackApi } from '@/api/feedback'

export default {
  name: 'FeedbackView',
  setup() {
    const feedbackForm = ref(null)
    const loading = ref(false)
    
    const form = reactive({
      type: '',
      title: '',
      content: '',
      contact: ''
    })
    
    const typeMapping = {
      'suggestion': 0,
      'issue': 1,
      'feature': 2,
      'other': 0 // 默认为建议
    }

    const submitFeedback = () => {
      feedbackForm.value.validate(async (valid) => {
        if (!valid) return
        
        // 获取用户信息
        const userInfoStr = localStorage.getItem('userInfo')
        if (!userInfoStr) {
          ElMessage.warning('请先登录后再提交反馈')
          return
        }
        
        let userId
        try {
          const userInfo = JSON.parse(userInfoStr)
          userId = userInfo.id
        } catch (e) {
          console.error('解析用户信息失败', e)
          ElMessage.warning('用户信息异常，请重新登录')
          return
        }

        loading.value = true
        
        try {
          const feedbackData = {
            userId: userId,
            feedbackType: typeMapping[form.type] ?? 0,
            feedbackTitle: form.title,
            feedbackContent: form.content,
            contactInfo: form.contact
          }

          await submitFeedbackApi(feedbackData)
          
          ElMessage.success('反馈提交成功，感谢您的宝贵意见！')
          resetForm()
        } catch (err) {
          console.error('提交反馈失败', err)
          ElMessage.error('提交失败：' + (err.response?.data?.message || '未知错误'))
        } finally {
          loading.value = false
        }
      })
    }
    
    const resetForm = () => {
      form.type = ''
      form.title = ''
      form.content = ''
      form.contact = ''
      
      if (feedbackForm.value) {
        feedbackForm.value.resetFields()
      }
    }
    
    return {
      feedbackForm,
      loading,
      form,
      submitFeedback,
      resetForm
    }
  }
}
</script>

<style scoped>
.feedback-view {
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

.feedback-notes p {
  margin: 10px 0;
  color: #606266;
}

.feedback-notes i {
  color: #67c23a;
  margin-right: 5px;
}
</style>