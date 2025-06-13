<template>
  <div class="campaign-detail">
    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>캠페인 정보를 불러오는 중입니다...</p>
    </div>

    <!-- 에러 메시지 -->
    <div v-else-if="error" class="error-message">
      {{ error }}
      <button @click="fetchCampaignDetail" class="retry-button">다시 시도</button>
    </div>

    <!-- 캠페인 상세 정보 -->
    <div v-else-if="campaign" class="campaign-content">
      <div class="campaign-header">
        <img :src="campaign.campaignImg" :alt="campaign.campaignName" class="campaign-image">
        <div class="campaign-info">
          <div class="campaign-meta">
            <span class="category">{{ campaign.campaignCategory }}</span>
            <span class="deadline">마감일: {{ formatDate(campaign.campaignDeadline) }}</span>
          </div>
          <h1 class="campaign-title">{{ campaign.campaignName }}</h1>
          <p class="company-name">{{ campaign.companyName }}</p>
          <div class="campaign-description">
            {{ campaign.campaignDesc }}
          </div>
        </div>
      </div>

      <div class="campaign-details">
        <section class="info-section">
          <h2>캠페인 조건</h2>
          <div class="condition-box">
            {{ campaign.campaignCondition }}
          </div>
        </section>

        <section class="info-section">
          <h2>캠페인 상태</h2>
          <div class="status-box">
            <span :class="['status-badge', campaign.campaignStatus.toLowerCase()]">
              {{ getStatusText(campaign.campaignStatus) }}
            </span>
          </div>
        </section>

        <!-- 광고주 리뷰 섹션 -->
        <section class="info-section reviews-section">
          <div class="reviews-header">
            <h2>광고주 리뷰</h2>
          </div>
          <div v-if="loadingReviews" class="loading-state">
            <div class="loading-spinner"></div>
            <p>리뷰를 불러오는 중입니다...</p>
          </div>
          <div v-else-if="reviewError" class="error-message">
            {{ reviewError }}
            <button @click="fetchAdvertiserReviews" class="retry-button">다시 시도</button>
          </div>
          <div v-else-if="reviews.length === 0" class="no-reviews">
            아직 등록된 리뷰가 없습니다.
          </div>
          <div v-else class="reviews-list">
            <div v-for="review in reviews" :key="review.reviewId" class="review-item">
              <div class="review-content">
                <div class="review-rating">
                  <div class="stars">
                    <div class="stars-filled" :style="{ width: `${review.reviewScore * 20}%` }"></div>
                    <div class="stars-empty"></div>
                  </div>
                  <span class="rating-text">{{ review.reviewScore.toFixed(1) }}</span>
                </div>
                <p class="review-comment">{{ review.reviewComment }}</p>
                <span class="review-date">{{ formatDate(review.reviewCreatedAt) }}</span>
              </div>
            </div>
          </div>
        </section>

        <div class="action-buttons">
          <button class="apply-button" @click="goToProposal">지원하기</button>
          <button class="back-button" @click="goBack">목록으로</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { campaignAPI } from '@/api/campaign'
import { reviewApi } from '@/api/review'

const route = useRoute()
const router = useRouter()
const campaign = ref(null)
const loading = ref(false)
const error = ref(null)

// 리뷰 관련 상태
const reviews = ref([])
const loadingReviews = ref(false)
const reviewError = ref(null)

const fetchCampaignDetail = async () => {
  try {
    loading.value = true
    error.value = null
    const data = await campaignAPI.getCampaignDetail(route.params.id)
    campaign.value = data
    console.log('Campaign detail:', data)
    // 캠페인 정보를 가져온 후 광고주 리뷰를 가져옴
    if (data?.advertiserId) {
      await fetchAdvertiserReviews()
    }
  } catch (err) {
    console.error('캠페인 상세 정보 로딩 실패:', err)
    error.value = '캠페인 정보를 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
}

const fetchAdvertiserReviews = async () => {
  try {
    loadingReviews.value = true
    reviewError.value = null
    // 캠페인의 campaignId로 리뷰 조회
    if (campaign.value?.campaignId) {
      const data = await reviewApi.getAdvertiserReviewsByCampaign(campaign.value.campaignId)
      reviews.value = data
    }
  } catch (err) {
    console.error('광고주 리뷰 로딩 실패:', err)
    reviewError.value = '리뷰를 불러오는데 실패했습니다.'
  } finally {
    loadingReviews.value = false
  }
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getStatusText = (status) => {
  const statusMap = {
    'ACTIVE': '진행중',
    'CLOSED': '마감',
    'DRAFT': '임시저장'
  }
  return statusMap[status] || status
}

const goToProposal = () => {
  router.push(`/campaign/${route.params.id}/proposal`)
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchCampaignDetail()
})

// route.params.id가 바뀔 때마다 캠페인 상세 재조회
watch(() => route.params.id, () => {
  fetchCampaignDetail()
})
</script>

<style scoped>
.campaign-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.loading-state {
  text-align: center;
  padding: 40px;
}

.loading-spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #7c3aed;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  text-align: center;
  color: #dc2626;
  padding: 20px;
  background: #fee2e2;
  border-radius: 8px;
  margin: 20px 0;
}

