<template>
  <div class="contract-create-page">
    <h1>계약서 작성</h1>
    <form @submit.prevent="submitContract">
      <section class="contract-section">
        <h2>광고주(갑) 정보</h2>
        <div class="form-group">
          <label>회사명</label>
          <input v-model="form.advertiser.companyName" required maxlength="100" placeholder="회사명을 입력하세요" />
        </div>
        <div class="form-group">
          <label>주소</label>
          <input v-model="form.advertiser.address" required maxlength="200" placeholder="사업장 주소를 입력하세요" />
        </div>
        <div class="form-group">
          <label>사업자등록번호 <span class="format-hint">(000-00-00000)</span></label>
          <input 
            v-model="form.advertiser.businessNumber" 
            required 
            pattern="[0-9]{3}-[0-9]{2}-[0-9]{5}"
            placeholder="000-00-00000"
            @input="formatBusinessNumber"
            maxlength="12"
            :class="{ 
              'error': errors.businessNumber, 
              'success': form.advertiser.businessNumber && !errors.businessNumber 
            }"
          />
          <span v-if="errors.businessNumber" class="error-text">{{ errors.businessNumber }}</span>
        </div>
        <div class="form-group">
          <label>전화번호 <span class="format-hint">(010-0000-0000)</span></label>
          <input 
            v-model="form.advertiser.phone" 
            required 
            pattern="[0-9]{3}-[0-9]{4}-[0-9]{4}"
            placeholder="010-0000-0000"
            @input="formatPhoneNumber('advertiser')"
            maxlength="13"
            :class="{ 
              'error': errors.advertiserPhone, 
              'success': form.advertiser.phone && !errors.advertiserPhone 
            }"
          />
          <span v-if="errors.advertiserPhone" class="error-text">{{ errors.advertiserPhone }}</span>
        </div>
        <div class="form-group">
          <label>대표자명</label>
          <input v-model="form.advertiser.ceo" required maxlength="50" placeholder="대표자 성명을 입력하세요" />
        </div>
        <div class="form-group">
          <label>광고 계약 금액 <span class="format-hint">(원)</span></label>
          <input 
            v-model="form.advertiser.amount" 
            required 
            type="number" 
            min="10000" 
            max="1000000000"
            step="1000"
            placeholder="10,000 이상 입력하세요"
            @input="validateAmount"
            :class="{ 
              'error': errors.amount, 
              'success': form.advertiser.amount && !errors.amount 
            }"
          />
          <span v-if="errors.amount" class="error-text">{{ errors.amount }}</span>
        </div>
        <div class="form-group">
          <label>캠페인 URL</label>
          <input 
            v-model="form.advertiser.campaignUrl" 
            required 
            type="url"
            pattern="https?://.+"
            placeholder="https://example.com/campaign"
            @blur="validateCampaignUrl"
            :class="{ 
              'error': errors.campaignUrl, 
              'success': form.advertiser.campaignUrl && !errors.campaignUrl 
            }"
          />
          <span v-if="errors.campaignUrl" class="error-text">{{ errors.campaignUrl }}</span>
        </div>
      </section>
      <section class="contract-section">
        <h2>인플루언서(을) 정보</h2>
        <div class="form-group">
          <label>이름</label>
          <input v-model="form.influencer.name" required maxlength="50" placeholder="실명을 입력하세요" />
        </div>
        <div class="form-group">
          <label>채널명</label>
          <input v-model="form.influencer.channel" required maxlength="100" placeholder="유튜브 채널명을 입력하세요" />
        </div>
        <div class="form-group">
          <label>주소</label>
          <input v-model="form.influencer.address" required maxlength="200" placeholder="주민등록상 주소를 입력하세요" />
        </div>
        <div class="form-group">
          <label>전화번호 <span class="format-hint">(010-0000-0000)</span></label>
          <input 
            v-model="form.influencer.phone" 
            required 
            pattern="[0-9]{3}-[0-9]{4}-[0-9]{4}"
            placeholder="010-0000-0000"
            @input="formatPhoneNumber('influencer')"
            maxlength="13"
            :class="{ 
              'error': errors.influencerPhone, 
              'success': form.influencer.phone && !errors.influencerPhone 
            }"
          />
          <span v-if="errors.influencerPhone" class="error-text">{{ errors.influencerPhone }}</span>
        </div>
        <div class="form-group">
          <label>은행명</label>
          <select 
            v-model="form.influencer.bankName" 
            required
            :class="{ 
              'success': form.influencer.bankName 
            }"
          >
            <option value="">은행을 선택하세요</option>
            <option value="국민은행">국민은행</option>
            <option value="신한은행">신한은행</option>
            <option value="우리은행">우리은행</option>
            <option value="하나은행">하나은행</option>
            <option value="농협은행">농협은행</option>
            <option value="기업은행">기업은행</option>
            <option value="새마을금고">새마을금고</option>
            <option value="카카오뱅크">카카오뱅크</option>
            <option value="토스뱅크">토스뱅크</option>
            <option value="케이뱅크">케이뱅크</option>
            <option value="SC제일은행">SC제일은행</option>
            <option value="씨티은행">씨티은행</option>
            <option value="경남은행">경남은행</option>
            <option value="광주은행">광주은행</option>
            <option value="대구은행">대구은행</option>
            <option value="부산은행">부산은행</option>
            <option value="전북은행">전북은행</option>
            <option value="제주은행">제주은행</option>
            <option value="수협은행">수협은행</option>
            <option value="산업은행">산업은행</option>
            <option value="우체국">우체국</option>
            <option value="기타">기타</option>
          </select>
        </div>
        <div class="form-group">
          <label>계좌번호 <span class="format-hint">(숫자와 하이픈만)</span></label>
          <input 
            v-model="form.influencer.accountNumber" 
            required 
            maxlength="30"
            placeholder="예: 123456-12-123456"
            @input="formatAccountNumber"
            @blur="validateAccountNumber"
            :class="{ 
              'error': errors.accountNumber, 
              'success': form.influencer.accountNumber && !errors.accountNumber 
            }"
          />
          <span v-if="errors.accountNumber" class="error-text">{{ errors.accountNumber }}</span>
        </div>
        <div class="form-group">
          <label>광고 업로드 예정일</label>
          <input 
            v-model="form.influencer.uploadDate" 
            required 
            type="date" 
            :min="minUploadDate"
            :max="maxUploadDate"
            :class="{ 
              'success': form.influencer.uploadDate 
            }"
          />
          <span v-if="errors.uploadDate" class="error-text">{{ errors.uploadDate }}</span>
        </div>
        <div class="form-group">
          <label>계약 이행 조건</label>
          <textarea 
            v-model="form.influencer.condition" 
            required 
            maxlength="1000"
            rows="5"
            placeholder="광고 내용, 업로드 조건, 유지 기간 등을 구체적으로 작성하세요"
          />
          <div class="char-count">{{ form.influencer.condition.length }}/1000</div>
        </div>
      </section>
      <div class="button-group">
        <button type="submit" class="submit-btn" :disabled="!isFormValid">계약서 저장</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { contractApi } from '@/api/advertiser/advertiser-contract'

