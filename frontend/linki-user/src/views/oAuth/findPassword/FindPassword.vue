<template>
  <div class="user-find-password-page">
    <div class="user-find-password-container">
      <div class="user-find-password-box">
        <div class="user-find-password-form">
          <h1 class="user-find-password-title">비밀번호 찾기</h1>
          <p class="user-find-password-subtitle">아이디와 이메일을 입력해주세요</p>
          
          <!-- 1단계: 아이디와 이메일 입력 -->
          <div v-if="step === 1">
            <div class="user-input-group">
              <input
                type="text"
                v-model="userId"
                class="user-input"
                placeholder="아이디"
                :disabled="isLoading"
              />
            </div>
            <div class="user-input-group">
              <input
                type="email"
                v-model="email"
                class="user-input"
                placeholder="이메일"
                :disabled="isLoading"
              />
            </div>
            <button
              class="user-find-password-button"
              @click="sendVerificationCode"
              :disabled="isLoading || !userId || !email"
            >
              {{ isLoading ? '인증번호 발송 중...' : '인증번호 발송' }}
            </button>
          </div>

          <!-- 2단계: 인증번호 입력 -->
          <div v-if="step === 2">
            <div class="user-verification-info">
              <p>{{ email }}로 인증번호를 발송했습니다.</p>
              <p>인증번호를 입력해주세요.</p>
            </div>
            <div class="user-input-group">
              <input
                type="text"
                v-model="verificationCode"
                class="user-input"
                placeholder="인증번호 6자리"
                maxlength="6"
                :disabled="isLoading"
              />
            </div>
            <div class="user-timer" v-if="timeLeft > 0">
              남은 시간: {{ Math.floor(timeLeft / 60) }}:{{ String(timeLeft % 60).padStart(2, '0') }}
            </div>
            <div class="user-timer user-timer-expired" v-else>
              인증번호가 만료되었습니다.
            </div>
            <button
              class="user-find-password-button"
              @click="verifyCode"
              :disabled="isLoading || !verificationCode || timeLeft <= 0"
            >
              {{ isLoading ? '확인 중...' : '인증번호 확인' }}
            </button>
            <button
              class="user-resend-button"
              @click="resendCode"
              :disabled="isLoading || timeLeft > 0"
            >
              인증번호 재발송
            </button>
          </div>

          <!-- 3단계: 새 비밀번호 입력 -->
          <div v-if="step === 3">
            <div class="user-password-info">
              <h3>새 비밀번호 설정</h3>
              <p>새로운 비밀번호를 입력해주세요.</p>
            </div>
            <div class="user-input-group">
              <input
                type="password"
                v-model="newPassword"
                class="user-input"
                placeholder="새 비밀번호"
                :disabled="isLoading"
              />
            </div>
            <div class="user-input-group">
              <input
                type="password"
                v-model="confirmPassword"
                class="user-input"
                placeholder="새 비밀번호 확인"
                :disabled="isLoading"
              />
            </div>
            <button
              class="user-find-password-button"
              @click="changePassword"
              :disabled="isLoading || !newPassword || !confirmPassword || newPassword !== confirmPassword"
            >
              {{ isLoading ? '변경 중...' : '비밀번호 변경' }}
            </button>
          </div>

          <!-- 4단계: 완료 -->
          <div v-if="step === 4">
            <div class="user-result-info">
              <h3>비밀번호 변경 완료</h3>
              <p>비밀번호가 성공적으로 변경되었습니다.</p>
            </div>
            <button
              class="user-find-password-button"
              @click="goToLogin"
            >
              로그인으로 돌아가기
            </button>
          </div>

          <div class="user-help-links">
            <router-link to="/findid" class="user-link">아이디 찾기</router-link>
            <router-link to="/login" class="user-link">로그인으로 돌아가기</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import httpClient from '../../../utils/httpRequest'

