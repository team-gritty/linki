<template>
  <div class="contract-detail-content">
    <div v-if="!contract">
      <p>계약 정보가 없습니다.</p>
    </div>
    <div v-else>
      <div class="page-header">
        <div class="header-content">
          <h2>{{ contract.contractTitle }}</h2>
          <button class="back-button" @click="$emit('back')">
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

      <!-- 광고 이행 확인 버튼: UI/UX를 고려해 하단에 배치 -->
      <div class="ad-execution-section">
        <ContractExecutionButton
          :contract-id="contract.contractId"
          :ad-executed="contract.adExecuted || contract.ad_executed || 'no'"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import { contractApi } from '@/api/advertiser/advertiser-contract'
import ContractExecutionButton from '../ContractExecutionButton.vue'

const emit = defineEmits(['back'])
const props = defineProps({
  contract: {
    type: Object,
    required: true
  }
})

function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`
}

function formatAmount(amount) {
  if (!amount) return '0'
  return new Intl.NumberFormat('ko-KR').format(amount)
}

function getStatusClass(status) {
  return {
    'status-pending': status === 'PENDING',
    'status-active': status === 'ACTIVE',
    'status-completed': status === 'COMPLETED'
  }
}

function getStatusText(status) {
  const statusMap = {
    'PENDING': '진행중',
    'ACTIVE': '활성',
    'COMPLETED': '완료'
  }
  return statusMap[status] || status
}

async function viewContractDocument() {
  // 실제 API 연동 필요시 contractApi.getContractDocument(props.contract.contractId)
  alert('계약서 조회 기능은 추후 구현 예정입니다.')
}

async function signContract() {
  // 실제 API 연동 필요시 contractApi.signContract(props.contract.contractId)
  alert('전자 서명 기능은 추후 구현 예정입니다.')
}
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

.ad-execution-section {
  margin-top: 32px;
  display: flex;
  justify-content: flex-end;
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