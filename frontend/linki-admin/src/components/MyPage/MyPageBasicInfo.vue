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
          v-model="formattedPhone"
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
        <label>주소</label>
        <input 
          type="text" 
          v-model="profileData.address"
          placeholder="주소를 입력하세요"
          :disabled="isLoading"
        />
        <span class="error-message" v-if="errors.address">{{ errors.address }}</span>
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
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import httpClient from '@/utils/httpRequest'
import { useAccountStore } from '@/stores/account'

const router = useRouter()
const accountStore = useAccountStore()
const isLoading = ref(false)

// 원본 연락처 데이터 (숫자만)
const rawPhone = ref('')

const profileData = ref({
  name: '',
  phone: '',
  email: '',
  address: '',
  joinDate: null
})

// 포맷된 연락처 (표시용)
const formattedPhone = computed({
  get: () => {
    return formatPhoneNumber(rawPhone.value)
  },
  set: (value) => {
    // 숫자만 추출하여 저장
    rawPhone.value = value.replace(/[^\d]/g, '')
  }
})

const errors = ref({
  name: '',
  phone: '',
  email: '',
  address: ''
})

// 연락처 포맷팅 함수
const formatPhoneNumber = (value) => {
  if (!value) return ''
  
  // 숫자만 추출
  const numbers = value.replace(/[^\d]/g, '')
  
  // 길이에 따라 포맷팅
  if (numbers.length <= 3) {
    return numbers
  } else if (numbers.length <= 7) {
    return `${numbers.slice(0, 3)}-${numbers.slice(3)}`
  } else {
    return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(7, 11)}`
  }
}

// rawPhone이 변경될 때 profileData.phone도 업데이트
watch(rawPhone, (newValue) => {
  profileData.value.phone = formatPhoneNumber(newValue)
})

// 로그인된 관리자 정보 가져오기
const loadUserInfo = async () => {
  try {
    isLoading.value = true
    const response = await httpClient.get('v1/api/admin/mypage')
    profileData.value = {
      name: response.data.adminName || '',
      phone: response.data.adminPhone || '',
      email: response.data.adminEmail || '',
      address: response.data.adminAddress || '',
      joinDate: response.data.adminEnterDay ? new Date(response.data.adminEnterDay) : null
    }
    // rawPhone 설정 (숫자만 추출)
    rawPhone.value = (response.data.adminPhone || '').replace(/[^\d]/g, '')
  } catch (error) {
    console.error('관리자 정보 로딩 실패:', error)
    alert('관리자 정보를 불러오는데 실패했습니다.')
  } finally {
    isLoading.value = false
  }
}

const isFormValid = computed(() => {
  return (
    profileData.value.name &&
    profileData.value.email &&
    profileData.value.address &&
    !errors.value.name &&
    !errors.value.phone &&
    !errors.value.email &&
    !errors.value.address
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
    email: '',
    address: ''
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

  if (!profileData.value.address || profileData.value.address.length < 5) {
    errors.value.address = '주소를 5자 이상 입력해주세요.'
    return false
  }

  return true
}

const handleSubmit = async () => {
  if (!validateForm()) return

  try {
    isLoading.value = true
    const response = await httpClient.patch('v1/api/admin/mypage', {
      adminName: profileData.value.name,
      adminPhone: profileData.value.phone,
      adminEmail: profileData.value.email,
      adminAddress: profileData.value.address
    })

    if (response.status === 200) {
      alert('관리자 정보가 성공적으로 업데이트되었습니다.')
      // Store의 관리자 정보도 업데이트
      const currentUser = accountStore.getUser
      if (currentUser) {
        accountStore.setUser({
          ...currentUser,
          adminName: profileData.value.name,
          adminEmail: profileData.value.email,
          adminPhone: profileData.value.phone,
          adminAddress: profileData.value.address
        })
      }
    } else {
      alert('관리자 정보 업데이트에 실패했습니다.')
    }
  } catch (error) {
    console.error('관리자 정보 업데이트 실패:', error)
    const errorMessage = error.response?.data?.message || '관리자 정보 업데이트 중 오류가 발생했습니다.'
    alert(errorMessage)
  } finally {
    isLoading.value = false
  }
}

// 컴포넌트 마운트 시 관리자 정보 로드
onMounted(() => {
  loadUserInfo()
})
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