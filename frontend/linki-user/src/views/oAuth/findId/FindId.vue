<template>
  <div class="user-find-id-page">
    <div class="user-find-id-container">
      <div class="user-find-id-box">
        <div class="user-find-id-form">
          <h1 class="user-find-id-title">아이디 찾기</h1>
          <p class="user-find-id-subtitle">이름과 이메일을 입력해주세요</p>
          
          <!-- 1단계: 이름과 이메일 입력 -->
          <div v-if="step === 1">
            <div class="user-input-group">
              <input
                type="text"
                v-model="name"
                class="user-input"
                placeholder="이름"
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
              class="user-find-id-button"
              @click="sendVerificationCode"
              :disabled="isLoading || !name || !email"
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
              class="user-find-id-button"
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

          <!-- 3단계: 아이디 표시 -->
          <div v-if="step === 3">
            <div class="user-result-info">
              <h3>아이디 찾기 완료</h3>
              <p>회원님의 아이디는 <strong>{{ foundUserId }}</strong> 입니다.</p>
            </div>
            <button
              class="user-find-id-button"
              @click="goToLogin"
            >
              로그인으로 돌아가기
            </button>
          </div>

          <div class="user-help-links">
            <router-link to="/findpassword" class="user-link">비밀번호 찾기</router-link>
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
const name = ref('')
const email = ref('')
const verificationCode = ref('')
const isLoading = ref(false)
const step = ref(1)
const timeLeft = ref(0)
const foundUserId = ref('')
let timer = null

// 인증번호 발송
const sendVerificationCode = async () => {
  if (!name.value || !email.value) {
    alert('이름과 이메일을 모두 입력해주세요.')
    return
  }

  try {
    isLoading.value = true
    const response = await httpClient.post('/v1/api/user/find-id/send-verification', {
      userName: name.value,
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
    const response = await httpClient.post('/v1/api/user/find-id/verify', {
      userName: name.value,
      userEmail: email.value,
      verificationCode: verificationCode.value
    })

    if (response.data.success) {
      foundUserId.value = response.data.userId
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
    const response = await httpClient.post('/v1/api/user/find-id/resend-verification', {
      userName: name.value,
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
  name: 'UserFindId'
})
</script>

<style scoped>
.user-find-id-page {
  width: 100%;
  min-height: 100vh;
  background-color: #fff;
}

.user-find-id-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 48px);
  width: 100%;
  padding: 2rem;
  box-sizing: border-box;
}

.user-find-id-box {
  width: 100%;
  max-width: 400px;
  background-color: #fff;
  border-radius: 8px;
  padding: 2rem;
}

.user-find-id-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.user-find-id-title {
  font-size: 32px;
  font-weight: 600;
  color: #000;
  margin: 0;
  text-align: center;
}

.user-find-id-subtitle {
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

.user-find-id-button {
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

.user-find-id-button:hover:not(:disabled) {
  background-color: #c4a3f7;
}

.user-find-id-button:disabled {
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