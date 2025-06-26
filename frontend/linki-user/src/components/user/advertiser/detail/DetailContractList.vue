<template>
  <div class="contract-list-content">
    <h1 class="content-title">계약서 목록</h1>
    <div class="content-box">
      <div v-if="contracts.length === 0" class="no-contracts">
        <p>등록된 계약서가 없습니다.</p>
      </div>
      <div v-else class="contracts-list">
        <div v-for="contract in contracts" :key="contract.contractId" class="contract-item" @click="viewContractDetail(contract)" @mouseenter="hovered = contract.contractId" @mouseleave="hovered = null" :style="hovered === contract.contractId ? 'box-shadow: 0 4px 16px rgba(140,48,245,0.08); transform: translateY(-4px);' : ''">
          <div class="contract-header">
            <h3 class="contract-title">{{ contract.contractTitle }}</h3>
            <span class="status-badge" :class="getStatusClass(contract.contractStatus)">
              {{ getStatusText(contract.contractStatus) }}
            </span>
          </div>
          <div class="contract-details">
            <div class="detail-group">
              <div class="detail-item">
                <span class="label">계약 기간</span>
                <span class="value">{{ formatDate(contract.contractStartDate) }} ~ {{ formatDate(contract.contractEndDate) }}</span>
              </div>
              <div class="detail-item">
                <span class="label">계약금액</span>
                <span class="value amount">{{ formatAmount(contract.contractAmount) }}원</span>
              </div>
            </div>
            <button class="detail-btn" @click.stop="viewContractDetail(contract)">상세조회</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { contractApi } from '@/api/advertiser/advertiser-contract'
import { useRoute, useRouter } from 'vue-router'

const props = defineProps({
  campaignId: {
    type: String,
    required: true
  }
})

const route = useRoute()
const router = useRouter()
const contracts = ref([])
const hovered = ref(null)

const fetchContracts = async () => {
  try {
    console.log('=== DetailContractList fetchContracts 시작 ===')
    console.log('campaignId:', props.campaignId)
    
    const response = await contractApi.getMyContracts(['ONGOING', 'PENDING_SIGN', 'COMPLETED'])
    console.log('getMyContracts response:', response)
    
    let contractList
    if (Array.isArray(response.data)) {
      contractList = response.data
    } else if (Array.isArray(response)) {
      contractList = response
    } else {
      contractList = []
    }
    
    console.log('contractList before filter:', contractList)
    
    // 해당 캠페인의 계약서만 필터링
    contracts.value = contractList
      .filter(contract => contract.campaignId === props.campaignId)
      .map(contract => ({
        contractId: contract.contractId,
        contractTitle: contract.contractTitle,
        contractStatus: contract.contractStatus,
        contractStartDate: contract.contractStartDate,
        contractEndDate: contract.contractEndDate,
        contractAmount: contract.contractAmount,
        ...contract
      }))
    
    console.log('contracts.value after filter:', contracts.value)
  } catch (error) {
    console.error('Error in fetchContracts:', error)
    contracts.value = []
  }
}

// 계약서 상세보기로 이동 (DetailContract 컴포넌트 사용)
function viewContractDetail(contract) {
  console.log('Viewing contract detail:', contract.contractId)
  
  // URL 업데이트하여 DetailContract가 표시되도록
  router.push({
    path: route.path,
    query: { 
      ...route.query,
      tab: 'contract',  // 계약 탭으로 전환
      contractId: contract.contractId
    }
  })
}

function formatDate(dateString) {
  const date = new Date(dateString)
  return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`
}

function formatAmount(amount) {
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

onMounted(() => {
  fetchContracts()
})
</script>

<style scoped>
@import '@/assets/css/mypage.css';

.detail-btn {
  padding: 8px 24px;
  background-color: #8B5CF6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.detail-btn:hover {
  background-color: #7C3AED;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
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

.contract-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  transition: all 0.2s ease;
  border: 1px solid #E5E7EB;
  cursor: pointer;
}

.contract-item:hover {
  box-shadow: 0 4px 16px rgba(139, 92, 246, 0.1);
  transform: translateY(-2px);
}

.amount {
  color: #8B5CF6;
  font-weight: 600;
}
</style> 