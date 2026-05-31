<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const mode = ref<'menu' | 'login' | 'register'>('menu')
const loginLoading = ref(false)
const registerLoading = ref(false)
const loginAttempts = ref(0)
const captchaText = ref('')
const captchaInput = ref('')

const loginFormRef = ref<FormInstance>()
const registerFormRef = ref<FormInstance>()

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const loginRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 16, message: '用户名长度为3~16位', trigger: 'blur' },
    {
      pattern: /^(?![0-9]+$)[a-zA-Z0-9]+$/,
      message: '用户名必须包含字母，且只能由字母和数字组成',
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, max: 8, message: '密码长度为3~8位', trigger: 'blur' },
    {
      pattern: /^(?![a-zA-Z]+$)(?![0-9]+$)[a-zA-Z0-9]+$/,
      message: '密码必须包含字母和数字的组合',
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

function generateCaptcha() {
  const letters = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'
  let result = ''
  for (let i = 0; i < 4; i++) {
    result += letters[Math.floor(Math.random() * letters.length)]
  }
  const digit = Math.floor(Math.random() * 10).toString()
  const pos = Math.floor(Math.random() * (result.length + 1))
  result = result.slice(0, pos) + digit + result.slice(pos)
  captchaText.value = result
}

function switchToLogin() {
  mode.value = 'login'
  loginAttempts.value = 0
  loginForm.username = ''
  loginForm.password = ''
  captchaInput.value = ''
  generateCaptcha()
}

function switchToRegister() {
  mode.value = 'register'
  registerForm.username = ''
  registerForm.password = ''
  registerForm.confirmPassword = ''
}

function goBack() {
  mode.value = 'menu'
}

function handleLogin() {
  loginFormRef.value?.validate((valid) => {
    if (!valid) return

    if (captchaInput.value !== captchaText.value) {
      ElMessage.error('验证码输入错误，请重新输入')
      generateCaptcha()
      return
    }

    loginAttempts.value++
    loginLoading.value = true

    // TODO: 对接后端API后替换此处
    setTimeout(() => {
      loginLoading.value = false

      // 模拟登录：假设用户名=密码即可登录
      if (loginForm.username === loginForm.password) {
        ElMessage.success('登录成功')
        userStore.setUser({
          id: 'temp-' + Date.now(),
          username: loginForm.username,
          password: '',
          status: true
        })
        router.push('/character-select')
      } else {
        const remaining = 3 - loginAttempts.value
        if (remaining <= 0) {
          ElMessage.error('密码连续输错3次，账户已锁定！请联系客服')
          goBack()
        } else {
          ElMessage.error(`密码错误，还剩 ${remaining} 次机会`)
        }
      }
    }, 500)
  })
}

function handleRegister() {
  registerFormRef.value?.validate((valid) => {
    if (!valid) return

    registerLoading.value = true

    // TODO: 对接后端API后替换此处
    setTimeout(() => {
      registerLoading.value = false
      ElMessage.success('注册成功！请登录')
      switchToLogin()
    }, 500)
  })
}
</script>

<template>
  <div class="login-page">
    <div class="bg-particles"></div>
    <div class="ring-bg ring-left"></div>
    <div class="ring-bg ring-right"></div>

    <div v-if="mode === 'menu'" class="menu-box">
      <div class="title-block">
        <div class="title-line"></div>
        <h1 class="main-title">
          <span class="title-icon">⚔</span>
          巅峰对决
          <span class="title-icon">⚔</span>
        </h1>
        <div class="title-sub">PEAK CLASH</div>
        <div class="title-divider">
          <span class="divider-dot"></span>
          <span class="divider-line"></span>
          <span class="divider-diamond">◆</span>
          <span class="divider-line"></span>
          <span class="divider-dot"></span>
        </div>
      </div>

      <div class="menu-options">
        <button class="btn-arena btn-cyan" @click="switchToLogin">
          <span class="btn-text">进入战场</span>
          <span class="btn-sub">LOGIN</span>
        </button>
        <button class="btn-arena btn-gold" @click="switchToRegister">
          <span class="btn-text">创建档案</span>
          <span class="btn-sub">REGISTER</span>
        </button>
        <button class="btn-arena btn-red" @click="router.push('/login')">
          <span class="btn-text">离开战场</span>
          <span class="btn-sub">EXIT</span>
        </button>
      </div>

      <div class="version-tag">v1.0.0</div>
    </div>

    <div v-if="mode === 'login'" class="form-box login-form-box">
      <div class="form-header">
        <span class="form-icon">🎯</span>
        <h2>登录战场</h2>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-width="80px"
        size="large"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="验证码">
          <div class="captcha-row">
            <el-input v-model="captchaInput" placeholder="请输入验证码" class="captcha-input" />
            <div class="captcha-display" @click="generateCaptcha">
              {{ captchaText }}
            </div>
          </div>
        </el-form-item>

        <el-form-item class="form-actions">
          <el-button type="primary" :loading="loginLoading" class="submit-btn" @click="handleLogin">
            登 录
          </el-button>
          <el-button class="back-btn" @click="goBack">返 回</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div v-if="mode === 'register'" class="form-box register-form-box">
      <div class="form-header">
        <span class="form-icon">📋</span>
        <h2>创建档案</h2>
      </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-width="80px"
        size="large"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="3~16位，含字母和数字" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="3~8位，字母+数字组合"
            show-password
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>

        <el-form-item class="form-actions">
          <el-button type="success" :loading="registerLoading" class="submit-btn" @click="handleRegister">
            注 册
          </el-button>
          <el-button class="back-btn" @click="goBack">返 回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background:
    radial-gradient(ellipse at 20% 20%, rgba(0, 102, 204, 0.12) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 80%, rgba(180, 120, 40, 0.1) 0%, transparent 50%),
    radial-gradient(ellipse at 50% 0%, rgba(20, 40, 80, 0.6) 0%, transparent 60%),
    linear-gradient(180deg, #0e1428 0%, #0a0d1a 40%, #0d1020 100%);
  overflow: hidden;
}

.login-page::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image:
    repeating-linear-gradient(0deg, transparent, transparent 2px, rgba(255,255,255,0.008) 2px, rgba(255,255,255,0.008) 4px),
    repeating-linear-gradient(90deg, transparent, transparent 2px, rgba(255,255,255,0.008) 2px, rgba(255,255,255,0.008) 4px);
  pointer-events: none;
  z-index: 0;
}

