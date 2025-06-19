<template>
  <div class="user-signup-page">
    <div class="user-signup-container">
      <div class="user-signup-box">
        <div class="user-signup-form">
          <h1 class="user-signup-title">Create an account</h1>
          <p class="user-signup-subtitle">Enter your details below</p>

          <div class="user-input-group">
            <input type="text" v-model="userId" class="user-input" placeholder="아이디" :disabled="isLoading" />
            <span class="error-message" v-if="errors.userId">{{ errors.userId }}</span>
          </div>

          <div class="user-input-group">
            <input type="password" v-model="password" class="user-input" placeholder="패스워드" :disabled="isLoading" />
            <span class="error-message" v-if="errors.password">{{ errors.password }}</span>
          </div>

          <div class="user-input-group">
            <input type="password" v-model="confirmPassword" class="user-input" placeholder="패스워드 확인" :disabled="isLoading" />
            <span class="error-message" v-if="errors.confirmPassword">{{ errors.confirmPassword }}</span>
          </div>

          <div class="user-input-group">
            <input type="text" v-model="name" class="user-input" placeholder="이름" :disabled="isLoading" />
            <span class="error-message" v-if="errors.name">{{ errors.name }}</span>
          </div>

          <div class="user-input-group">
            <input type="text" v-model="handphone" class="user-input" placeholder="핸드폰" :disabled="isLoading" />
            <span class="error-message" v-if="errors.handphone">{{ errors.handphone }}</span>
          </div>

          <div class="user-input-group">
            <input type="email" v-model="email" class="user-input" placeholder="이메일" :disabled="isLoading" />
            <span class="error-message" v-if="errors.email">{{ errors.email }}</span>
          </div>

          <!-- 약관 동의 섹션 -->
          <div class="terms-section">
            <div class="terms-all">
              <label class="checkbox-label">
                <input 
                  type="checkbox" 
                  v-model="allTermsAgreed"
                  @change="handleAllTermsChange"
                  :disabled="isLoading"
                />
                <span class="checkmark"></span>
                <span>전체 약관에 동의합니다</span>
              </label>
            </div>
            <div class="terms-divider"></div>
            <div class="terms-item">
              <label class="checkbox-label">
                <input 
                  type="checkbox" 
                  v-model="termsAgreed"
                  :disabled="isLoading"
                  @change="validateTerms"
                />
                <span class="checkmark"></span>
                <span>이용약관 동의 (필수)</span>
              </label>
              <button class="terms-detail-button" @click="showTermsModal = true" type="button">
                자세히
              </button>
            </div>
            <div class="terms-item">
              <label class="checkbox-label">
                <input 
                  type="checkbox" 
                  v-model="privacyAgreed"
                  :disabled="isLoading"
                  @change="validateTerms"
                />
                <span class="checkmark"></span>
                <span>개인정보 수집 및 이용 동의 (필수)</span>
              </label>
              <button class="terms-detail-button" @click="showPrivacyModal = true" type="button">
                자세히
              </button>
            </div>
            <div class="terms-item">
              <label class="checkbox-label">
                <input 
                  type="checkbox" 
                  v-model="marketingAgreed"
                  :disabled="isLoading"
                />
                <span class="checkmark"></span>
                <span>마케팅 정보 수신 동의 (선택)</span>
              </label>
              <button class="terms-detail-button" @click="showMarketingModal = true" type="button">
                자세히
              </button>
            </div>
            <span class="error-message" v-if="errors.terms">{{ errors.terms }}</span>
          </div>

          <button class="user-signup-button" @click="handleSignup" :disabled="isLoading || !isFormValid || !isTermsValid">
            {{ isLoading ? 'Creating Account...' : 'Create Account' }}
          </button>

          <button class="user-google-button" @click="handleGoogleSignup" :disabled="isLoading">
            <img src="@/assets/google-icon.svg" alt="Google" class="user-google-icon" />
            Sign up with Google
          </button>

          <div class="user-login-prompt">
            <span>Already have account?</span>
            <router-link to="/login" class="user-login-link">Log in</router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- 이용약관 모달 -->
    <div v-if="showTermsModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>이용약관</h2>
          <button class="modal-close" @click="showTermsModal = false">&times;</button>
        </div>
        <div class="modal-body">
          <h3>제 1 장 총칙</h3>
          <p>제 1 조 (목적)</p>
          <p>본 약관은 링키(이하 "회사")가 제공하는 서비스의 이용조건 및 절차, 회사와 회원 간의 권리, 의무 및 책임사항 등을 규정함을 목적으로 합니다.</p>
          
          <h3>제 2 장 서비스 이용</h3>
          <p>제 2 조 (서비스의 제공)</p>
          <p>1. 회사는 다음과 같은 서비스를 제공합니다.</p>
          <p>   - 인플루언서와 광고주 매칭 서비스</p>
          <p>   - 광고 캠페인 관리 서비스</p>
          <p>   - 기타 회사가 정하는 서비스</p>
          
          <!-- 추가 약관 내용 -->
        </div>
        <div class="modal-footer">
          <button class="modal-button" @click="showTermsModal = false">확인</button>
        </div>
      </div>
    </div>

    <!-- 개인정보처리방침 모달 -->
    <div v-if="showPrivacyModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>개인정보 수집 및 이용</h2>
          <button class="modal-close" @click="showPrivacyModal = false">&times;</button>
        </div>
        <div class="modal-body">
          <h3>1. 수집하는 개인정보 항목</h3>
          <p>회사는 회원가입, 상담, 서비스 신청 등을 위해 아래와 같은 개인정보를 수집하고 있습니다.</p>
          <p>- 수집항목: 이름, 이메일, 휴대전화번호, 아이디, 비밀번호</p>
          <p>- 개인정보 수집방법: 홈페이지 회원가입, 서비스 이용</p>
          
          <h3>2. 개인정보의 수집 및 이용목적</h3>
          <p>- 회원 관리: 회원제 서비스 이용에 따른 본인확인, 개인식별, 불량회원의 부정 이용 방지</p>
          <p>- 서비스 제공: 인플루언서-광고주 매칭, 캠페인 관리, 콘텐츠 제공</p>
          
          <!-- 추가 개인정보처리방침 내용 -->
        </div>
        <div class="modal-footer">
          <button class="modal-button" @click="showPrivacyModal = false">확인</button>
        </div>
      </div>
    </div>

    <!-- 마케팅 정보 수신 동의 모달 -->
    <div v-if="showMarketingModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>마케팅 정보 수신 동의</h2>
          <button class="modal-close" @click="showMarketingModal = false">&times;</button>
        </div>
        <div class="modal-body">
          <h3>마케팅 정보 수신 동의</h3>
          <p>링키는 회원님께 유용한 서비스 및 이벤트 정보를 제공하기 위해 마케팅 정보를 발송합니다.</p>
          
          <h3>수신 정보 내용</h3>
          <p>- 서비스 업데이트 및 새로운 기능 안내</p>
          <p>- 이벤트, 프로모션 정보</p>
          <p>- 맞춤형 광고 및 캠페인 추천</p>
          
          <h3>수신 방법</h3>
          <p>- 이메일</p>
          <p>- SMS/MMS</p>
          <p>- 푸시 알림</p>
          
          <p class="note">* 동의하지 않으셔도 기본 서비스 이용에는 제한이 없습니다.</p>
        </div>
        <div class="modal-footer">
          <button class="modal-button" @click="showMarketingModal = false">확인</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useAlert } from '@/composables/alert'

