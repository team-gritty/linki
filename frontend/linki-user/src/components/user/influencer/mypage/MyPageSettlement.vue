<template>
  <div class="settlement-content">
    <h1 class="content-title">정산 내역</h1>
    <div class="content-box">
      <div v-if="settlements.length === 0" class="no-settlements">
        <p>정산 내역이 없습니다.</p>
      </div>
      <div v-else class="settlements-list">
        <div v-for="settlement in settlements" :key="settlement.settlementId" class="settlement-item">
          <div class="settlement-header">
            <div class="settlement-id-group">
              <h3 class="settlement-number">정산번호: {{ settlement.settlementId }}</h3>
              <span class="status-badge" :class="getStatusClass(settlement.settlementStatus)">
                {{ getStatusText(settlement.settlementStatus) }}
              </span>
            </div>
            <div class="settlement-info">
              <div class="info-group">
                <div class="info-item">
                  <span class="label">계약 ID</span>
                  <span class="value">{{ settlement.contractId }}</span>
                </div>
                <div class="info-item">
                  <span class="label">정산일</span>
                  <span class="value">{{ formatDate(settlement.settlementDate) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">정산금액</span>
                  <span class="value amount">{{ formatAmount(settlement.settlementAmount) }}원</span>
                </div>
              </div>
            </div>
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
  name: 'MyPageSettlement',

  setup() {
    const settlements = ref([]);

    const fetchSettlements = async () => {
      try {
        const data = await contractApi.getAllSettlements();
        settlements.value = data;
      } catch (error) {
        console.error('정산 내역 조회 실패:', error);
        settlements.value = [];
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
        'status-completed': status === 'COMPLETED',
        'status-pending': status === 'PENDING'
      };
    };

    const getStatusText = (status) => {
      const statusMap = {
        'COMPLETED': '정산완료',
        'PENDING': '정산대기'
      };
      return statusMap[status] || status;
    };

    onMounted(() => {
      fetchSettlements();
    });

    return {
      settlements,
      formatDate,
      formatAmount,
      getStatusClass,
      getStatusText
    };
  }
};
</script>

<style scoped>
.settlement-content {
  padding: 32px;
  max-width: 1200px;
  margin: 0 auto;
}

.content-title {
  font-size: 24px;
  font-weight: 700;
  color: #1F2937;
  margin-bottom: 24px;
}

.settlements-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.settlement-item {
  background: white;
  border-radius: 12px;
  padding: 20px 24px;
  border: 1px solid #E5E7EB;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.settlement-item:hover {
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.12);
  transform: translateY(-1px);
  border-color: rgba(139, 92, 246, 0.3);
}

.settlement-header {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.settlement-id-group {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.settlement-number {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.settlement-info {
  width: 100%;
}

.info-group {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  width: 100%;
  padding: 0 12px;
}

.info-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
}

.info-item:first-child {
  justify-content: flex-start;
}

.info-item:last-child {
  justify-content: flex-end;
}

.label {
  font-size: 14px;
  color: #6B7280;
  font-weight: 500;
  white-space: nowrap;
}

.value {
  font-size: 14px;
  color: #111827;
  font-weight: 600;
  white-space: nowrap;
}

.amount {
  color: #8B5CF6;
  font-weight: 700;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.status-completed {
  background-color: #F0FDF4;
  color: #15803D;
}

.status-pending {
  background-color: #FFFBEB;
  color: #B45309;
}

.no-settlements {
  text-align: center;
  padding: 48px 24px;
  color: #6B7280;
  background: white;
  border-radius: 12px;
  border: 1px solid #E5E7EB;
  font-size: 15px;
  font-weight: 500;
}

@media (max-width: 768px) {
  .settlement-content {
    padding: 16px;
  }

  .settlement-item {
    padding: 16px;
  }

  .info-group {
    flex-direction: column;
    align-items: stretch;
    padding: 0;
  }

  .info-item {
    justify-content: space-between !important;
    width: 100%;
  }

  .settlement-id-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .status-badge {
    align-self: flex-start;
  }
}
</style> 