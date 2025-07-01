<template>
  <div class="password-container">
    <h2 class="password-title">비밀번호 변경</h2>
    
    <form class="password-form" @submit.prevent="handleSubmit">
      <div class="form-group">
        <label>현재 비밀번호</label>
        <input
            type="password"
            v-model="passwordData.currentPassword"
            placeholder="현재 비밀번호를 입력하세요"
            :disabled="isLoading"
        />
        <span class="error-message" v-if="errors.currentPassword">{{ errors.currentPassword }}</span>
      </div>

      <div class="form-group">
        <label>새 비밀번호</label>
        <input
            type="password"
            v-model="passwordData.newPassword"
            placeholder="새 비밀번호를 입력하세요"
            :disabled="isLoading"
        />
        <span class="error-message" v-if="errors.newPassword">{{ errors.newPassword }}</span>
      </div>

      <div class="form-group">
        <label>새 비밀번호 확인</label>
        <input
            type="password"
            v-model="passwordData.confirmPassword"
            placeholder="새 비밀번호를 다시 입력하세요"
            :disabled="isLoading"
        />
        <span class="error-message" v-if="errors.confirmPassword">{{ errors.confirmPassword }}</span>
      </div>

      <div class="button-group">
        <button type="submit" class="save-button" :disabled="isLoading || !isFormValid">
          {{ isLoading ? '변경 중...' : '변경' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import httpClient from '@/utils/httpRequest'
import { useAlert } from '@/composables/alert'

const router = useRouter()
const { showAlert } = useAlert()
const isLoading = ref(false)

const passwordData = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const errors = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 실시간 비밀번호 유효성 검사
watch(() => passwordData.value.newPassword, (newValue) => {
  if (newValue && newValue.length < 6) {
    errors.value.newPassword = '비밀번호는 6자 이상이어야 합니다.'
  } else if (newValue && passwordData.value.currentPassword === newValue) {
    errors.value.newPassword = '새 비밀번호는 현재 비밀번호와 달라야 합니다.'
  } else {
    errors.value.newPassword = ''
  }
})

watch(() => passwordData.value.confirmPassword, (newValue) => {
  if (newValue && passwordData.value.newPassword !== newValue) {
    errors.value.confirmPassword = '비밀번호가 일치하지 않습니다.'
  } else {
    errors.value.confirmPassword = ''
  }
})

watch(() => passwordData.value.currentPassword, (newValue) => {
  if (newValue && passwordData.value.newPassword && newValue === passwordData.value.newPassword) {
    errors.value.newPassword = '새 비밀번호는 현재 비밀번호와 달라야 합니다.'
  } else if (passwordData.value.newPassword && !errors.value.newPassword.includes('6자 이상')) {
    errors.value.newPassword = ''
  }
})

const isFormValid = computed(() => {
  return (
    passwordData.value.currentPassword &&
    passwordData.value.newPassword &&
    passwordData.value.confirmPassword &&
    passwordData.value.newPassword.length >= 6 &&
    passwordData.value.newPassword === passwordData.value.confirmPassword &&
    passwordData.value.currentPassword !== passwordData.value.newPassword &&
    !errors.value.currentPassword &&
    !errors.value.newPassword &&
    !errors.value.confirmPassword
  )
})

const validateForm = () => {
  errors.value = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  }

  if (!passwordData.value.currentPassword) {
    errors.value.currentPassword = '현재 비밀번호를 입력해주세요.'
    return false
  }

  if (!passwordData.value.newPassword) {
    errors.value.newPassword = '새 비밀번호를 입력해주세요.'
    return false
  }

  if (passwordData.value.newPassword.length < 6) {
    errors.value.newPassword = '비밀번호는 6자 이상이어야 합니다.'
    return false
  }

  if (passwordData.value.currentPassword === passwordData.value.newPassword) {
    errors.value.newPassword = '새 비밀번호는 현재 비밀번호와 달라야 합니다.'
    return false
  }

  if (!passwordData.value.confirmPassword) {
    errors.value.confirmPassword = '새 비밀번호 확인을 입력해주세요.'
    return false
  }

  if (passwordData.value.newPassword !== passwordData.value.confirmPassword) {
    errors.value.confirmPassword = '비밀번호가 일치하지 않습니다.'
    return false
  }

  return true
}

const handleSubmit = async () => {
  if (!validateForm()) return

  try {
    isLoading.value = true
          const response = await httpClient.patch('/v1/api/advertiser/password', {
      currentPassword: passwordData.value.currentPassword,
      newPassword: passwordData.value.newPassword
    })

    if (response.status === 200) {
      showAlert('비밀번호가 성공적으로 변경되었습니다.', 'success')
      // 입력 필드 초기화
      passwordData.value = {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    } else {
      showAlert('비밀번호 변경에 실패했습니다.', 'error')
    }
  } catch (error) {
    console.error('비밀번호 변경 실패:', error)
    if (error.response?.status === 400) {
      showAlert('현재 비밀번호가 올바르지 않습니다.', 'error')
    } else {
      const errorMessage = error.response?.data?.message || '비밀번호 변경 중 오류가 발생했습니다.'
      showAlert(errorMessage, 'error')
    }
  } finally {
    // 항상 로딩 상태를 false로 설정
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
