<template>
  <div class="ai-assistant-container">
    <el-container class="full-height">
      <!-- 左侧聊天记录侧边栏 -->
      <transition name="sidebar-transition">
        <el-aside v-if="sidebarVisible" width="300px" class="sidebar">
          <div class="sidebar-header">
            <div class="sidebar-title">
              <h3>聊天记录</h3>
            </div>
            <!-- 隐藏按钮：点击收起侧边栏 -->
            <el-button
              type="default"
              size="small"
              class="toggle-btn sidebar-toggle"
              @click="sidebarVisible = false"
              title="收起聊天记录"
              plain
            >
              <i class="el-icon-arrow-left"></i>
              <span>收起</span>
            </el-button>
          </div>
          
          <!-- 开启新对话按钮 -->
          <div class="new-conversation-wrapper">
            <el-button
              class="new-conversation-btn"
              @click="startNewConversation"
            >
              <div class="new-conversation-icon">
                <i class="el-icon-plus"></i>
              </div>
              <span>开启新对话</span>
            </el-button>
          </div>
          
          <el-scrollbar class="history-scroll">
            <el-menu class="history-menu">
              <el-menu-item
                v-for="(history, index) in chatHistories"
                :key="index"
                @click="loadHistory(index)"
                :class="{ active: currentHistoryIndex === index }"
              >
                <span class="history-item-content">
                  <span class="history-title">{{ history.title || `对话 ${index + 1}` }}</span>
                </span>
              </el-menu-item>
            </el-menu>
          </el-scrollbar>
        </el-aside>
      </transition>

      <!-- 主聊天区域 -->
      <el-main class="chat-main">
        <!-- 侧边栏隐藏时显示的展开按钮（浮动在左上角） -->
        <el-button
          v-if="!sidebarVisible"
          type="primary"
          class="toggle-btn floating-toggle"
          @click="sidebarVisible = true"
          title="显示聊天记录"
        >
          <i class="el-icon-arrow-right"></i>
          <span>聊天记录</span>
        </el-button>

        <!-- 聊天内容区 -->
        <div class="chat-history" ref="chatHistory">
          <div
            v-for="(message, index) in messages"
            :key="index"
            :class="['message', message.role]"
          >
            <div class="message-content">
              <div class="avatar">
                <i :class="message.role === 'user' ? 'el-icon-user' : 'el-icon-robot'"></i>
              </div>
              <div class="text">
                <div class="username">{{ message.role === 'user' ? '我' : 'AI助手' }}</div>
                <div class="content" v-html="renderMarkdown(message.content)"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入框固定在最底部 -->
        <div class="chat-input fixed-bottom">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="2"
            placeholder="请输入您的问题...（Ctrl+Enter 发送）"
            @keydown.ctrl.enter="sendMessage"
          />
          <div class="chat-actions">
            <el-button type="primary" @click="sendMessage" :loading="sending">发送</el-button>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { ref, reactive, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/api'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return '<pre class="hljs"><code>' + hljs.highlight(str, { language: lang, ignoreIllegals: true }).value + '</code></pre>'
      } catch (_) {}
    }
    return '<pre class="hljs"><code>' + md.utils.escapeHtml(str) + '</code></pre>'
  }
})

