<template>
  <!-- 채널 상세 페이지의 리뷰 탭 -->
  <div class="review-tab">
    <!-- 리뷰 탭의 상단 영역: 제목과 평균 평점/리뷰 개수 -->
    <div class="review-header-row">
      <h2>광고주 리뷰</h2>
      <!-- 리뷰가 있으면 평균 평점과 개수를 보여줌 -->
      <div class="review-summary" v-if="reviews.length">
        <!-- 별점 표시 영역 -->
        <span class="stars">
          <!-- 5개의 별을 반복 렌더링 -->
          <span v-for="n in 5" :key="n">
            <!-- 가득 찬 별 -->
            <svg v-if="avgScore >= n" width="22" height="22" viewBox="0 0 22 22" fill="#FFC107"><polygon points="11,2 14,8 21,8 15.5,12.5 17.5,20 11,15.5 4.5,20 6.5,12.5 1,8 8,8"/></svg>
            <!-- 반 별 -->
            <svg v-else-if="avgScore >= n-0.5" width="22" height="22" viewBox="0 0 22 22"><defs><linearGradient :id="'half'+n"><stop offset="50%" stop-color="#FFC107"/><stop offset="50%" stop-color="#eee"/></linearGradient></defs><polygon points="11,2 14,8 21,8 15.5,12.5 17.5,20 11,15.5 4.5,20 6.5,12.5 1,8 8,8" :fill="'url(#half'+n+')'"/></svg>
            <!-- 빈 별 -->
            <svg v-else width="22" height="22" viewBox="0 0 22 22" fill="#eee"><polygon points="11,2 14,8 21,8 15.5,12.5 17.5,20 11,15.5 4.5,20 6.5,12.5 1,8 8,8"/></svg>
          </span>
        </span>
        <!-- 평균 평점 표시 -->
        <span class="avg-score">{{ avgScore.toFixed(1) }}/5</span>
        <!-- 리뷰 개수 표시 -->
        <span class="count">({{ reviews.length }})</span>
      </div>
    </div>
    <!-- 로딩 중 표시 -->
    <div v-if="loading" class="loading">리뷰 로딩중.....</div>
    <!-- 에러 메시지 표시 -->
    <div v-else-if="error" class="error">{{ error }}</div>
    <!-- 리뷰가 없을 때 메시지 -->
    <div v-else-if="reviews.length === 0" class="no-reviews">
      등록된 리뷰가 없습니다.
    </div>
    <!-- 리뷰가 있으면 리뷰 리스트를 렌더링 -->
    <div v-else class="reviews-container">
      <!-- 각 리뷰 카드를 반복 렌더링 -->
      <div v-for="review in reviews" :key="review.influencerReviewId" class="review-card">
        <!-- 리뷰 카드의 헤더: 별점과 점수 -->
        <div class="review-card-header">
          <span class="stars">
            <span v-for="n in 5" :key="n">
              <!-- 가득 찬 별 -->
              <svg v-if="review.influencerReviewScore >= n" width="22" height="22" viewBox="0 0 22 22" fill="#FFC107"><polygon points="11,2 14,8 21,8 15.5,12.5 17.5,20 11,15.5 4.5,20 6.5,12.5 1,8 8,8"/></svg>
              <!-- 반 별 -->
              <svg v-else-if="review.influencerReviewScore >= n-0.5" width="22" height="22" viewBox="0 0 22 22"><defs><linearGradient :id="'half'+review.influencerReviewId+n"><stop offset="50%" stop-color="#FFC107"/><stop offset="50%" stop-color="#eee"/></linearGradient></defs><polygon points="11,2 14,8 21,8 15.5,12.5 17.5,20 11,15.5 4.5,20 6.5,12.5 1,8 8,8" :fill="'url(#half'+review.influencerReviewId+n+')'"/></svg>
              <!-- 빈 별 -->
              <svg v-else width="22" height="22" viewBox="0 0 22 22" fill="#eee"><polygon points="11,2 14,8 21,8 15.5,12.5 17.5,20 11,15.5 4.5,20 6.5,12.5 1,8 8,8"/></svg>
            </span>
          </span>
          <!-- 리뷰 점수 -->
          <span class="score">{{ review.influencerReviewScore.toFixed(1) }}</span>
        </div>
        <!-- 리뷰 카드 본문: 코멘트와 날짜 -->
        <div class="review-card-body">
          <div class="comment">{{ review.influencerReviewComment }}</div>
          <div class="date">{{ formatDate(review.createdAt) }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// axios 라이브러리 가져오기
import axios from 'axios'

export default {
  name: 'ReviewTab', // 컴포넌트 이름
  props: {
    channelId: { // 채널 ID를 부모로부터 props로 받음
      type: [Number, String],
      required: true
    }
  },
  data() {
    return {
      reviews: [],   // 리뷰 배열
      loading: true, // 로딩 상태
      error: null,   // 에러 메시지
      avgScore: 0    // 평균 평점
    }
  },
  methods: {
    // 리뷰 데이터를 불러오는 메서드
    async fetchReviews() {
      try {
        this.loading = true
        // API로부터 리뷰 데이터 가져오기 (influencerId로 필터링, camelCase)
        const response = await axios.get(`http://localhost:3000/influencer-reviews?influencerId=${this.channelId}`)
        this.reviews = response.data
        // 리뷰 통계 계산
        this.calcStats()
      } catch (err) {
        this.error = 'Failed to load reviews'
        console.error('Error fetching reviews:', err)
      } finally {
        this.loading = false
      }
    },
    // 평균 평점 계산 메서드
    calcStats() {
      if (this.reviews.length) {
        // 모든 평점의 합을 평균으로 계산 (camelCase)
        const avg = this.reviews.reduce((sum, r) => sum + (r.influencerReviewScore || 0), 0) / this.reviews.length
        this.avgScore = avg
        this.$emit('review-stats', { count: this.reviews.length, avg })
      } else {
        // 리뷰 없으면 0으로 초기화
        this.avgScore = 0
        this.$emit('review-stats', { count: 0, avg: 0 })
      }
    },
    // 날짜를 포맷팅하는 메서드
    formatDate(dateString) {
      const date = new Date(dateString)
      // 한국어 형식으로 날짜 표시
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }
  },
  created() {
    // 컴포넌트가 생성될 때 리뷰 불러오기
    this.fetchReviews()
  },
  watch: {
    // channelId가 바뀌면 다시 불러오기
    channelId() {
      this.fetchReviews()
    }
  }
}
</script>

<style scoped>
/* 리뷰 탭 스타일 */
.review-tab {
  padding: 20px 0 0 0;
  max-width: 900px;
  margin: 0 auto;
}
.review-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}
.review-header-row h2 {
  font-size: 1.3rem;
  font-weight: 800;
  color: #23262F;
}
.review-summary {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.1rem;
  font-weight: 700;
  color: #23262F;
}
.stars {
  display: flex;
  align-items: center;
  gap: 1px;
}
.avg-score {
  color: #8C30F5;
  font-weight: 800;
  margin-left: 4px;
}
.count {
  color: #888;
  font-weight: 500;
  margin-left: 2px;
}
.loading, .error, .no-reviews {
  text-align: center;
  padding: 20px;
  color: #666;
}
.error {
  color: #dc3545;
}
.reviews-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.review-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(140,48,245,0.06);
  padding: 28px 24px 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 0;
}
.review-card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
.review-card-header .stars {
  font-size: 1.1rem;
  color: #FFC107;
}
.review-card-header .score {
  font-weight: 700;
  color: #8C30F5;
  margin-left: 8px;
  font-size: 1.1rem;
}
.review-card-body {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 12px;
}

</style>
