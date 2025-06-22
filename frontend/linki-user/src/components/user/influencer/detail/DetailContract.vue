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
import { ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { contractApi } from '@/api/contract';

export default {
  name: 'DetailContract',
  props: {
    detailData: {
      type: Object,
      required: true
    }
  },

  setup(props) {
    const router = useRouter();
    const loading = ref(true);
    const error = ref(null);
    const contract = ref({});

    // detailData 변경 감지하여 계약 정보 설정
    watch(() => props.detailData, (newData) => {
      loading.value = true;
      console.log('DetailContract received data:', newData);
      
      if (newData) {
        // contract 직접 접근 또는 contract 속성 접근 시도
        const contractData = newData.contract || newData;
        if (contractData) {
          console.log('Setting contract detail:', contractData);
          contract.value = contractData;
        } else {
          console.warn('No contract data found in:', newData);
          contract.value = {};
          error.value = '계약 정보를 찾을 수 없습니다.';
        }
      } else {
        console.warn('No detail data received');
        contract.value = {};
        error.value = '데이터를 받지 못했습니다.';
      }
      
      loading.value = false;
    }, { immediate: true });

    const viewContractDocument = async () => {
      try {
        if (!contract.value.contractId) {
          throw new Error('계약 ID가 없습니다.');
        }
        const documentData = await contractApi.getContractDocument(contract.value.contractId);
        if (documentData && documentData.documentUrl) {
          window.location.href = documentData.documentUrl;
        } else {
          throw new Error('계약서 URL을 찾을 수 없습니다.');
        }
      } catch (error) {
        console.error('계약서 조회 실패:', error);
        alert('계약서 조회에 실패했습니다.');
      }
    };

    const signContract = async () => {
      try {
        if (!contract.value.contractId) {
          throw new Error('계약 ID가 없습니다.');
        }
        const signData = await contractApi.signContract(contract.value.contractId);
        if (signData && signData.signUrl) {
          window.location.href = signData.signUrl;
        } else {
          throw new Error('서명 URL을 찾을 수 없습니다.');
        }
      } catch (error) {
        console.error('전자서명 실패:', error);
        alert('전자서명 처리에 실패했습니다.');
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
        'PENDING_SIGN': '서명 대기중',
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
      getStatusText
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