const router = useRouter()
const userId = ref('')
const email = ref('')
const verificationCode = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const isLoading = ref(false)
const step = ref(1)
const timeLeft = ref(0)
let timer = null

// 인증번호 발송
const sendVerificationCode = async () => {
  if (!userId.value || !email.value) {
    alert('아이디와 이메일을 모두 입력해주세요.')
    return
  }

  try {
    isLoading.value = true
    const response = await httpClient.post('/v1/api/user/find-password/send-verification', {
      userLoginId: userId.value,
      userEmail: email.value
    })

    if (response.data.success) {
      step.value = 2
      startTimer()
      alert('인증번호가 이메일로 발송되었습니다.')
    } else {
      alert(response.data.message || '인증번호 발송에 실패했습니다.')
    }
  } catch (error) {
    console.error('Send verification code failed:', error)
    if (error.response) {
      alert(error.response.data.message || '인증번호 발송 중 오류가 발생했습니다.')
    } else if (error.request) {
      alert('서버와 통신할 수 없습니다. 잠시 후 다시 시도해주세요.')
    } else {
      alert('요청을 보내지 못했습니다. 잠시 후 다시 시도해주세요.')
    }
  } finally {
    isLoading.value = false
  }
}

// 인증번호 확인
const verifyCode = async () => {
  if (!verificationCode.value) {
    alert('인증번호를 입력해주세요.')
    return
  }

  try {
    isLoading.value = true
    const response = await httpClient.post('/v1/api/user/find-password/verify', {
      userLoginId: userId.value,
      userEmail: email.value,
      verificationCode: verificationCode.value
    })

    if (response.data.success) {
      step.value = 3
      stopTimer()
    } else {
      alert(response.data.message || '인증번호가 올바르지 않습니다.')
    }
  } catch (error) {
    console.error('Verify code failed:', error)
    if (error.response) {
      alert(error.response.data.message || '인증번호 확인 중 오류가 발생했습니다.')
    } else if (error.request) {
      alert('서버와 통신할 수 없습니다. 잠시 후 다시 시도해주세요.')
    } else {
      alert('요청을 보내지 못했습니다. 잠시 후 다시 시도해주세요.')
    }
  } finally {
    isLoading.value = false
  }
}

// 인증번호 재발송
const resendCode = async () => {
  try {
    isLoading.value = true
    const response = await httpClient.post('/v1/api/user/find-password/resend-verification', {
      userLoginId: userId.value,
      userEmail: email.value
    })

    if (response.data.success) {
      verificationCode.value = ''
      startTimer()
      alert('인증번호가 재발송되었습니다.')
    } else {
      alert(response.data.message || '인증번호 재발송에 실패했습니다.')
    }
  } catch (error) {
    console.error('Resend verification code failed:', error)
    if (error.response) {
      alert(error.response.data.message || '인증번호 재발송 중 오류가 발생했습니다.')
    } else if (error.request) {
      alert('서버와 통신할 수 없습니다. 잠시 후 다시 시도해주세요.')
    } else {
      alert('요청을 보내지 못했습니다. 잠시 후 다시 시도해주세요.')
    }
  } finally {
    isLoading.value = false
  }
}

// 비밀번호 변경
const changePassword = async () => {
  if (!newPassword.value || !confirmPassword.value) {
    alert('새 비밀번호를 입력해주세요.')
    return
  }

  if (newPassword.value !== confirmPassword.value) {
    alert('비밀번호가 일치하지 않습니다.')
    return
  }

  if (newPassword.value.length < 6) {
    alert('비밀번호는 6자 이상이어야 합니다.')
    return
  }

  try {
    isLoading.value = true
    const response = await httpClient.post('/v1/api/user/find-password/change-password', {
      userLoginId: userId.value,
      userEmail: email.value,
      verificationCode: verificationCode.value,
      newPassword: newPassword.value
    })

    if (response.data.success) {
      step.value = 4
    } else {
      alert(response.data.message || '비밀번호 변경에 실패했습니다.')
    }
  } catch (error) {
    console.error('Change password failed:', error)
    if (error.response) {
      alert(error.response.data.message || '비밀번호 변경 중 오류가 발생했습니다.')
    } else if (error.request) {
      alert('서버와 통신할 수 없습니다. 잠시 후 다시 시도해주세요.')
    } else {
      alert('요청을 보내지 못했습니다. 잠시 후 다시 시도해주세요.')
    }
  } finally {
    isLoading.value = false
  }
}

