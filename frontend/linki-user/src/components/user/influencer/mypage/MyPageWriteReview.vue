<template>
  <div class="write-review-content">
    <h1 class="content-title">리뷰 작성</h1>
    <div class="content-box">
      <div v-if="completedContracts.length === 0" class="no-contracts">
        <p>리뷰를 작성할 수 있는 계약이 없습니다.</p>
      </div>
      <div v-else class="contracts-list">
        <div v-for="contract in completedContracts" :key="contract.contractId" class="contract-item">
          <div class="contract-info">
            <h3 class="contract-title">{{ contract.contractTitle }}</h3>
            <div class="contract-details">
              <div class="detail-item">
                <span class="label">계약 기간</span>
                <span class="value">{{ formatDate(contract.contractStartDate) }} ~ {{ formatDate(contract.contractEndDate) }}</span>
              </div>
              <div class="detail-item">
                <span class="label">계약 금액</span>
                <span class="value">{{ formatCurrency(contract.contractAmount) }}원</span>
              </div>
              <div class="detail-item">
                <span class="label">정산 상태</span>
                <span class="value completed">정산 완료</span>
              </div>
            </div>
            <button @click="openReviewModal(contract)" class="write-review-btn">
              리뷰 작성
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 리뷰 작성 모달 -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <h2>리뷰 작성</h2>
        <div class="modal-body">
          <div class="rating-input">
            <div class="score-input">
              <span class="rating-label">별점</span>
              <input 
                type="text" 
                v-model="review.score" 
                @input="validateScore"
                placeholder="0.0"
                class="score-number"
              >
              <span class="score-max">/5점</span>
            </div>
            <div class="stars">
              <span v-for="n in 5" :key="n"
                    :class="{ 'star': true, 'filled': n <= Math.round(review.score) }">★</span>
            </div>
          </div>
          <div class="review-input">
            <label>리뷰 내용</label>
            <textarea v-model="review.comment" placeholder="리뷰 내용을 작성해주세요"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeModal" class="cancel-btn">취소</button>
          <button @click="submitReview" class="submit-btn">작성완료</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import httpClient from '../../../../utils/httpRequest';

export default {
  name: 'MyPageWriteReview',
  
  setup() {
    const completedContracts = ref([]);
    const showModal = ref(false);
    const selectedContract = ref(null);
    const tempScore = ref(0);
    
    const review = ref({
      score: '',
      comment: '',
      visibility: true
    });

    const validateScore = () => {
      let value = review.value.score;
      // 숫자와 소수점만 허용
      value = value.replace(/[^\d.]/g, '');
      
      // 소수점이 하나만 있도록
      const parts = value.split('.');
      if (parts.length > 2) {
        value = parts[0] + '.' + parts.slice(1).join('');
      }
      
      // 소수점 한자리로 제한
      if (parts[1]?.length > 1) {
        value = parts[0] + '.' + parts[1].slice(0, 1);
      }
      
      // 숫자 범위 제한 (0-5)
      let numValue = parseFloat(value);
      if (!isNaN(numValue)) {
        if (numValue > 5) value = '5';
        if (numValue < 0) value = '0';
      }
      
      review.value.score = value;
    };

    const setTempScore = (score) => {
      tempScore.value = score;
    };

    const setScore = (score) => {
      review.value.score = score;
      tempScore.value = score;
    };

    const fetchCompletedContracts = async () => {
      try {
        const contractsResponse = await httpClient.get('/v1/api/influencer/contracts');
        const contracts = Array.isArray(contractsResponse.data) ? contractsResponse.data : [];

        // 정산 정보 가져오기
        const settlementsResponse = await httpClient.get('/v1/api/influencer/settlements');
        const settlements = Array.isArray(settlementsResponse.data) ? settlementsResponse.data : [];

        // 정산이 완료된 계약만 필터링
        completedContracts.value = contracts
          .filter(contract => {
            const settlement = settlements.find(s => s.contractId === contract.contractId);
            return contract.contractStatus === 'COMPLETED' && settlement?.settlementStatus === 'COMPLETED';
          });

        console.log('Fetched contracts:', contracts);
        console.log('Fetched settlements:', settlements);
        console.log('Filtered contracts:', completedContracts.value);

      } catch (error) {
        console.error('Error fetching contracts:', error);
        if (error.response) {
          console.error('Response data:', error.response.data);
          console.error('Response status:', error.response.status);
        }
        completedContracts.value = [];
      }
    };

    const openReviewModal = (contract) => {
      selectedContract.value = contract;
      review.value = {
        score: '',
        comment: '',
        visibility: true
      };
      showModal.value = true;
    };

    const closeModal = () => {
      showModal.value = false;
      selectedContract.value = null;
    };

    const submitReview = async () => {
      if (!selectedContract.value) return;

      try {
        const reviewData = {
          influencerReviewId: `IR${Date.now()}`,
          influencerReviewScore: parseFloat(review.value.score) || 0,
          influencerReviewComment: review.value.comment,
          createdAt: new Date().toISOString(),
          contractId: selectedContract.value.contractId,
          visibility: true
        };

        await httpClient.post('/v1/api/influencer/reviews/written', reviewData);
        alert('리뷰가 저장되었습니다.');
        closeModal();
        fetchCompletedContracts();
      } catch (error) {
        console.error('Error in review submission process:', error);
        alert('리뷰 저장에 실패했습니다. 다시 시도해주세요.');
      }
    };

    const formatDate = (dateString) => {
      return new Date(dateString).toLocaleDateString('ko-KR');
    };

    const formatCurrency = (amount) => {
      return amount.toLocaleString('ko-KR');
    };

    onMounted(() => {
      fetchCompletedContracts();
    });

    return {
      completedContracts,
      showModal,
      review,
      tempScore,
      setTempScore,
      setScore,
      openReviewModal,
      closeModal,
      submitReview,
      formatDate,
      formatCurrency,
      validateScore
    };
  }
};
</script>

<style scoped>
.write-review-content {
  padding: 20px;
}

.content-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.content-box {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.no-contracts {
  text-align: center;
  padding: 40px;
  color: #666;
}

.contract-item {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
}

.contract-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 12px;
}

.contract-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
}

.label {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.value {
  font-weight: 500;
}

.value.completed {
  color: #10b981;
}

.write-review-btn {
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

.write-review-btn:hover {
  background-color: #7C3AED;
}

/* 모달 스타일 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  padding: 24px;
  width: 90%;
  max-width: 500px;
}

.modal-content h2 {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
}

.rating-input {
  margin-bottom: 20px;
}

.score-input {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.score-number {
  width: 60px;
  padding: 4px 8px;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  font-size: 16px;
  text-align: center;
}

.score-max {
  color: #666;
  font-size: 16px;
}

.stars {
  display: flex;
  gap: 4px;
}

.star {
  font-size: 24px;
  color: #e2e8f0;
}

.star.filled {
  color: #fbbf24;
}

.review-input {
  margin-bottom: 20px;
}

.review-input label {
  display: block;
  margin-bottom: 8px;
}

.review-input textarea {
  width: 100%;
  height: 120px;
  padding: 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  resize: vertical;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  padding: 8px 24px;
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  background-color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.submit-btn {
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

.submit-btn:hover {
  background-color: #7C3AED;
}
</style> 