<template>
  <div class="feedback-management-container">
    <el-card>
      <div slot="header">
        <h2>反馈管理</h2>
      </div>
      
      <el-form :inline="true" :model="searchForm" @submit.prevent="searchFeedback">
        <el-form-item label="反馈类型">
          <el-select v-model="searchForm.type" placeholder="请选择反馈类型">
            <el-option label="全部" value=""></el-option>
            <el-option label="建议" value="suggestion"></el-option>
            <el-option label="问题" value="issue"></el-option>
            <el-option label="功能需求" value="feature"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="全部" value=""></el-option>
            <el-option label="待处理" value="pending"></el-option>
            <el-option label="处理中" value="processing"></el-option>
            <el-option label="已解决" value="resolved"></el-option>
            <el-option label="已关闭" value="closed"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchFeedback">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="feedbackList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="title" label="标题"></el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="getFeedbackTypeTag(scope.row.type)">
              {{ getFeedbackTypeName(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getFeedbackStatusTag(scope.row.status)">
              {{ getFeedbackStatusName(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitter" label="提交者" width="120"></el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180"></el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-button size="mini" @click="viewFeedback(scope.row)">查看详情</el-button>
            <el-button 
              v-if="scope.row.status === 'pending' || scope.row.status === 'processing'" 
              size="mini" 
              type="primary" 
              @click="processFeedback(scope.row)"
            >
              处理反馈
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
    
    <!-- 反馈详情对话框 -->
    <el-dialog title="反馈详情" v-model="detailDialogVisible" width="600px">
      <el-form :model="currentFeedback" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="currentFeedback.title" disabled></el-input>
        </el-form-item>
        <el-form-item label="类型">
          <el-input :value="getFeedbackTypeName(currentFeedback.type)" disabled></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="currentFeedback.status">
            <el-option label="待处理" value="pending"></el-option>
            <el-option label="处理中" value="processing"></el-option>
            <el-option label="已解决" value="resolved"></el-option>
            <el-option label="已关闭" value="closed"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="提交者">
          <el-input v-model="currentFeedback.submitter" disabled></el-input>
        </el-form-item>
        <el-form-item label="提交时间">
          <el-input v-model="currentFeedback.submitTime" disabled></el-input>
        </el-form-item>
        <el-form-item label="内容">
          <el-input 
            type="textarea" 
            v-model="currentFeedback.content" 
            disabled
            :rows="4"
          ></el-input>
        </el-form-item>
        <el-form-item label="处理意见" v-if="currentFeedback.status !== 'pending'">
          <el-input 
            type="textarea" 
            v-model="currentFeedback.processingNote" 
            :rows="3"
            placeholder="请输入处理意见"
          ></el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveFeedback">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { reactive, ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/api'
import { getUserInfo } from '@/api/user'

export default {
  name: 'FeedbackManagementView',
  setup() {
    const loading = ref(false)
    const detailDialogVisible = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const currentUserId = ref(null)
    
    const totalPages = computed(() => Math.ceil(total.value / pageSize.value))
    
    const searchForm = reactive({
      type: '',
      status: ''
    })
    
    const feedbackList = ref([])
    const currentFeedback = ref({})

    const typeKeyToCode = {
      suggestion: 0,
      issue: 1,
      feature: 2
    }

    const typeCodeToKey = {
      0: 'suggestion',
      1: 'issue',
      2: 'feature'
    }

    const statusKeyToCode = {
      pending: 0,
      processing: 1,
      resolved: 2,
      closed: 3
    }

    const statusCodeToKey = {
      0: 'pending',
      1: 'processing',
      2: 'resolved',
      3: 'closed'
    }

    const initCurrentUserId = async () => {
      const storedUserInfo = localStorage.getItem('userInfo')
      if (storedUserInfo) {
        try {
          const user = JSON.parse(storedUserInfo)
          if (user && user.id) {
            currentUserId.value = user.id
            return
          }
        } catch (e) {
        }
      }

      try {
        const res = await getUserInfo()
        if (res && res.data && res.data.id) {
          currentUserId.value = res.data.id
        }
      } catch (e) {
      }
    }
    
    const loadFeedback = async () => {
      loading.value = true
      try {
        const res = await request({
          url: '/feedback/list',
          method: 'get',
          params: {
            pageNum: currentPage.value,
            pageSize: pageSize.value,
            feedbackType: searchForm.type ? typeKeyToCode[searchForm.type] : undefined,
            status: searchForm.status ? statusKeyToCode[searchForm.status] : undefined
          }
        })

        const pageData = res.data || {}
        const records = pageData.records || []

        feedbackList.value = records.map(item => ({
          id: item.id,
          title: item.feedbackTitle,
          type: typeCodeToKey[item.feedbackType],
          status: statusCodeToKey[item.status],
          submitter: item.username,
          submitTime: item.submittedAt,
          content: item.feedbackContent,
          processingNote: item.processResult,
          processedBy: item.processedBy,
          processedByName: item.processedByName
        }))

        total.value = pageData.total || 0
      } catch (err) {
        const message = err.message || '加载反馈列表失败'
        ElMessage.error(message)
      } finally {
        loading.value = false
      }
    }
    
    const searchFeedback = () => {
      currentPage.value = 1
      loadFeedback()
    }
    
    const resetSearch = () => {
      searchForm.type = ''
      searchForm.status = ''
      currentPage.value = 1
      loadFeedback()
    }
    
    const handlePageChange = (page) => {
      currentPage.value = page
      loadFeedback()
    }
    
    const viewFeedback = (feedback) => {
      currentFeedback.value = { ...feedback }
      detailDialogVisible.value = true
    }
    
    const processFeedback = (feedback) => {
      currentFeedback.value = { ...feedback }
      // 设置为处理中状态
      currentFeedback.value.status = 'processing'
      detailDialogVisible.value = true
    }
    
    const saveFeedback = async () => {
      if (!currentFeedback.value || !currentFeedback.value.id) {
        return
      }

      if (!currentUserId.value) {
        ElMessage.error('当前用户信息获取失败，无法保存反馈处理结果')
        return
      }

      try {
        const statusCode = statusKeyToCode[currentFeedback.value.status] ?? currentFeedback.value.status

        await request({
          url: `/feedback/process/${currentFeedback.value.id}`,
          method: 'put',
          params: {
            status: statusCode,
            processResult: currentFeedback.value.processingNote,
            processedBy: currentUserId.value
          }
        })

        ElMessage.success('保存成功')
        detailDialogVisible.value = false
        loadFeedback()
      } catch (err) {
        const message = err.message || '保存失败'
        ElMessage.error(message)
      }
    }
    
    const getFeedbackTypeTag = (type) => {
      const typeMap = {
        suggestion: 'success',
        issue: 'danger',
        feature: 'primary'
      }
      return typeMap[type] || 'info'
    }
    
    const getFeedbackTypeName = (type) => {
      const nameMap = {
        suggestion: '建议',
        issue: '问题',
        feature: '功能需求'
      }
      return nameMap[type] || '未知'
    }
    
    const getFeedbackStatusTag = (status) => {
      const statusMap = {
        pending: 'info',
        processing: 'warning',
        resolved: 'success',
        closed: 'danger'
      }
      return statusMap[status] || 'info'
    }
    
    const getFeedbackStatusName = (status) => {
      const nameMap = {
        pending: '待处理',
        processing: '处理中',
        resolved: '已解决',
        closed: '已关闭'
      }
      return nameMap[status] || '未知'
    }
    
    initCurrentUserId()
    loadFeedback()
    
    return {
      loading,
      detailDialogVisible,
      currentPage,
      pageSize,
      total,
      totalPages,
      searchForm,
      feedbackList,
      currentFeedback,
      searchFeedback,
      resetSearch,
      handlePageChange,
      viewFeedback,
      processFeedback,
      saveFeedback,
      getFeedbackTypeTag,
      getFeedbackTypeName,
      getFeedbackStatusTag,
      getFeedbackStatusName
    }
  }
}
</script>

<style scoped>
.feedback-management-container {
  padding: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>