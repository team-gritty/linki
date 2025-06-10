<template>
  <div class="settlement-content">
    <h1 class="content-title">정산 내역</h1>
    <div class="content-box">
      <div v-if="settlements.length === 0" class="no-settlements">
        <p>정산 내역이 없습니다.</p>
      </div>
      <div v-else class="settlements-list">
        <div v-for="settlement in settlements" :key="settlement.settlementId" class="settlement-item">
          <div class="settlement-content">
            <div class="settlement-header">
              <h3 class="settlement-id">정산번호: {{ settlement.settlementId }}</h3>
              <span class="status-badge" :class="getStatusClass(settlement.settlementStatus)">
                {{ getStatusText(settlement.settlementStatus) }}
              </span>
            </div>
            <div class="settlement-details">
              <div class="detail-group">
                <div class="detail-item">
                  <span class="label">계약 ID</span>
                  <span class="value">{{ settlement.contractId }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">정산일</span>
                  <span class="value">{{ formatDate(settlement.settlementDate) }}</span>
                </div>
                <div class="detail-item">
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
        const response = await contractApi.getAllSettlements();
        settlements.value = response.data;
      } catch (error) {
        console.error('정산 내역 조회 실패:', error);
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

<style>
@import '@/assets/css/mypage.css';
</style> 