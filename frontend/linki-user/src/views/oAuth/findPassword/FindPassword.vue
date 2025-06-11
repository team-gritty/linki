<template>
  <div class="container">
    <div class="find-password-box">
      <h1>비밀번호 찾기</h1>
      <div class="input-group">
        <input type="text" v-model="name" placeholder="이름" :disabled="isLoading" />
      </div>
      <div class="input-group">
        <input type="text" v-model="userId" placeholder="아이디" :disabled="isLoading" />
      </div>
      <div class="input-group">
        <input type="email" v-model="email" placeholder="이메일" :disabled="isLoading" />
      </div>
      <button @click="findPassword" :disabled="isLoading">
        {{ isLoading ? '처리 중...' : '비밀번호 찾기' }}
      </button>
      <div class="links">
        <router-link to="/findid">아이디 찾기</router-link> |
        <router-link to="/login">로그인으로 돌아가기</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const name = ref('')
const userId = ref('')
const email = ref('')
const isLoading = ref(false)

const findPassword = async () => {
  if (!name.value || !userId.value || !email.value) {
    alert('모든 정보를 입력해주세요.')
    return
  }

  try {
    isLoading.value = true
    const response = await axios.post('/api/user/find-password', {
      name: name.value,
      userId: userId.value,
      email: email.value
    })

    if (response.data.success) {
      alert('입력하신 이메일로 임시 비밀번호가 발송되었습니다.')
      router.push('/login')
    } else {
      alert(response.data.message || '일치하는 회원 정보를 찾을 수 없습니다.')
    }
  } catch (error) {
    console.error('Find password failed:', error)
    if (error.response) {
      // 서버에서 응답이 왔지만 에러인 경우
      alert(error.response.data.message || '비밀번호 찾기 중 오류가 발생했습니다.')
    } else if (error.request) {
      // 요청은 보냈지만 응답을 받지 못한 경우
      alert('서버와 통신할 수 없습니다. 잠시 후 다시 시도해주세요.')
    } else {
      // 요청 자체를 보내지 못한 경우
      alert('요청을 보내지 못했습니다. 잠시 후 다시 시도해주세요.')
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: white;
}

.find-password-box {
  width: 100%;
  max-width: 400px;
  padding: 2rem;
  background: white;
  border-radius: 8px;
}

h1 {
  text-align: center;
  margin-bottom: 2rem;
}

.input-group {
  margin-bottom: 1rem;
}

input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

button {
  width: 100%;
  padding: 0.75rem;
  background-color: #d6bcf7;
  border: none;
  border-radius: 4px;
  color: black;
  font-size: 1rem;
  cursor: pointer;
  margin-top: 1rem;
  transition: background-color 0.2s;
}

button:disabled {
  background-color: #e9d6ff;
  cursor: not-allowed;
}

.links {
  text-align: center;
  margin-top: 1rem;
}

.links a {
  color: #666;
  text-decoration: none;
  margin: 0 0.5rem;
}

.links a:hover {
  color: #000;
}
</style> 