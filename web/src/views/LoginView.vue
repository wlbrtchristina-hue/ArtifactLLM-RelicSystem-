<template>
  <div class="login-view">
    <div class="login-container">
      <el-card class="login-card">
        <div slot="header">
          <h2>用户登录</h2>
        </div>
        
        <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" label-width="80px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
          </el-form-item>
          
          <el-form-item label="密码" prop="password">
            <el-input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="请输入密码"
              show-password
            ></el-input>
          </el-form-item>
          
          <el-form-item label="验证码" prop="captcha">
            <el-row :gutter="10" style="width: 100%">
              <el-col :span="14">
                <el-input v-model="loginForm.captcha" placeholder="请输入验证码"></el-input>
              </el-col>
              <el-col :span="10" class="captcha-col">
                <img
                  v-if="captchaImage"
                  :src="captchaImage"
                  alt="验证码"
                  class="captcha-image"
                  @click="loadCaptcha"
                />
                <el-button v-else type="text" @click="loadCaptcha">获取验证码</el-button>
              </el-col>
            </el-row>
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              @click="handleLogin" 
              :loading="loading"
              style="width: 100%"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="register-link">
          还没有账号？<router-link to="/register">立即注册</router-link>
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
import { login, getCaptcha } from '@/api/user'

export default {
  name: 'LoginView',
  setup() {
    const store = useStore()
    const router = useRouter()
    const loginFormRef = ref(null)
    const captchaImage = ref('')
    
    const loginForm = reactive({
      username: '',
      password: '',
      captcha: '',
      captchaUuid: ''
    })
    
    const loading = ref(false)
    
    const loginRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度至少6位', trigger: 'blur' }
      ],
      captcha: [
        { required: true, message: '请输入验证码', trigger: 'blur' }
      ]
    }
    
    const loadCaptcha = async () => {
      try {
        const res = await getCaptcha()
        loginForm.captchaUuid = res.data.uuid
        captchaImage.value = res.data.image
      } catch (e) {
      }
    }
    
    onMounted(() => {
      loadCaptcha()
    })
    
    const handleLogin = async () => {
      loginFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          
          try {
            // 调用登录API
            const res = await login(loginForm)
            
            // 登录成功，保存token
            const token = res.data.token
            const user = res.data.user
            
            localStorage.setItem('token', token)
            localStorage.setItem('userInfo', JSON.stringify(user))
            
            // 保存用户信息到store
            store.commit('user/login', {
              username: user.username,
              role: user.role || 'user' 
            })
            
            ElMessage.success('登录成功')
            router.push('/')
          } catch (err) {
            console.error(err)
            if (err && typeof err.message === 'string' && err.message.includes('验证码')) {
              loginForm.captcha = ''
              await loadCaptcha()
            }
          } finally {
            loading.value = false
          }
        }
      })
    }
    
    return {
      loginForm,
      loginRules,
      loading,
      loginFormRef,
      handleLogin,
      captchaImage,
      loadCaptcha
    }
  }
}
</script>

<style scoped>
.login-view {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 120px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-container {
  width: 100%;
  max-width: 400px;
  padding: 20px;
}

.login-card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.login-card h2 {
  text-align: center;
  margin: 0;
  color: #303133;
}

.register-link {
  margin-top: 20px;
  text-align: center;
}

.register-link a {
  color: #409eff;
  text-decoration: none;
}

.captcha-image {
  width: 100%;
  height: 40px;
  cursor: pointer;
}

.captcha-col {
  display: flex;
  align-items: center;
}
</style>
