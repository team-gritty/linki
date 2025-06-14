<template>
  <div class="admin-signup-page">
    <div class="admin-signup-container">
      <div class="admin-signup-box">
        <div class="admin-signup-form">
          <h1 class="admin-signup-title">Create an Admin account</h1>
          <p class="admin-signup-subtitle">Enter your details below</p>

          <div class="admin-input-group">
            <input type="text" v-model="userId" class="admin-input" placeholder="아이디" />
          </div>

          <div class="admin-input-group">
            <input type="password" v-model="password" class="admin-input" placeholder="패스워드" />
          </div>

          <div class="admin-input-group">
            <input type="password" v-model="confirmPassword" class="admin-input" placeholder="패스워드 확인" />
          </div>

          <div class="admin-input-group">
            <input type="text" v-model="name" class="admin-input" placeholder="이름" />
          </div>

          <div class="admin-input-group">
            <input type="text" v-model="handphone" class="admin-input" placeholder="핸드폰" />
          </div>

          <div class="admin-input-group">
            <input type="email" v-model="email" class="admin-input" placeholder="이메일" />
          </div>

          <button class="admin-signup-button" @click="handleSignup" :disabled="isLoading">
            {{ isLoading ? 'Creating Account...' : 'Create Account' }}
          </button>

          <button class="admin-google-button" @click="handleGoogleSignup" :disabled="isLoading">
            <img src="../../../assets/google-icon.svg" alt="Google" class="admin-google-icon" />
            Sign up with Google
          </button>

          <div class="admin-login-prompt">
            <span>Already have account?</span>
            <router-link to="/login" class="admin-login-link">Log in</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const userId = ref('')
const password = ref('')
const confirmPassword = ref('')
const name = ref('')
const handphone = ref('')
const email = ref('')
const isLoading = ref(false)

const isFormValid = computed(() => {
  return (
    userId.value.length > 0 &&
    password.value.length > 0 &&
    password.value === confirmPassword.value &&
    name.value.length > 0 &&
    handphone.value.length > 0 &&
    email.value.length > 0 &&
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)
  )
})

const handleSignup = async () => {
  if (!isFormValid.value) {
    alert('모든 필드를 올바르게 입력해주세요.')
    return
  }

  try {
    isLoading.value = true
    const signupData = {
      userId: userId.value,
      password: password.value,
      name: name.value,
      handphone: handphone.value,
      email: email.value
    }

    const response = await axios.post('/api/admin/signup', signupData)
    
    if (response.data.success) {
      alert('회원가입이 완료되었습니다.')
      router.push('/login')
    } else {
      alert(response.data.message || '회원가입 중 오류가 발생했습니다.')
    }
  } catch (error) {
    console.error('Signup failed:', error)
    if (error.response) {
      // 서버에서 응답이 왔지만 에러인 경우
      alert(error.response.data.message || '회원가입 중 오류가 발생했습니다.')
    } else if (error.request) {
      // 요청은 보냈지만 응답을 받지 못한 경우
      alert('서버와 통신할 수 없습니다. 잠시 후 다시 시도해주세요.')
    } else {
      // 요청 자체를 보내지 못한 경우
      alert('회원가입 요청을 보내지 못했습니다. 잠시 후 다시 시도해주세요.')
    }
  } finally {
    isLoading.value = false
  }
}

const handleGoogleSignup = async () => {
  try {
    isLoading.value = true
    const response = await axios.get('/api/admin/auth/google')
    window.location.href = response.data.authUrl
  } catch (error) {
    console.error('Google signup failed:', error)
    alert('Google 로그인 중 오류가 발생했습니다.')
  } finally {
    isLoading.value = false
  }
}

defineOptions({
  name: 'AdminSignUp'
})
</script>

<style scoped>
.admin-signup-page {
  width: 100%;
  min-height: 100vh;
  background-color: #fff;
}

.admin-signup-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 48px);
  width: 100%;
  padding: 2rem;
  box-sizing: border-box;
}

.admin-signup-box {
  width: 100%;
  max-width: 400px;
  background-color: #fff;
  border-radius: 8px;
  padding: 2rem;
}

.admin-signup-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.admin-signup-title {
  font-size: 32px;
  font-weight: 600;
  color: #000;
  margin: 0;
  text-align: center;
}

.admin-signup-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
  text-align: center;
  margin-bottom: 2rem;
}

.admin-input-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 1rem;
}

.admin-input {
  padding: 0.75rem 0;
  border: none;
  border-bottom: 1px solid #ddd;
  font-size: 16px;
  transition: border-color 0.2s;
  width: 100%;
  background: transparent;
}

.admin-input:focus {
  outline: none;
  border-bottom-color: #d6bcf7;
}

.admin-input::placeholder {
  color: #666;
}

.admin-signup-button {
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

.admin-signup-button:hover {
  background-color: #c4a3f7;
}

.admin-google-button {
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

.admin-google-button:hover {
  background-color: #f5f5f5;
}

.admin-google-icon {
  width: 20px;
  height: 20px;
}

.admin-login-prompt {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-top: 1rem;
}

.admin-login-link {
  color: #000;
  text-decoration: underline;
  margin-left: 0.5rem;
  font-weight: 500;
}

.admin-login-link:hover {
  color: #666;
}
</style> 