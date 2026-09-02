<template>
  <div class="model-review-view">
    <el-card class="review-card">
      <div slot="header">
        <h2><i class="el-icon-document-checked"></i> 建模审核</h2>
      </div>
      
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="待审核" name="pending">
          <el-table :data="paginatedPendingReviews" style="width: 100%" v-loading="loading">
            <el-table-column prop="type" label="类型" width="120"></el-table-column>
            <el-table-column prop="name" label="名称"></el-table-column>
            <el-table-column prop="creator" label="创建者" width="150"></el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="200"></el-table-column>
            <el-table-column label="操作" width="240" align="left">
              <template #default="scope">
                <div class="operation-buttons">
                  <el-button size="mini" @click="viewDetail(scope.row)">查看详情</el-button>
                  <el-button size="mini" type="success" @click="approve(scope.row)">通过</el-button>
                  <el-button size="mini" type="danger" @click="reject(scope.row)">拒绝</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 待审核分页 -->
          <div class="centered-pagination">
            <span class="pagination-total">共 {{ pendingTotalPages }} 页</span>
            <el-pagination
              background
              layout="prev, pager, next"
              :current-page="pendingCurrentPage"
              :page-size="pageSize"
              :total="pendingReviews.length"
              @current-change="handlePendingPageChange"
            />
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="已通过" name="approved">
          <el-table :data="paginatedApprovedReviews" style="width: 100%" v-loading="loading">
            <el-table-column prop="type" label="类型" width="120"></el-table-column>
            <el-table-column prop="name" label="名称"></el-table-column>
            <el-table-column prop="creator" label="创建者" width="150"></el-table-column>
            <el-table-column prop="approver" label="审核人" width="150"></el-table-column>
            <el-table-column prop="approveTime" label="审核时间" width="200"></el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="mini" @click="viewDetail(scope.row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 已通过分页 -->
          <div class="centered-pagination">
            <span class="pagination-total">共 {{ approvedTotalPages }} 页</span>
            <el-pagination
              background
              layout="prev, pager, next"
              :current-page="approvedCurrentPage"
              :page-size="pageSize"
              :total="approvedReviews.length"
              @current-change="handleApprovedPageChange"
            />
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="已拒绝" name="rejected">
          <el-table :data="paginatedRejectedReviews" style="width: 100%" v-loading="loading">
            <el-table-column prop="type" label="类型" width="120"></el-table-column>
            <el-table-column prop="name" label="名称"></el-table-column>
            <el-table-column prop="creator" label="创建者" width="150"></el-table-column>
            <el-table-column prop="rejecter" label="拒绝人" width="150"></el-table-column>
            <el-table-column prop="rejectReason" label="拒绝原因"></el-table-column>
            <el-table-column prop="rejectTime" label="拒绝时间" width="200"></el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="mini" @click="viewDetail(scope.row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 已拒绝分页 -->
          <div class="centered-pagination">
            <span class="pagination-total">共 {{ rejectedTotalPages }} 页</span>
            <el-pagination
              background
              layout="prev, pager, next"
              :current-page="rejectedCurrentPage"
              :page-size="pageSize"
              :total="rejectedReviews.length"
              @current-change="handleRejectedPageChange"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
    
    <!-- 审核详情对话框 -->
    <el-dialog :title="detailDialogTitle" v-model="detailDialogVisible" width="800px">
      <div v-if="currentReview">
        <!-- 实体类型详情 -->
        <div v-if="currentReview.reviewType === 'entityType'">
          <el-form label-width="120px">
            <el-form-item label="类型名称">
              <el-input v-model="currentReview.data.name" :disabled="!isPending"></el-input>
            </el-form-item>
            <el-form-item label="描述">
              <el-input v-model="currentReview.data.description" type="textarea" :disabled="!isPending"></el-input>
            </el-form-item>
            <el-form-item label="属性列表">
              <el-table :data="currentReview.data.attributes" style="width: 100%">
                <el-table-column prop="name" label="属性名称"></el-table-column>
                <el-table-column prop="type" label="属性类型"></el-table-column>
                <el-table-column prop="description" label="描述"></el-table-column>
              </el-table>
            </el-form-item>
          </el-form>
        </div>
        
        <!-- 实体详情 -->
        <div v-else-if="currentReview.reviewType === 'entity'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form label-width="120px">
                <el-form-item label="文物名称">
                  <el-input v-model="currentReview.data.name" :disabled="!isPending"></el-input>
                </el-form-item>
                <el-form-item label="实体类型">
                  <el-input v-model="currentReview.data.type" :disabled="true"></el-input>
                </el-form-item>
                <el-form-item label="年代">
                  <el-input v-model="currentReview.data.era" :disabled="!isPending"></el-input>
                </el-form-item>
                <el-form-item label="材质">
                  <el-input v-model="currentReview.data.material" :disabled="!isPending"></el-input>
                </el-form-item>
                <el-form-item label="出土地点">
                  <el-input v-model="currentReview.data.location" :disabled="!isPending"></el-input>
                </el-form-item>
                <el-form-item label="馆藏位置">
                  <el-input v-model="currentReview.data.storageLocation" :disabled="!isPending"></el-input>
                </el-form-item>
                <el-form-item label="详细描述">
                  <el-input v-model="currentReview.data.description" type="textarea" :disabled="!isPending"></el-input>
                </el-form-item>
              </el-form>
            </el-col>
            <el-col :span="12">
              <div class="image-preview">
                <h4>文物图片</h4>
                <div v-if="currentReview.data.images && currentReview.data.images.length > 0">
                  <el-image
                    v-for="(image, index) in currentReview.data.images"
                    :key="index"
                    :src="image"
                    class="preview-image"
                    fit="cover"
                    :preview-src-list="currentReview.data.images"
                  ></el-image>
                </div>
                <div v-else>
                  <p>暂无图片</p>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <!-- 文物修改详情 -->
        <div v-else-if="currentReview.reviewType === 'artifactModification'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form label-width="120px">
                <el-form-item label="文物名称">
                  <el-input v-model="currentReview.data.name" :disabled="!isPending"></el-input>
                </el-form-item>
                <el-form-item label="年代">
                  <el-input v-model="currentReview.data.era" :disabled="!isPending"></el-input>
                </el-form-item>
                <el-form-item label="材质">
                  <el-input v-model="currentReview.data.material" :disabled="!isPending"></el-input>
                </el-form-item>
                <el-form-item label="出土地点">
                  <el-input v-model="currentReview.data.location" :disabled="!isPending"></el-input>
                </el-form-item>
                <el-form-item label="馆藏位置">
                  <el-input v-model="currentReview.data.storageLocation" :disabled="!isPending"></el-input>
                </el-form-item>
                <el-form-item label="详细描述">
                  <el-input v-model="currentReview.data.description" type="textarea" :disabled="!isPending"></el-input>
                </el-form-item>
              </el-form>
            </el-col>
            <el-col :span="12">
              <div class="image-preview">
                <h4>文物图片</h4>
                <div v-if="currentReview.data.images && currentReview.data.images.length > 0">
                  <el-image
                    v-for="(image, index) in currentReview.data.images"
                    :key="index"
                    :src="image"
                    class="preview-image"
                    fit="cover"
                    :preview-src-list="currentReview.data.images"
                  ></el-image>
                </div>
                <div v-else>
                  <p>暂无图片</p>
                </div>
              </div>
              
              <div class="image-preview" style="margin-top: 20px;">
                <h4>原始图片</h4>
                <div v-if="currentReview.originalData && currentReview.originalData.images && currentReview.originalData.images.length > 0">
                  <el-image
                    v-for="(image, index) in currentReview.originalData.images"
                    :key="'original'+index"
                    :src="image"
                    class="preview-image"
                    fit="cover"
                    :preview-src-list="currentReview.originalData.images"
                  ></el-image>
                </div>
                <div v-else>
                  <p>暂无图片</p>
                </div>
              </div>
            </el-col>
          </el-row>
          
          <el-divider>知识图谱变更</el-divider>
          <el-row :gutter="20">
            <el-col :span="12">
              <h4>关联文物变更</h4>
              <el-table :data="currentReview.data.relatedArtifacts" style="width: 100%">
                <el-table-column prop="name" label="文物名称"></el-table-column>
                <el-table-column prop="relation" label="关联类型"></el-table-column>
              </el-table>
            </el-col>
            <el-col :span="12">
              <h4>相关事件变更</h4>
              <el-table :data="currentReview.data.events" style="width: 100%">
                <el-table-column prop="name" label="事件名称"></el-table-column>
                <el-table-column prop="time" label="时间"></el-table-column>
              </el-table>
            </el-col>
          </el-row>
        </div>
        
        <!-- 文物删除详情 -->
        <div v-else-if="currentReview.reviewType === 'artifactDeletion'">
          <el-alert
            title="文物删除申请"
            type="warning"
            description="用户申请删除以下文物，请仔细审查后再决定是否批准删除操作。"
            show-icon
          ></el-alert>
          
          <el-form label-width="120px" style="margin-top: 20px;">
            <el-form-item label="文物名称">
              <el-input v-model="currentReview.data.name" :disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="年代">
              <el-input v-model="currentReview.data.era" :disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="材质">
              <el-input v-model="currentReview.data.material" :disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="出土地点">
              <el-input v-model="currentReview.data.location" :disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="馆藏位置">
              <el-input v-model="currentReview.data.storageLocation" :disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="详细描述">
              <el-input v-model="currentReview.data.description" type="textarea" :disabled="true"></el-input>
            </el-form-item>
          </el-form>
          
          <div class="image-preview">
            <h4>文物图片</h4>
            <div v-if="currentReview.data.images && currentReview.data.images.length > 0">
              <el-image
                v-for="(image, index) in currentReview.data.images"
                :key="index"
                :src="image"
                class="preview-image"
                fit="cover"
                :preview-src-list="currentReview.data.images"
              ></el-image>
            </div>
            <div v-else>
              <p>暂无图片</p>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer v-if="isPending">
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">取消</el-button>
          <el-button type="success" @click="approveCurrent">通过</el-button>
          <el-button type="danger" @click="showRejectDialog = true">拒绝</el-button>
        </span>
      </template>
      <template #footer v-else>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 拒绝原因对话框 -->
    <el-dialog title="拒绝原因" v-model="showRejectDialog" width="500px">
      <el-input 
        v-model="rejectReason" 
        type="textarea" 
        placeholder="请输入拒绝原因"
        :rows="4"
      ></el-input>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showRejectDialog = false">取消</el-button>
          <el-button type="danger" @click="rejectCurrent">确认拒绝</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/api'

