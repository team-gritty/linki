<template>
  <div class="contract-create-page">
    <h1>계약서 작성</h1>
    <form @submit.prevent="submitContract">
      <section class="contract-section">
        <h2>광고주 정보</h2>
        <div class="form-group">
          <label>광고주 주소</label>
          <div class="address-input-group">
            <input 
              v-model="form.advertiserAddress" 
              required 
              maxlength="200" 
              placeholder="주소 검색을 클릭하세요"
              readonly
              class="address-display"
            />
            <button type="button" @click="openAddressSearch('advertiser')" class="address-search-btn">
              주소 검색
            </button>
          </div>
        </div>
      </section>

      <section class="contract-section">
        <h2>인플루언서 정보</h2>
        <div class="form-group">
          <label>인플루언서 주소</label>
          <div class="address-input-group">
            <input 
              v-model="form.influencerAddress" 
              required 
              maxlength="200" 
              placeholder="주소 검색을 클릭하세요"
              readonly
              class="address-display"
            />
            <button type="button" @click="openAddressSearch('influencer')" class="address-search-btn">
              주소 검색
            </button>
          </div>
        </div>
      </section>

      <section class="contract-section">
        <h2>계약 조건</h2>
        <div class="form-group">
          <label>계약 금액 <span class="format-hint">(원)</span></label>
          <input 
            v-model="form.contractAmount" 
            required 
            type="number" 
            min="10000" 
            max="1000000000"
            step="1000"
            placeholder="10,000 이상 입력하세요"
            @input="validateAmount"
            :class="{ 
              'error': errors.amount, 
              'success': form.contractAmount && !errors.amount 
            }"
          />
          <span v-if="errors.amount" class="error-text">{{ errors.amount }}</span>
        </div>

        <div class="date-group">
          <div class="form-group">
            <label>계약 시작일</label>
            <input 
              v-model="form.contractStartDate" 
              required 
              type="date" 
              :min="minStartDate"
              :max="maxStartDate"
              @change="validateDateRange"
              :class="{ 
                'error': errors.startDate,
                'success': form.contractStartDate && !errors.startDate 
              }"
            />
            <span v-if="errors.startDate" class="error-text">{{ errors.startDate }}</span>
          </div>

          <div class="form-group">
            <label>계약 종료일</label>
            <input 
              v-model="form.contractEndDate" 
              required 
              type="date" 
              :min="form.contractStartDate || minStartDate"
              :max="maxEndDate"
              @change="validateDateRange"
              :class="{ 
                'error': errors.endDate,
                'success': form.contractEndDate && !errors.endDate 
              }"
            />
            <span v-if="errors.endDate" class="error-text">{{ errors.endDate }}</span>
          </div>
        </div>

        <div class="form-group">
          <label>광고 이행 URL</label>
          <input 
            v-model="form.adDeliveryUrl" 
            required 
            type="url"
            pattern="https?://.+"
            placeholder="https://example.com/video"
            @blur="validateAdDeliveryUrl"
            :class="{ 
              'error': errors.adDeliveryUrl, 
              'success': form.adDeliveryUrl && !errors.adDeliveryUrl 
            }"
          />
          <span v-if="errors.adDeliveryUrl" class="error-text">{{ errors.adDeliveryUrl }}</span>
          <small class="field-description">광고 영상이 업로드될 URL을 입력하세요</small>
        </div>

        <div class="form-group">
          <label>특약사항 (광고 조건)</label>
          <textarea 
            v-model="form.contractSpecialTerms" 
            required 
            maxlength="1000"
            rows="6"
            placeholder="광고 내용, 업로드 조건, 유지 기간, 특별 요구사항 등을 구체적으로 작성하세요"
          />
          <div class="char-count">{{ form.contractSpecialTerms.length }}/1000</div>
        </div>
      </section>

      <div class="button-group">
        <button type="button" @click="goBack" class="cancel-btn">취소</button>
        <button type="submit" class="submit-btn" :disabled="!isFormValid">계약서 저장</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { contractApi } from '@/api/advertiser/advertiser-contract'

const router = useRouter()
const route = useRoute()

const form = ref({
  advertiserAddress: '',
  influencerAddress: '',
  contractAmount: '',
  contractStartDate: '',
  contractEndDate: '',
  adDeliveryUrl: '',
  contractSpecialTerms: ''
})

