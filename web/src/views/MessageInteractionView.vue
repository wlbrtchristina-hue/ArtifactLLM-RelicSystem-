<template>
  <div class="message-interaction-container">
    <el-row :gutter="20" style="height: 100%;">
      <el-col :span="6">
        <el-card class="contact-list-card">
          <div slot="header">
            <h3>联系人</h3>
          </div>
          <el-input 
            placeholder="搜索联系人" 
            v-model="searchContact" 
            class="search-input"
          >
            <template #prefix>
              <i class="el-input__icon el-icon-search"></i>
            </template>
          </el-input>
          <el-menu 
            :default-active="activeContact" 
            @select="selectContact"
            class="contact-menu"
          >
            <el-menu-item 
              v-for="contact in filteredContacts" 
              :key="contact.id" 
              :index="contact.id.toString()"
            >
              <el-badge :is-dot="contact.unread > 0">
                <i class="el-icon-user"></i>
                {{ contact.name }}
              </el-badge>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      
      <el-col :span="18">
        <el-card class="message-card">
          <div slot="header" class="message-header">
            <h3>{{ currentContact ? currentContact.name : '请选择联系人' }}</h3>
          </div>
          
          <div v-if="currentContact" class="message-container">
            <div class="message-history" ref="messageHistory">
              <div 
                v-for="(message, index) in currentMessages" 
                :key="index" 
                :class="['message-item', message.senderId === currentUserId ? 'sent' : 'received']"
              >
                <div class="message-content">
                  <div class="text">{{ message.content }}</div>
                  <div class="time">{{ formatTime(message.timestamp) }}</div>
                </div>
              </div>
            </div>
            
            <div class="message-input">
              <el-input
                type="textarea"
                v-model="newMessage"
                placeholder="请输入消息内容..."
                :rows="3"
              ></el-input>
              <div class="input-actions">
                <el-button type="primary" @click="sendMessage" :loading="sending">发送</el-button>
              </div>
            </div>
          </div>
          
          <div v-else class="no-contact-selected">
            <p>请选择一个联系人开始聊天</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, reactive, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/api'
import { getUserInfo } from '@/api/user'

export default {
  name: 'MessageInteractionView',
  setup() {
    const messageHistory = ref(null)
    const sending = ref(false)
    const searchContact = ref('')
    const activeContact = ref('')
    const newMessage = ref('')
    const currentUserId = ref(null)
    
    const contacts = reactive([
      { id: 1, name: '张三', unread: 0 },
      { id: 2, name: '李四', unread: 3 },
      { id: 3, name: '王五', unread: 0 },
      { id: 4, name: '赵六', unread: 1 },
      { id: 5, name: '钱七', unread: 0 }
    ])
    
    const messages = reactive({
      1: [
        { senderId: 1, content: '你好！', timestamp: '2025-11-24T10:00:00' },
        { senderId: 2, content: '你好！有什么可以帮助你的吗？', timestamp: '2025-11-24T10:01:00' }
      ],
      2: [
        { senderId: 2, content: '关于昨天讨论的文物分类问题，我有一些新的想法。', timestamp: '2025-11-24T09:30:00' },
        { senderId: 1, content: '好的，请详细说明。', timestamp: '2025-11-24T09:35:00' }
      ]
    })

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
    
    const filteredContacts = computed(() => {
      return contacts.filter(contact => 
        contact.name.toLowerCase().includes(searchContact.value.toLowerCase())
      )
    })
    
    const currentContact = computed(() => {
      const contactId = parseInt(activeContact.value)
      return contacts.find(contact => contact.id === contactId)
    })
    
    const currentMessages = computed(() => {
      const contactId = parseInt(activeContact.value)
      return messages[contactId] || []
    })
    
    const selectContact = (index) => {
      activeContact.value = index
      // 清除未读标记
      const contactId = parseInt(index)
      const contact = contacts.find(c => c.id === contactId)
      if (contact) {
        contact.unread = 0
      }
      
      // 滚动到最新消息
      nextTick(() => {
        scrollToBottom()
      })
    }
    
    const sendMessage = async () => {
      if (!newMessage.value.trim()) {
        ElMessage.warning('请输入消息内容')
        return
      }
      
      if (!activeContact.value) {
        ElMessage.warning('请选择联系人')
        return
      }

      if (!currentUserId.value) {
        ElMessage.error('当前用户信息获取失败，无法发送消息')
        return
      }
      
      sending.value = true
      
      try {
        const contactId = parseInt(activeContact.value)
        
        // 添加消息到本地列表
        if (!messages[contactId]) {
          messages[contactId] = []
        }
        
        const content = newMessage.value

        messages[contactId].push({
          senderId: currentUserId.value,
          content,
          timestamp: new Date().toISOString()
        })

        await request({
          url: '/messages',
          method: 'post',
          data: {
            receiverId: contactId,
            content
          }
        })
        
        newMessage.value = ''
      } catch (err) {
        ElMessage.error('发送失败：' + (err.response?.data?.message || '未知错误'))
      } finally {
        sending.value = false
        nextTick(() => {
          scrollToBottom()
        })
      }
    }
    
    const formatTime = (timestamp) => {
      const date = new Date(timestamp)
      return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    }
    
    const scrollToBottom = () => {
      if (messageHistory.value) {
        messageHistory.value.scrollTop = messageHistory.value.scrollHeight
      }
    }
    
    initCurrentUserId()

    return {
      messageHistory,
      sending,
      searchContact,
      activeContact,
      newMessage,
      currentUserId,
      contacts,
      filteredContacts,
      currentContact,
      currentMessages,
      selectContact,
      sendMessage,
      formatTime
    }
  }
}
</script>

<style scoped>
.message-interaction-container {
  padding: 20px;
  height: calc(100vh - 200px);
}

.contact-list-card,
.message-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.contact-menu {
  border: none;
  margin-top: 10px;
}

.contact-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
}

.search-input {
  margin-bottom: 10px;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.message-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.message-history {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 4px;
  margin-bottom: 20px;
}

.message-item {
  margin-bottom: 15px;
}

.message-item.sent {
  text-align: right;
}

.message-item.received {
  text-align: left;
}

.message-content {
  display: inline-block;
  max-width: 70%;
}

.message-item.sent .message-content {
  background-color: #409eff;
  color: white;
}

.message-item.received .message-content {
  background-color: white;
  color: #333;
}

.message-content .text {
  padding: 10px;
  border-radius: 8px;
}

.message-content .time {
  font-size: 12px;
  margin-top: 5px;
  opacity: 0.7;
}

.message-input {
  display: flex;
  flex-direction: column;
}

.input-actions {
  margin-top: 10px;
  text-align: right;
}

.no-contact-selected {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #999;
}
</style>