const router = useRouter()
const route = useRoute()

const form = ref({
  advertiser: {
    companyName: '',
    address: '',
    businessNumber: '',
    phone: '',
    ceo: '',
    amount: '',
    campaignUrl: ''
  },
  influencer: {
    name: '',
    channel: '',
    address: '',
    phone: '',
    bankName: '',
    accountNumber: '',
    uploadDate: '',
    condition: ''
  }
})

const errors = ref({
  businessNumber: '',
  advertiserPhone: '',
  influencerPhone: '',
  amount: '',
  campaignUrl: '',
  accountNumber: '',
  uploadDate: ''
})

// 날짜 제한 (내일부터 3개월 후까지)
const minUploadDate = computed(() => {
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  return tomorrow.toISOString().split('T')[0]
})

const maxUploadDate = computed(() => {
  const threeMonthsLater = new Date()
  threeMonthsLater.setMonth(threeMonthsLater.getMonth() + 3)
  return threeMonthsLater.toISOString().split('T')[0]
})

// 폼 유효성 검사
const isFormValid = computed(() => {
  const hasNoErrors = Object.values(errors.value).every(error => error === '')
  const hasAllRequiredFields = form.value.advertiser.companyName && 
    form.value.advertiser.address && 
    form.value.advertiser.businessNumber && 
    form.value.advertiser.phone && 
    form.value.advertiser.ceo && 
    form.value.advertiser.amount && 
    form.value.advertiser.campaignUrl &&
    form.value.influencer.name && 
    form.value.influencer.channel && 
    form.value.influencer.address && 
    form.value.influencer.phone && 
    form.value.influencer.bankName && 
    form.value.influencer.accountNumber && 
    form.value.influencer.uploadDate && 
    form.value.influencer.condition
  
  return hasNoErrors && hasAllRequiredFields
})

