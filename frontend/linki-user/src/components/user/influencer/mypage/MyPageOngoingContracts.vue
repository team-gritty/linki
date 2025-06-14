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
import { useRouter } from 'vue-router';
import httpClient from '../../../../utils/httpRequest';

export default {
  name: 'MyPageOngoingContracts',

  setup() {
    const contracts = ref([]);
    const router = useRouter();

    const fetchContracts = async () => {
      try {
        const response = await httpClient.get('/v1/api/influencer/contracts');
        // ONGOING 또는 PENDING_SIGN 상태인 계약만 필터링
        contracts.value = response.data.filter(contract => 
          contract.contractStatus === 'ONGOING' || contract.contractStatus === 'PENDING_SIGN'
        );
        console.log('Fetched contracts:', contracts.value);
      } catch (error) {
        console.error('계약 목록 조회 실패:', error);
        contracts.value = [];
      }
    };

    const viewContractDetail = async (contract) => {
      try {
        // 계약서 상세 페이지로 이동 (제안서 상세 페이지와 동일한 라우트 사용)
        router.push({
          path: `/proposal/${contract.proposal_id}`,
          query: { tab: 'contract' }
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
        'status-ongoing': status === 'ONGOING',
        'status-pending-sign': status === 'PENDING_SIGN',
        'status-completed': status === 'COMPLETED'
      };
    };

    const getStatusText = (status) => {
      const statusMap = {
        'ONGOING': '진행중',
        'PENDING_SIGN': '서명 대기중',
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

<style scoped>
.ongoing-contracts-content {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

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
  background-color: #EEF2FF;
  color: #6366F1;
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