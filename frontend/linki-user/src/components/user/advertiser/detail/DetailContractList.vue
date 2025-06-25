<template>
  <div class="contract-list-layout">
    <main class="contract-list-content">
      <template v-if="!selectedContract">
        <h1 class="content-title">계약서 목록</h1>
        <div class="content-box">
          <div v-if="contracts.length === 0" class="no-contracts">
            <p>등록된 계약서가 없습니다.</p>
          </div>
          <div v-else class="contracts-list">
            <div v-for="contract in contracts" :key="contract.contractId" class="contract-item" @click="selectContract(contract)" @mouseenter="hovered = contract.contractId" @mouseleave="hovered = null" :style="hovered === contract.contractId ? 'box-shadow: 0 4px 16px rgba(140,48,245,0.08); transform: translateY(-4px);' : ''">
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
                <button class="detail-btn" @click.stop="selectContract(contract)">상세조회</button>
              </div>
            </div>
          </div>
        </div>
      </template>
      <template v-else>
        <DetailContract @back="goBackToList" />
      </template>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { contractApi } from '@/api/advertiser/advertiser-contract'
import { useRoute, useRouter } from 'vue-router'
import DetailContract from './DetailContract.vue'

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
const selectedContract = ref(null)

const fetchContracts = async () => {
  try {
    const response = await contractApi.getMyContracts()
    let contractList = Array.isArray(response.data) ? response.data : []
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
    
    // URL에서 contractId 파라미터가 있으면 해당 계약서를 선택
    const contractId = route.query.contractId
    if (contractId) {
      const contract = contracts.value.find(c => c.contractId === contractId)
      if (contract) {
        selectedContract.value = contract
      }
    }
  } catch (error) {
    contracts.value = []
  }
}

function selectContract(contract) {
  selectedContract.value = contract
  // URL 업데이트 (히스토리에 추가하지 않고)
  router.replace({
    path: route.path,
    query: { 
      ...route.query,
      contractId: contract.contractId
    }
  })
}

function goBackToList() {
  selectedContract.value = null
  // URL에서 contractId 제거
  router.replace({
    path: route.path,
    query: { 
      ...route.query,
      contractId: undefined
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
    'status-pending': status === 'PENDING',
    'status-active': status === 'ACTIVE',
    'status-completed': status === 'COMPLETED'
  }
}

function getStatusText(status) {
  const statusMap = {
    'PENDING': '진행중',
    'ACTIVE': '활성',
    'COMPLETED': '완료'
  }
  return statusMap[status] || status
}

onMounted(() => {
  fetchContracts()
})
</script>

<style>
@import '@/assets/css/mypage.css';
</style> 