const router = useRouter()
const { showAlert } = useAlert()
const userId = ref('')
const password = ref('')
const confirmPassword = ref('')
const name = ref('')
const handphone = ref('')
const email = ref('')
const isLoading = ref(false)

// 모달 상태
const showTermsModal = ref(false)
const showPrivacyModal = ref(false)
const showMarketingModal = ref(false)

// 약관 동의 상태
const termsAgreed = ref(false)
const privacyAgreed = ref(false)
const marketingAgreed = ref(false)
const allTermsAgreed = ref(false)

// 에러 메시지 상태
const errors = ref({
  userId: '',
  password: '',
  confirmPassword: '',
  name: '',
  handphone: '',
  email: '',
  terms: ''
})

// 실시간 유효성 검사
const validateUserId = () => {
  if (!userId.value) {
    errors.value.userId = '아이디를 입력해주세요.'
  } else if (userId.value.length < 4) {
    errors.value.userId = '아이디는 4자 이상이어야 합니다.'
  } else {
    errors.value.userId = ''
  }
}

const validatePassword = () => {
  if (!password.value) {
    errors.value.password = '비밀번호를 입력해주세요.'
  } else if (password.value.length < 8) {
    errors.value.password = '비밀번호는 8자 이상이어야 합니다.'
  } else {
    errors.value.password = ''
  }
}

