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

          :disabled="isLoading"

        />
      </div>

      <div class="form-group">
        <label>새 비밀번호</label>
        <input 
          type="password" 
          v-model="passwordData.newPassword"
          placeholder="새 비밀번호를 입력하세요"
          required

          :disabled="isLoading"
        />
        <div v-if="passwordError" class="error-message">{{ passwordError }}</div>
        <div class="password-rules">
          비밀번호는 8자 이상이며, 영문/숫자/특수문자를 포함해야 합니다.
        </div>

      </div>

      <div class="form-group">
        <label>새 비밀번호 확인</label>
        <input 
          type="password" 
          v-model="passwordData.confirmPassword"
          placeholder="새 비밀번호를 다시 입력하세요"
          required

          :disabled="isLoading"

        />
        <div v-if="confirmError" class="error-message">{{ confirmError }}</div>
      </div>

      <div class="button-group">

        <button type="button" class="cancel-button" @click="handleCancel" :disabled="isLoading">취소</button>
        <button type="submit" class="submit-button" :disabled="isLoading || !isFormValid">

          {{ isLoading ? '변경 중...' : '비밀번호 변경' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script>

import { ref, computed } from 'vue'
import axios from 'axios'
import { useAlert } from '@/composables/alert'


export default {
  name: 'MyPagePassword',
  setup() {
    const isLoading = ref(false)

    const { showAlert } = useAlert()
    

    const passwordData = ref({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    const passwordError = ref('')
    const confirmError = ref('')

    const isFormValid = computed(() => {
      return (
        passwordData.value.currentPassword &&
        passwordData.value.newPassword &&
        passwordData.value.confirmPassword &&
        !passwordError.value &&
        !confirmError.value
      )
    })


    const validatePassword = () => {
      passwordError.value = ''
      confirmError.value = ''


      if (!passwordData.value.currentPassword) {
        showAlert('현재 비밀번호를 입력해주세요.', 'error')
        return false
      }

      if (passwordData.value.newPassword.length < 8) {
        passwordError.value = '비밀번호는 8자 이상이어야 합니다.'
        showAlert('비밀번호는 8자 이상이어야 합니다.', 'error')
        return false
      }

      const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/
      if (!passwordRegex.test(passwordData.value.newPassword)) {
        passwordError.value = '비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다.'
        showAlert('비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다.', 'error')
        return false
      }

      if (passwordData.value.newPassword === passwordData.value.currentPassword) {
        passwordError.value = '새 비밀번호는 현재 비밀번호와 달라야 합니다.'
        showAlert('새 비밀번호는 현재 비밀번호와 달라야 합니다.', 'error')

        return false
      }

      if (passwordData.value.newPassword !== passwordData.value.confirmPassword) {
        confirmError.value = '비밀번호가 일치하지 않습니다.'

        showAlert('새 비밀번호가 일치하지 않습니다.', 'error')

        return false
      }

      return true
    }

    const handleSubmit = async () => {
      if (!validatePassword()) return

      isLoading.value = true
      try {

        const response = await axios.patch('/api/user/password', {

          currentPassword: passwordData.value.currentPassword,
          newPassword: passwordData.value.newPassword
        })
        

        if (response.data.success) {
          showAlert('비밀번호가 성공적으로 변경되었습니다.', 'success')
          passwordData.value = {
            currentPassword: '',
            newPassword: '',
            confirmPassword: ''
          }
          passwordError.value = ''
          confirmError.value = ''
        } else {
          showAlert(response.data.message || '비밀번호 변경에 실패했습니다.', 'error')
        }
      } catch (error) {
        console.error('비밀번호 변경 실패:', error)
        if (error.response?.status === 401) {
          showAlert('현재 비밀번호가 올바르지 않습니다.', 'error')
        } else {
          const errorMessage = error.response?.data?.message || '비밀번호 변경 중 오류가 발생했습니다.'
          showAlert(errorMessage, 'error')

        }
      } finally {
        isLoading.value = false
      }
    }

    const handleCancel = () => {
      passwordData.value = {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      passwordError.value = ''
      confirmError.value = ''
    }

    return {
      passwordData,
      passwordError,
      confirmError,
      isLoading,

      isFormValid,

      handleSubmit,
      handleCancel
    }
  }
}
</script>

<style scoped>
.password-change-container {
  padding: 40px;
  max-width: 600px;
}

.section-title {
  font-size: 24px;
  font-weight: 500;
  color: #333;
  margin-bottom: 32px;
}

.password-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  color: #666;
}

.form-group input {
  height: 40px;
  padding: 0 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.2s;
}

.form-group input:focus {
  outline: none;
  border-color: #d6bcf7;
}

.error-message {
  font-size: 12px;
  color: #ff4d4f;
  margin-top: 4px;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 16px;
}

.cancel-button {
  padding: 0 24px;
  height: 40px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-button:hover {
  background: #f5f5f5;
}

.submit-button {
  padding: 0 24px;
  height: 40px;
  border: none;
  border-radius: 4px;
  background: #d6bcf7;
  color: white;
  cursor: pointer;
  transition: all 0.2s;
}

.submit-button:hover {
  background: #c4a1f7;
}

.submit-button:disabled {
  background: #eee;
  cursor: not-allowed;
}


.password-rules {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

</style> 