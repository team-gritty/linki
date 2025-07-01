<template>
  <div class="ongoing-contracts-content">
    <h1 class="content-title">진행중인 계약</h1>
    <div class="content-box">
      <div v-if="contracts.length === 0" class="no-contracts">
        <p>진행중인 계약이 없습니다.</p>
      </div>
      <div v-else class="contracts-list">
        <div v-for="contract in contracts" :key="contract.contractId" class="contract-item" @click="viewContractDetail(contract)" @mouseenter="hovered = contract.contractId" @mouseleave="hovered = null" :style="hovered === contract.contractId ? 'box-shadow: 0 4px 16px rgba(140,48,245,0.08); transform: translateY(-4px);' : ''">
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
            <button class="detail-btn" @click.stop="viewContractDetail(contract)">상세조회</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineEmits } from 'vue';
import { contractApi, CONTRACT_STATUS } from '@/api/advertiser/advertiser-contract';
import { useRouter } from 'vue-router';

const contracts = ref([]);
const hovered = ref(null);
const emit = defineEmits(['show-detail']);
const router = useRouter();

const fetchContracts = async () => {
  try {
    // 진행중인 계약만 조회: ONGOING, PENDING_SIGN 상태만
    const response = await contractApi.getMyContracts(['ONGOING', 'PENDING_SIGN']);
    console.log('API Response:', response);
    console.log('Response data:', response.data);
    
    // 응답 구조 확인
    let contractList;
    if (Array.isArray(response.data)) {
      contractList = response.data;
    } else if (Array.isArray(response)) {
      contractList = response;
    } else if (response.data && Array.isArray(response.data.data)) {
      contractList = response.data.data;
    } else {
      console.warn('Unexpected response structure:', response);
      contractList = [];
    }
    
    console.log('Contract list:', contractList);
    
    contracts.value = contractList.map(contract => {
      console.log('Processing contract:', contract.contractId, 'status:', contract.contractStatus);
      return {
        contractId: contract.contractId,
        contractTitle: contract.contractTitle,
        contractStatus: contract.contractStatus,
        contractStartDate: contract.contractStartDate,
        contractEndDate: contract.contractEndDate,
        contractAmount: contract.contractAmount,
        ...contract
      };
    });
    
    console.log('Final contracts:', contracts.value);
    
    // 각 계약의 상태 재확인
    contracts.value.forEach(contract => {
      console.log(`Contract ${contract.contractId}: status = ${contract.contractStatus}`);
      console.log(`Should be in ongoing? ${['ONGOING', 'PENDING_SIGN'].includes(contract.contractStatus)}`);
    });
  } catch (error) {
    console.error('Error fetching contracts:', error);
    contracts.value = [];
  }
};

async function viewContractDetail(contract) {
  console.log('=== viewContractDetail 시작 ===');
  console.log('Viewing contract detail:', contract);
  console.log('contractId:', contract.contractId);
  console.log('campaignId:', contract.campaignId);
  
  try {
    // campaignId가 없으면 계약 상세 API를 먼저 호출해서 campaignId를 가져옴
    if (!contract.campaignId) {
      console.log('campaignId not found, fetching contract detail...');
      console.log('Calling contractApi.getContractDetail with:', contract.contractId);
      
      const detailResponse = await contractApi.getContractDetail(contract.contractId);
      console.log('Contract detail response:', detailResponse);
      
      if (detailResponse && detailResponse.campaignId) {
        contract.campaignId = detailResponse.campaignId;
        console.log('Found campaignId:', detailResponse.campaignId);
      } else {
        console.warn('No campaignId found in response');
      }
    }
    
    console.log('Final campaignId before navigation:', contract.campaignId);
    
    // 4개 탭이 있는 상세 페이지로 이동 (계약 탭을 기본으로)
    const targetPath = `/mypage/campaign-detail/${contract.campaignId || 'temp'}`;
    console.log('Navigating to:', targetPath);
    
    router.push({
      path: targetPath,
      query: { 
        tab: 'contract',
        contractId: contract.contractId
      }
    });
  } catch (error) {
    console.error('Error in viewContractDetail:', error);
    console.error('Error details:', error.message, error.stack);
    
    // 에러가 발생해도 일단 이동 (DetailContract에서 다시 API 호출함)
    router.push({
      path: `/mypage/campaign-detail/temp`,
      query: { 
        tab: 'contract',
        contractId: contract.contractId
      }
    });
  }
  
  console.log('=== viewContractDetail 종료 ===');
}

function formatDate(dateString) {
  const date = new Date(dateString);
  return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
}

function formatAmount(amount) {
  return new Intl.NumberFormat('ko-KR').format(amount);
}

function getStatusText(status) {
  console.log('Getting status text for:', status);
  const statusMap = {
    'ONGOING': '진행중',
    'PENDING_SIGN': '서명 대기중',
    'COMPLETED': '완료'
  };
  const result = statusMap[status] || status;
  console.log('Status text result:', result);
  return result;
}

function getStatusClass(status) {
  return {
    'status-ongoing': status === 'ONGOING',
    'status-pending-sign': status === 'PENDING_SIGN',
    'status-completed': status === 'COMPLETED'
  };
}

onMounted(() => {
  fetchContracts();
});
</script>

<style scoped>
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
  background-color: #F3F0FF;
  color: #7B21E8;
}

.status-pending-sign {
  background-color: #FEF3C7;
  color: #D97706;
}

.status-active {
  background-color: #ECFDF5;
  color: #059669;
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