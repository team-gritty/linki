<template>
  <div class="admin-signup-page">
    <div class="admin-signup-container">
      <div class="admin-signup-box">
        <div class="admin-signup-form">
          <h1 class="admin-signup-title">관리자 회원가입</h1>

          <div class="admin-input-group">
            <input type="text" v-model="adminId" class="admin-input" placeholder="관리자 아이디" :disabled="isLoading" />
            <span class="error-message" v-if="errors.adminId">{{ errors.adminId }}</span>
          </div>

          <div class="admin-input-group">
            <input type="password" v-model="password" class="admin-input" placeholder="패스워드" :disabled="isLoading" />
            <span class="error-message" v-if="errors.password">{{ errors.password }}</span>
          </div>

          <div class="admin-input-group">
            <input type="password" v-model="confirmPassword" class="admin-input" placeholder="패스워드 확인" :disabled="isLoading" />
            <span class="error-message" v-if="errors.confirmPassword">{{ errors.confirmPassword }}</span>
          </div>

          <div class="admin-input-group">
            <input type="text" v-model="name" class="admin-input" placeholder="이름" :disabled="isLoading" />
            <span class="error-message" v-if="errors.name">{{ errors.name }}</span>
          </div>

          <div class="admin-input-group">
            <input type="text" v-model="phone" class="admin-input" placeholder="핸드폰" :disabled="isLoading" />
            <span class="error-message" v-if="errors.phone">{{ errors.phone }}</span>
          </div>

          <div class="admin-input-group">
            <input type="email" v-model="email" class="admin-input" placeholder="이메일" :disabled="isLoading" />
            <span class="error-message" v-if="errors.email">{{ errors.email }}</span>
          </div>

          <div class="admin-input-group">
            <input type="text" v-model="address" class="admin-input" placeholder="주소" :disabled="isLoading" />
            <span class="error-message" v-if="errors.address">{{ errors.address }}</span>
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
                <span>관리자 이용약관 동의 (필수)</span>
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
            <span class="error-message" v-if="errors.terms">{{ errors.terms }}</span>
          </div>

          <button class="admin-signup-button" @click="handleSignup" :disabled="isLoading || !isFormValid || !isTermsValid">
            {{ isLoading ? '등록 중...' : '관리자 등록' }}
          </button>

          <div class="admin-login-prompt">
            <span>이미 계정이 있으신가요?</span>
            <router-link to="/login" class="admin-login-link">로그인</router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- 관리자 이용약관 모달 -->
    <div v-if="showTermsModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>관리자 이용약관</h2>
          <button class="modal-close" @click="showTermsModal = false">&times;</button>
        </div>
        <div class="modal-body">
          <h3>제 1 장 총칙</h3>
          <p>제 1 조 (목적)</p>
          <p>본 약관은 링키(이하 "회사")가 제공하는 관리자 서비스의 이용조건 및 절차, 회사와 관리자 간의 권리, 의무 및 책임사항 등을 규정함을 목적으로 합니다.</p>
          
          <h3>제 2 장 관리자 서비스 이용</h3>
          <p>제 2 조 (관리자 권한)</p>
          <p>1. 관리자는 다음과 같은 권한을 가집니다.</p>
          <p>   - 사용자 관리 및 모니터링</p>
          <p>   - 시스템 설정 관리</p>
          <p>   - 데이터 관리 및 분석</p>
          <p>   - 기타 회사가 정하는 관리 업무</p>
          
          <h3>제 3 장 관리자 의무</h3>
          <p>제 3 조 (보안 준수)</p>
          <p>관리자는 시스템 보안을 위해 다음 사항을 준수해야 합니다.</p>
          <p>   - 계정 정보의 안전한 관리</p>
          <p>   - 권한 남용 금지</p>
          <p>   - 개인정보 보호 의무</p>
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
          <p>회사는 관리자 등록, 관리 업무 수행을 위해 아래와 같은 개인정보를 수집하고 있습니다.</p>
          <p>- 수집항목: 이름, 이메일, 휴대전화번호, 관리자 아이디, 비밀번호</p>
          <p>- 개인정보 수집방법: 관리자 등록 신청</p>
          
          <h3>2. 개인정보의 수집 및 이용목적</h3>
          <p>- 관리자 인증: 관리자 본인확인, 개인식별, 관리 권한 부여</p>
          <p>- 시스템 관리: 관리 업무 수행, 시스템 모니터링, 보안 관리</p>
          <p>- 연락 및 공지: 중요 시스템 알림, 업무 관련 연락</p>
          
          <h3>3. 개인정보의 보유 및 이용기간</h3>
          <p>관리자 계정 삭제 시까지 보유하며, 관련 법령에 따라 일정 기간 보관할 수 있습니다.</p>
        </div>
        <div class="modal-footer">
          <button class="modal-button" @click="showPrivacyModal = false">확인</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import httpClient from '../../../utils/httpRequest'