// 사업자등록번호 포맷팅
function formatBusinessNumber() {
  let value = form.value.advertiser.businessNumber.replace(/\D/g, '')
  if (value.length <= 3) {
    form.value.advertiser.businessNumber = value
  } else if (value.length <= 5) {
    form.value.advertiser.businessNumber = `${value.slice(0, 3)}-${value.slice(3)}`
  } else {
    form.value.advertiser.businessNumber = `${value.slice(0, 3)}-${value.slice(3, 5)}-${value.slice(5, 10)}`
  }
  
  // 유효성 검사
  if (value.length === 10 && !isValidBusinessNumber(value)) {
    errors.value.businessNumber = '올바르지 않은 사업자등록번호입니다.'
  } else {
    errors.value.businessNumber = ''
  }
}

// 사업자등록번호 검증 알고리즘
function isValidBusinessNumber(number) {
  const weights = [1, 3, 7, 1, 3, 7, 1, 3, 5, 1]
  let sum = 0
  for (let i = 0; i < 9; i++) {
    sum += parseInt(number[i]) * weights[i]
  }
  sum += Math.floor((parseInt(number[8]) * 5) / 10)
  const checkDigit = (10 - (sum % 10)) % 10
  return checkDigit === parseInt(number[9])
}

// 전화번호 포맷팅
function formatPhoneNumber(type) {
  const phoneField = type === 'advertiser' ? 'advertiser' : 'influencer'
  let value = form.value[phoneField].phone.replace(/\D/g, '')
  
  if (value.length <= 3) {
    form.value[phoneField].phone = value
  } else if (value.length <= 7) {
    form.value[phoneField].phone = `${value.slice(0, 3)}-${value.slice(3)}`
  } else {
    form.value[phoneField].phone = `${value.slice(0, 3)}-${value.slice(3, 7)}-${value.slice(7, 11)}`
  }
  
  // 유효성 검사
  if (value.length === 11 && !value.startsWith('010')) {
    errors.value[`${phoneField}Phone`] = '010으로 시작하는 번호만 입력 가능합니다.'
  } else if (value.length > 0 && value.length < 11) {
    errors.value[`${phoneField}Phone`] = '11자리 전화번호를 입력하세요.'
  } else {
    errors.value[`${phoneField}Phone`] = ''
  }
}

// 금액 유효성 검사
function validateAmount() {
  const amount = parseInt(form.value.advertiser.amount)
  if (amount < 10000) {
    errors.value.amount = '최소 10,000원 이상 입력하세요.'
  } else if (amount > 1000000000) {
    errors.value.amount = '10억원을 초과할 수 없습니다.'
  } else {
    errors.value.amount = ''
  }
}

// 계좌번호 포맷팅
function formatAccountNumber() {
  // 숫자와 하이픈만 허용
  let value = form.value.influencer.accountNumber.replace(/[^0-9-]/g, '')
  form.value.influencer.accountNumber = value
}

// 계좌 유효성 검사
function validateAccountNumber() {
  const account = form.value.influencer.accountNumber
  const accountPattern = /^[0-9-]{10,30}$/
  
  if (account.length < 10) {
    errors.value.accountNumber = '계좌번호는 최소 10자리 이상이어야 합니다.'
  } else if (!accountPattern.test(account)) {
    errors.value.accountNumber = '숫자와 하이픈(-)만 입력 가능합니다.'
  } else {
    errors.value.accountNumber = ''
  }
}

// 캠페인 URL 유효성 검사
function validateCampaignUrl() {
  const url = form.value.advertiser.campaignUrl
  const urlPattern = /^https?:\/\/.+\..+/
  if (!urlPattern.test(url)) {
    errors.value.campaignUrl = '올바른 URL 형식을 입력하세요. (예: https://example.com)'
  } else {
    errors.value.campaignUrl = ''
  }
}

async function submitContract() {
  try {
    const proposalId = route.query.proposalId
    if (!proposalId) {
      alert('제안서 정보가 없습니다.')
      return
    }
    
    // 최종 유효성 검사
    if (!isFormValid.value) {
      alert('모든 필드를 올바르게 입력해주세요.')
      return
    }
    
    // 계약서 생성 API 호출
    const response = await contractApi.startContract(proposalId)
    alert('계약서가 저장되었습니다!')
    router.push('/mypage/advertiser/contracts')
  } catch (error) {
    console.error('계약서 저장 중 오류가 발생했습니다:', error)
    alert('계약서 저장에 실패했습니다. 다시 시도해주세요.')
  }
}
</script>

