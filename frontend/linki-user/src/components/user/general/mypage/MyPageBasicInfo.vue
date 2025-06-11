<template>
  <div class="basic-info-container">
    <h2 class="section-title">기본 정보</h2>
    <form @submit.prevent="handleSubmit" class="info-form">
      <div class="form-group">
        <label>이름</label>
        <input 
          type="text" 
          v-model="profileData.name"
          placeholder="이름을 입력하세요"
          required
          :disabled="isLoading"

      </div>

      <div class="form-group">
        <label>연락처</label>
        <input 
          type="tel" 
          v-model="profileData.phone"
          placeholder="연락처를 입력하세요"
          pattern="[0-9]{3}-[0-9]{4}-[0-9]{4}"
          title="000-0000-0000 형식으로 입력해주세요"
          :disabled="isLoading"

      </div>

      <div class="form-group">
        <label>이메일</label>
        <input 
          type="email" 
          v-model="profileData.email"
          placeholder="이메일을 입력하세요"
          required
          :disabled="isLoading"

      </div>

      <div class="form-group">
        <label>가입일</label>
        <div class="readonly-field">{{ formatDate(profileData.joinDate) }}</div>
      </div>

      <div class="button-group">

        <button type="button" class="cancel-button" @click="handleCancel" :disabled="isLoading">취소</button>
        <button type="submit" class="submit-button" :disabled="isLoading">
          {{ isLoading ? '저장 중...' : '저장' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useAlert } from '@/composables/alert'
import { useUserStore } from '@/stores/user'


export default {
  name: 'MyPageBasicInfo',
  setup() {
    const isLoading = ref(false)
    const { showAlert } = useAlert()
    const userStore = useUserStore()

    const profileData = ref({
      name: '',
      phone: '',
      email: '',
      joinDate: null
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
      if (!profileData.value.name) {
        showAlert('이름을 입력해주세요.', 'error')
        return false
      }
      if (profileData.value.phone && !/^\d{3}-\d{4}-\d{4}$/.test(profileData.value.phone)) {
        showAlert('연락처를 올바른 형식으로 입력해주세요. (000-0000-0000)', 'error')
        return false
      }
      if (!profileData.value.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(profileData.value.email)) {
        showAlert('유효한 이메일을 입력해주세요.', 'error')
        return false
      }
      return true
    }

    const fetchProfile = async () => {
      try {
        isLoading.value = true
        const response = await axios.get('/api/user/profile')

        profileData.value = {
          name: response.data.name,
          phone: response.data.phone,
          email: response.data.email,
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

      isLoading.value = true
      try {
        const response = await axios.patch('/api/user/profile', {

          name: profileData.value.name,
          phone: profileData.value.phone,
          email: profileData.value.email
        })

        
        if (response.data.success) {
          showAlert('프로필이 성공적으로 업데이트되었습니다.', 'success')
          // Update user store if needed
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

    const handleCancel = () => {
      fetchProfile()
    }

    onMounted(() => {
      fetchProfile()
    })

    return {
      profileData,
      isLoading,
      formatDate,
      handleSubmit,
      handleCancel
    }
  }
}
</script>

<style scoped>
.basic-info-container {
  padding: 40px;
  max-width: 600px;
}

.section-title {
  font-size: 24px;
  font-weight: 500;
  color: #333;
  margin-bottom: 32px;
}

.info-form {
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

.readonly-field {
  height: 40px;
  padding: 0 16px;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  display: flex;
  align-items: center;
  color: #666;
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
</style> 