const router = useRouter()

// Form data
const adminId = ref('')
const password = ref('')
const confirmPassword = ref('')
const name = ref('')
const phone = ref('')
const email = ref('')
const address = ref('')

// Terms agreement
const allTermsAgreed = ref(false)
const termsAgreed = ref(false)
const privacyAgreed = ref(false)

// Modal states
const showTermsModal = ref(false)
const showPrivacyModal = ref(false)

// Loading state
const isLoading = ref(false)

// Error messages
const errors = ref({
  adminId: '',
  password: '',
  confirmPassword: '',
  name: '',
  phone: '',
  email: '',
  address: '',
  terms: ''
})

// Validation functions
const validateAdminId = () => {
  if (!adminId.value) {
    errors.value.adminId = '관리자 아이디를 입력해주세요.'
    return false
  }
  if (adminId.value.length < 4) {
    errors.value.adminId = '아이디는 4자 이상 입력해주세요.'
    return false
  }
  if (!/^[a-zA-Z0-9]+$/.test(adminId.value)) {
    errors.value.adminId = '아이디는 영문자와 숫자만 사용 가능합니다.'
    return false
  }
  errors.value.adminId = ''
  return true
}

const validatePassword = () => {
  if (!password.value) {
    errors.value.password = '비밀번호를 입력해주세요.'
    return false
  }
  if (password.value.length < 6) {
    errors.value.password = '비밀번호는 6자 이상 입력해주세요.'
    return false
  }
  errors.value.password = ''
  return true
}

const validateConfirmPassword = () => {
  if (!confirmPassword.value) {
    errors.value.confirmPassword = '비밀번호 확인을 입력해주세요.'
    return false
  }
  if (password.value !== confirmPassword.value) {
    errors.value.confirmPassword = '비밀번호가 일치하지 않습니다.'
    return false
  }
  errors.value.confirmPassword = ''
  return true
}

const validateName = () => {
  if (!name.value) {
    errors.value.name = '이름을 입력해주세요.'
    return false
  }
  if (name.value.length < 2) {
    errors.value.name = '이름은 2자 이상 입력해주세요.'
    return false
  }
  errors.value.name = ''
  return true
}

const validatePhone = () => {
  if (!phone.value) {
    errors.value.phone = '핸드폰 번호를 입력해주세요.'
    return false
  }
  if (!/^01[016789]\d{7,8}$/.test(phone.value.replace(/-/g, ''))) {
    errors.value.phone = '올바른 핸드폰 번호를 입력해주세요.'
    return false
  }
  errors.value.phone = ''
  return true
}

const validateEmail = () => {
  if (!email.value) {
    errors.value.email = '이메일을 입력해주세요.'
    return false
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(email.value)) {
    errors.value.email = '올바른 이메일 형식을 입력해주세요.'
    return false
  }
  errors.value.email = ''
  return true
}

const validateAddress = () => {
  if (!address.value) {
    errors.value.address = '주소를 입력해주세요.'
    return false
  }
  if (address.value.length < 5) {
    errors.value.address = '주소는 5자 이상 입력해주세요.'
    return false
  }
  errors.value.address = ''
  return true
}

const validateTerms = () => {
  if (!termsAgreed.value || !privacyAgreed.value) {
    errors.value.terms = '필수 약관에 동의해주세요.'
    return false
  }
  errors.value.terms = ''
  return true
}

// Computed properties
const isFormValid = computed(() => {
  return validateAdminId() && 
         validatePassword() && 
         validateConfirmPassword() && 
         validateName() && 
         validatePhone() && 
         validateEmail() &&
         validateAddress()
})

const isTermsValid = computed(() => {
  return termsAgreed.value && privacyAgreed.value
})

// Event handlers
const handleAllTermsChange = () => {
  if (allTermsAgreed.value) {
    termsAgreed.value = true
    privacyAgreed.value = true
  } else {
    termsAgreed.value = false
    privacyAgreed.value = false
  }
  validateTerms()
}

