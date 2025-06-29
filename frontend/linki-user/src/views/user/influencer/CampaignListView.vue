<template>
  <div class="campaign-list">
    <nav class="category-tabs" aria-label="Campaign categories">
      <div 
        v-for="category in categories" 
        :key="category.id"
        :class="['category-tab', { active: selectedCategory === category.id }]"
        @click="selectCategory(category.id)"
        role="button"
        :aria-selected="selectedCategory === category.id"
        :tabindex="0"
        @keyup.enter="selectCategory(category.id)"
      >
        {{ category.name }}
      </div>
    </nav>

    <!-- 로딩 상태 표시 -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>캠페인 목록을 불러오는 중입니다...</p>
    </div>

    <!-- 에러 메시지 -->
    <div v-else-if="error" class="error-message">
      {{ error }}
      <button @click="fetchCampaigns" class="retry-button">다시 시도</button>
    </div>

    <template v-else>
      <!-- 인기 캠페인 섹션 -->
      <section class="popular-section">
        <h2 class="section-title">인기 캠페인</h2>
        <div class="campaign-grid">
          <div 
            v-for="campaign in popularCampaigns" 
            :key="campaign.campaignId"
            class="campaign-card"
            @click="goToCampaignDetail(campaign.campaignId)"
          >
            <img :src="campaign.campaignImg" :alt="campaign.campaignName">
            <div class="campaign-info">
              <h3>{{ campaign.campaignName }}</h3>
              <p>{{ campaign.campaignCondition }}</p>
              <div class="campaign-meta-row">
                <div class="campaign-tags">
                  <span class="campaign-category" v-if="campaign.campaignCategory">{{ campaign.campaignCategory }}</span>
                  <span class="campaign-category" v-else>카테고리 미지정</span>
                  <span :class="['campaign-status', getStatusClass(campaign.campaignPublishStatus)]">
                    {{ getStatusText(campaign.campaignPublishStatus) }}
                  </span>
                </div>
                <span class="deadline" v-if="campaign.campaignDeadline">마감일: {{ formatDate(campaign.campaignDeadline) }}</span>
                <span class="deadline" v-else>마감일 미지정</span>
              </div>
              <div class="campaign-rating" v-if="campaign.averageRating">
                <div class="stars">
                  <div class="stars-filled" :style="{ width: `${campaign.averageRating * 20}%` }"></div>
                  <div class="stars-empty"></div>
                </div>
                <span class="rating-text">{{ campaign.averageRating.toFixed(1) }} ({{ campaign.reviewCount }})</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 전체 캠페인 섹션 -->
      <section class="all-campaigns-section">
        <div class="list-header">
          <h2 class="section-title">모든 캠페인</h2>
          <div class="sort-options">
            <select v-model="sortBy" @change="fetchCampaigns">
              <option value="createdAt">최신순</option>
              <option value="campaignDeadline">마감임박순</option>
            </select>
          </div>
        </div>

        <div class="campaign-grid">
          <div 
            v-for="campaign in campaigns" 
            :key="campaign.campaignId"
            class="campaign-card"
            @click="goToCampaignDetail(campaign.campaignId)"
          >
            <img :src="campaign.campaignImg" :alt="campaign.campaignName">
            <div class="campaign-info">
              <h3>{{ campaign.campaignName }}</h3>
              <p>{{ campaign.campaignCondition }}</p>
              <div class="campaign-meta-row">
                <div class="campaign-tags">
                  <span class="campaign-category" v-if="campaign.campaignCategory">{{ campaign.campaignCategory }}</span>
                  <span class="campaign-category" v-else>카테고리 미지정</span>
                  <span :class="['campaign-status', getStatusClass(campaign.campaignPublishStatus)]">
                    {{ getStatusText(campaign.campaignPublishStatus) }}
                  </span>
                </div>
                <span class="deadline" v-if="campaign.campaignDeadline">마감일: {{ formatDate(campaign.campaignDeadline) }}</span>
                <span class="deadline" v-else>마감일 미지정</span>
              </div>
              <div class="campaign-rating" v-if="campaign.averageRating">
                <div class="stars">
                  <div class="stars-filled" :style="{ width: `${campaign.averageRating * 20}%` }"></div>
                  <div class="stars-empty"></div>
                </div>
                <span class="rating-text">{{ campaign.averageRating.toFixed(1) }} ({{ campaign.reviewCount }})</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 페이지네이션 -->
        <div v-if="totalPages > 1" class="pagination">
          <button 
            :disabled="currentPage === 1" 
            @click="changePage(1)"
            class="page-button first-last"
          >
            처음
          </button>
          <button 
            :disabled="currentPage === 1" 
            @click="changePage(currentPage - 1)"
            class="page-button prev-next"
          >
            이전
          </button>
          
          <!-- 페이지 번호들 -->
          <div class="page-numbers">
            <button
              v-for="pageNum in getPageNumbers()"
              :key="pageNum"
              @click="changePage(pageNum)"
              :class="['page-number', { 'active': pageNum === currentPage }]"
            >
              {{ pageNum }}
            </button>
          </div>
          
          <button 
            :disabled="currentPage === totalPages" 
            @click="changePage(currentPage + 1)"
            class="page-button prev-next"
          >
            다음
          </button>
          <button 
            :disabled="currentPage === totalPages" 
            @click="changePage(totalPages)"
            class="page-button first-last"
          >
            마지막
          </button>
        </div>
        
        <!-- 페이지네이션 정보 (디버깅용) -->
        <div class="pagination-debug">
          <span>총 {{ totalPages }}페이지 | 현재: {{ currentPage }}페이지 | 항목: {{ campaigns.length }}개</span>
        </div>
      </section>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { campaignAPI } from '@/api/campaign'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const error = ref(null)
