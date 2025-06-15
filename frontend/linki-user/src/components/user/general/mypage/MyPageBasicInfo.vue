<template>
  <div class="basic-info-container">
    <h2 class="basic-info-title">기본 정보</h2>
    
    <form class="basic-info-form" @submit.prevent="handleSubmit">
      <div class="form-group">
        <label>이름</label>
        <input 
          type="text" 
          v-model="profileData.name"
          placeholder="이름을 입력하세요"
          :disabled="isLoading"
        />
        <span class="error-message" v-if="errors.name">{{ errors.name }}</span>
      </div>

      <div class="form-group">
        <label>연락처</label>
        <input 
          type="tel" 
          v-model="profileData.phone"
          placeholder="000-0000-0000"
          pattern="[0-9]{3}-[0-9]{4}-[0-9]{4}"
          title="000-0000-0000 형식으로 입력해주세요"
          :disabled="isLoading"
        />
        <span class="error-message" v-if="errors.phone">{{ errors.phone }}</span>
      </div>

      <div class="form-group">
        <label>이메일</label>
        <input 
          type="email" 
          v-model="profileData.email"
          placeholder="이메일을 입력하세요"
          :disabled="isLoading"
        />
        <span class="error-message" v-if="errors.email">{{ errors.email }}</span>
      </div>

      <div class="form-group">
        <label>가입일</label>
        <div class="join-date">{{ formatDate(profileData.joinDate) }}</div>
      </div>

      <div class="button-group">
        <button type="submit" class="save-button" :disabled="isLoading || !isFormValid">
          {{ isLoading ? '저장 중...' : '저장' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useAlert } from '@/composables/alert'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const { showAlert } = useAlert()
const userStore = useUserStore()
const isLoading = ref(false)

const profileData = ref({
  name: '',
  phone: '',
  email: '',
  joinDate: null
})

const errors = ref({
  name: '',
  phone: '',
  email: ''
})

const isFormValid = computed(() => {
  return (
    profileData.value.name &&
    profileData.value.email &&
    !errors.value.name &&
    !errors.value.phone &&
    !errors.value.email
  )
})

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const validateForm = () => {
  errors.value = {
    name: '',
    phone: '',
    email: ''
  }

  if (!profileData.value.name) {
    errors.value.name = '이름을 입력해주세요.'
    return false
  }

  if (profileData.value.phone && !/^\d{3}-\d{4}-\d{4}$/.test(profileData.value.phone)) {
    errors.value.phone = '연락처를 올바른 형식으로 입력해주세요. (000-0000-0000)'
    return false
  }

  if (!profileData.value.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(profileData.value.email)) {
    errors.value.email = '유효한 이메일을 입력해주세요.'
    return false
  }

  return true
}

const fetchProfile = async () => {
  try {
    isLoading.value = true
    const response = await axios.get('/api/user/profile')
    profileData.value = {
      ...response.data,
      joinDate: new Date(response.data.joinDate)
    }
  } catch (error) {
    console.error('프로필 정보 로딩 실패:', error)
    showAlert('프로필 정보를 불러오는데 실패했습니다.', 'error')
  } finally {
    isLoading.value = false
  }
}

const handleSubmit = async () => {
  if (!validateForm()) return

  try {
    isLoading.value = true
    const response = await axios.patch('/api/user/profile', {
      name: profileData.value.name,
      phone: profileData.value.phone,
      email: profileData.value.email
    })

    if (response.data.success) {
      showAlert('프로필이 성공적으로 업데이트되었습니다.', 'success')
      userStore.setUserInfo({
        ...userStore.getUserInfo,
        name: profileData.value.name,
        email: profileData.value.email
      })
    } else {
      showAlert(response.data.message || '프로필 업데이트에 실패했습니다.', 'error')
    }
  } catch (error) {
    console.error('프로필 업데이트 실패:', error)
    const errorMessage = error.response?.data?.message || '프로필 업데이트 중 오류가 발생했습니다.'
    showAlert(errorMessage, 'error')
  } finally {
    isLoading.value = false
  }
}

fetchProfile()
</script>

<style scoped>
.basic-info-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
}

.basic-info-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 2rem;
  color: #333;
  text-align: center;
}

.basic-info-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
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

.join-date {
  padding: 0.75rem;
  background-color: #f5f5f5;
  border-radius: 4px;
  color: #666;
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
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  background-color: #7B21E8;
  color: #ffffff;
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