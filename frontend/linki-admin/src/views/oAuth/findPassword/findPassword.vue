<template>
  <div class="admin-findpw-page">
    <div class="admin-findpw-container">
      <div class="admin-findpw-box">
        <div class="admin-findpw-form">
          <h1 class="admin-findpw-title">비밀번호 찾기</h1>
          <p class="admin-findpw-subtitle">Enter your details below</p>

          <div class="admin-input-group">
            <input type="text" v-model="userId" class="admin-input" placeholder="아이디" />
          </div>

          <div class="admin-input-group">
            <input type="email" v-model="email" class="admin-input" placeholder="이메일" />
          </div>

          <button class="admin-findpw-button" @click="handleFindPassword" :disabled="isLoading">
            {{ isLoading ? 'Searching...' : '비밀번호 찾기' }}
          </button>

          <div class="admin-help-links">
            <router-link to="/admin/findid" class="admin-link">아이디 찾기</router-link>
            <router-link to="/login" class="admin-link">로그인으로 돌아가기</router-link>
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
const email = ref('')
const isLoading = ref(false)

const handleFindPassword = async () => {
  if (!userId.value || !email.value) {
    console.error('Please fill in all fields')
    return
  }

  try {
    isLoading.value = true
    const findPasswordData = {
      userId: userId.value,
      email: email.value
    }
    console.log('Find Password attempt with:', findPasswordData)
    await new Promise(resolve => setTimeout(resolve, 1000))
    alert('비밀번호 찾기 기능 준비중입니다.')
    router.push('/login')
  } catch (error) {
    console.error('Find Password failed:', error)
  } finally {
    isLoading.value = false
  }
}

defineOptions({
  name: 'AdminFindPassword'
})
</script>

<style scoped>
.admin-findpw-page {
  width: 100%;
  min-height: 100vh;
  background-color: #fff;
}

.admin-findpw-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 48px);
  width: 100%;
  padding: 2rem;
  box-sizing: border-box;
}

.admin-findpw-box {
  width: 100%;
  max-width: 400px;
  background-color: #fff;
  border-radius: 8px;
  padding: 2rem;
}

.admin-findpw-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.admin-findpw-title {
  font-size: 32px;
  font-weight: 600;
  color: #000;
  margin: 0;
  text-align: center;
}

.admin-findpw-subtitle {
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

.admin-findpw-button {
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

.admin-findpw-button:hover {
  background-color: #c4a3f7;
}

.admin-help-links {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-top: 1rem;
}

.admin-link {
  color: #666;
  text-decoration: none;
  font-size: 14px;
}

.admin-link:hover {
  color: #000;
}
</style> 