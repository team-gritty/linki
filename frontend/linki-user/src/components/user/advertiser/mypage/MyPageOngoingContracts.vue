<template>
  <div class="ongoing-contracts-content">
    <h1 class="content-title">진행중인 계약</h1>
    <div class="content-box">
      <div v-if="contracts.length === 0" class="no-contracts">
        <p>진행중인 계약이 없습니다.</p>
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
import { ref, onMounted, defineEmits } from 'vue';
import { contractApi } from '@/api/advertiser/advertiser-contract';

const contracts = ref([]);
const hovered = ref(null);
const emit = defineEmits(['show-detail']);

const fetchContracts = async () => {
  try {
    const response = await contractApi.getMyContracts();
    console.log('contracts API 응답', response.data);
    let contractList = [];
    if (Array.isArray(response.data)) {
      contractList = response.data;
    } else if (response.data && Array.isArray(response.data.contracts)) {
      contractList = response.data.contracts;
    }
    console.log('필터 전 contracts', contractList);
    contracts.value = Array.isArray(contractList)
      ? contractList.filter(contract =>
          contract.contractStatus === 'PENDING' || contract.contractStatus === '진행중')
      : [];
  } catch (error) {
    console.error('계약 목록 조회 실패:', error);
  }
};

function viewContractDetail(contract) {
  emit('show-detail', contract);
}

function formatDate(dateString) {
  const date = new Date(dateString);
  return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
}

function formatAmount(amount) {
  return new Intl.NumberFormat('ko-KR').format(amount);
}

function getStatusClass(status) {
  return {
    'status-pending': status === 'PENDING',
    'status-active': status === 'ACTIVE',
    'status-completed': status === 'COMPLETED'
  };
}

function getStatusText(status) {
  const statusMap = {
    'PENDING': '진행중',
    'ACTIVE': '활성',
    'COMPLETED': '완료'
  };
  return statusMap[status] || status;
}

onMounted(() => {
  fetchContracts();
});
</script>

<style>
@import '@/assets/css/mypage.css';
</style> 