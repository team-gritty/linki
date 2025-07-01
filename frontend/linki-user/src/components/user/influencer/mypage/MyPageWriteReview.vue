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
              <div class="detail-row">
                <div class="detail-item">
                  <span class="label">계약 기간</span>
                  <span class="value">{{ formatDate(contract.contractStartDate) }} ~ {{ formatDate(contract.contractEndDate) }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">계약 금액</span>
                  <span class="value">{{ formatCurrency(contract.contractAmount) }}원</span>
                </div>
              </div>
              <div class="detail-row">
                <div class="detail-item">
                  <span class="label">캠페인명</span>
                  <span class="value">{{ contract.campaignTitle }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">정산 상태</span>
                  <span class="value completed">정산 완료</span>
                </div>
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
        
        <!-- 안내 문구 추가 -->
        <div class="review-notice">
          <div class="notice-icon">💡</div>
          <div class="notice-text">
            <strong>안내사항:</strong> 리뷰는 한 계약당 한 번만 작성 가능하며, 작성 후 수정이나 삭제가 불가능합니다. 신중하게 작성해 주세요.
          </div>
        </div>
        
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
import { reviewApi } from '@/api/review';

export default {
  name: 'MyPageWriteReview',
  
  setup() {
    const completedContracts = ref([]);
    const showModal = ref(false);
    const selectedContract = ref(null);
    
    const review = ref({
      score: '',
      comment: ''
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

    // 백엔드에서 리뷰 가능한 계약 목록 조회 (계약, 정산 모두 COMPLETED)
    const fetchCompletedContracts = async () => {
      try {
        console.log('Fetching reviewable contracts...');
        const response = await reviewApi.getReviewableContracts();
        completedContracts.value = Array.isArray(response) ? response : [];
        console.log('Fetched reviewable contracts:', completedContracts.value);
      } catch (error) {
        console.error('Error fetching reviewable contracts:', error);
        completedContracts.value = [];
      }
    };

    const openReviewModal = (contract) => {
      selectedContract.value = contract;
      review.value = {
        score: '',
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
        console.log('Submitting review for contract:', selectedContract.value.contractId);
        
        const reviewData = {
          contractId: selectedContract.value.contractId,
          advertiserReviewScore: parseFloat(review.value.score),
          advertiserReviewComment: review.value.comment.trim()
        };

        console.log('Review data:', reviewData);
        
        await reviewApi.submitAdvertiserReview(reviewData);
        alert('리뷰가 성공적으로 저장되었습니다.');
        closeModal();
        // 리뷰 작성 후 목록 새로고침
        fetchCompletedContracts();
      } catch (error) {
        console.error('Error submitting review:', error);
        console.error('Error response:', error.response);
        
        // 백엔드에서 오는 오류 메시지 처리
        if (error.response?.data) {
          const errorData = error.response.data;
          console.log('Error data:', errorData);
          
          // 다양한 형태의 오류 메시지 확인
          const errorMessage = errorData.message || errorData.error || errorData.details || '';
          
          // 400, 500 에러이면서 중복 리뷰와 관련된 키워드가 있는 경우
          if (error.response?.status === 400 || error.response?.status === 500 || error.response?.status === 409) {
            // 중복 리뷰와 관련된 다양한 키워드 확인
            if (errorMessage.includes('이미') || 
                errorMessage.includes('중복') || 
                errorMessage.includes('already') ||
                errorMessage.includes('duplicate') ||
                errorMessage.includes('exists') ||
                errorMessage.includes('Duplicate') ||
                errorMessage.includes('constraint') ||
                errorMessage.includes('UNIQUE') ||
                errorMessage.toLowerCase().includes('review') && errorMessage.toLowerCase().includes('exist')) {
              alert('이미 작성된 리뷰입니다. 한 계약에 대해서는 한 번만 리뷰를 작성할 수 있습니다.');
              return;
            }
          }
          
          // 일반적인 중복 리뷰 확인
          if (errorMessage.includes('이미') || errorMessage.includes('중복') || errorMessage.includes('already')) {
            alert('이미 작성된 리뷰입니다. 한 계약에 대해서는 한 번만 리뷰를 작성할 수 있습니다.');
          } else if (errorMessage) {
            alert(`리뷰 저장 실패: ${errorMessage}`);
          } else {
            // 500 에러인 경우 일반적으로 중복 리뷰일 가능성이 높음
            alert('이미 작성된 리뷰입니다. 한 계약에 대해서는 한 번만 리뷰를 작성할 수 있습니다.');
          }
        } else if (error.response?.status === 400) {
          // 400 Bad Request - 백엔드에서 명시적으로 처리한 오류
          alert('이미 작성된 리뷰입니다. 한 계약에 대해서는 한 번만 리뷰를 작성할 수 있습니다.');
        } else if (error.response?.status === 409) {
          // 409 Conflict 상태코드는 일반적으로 중복 데이터를 의미
          alert('이미 작성된 리뷰입니다. 한 계약에 대해서는 한 번만 리뷰를 작성할 수 있습니다.');
        } else if (error.response?.status === 500) {
          // 500 에러인 경우도 중복 리뷰일 가능성이 높음
          alert('이미 작성된 리뷰입니다. 한 계약에 대해서는 한 번만 리뷰를 작성할 수 있습니다.');
        } else {
          alert('리뷰 저장에 실패했습니다. 다시 시도해주세요.');
        }
      }
    };

    const formatDate = (dateString) => {
      if (!dateString) return '';
      return new Date(dateString).toLocaleDateString('ko-KR');
    };

    const formatCurrency = (amount) => {
      if (!amount) return '0';
      return Number(amount).toLocaleString('ko-KR');
    };

    onMounted(() => {
      fetchCompletedContracts();
    });

    return {
      completedContracts,
      showModal,
      review,
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
  margin-bottom: 16px;
}

.detail-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 12px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  padding: 8px 0;
}

.label {
  font-size: 14px;
  color: #666;
  margin-bottom: 6px;
  font-weight: 500;
}

.value {
  font-weight: 600;
  font-size: 15px;
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

/* 안내 문구 스타일 */
.review-notice {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 16px;
  margin: 16px 0 20px 0;
}

.notice-icon {
  font-size: 20px;
  flex-shrink: 0;
  margin-top: 2px;
}

.notice-text {
  font-size: 14px;
  line-height: 1.5;
  color: #495057;
}

.notice-text strong {
  color: #212529;
  font-weight: 600;
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