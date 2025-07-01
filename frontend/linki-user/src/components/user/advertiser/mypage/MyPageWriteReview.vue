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
              <div class="detail-item" v-if="contract.campaignTitle">
                <span class="label">캠페인명</span>
                <span class="value">{{ contract.campaignTitle }}</span>
              </div>
              <div class="detail-item">
                <span class="label">계약 상태</span>
                <span class="value completed">{{ contract.contractStatus }}</span>
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
import { reviewApi } from '@/api/advertiser/advertiser-review';
import { contractApi } from '@/api/advertiser/advertiser-contract';

const completedContracts = ref([]);
const showModal = ref(false);
const selectedContract = ref(null);
const review = ref({
  score: 0,
  comment: ''
});

// 리뷰 작성 가능한 완료된 계약 조회
const fetchCompletedContracts = async () => {
  try {
    console.log('=== 리뷰 작성용 완료된 계약 조회 시작 ===');
    
    // 완료된 계약 직접 조회
    const contractsResponse = await contractApi.getMyContracts(['COMPLETED']);
    console.log('=== 완료된 계약 조회 응답 ===', contractsResponse);
    
    // 이미 작성한 리뷰 목록 조회
    const givenReviewsResponse = await reviewApi.getGivenReviews();
    console.log('=== 작성한 리뷰 목록 응답 ===', givenReviewsResponse);
    
    // API 응답에서 data 필드 추출
    const contractsList = Array.isArray(contractsResponse) ? contractsResponse : [];
    const givenReviewsData = givenReviewsResponse?.data || givenReviewsResponse;
    const givenReviews = Array.isArray(givenReviewsData) ? givenReviewsData : [];
    
    // 이미 리뷰를 작성한 계약 ID 목록 (reviewId 대신 contractId 사용)
    const reviewedContractIds = givenReviews.map(review => {
      console.log('=== 리뷰 항목 ===', review);
      // 백엔드 응답에서 contractId 찾기 (다양한 필드명 대응)
      return review.contractId || review.contract_id || review.contractTitle;
    }).filter(id => id); // undefined 값 제거
    
    console.log('=== 이미 리뷰 작성된 계약 ID 목록 ===', reviewedContractIds);
    
    // 완료된 계약 중 리뷰를 아직 작성하지 않은 계약만 필터링
    completedContracts.value = contractsList.filter(contract => {
      const contractId = contract.contractId;
      const isReviewed = reviewedContractIds.includes(contractId);
      console.log(`=== 계약 ${contractId} 리뷰 작성 여부: ${isReviewed} ===`);
      return !isReviewed;
    });
    
    console.log('=== 리뷰 작성 가능한 계약 목록 ===', completedContracts.value);
  } catch (error) {
    console.error('리뷰 작성용 완료된 계약 조회 실패:', error);
    completedContracts.value = [];
  }
};

const openReviewModal = (contract) => {
  selectedContract.value = contract;
  review.value = {
    score: 0,
    comment: ''
  };
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  selectedContract.value = null;
};

const submitReview = async () => {
  if (!selectedContract.value) return;

  // 유효성 검사
  if (!review.value.score || parseFloat(review.value.score) <= 0) {
    alert('별점을 입력해주세요.');
    return;
  }

  if (!review.value.comment || review.value.comment.trim() === '') {
    alert('리뷰 내용을 입력해주세요.');
    return;
  }

  try {
    console.log('=== 리뷰 작성 시작 ===');
    console.log('Contract ID:', selectedContract.value.contractId);
    
    const reviewData = {
      reviewScore: parseFloat(review.value.score),
      reviewComment: review.value.comment.trim()
    };

    console.log('Review Data:', reviewData);
    
    await reviewApi.writeInfluencerReview(selectedContract.value.contractId, reviewData);
    alert('리뷰가 성공적으로 저장되었습니다.');
    closeModal();
    // 리뷰 작성 후 목록 새로고침
    await fetchCompletedContracts();
  } catch (error) {
    console.error('리뷰 저장 실패:', error);
    if (error.response?.status === 400 || error.response?.status === 409 || error.response?.status === 500) {
      alert('이미 작성된 리뷰입니다. 한 계약에 대해서는 한 번만 리뷰를 작성할 수 있습니다.');
    } else {
      alert('리뷰 저장에 실패했습니다. 다시 시도해주세요.');
    }
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

onMounted(() => {
  console.log('=== MyPageWriteReview 컴포넌트 마운트됨 ===');
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