const campaigns = ref([])
const popularCampaigns = ref([])
const currentPage = ref(1)
const itemsPerPage = 15
const totalPages = ref(1)
const selectedCategory = ref('전체')
const sortBy = ref('createdAt')
const categories = ref([
  { id: '전체', name: '전체' }
])

// 카테고리 데이터 불러오기
const fetchCategories = async () => {
  try {
    const response = await campaignAPI.getCategories()
    categories.value = [
      { id: '전체', name: '전체' },
      ...response
    ]
  } catch (err) {
    console.error('카테고리 로딩 실패:', err)
    error.value = '카테고리 목록을 불러오는데 실패했습니다.'
  }
}

// URL 쿼리 파라미터로부터 카테고리 설정
const initializeFromQuery = () => {
  const categoryFromQuery = route.query.category
  if (categoryFromQuery) {
    selectedCategory.value = categoryFromQuery
  } else {
    selectedCategory.value = '전체'
  }
}

// 인기 캠페인 불러오기 (선택된 카테고리의 최신 3개)
const fetchPopularCampaigns = async () => {
  try {
    const params = {
      _sort: 'createdAt',
      _order: 'desc'
    }

    // 선택된 카테고리가 있으면 필터링 적용
    if (selectedCategory.value && selectedCategory.value !== '전체') {
      params.campaignCategory = selectedCategory.value
    }

    console.log('Fetching popular campaigns with params:', params) // 디버깅용

    const response = await campaignAPI.getCampaigns(params)
    
    // 항상 최신 3개만 가져오기 (백엔드에서 limit를 지원하지 않으므로 프론트에서 처리)
    const topCampaigns = response.campaigns.slice(0, 3)
    
    // 평점 정보 추가
    popularCampaigns.value = await addRatingsToCampaigns(topCampaigns)
    
    console.log('Fetched popular campaigns:', popularCampaigns.value) // 디버깅용
  } catch (err) {
    console.error('인기 캠페인 로딩 실패:', err)
  }
}

// 전체 캠페인 불러오기 (페이지네이션 적용)
const fetchCampaigns = async (additionalParams = {}) => {
  try {
    loading.value = true
    error.value = null
    
    // additionalParams의 _page가 있으면 우선 사용, 없으면 currentPage.value 사용
    const pageToFetch = additionalParams._page || currentPage.value
    
    const params = {
      _page: pageToFetch,
      _limit: itemsPerPage,
      _sort: sortBy.value,
      _order: 'desc',
      ...additionalParams
    }

    if (selectedCategory.value && selectedCategory.value !== '전체') {
      params.campaignCategory = selectedCategory.value
    }

    console.log('Fetching with pagination params:', params) // 디버깅용
    console.log('Current page state:', currentPage.value, 'Page to fetch:', pageToFetch) // 디버깅용

    try {
      // 먼저 페이지네이션 API 시도
      const response = await campaignAPI.getCampaignsWithPagination(params)
      console.log('Pagination response:', response) // 디버깅용
      
      // 평점 정보 추가
      campaigns.value = await addRatingsToCampaigns(response.campaigns)
      totalPages.value = response.totalPages
      
      // 응답받은 currentPage로 현재 페이지 상태 업데이트
      if (response.currentPage) {
        currentPage.value = response.currentPage
      }
      
      console.log(`Page ${response.currentPage} of ${response.totalPages} (state updated to: ${currentPage.value})`) // 디버깅용
      console.log('Fetched campaigns:', campaigns.value.length) // 디버깅용
    } catch (paginationError) {
      console.warn('페이지네이션 API 실패, 기존 API 사용:', paginationError)
      
      // 페이지네이션 API 실패 시 기존 API 사용
      const response = await campaignAPI.getCampaigns(params)
      console.log('Fallback response:', response) // 디버깅용
      
      const allCampaigns = response.campaigns
      
      // 프론트엔드에서 페이지네이션 처리
      const startIndex = (currentPage.value - 1) * itemsPerPage
      const endIndex = startIndex + itemsPerPage
      const paginatedCampaigns = allCampaigns.slice(startIndex, endIndex)
      
      // 평점 정보 추가
      campaigns.value = await addRatingsToCampaigns(paginatedCampaigns)
      totalPages.value = Math.ceil(allCampaigns.length / itemsPerPage)
      
      console.log(`Fallback pagination: Page ${currentPage.value} of ${totalPages.value}`)
      console.log('Fetched campaigns (fallback):', campaigns.value.length)
    }
  } catch (err) {
    console.error('캠페인 목록 로딩 실패:', err)
    error.value = '캠페인 목록을 불러오는데 실패했습니다.'
    campaigns.value = []
    totalPages.value = 1
  } finally {
    loading.value = false
  }
}

