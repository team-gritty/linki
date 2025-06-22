<template>
  <div class="admin-findid-page">
    <div class="admin-findid-container">
      <div class="admin-findid-box">
        <div class="admin-findid-form">
          <h1 class="admin-findid-title">아이디 찾기</h1>
          <p class="admin-findid-subtitle">Enter your details below</p>

          <div class="admin-input-group">
            <input type="text" v-model="name" class="admin-input" placeholder="이름" />
          </div>

          <div class="admin-input-group">
            <input type="email" v-model="email" class="admin-input" placeholder="이메일" />
          </div>

          <button class="admin-findid-button" @click="handleFindId" :disabled="isLoading">
            {{ isLoading ? 'Searching...' : '아이디 찾기' }}
          </button>

          <div class="admin-help-links">
            <router-link to="/admin/findpassword" class="admin-link">비밀번호 찾기</router-link>
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
import axios from 'axios'

const router = useRouter()
const name = ref('')
const email = ref('')
const isLoading = ref(false)

const handleFindId = async () => {
  if (!name.value || !email.value) {
    alert('이름과 이메일을 모두 입력해주세요.')
    return
  }

  try {
    isLoading.value = true
    const findIdData = {
      name: name.value,
      email: email.value
    }

    const response = await axios.post('/api/admin/find-id', findIdData)
    
    if (response.data.success) {
      alert(`회원님의 아이디는 ${response.data.userId} 입니다.`)
      router.push('/login')
    } else {
      alert(response.data.message || '일치하는 회원 정보를 찾을 수 없습니다.')
    }
  } catch (error) {
    console.error('Find ID failed:', error)
    if (error.response) {
      // 서버에서 응답이 왔지만 에러인 경우
      alert(error.response.data.message || '아이디 찾기 중 오류가 발생했습니다.')
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

defineOptions({
  name: 'AdminFindId'
})
</script>

<style scoped>
.admin-findid-page {
  width: 100%;
  min-height: 100vh;
  background-color: #fff;
}

.admin-findid-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 48px);
  width: 100%;
  padding: 2rem;
  box-sizing: border-box;
}

.admin-findid-box {
  width: 100%;
  max-width: 400px;
  background-color: #fff;
  border-radius: 8px;
  padding: 2rem;
}

.admin-findid-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.admin-findid-title {
  font-size: 32px;
  font-weight: 600;
  color: #000;
  margin: 0;
  text-align: center;
}

.admin-findid-subtitle {
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

.admin-findid-button {
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

.admin-findid-button:hover {
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