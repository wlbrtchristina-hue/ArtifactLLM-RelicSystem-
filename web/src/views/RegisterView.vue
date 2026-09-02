<template>
  <div class="register-view">
    <div class="register-container">
      <el-card class="register-card">
        <div slot="header">
          <h2>用户注册</h2>
        </div>
        
        <el-alert
          title="注册说明"
          description="注册用户默认为普通用户，高级用户需管理员授权"
          type="info"
          show-icon
          style="margin-bottom: 20px;"
        />
        
        <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" label-width="100px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
          </el-form-item>
          
          <el-form-item label="密码" prop="password">
            <el-input 
              v-model="registerForm.password" 
              type="password" 
              placeholder="请输入密码"
              show-password
            ></el-input>
          </el-form-item>
          
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input 
              v-model="registerForm.confirmPassword" 
              type="password" 
              placeholder="请再次输入密码"
              show-password
            ></el-input>
          </el-form-item>
          
          <el-form-item label="邮箱" prop="email">
            <div style="display: flex;">
              <el-input v-model="registerForm.email" placeholder="请输入邮箱地址" style="flex: 1; margin-right: 10px;"></el-input>
              <el-button 
                type="primary" 
                @click="handleSendCode" 
                :disabled="isSendingCode || countdown > 0"
                :loading="isSendingCode"
              >
                {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
              </el-button>
            </div>
          </el-form-item>

          <el-form-item label="邮箱验证码" prop="emailCode">
            <el-input v-model="registerForm.emailCode" placeholder="请输入6位邮箱验证码"></el-input>
          </el-form-item>
          
          <el-form-item label="图形验证码" prop="captcha">
            <div style="display: flex; align-items: center;">
              <el-input v-model="registerForm.captcha" placeholder="请输入验证码" style="flex: 1; margin-right: 10px;"></el-input>
              <img 
                v-if="captchaImage" 
                :src="captchaImage" 
                @click="loadCaptcha" 
                alt="验证码" 
                style="height: 32px; cursor: pointer;"
              />
            </div>
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              @click="handleRegister" 
              :loading="loading"
              style="width: 100%"
            >
              注册
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="login-link">
          已有账号？<router-link to="/login">立即登录</router-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { reactive, ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register, getCaptcha, sendEmailCode } from '@/api/user'

export default {
  name: 'RegisterView',
  setup() {
    const store = useStore()
    const router = useRouter()
    const registerFormRef = ref(null)
    
    const registerForm = reactive({
      username: '',
      password: '',
      confirmPassword: '',
      email: '',
      emailCode: '',
      captcha: '',
      captchaUuid: ''
    })
    
    const loading = ref(false)
    const isSendingCode = ref(false)
    const countdown = ref(0)
    const captchaImage = ref('')
    
    const validatePassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else {
        if (registerForm.confirmPassword !== '') {
          registerFormRef.value.validateField('confirmPassword')
        }
        callback()
      }
    }
    
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== registerForm.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    
    const registerRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
      ],
      password: [
        { required: true, validator: validatePassword, trigger: 'blur' },
        { min: 6, message: '密码长度至少6位', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, validator: validateConfirmPassword, trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱地址', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ],
      emailCode: [
        { required: true, message: '请输入邮箱验证码', trigger: 'blur' },
        { len: 6, message: '验证码长度为6位', trigger: 'blur' }
      ],
      captcha: [
        { required: true, message: '请输入图形验证码', trigger: 'blur' }
      ]
    }
    
    const loadCaptcha = async () => {
      try {
        const res = await getCaptcha()
        if (res.code === 200) {
          captchaImage.value = res.data.image
          registerForm.captchaUuid = res.data.uuid
        }
      } catch (err) {
        console.error('获取验证码失败', err)
      }
    }
    
    const handleSendCode = async () => {
      const email = registerForm.email
      const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/
      if (!emailRegex.test(email)) {
        ElMessage.error('请输入正确的邮箱地址')
        return
      }
      if (countdown.value > 0 || isSendingCode.value) return
      isSendingCode.value = true
      try {
        const res = await sendEmailCode(email)
        if (res.code === 200) {
          ElMessage.success('验证码发送成功')
          countdown.value = 60
          const timer = setInterval(() => {
            countdown.value--
            if (countdown.value <= 0) {
              clearInterval(timer)
            }
          }, 1000)
        } else {
          ElMessage.error(res.message || '发送失败')
        }
      } catch (err) {
        ElMessage.error('发送失败：' + (err.response?.data?.message || '未知错误'))
      } finally {
        isSendingCode.value = false
      }
    }
    
    const handleRegister = async () => {
      registerFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          
          try {
            const res = await register(registerForm)
            
            if (res.code === 200) {
              ElMessage.success('注册成功')
              // 自动登录或跳转登录页
              router.push('/login')
            } else {
              ElMessage.error(res.message || '注册失败')
              // 刷新验证码
              loadCaptcha()
            }
          } catch (err) {
            ElMessage.error('注册失败：' + (err.response?.data?.message || '未知错误'))
            loadCaptcha()
          } finally {
            loading.value = false
          }
        }
      })
    }
    
    onMounted(() => {
      loadCaptcha()
    })
    
    return {
      registerForm,
      registerRules,
      loading,
      isSendingCode,
      countdown,
      captchaImage,
      registerFormRef,
      loadCaptcha,
      handleSendCode,
      handleRegister
    }
  }
}
</script>

<style scoped>
.register-view {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 120px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-container {
  width: 100%;
  max-width: 500px;
  padding: 20px;
}

.register-card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.login-link {
  margin-top: 20px;
  text-align: center;
}

.login-link a {
  color: #409eff;
  text-decoration: none;
}
</style>