.retry-button {
  background: #7c3aed;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  margin-top: 10px;
  cursor: pointer;
}

.retry-button:hover {
  background: #6d28d9;
}

.campaign-header {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  margin-bottom: 40px;
}

.campaign-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 12px;
}

.campaign-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.campaign-meta {
  display: flex;
  gap: 12px;
  align-items: center;
}

.category {
  background: #f3f4f6;
  padding: 4px 12px;
  border-radius: 20px;
  color: #374151;
  font-size: 0.9rem;
}

.deadline {
  color: #7c3aed;
  font-size: 0.9rem;
}

.campaign-title {
  font-size: 2rem;
  font-weight: bold;
  color: #111827;
  margin: 0;
}

.company-name {
  font-size: 1.1rem;
  color: #4b5563;
  margin: 0;
}

.campaign-description {
  font-size: 1rem;
  line-height: 1.6;
  color: #4b5563;
}

.campaign-details {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.info-section {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.info-section h2 {
  font-size: 1.25rem;
  color: #111827;
  margin: 0 0 16px 0;
}

.condition-box {
  background: #f9fafb;
  padding: 16px;
  border-radius: 8px;
  color: #4b5563;
  line-height: 1.6;
}

.status-box {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.9rem;
}

.status-badge.active {
  background: #dcfce7;
  color: #166534;
}

.status-badge.closed {
  background: #fee2e2;
  color: #991b1b;
}

.status-badge.draft {
  background: #f3f4f6;
  color: #374151;
}

.action-buttons {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}

.apply-button {
  flex: 1;
  padding: 12px 24px;
  background: #7c3aed;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.apply-button:hover {
  background: #6d28d9;
}

.back-button {
  padding: 12px 24px;
  background: white;
  color: #4b5563;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s;
}

.back-button:hover {
  background: #f3f4f6;
  border-color: #d1d5db;
}

@media (max-width: 768px) {
  .campaign-header {
    grid-template-columns: 1fr;
  }

  .campaign-image {
    height: 300px;
  }

  .action-buttons {
    flex-direction: column;
  }
}

.reviews-section {
  margin-top: 24px;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  background: #f9fafb;
  padding: 24px;
  border-radius: 12px;
}

.review-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.review-rating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stars {
  position: relative;
  display: inline-block;
  font-size: 20px;
  line-height: 1;
  color: #e5e7eb;
}

.stars-filled {
  position: absolute;
  top: 0;
  left: 0;
  white-space: nowrap;
  overflow: hidden;
  color: #fbbf24;
  z-index: 1;
}

.stars-empty {
  position: relative;
  z-index: 0;
}

.stars-filled::before {
  content: "★★★★★";
}

.stars-empty::before {
  content: "★★★★★";
}

.rating-text {
  font-size: 15px;
  font-weight: 600;
  color: #4b5563;
}

.review-comment {
  color: #374151;
  line-height: 1.6;
  margin: 0;
  font-size: 15px;
}

.review-date {
  color: #6b7280;
  font-size: 14px;
}

.no-reviews {
  text-align: center;
  padding: 24px;
  color: #6b7280;
  background: #f9fafb;
  border-radius: 8px;
}

.reviews-header {
  margin-bottom: 24px;
}

.average-rating,
.review-header {
  display: none;
}

@media (max-width: 768px) {
  .review-item {
    padding: 20px;
  }
}
</style> 