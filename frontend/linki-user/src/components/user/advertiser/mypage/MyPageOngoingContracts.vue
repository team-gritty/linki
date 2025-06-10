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
import { ref, onMounted } from 'vue';
import { contractApi } from '@/api/advertiser/advertiser-contract';

const contracts = ref([]);
const hovered = ref(null);

const fetchContracts = async () => {
  try {
    const response = await contractApi.getMyContracts();
    contracts.value = response.data.filter(contract => contract.contractStatus === 'PENDING');
  } catch (error) {
    console.error('계약 목록 조회 실패:', error);
  }
};

function viewContractDetail(contract) {
  // emit event for parent to handle detail view
  // or use router if needed
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