<template>
  <div class="container">
    <div class="find-password-box">
      <h1>비밀번호 찾기</h1>
      <div class="input-group">
        <input type="text" v-model="name" placeholder="이름" />
      </div>
      <div class="input-group">
        <input type="text" v-model="userId" placeholder="아이디" />
      </div>
      <div class="input-group">
        <input type="email" v-model="email" placeholder="이메일" />
      </div>
      <button @click="findPassword">비밀번호 찾기</button>
      <div class="links">
        <router-link to="/login">로그인으로 돌아가기</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const name = ref('')
const userId = ref('')
const email = ref('')

const findPassword = () => {
  alert('비밀번호 찾기 기능 준비중입니다.')
  router.push('/login')
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
}

.links {
  text-align: center;
  margin-top: 1rem;
}

.links a {
  color: #666;
  text-decoration: none;
}
</style> 