export default {
  setup() {
    const sidebarVisible = ref(true)  // 默认显示侧边栏
    const messages = reactive([])
    const chatHistory = ref(null)
    const sending = ref(false)
    const inputMessage = ref('')
    
    // 游客使用次数限制
    const guestUsageCount = ref(0)
    const maxGuestUsage = 5
    
    // 聊天历史记录
    const chatHistories = ref([
      { 
        title: '默认对话', 
        messages: [
          {
            role: 'assistant',
            content: '您好！我是文物资源知识管理AI助手，您可以向我提问任何关于文物的问题。' + 
                     (localStorage.getItem('role') === 'guest' ? `\n\n温馨提示：游客用户每天可使用${maxGuestUsage}次AI助手功能。` : '')
          }
        ]
      }
    ])
    
    const currentHistoryIndex = ref(0)

    // 渲染 Markdown
    const renderMarkdown = (content) => {
      return md.render(content || '')
    }
    
    const sendMessage = async () => {
      if (!inputMessage.value.trim()) {
        ElMessage.warning('请输入消息内容')
        return
      }
      
      // 游客使用次数检查
      const userRole = localStorage.getItem('role') || 'guest'
      if (userRole === 'guest' && guestUsageCount.value >= maxGuestUsage) {
        ElMessage.warning(`游客用户每天只能使用${maxGuestUsage}次AI助手功能，请登录后继续使用`)
        return
      }
      
      // 添加用户消息
      const userMsg = {
        role: 'user',
        content: inputMessage.value
      }
      messages.push(userMsg)
      
      // 同步到当前历史记录
      chatHistories.value[currentHistoryIndex.value].messages = [...messages]
      
      const userMessage = inputMessage.value
      inputMessage.value = ''
      sending.value = true
      
      try {
        // 滚动到底部
        await nextTick()
        scrollToBottom()
        
        if (userRole === 'guest') {
          guestUsageCount.value++
        }
        const sessionKey = 'aiSessionId'
        const existingSessionId = localStorage.getItem(sessionKey) || undefined

        const res = await request({
          url: '/ai/chat',
          method: 'post',
          data: {
            prompt: userMessage,
            sessionId: existingSessionId
          }
        })

        const aiData = res.data || {}
        const aiMsg = {
          role: 'assistant',
          content: aiData.content || '抱歉，我没有理解您的问题。'
        }

        if (!existingSessionId && aiData.sessionId) {
          localStorage.setItem(sessionKey, aiData.sessionId)
        }

        messages.push(aiMsg)
        
        // 同步到当前历史记录
        chatHistories.value[currentHistoryIndex.value].messages = [...messages]
      } catch (err) {
        const errorMessage = err?.response?.data?.message || err.message || '抱歉，AI助手暂时无法回答您的问题，请稍后再试。'
        const errorMsg = {
          role: 'assistant',
          content: errorMessage
        }
        messages.push(errorMsg)
        
        // 同步到当前历史记录
        chatHistories.value[currentHistoryIndex.value].messages = [...messages]
      } finally {
        sending.value = false
        await nextTick()
        scrollToBottom()
      }
    }
    
    const scrollToBottom = () => {
      if (chatHistory.value) {
        chatHistory.value.scrollTop = chatHistory.value.scrollHeight
      }
    }
    
    // 加载历史记录
    const loadHistory = (index) => {
      currentHistoryIndex.value = index
      messages.splice(0)
      chatHistories.value[index].messages.forEach(msg => {
        messages.push({...msg})
      })
      nextTick(() => {
        scrollToBottom()
      })
    }
    
    // 开启新对话
    const startNewConversation = () => {
      const newHistory = {
        title: `新对话 ${chatHistories.value.length + 1}`,
        messages: [
          {
            role: 'assistant',
            content: '您好！我是文物资源知识管理AI助手，您可以向我提问任何关于文物的问题。' + 
                     (localStorage.getItem('role') === 'guest' ? `\n\n温馨提示：游客用户每天可使用${maxGuestUsage}次AI助手功能。` : '')
          }
        ]
      }
      chatHistories.value.push(newHistory)
      currentHistoryIndex.value = chatHistories.value.length - 1
      messages.splice(0)
      newHistory.messages.forEach(msg => {
        messages.push({...msg})
      })
      nextTick(() => {
        scrollToBottom()
      })
    }

    return {
      sidebarVisible,
      currentHistoryIndex,
      messages,
      chatHistories,
      inputMessage,
      sending,
      renderMarkdown,
      sendMessage,
      loadHistory,
      startNewConversation,
      chatHistory
    }
  }
}
</script>

<style scoped>
.ai-assistant-container {
  height: calc(100vh - 60px);
  overflow: hidden;
  --brand-primary: #42b983;
  --brand-dark: #2c3e50;
  background: #f5f7fa;
  padding: 16px;
}

.full-height {
  height: 100%;
}

/* 侧边栏动画 */
.sidebar-transition-enter-active,
.sidebar-transition-leave-active {
  transition: all 0.3s ease;
}
.sidebar-transition-enter-from,
.sidebar-transition-leave-to {
  transform: translateX(-100%);
  opacity: 0;
}

.sidebar {
  background: #ffffff;
  border-right: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.04);
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
  flex-shrink: 0;
  gap: 12px;
}

.sidebar-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--brand-dark);
}

