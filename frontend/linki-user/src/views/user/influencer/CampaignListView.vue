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
                <span class="campaign-category" v-if="campaign.campaignCategory">{{ campaign.campaignCategory }}</span>
                <span class="campaign-category" v-else>카테고리 미지정</span>
                <span class="deadline" v-if="campaign.campaignDeadline">마감일: {{ formatDate(campaign.campaignDeadline) }}</span>
                <span class="deadline" v-else>마감일 미지정</span>
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
                <span class="campaign-category" v-if="campaign.campaignCategory">{{ campaign.campaignCategory }}</span>
                <span class="campaign-category" v-else>카테고리 미지정</span>
                <span class="deadline" v-if="campaign.campaignDeadline">마감일: {{ formatDate(campaign.campaignDeadline) }}</span>
                <span class="deadline" v-else>마감일 미지정</span>
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
    popularCampaigns.value = response.campaigns.slice(0, 3)
    
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
    
    const params = {
      _sort: sortBy.value,
      _order: 'desc',
      ...additionalParams
    }

    if (selectedCategory.value && selectedCategory.value !== '전체') {
      params.campaignCategory = selectedCategory.value
    }

    console.log('Fetching with params:', params) // 디버깅용

    const response = await campaignAPI.getCampaigns(params)
    console.log('Total campaigns from API:', response.campaigns.length) // 디버깅용
    
    // 인기 캠페인에 이미 표시된 캠페인들의 ID 목록
    const popularCampaignIds = popularCampaigns.value.map(campaign => campaign.campaignId)
    console.log('Popular campaign IDs:', popularCampaignIds) // 디버깅용
    
    // 인기 캠페인에 없는 캠페인들만 필터링
    const filteredCampaigns = response.campaigns.filter(campaign => 
      !popularCampaignIds.includes(campaign.campaignId)
    )
    console.log('Filtered campaigns (excluding popular):', filteredCampaigns.length) // 디버깅용
    
    // 전체 아이템 수 계산 (인기 캠페인 제외)
    const totalFilteredItems = filteredCampaigns.length
    totalPages.value = Math.ceil(totalFilteredItems / itemsPerPage)
    console.log(`Total filtered items: ${totalFilteredItems}, Items per page: ${itemsPerPage}, Total pages: ${totalPages.value}`) // 디버깅용
    
    // 현재 페이지에 해당하는 캠페인만 추출
    const startIndex = (currentPage.value - 1) * itemsPerPage
    const endIndex = startIndex + itemsPerPage
    campaigns.value = filteredCampaigns.slice(startIndex, endIndex)
    
    console.log(`Showing page ${currentPage.value} of ${totalPages.value}`) // 디버깅용
    console.log(`Start index: ${startIndex}, End index: ${endIndex}`) // 디버깅용
    console.log('Fetched campaigns (current page):', campaigns.value.length) // 디버깅용
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
  const additionalParams = {}
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
.campaign-list {
  padding: 20px;
  max-width: 1300px;
  margin: 0 auto;
  overflow-x: visible;
}

.popular-section,
.all-campaigns-section {
  margin-bottom: 40px;
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

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0;
}

.sort-options select {
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #e5e7eb;
}

.category-tabs {
  display: flex;
  gap: 0px;
  overflow-x: auto;
  padding: 16px 0;
  margin: 0 0 32px 0;
  -ms-overflow-style: none;  /* IE and Edge */
  scrollbar-width: none;  /* Firefox */
  position: sticky;
  top: 0;
  min-width: 100%;
}

/* Hide scrollbar for Chrome, Safari and Opera */
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

.section-title {
  margin: 20px 0;
  font-size: 1.5rem;
  font-weight: bold;
}

.campaign-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.campaign-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s;
  background: white;
}

.campaign-card:hover {
  transform: translateY(-4px);
}

.campaign-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.campaign-info {
  padding: 15px;
}

.campaign-info h3 {
  margin: 0 0 8px 0;
  font-size: 1.1rem;
  color: #111827;
}

.campaign-info p {
  margin: 0 0 12px 0;
  color: #6b7280;
  font-size: 0.9rem;
  line-height: 1.4;
}

.campaign-meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.campaign-category {
  display: inline-block;
  background: #f3f4f6;
  color: #374151;
  border-radius: 8px;
  padding: 4px 14px;
  font-size: 15px;
  font-weight: 700;
}

.deadline {
  color: #7c3aed;
}

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

@media (max-width: 768px) {
  .campaign-grid {
    grid-template-columns: 1fr;
  }
}
</style> 