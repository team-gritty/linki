<template>
  <!-- 채널 상세 페이지의 리뷰탭 -->
  <div class="review-tab">
    <div class="review-header-row">
      <h2>광고주 리뷰</h2>
      <div class="review-summary" v-if="reviews.length">
        <span class="stars">
          <span v-for="n in 5" :key="n">
            <svg v-if="avgScore >= n" width="22" height="22" viewBox="0 0 22 22" fill="#FFC107"><polygon points="11,2 14,8 21,8 15.5,12.5 17.5,20 11,15.5 4.5,20 6.5,12.5 1,8 8,8"/></svg>
            <svg v-else-if="avgScore >= n-0.5" width="22" height="22" viewBox="0 0 22 22"><defs><linearGradient :id="'half'+n"><stop offset="50%" stop-color="#FFC107"/><stop offset="50%" stop-color="#eee"/></linearGradient></defs><polygon points="11,2 14,8 21,8 15.5,12.5 17.5,20 11,15.5 4.5,20 6.5,12.5 1,8 8,8" :fill="'url(#half'+n+')'"/></svg>
            <svg v-else width="22" height="22" viewBox="0 0 22 22" fill="#eee"><polygon points="11,2 14,8 21,8 15.5,12.5 17.5,20 11,15.5 4.5,20 6.5,12.5 1,8 8,8"/></svg>
          </span>
        </span>
        <span class="avg-score">{{ avgScore.toFixed(1) }}/5</span>
        <span class="count">({{ reviews.length }})</span>
      </div>
    </div>
    <div v-if="loading" class="loading">Loading reviews...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="reviews.length === 0" class="no-reviews">
      등록된 리뷰가 없습니다.
    </div>
    <div v-else class="reviews-container">
      <div v-for="review in reviews" :key="review.influencer_review_id" class="review-card">
        <div class="review-card-header">
          <span class="stars">
            <span v-for="n in 5" :key="n">
              <svg v-if="review.influencer_review_score >= n" width="22" height="22" viewBox="0 0 22 22" fill="#FFC107"><polygon points="11,2 14,8 21,8 15.5,12.5 17.5,20 11,15.5 4.5,20 6.5,12.5 1,8 8,8"/></svg>
              <svg v-else-if="review.influencer_review_score >= n-0.5" width="22" height="22" viewBox="0 0 22 22"><defs><linearGradient :id="'half'+review.influencer_review_id+n"><stop offset="50%" stop-color="#FFC107"/><stop offset="50%" stop-color="#eee"/></linearGradient></defs><polygon points="11,2 14,8 21,8 15.5,12.5 17.5,20 11,15.5 4.5,20 6.5,12.5 1,8 8,8" :fill="'url(#half'+review.influencer_review_id+n+')'"/></svg>
              <svg v-else width="22" height="22" viewBox="0 0 22 22" fill="#eee"><polygon points="11,2 14,8 21,8 15.5,12.5 17.5,20 11,15.5 4.5,20 6.5,12.5 1,8 8,8"/></svg>
            </span>
          </span>
          <span class="score">{{ review.influencer_review_score.toFixed(1) }}</span>
        </div>
        <div class="review-card-body">
          <div class="comment">{{ review.influencer_review_comment }}</div>
          <div class="date">{{ formatDate(review.created_at) }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ReviewTab',
  props: {
    channelId: {
      type: [Number, String],
      required: true
    }
  },
  data() {
    return {
      reviews: [],
      loading: true,
      error: null,
      avgScore: 0
    }
  },
  methods: {
    async fetchReviews() {
      try {
        this.loading = true
        const response = await axios.get(`http://localhost:3001/influencer-reviews?influencer_id=${this.channelId}`)
        this.reviews = response.data
        this.calcStats()
      } catch (err) {
        this.error = 'Failed to load reviews'
        console.error('Error fetching reviews:', err)
      } finally {
        this.loading = false
      }
    },
    calcStats() {
      if (this.reviews.length) {
        const avg = this.reviews.reduce((sum, r) => sum + (r.influencer_review_score || 0), 0) / this.reviews.length
        this.avgScore = avg
        this.$emit('review-stats', { count: this.reviews.length, avg })
      } else {
        this.avgScore = 0
        this.$emit('review-stats', { count: 0, avg: 0 })
      }
    },
    formatDate(dateString) {
      const date = new Date(dateString)
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }
  },
  created() {
    this.fetchReviews()
  },
  watch: {
    channelId() {
      this.fetchReviews()
    }
  }
}
</script>

<style scoped>
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
.review-card-body .comment {
  font-size: 1.08rem;
  color: #23262F;
  line-height: 1.7;
  flex: 1;
}
.review-card-body .date {
  color: #888;
  font-size: 1rem;
  font-weight: 500;
  margin-left: 18px;
  white-space: nowrap;
}
</style> 