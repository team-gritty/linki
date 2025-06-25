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
          <i class="fas fa-download"></i>
          계약서 다운로드
        </button>
      </div>

      <!-- 광고 이행 확인 버튼: 완료된 계약서(COMPLETED)에서만 노출 -->
      <div v-if="contract.contractStatus === 'COMPLETED' " class="ad-execution-section">
        <ContractExecutionButton
          :contract-id="contract.contractId"
          :is-executed="contract.isExecuted"
          @execution-completed="handleExecutionCompleted"
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
  try {
    console.log('계약서 조회 시작, contractId:', contract.value.contractId)
    
    const response = await contractApi.getContractDocument(contract.value.contractId)
    console.log('API 응답:', response)
    
    // contract.js API 응답 구조에 맞춰 URL 추출: response.result.file
    const url = response?.result?.file
    console.log('추출된 URL:', url)
    
    if (!url) {
      console.error('URL을 찾을 수 없습니다. 응답 구조:', response)
      alert('계약서 URL을 찾을 수 없습니다.')
      return
    }
    
    // 새 탭에서 계약서 열기
    console.log('새 탭에서 열 URL:', url)
    window.open(url, '_blank')
    console.log('계약서 조회 성공: 새 탭에서 열림')
    
  } catch (error) {
    console.error('계약서 조회 실패:', error)
    alert('계약서 조회에 실패했습니다.')
  }
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

// 광고 이행 완료 이벤트 핸들러
function handleExecutionCompleted(data) {
  console.log('광고 이행 완료 이벤트 받음:', data)
  
  // 백엔드에서 isExecuted를 DTO로 반환하지 않으므로
  // 프론트엔드에서 직접 상태 업데이트
  if (contract.value) {
    contract.value.isExecuted = true
    console.log('계약 isExecuted 상태를 true로 업데이트')
  }
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