.ring-bg {
  position: absolute;
  width: 600px;
  height: 600px;
  border-radius: 50%;
  border: 1px solid rgba(0, 180, 255, 0.06);
  pointer-events: none;
}

.ring-left {
  left: -300px;
  top: -150px;
  box-shadow: inset 0 0 80px rgba(0, 180, 255, 0.04);
}

.ring-right {
  right: -300px;
  bottom: -150px;
  box-shadow: inset 0 0 80px rgba(255, 180, 40, 0.04);
  border-color: rgba(255, 180, 40, 0.05);
}

.menu-box {
  position: relative;
  z-index: 1;
  text-align: center;
  animation: fadeInDown 0.8s ease-out;
}

.title-block {
  margin-bottom: 40px;
}

.title-line {
  width: 60px;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(0, 200, 255, 0.6), transparent);
  margin: 0 auto 20px;
}

.main-title {
  font-size: 56px;
  font-weight: 900;
  letter-spacing: 8px;
  color: #fff;
  margin-bottom: 8px;
  text-shadow:
    0 0 20px rgba(0, 200, 255, 0.4),
    0 0 60px rgba(0, 200, 255, 0.15),
    0 2px 4px rgba(0, 0, 0, 0.8);
}

.title-icon {
  display: inline-block;
  color: #c8a84e;
  font-size: 42px;
  text-shadow: 0 0 20px rgba(200, 168, 78, 0.6);
}

.title-sub {
  font-size: 14px;
  letter-spacing: 12px;
  color: rgba(255, 255, 255, 0.3);
  margin-bottom: 24px;
}

.title-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0;
}

.divider-line {
  width: 80px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 200, 60, 0.3), transparent);
}

.divider-dot {
  width: 4px;
  height: 4px;
  background: rgba(255, 200, 60, 0.4);
  border-radius: 50%;
  margin: 0 6px;
}

.divider-diamond {
  color: rgba(255, 200, 60, 0.5);
  font-size: 10px;
  margin: 0 4px;
}

.menu-options {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
}

.btn-arena {
  position: relative;
  width: 280px;
  height: 56px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.03);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  overflow: hidden;
}

