<template>
  <div :class="$style.myProfileParent">
    <div :class="$style.myProfile">My Profile</div>
    <form @submit.prevent="handleSubmit" :class="$style.form">
      <div :class="$style.profileContainer">
        <div :class="$style.profileField">
          <div :class="$style.label">이름</div>
          <div :class="$style.inputWrapper">
            <input 
              type="text" 
              v-model="profileData.name"
              :class="$style.input"
              placeholder="이름을 입력하세요"
              required
            />
          </div>
        </div>
        <div :class="$style.profileField">
          <div :class="$style.label">연락처</div>
          <div :class="$style.inputWrapper">
            <input 
              type="tel" 
              v-model="profileData.phone"
              :class="$style.input"
              placeholder="연락처를 입력하세요"
              pattern="[0-9]{3}-[0-9]{4}-[0-9]{4}"
              title="000-0000-0000 형식으로 입력해주세요"
            />
          </div>
        </div>
        <div :class="$style.profileField">
          <div :class="$style.label">Email</div>
          <div :class="$style.inputWrapper">
            <input 
              type="email" 
              v-model="profileData.email"
              :class="$style.input"
              placeholder="이메일을 입력하세요"
              required
            />
          </div>
        </div>
        <div :class="$style.profileField">
          <div :class="$style.label">가입일</div>
          <div :class="$style.inputWrapper">
            <div :class="$style.readOnlyInput">{{ formatDate(profileData.joinDate) }}</div>
          </div>
        </div>
      </div>
      <div :class="$style.buttonContainer">
        <button type="button" :class="$style.cancel" @click="handleCancel">Cancel</button>
        <button type="submit" :class="$style.button" :disabled="isLoading">
          <div :class="$style.viewAllProducts">
            {{ isLoading ? '저장 중...' : 'Save Changes' }}
          </div>
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
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
    const response = await axios.get('http://localhost:3000/admins/1')
    const adminData = response.data
    
    profileData.value = {
      name: adminData.name,
      phone: adminData.phone,
      email: adminData.email,
      joinDate: new Date(adminData.joinDate)
    }
  } catch (error) {
    console.error('프로필 정보 로딩 실패:', error)
    alert('프로필 정보를 불러오는데 실패했습니다.')
  }
}

const handleSubmit = async () => {
  isLoading.value = true
  try {
    await axios.patch('http://localhost:3000/admins/1', {
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
  router.push('/admin/mypage')
}

onMounted(() => {
  fetchProfile()
})
</script>

<style module>
.myProfile {
  margin: 40px 0 30px 80px;
  font-size: 20px;
  line-height: 28px;
  font-weight: 500;
}

.form {
  width: 100%;
  height: 100%;
  position: relative;
  display: flex;
  flex-direction: column;
}

.profileContainer {
  display: flex;
  flex-direction: column;
  gap: 24px;
  width: 400px;
  margin-left: 80px;
}

.profileField {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.label {
  font-size: 16px;
  line-height: 24px;
  color: rgba(0, 0, 0, 0.7);
}

.inputWrapper {
  width: 100%;
  height: 50px;
}

.input {
  width: 100%;
  height: 100%;
  padding: 0 16px;
  border: none;
  background-color: rgba(245, 245, 245, 0.7);
  border-radius: 4px;
  font-size: 16px;
  color: #000;
}

.input:focus {
  outline: none;
  border: 1px solid #d6bcf7;
}

.readOnlyInput {
  width: 100%;
  height: 100%;
  padding: 13px 16px;
  background-color: rgba(245, 245, 245, 0.7);
  border-radius: 4px;
  font-size: 16px;
  color: rgba(0, 0, 0, 0.5);
}

.buttonContainer {
  display: flex;
  justify-content: flex-end;
  gap: 32px;
  padding: 0 80px;
  margin-top: 40px;
}

.cancel {
  background: none;
  border: none;
  cursor: pointer;
  color: rgba(0, 0, 0, 0.7);
  font-size: 16px;
  line-height: 24px;
}

.cancel:hover {
  color: rgba(0, 0, 0, 0.9);
}

.button {
  border: none;
  border-radius: 4px;
  background-color: #d6bcf7;
  padding: 16px 48px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.button:hover {
  background-color: #c4a1f7;
}

.button:disabled {
  background-color: #e9e9e9;
  cursor: not-allowed;
}

.viewAllProducts {
  color: #fff;
  font-size: 16px;
  line-height: 24px;
  font-weight: 500;
}

.myProfileParent {
  width: 100%;
  position: relative;
  background-color: #fff;
  min-height: 300px;
  padding-bottom: 40px;
  box-shadow: 0px 1px 13px rgba(0, 0, 0, 0.05);
  border-radius: 4px;
  font-family: Poppins;
}
</style>