// 타이머 시작 (3분)
const startTimer = () => {
  timeLeft.value = 180 // 3분
  timer = setInterval(() => {
    timeLeft.value--
    if (timeLeft.value <= 0) {
      stopTimer()
    }
  }, 1000)
}

// 타이머 정지
const stopTimer = () => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

// 로그인 페이지로 이동
const goToLogin = () => {
  router.push('/login')
}

// 컴포넌트 언마운트 시 타이머 정리
onUnmounted(() => {
  stopTimer()
})

defineOptions({
  name: 'UserFindPassword'
})
</script>

<style scoped>
.user-find-password-page {
  width: 100%;
  min-height: 100vh;
  background-color: #fff;
}

.user-find-password-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 48px);
  width: 100%;
  padding: 2rem;
  box-sizing: border-box;
}

.user-find-password-box {
  width: 100%;
  max-width: 400px;
  background-color: #fff;
  border-radius: 8px;
  padding: 2rem;
}

.user-find-password-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.user-find-password-title {
  font-size: 32px;
  font-weight: 600;
  color: #000;
  margin: 0;
  text-align: center;
}

.user-find-password-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
  text-align: center;
  margin-bottom: 2rem;
}

.user-input-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 1rem;
}

.user-input {
  padding: 0.75rem 0;
  border: none;
  border-bottom: 1px solid #ddd;
  font-size: 16px;
  transition: border-color 0.2s;
  width: 100%;
  background: transparent;
}

.user-input:focus {
  outline: none;
  border-bottom-color: #d6bcf7;
}

.user-input::placeholder {
  color: #666;
}

.user-input:disabled {
  background-color: transparent;
  cursor: not-allowed;
}

.user-find-password-button {
  background-color: #d6bcf7;
  color: #000;
  padding: 1rem;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-top: 1rem;
  width: 100%;
}

.user-find-password-button:hover:not(:disabled) {
  background-color: #c4a3f7;
}

.user-find-password-button:disabled {
  background-color: #e9d6ff;
  cursor: not-allowed;
}

.user-resend-button {
  background-color: #fff;
  color: #666;
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-top: 0.5rem;
  width: 100%;
}

.user-resend-button:hover:not(:disabled) {
  background-color: #f5f5f5;
}

.user-resend-button:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.user-verification-info {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #666;
}

.user-verification-info p {
  margin: 0.5rem 0;
  font-size: 14px;
}

.user-password-info {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #666;
}

.user-password-info h3 {
  color: #8C30F5;
  margin-bottom: 0.5rem;
  font-size: 20px;
  font-weight: 600;
}

.user-password-info p {
  margin: 0.5rem 0;
  font-size: 14px;
}

.user-timer {
  text-align: center;
  margin: 1rem 0;
  font-weight: bold;
  color: #8C30F5;
  font-size: 14px;
}

.user-timer-expired {
  color: #ff4444;
}

.user-result-info {
  text-align: center;
  margin-bottom: 2rem;
}

.user-result-info h3 {
  color: #8C30F5;
  margin-bottom: 1rem;
  font-size: 24px;
  font-weight: 600;
}

.user-result-info p {
  color: #666;
  font-size: 16px;
  margin: 0.5rem 0;
}

.user-result-info strong {
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.user-help-links {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-top: 1rem;
}

.user-link {
  color: #666;
  text-decoration: none;
  font-size: 14px;
}

.user-link:hover {
  color: #000;
}
</style> 