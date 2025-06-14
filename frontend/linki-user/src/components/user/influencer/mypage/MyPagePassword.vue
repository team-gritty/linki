<template>
  <div class="password-container">
    <h2 class="password-title">비밀번호 변경</h2>
    <div class="password-form" >
      <div class="form-group">
        <label>현재 비밀번호</label>
        <input 
          type="password" 
          v-model="passwordData.currentPassword"
          :disabled="isLoading"
        />
      </div>
      <div class="form-group">
        <label>새 비밀번호</label>
        <input 
          type="password" 
          v-model="passwordData.newPassword"
          :disabled="isLoading"
        />
        <span class="error-message" v-if="passwordError">{{ passwordError }}</span>
      </div>
      <div class="form-group">
        <label>새 비밀번호 확인</label>
        <input 
          type="password" 
          v-model="passwordData.confirmPassword"
          :disabled="isLoading"
        />
        <span class="error-message" v-if="confirmError">{{ confirmError }}</span>

        <div class="button-group">
          <div  class="save-button" @click="handleSubmit" :disabled="isLoading">
            {{ isLoading ? '변경 중...' : '변경' }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const isLoading = ref(false)
const passwordError = ref('')
const confirmError = ref('')

const passwordData = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePassword = () => {
  passwordError.value = ''
  confirmError.value = ''

  if (passwordData.value.newPassword.length < 8) {
    passwordError.value = '비밀번호는 8자 이상이어야 합니다.'
    return false
  }

  if (passwordData.value.newPassword !== passwordData.value.confirmPassword) {
    confirmError.value = '비밀번호가 일치하지 않습니다.'
    return false
  }

  return true
}

const handleSubmit = async () => {
  if (!validatePassword()) return

  isLoading.value = true
  try {
    await axios.post('/api/influencer/change-password', {
      currentPassword: passwordData.value.currentPassword,
      newPassword: passwordData.value.newPassword
    })
    
    alert('비밀번호가 성공적으로 변경되었습니다.')
  } catch (error) {
    if (error.response?.status === 401) {
      alert('현재 비밀번호가 올바르지 않습니다.')
    } else {
      alert('비밀번호 변경 중 오류가 발생했습니다.')
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.password-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.password-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 2rem;
  color: #333;
}

.password-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  width: 100%;
  background-color: #fffcfc;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-size: 0.9rem;
  font-weight: 500;
  color: #666;
}

.form-group input {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.form-group input:focus {
  outline: none;
  border-color: #d6bcf7;
}

.form-group input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.error-message {
  font-size: 0.8rem;
  color: #ff4444;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  margin-top: 1rem;
}

.save-button {
  min-width: 80px;
  padding: 0.5rem 2rem;
  border: none;
  border-radius: 4px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  background-color: #8C30F5;
  color: #fff;
}

.save-button:hover {
  background-color: #6B21E8;
}

.save-button:disabled {
  background-color: #eee;
  color: #999;
  cursor: not-allowed;
}
</style> 