const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  currentPage.value = 1 // 카테고리 변경 시 첫 페이지로
  
  // URL 업데이트
  router.push({
    query: {
      ...route.query,
      category: categoryId === '전체' ? undefined : categoryId
    }
  })
  
  console.log('Selected category:', categoryId) // 디버깅용
  
  // 인기 캠페인과 전체 캠페인 데이터 모두 다시 불러오기
  fetchPopularCampaigns()
  fetchCampaigns({
    campaignCategory: categoryId === '전체' ? undefined : categoryId
  })
}

const changePage = (page) => {
  currentPage.value = page
  console.log(`Changing to page: ${page}`) // 디버깅용
  
  // 현재 선택된 카테고리를 유지하면서 페이지 변경
  const additionalParams = {
    _page: page  // 명시적으로 페이지 번호 전달
  }
  if (selectedCategory.value && selectedCategory.value !== '전체') {
    additionalParams.campaignCategory = selectedCategory.value
  }
  
  fetchCampaigns(additionalParams)
}

// 페이지네이션에 표시할 페이지 번호들 계산
const getPageNumbers = () => {
  const total = totalPages.value
  const current = currentPage.value
  const delta = 2 // 현재 페이지 좌우로 보여줄 페이지 수
  
  let start = Math.max(1, current - delta)
  let end = Math.min(total, current + delta)
  
  // 시작이나 끝 부분에서 더 많은 페이지를 보여주기 위해 조정
  if (current - delta <= 1) {
    end = Math.min(total, end + (delta - current + 1))
  }
  if (current + delta >= total) {
    start = Math.max(1, start - (current + delta - total))
  }
  
  const pages = []
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  
  return pages
}