const handleSignup = async () => {
  // Validate all fields
  const isValid = validateAdminId() && 
                  validatePassword() && 
                  validateConfirmPassword() && 
                  validateName() && 
                  validatePhone() && 
                  validateEmail() && 
                  validateAddress() &&
                  validateTerms()

  if (!isValid) {
    return
  }

  try {
    isLoading.value = true
    
    const signupData = {
      adminLoginId: adminId.value,
      adminLoginPw: password.value,
      adminName: name.value,
      adminPhone: phone.value,
      adminEmail: email.value,
      adminAddress: address.value
    }

    console.log('전송할 회원가입 데이터:', signupData)
    const response = await httpClient.post('v1/admin/api/signup', signupData)
    
    if (response.data.success) {
      alert('관리자 등록이 완료되었습니다. 승인 후 로그인이 가능합니다.')
      router.push('/login')
    } else {
      alert(response.data.message || '등록에 실패했습니다.')
    }
  } catch (error) {
    console.error('Admin signup failed:', error)
    console.log('Error response:', error.response?.data)
    console.log('Error status:', error.response?.status)
    
    if (error.response) {
      if (error.response.status === 409) {
        alert('이미 사용 중인 아이디 또는 이메일입니다.')
      } else if (error.response.status === 400) {
        alert('입력 정보가 올바르지 않습니다.')
      } else if (error.response.status === 500) {
        alert(`서버 내부 오류가 발생했습니다.\n상세 정보: ${error.response.data?.message || '알 수 없는 오류'}`)
      } else {
        alert(error.response.data?.message || '등록 중 오류가 발생했습니다.')
      }
    } else if (error.request) {
      alert('서버와 통신할 수 없습니다. 잠시 후 다시 시도해주세요.')
    } else {
      alert('등록 요청을 보내지 못했습니다. 잠시 후 다시 시도해주세요.')
    }
  } finally {
    isLoading.value = false
  }
}

// Watch for individual field changes
import { watch } from 'vue'

watch(adminId, validateAdminId)
watch(password, () => {
  validatePassword()
  if (confirmPassword.value) validateConfirmPassword()
})
watch(confirmPassword, validateConfirmPassword)
watch(name, validateName)
watch(phone, validatePhone)
watch(email, validateEmail)
watch(address, validateAddress)
watch([termsAgreed, privacyAgreed], () => {
  allTermsAgreed.value = termsAgreed.value && privacyAgreed.value
  validateTerms()
})
</script>

<style scoped>
.admin-signup-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.admin-signup-container {
  width: 100%;
  max-width: 500px;
}

.admin-signup-box {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.admin-signup-form {
  padding: 48px 32px;
}

.admin-signup-title {
  font-size: 32px;
  font-weight: 700;
  text-align: center;
  color: #1f2937;
  margin-bottom: 32px;
  line-height: 1.2;
}

.admin-input-group {
  margin-bottom: 20px;
}

.admin-input {
  width: 100%;
  padding: 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.2s ease;
  background-color: #f9fafb;
  box-sizing: border-box;
}

.admin-input:focus {
  outline: none;
  border-color: #667eea;
  background-color: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.admin-input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  color: #dc2626;
  font-size: 14px;
  margin-top: 4px;
  display: block;
}

.terms-section {
  margin: 24px 0;
  padding: 20px;
  background-color: #f9fafb;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.terms-all {
  margin-bottom: 16px;
}

.terms-divider {
  height: 1px;
  background-color: #e5e7eb;
  margin: 16px 0;
}

.terms-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 14px;
  color: #374151;
}

.checkbox-label input[type="checkbox"] {
  margin-right: 8px;
  cursor: pointer;
}

.terms-detail-button {
  background: none;
  border: 1px solid #d1d5db;
  color: #6b7280;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.terms-detail-button:hover {
  background-color: #f3f4f6;
  border-color: #9ca3af;
}

.admin-signup-button {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 16px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-bottom: 24px;
  box-sizing: border-box;
}

.admin-signup-button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 10px 25px -5px rgba(102, 126, 234, 0.3);
}

.admin-signup-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.admin-login-prompt {
  text-align: center;
  font-size: 14px;
  color: #6b7280;
}

.admin-login-link {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  margin-left: 8px;
  transition: color 0.2s ease;
}

.admin-login-link:hover {
  color: #764ba2;
}

/* Modal styles */
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
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6b7280;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-close:hover {
  color: #374151;
}

.modal-body {
  padding: 24px;
  max-height: 400px;
  overflow-y: auto;
}

.modal-body h3 {
  color: #1f2937;
  font-size: 16px;
  font-weight: 600;
  margin: 16px 0 8px 0;
}

.modal-body p {
  color: #4b5563;
  font-size: 14px;
  line-height: 1.6;
  margin: 8px 0;
}

.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
  text-align: right;
}

.modal-button {
  background: #667eea;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.modal-button:hover {
  background: #5a67d8;
}

/* 반응형 디자인 */
@media (max-width: 640px) {
  .admin-signup-page {
    padding: 16px;
  }
  
  .admin-signup-form {
    padding: 32px 24px;
  }
  
  .admin-signup-title {
    font-size: 28px;
    margin-bottom: 24px;
  }
  
  .terms-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .modal-content {
    width: 95%;
  }
  
  .modal-header {
    padding: 16px;
  }
  
  .modal-body {
    padding: 16px;
  }
  
  .modal-footer {
    padding: 12px 16px;
  }
}
</style> 