const errors = ref({
  amount: '',
  startDate: '',
  endDate: '',
  adDeliveryUrl: ''
})

// 날짜 제한 (오늘부터 1년 후까지)
const minStartDate = computed(() => {
  const today = new Date()
  return today.toISOString().split('T')[0]
})

const maxStartDate = computed(() => {
  const oneYearLater = new Date()
  oneYearLater.setFullYear(oneYearLater.getFullYear() + 1)
  return oneYearLater.toISOString().split('T')[0]
})

const maxEndDate = computed(() => {
  const oneYearLater = new Date()
  oneYearLater.setFullYear(oneYearLater.getFullYear() + 1)
  return oneYearLater.toISOString().split('T')[0]
})

// 폼 유효성 검사
const isFormValid = computed(() => {
  const hasNoErrors = Object.values(errors.value).every(error => error === '')
  const hasAllRequiredFields = form.value.advertiserAddress && 
    form.value.influencerAddress && 
    form.value.contractAmount && 
    form.value.contractStartDate && 
    form.value.contractEndDate && 
    form.value.adDeliveryUrl &&
    form.value.contractSpecialTerms
  
  return hasNoErrors && hasAllRequiredFields
})

// 카카오 주소 API 스크립트 로드
const loadKakaoScript = () => {
  return new Promise((resolve, reject) => {
    if (window.daum && window.daum.Postcode) {
      resolve()
      return
    }

    const script = document.createElement('script')
    script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'
    script.onload = () => resolve()
    script.onerror = () => reject(new Error('카카오 주소 API 로드 실패'))
    document.head.appendChild(script)
  })
}

// 주소 검색 팝업 열기
const openAddressSearch = async (type) => {
  try {
    await loadKakaoScript()
    
    new window.daum.Postcode({
      oncomplete: function(data) {
        let addr = '' // 주소 변수
        
        // 사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
        if (data.userSelectedType === 'R') { // 도로명 주소 선택
          addr = data.roadAddress
        } else { // 지번 주소 선택
          addr = data.jibunAddress
        }
        
        // 상세주소 입력을 위한 프롬프트
        const detailAddr = prompt('상세주소를 입력하세요 (선택사항):')
        const fullAddr = detailAddr ? `${addr} ${detailAddr}` : addr
        
        if (type === 'advertiser') {
          form.value.advertiserAddress = fullAddr
        } else {
          form.value.influencerAddress = fullAddr
        }
      },
      width: '100%',
      height: '100%'
    }).open()
  } catch (error) {
    console.error('주소 검색 오류:', error)
    alert('주소 검색 서비스를 불러올 수 없습니다. 잠시 후 다시 시도해주세요.')
  }
}

// 금액 유효성 검사
const validateAmount = () => {
  const amount = parseInt(form.value.contractAmount)
  if (amount < 10000) {
    errors.value.amount = '최소 10,000원 이상 입력하세요.'
  } else if (amount > 1000000000) {
    errors.value.amount = '10억원을 초과할 수 없습니다.'
  } else {
    errors.value.amount = ''
  }
}

// 날짜 범위 유효성 검사
const validateDateRange = () => {
  if (form.value.contractStartDate && form.value.contractEndDate) {
    const startDate = new Date(form.value.contractStartDate)
    const endDate = new Date(form.value.contractEndDate)
    
    if (startDate >= endDate) {
      errors.value.endDate = '종료일은 시작일보다 늦어야 합니다.'
    } else {
      errors.value.startDate = ''
      errors.value.endDate = ''
    }
  }
}

// 광고 이행 URL 유효성 검사
const validateAdDeliveryUrl = () => {
  const url = form.value.adDeliveryUrl
  const urlPattern = /^https?:\/\/.+\..+/
  if (!urlPattern.test(url)) {
    errors.value.adDeliveryUrl = '올바른 URL 형식을 입력하세요. (예: https://youtube.com/watch?v=...)'
  } else {
    errors.value.adDeliveryUrl = ''
  }
}

const goBack = () => {
  router.go(-1)
}

