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
                <span class="value">{{ formatAmount(contract.contractAmount) }}원</span>
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
                type="number"
                v-model.number="review.score"
                min="0"
                max="5"
                step="0.1"
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

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { reviewApi } from '@/api/advertiser/advertiser-review';
import { contractApi } from '@/api/advertiser/advertiser-contract';

const completedContracts = ref([]);
const showModal = ref(false);
const selectedContract = ref(null);
const review = ref({
  score: 0,
  comment: '',
  visibility: true
});
const router = useRouter();

const fetchCompletedContracts = async () => {
  try {
    // 1. 모든 완료된 계약 불러오기
    const contractsResponse = await contractApi.getMyContracts();
    let contractList = Array.isArray(contractsResponse.data) ? contractsResponse.data : [];
    const completedList = contractList.filter(contract => contract.contractStatus === 'COMPLETED');

    // 2. 작성한 리뷰 DB에서 이미 리뷰 작성된 contractId 목록 불러오기
    const reviewsResponse = await reviewApi.getGivenReviews();
    const reviewedContractIds = (Array.isArray(reviewsResponse.data) ? reviewsResponse.data : []).map(r => r.contractId);

    // 3. 리뷰가 작성되지 않은 계약만 필터링
    completedContracts.value = completedList
      .filter(contract => !reviewedContractIds.includes(contract.contractId))
      .map(contract => ({
        contractId: contract.contractId,
        contractTitle: contract.contractTitle,
        contractStatus: contract.contractStatus,
        contractStartDate: contract.contractStartDate,
        contractEndDate: contract.contractEndDate,
        contractAmount: contract.contractAmount,
        ...contract
      }));
  } catch (error) {
    completedContracts.value = [];
  }
};

const openReviewModal = (contract) => {
  selectedContract.value = contract;
  review.value = {
    score: 0,
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
    // API 엔드포인트 및 필드명에 맞게 POST, id 필드 추가
    const reviewData = {
      id: `AR${Date.now()}`,
      contractId: selectedContract.value.contractId,
      score: parseFloat(review.value.score) || 0,
      comment: review.value.comment,
      createdAt: new Date().toISOString(),
      visibility: review.value.visibility
    };
    await reviewApi.writeInfluencerReview(reviewData);
    alert('리뷰가 저장되었습니다.');
    closeModal();
    await fetchCompletedContracts(); // 리뷰 작성 후 최신화
  } catch (error) {
    if (error.response) {
      console.error('리뷰 저장 실패:', error.response.data, error.response.status);
    } else {
      console.error('리뷰 저장 실패:', error);
    }
    alert('리뷰 저장에 실패했습니다. 다시 시도해주세요.');
  }
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
};

const formatAmount = (amount) => {
  return new Intl.NumberFormat('ko-KR').format(amount);
};

onMounted(() => {
  fetchCompletedContracts();
});
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
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.write-review-btn:hover {
  background-color: #2563eb;
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

.visibility-toggle {
  margin-bottom: 20px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  padding: 8px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background-color: white;
  cursor: pointer;
}

.submit-btn {
  padding: 8px 16px;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.submit-btn:hover {
  background-color: #2563eb;
}
</style> 