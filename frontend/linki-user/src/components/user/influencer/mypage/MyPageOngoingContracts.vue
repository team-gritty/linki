<template>
  <div class="ongoing-contracts-content">
    <h1 class="content-title">진행중인 계약</h1>
    <div class="content-box">
      <div v-if="contracts.length === 0" class="no-contracts">
        <p>진행중인 계약이 없습니다.</p>
      </div>
      <div v-else class="contracts-list">
        <div v-for="contract in contracts" :key="contract.contractId" class="contract-item">
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
            <button class="detail-btn" @click="viewContractDetail(contract.contractId)">
              상세조회
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { contractApi } from '@/api/contract';

export default {
  name: 'MyPageOngoingContracts',

  setup() {
    const contracts = ref([]);

    const fetchContracts = async () => {
      try {
        const response = await contractApi.getMyContracts();
        // PENDING 상태인 계약만 필터링
        contracts.value = response.data.filter(contract => contract.contractStatus === 'PENDING');
      } catch (error) {
        console.error('계약 목록 조회 실패:', error);
      }
    };

    const viewContractDetail = async (contractId) => {
      try {
        const response = await contractApi.getContractDetail(contractId);
        console.log('계약 상세:', response.data);
      } catch (error) {
        console.error('계약 상세 조회 실패:', error);
      }
    };

    const formatDate = (dateString) => {
      const date = new Date(dateString);
      return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
    };

    const formatAmount = (amount) => {
      return new Intl.NumberFormat('ko-KR').format(amount);
    };

    const getStatusClass = (status) => {
      return {
        'status-pending': status === 'PENDING',
        'status-active': status === 'ACTIVE',
        'status-completed': status === 'COMPLETED'
      };
    };

    const getStatusText = (status) => {
      const statusMap = {
        'PENDING': '진행중',
        'ACTIVE': '활성',
        'COMPLETED': '완료'
      };
      return statusMap[status] || status;
    };

    onMounted(() => {
      fetchContracts();
    });

    return {
      contracts,
      viewContractDetail,
      formatDate,
      formatAmount,
      getStatusClass,
      getStatusText
    };
  }
};
</script>

<style>
@import '@/assets/css/mypage.css';
</style> 