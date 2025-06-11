<template>
  <div class="contract-detail-content">
    <div v-if="loading" class="loading-state">
      <p>데이터 로딩중...</p>
    </div>
    <div v-else>
      <!-- <div class="page-header">
        <div class="header-content">
          <h2>{{ contract.contractTitle }}</h2>
          <button class="back-button" @click="goToContractList">
            계약서 목록
          </button>
        </div>
        <div v-if="contract.contractStatus" class="status-badge" :class="getStatusClass(contract.contractStatus)">
          {{ getStatusText(contract.contractStatus) }}
        </div> -->
      <!-- </div>  -->

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
          <i class="fas fa-file-contract"></i>
          계약서 조회
        </button>
        <button 
          class="action-button sign-contract" 
          @click="signContract"
          :disabled="contract.contractStatus !== 'PENDING'"
        >
          <i class="fas fa-signature"></i>
          전자 서명
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { contractApi } from '@/api/contract';
import axios from 'axios';

export default {
  name: 'DetailContract',

  setup() {
    const route = useRoute();
    const router = useRouter();
    const contract = ref({});
    const loading = ref(true);
    const error = ref(null);

    const fetchContractDetail = async () => {
      try {
        loading.value = true;
        error.value = null;
        
        // 1. 제안서 ID로 제안서 정보 조회
        const proposalId = route.params.id;
        if (!proposalId) {
          throw new Error('제안서 ID를 찾을 수 없습니다.');
        }

        const proposalResponse = await axios.get(`/v1/api/influencer/proposals/${proposalId}`);
        if (!proposalResponse.data || proposalResponse.data.length === 0) {
          throw new Error('제안서 정보를 찾을 수 없습니다.');
        }

        // 2. 제안서에서 캠페인 ID 확인
        const proposal = proposalResponse.data[0];
        console.log('Proposal data:', proposal);
        
        if (!proposal.campaign_id) {
          throw new Error('캠페인 정보를 찾을 수 없습니다.');
        }

        // 3. 캠페인 ID로 계약 정보 조회 - routes.json 설정에 맞춰 수정
        const contractResponse = await axios.get(`/v1/api/influencer/contracts/campaign/${proposal.campaign_id}`);
        console.log('Contract API Response:', contractResponse.data);
        
        if (contractResponse.data && contractResponse.data.length > 0) {
          contract.value = contractResponse.data[0];
          console.log('Found contract:', contract.value);
        } else {
          throw new Error('해당 캠페인의 계약 정보를 찾을 수 없습니다.');
        }

      } catch (err) {
        console.error('계약 상세 조회 실패:', err);
        error.value = err.message || '계약 정보를 불러오는데 실패했습니다.';
      } finally {
        loading.value = false;
      }
    };

    onMounted(() => {
      fetchContractDetail();
    });

    const goToContractList = () => {
      // 현재 URL이 /proposal/로 시작하므로, 제안서 목록으로 이동
      router.push({
        path: '/mypage',
        query: { tab: 'contracts' }  // 마이페이지의 계약서 탭으로 이동
      });
    };

    const viewContractDocument = async () => {
      try {
        const response = await axios.get(`/v1/api/influencer/contracts/${contract.value.contractId}`);
        window.location.href = response.data.documentUrl;
      } catch (error) {
        console.error('계약서 조회 실패:', error);
      }
    };

    const signContract = async () => {
      try {
        const response = await axios.post(`/v1/api/influencer/contracts/${contract.value.contractId}/sign`);
        window.location.href = response.data.signUrl;
      } catch (error) {
        console.error('전자서명 실패:', error);
      }
    };

    const formatDate = (dateString) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
    };

    const formatAmount = (amount) => {
      if (!amount) return '0';
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

    return {
      contract,
      loading,
      error,
      viewContractDocument,
      signContract,
      formatDate,
      formatAmount,
      getStatusClass,
      getStatusText,
      goToContractList
    };
  }
};
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

.loading-state,
.error-state {
  text-align: center;
  padding: 48px;
  color: #666;
}

.error-state {
  color: #ef4444;
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

.sign-contract {
  background-color: #3b82f6;
  color: white;
}

.sign-contract:hover {
  background-color: #2563eb;
}

.sign-contract:disabled {
  background-color: #e2e8f0;
  color: #94a3b8;
  cursor: not-allowed;
}

.status-badge {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.status-pending {
  background-color: #e6effd;
  color: #3b82f6;
}

.status-active {
  background-color: #dcfce7;
  color: #16a34a;
}

.status-completed {
  background-color: #f3f4f6;
  color: #6b7280;
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