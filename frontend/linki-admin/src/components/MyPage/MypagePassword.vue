<template>
  <div :class="$style.parent">
    <div :class="$style.div">비밀번호 변경</div>
    <form @submit.prevent="handleSubmit" :class="$style.form">
      <div :class="$style.frameWrapper">
        <div :class="$style.group">
          <div :class="$style.cancel">현재 비밀번호</div>
          <div :class="$style.placeboxInfo">
            <input 
              type="password" 
              v-model="currentPassword"
              :class="$style.input"
              placeholder="현재 비밀번호를 입력하세요"
              required
            />
          </div>
        </div>
      </div>
      <div :class="$style.frameContainer">
        <div :class="$style.group">
          <div :class="$style.cancel">변경 비밀번호</div>
          <div :class="$style.placeboxInfo">
            <input 
              type="password" 
              v-model="newPassword"
              :class="$style.input"
              placeholder="새 비밀번호를 입력하세요"
              required
            />
          </div>
          <div v-if="passwordError" :class="$style.error">{{ passwordError }}</div>
        </div>
      </div>
      <div :class="$style.frameDiv">
        <div :class="$style.group">
          <div :class="$style.cancel">변경 비밀번호 확인</div>
          <div :class="$style.placeboxInfo">
            <input 
              type="password" 
              v-model="confirmPassword"
              :class="$style.input"
              placeholder="새 비밀번호를 다시 입력하세요"
              required
            />
          </div>
          <div v-if="confirmError" :class="$style.error">{{ confirmError }}</div>
        </div>
      </div>
      <div :class="$style.cancelParent">
        <button type="button" :class="$style.cancel" @click="handleCancel">Cancel</button>
        <button type="submit" :class="$style.button" :disabled="isLoading">
          <div :class="$style.viewAllProducts">
            {{ isLoading ? '변경 중...' : 'Save Changes' }}
          </div>
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const passwordError = ref('')
const confirmError = ref('')
const isLoading = ref(false)

const validatePassword = () => {
  passwordError.value = ''
  confirmError.value = ''

  if (newPassword.value.length < 8) {
    passwordError.value = '비밀번호는 8자 이상이어야 합니다.'
    return false
  }

  if (newPassword.value !== confirmPassword.value) {
    confirmError.value = '비밀번호가 일치하지 않습니다.'
    return false
  }

  return true
}

const handleSubmit = async () => {
  if (!validatePassword()) return

  isLoading.value = true
  try {
    // API 엔드포인트는 실제 백엔드 URL로 변경해야 합니다
    await axios.post('/api/admin/change-password', {
      currentPassword: currentPassword.value,
      newPassword: newPassword.value
    })
    
    alert('비밀번호가 성공적으로 변경되었습니다.')
    router.push('/admin/mypage')
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

const handleCancel = () => {
  router.push('/admin/mypage')
}
</script>

<style module>
.div {
  position: absolute;
  top: 40px;
  left: 80px;
  font-size: 20px;
  line-height: 28px;
  font-weight: 500;
}

.form {
  width: 100%;
  height: 100%;
  position: relative;
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

.error {
  color: #ff4d4f;
  font-size: 14px;
  margin-top: 4px;
}

.placeboxInfo {
  width: 330px;
  position: relative;
  height: 50px;
}

.group {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  gap: 8px;
}

.frameWrapper {
  position: absolute;
  top: 84px;
  left: 80px;
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  justify-content: flex-start;
}

.frameContainer {
  position: absolute;
  top: 190px;
  left: 80px;
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  justify-content: flex-start;
}

.frameDiv {
  position: absolute;
  top: 296px;
  left: 80px;
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  justify-content: flex-start;
}

.cancel {
  position: relative;
  line-height: 24px;
  background: none;
  border: none;
  cursor: pointer;
  color: rgba(0, 0, 0, 0.7);
}

.cancel:hover {
  color: rgba(0, 0, 0, 0.9);
}

.viewAllProducts {
  position: relative;
  line-height: 24px;
  font-weight: 500;
}

.button {
  border: none;
  border-radius: 4px;
  background-color: #d6bcf7;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
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

.cancelParent {
  position: absolute;
  top: calc(50% + 219px);
  right: 80px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;
  gap: 32px;
  color: #fafafa;
}

.parent {
  width: 100%;
  position: relative;
  box-shadow: 0px 1px 13px rgba(0, 0, 0, 0.05);
  border-radius: 4px;
  background-color: #fff;
  height: 630px;
  overflow: hidden;
  text-align: left;
  font-size: 16px;
  color: #7b21e8;
  font-family: Poppins;
}
</style>