<template>
  <div :class="$style.myProfileParent">
    <div :class="$style.myProfile">비밀번호 변경</div>
    <div :class="$style.formContainer">
      <div :class="$style.parent">
        <div :class="$style.div">현재 비밀번호</div>
        <div :class="$style.placeboxInfo">
          <div :class="$style.placeToInfoBox"></div>
          <input
              type="password"
              v-model="passwordData.currentPassword"
              :class="$style.md"
              :disabled="isLoading"
          />
        </div>
      </div>
      <div :class="$style.parent">
        <div :class="$style.div">새 비밀번호</div>
        <div :class="$style.placeboxInfo">
          <div :class="$style.placeToInfoBox"></div>
          <input
              type="password"
              v-model="passwordData.newPassword"
              :class="$style.md"
              :disabled="isLoading"
          />
        </div>
        <div v-if="passwordError" :class="$style.errorMessage">{{ passwordError }}</div>
      </div>
      <div :class="$style.parent">
        <div :class="$style.div">새 비밀번호 확인</div>
        <div :class="$style.placeboxInfo">
          <div :class="$style.placeToInfoBox"></div>
          <input
              type="password"
              v-model="passwordData.confirmPassword"
              :class="$style.md"
              :disabled="isLoading"
          />
        </div>
        <div v-if="confirmError" :class="$style.errorMessage">{{ confirmError }}</div>
      </div>
      <div :class="$style.buttonContainer">
        <div :class="$style.button" @click="handleSubmit" :disabled="isLoading">
          <div :class="$style.viewAllProducts">{{ isLoading ? '변경 중...' : '변경' }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
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
    await axios.post('/api/user/change-password', {
      currentPassword: passwordData.value.currentPassword,
      newPassword: passwordData.value.newPassword
    })

    alert('비밀번호가 성공적으로 변경되었습니다.')
    router.push('/mypage?currentMenu=profile.basic')
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

<style module>
.myProfileParent {
  position: relative;
  width: 100%;
  padding: 40px;
}

.myProfile {
  font-size: 20px;
  line-height: 28px;
  font-weight: 500;
  margin-bottom: 40px;
}

.formContainer {
  display: flex;
  flex-direction: column;
  gap: 24px;
  max-width: 400px;
}

.div {
  line-height: 24px;
  background: linear-gradient(rgba(0, 0, 0, 0.2), rgba(0, 0, 0, 0.2)), #000;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 8px;
}

.placeToInfoBox {
  position: absolute;
  height: 100%;
  width: 100%;
  top: 0;
  left: 0;
  border-radius: 4px;
  background-color: rgba(245, 245, 245, 0.7);
}

.md {
  position: absolute;
  top: 13px;
  left: 16px;
  line-height: 24px;
  background: transparent;
  border: none;
  width: calc(100% - 32px);
  color: rgba(0, 0, 0, 0.7);
}

.md:focus {
  outline: none;
}

.placeboxInfo {
  position: relative;
  width: 100%;
  height: 50px;
}

.parent {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.errorMessage {
  color: #ff4d4f;
  font-size: 14px;
  margin-top: 4px;
}

.buttonContainer {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.button {
  padding: 12px 48px;
  background-color: #1890ff;
  border-radius: 4px;
  color: white;
  cursor: pointer;
  font-weight: 500;
}

.button:hover {
  background-color: #40a9ff;
}

.viewAllProducts {
  line-height: 24px;
}

[disabled] {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>