const formatDate = (dateString) => {
  if (!dateString) return '';
  
  let date;
  
  // 다양한 날짜 형식 처리
  if (typeof dateString === 'string') {
    // "YYYY-MM-DD" 형식인 경우
    if (dateString.match(/^\d{4}-\d{2}-\d{2}$/)) {
      date = new Date(dateString + 'T00:00:00');
    } else {
      date = new Date(dateString);
    }
  } else if (Array.isArray(dateString) && dateString.length >= 3) {
    // [2024, 6, 19] 형식인 경우 (Java LocalDate가 배열로 올 수 있음)
    date = new Date(dateString[0], dateString[1] - 1, dateString[2]);
  } else {
    date = new Date(dateString);
  }
  
  // 유효한 날짜인지 확인
  if (isNaN(date.getTime())) {
    console.error('Invalid date:', dateString);
    return dateString.toString();
  }
  
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const goToCampaignDetail = (campaignId) => {
  router.push(`/campaign/${campaignId}`)
}

// 캠페인 상태 텍스트 변환
const getStatusText = (status) => {
  switch (status) {
    case 'ACTIVE':
      return '활성'
    case 'HIDDEN':
      return '비활성'
    default:
      return '미지정'
  }
}

// 캠페인 상태 CSS 클래스 반환
const getStatusClass = (status) => {
  switch (status) {
    case 'ACTIVE':
      return 'active'
    case 'HIDDEN':
      return 'hidden'
    default:
      return 'unknown'
  }
}

// 광고주 리뷰 가져오기 및 평균 평점 계산
const fetchAdvertiserReviews = async (campaignId) => {
  try {
    let reviews = await campaignAPI.getAdvertiserReviewsByCampaign(campaignId)
    
    console.log(`캠페인 ${campaignId}의 원본 리뷰 데이터:`, reviews)
    console.log('리뷰 타입:', typeof reviews)
    
    // 문자열로 온 경우 JSON 파싱 시도
    if (typeof reviews === 'string') {
      try {
        reviews = JSON.parse(reviews)
        console.log('파싱 후 리뷰 데이터:', reviews)
      } catch (parseError) {
        console.error('JSON 파싱 실패:', parseError)
        return null
      }
    }
    
    console.log('최종 리뷰 배열 여부:', Array.isArray(reviews))
    
    // 배열이고 길이가 0보다 큰 경우 처리
    if (Array.isArray(reviews) && reviews.length > 0) {
      console.log('리뷰 개수:', reviews.length)
      console.log('첫 번째 리뷰:', reviews[0])
      
      // 평균 평점 계산
      const totalScore = reviews.reduce((sum, review) => {
        const score = parseFloat(review.advertiserReviewScore || 0)
        console.log('리뷰 점수:', score)
        return sum + score
      }, 0)
      const averageRating = totalScore / reviews.length
      
      console.log('총 점수:', totalScore, '평균 평점:', averageRating)
      
      return {
        averageRating: Math.round(averageRating * 10) / 10, // 소수점 첫째자리까지
        reviewCount: reviews.length
      }
    }
    
    // 리뷰가 없거나 잘못된 형태의 데이터인 경우
    console.log('리뷰 없음 또는 잘못된 형태')
    return null
  } catch (err) {
    console.error('리뷰 로딩 실패:', err)
    return null
  }
}

// 캠페인 목록에 평점 정보 추가
const addRatingsToCampaigns = async (campaignList) => {
  const campaignsWithRatings = await Promise.all(
    campaignList.map(async (campaign) => {
      const ratingInfo = await fetchAdvertiserReviews(campaign.campaignId)
      return {
        ...campaign,
        averageRating: ratingInfo?.averageRating || null,
        reviewCount: ratingInfo?.reviewCount || 0
      }
    })
  )
  return campaignsWithRatings
}

// URL 쿼리 변경 감지
watch(
  () => route.query,
  () => {
    initializeFromQuery()
    fetchPopularCampaigns()
    fetchCampaigns()
  }
)

onMounted(() => {
  fetchCategories()
  initializeFromQuery()
  fetchPopularCampaigns()
  fetchCampaigns()
})
</script>

<style scoped>
/* ===== 컨테이너 & 레이아웃 ===== */
.campaign-list {
  padding: 20px;
  max-width: 1800px;
  margin: 0 auto;
  overflow-x: visible;
  width: 100%;
}

.popular-section,
.all-campaigns-section {
  margin-bottom: 40px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 25px 0;
}

.section-title {
  margin: 25px 0;
  font-size: 1.7rem;
  font-weight: bold;
  color: #1f2937;
}

/* ===== 로딩 & 에러 상태 ===== */
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
  0% { 
    transform: rotate(0deg); 
  }
  100% { 
    transform: rotate(360deg); 
  }
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

/* ===== 카테고리 탭 ===== */
.category-tabs {
  display: flex;
  gap: 0px;
  overflow-x: auto;
  padding: 16px 0;
  margin: 0 0 32px 0;
  -ms-overflow-style: none;
  scrollbar-width: none;
  position: sticky;
  top: 0;
  min-width: 100%;
}

.category-tabs::-webkit-scrollbar {
  display: none;
}

.category-tab {
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 20px;
  font-weight: 500;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s ease;
  background-color: #f3f4f6;
  color: #6b7280;
  border: none;
}

.category-tab:hover {
  background-color: #f3f4f6;
  color: #374151;
}

.category-tab.active {
  background-color: #7c3aed;
  color: white;
  font-weight: 600;
  box-shadow: 0 2px 4px rgba(124, 58, 237, 0.1);
}

.category-tab.active:hover {
  background-color: #6d28d9;
}

/* ===== 정렬 옵션 ===== */
.sort-options select {
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #e5e7eb;
}

/* ===== 캠페인 그리드 & 카드 ===== */
.campaign-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
  margin-bottom: 40px;
  width: 100%;
}

.campaign-card {
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
  min-height: 420px;
  width: 100%;
}

.campaign-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.campaign-card img {
  width: 100%;
  height: 260px;
  object-fit: cover;
}

