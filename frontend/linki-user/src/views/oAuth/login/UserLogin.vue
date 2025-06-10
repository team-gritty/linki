<template>
  <div class="user-login-page">
    <div class="user-login-container">
      <div class="user-login-box">
        <div class="user-login-form">
          <h1 class="user-login-title">Log in to LINKI</h1>
          <p class="user-login-subtitle">Enter your details below</p>

          <div class="user-input-group">
            <input
                type="text"
                v-model="userId"
                class="user-input"
                placeholder="아이디"
                @keyup.enter="handleLogin"
            />
          </div>

          <div class="user-input-group">
            <input
                type="password"
                v-model="password"
                class="user-input"
                placeholder="패스워드"
                @keyup.enter="handleLogin"
            />
          </div>

          <button
              class="user-login-button"
              @click="handleLogin"
              :disabled="isLoading"
          >
            {{ isLoading ? 'Logging in...' : 'Log In' }}
          </button>

          <button
              class="user-google-button"
              @click="handleGoogleLogin"
              :disabled="isLoading"
          >
            <img src="../../../assets/google-icon.svg" alt="Google" class="user-google-icon" />
            Sign in with Google
          </button>

          <div class="user-help-links">
            <router-link to="/findid" class="user-link">아이디 찾기</router-link>
            <router-link to="/findpassword" class="user-link">비밀번호 찾기</router-link>
          </div>

          <div class="user-signup-prompt">
            <span>처음 방문하시나요?</span>
            <router-link to="/signup" class="user-signup-link">회원가입</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userId = ref('')
const password = ref('')
const isLoading = ref(false)

const handleLogin = async () => {
  if (!userId.value || !password.value) {
    console.error('Please fill in all fields')
    return
  }

  try {
    isLoading.value = true
    const loginData = {
      userId: userId.value,
      password: password.value
    }
    console.log('Login attempt with:', loginData)
    await new Promise(resolve => setTimeout(resolve, 1000))
    router.push('/dashboard')
  } catch (error) {
    console.error('Login failed:', error)
  } finally {
    isLoading.value = false
  }
}

const handleGoogleLogin = async () => {
  try {
    isLoading.value = true
    console.log('Google login attempt')
    await new Promise(resolve => setTimeout(resolve, 1000))
    router.push('/dashboard')
  } catch (error) {
    console.error('Google login failed:', error)
  } finally {
    isLoading.value = false
  }
}

const goToFindId = () => {
  router.push('/find-id')
}

defineOptions({
  name: 'UserLogin'
})
</script>

<style scoped>
.user-login-page {
  width: 100%;
  min-height: 100vh;
  background-color: #fff;
}

.user-login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 48px);
  width: 100%;
  padding: 2rem;
  box-sizing: border-box;
}

.user-login-box {
  width: 100%;
  max-width: 400px;
  background-color: #fff;
  border-radius: 8px;
  padding: 2rem;
}

.user-login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.user-login-title {
  font-size: 32px;
  font-weight: 600;
  color: #000;
  margin: 0;
  text-align: center;
}

.user-login-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
  text-align: center;
  margin-bottom: 2rem;
}

.user-input-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 1rem;
}

.user-input {
  padding: 0.75rem 0;
  border: none;
  border-bottom: 1px solid #ddd;
  font-size: 16px;
  transition: border-color 0.2s;
  width: 100%;
  background: transparent;
}

.user-input:focus {
  outline: none;
  border-bottom-color: #d6bcf7;
}

.user-input::placeholder {
  color: #666;
}

.user-login-button {
  background-color: #d6bcf7;
  color: #000;
  padding: 1rem;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-top: 1rem;
  width: 100%;
}

.user-login-button:hover {
  background-color: #c4a3f7;
}

.user-google-button {
  background-color: #fff;
  color: #000;
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-top: 1rem;
  width: 100%;
}

.user-google-button:hover {
  background-color: #f5f5f5;
}

.user-google-icon {
  width: 20px;
  height: 20px;
}

.user-help-links {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-top: 1rem;
}

.user-link {
  color: #666;
  text-decoration: none;
  font-size: 14px;
}

.user-link:hover {
  color: #000;
}

.user-signup-prompt {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-top: 1rem;
}

.user-signup-link {
  color: #000;
  text-decoration: underline;
  margin-left: 0.5rem;
  font-weight: 500;
}

.user-signup-link:hover {
  color: #666;
}
</style> 