.btn-arena::before {
  content: '';
  position: absolute;
  inset: 0;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.btn-cyan {
  border: 1px solid rgba(0, 200, 255, 0.2);
}
.btn-cyan::before { background: linear-gradient(180deg, rgba(0, 200, 255, 0.1), transparent); }
.btn-cyan:hover { border-color: rgba(0, 200, 255, 0.5); }
.btn-cyan:hover::before { opacity: 1; }
.btn-cyan:hover { box-shadow: 0 0 24px rgba(0, 200, 255, 0.15), inset 0 0 24px rgba(0, 200, 255, 0.05); }

.btn-gold {
  border: 1px solid rgba(255, 200, 60, 0.2);
}
.btn-gold::before { background: linear-gradient(180deg, rgba(255, 200, 60, 0.1), transparent); }
.btn-gold:hover { border-color: rgba(255, 200, 60, 0.5); }
.btn-gold:hover::before { opacity: 1; }
.btn-gold:hover { box-shadow: 0 0 24px rgba(255, 200, 60, 0.15), inset 0 0 24px rgba(255, 200, 60, 0.05); }

.btn-red {
  border: 1px solid rgba(255, 80, 60, 0.2);
}
.btn-red::before { background: linear-gradient(180deg, rgba(255, 80, 60, 0.1), transparent); }
.btn-red:hover { border-color: rgba(255, 80, 60, 0.5); }
.btn-red:hover::before { opacity: 1; }
.btn-red:hover { box-shadow: 0 0 24px rgba(255, 80, 60, 0.15), inset 0 0 24px rgba(255, 80, 60, 0.05); }

.btn-text {
  font-size: 18px;
  font-weight: 600;
  color: #e0e0e0;
  letter-spacing: 6px;
  position: relative;
  z-index: 1;
}

.btn-sub {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.35);
  letter-spacing: 8px;
  margin-top: 2px;
  position: relative;
  z-index: 1;
}

.version-tag {
  margin-top: 36px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.15);
  letter-spacing: 4px;
}

.form-box {
  position: relative;
  z-index: 1;
  width: 440px;
  padding: 40px 48px;
  background: rgba(8, 12, 24, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  animation: scaleIn 0.5s ease-out;
  backdrop-filter: blur(20px);
}

.login-form-box {
  border-color: rgba(0, 200, 255, 0.15);
  box-shadow: 0 0 40px rgba(0, 150, 255, 0.06), inset 0 1px 0 rgba(255, 255, 255, 0.03);
}

.register-form-box {
  border-color: rgba(255, 200, 60, 0.15);
  box-shadow: 0 0 40px rgba(255, 180, 40, 0.06), inset 0 1px 0 rgba(255, 255, 255, 0.03);
}

.form-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.form-icon {
  font-size: 28px;
}

.form-header h2 {
  font-size: 22px;
  font-weight: 700;
  color: #e8e8e8;
  letter-spacing: 4px;
  margin: 0;
}

.submit-btn {
  width: 140px;
}

.back-btn {
  width: 140px;
}

.form-actions :deep(.el-form-item__content) {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-left: 0 !important;
}

.captcha-row {
  display: flex;
  gap: 12px;
  width: 100%;
}

.captcha-input {
  flex: 1;
}

.captcha-display {
  width: 120px;
  height: 40px;
  background: rgba(0, 0, 0, 0.4);
  border: 1px solid rgba(0, 200, 255, 0.2);
  color: #fff;
  font-size: 22px;
  font-weight: bold;
  letter-spacing: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  cursor: pointer;
  user-select: none;
  font-family: 'Courier New', monospace;
  transition: all 0.2s ease;
}

.captcha-display:hover {
  border-color: rgba(0, 200, 255, 0.5);
  box-shadow: 0 0 12px rgba(0, 200, 255, 0.15);
}

@media (max-width: 768px) {
  .main-title { font-size: 36px; letter-spacing: 4px; }
  .title-icon { font-size: 28px; }
  .title-sub { font-size: 11px; letter-spacing: 8px; }
  .btn-arena { width: 240px; height: 48px; }
  .btn-text { font-size: 16px; letter-spacing: 4px; }
  .form-box { width: 90%; max-width: 400px; padding: 28px 24px; }
  .form-header h2 { font-size: 18px; }
  .captcha-display { width: 100px; font-size: 18px; }
  .submit-btn, .back-btn { width: 120px; }
}

@media (max-width: 480px) {
  .main-title { font-size: 28px; }
  .btn-arena { width: 200px; height: 44px; }
  .btn-text { font-size: 14px; }
  .form-box { width: 92%; padding: 20px 16px; }
  .submit-btn, .back-btn { width: 110px; font-size: 13px; }
  .form-actions :deep(.el-form-item__content) { gap: 8px; }
}
</style>
