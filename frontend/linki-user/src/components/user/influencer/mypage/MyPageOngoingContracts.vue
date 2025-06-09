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

<style scoped>
.ongoing-contracts-content {
  padding: 24px;
}

.content-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #1a1a1a;
}

.content-box {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.no-contracts {
  text-align: center;
  padding: 48px 0;
  color: #666;
}

.contracts-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.contract-item {
  padding: 24px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 12px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.contract-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.contract-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.contract-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  color: white;
}

.status-pending {
  background-color: #6c5ce7;
}

.status-active {
  background-color: #00b894;
}

.status-completed {
  background-color: #74b9ff;
}

.contract-details {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 20px;
  align-items: center;
}

.detail-group {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.label {
  color: #666;
  font-size: 14px;
  min-width: 80px;
  flex-shrink: 0;
}

.value {
  color: #1a1a1a;
  font-size: 14px;
  flex: 1;
}

.amount {
  font-weight: 600;
  color: #6c5ce7;
}

.detail-btn {
  padding: 8px 24px;
  background: #6c5ce7;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
  height: fit-content;
  margin-left: auto;
}

.detail-btn:hover {
  background: #5f4dd0;
}
</style> 