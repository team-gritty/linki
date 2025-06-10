<template>
  <div class="user-signup-page">
    <div class="user-signup-container">
      <div class="user-signup-box">
        <div class="user-signup-form">
          <h1 class="user-signup-title">Create an account</h1>
          <p class="user-signup-subtitle">Enter your details below</p>

          <div class="user-input-group">
            <input type="text" v-model="userId" class="user-input" placeholder="아이디" />
          </div>

          <div class="user-input-group">
            <input type="password" v-model="password" class="user-input" placeholder="패스워드" />
          </div>

          <div class="user-input-group">
            <input type="password" v-model="confirmPassword" class="user-input" placeholder="패스워드 확인" />
          </div>

          <div class="user-input-group">
            <input type="text" v-model="name" class="user-input" placeholder="이름" />
          </div>

          <div class="user-input-group">
            <input type="text" v-model="handphone" class="user-input" placeholder="핸드폰" />
          </div>

          <div class="user-input-group">
            <input type="email" v-model="email" class="user-input" placeholder="이메일" />
          </div>

          <button class="user-signup-button" @click="handleSignup">
            Create Account
          </button>

          <button class="user-google-button" @click="handleGoogleSignup">
            <img src="@/assets/google-icon.svg" alt="Google" class="user-google-icon" />
            Sign up with Google
          </button>

          <div class="user-login-prompt">
            <span>Already have account?</span>
            <router-link to="/login" class="user-login-link">Log in</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

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
    console.error('Please fill in all fields correctly')
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
    console.log('Signup attempt with:', signupData)
    await new Promise(resolve => setTimeout(resolve, 1000))
    router.push('/login')
  } catch (error) {
    console.error('Signup failed:', error)
  } finally {
    isLoading.value = false
  }
}

const handleGoogleSignup = async () => {
  try {
    isLoading.value = true
    console.log('Google signup attempt')
    await new Promise(resolve => setTimeout(resolve, 1000))
    router.push('/login')
  } catch (error) {
    console.error('Google signup failed:', error)
  } finally {
    isLoading.value = false
  }
}

defineOptions({
  name: 'UserSignUp'
})
</script>

<style scoped>
.user-signup-page {
  width: 100%;
  min-height: 100vh;
  background-color: #fff;
}

.user-signup-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 48px);
  width: 100%;
  padding: 2rem;
  box-sizing: border-box;
}

.user-signup-box {
  width: 100%;
  max-width: 400px;
  background-color: #fff;
  border-radius: 8px;
  padding: 2rem;
}

.user-signup-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.user-signup-title {
  font-size: 32px;
  font-weight: 600;
  color: #000;
  margin: 0;
  text-align: center;
}

.user-signup-subtitle {
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

.user-signup-button {
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

.user-signup-button:hover {
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

.user-login-prompt {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-top: 1rem;
}

.user-login-link {
  color: #000;
  text-decoration: underline;
  margin-left: 0.5rem;
  font-weight: 500;
}

.user-login-link:hover {
  color: #666;
}
</style>