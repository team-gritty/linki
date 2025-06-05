<template>
  <!-- 채널 상세 페이지의 리뷰탭 -->
  <div class="review-tab">
    <div v-if="loading" class="loading">Loading reviews...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="reviews.length === 0" class="no-reviews">
      No reviews available for this channel.
    </div>
    <div v-else class="reviews-container">
      <div v-for="review in reviews" :key="review.influencer_review_id" class="review-card">
        <div class="review-header">
          <div class="review-score">
            <span class="score">{{ review.influencer_review_score }}</span>
            <span class="max-score">/5.0</span>
          </div>
          <div class="review-date">{{ formatDate(review.created_at) }}</div>
        </div>
        <div class="review-comment">{{ review.influencer_review_comment }}</div>
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
      error: null
    }
  },
  methods: {
    async fetchReviews() {
      try {
        console.log(this);
        this.loading = true
        const response = await axios.get(`http://localhost:3001/influencer-reviews?influencer_id=${this.channelId}`)
        this.reviews = response.data
      } catch (err) {
        this.error = 'Failed to load reviews'
        console.error('Error fetching reviews:', err)
      } finally {
        this.loading = false
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
  padding: 20px;
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
  gap: 20px;
}

.review-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.review-score {
  font-size: 1.2em;
  font-weight: bold;
}

.score {
  color: #ff6b6b;
}

.max-score {
  color: #666;
  font-size: 0.8em;
}

.review-date {
  color: #666;
  font-size: 0.9em;
}

.review-comment {
  color: #333;
  line-height: 1.5;
}
</style> 