<style scoped>
.contract-create-page {
  max-width: 800px;
  margin: 40px auto;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(124,58,237,0.12);
  padding: 40px 32px;
  border: 1px solid rgba(124,58,237,0.08);
}

.contract-create-page h1 {
  text-align: center;
  color: #1a1a1a;
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 8px;
}

.contract-section {
  margin-bottom: 40px;
}

.contract-section h2 {
  color: #7c3aed;
  font-size: 1.3rem;
  font-weight: 600;
  margin-bottom: 24px;
  padding-bottom: 8px;
  border-bottom: 2px solid rgba(124,58,237,0.1);
}

.form-group {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-weight: 600;
  margin-bottom: 8px;
  color: #374151;
  font-size: 0.95rem;
}

.form-group input, 
.form-group textarea,
.form-group select {
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  padding: 14px 16px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: #fafafa;
  color: #1f2937;
}

.form-group input:focus, 
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #7c3aed;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(124,58,237,0.1);
  transform: translateY(-1px);
}

.form-group input:hover:not(:focus), 
.form-group textarea:hover:not(:focus),
.form-group select:hover:not(:focus) {
  border-color: #d1d5db;
  background: #fff;
}

.form-group input.error, 
.form-group textarea.error,
.form-group select.error {
  border-color: #fca5a5;
  background: #fef2f2;
}

.form-group input.success, 
.form-group textarea.success,
.form-group select.success {
  border-color: #86efac;
  background: #f0fdf4;
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: #9ca3af;
  font-style: italic;
}

.form-group select option {
  background: #fff;
  color: #1f2937;
  padding: 8px;
}

.format-hint {
  color: #7c3aed;
  font-size: 0.85rem;
  font-weight: 500;
  opacity: 0.8;
}

.error-text {
  color: #ef4444;
  font-size: 0.85rem;
  margin-top: 6px;
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
}

.error-text::before {
  content: "⚠";
  font-size: 0.9rem;
}

.char-count {
  text-align: right;
  font-size: 0.8rem;
  color: #6b7280;
  margin-top: 6px;
  font-weight: 500;
}

.button-group {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid rgba(124,58,237,0.1);
}

.submit-btn {
  background: linear-gradient(135deg, #7c3aed 0%, #a855f7 100%);
  color: #fff;
  border: none;
  border-radius: 16px;
  padding: 16px 48px;
  font-size: 1.1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(124,58,237,0.3);
  letter-spacing: 0.025em;
}

.submit-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #6d28d9 0%, #9333ea 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(124,58,237,0.4);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 4px 20px rgba(124,58,237,0.3);
}

.submit-btn:disabled {
  background: linear-gradient(135deg, #d1d5db 0%, #e5e7eb 100%);
  color: #9ca3af;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.submit-btn:disabled:hover {
  background: linear-gradient(135deg, #d1d5db 0%, #e5e7eb 100%);
  transform: none;
  box-shadow: none;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .contract-create-page {
    margin: 20px;
    padding: 24px 20px;
    border-radius: 16px;
  }
  
  .contract-create-page h1 {
    font-size: 1.6rem;
  }
  
  .contract-section h2 {
    font-size: 1.1rem;
  }
  
  .submit-btn {
    padding: 14px 32px;
    font-size: 1rem;
  }
}

/* 다크모드 호환 */
@media (prefers-color-scheme: dark) {
  .contract-create-page {
    background: #1f2937;
    border-color: rgba(124,58,237,0.2);
  }
  
  .contract-create-page h1 {
    color: #f9fafb;
  }
  
  .form-group label {
    color: #e5e7eb;
  }
  
  .form-group input, 
  .form-group textarea,
  .form-group select {
    background: #374151;
    border-color: #4b5563;
    color: #f9fafb;
  }
  
  .form-group input:focus, 
  .form-group textarea:focus,
  .form-group select:focus {
    background: #4b5563;
  }
}

/* 애니메이션 효과 */
.form-group {
  animation: slideInUp 0.5s ease-out;
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 성공/오류 상태 개선 */
.form-group input.success:focus {
  box-shadow: 0 0 0 3px rgba(34,197,94,0.1);
}

.form-group input.error:focus {
  box-shadow: 0 0 0 3px rgba(239,68,68,0.1);
}
</style> 