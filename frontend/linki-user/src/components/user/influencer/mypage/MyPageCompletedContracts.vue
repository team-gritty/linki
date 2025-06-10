<template>
  <div class="completed-contracts-content">
    <h1 class="content-title">완료된 계약</h1>
    <div class="content-box">
      <div v-if="contracts.length === 0" class="no-contracts">
        <p>완료된 계약이 없습니다.</p>
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
            <button class="detail-btn" @click="viewContractDetail(contract)">
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
import { useRouter } from 'vue-router';

export default {
  name: 'MyPageCompletedContracts',

  setup() {
    const contracts = ref([]);
    const router = useRouter();

    const fetchContracts = async () => {
      try {
        const response = await contractApi.getMyContracts();
        // COMPLETED 상태인 계약만 필터링
        contracts.value = response.data.filter(contract => contract.contractStatus === 'COMPLETED');
      } catch (error) {
        console.error('계약 목록 조회 실패:', error);
      }
    };

    const viewContractDetail = async (contract) => {
      try {
        // 제안서 상세 페이지로 이동하면서 계약 탭으로 설정
        router.push({
          name: 'proposal-detail',
          // 계약 ID를 params로 전달
          params: { id: contract.contractId },
          // 계약서 탭으로 설정
          query: { 
            tab: 'contract',
            // 계약 정보도 함께 전달
            contractId: contract.contractId,
            contractTitle: contract.contractTitle,
            contractStartDate: contract.contractStartDate,
            contractEndDate: contract.contractEndDate,
            contractAmount: contract.contractAmount,
            contractStatus: contract.contractStatus
          }
        });
      } catch (error) {
        console.error('페이지 이동 실패:', error);
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