export default {
  name: 'ModelReviewView',
  setup() {
    const activeTab = ref('pending')
    const loading = ref(false)
    const detailDialogVisible = ref(false)
    const showRejectDialog = ref(false)
    const rejectReason = ref('')
    const currentReview = ref(null)
    
    // 分页相关变量
    const pageSize = ref(10)
    const pendingCurrentPage = ref(1)
    const approvedCurrentPage = ref(1)
    const rejectedCurrentPage = ref(1)
    
    // 待审核列表
    const pendingReviews = ref([
      {
        id: 1,
        type: '实体类型',
        name: '陶瓷类',
        reviewType: 'entityType',
        creator: '普通用户A',
        createTime: '2025-04-01 10:00:00',
        data: {
          name: '陶瓷类',
          description: '各种陶瓷制品',
          attributes: [
            { name: '名称', type: 'text', description: '文物名称' },
            { name: '年代', type: 'text', description: '文物所属年代' },
            { name: '材质', type: 'text', description: '文物制作材料' }
          ]
        }
      },
      {
        id: 2,
        type: '文物实体',
        name: '青花瓷瓶',
        reviewType: 'entity',
        creator: '普通用户B',
        createTime: '2025-04-01 11:00:00',
        data: {
          name: '青花瓷瓶',
          type: '陶瓷类',
          era: '明代',
          material: '陶瓷',
          location: '景德镇',
          storageLocation: '故宫博物院',
          description: '明代青花瓷代表作品',
          images: []
        }
      },
      {
        id: 3,
        type: '文物修改',
        name: '司母戊鼎信息修改',
        reviewType: 'artifactModification',
        creator: '普通用户C',
        createTime: '2025-04-01 12:00:00',
        data: {
          name: '司母戊鼎',
          era: '商代晚期',
          material: '青铜',
          location: '河南安阳',
          storageLocation: '中国国家博物馆',
          description: '商代晚期青铜器代表作，目前已知中国古代最重的青铜器',
          images: [],
          relatedArtifacts: [
            { name: '四羊方尊', relation: '同时代文物' }
          ],
          events: [
            { name: '商代青铜文化发展', time: '公元前1600年-公元前1046年' }
          ]
        },
        originalData: {
          name: '司母戊鼎',
          era: '商代晚期',
          material: '青铜',
          location: '河南安阳',
          storageLocation: '中国国家博物馆',
          description: '商代晚期青铜器代表作',
          images: []
        }
      },
      {
        id: 4,
        type: '文物删除',
        name: '破损陶罐删除申请',
        reviewType: 'artifactDeletion',
        creator: '普通用户D',
        createTime: '2025-04-01 13:00:00',
        data: {
          name: '破损陶罐',
          era: '唐代',
          material: '陶瓷',
          location: '西安',
          storageLocation: '陕西历史博物馆',
          description: '一件有破损的唐代陶罐',
          images: []
        }
      }
    ])
    
    // 已通过列表
    const approvedReviews = ref([
      {
        id: 5,
        type: '实体类型',
        name: '书画类',
        reviewType: 'entityType',
        creator: '普通用户E',
        approver: '管理员',
        approveTime: '2025-03-30 15:00:00',
        data: {
          name: '书画类',
          description: '书法绘画作品',
          attributes: [
            { name: '名称', type: 'text', description: '作品名称' },
            { name: '作者', type: 'text', description: '作品作者' },
            { name: '年代', type: 'text', description: '创作年代' }
          ]
        }
      }
    ])
    
    // 已拒绝列表
    const rejectedReviews = ref([
      {
        id: 6,
        type: '文物实体',
        name: '赝品古币',
        reviewType: 'entity',
        creator: '普通用户F',
        rejecter: '高级用户',
        rejectReason: '经鉴定为现代仿制品，不具备文物价值',
        rejectTime: '2025-03-29 14:00:00',
        data: {
          name: '赝品古币',
          type: '钱币类',
          era: '汉代',
          material: '铜',
          location: '私人收藏',
          storageLocation: '无',
          description: '声称是汉代古币',
          images: []
        }
      }
    ])
    
    // 计算属性：分页后的数据
    const paginatedPendingReviews = computed(() => {
      const start = (pendingCurrentPage.value - 1) * pageSize.value
      return pendingReviews.value.slice(start, start + pageSize.value)
    })
    
    const paginatedApprovedReviews = computed(() => {
      const start = (approvedCurrentPage.value - 1) * pageSize.value
      return approvedReviews.value.slice(start, start + pageSize.value)
    })
    
    const paginatedRejectedReviews = computed(() => {
      const start = (rejectedCurrentPage.value - 1) * pageSize.value
      return rejectedReviews.value.slice(start, start + pageSize.value)
    })
    
    // 计算属性：总页数
    const pendingTotalPages = computed(() => Math.ceil(pendingReviews.value.length / pageSize.value))
    const approvedTotalPages = computed(() => Math.ceil(approvedReviews.value.length / pageSize.value))
    const rejectedTotalPages = computed(() => Math.ceil(rejectedReviews.value.length / pageSize.value))
    
    // 当前是否为待审核状态
    const isPending = computed(() => activeTab.value === 'pending')
    
    // 详情对话框标题
    const detailDialogTitle = computed(() => {
      if (!currentReview.value) return ''
      
      const titles = {
        entityType: '实体类型详情',
        entity: '文物实体详情',
        artifactModification: '文物修改详情',
        artifactDeletion: '文物删除申请详情'
      }
      
      return titles[currentReview.value.reviewType] || '详情'
    })
    
    // 查看详情
    const viewDetail = (review) => {
      currentReview.value = review
      detailDialogVisible.value = true
    }
    
    // 通过审核
    const approve = async (review) => {
      loading.value = true
      try {
        await request({
          url: `/audit/${review.id}/approve`,
          method: 'put',
          data: {
            comment: ''
          }
        })
        
        // 从待审核列表移除
        const index = pendingReviews.value.findIndex(item => item.id === review.id)
        if (index > -1) {
          const approvedItem = pendingReviews.value.splice(index, 1)[0]
          // 添加到已通过列表
          approvedItem.approver = '当前用户' // 实际应为当前登录用户
          approvedItem.approveTime = new Date().toLocaleString()
          approvedReviews.value.unshift(approvedItem)
        }
        
        ElMessage.success('审核已通过')
      } catch (err) {
        ElMessage.error('操作失败：' + (err.response?.data?.message || '未知错误'))
      } finally {
        loading.value = false
      }
    }
    
    // 拒绝审核
    const reject = (review) => {
      currentReview.value = review
      showRejectDialog.value = true
    }
    
    // 通过当前审核项
    const approveCurrent = () => {
      if (currentReview.value) {
        approve(currentReview.value)
        detailDialogVisible.value = false
      }
    }
    
    // 拒绝当前审核项
    const rejectCurrent = async () => {
      if (!rejectReason.value.trim()) {
        ElMessage.warning('请输入拒绝原因')
        return
      }
      
      loading.value = true
      try {
        // 模拟API调用，实际项目中请取消注释以下代码
        /*
        await request({
          url: `/audit/${currentReview.value.id}/reject`,
          method: 'put',
          data: {
            comment: '',
            rejectReason: rejectReason.value
          }
        })
        */
        
        // 模拟网络延迟
        await new Promise(resolve => setTimeout(resolve, 500))
        
        // 从待审核列表移除
        const index = pendingReviews.value.findIndex(item => item.id === currentReview.value.id)
        if (index > -1) {
          const rejectedItem = pendingReviews.value.splice(index, 1)[0]
          // 添加到已拒绝列表
          rejectedItem.rejecter = '当前用户' // 实际应为当前登录用户
          rejectedItem.rejectReason = rejectReason.value
          rejectedItem.rejectTime = new Date().toLocaleString()
          rejectedReviews.value.unshift(rejectedItem)
        }
        
        ElMessage.success('已拒绝该申请')
        showRejectDialog.value = false
        detailDialogVisible.value = false
        rejectReason.value = ''
      } catch (err) {
        ElMessage.error('操作失败：' + (err.response?.data?.message || '未知错误'))
      } finally {
        loading.value = false
      }
    }
    
    // 标签页切换
    const handleTabClick = () => {
      // 可以在这里添加刷新数据的逻辑
    }
    
    // 分页处理函数
    const handlePendingPageChange = (page) => {
      pendingCurrentPage.value = page
    }
    
    const handleApprovedPageChange = (page) => {
      approvedCurrentPage.value = page
    }
    
    const handleRejectedPageChange = (page) => {
      rejectedCurrentPage.value = page
    }
    
    return {
      activeTab,
      loading,
      detailDialogVisible,
      showRejectDialog,
      rejectReason,
      currentReview,
      pendingReviews: pendingReviews.value,
      approvedReviews: approvedReviews.value,
      rejectedReviews: rejectedReviews.value,
      isPending,
      detailDialogTitle,
      viewDetail,
      approve,
      reject,
      approveCurrent,
      rejectCurrent,
      handleTabClick,
      // 分页相关
      pageSize,
      pendingCurrentPage,
      approvedCurrentPage,
      rejectedCurrentPage,
      paginatedPendingReviews,
      paginatedApprovedReviews,
      paginatedRejectedReviews,
      pendingTotalPages,
      approvedTotalPages,
      rejectedTotalPages,
      handlePendingPageChange,
      handleApprovedPageChange,
      handleRejectedPageChange
    }
  }
}
</script>

<style scoped>
.model-review-view {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.image-preview {
  margin-top: 10px;
}

.preview-image {
  width: 100px;
  height: 100px;
  margin-right: 10px;
  margin-bottom: 10px;
  display: inline-block;
}

.operation-buttons {
  display: flex;
  gap: 6px;                    /* 减小按钮间距 */
  flex-wrap: nowrap;          /* 强制不换行，始终一行 */
  justify-content: flex-start; /* 左对齐（关键！消除中间空白） */
  align-items: center;         /* 垂直居中 */
  width: fit-content;          /* 容器宽度自适应内容，不拉伸 */
  margin-left: -10px;         /* 向左移动一些 */
}

/* 可选：给按钮设置最小宽度，防止文字太短时变形 */
.operation-buttons .el-button {
  min-width: 60px;
  padding: 7px 10px;          /* 减小按钮内边距 */
}

.centered-pagination {
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