.campaign-info {
  padding: 20px;
  height: 160px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.campaign-info h3 {
  margin: 0 0 10px 0;
  font-size: 1.25rem;
  color: #111827;
  font-weight: 600;
  line-height: 1.3;
  word-break: keep-all;
}

.campaign-info p {
  margin: 0 0 15px 0;
  color: #6b7280;
  font-size: 1rem;
  line-height: 1.5;
  flex-grow: 1;
  word-break: keep-all;
}

.campaign-meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  flex-wrap: wrap;
  gap: 8px;
}

.campaign-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

.campaign-category {
  display: inline-block;
  background: #f3f4f6;
  color: #374151;
  border-radius: 10px;
  padding: 6px 14px;
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
}

.campaign-status {
  display: inline-block;
  border-radius: 10px;
  padding: 6px 14px;
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
}

.campaign-status.active {
  background: #dcfce7;
  color: #166534;
}

.campaign-status.hidden {
  background: #fee2e2;
  color: #991b1b;
}

.campaign-status.unknown {
  background: #f3f4f6;
  color: #6b7280;
}

.campaign-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.stars {
  position: relative;
  display: inline-block;
  font-size: 16px;
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
  font-size: 14px;
  font-weight: 600;
  color: #4b5563;
}

.deadline {
  color: #7c3aed;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
}

/* ===== 페이지네이션 ===== */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin: 40px 0;
  padding: 20px 0;
}

.page-button {
  padding: 10px 16px;
  border: 1px solid #d1d5db;
  background: white;
  color: #374151;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.page-button:hover:not(:disabled) {
  background: #f3f4f6;
  border-color: #9ca3af;
}

.page-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: #f9fafb;
}

.page-button.first-last {
  background: #6366f1;
  color: white;
  border-color: #6366f1;
}

.page-button.first-last:hover:not(:disabled) {
  background: #4f46e5;
}

.page-button.prev-next {
  background: #7c3aed;
  color: white;
  border-color: #7c3aed;
}

.page-button.prev-next:hover:not(:disabled) {
  background: #6d28d9;
}

.page-numbers {
  display: flex;
  gap: 4px;
  margin: 0 12px;
}

.page-number {
  padding: 10px 14px;
  border: 1px solid #d1d5db;
  background: white;
  color: #374151;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  min-width: 40px;
  text-align: center;
  transition: all 0.2s ease;
}

.page-number:hover {
  background: #f3f4f6;
  border-color: #7c3aed;
  color: #7c3aed;
}

.page-number.active {
  background: #7c3aed;
  color: white;
  border-color: #7c3aed;
}

.pagination-debug {
  text-align: center;
  margin-top: 10px;
  color: #6b7280;
  font-size: 14px;
}

/* ===== 반응형 디자인 ===== */
@media (max-width: 1200px) {
  .campaign-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 25px;
  }
  
  .campaign-card {
    min-height: 400px;
  }
  
  .campaign-card img {
    height: 240px;
  }
  
  .section-title {
    font-size: 1.6rem;
  }
}

@media (max-width: 768px) {
  .campaign-list {
    padding: 15px 20px;
  }
  
  .campaign-grid {
    grid-template-columns: 1fr;
    max-width: 500px;
    gap: 20px;
  }
  
  .campaign-card {
    min-height: 350px;
  }
  
  .campaign-card img {
    height: 200px;
  }
  
  .campaign-info {
    height: 130px;
    padding: 18px;
  }
  
  .list-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
    margin: 20px 0;
  }
  
  .category-tabs {
    padding: 12px 0;
  }
  
  .category-tab {
    font-size: 18px;
    padding: 6px 16px;
  }
  
  .pagination {
    flex-wrap: wrap;
    gap: 5px;
  }
  
  .page-numbers {
    margin: 0 5px;
  }
  
  .section-title {
    font-size: 1.5rem;
    margin: 20px 0;
  }
}

@media (max-width: 480px) {
  .campaign-list {
    padding: 15px;
  }
  
  .campaign-grid {
    gap: 15px;
    max-width: 100%;
  }
  
  .campaign-card {
    min-height: 320px;
  }
  
  .campaign-card img {
    height: 180px;
  }
  
  .campaign-info {
    padding: 15px;
    height: 120px;
  }
  
  .campaign-info h3 {
    font-size: 1.1rem;
  }
  
  .campaign-info p {
    font-size: 0.9rem;
  }
  
  .section-title {
    font-size: 1.3rem;
    margin: 15px 0;
  }
  
  .category-tab {
    font-size: 16px;
    padding: 5px 12px;
  }
  
  .popular-section,
  .all-campaigns-section {
    margin-bottom: 30px;
  }
}
</style> 