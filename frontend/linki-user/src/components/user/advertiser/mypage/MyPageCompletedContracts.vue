<template>
    <div class="completed-contracts-content">
      <h1 class="content-title">완료된 계약</h1>
      <div class="content-box">
        <div v-if="contracts.length === 0" class="no-contracts">
          <p>완료된 계약이 없습니다.</p>
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
  import { contractApi, CONTRACT_STATUS } from '@/api/advertiser/advertiser-contract';
  
  const contracts = ref([]);
  const hovered = ref(null);
  const emit = defineEmits(['show-detail']);
  
  const fetchContracts = async () => {
    try {
      const response = await contractApi.getMyContracts();
      let contractList = Array.isArray(response.data) ? response.data : [];
      contracts.value = contractList
        .filter(contract => contract.contractStatus === 'COMPLETED')
        .map(contract => ({
          contractId: contract.contractId,
          contractTitle: contract.contractTitle,
          contractStatus: contract.contractStatus,
          contractStartDate: contract.contractStartDate,
          contractEndDate: contract.contractEndDate,
          contractAmount: contract.contractAmount,
          ...contract
        }));
    } catch (error) {
      contracts.value = [];
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
  
  function getStatusText(status) {
    const statusMap = {
      [CONTRACT_STATUS.PENDING]: '진행중',
      [CONTRACT_STATUS.PENDING_SIGN]: '서명 대기중',
      [CONTRACT_STATUS.ACTIVE]: '활성',
      [CONTRACT_STATUS.COMPLETED]: '완료'
    };
    return statusMap[status] || status;
  }
  
  function getStatusClass(status) {
    return {
      'status-pending': status === CONTRACT_STATUS.PENDING,
      'status-pending-sign': status === CONTRACT_STATUS.PENDING_SIGN,
      'status-active': status === CONTRACT_STATUS.ACTIVE,
      'status-completed': status === CONTRACT_STATUS.COMPLETED
    };
  }
  
  onMounted(() => {
    fetchContracts();
  });
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
  
  .status-pending {
    background-color: #EEF2FF;
    color: #6366F1;
  }
  
  .status-pending-sign {
    background-color: #FEF3C7;
    color: #D97706;
  }
  
  .status-active {
    background-color: #ECFDF5;
    color: #059669;
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