<template>
  <div class="contract-detail-content">
    <div v-if="loading" class="loading">
      <p>계약 정보를 불러오는 중입니다...</p>
    </div>
    <div v-else-if="!contract">
      <p>계약 정보가 없습니다.</p>
    </div>
    <div v-else>
      <div class="page-header">
        <div class="header-content">
          <h2>{{ contract.contractTitle }}</h2>
          <button class="back-button" @click="goBackToList">
            계약서 목록
          </button>
        </div>
        <div v-if="contract.contractStatus" class="status-badge" :class="getStatusClass(contract.contractStatus)">
          {{ getStatusText(contract.contractStatus) }}
        </div>
      </div>

      <div class="contract-info-section">
        <h2 class="section-title">계약 정보</h2>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">계약 ID</span>
            <span class="value">{{ contract.contractId }}</span>
          </div>
          <div class="info-item">
            <span class="label">계약명</span>
            <span class="value">{{ contract.contractTitle }}</span>
          </div>
          <div class="info-item">
            <span class="label">계약 기간</span>
            <span class="value">{{ formatDate(contract.contractStartDate) }} ~ {{ formatDate(contract.contractEndDate) }}</span>
          </div>
          <div class="info-item">
            <span class="label">계약 금액</span>
            <span class="value amount">{{ formatAmount(contract.contractAmount) }}원</span>
          </div>
        </div>
      </div>

      <div class="contract-actions">
        <button class="action-button view-contract" @click="viewContractDocument">
          <i class="fas fa-file-contract"></i>
          계약서 조회
        </button>
        <button 
          v-if="contract.contractStatus === 'PENDING_SIGN'"
          class="action-button sign-contract" 
          @click="signContract"
        >
          <i class="fas fa-signature"></i>
          전자 서명
        </button>
      </div>

      <!-- 광고 이행 확인 버튼: 진행중인 계약서(PENDING)에서만 노출 -->
      <div v-if="contract.contractStatus === 'PENDING'" class="ad-execution-section">
        <ContractExecutionButton
          :contract-id="contract.contractId"
          :is-executed="contract.isExecuted"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineEmits, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { contractApi } from '@/api/advertiser/advertiser-contract'
import ContractExecutionButton from '../ContractExecutionButton.vue'

const emit = defineEmits(['back'])
const route = useRoute()
const router = useRouter()

// 계약 상세 정보
const contract = ref(null)
const loading = ref(false)

const fetchContractDetail = async () => {
  try {
    console.log('=== fetchContractDetail 시작 ===')
    loading.value = true
    const contractId = route.query.contractId
    
    console.log('Route params:', route.params)
    console.log('Route query:', route.query)
    console.log('Contract ID from query:', contractId)
    
    if (!contractId) {
      console.error('Contract ID not found in query parameters')
      return
    }
    
    console.log('Calling contractApi.getContractDetail with:', contractId)
    console.log('API function exists:', typeof contractApi.getContractDetail)
    
    const response = await contractApi.getContractDetail(contractId)
    contract.value = response
    console.log('Contract detail API response:', response)
    
    // campaignId가 URL에 없거나 잘못된 경우 URL 업데이트
    if (response.campaignId && (route.params.id === 'undefined' || route.params.id === 'unknown' || route.params.id === 'temp')) {
      console.log('Updating URL with correct campaignId:', response.campaignId)
      // 현재 URL을 올바른 campaignId로 업데이트
      router.replace({
        path: `/mypage/campaign-detail/${response.campaignId}`,
        query: route.query
      })
    }
  } catch (error) {
    console.error('Error in fetchContractDetail:', error)
    console.error('Error message:', error.message)
    console.error('Error stack:', error.stack)
  } finally {
    loading.value = false
    console.log('=== fetchContractDetail 종료 ===')
  }
}

onMounted(() => {
  console.log('DetailContract onMounted - route:', route.path, route.query)
  fetchContractDetail()
})

function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`
}

function formatAmount(amount) {
  if (!amount) return '0'
  return new Intl.NumberFormat('ko-KR').format(amount)
}

function getStatusClass(status) {
  return {
    'status-ongoing': status === 'ONGOING',
    'status-pending-sign': status === 'PENDING_SIGN',
    'status-completed': status === 'COMPLETED'
  }
}

function getStatusText(status) {
  const statusMap = {
    'ONGOING': '진행중',
    'PENDING_SIGN': '서명 대기중',
    'COMPLETED': '완료'
  }
  return statusMap[status] || status
}

async function viewContractDocument() {
  // 실제 API 연동 필요시 contractApi.getContractDocument(props.contract.contractId)
  alert('계약서 조회 기능은 추후 구현 예정입니다.')
}

async function signContract() {
  // 실제 API 연동 필요시 contractApi.signContract(props.contract.contractId)
  alert('전자 서명 기능은 추후 구현 예정입니다.')
}

function goBackToList() {
  // contractId 파라미터를 제거하여 목록으로 돌아가기
  router.push({
    path: route.path,
    query: { 
      ...route.query,
      contractId: undefined  // contractId 제거
    }
  })
}
</script>

<style scoped>
.contract-detail-content {
  padding: 32px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 24px;
}

.header-content h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.back-button {
  padding: 8px 16px;
  background-color: #f3f4f6;
  border: none;
  border-radius: 8px;
  color: #4b5563;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.back-button:hover {
  background-color: #e5e7eb;
}

.contract-info-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.label {
  font-size: 14px;
  color: #666;
}

.value {
  font-size: 16px;
  color: #1a1a1a;
  font-weight: 500;
}

.value.amount {
  color: #6c5ce7;
}

.contract-actions {
  display: flex;
  gap: 16px;
  margin-top: 32px;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.action-button i {
  font-size: 18px;
}

.view-contract {
  background-color: #f8fafc;
  color: #1a1a1a;
}

.view-contract:hover {
  background-color: #e2e8f0;
}

.sign-contract {
  background-color: #3b82f6;
  color: white;
}

.sign-contract:hover {
  background-color: #2563eb;
}

.sign-contract:disabled {
  background-color: #e2e8f0;
  color: #94a3b8;
  cursor: not-allowed;
}

.status-badge {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.status-ongoing {
  background-color: #F3F0FF;
  color: #7B21E8;
}

.status-pending-sign {
  background-color: #FEF3C7;
  color: #D97706;
}

.status-completed {
  background-color: #F3F4F6;
  color: #4B5563;
}

.ad-execution-section {
  margin-top: 32px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .contract-detail-content {
    padding: 16px;
  }

  .contract-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .contract-actions {
    flex-direction: column;
  }

  .action-button {
    width: 100%;
    justify-content: center;
  }
}
</style> 