.sidebar-title h3 {
  margin: 0;
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

/* 开启新对话按钮区域 */
.new-conversation-wrapper {
  padding: 12px 16px;
  flex-shrink: 0;
  border-bottom: 1px solid #ebeef5;
}

.new-conversation-btn {
  width: 100%;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  color: #303133;
  font-size: 14px;
  transition: all 0.2s;
  padding: 0;
}

.new-conversation-btn:hover {
  background: #ecf5ff;
  border-color: #b3d8ff;
  color: #409eff;
}

.new-conversation-icon {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
  color: #606266;
  transition: color 0.2s;
}

.new-conversation-btn:hover .new-conversation-icon {
  color: #409eff;
}

.history-scroll {
  flex: 1;
  height: 0;
  overflow: hidden;
}

.history-menu {
  border-right: none;
  padding: 8px 0;
}

.history-menu .el-menu-item {
  border-radius: 8px;
  margin: 4px 12px;
  height: 44px;
  line-height: 44px;
  padding: 0 12px;
}

.history-menu .el-menu-item.active,
.history-menu .el-menu-item.is-active {
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.history-item-content {
  display: flex;
  align-items: center;
  width: 100%;
  overflow: hidden;
}

.history-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
}

.chat-main {
  padding: 0 !important;
  display: flex;
  flex-direction: column;
  position: relative;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  border: 1px solid #ebeef5;
}

/* 聊天历史内容区 */
.chat-history {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  padding-bottom: 140px; /* 为底部输入框留出空间 */
  background: linear-gradient(180deg, #f9fbfc 0%, #ffffff 80%);
}

/* 输入框固定底部 */
.fixed-bottom {
  position: sticky;
  bottom: 0;
  background: #fff;
  padding: 16px 20px;
  border-top: 1px solid #ebeef5;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
  z-index: 10;
}

.chat-actions {
  margin-top: 12px;
  text-align: right;
}

/* 隐藏侧边栏时显示的展开按钮 */
.floating-toggle {
  position: absolute;
  left: 12px;
  top: 18px;
  z-index: 100;
}

/* 消息气泡样式（保持您原来的） */
.message {
  display: flex;
  margin-bottom: 20px;
}

.message.assistant {
  justify-content: flex-start;
}

.message.user {
  justify-content: flex-end;
}

.message-content {
  display: flex;
  max-width: 80%;
  background: #ffffff;
  border-radius: 12px;
  padding: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}

.message.user .message-content {
  flex-direction: row-reverse;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--brand-dark);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin: 0 10px;
  flex-shrink: 0;
}

.message.user .avatar {
  background-color: var(--brand-primary);
}

.text {
  background-color: #ffffff;
  padding: 8px 12px;
  border-radius: 10px;
}

.message.user .text {
  background-color: var(--brand-primary);
  color: white;
}

.username {
  font-size: 12px;
  margin-bottom: 5px;
  opacity: 0.8;
}

.content {
  line-height: 1.6;
  word-wrap: break-word;
}

.message.user .content {
  color: white;
}

/* Markdown 渲染美化（您之前已加，这里保留关键部分） */
.content :deep(p) { margin: 8px 0; }
.content :deep(ul), .content :deep(ol) { padding-left: 20px; margin: 8px 0; }
.content :deep(pre) { background: #f6f8fa; padding: 12px; border-radius: 6px; overflow-x: auto; }
.content :deep(code) { background: #f4f4f4; padding: 2px 6px; border-radius: 4px; }
.content :deep(blockquote) { border-left: 4px solid #409eff; padding-left: 16px; margin: 12px 0; color: #666; }

.message.user .content :deep(code) { background: rgba(255, 255, 255, 0.2); }
.message.user .content :deep(pre) { background: rgba(0, 0, 0, 0.2); }
.message.user .content :deep(blockquote) { border-left-color: #a0cfff; color: #e6f0ff; }

/* 统一按钮采用 Element 默认配色，保持与全局一致 */
.toggle-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border-radius: 22px;
  padding: 8px 14px;
  background-color: var(--el-color-primary);
  border-color: var(--el-color-primary);
  color: #fff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.18);
}

.toggle-btn :deep(.el-icon) {
  font-size: 14px;
}

.sidebar-toggle {
  padding: 8px 12px;
}
</style>
