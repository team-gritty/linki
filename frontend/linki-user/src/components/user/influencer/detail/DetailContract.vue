<template>
  <div class="contract-detail-content">
    <div v-if="loading" class="loading-state">
      <p>데이터 로딩중...</p>
    </div>
    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
    </div>
    <div v-else-if="!contract || !contract.contractId" class="error-state">
      <p>계약 정보가 없습니다.</p>
    </div>
    <div v-else>
      <div class="page-header">
        <div class="header-content">
          <h2>{{ contract.contractTitle }}</h2>
          <button class="back-button" @click="goToContractList">
            계약서 목록
          </button>
        </div>
        <div v-if="contract.contractStatus" class="status-badge" :class="getStatusClass(contract.contractStatus)">
          {{ getStatusText(contract.contractStatus) }}
        </div>
      </div>

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
          <i class="fas fa-download"></i>
          계약서 다운로드
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { contractApi } from '@/api/contract';

export default {
  name: 'DetailContract',
  props: {
    detailData: {
      type: Object,
      required: false
    }
  },

  setup(props) {
    const router = useRouter();
    const route = useRoute();
    const loading = ref(true);
    const error = ref(null);
    const contract = ref({});

    // 계약 상세 정보 조회
    const fetchContractDetail = async () => {
      try {
        loading.value = true;
        error.value = null;
        
        // 1. contractId 추출 시도 (URL query가 최우선)
        let contractId = null;
        
        if (route.query.contractId) {
          contractId = route.query.contractId;
        } else if (props.detailData?.contract?.contractId) {
          contractId = props.detailData.contract.contractId;
        } else if (props.detailData?.contractId) {
          contractId = props.detailData.contractId;
        }
        
        console.log('Contract ID sources:', {
          queryContractId: route.query.contractId,
          detailDataContract: props.detailData?.contract?.contractId,
          detailDataContractId: props.detailData?.contractId,
          finalContractId: contractId
        });
        
        if (!contractId) {
          console.error('Contract ID not found in any source');
          throw new Error('계약 ID를 찾을 수 없습니다.');
        }
        
        // 2. API로 계약 상세 정보 조회
        console.log('Fetching contract detail for ID:', contractId);
        const response = await contractApi.getContractDetail(contractId);
        console.log('Contract detail response:', response);
        
        if (!response) {
          throw new Error('계약 정보를 가져올 수 없습니다.');
        }
        
        contract.value = response;
        
      } catch (err) {
        console.error('계약 상세 정보 조회 실패:', err);
        error.value = err.message || '계약 정보를 불러오는데 실패했습니다.';
        contract.value = {};
      } finally {
        loading.value = false;
      }
    };

    // detailData 변경 감지
    watch(() => props.detailData, (newData) => {
      if (newData) {
        fetchContractDetail();
      }
    }, { immediate: true, deep: true });

    // route query 변경 감지
    watch(() => route.query.contractId, (newContractId) => {
      if (newContractId) {
        fetchContractDetail();
      }
    });

    const viewContractDocument = async () => {
      try {
        console.log('계약서 조회 시작, contractId:', contract.value.contractId)
        
        if (!contract.value.contractId) {
          throw new Error('계약 ID가 없습니다.');
        }
        
        const response = await contractApi.getContractDocument(contract.value.contractId);
        console.log('API 응답:', response);
        
        // contract.js API 응답 구조: response.result.file
        const url = response?.result?.file;
        console.log('추출된 URL:', url);
        
        if (!url) {
          console.error('URL을 찾을 수 없습니다. 응답 구조:', response);
          alert('계약서 URL을 찾을 수 없습니다.');
          return;
        }
        
        // 새 탭에서 계약서 열기
        console.log('새 탭에서 열 URL:', url);
        window.open(url, '_blank');
        console.log('계약서 조회 성공: 새 탭에서 열림');
        
      } catch (error) {
        console.error('계약서 조회 실패:', error);
        alert('계약서 조회에 실패했습니다.');
      }
    };

    const goToContractList = () => {
      router.push({ name: 'influencer-mypage', query: { currentMenu: 'contract.ongoing' } });
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
        'status-pending-sign': status === 'PENDING_SIGN',
        'status-active': status === 'ACTIVE',
        'status-completed': status === 'COMPLETED'
      };
    };

    const getStatusText = (status) => {
      const statusMap = {
        'PENDING': '진행중',
        'PENDING_SIGN': '서명 대기중',
        'ACTIVE': '활성',
        'COMPLETED': '완료'
      };
      return statusMap[status] || status;
    };

    onMounted(() => {
      fetchContractDetail();
    });

    return {
      contract,
      loading,
      error,
      viewContractDocument,
      goToContractList,
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