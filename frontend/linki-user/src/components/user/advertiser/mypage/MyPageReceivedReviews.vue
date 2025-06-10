<template>
  <div class="received-reviews-content">
    <h1 class="content-title">받은 리뷰</h1>
    <div class="content-box">
      <div v-if="reviews.length === 0" class="no-reviews">
        <p>받은 리뷰가 없습니다.</p>
      </div>
      <div v-else class="reviews-list">
        <div v-for="review in reviews" :key="review.advertiserReviewId" class="review-item">
          <div class="review-header">
            <div class="review-info">
              <span class="contract-id">계약 ID: {{ review.contractId }}</span>
              <span class="review-date">{{ formatDate(review.advertiserReviewCreatedAt) }}</span>
            </div>
            <div class="review-score">
              <span class="score-label">평점</span>
              <div class="score-display">
                <span class="score-value">{{ review.advertiserReviewScore }}</span>
                <div class="stars">
                  <span
                    v-for="index in 5"
                    :key="index"
                    class="star"
                    :class="{ 'filled': index <= Math.round(review.advertiserReviewScore) }"
                  >
                    ★
                  </span>
                </div>
              </div>
            </div>
          </div>
          <div class="review-content">
            <p class="review-comment">{{ review.advertiserReviewComment }}</p>
          </div>
          <div class="review-visibility">
            <span :class="['visibility-badge', review.visibility ? 'visible' : 'hidden']">
              {{ review.visibility ? '공개' : '비공개' }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { reviewAPI } from '@/api/review';


export default {
  name: 'MyPageReceivedReviews',

  setup() {
    const reviews = ref([]);

    const fetchReviews = async () => {
      try {
        const response = await reviewApi.getAdvertiserReviews();
        reviews.value = response.data;
      } catch (error) {
        console.error('리뷰 목록 조회 실패:', error);
      }
    };

    const formatDate = (dateString) => {
      const date = new Date(dateString);
      return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
    };

    onMounted(() => {
      fetchReviews();
    });

    return {
      reviews,
      formatDate
    };
  }
};
</script>

<style scoped>
.received-reviews-content {
  padding: 24px;
}

.content-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #1a1a1a;
}

.content-box {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.no-reviews {
  text-align: center;
  padding: 48px 0;
  color: #666;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-item {
  padding: 20px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 12px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.review-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.review-info {
  display: flex;
  gap: 16px;
  color: #666;
  font-size: 14px;
}

.review-score {
  display: flex;
  align-items: center;
  gap: 12px;
}

.score-label {
  color: #666;
  font-size: 14px;
}

.score-display {
  display: flex;
  align-items: center;
  gap: 8px;
}

.score-value {
  font-size: 16px;
  font-weight: 600;
  color: #6c5ce7;
  min-width: 28px;
}

.stars {
  display: flex;
  gap: 2px;
}

.star {
  font-size: 16px;
  color: #e2e8f0;
}

.star.filled {
  color: #fbbf24;
}

.review-content {
  margin-bottom: 16px;
}

.review-comment {
  color: #1a1a1a;
  font-size: 15px;
  line-height: 1.5;
}

.review-visibility {
  display: flex;
  justify-content: flex-end;
}

.visibility-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.visible {
  background-color: #e6effd;
  color: #3b82f6;
}

.hidden {
  background-color: #f3f4f6;
  color: #6b7280;
}
</style> 