const submitContract = async () => {
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
    
    // 백엔드 DTO 구조에 맞게 데이터 구성
    const contractData = {
      proposalId: proposalId,
      advertiserAddress: form.value.advertiserAddress,
      influencerAddress: form.value.influencerAddress,
      contractAmount: form.value.contractAmount,
      contractStartDate: form.value.contractStartDate,
      contractEndDate: form.value.contractEndDate,
      adDeliveryUrl: form.value.adDeliveryUrl,
      contractSpecialTerms: form.value.contractSpecialTerms,
      // DTO에 있는 다른 필드들 (필요시 기본값 설정)
      redirectUrl: '',
      advertiserName: '',
      influencerName: '',
      businessNumber: ''
    }
    
    console.log('Sending contract data:', contractData)
    const response = await contractApi.startContract(contractData)
    
    if (response && response.data) {
      alert('계약서가 성공적으로 저장되었습니다!')
      router.push('/mypage/advertiser/contracts')
    } else {
      alert('계약서가 저장되었습니다!')
      router.push('/mypage/advertiser/contracts')
    }
  } catch (error) {
    console.error('계약서 저장 중 오류가 발생했습니다:', error)
    
    // 중복 계약서 생성 에러 처리
    if (error.response?.status === 500 && 
        (error.response?.data?.message?.includes('이미 해당 제안서에 대한 계약이 존재합니다') ||
         error.response?.data?.message === 'Server error')) {
      alert('이미 해당 제안서에 대한 계약서가 존재합니다.\n기존 계약서를 확인해주세요.')
      // 계약 관리 페이지로 이동
      router.push('/mypage/advertiser/contracts')
    }
    // 명시적인 중복 계약 에러 메시지가 온 경우
    else if ((error.response?.status === 400 || error.response?.status === 500) && 
             (error.response?.data?.message?.includes('이미 해당 제안서에 대한 계약이 존재합니다') ||
              error.response?.data?.message?.includes('이미') && error.response?.data?.message?.includes('계약'))) {
      alert('이미 해당 제안서에 대한 계약서가 존재합니다.\n기존 계약서를 확인해주세요.')
      // 계약 관리 페이지로 이동
      router.push('/mypage/advertiser/contracts')
    }
    // 기타 에러
    else {
      // 백엔드에서 온 구체적인 에러 메시지가 있으면 표시, 없으면 기본 메시지
      const errorMessage = error.response?.data?.message || '계약서 저장에 실패했습니다. 다시 시도해주세요.'
      alert(errorMessage)
    }
  }
}

onMounted(() => {
  // 컴포넌트 마운트 시 카카오 API 미리 로드
  loadKakaoScript().catch(console.error)
})
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
.form-group textarea {
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  padding: 14px 16px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: #fafafa;
  color: #1f2937;
}

.form-group input:focus, 
.form-group textarea:focus {
  outline: none;
  border-color: #7c3aed;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(124,58,237,0.1);
  transform: translateY(-1px);
}

.form-group input.error, 
.form-group textarea.error {
  border-color: #fca5a5;
  background: #fef2f2;
}

.form-group input.success, 
.form-group textarea.success {
  border-color: #86efac;
  background: #f0fdf4;
}

.address-input-group {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.address-display {
  flex: 1;
}

.address-search-btn {
  background: #7c3aed;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 14px 20px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.address-search-btn:hover {
  background: #6d28d9;
  transform: translateY(-1px);
}

.date-group {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.field-description {
  color: #6b7280;
  font-size: 0.85rem;
  margin-top: 6px;
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
  gap: 16px;
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid rgba(124,58,237,0.1);
}

.cancel-btn {
  background: #f3f4f6;
  color: #374151;
  border: none;
  border-radius: 16px;
  padding: 16px 32px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background: #e5e7eb;
  transform: translateY(-1px);
}

.submit-btn {
  background: linear-gradient(135deg, #7c3aed 0%, #a855f7 100%);
  color: #fff;
  border: none;
  border-radius: 16px;
  padding: 16px 48px;
  font-size: 1rem;
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

.submit-btn:disabled {
  background: linear-gradient(135deg, #d1d5db 0%, #e5e7eb 100%);
  color: #9ca3af;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .contract-create-page {
    margin: 20px;
    padding: 24px 20px;
    border-radius: 16px;
  }
  
  .date-group {
    grid-template-columns: 1fr;
  }
  
  .address-input-group {
    flex-direction: column;
  }
  
  .address-search-btn {
    align-self: flex-start;
  }
  
  .button-group {
    flex-direction: column;
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
</style> 