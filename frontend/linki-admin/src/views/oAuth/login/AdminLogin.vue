<template>
  <div class="admin-login-page">
    <div class="admin-login-container">
      <div class="admin-login-box">
        <div class="admin-login-form">
          <h1 class="admin-login-title">관리자 로그인</h1>

          <div class="admin-input-group">
            <input
                ref="userIdInput"
                type="text"
                v-model="userId"
                class="admin-input"
                placeholder="아이디"
                tabindex="1"
                @keyup.enter="handleLogin"
                @keydown.tab="handleTabKey"
                :disabled="isLoading"
            />
          </div>

          <div class="admin-input-group">
            <input
                ref="passwordInput"
                type="password"
                v-model="password"
                class="admin-input"
                placeholder="패스워드"
                tabindex="2"
                @keyup.enter="handleLogin"
                :disabled="isLoading"
            />
          </div>

          <button
              class="admin-login-button"
              @click="handleLogin"
              tabindex="3"
              :disabled="isLoading"
          >
            {{ isLoading ? 'Logging in...' : '로그인' }}
          </button>

          <button
              type="button"
              class="admin-google-button"
              @click="handleGoogleLogin"
              tabindex="4"
              :disabled="isLoading"
          >
            <img src="../../../assets/google-icon.svg" alt="Google" class="admin-google-icon" />
            구글 로그인
          </button>

          <div class="admin-help-links">
            <router-link to="/admin/findid" class="admin-link">아이디 찾기</router-link>
            <router-link to="/admin/findpassword" class="admin-link">비밀번호 찾기</router-link>
          </div>

          <div class="admin-signup-prompt">
            <span>처음 방문하시나요?</span>
            <router-link to="/signup" class="admin-signup-link">회원가입</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAccountStore } from '../../../stores/account'
import httpClient from '../../../utils/httpRequest'

const router = useRouter()
const accountStore = useAccountStore()
const userId = ref('')
const password = ref('')
const isLoading = ref(false)
const userIdInput = ref(null)
const passwordInput = ref(null)

// 컴포넌트 마운트 시 아이디 입력 필드에 포커스
const focusUserIdInput = () => {
  if (userIdInput.value) {
    userIdInput.value.focus()
  }
}

// 비밀번호 입력 필드로 포커스 이동
const focusPasswordInput = () => {
  if (passwordInput.value) {
    passwordInput.value.focus()
  }
}

// JWT 토큰 파싱 함수
const parseJwtToken = (token) => {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    }).join(''))
    return JSON.parse(jsonPayload)
  } catch (error) {
    console.error('JWT 파싱 실패:', error)
    return null
  }
}

const handleLogin = async () => {
  // 입력 유효성 검사 강화
  if (!userId.value.trim()) {
    alert('아이디를 입력해주세요.')
    return
  }

  if (!password.value.trim()) {
    alert('비밀번호를 입력해주세요.')
    return
  }

  if (userId.value.length < 3) {
    alert('아이디는 3자 이상 입력해주세요.')
    return
  }

  if (password.value.length < 4) {
    alert('비밀번호는 4자 이상 입력해주세요.')
    return
  }

  try {
    isLoading.value = true
    const response = await httpClient.post('v1/api/admin/login', {
      adminLoginId: userId.value.trim(),
      adminLoginPw: password.value
    })

    // 백엔드에서 Authorization 헤더로 토큰을 전송하므로 헤더에서 추출
    const accessToken = response.data.accessToken || response.headers['authorization']?.replace('Bearer ', '')

    if (accessToken) {
      // JWT 토큰에서 사용자 정보 추출
      const tokenPayload = parseJwtToken(accessToken)

      if (tokenPayload) {
        const userRole = tokenPayload.userRole
        const userId = tokenPayload.userId

        // Store에 로그인 정보 저장
        accountStore.setLoginInfo(accessToken, { userId, userRole }, 'admin')

        // 콘솔에 사용자 정보 출력
        console.log('=== 관리자 로그인 성공 디버깅 ===')
        console.log('User ID:', userId)
        console.log('User Role:', userRole)
        console.log('Account Store User Role:', accountStore.getUser?.userRole)
        console.log('Account Store User Type:', accountStore.getUserType)
        console.log('Is Admin:', accountStore.isAdmin)

        // 홈 페이지로 리다이렉트
        router.push('/home')
      } else {
        alert('토큰 파싱에 실패했습니다.')
      }
    } else {
      alert('로그인에 실패했습니다. 토큰을 받지 못했습니다.')
    }
  } catch (error) {
    console.error('Login failed:', error)
    if (error.response) {
      if (error.response.status === 401) {
        alert('아이디 또는 비밀번호가 올바르지 않습니다.')
      } else if (error.response.status === 400) {
        alert('입력 정보가 올바르지 않습니다.')
      } else if (error.response.status === 500) {
        alert('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.')
      } else {
        alert(error.response.data?.message || '로그인 중 오류가 발생했습니다.')
      }
    } else if (error.request) {
      alert('서버와 통신할 수 없습니다. 잠시 후 다시 시도해주세요.')
    } else {
      alert('로그인 요청을 보내지 못했습니다. 잠시 후 다시 시도해주세요.')
    }
  } finally {
    isLoading.value = false
  }
}

