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
        />
      </div>

      <div class="form-group">
        <label>연락처</label>
        <input 
          type="tel" 
          v-model="profileData.phone"
          placeholder="연락처를 입력하세요"
          pattern="[0-9]{3}-[0-9]{4}-[0-9]{4}"
          title="000-0000-0000 형식으로 입력해주세요"
        />
      </div>

      <div class="form-group">
        <label>이메일</label>
        <input 
          type="email" 
          v-model="profileData.email"
          placeholder="이메일을 입력하세요"
          required
        />
      </div>

      <div class="form-group">
        <label>가입일</label>
        <div class="readonly-field">{{ formatDate(profileData.joinDate) }}</div>
      </div>

      <div class="button-group">
        <button type="button" class="cancel-button" @click="handleCancel">취소</button>
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

export default {
  name: 'MyPageBasicInfo',
  setup() {
    const isLoading = ref(false)
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

    const fetchProfile = async () => {
      try {
        const response = await axios.get('http://localhost:3000/users/1')
        profileData.value = {
          name: response.data.name,
          phone: response.data.phone,
          email: response.data.email,
          joinDate: new Date(response.data.joinDate)
        }
      } catch (error) {
        console.error('프로필 정보 로딩 실패:', error)
        alert('프로필 정보를 불러오는데 실패했습니다.')
      }
    }

    const handleSubmit = async () => {
      isLoading.value = true
      try {
        await axios.patch('http://localhost:3000/users/1', {
          name: profileData.value.name,
          phone: profileData.value.phone,
          email: profileData.value.email
        })
        alert('프로필이 성공적으로 업데이트되었습니다.')
      } catch (error) {
        console.error('프로필 업데이트 실패:', error)
        alert('프로필 업데이트에 실패했습니다.')
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