const validateConfirmPassword = () => {
  if (!confirmPassword.value) {
    errors.value.confirmPassword = '비밀번호 확인을 입력해주세요.'
  } else if (password.value !== confirmPassword.value) {
    errors.value.confirmPassword = '비밀번호가 일치하지 않습니다.'
  } else {
    errors.value.confirmPassword = ''
  }
}

const validateName = () => {
  if (!name.value) {
    errors.value.name = '이름을 입력해주세요.'
  } else {
    errors.value.name = ''
  }
}

const validateHandphone = () => {
  if (!handphone.value) {
    errors.value.handphone = '핸드폰 번호를 입력해주세요.'
  } else if (!/^01[016789]-?\d{3,4}-?\d{4}$/.test(handphone.value)) {
    errors.value.handphone = '올바른 핸드폰 번호를 입력해주세요.'
  } else {
    errors.value.handphone = ''
  }
}

const validateEmail = () => {
  if (!email.value) {
    errors.value.email = '이메일을 입력해주세요.'
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
    errors.value.email = '올바른 이메일 주소를 입력해주세요.'
  } else {
    errors.value.email = ''
  }
}

const validateTerms = () => {
  if (!termsAgreed.value || !privacyAgreed.value) {
    errors.value.terms = '필수 약관에 동의해주세요.'
  } else {
    errors.value.terms = ''
  }
}

// 입력값 변경 시 실시간 유효성 검사
watch(userId, validateUserId)
watch(password, () => {
  validatePassword()
  validateConfirmPassword()
})
watch(confirmPassword, validateConfirmPassword)
watch(name, validateName)
watch(handphone, validateHandphone)
watch(email, validateEmail)

// 전체 약관 동의 처리
const handleAllTermsChange = () => {
  termsAgreed.value = allTermsAgreed.value
  privacyAgreed.value = allTermsAgreed.value
  marketingAgreed.value = allTermsAgreed.value
  validateTerms()
}

// 약관 동의 유효성 검사
const isTermsValid = computed(() => {
  return termsAgreed.value && privacyAgreed.value
})

const isFormValid = computed(() => {
  return (
      userId.value.length > 0 &&
      password.value.length > 0 &&
      password.value === confirmPassword.value &&
      name.value.length > 0 &&
      handphone.value.length > 0 &&
      email.value.length > 0 &&
      /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value) &&
      isTermsValid.value
  )
})

const validateForm = () => {
  if (!userId.value) {
    showAlert('아이디를 입력해주세요.', 'error')
    return false
  }
  if (!password.value) {
    showAlert('비밀번호를 입력해주세요.', 'error')
    return false
  }
  if (password.value !== confirmPassword.value) {
    showAlert('비밀번호가 일치하지 않습니다.', 'error')
    return false
  }
  if (!name.value) {
    showAlert('이름을 입력해주세요.', 'error')
    return false
  }
  if (!handphone.value) {
    showAlert('핸드폰 번호를 입력해주세요.', 'error')
    return false
  }
  if (!email.value || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
    showAlert('유효한 이메일을 입력해주세요.', 'error')
    return false
  }
  if (!isTermsValid.value) {
    showAlert('필수 약관에 동의해주세요.', 'error')
    return false
  }
  return true
}

const handleSignup = async () => {
  if (!validateForm()) {
    return
  }

  try {
    isLoading.value = true
    const signupData = {
      userLoginId: userId.value,
      userLoginPw: password.value,
      userName: name.value,
      userPhone: handphone.value,
      userEmail: email.value,
    }

    const response = await axios.post('v1/api/nonuser/signup', signupData)
    
    if (response.data.success) {
      showAlert('회원가입이 완료되었습니다. 로그인해주세요.', 'success')
      router.push('/login')
    } else {
      showAlert(response.data.message || '회원가입에 실패했습니다.', 'error')
    }
  } catch (error) {
    console.error('Signup failed:', error)
    const errorMessage = error.response?.data?.message || '회원가입 중 오류가 발생했습니다.'
    showAlert(errorMessage, 'error')
  } finally {
    isLoading.value = false
  }
}