const handleGoogleLogin = async () => {
  try {
    isLoading.value = true
    const response = await httpClient.get('v1/api/admin/auth/google')
    
    if (response.data.success) {
      // Google OAuth URL로 리다이렉트
      window.location.href = response.data.authUrl
    } else {
      alert('Google 로그인을 시작할 수 없습니다.')
    }
  } catch (error) {
    console.error('Google login failed:', error)
    alert('Google 로그인 중 오류가 발생했습니다.')
  } finally {
    isLoading.value = false
  }
}

// 탭 키 이벤트 처리
const handleTabKey = (event) => {
  event.preventDefault() // 기본 탭 동작 방지
  event.stopPropagation() // 이벤트 전파 방지
  setTimeout(() => {
    if (passwordInput.value) {
      passwordInput.value.focus()
    }
  }, 0)
}

defineOptions({
  name: 'AdminLogin'
})
</script>

<style scoped>
.admin-login-page {
  width: 100%;
  min-height: 100vh;
  background-color: #fff;
}

.admin-login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 48px);
  width: 100%;
  padding: 2rem;
  box-sizing: border-box;
}

.admin-login-box {
  width: 100%;
  max-width: 400px;
  background-color: #fff;
  border-radius: 8px;
  padding: 2rem;
}

.admin-login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.admin-login-title {
  font-size: 32px;
  font-weight: 600;
  color: #000;
  margin: 0;
  text-align: center;
}

.admin-login-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
  text-align: center;
  margin-bottom: 2rem;
}

.admin-input-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 1rem;
}

.admin-input {
  padding: 0.75rem 0;
  border: none;
  border-bottom: 1px solid #ddd;
  font-size: 16px;
  transition: border-color 0.2s;
  width: 100%;
  background: transparent;
}

.admin-input:focus {
  outline: none;
  border-bottom-color: #d6bcf7;
}

.admin-input::placeholder {
  color: #666;
}

.admin-login-button {
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

.admin-login-button:hover {
  background-color: #c4a3f7;
}

.admin-google-button {
  background-color: #fff;
  color: #000;
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-top: 1rem;
  width: 100%;
}

.admin-google-button:hover {
  background-color: #f5f5f5;
}

.admin-google-icon {
  width: 20px;
  height: 20px;
}

.admin-help-links {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-top: 1rem;
}

.admin-link {
  color: #666;
  text-decoration: none;
  font-size: 14px;
}

.admin-link:hover {
  color: #000;
}

.admin-signup-prompt {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-top: 1rem;
}

.admin-signup-link {
  color: #000;
  text-decoration: underline;
  margin-left: 0.5rem;
  font-weight: 500;
}

.admin-signup-link:hover {
  color: #666;
}
</style>