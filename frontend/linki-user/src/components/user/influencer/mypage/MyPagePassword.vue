<template>
  <div class="password-change-container">
    <h2 class="section-title">비밀번호 변경</h2>
    <form @submit.prevent="handleSubmit" class="password-form">
      <div class="form-group">
        <label>현재 비밀번호</label>
        <input 
          type="password" 
          v-model="passwordData.currentPassword"
          placeholder="현재 비밀번호를 입력하세요"
          required
        />
      </div>
      <div class="form-group">
        <label>새 비밀번호</label>
        <input 
          type="password" 
          v-model="passwordData.newPassword"
          placeholder="새 비밀번호를 입력하세요"
          required
        />
        <div v-if="passwordError" class="error-message">{{ passwordError }}</div>
      </div>
      <div class="form-group">
        <label>새 비밀번호 확인</label>
        <input 
          type="password" 
          v-model="passwordData.confirmPassword"
          placeholder="새 비밀번호를 다시 입력하세요"
          required
        />
        <div v-if="confirmError" class="error-message">{{ confirmError }}</div>
      </div>
      <div class="button-group">
        <button type="submit" class="submit-button" :disabled="isLoading">
          {{ isLoading ? '변경 중...' : '비밀번호 변경' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script>
import { ref } from 'vue'
import axios from 'axios'

export default {
  name: 'MyPagePassword',
  setup() {
    const isLoading = ref(false)
    const passwordData = ref({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    const passwordError = ref('')
    const confirmError = ref('')

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
        passwordData.value = {
          currentPassword: '',
          newPassword: '',
          confirmPassword: ''
        }
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

    return {
      passwordData,
      passwordError,
      confirmError,
      isLoading,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.password-change-container {
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 24px;
  font-weight: 500;
  color: #333;
  margin-bottom: 30px;
}

.password-form {
  max-width: 400px;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.form-group input {
  width: 100%;
  height: 40px;
  padding: 0 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #d6bcf7;
}

.error-message {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 4px;
}

.button-group {
  margin-top: 32px;
}

.submit-button {
  width: 100%;
  height: 44px;
  background-color: #d6bcf7;
  border: none;
  border-radius: 4px;
  color: white;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.submit-button:hover {
  background-color: #c4a1f7;
}

.submit-button:disabled {
  background-color: #e9e9e9;
  cursor: not-allowed;
}
</style> 