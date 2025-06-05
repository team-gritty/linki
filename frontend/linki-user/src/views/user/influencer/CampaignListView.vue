<template>
  <div class="campaign-list">
    <div class="category-tabs">
      <div 
        v-for="category in categories" 
        :key="category.id"
        :class="['category-tab', { active: selectedCategory === category.id }]"
        @click="selectCategory(category.id)"
      >
        {{ category.name }}
      </div>
    </div>

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
            :key="campaign.productId"
            class="campaign-card"
            @click="goToCampaignDetail(campaign.productId)"
          >
            <img :src="campaign.productImg" :alt="campaign.productName">
            <div class="campaign-info">
              <h3>{{ campaign.productName }}</h3>
              <p>{{ campaign.productCondition }}</p>
              <div class="campaign-meta">
                <span class="category">{{ campaign.productCategory }}</span>
                <span class="deadline">마감일: {{ formatDate(campaign.productDeadline) }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 전체 캠페인 섹션 -->
      <section class="all-campaigns-section">
        <div class="list-header">
          <h2 class="section-title">전체 캠페인</h2>
          <div class="sort-options">
            <select v-model="sortBy" @change="fetchCampaigns">
              <option value="createdAt">최신순</option>
              <option value="productDeadline">마감임박순</option>
            </select>
          </div>
        </div>

        <div class="campaign-grid">
          <div 
            v-for="campaign in campaigns" 
            :key="campaign.productId"
            class="campaign-card"
            @click="goToCampaignDetail(campaign.productId)"
          >
            <img :src="campaign.productImg" :alt="campaign.productName">
            <div class="campaign-info">
              <h3>{{ campaign.productName }}</h3>
              <p>{{ campaign.productCondition }}</p>
              <div class="campaign-meta">
                <span class="category">{{ campaign.productCategory }}</span>
                <span class="deadline">마감일: {{ formatDate(campaign.productDeadline) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 페이지네이션 -->
        <div v-if="totalPages > 1" class="pagination">
          <button 
            :disabled="currentPage === 1" 
            @click="changePage(currentPage - 1)"
            class="page-button"
          >
            이전
          </button>
          <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
          <button 
            :disabled="currentPage === totalPages" 
            @click="changePage(currentPage + 1)"
            class="page-button"
          >
            다음
          </button>
        </div>
      </section>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { campaignAPI } from '@/api/campaign'

const router = useRouter()
const loading = ref(false)
const error = ref(null)
const campaigns = ref([])
const popularCampaigns = ref([])
const currentPage = ref(1)
const itemsPerPage = 10
const totalPages = ref(1)
const selectedCategory = ref('all')
const sortBy = ref('createdAt')

const categories = [
  { id: 'all', name: '전체' },
  { id: 'BEAUTY', name: '뷰티' },
  { id: 'SPORTS', name: '스포츠' },
  { id: 'FOOD', name: '음식' },
  { id: 'IT', name: '전자기기' },
  { id: 'TRAVEL', name: '여행' },
  { id: 'EDUCATION', name: '교육' },
  { id: 'PETS', name: '동물/펫' },
  { id: 'FASHION', name: '패션' },
  { id: 'VLOG/LIFESTYLE', name: 'Vlog/라이프스타일' },
  { id: 'MUSIC', name: '음악' },
]

// 인기 캠페인 불러오기 (최신 3개)
const fetchPopularCampaigns = async () => {
  try {
    const response = await campaignAPI.getCampaigns({
      _limit: 3,
      _sort: 'createdAt',
      _order: 'desc'
    })
    popularCampaigns.value = response.campaigns
  } catch (err) {
    console.error('인기 캠페인 로딩 실패:', err)
  }
}

// 전체 캠페인 불러오기 (페이지네이션 적용)
const fetchCampaigns = async () => {
  try {
    loading.value = true
    error.value = null
    
    const params = {
      _page: currentPage.value,
      _limit: itemsPerPage,
      _sort: sortBy.value,
      _order: 'desc'
    }

    if (selectedCategory.value !== 'all') {
      params.productCategory = selectedCategory.value
    }

    const response = await campaignAPI.getCampaigns(params)
    campaigns.value = response.campaigns
    totalPages.value = Math.ceil(response.totalItems / itemsPerPage)
  } catch (err) {
    console.error('캠페인 목록 로딩 실패:', err)
    error.value = '캠페인 목록을 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
}

const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  currentPage.value = 1 // 카테고리 변경 시 첫 페이지로
  fetchCampaigns()
}

const changePage = (page) => {
  currentPage.value = page
  fetchCampaigns()
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const goToCampaignDetail = (campaignId) => {
  router.push(`/campaign/${campaignId}`)
}

onMounted(() => {
  fetchPopularCampaigns()
  fetchCampaigns()
})
</script>

<style scoped>
.campaign-list {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
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
  gap: 10px;
  overflow-x: auto;
  padding-bottom: 15px;
  margin-bottom: 20px;
}

.category-tab {
  padding: 8px 16px;
  border-radius: 20px;
  background-color: #f5f5f5;
  cursor: pointer;
  white-space: nowrap;
}

.category-tab.active {
  background-color: #7c3aed;
  color: white;
}

.section-title {
  margin: 20px 0;
  font-size: 1.5rem;
  font-weight: bold;
}

.campaign-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
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

.campaign-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
}

.category {
  background: #f3f4f6;
  padding: 4px 8px;
  border-radius: 4px;
  color: #374151;
}

.deadline {
  color: #7c3aed;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 40px;
}

.page-button {
  padding: 8px 16px;
  border: 1px solid #7c3aed;
  background: white;
  color: #7c3aed;
  border-radius: 4px;
  cursor: pointer;
}

.page-button:disabled {
  border-color: #e5e7eb;
  color: #9ca3af;
  cursor: not-allowed;
}

.page-button:not(:disabled):hover {
  background: #7c3aed;
  color: white;
}

.page-info {
  color: #4b5563;
}

@media (max-width: 768px) {
  .campaign-grid {
    grid-template-columns: 1fr;
  }
}
</style> 