const handleGoogleSignup = async () => {
  try {
    isLoading.value = true
    const response = await axios.get('/api/user/auth/google')
    window.location.href = response.data.authUrl
  } catch (error) {
    console.error('Google signup failed:', error)
    showAlert('Google 로그인 연동 중 오류가 발생했습니다.', 'error')
  } finally {
    isLoading.value = false
  }
}

defineOptions({
  name: 'UserSignUp'
})
</script>

<style scoped>
.user-signup-page {
  width: 100%;
  min-height: 100vh;
  background-color: #fff;
}

.user-signup-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 48px);
  width: 100%;
  padding: 2rem;
  box-sizing: border-box;
}

.user-signup-box {
  width: 100%;
  max-width: 400px;
  background-color: #fff;
  border-radius: 8px;
  padding: 2rem;
}

.user-signup-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.user-signup-title {
  font-size: 32px;
  font-weight: 600;
  color: #000;
  margin: 0;
  text-align: center;
}

.user-signup-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
  text-align: center;
  margin-bottom: 2rem;
}

.user-input-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 0.5rem;
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

.user-input.error {
  border-bottom-color: #ff4444;
}

.user-signup-button {
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

.user-signup-button:hover {
  background-color: #c4a3f7;
}

.user-google-button {
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

.user-google-button:hover {
  background-color: #f5f5f5;
}

.user-google-icon {
  width: 20px;
  height: 20px;
}

.user-login-prompt {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-top: 1rem;
}

.user-login-link {
  color: #000;
  text-decoration: underline;
  margin-left: 0.5rem;
  font-weight: 500;
}

.user-login-link:hover {
  color: #666;
}

/* 약관 섹션 스타일 */
.terms-section {
  margin: 1rem 0;
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.terms-all {
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.terms-divider {
  height: 1px;
  background-color: #ddd;
  margin: 0.5rem 0;
}

.terms-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 0.5rem 0;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.checkbox-label input[type="checkbox"] {
  margin-right: 8px;
}

.terms-detail-button {
  background: none;
  border: none;
  color: #666;
  text-decoration: underline;
  cursor: pointer;
  padding: 0;
  font-size: 14px;
}

/* 모달 스타일 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 1rem;
  border-bottom: 1px solid #ddd;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.5rem;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0;
  color: #666;
}

.modal-body {
  padding: 1rem;
  overflow-y: auto;
  flex-grow: 1;
}

.modal-body h3 {
  margin: 1rem 0 0.5rem;
  font-size: 1.2rem;
}

.modal-body p {
  margin: 0.5rem 0;
  line-height: 1.5;
}

.modal-footer {
  padding: 1rem;
  border-top: 1px solid #ddd;
  text-align: right;
}

.modal-button {
  background-color: #d6bcf7;
  color: #000;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.modal-button:hover {
  background-color: #c4a3f7;
}

.note {
  color: #666;
  font-size: 0.9rem;
  font-style: italic;
}

/* 체크박스 커스텀 스타일 */
.checkmark {
  position: relative;
  display: inline-block;
  width: 18px;
  height: 18px;
  margin-right: 8px;
  border: 2px solid #d6bcf7;
  border-radius: 3px;
}

.checkbox-label input[type="checkbox"] {
  display: none;
}

.checkbox-label input[type="checkbox"]:checked + .checkmark::after {
  content: '';
  position: absolute;
  left: 5px;
  top: 2px;
  width: 5px;
  height: 10px;
  border: solid #d6bcf7;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.checkbox-label:hover .checkmark {
  border-color: #c4a3f7;
}

.checkbox-label input[type="checkbox"]:disabled + .checkmark {
  border-color: #ddd;
  background-color: #f5f5f5;
}

.error-message {
  color: #ff4444;
  font-size: 12px;
  margin-top: 4px;
  min-height: 16px;
}
</style>