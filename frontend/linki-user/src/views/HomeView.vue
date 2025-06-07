<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import axios from 'axios'

// API 기본 URL 설정
const API_BASE_URL = 'http://localhost:3000'

// 데이터 상태
const categories = ref([])
const campaignProducts = ref([])
const influencers = ref([])
const sidebarCategories = ref([])
const endingTodayProducts = ref([])
const banners = ref([])

// 타이머 상태
const days = ref(3)
const hours = ref(23)
const minutes = ref(19)
const seconds = ref(56)

// 데이터 로딩 상태
const loading = ref({
  categories: false,
  campaigns: false,
  influencers: false,
  sidebarCategories: false,
  endingToday: false,
  banners: false
})

// 에러 상태
const error = ref({
  categories: null,
  campaigns: null,
  influencers: null,
  sidebarCategories: null,
  endingToday: null,
  banners: null
})

// 데이터 불러오기 함수들
const fetchCategories = async () => {
  try {
    loading.value.categories = true
    const response = await axios.get(`${API_BASE_URL}/categories`)
    console.log('Categories response:', response.data)
    categories.value = response.data.map(category => ({
      ...category,
      icon: getIconComponent(category.name),
      active: false
    }))
  } catch (err) {
    console.error('카테고리 로딩 실패:', err)
    error.value.categories = '카테고리를 불러오는데 실패했습니다.'
  } finally {
    loading.value.categories = false
  }
}

// 아이콘 컴포넌트 매핑 함수
const getIconComponent = (categoryName) => {
  const iconMap = {
    '뷰티': '💄',
    '스포츠': '⚽',
    '음식': '🍽️',
    '전자기기': '📱',
    '여행': '✈️',
    '동물/펫': '🐾'
  }
  return iconMap[categoryName] || '📱'
}

const fetchCampaignProducts = async () => {
  try {
    loading.value.campaigns = true
    const response = await axios.get(`${API_BASE_URL}/campaignProducts`)
    console.log('Campaign products response:', response.data)
    campaignProducts.value = response.data
  } catch (err) {
    console.error('캠페인 상품 로딩 실패:', err)
    error.value.campaigns = '캠페인 상품을 불러오는데 실패했습니다.'
  } finally {
    loading.value.campaigns = false
  }
}

const fetchInfluencers = async () => {
  try {
    loading.value.influencers = true
    const response = await axios.get(`${API_BASE_URL}/influencers`)
    console.log('Influencers response:', response.data)
    influencers.value = response.data
  } catch (err) {
    console.error('인플루언서 로딩 실패:', err)
    error.value.influencers = '인플루언서 정보를 불러오는데 실패했습니다.'
  } finally {
    loading.value.influencers = false
  }
}

const fetchSidebarCategories = async () => {
  try {
    loading.value.sidebarCategories = true
    const response = await axios.get(`${API_BASE_URL}/sidebarCategories`)
    console.log('Sidebar categories response:', response.data)
    sidebarCategories.value = response.data
  } catch (err) {
    console.error('사이드바 카테고리 로딩 실패:', err)
    error.value.sidebarCategories = '사이드바 카테고리를 불러오는데 실패했습니다.'
  } finally {
    loading.value.sidebarCategories = false
  }
}

const fetchEndingTodayProducts = async () => {
  try {
    loading.value.endingToday = true
    const response = await axios.get(`${API_BASE_URL}/endingTodayProducts`)
    console.log('Ending today products response:', response.data)
    endingTodayProducts.value = response.data
  } catch (err) {
    console.error('오늘 마감 상품 로딩 실패:', err)
    error.value.endingToday = '오늘 마감 상품을 불러오는데 실패했습니다.'
  } finally {
    loading.value.endingToday = false
  }
}

const fetchBanners = async () => {
  try {
    loading.value.banners = true
    const response = await axios.get(`${API_BASE_URL}/banners`)
    console.log('Banners response:', response.data)
    banners.value = response.data
  } catch (err) {
    console.error('배너 로딩 실패:', err)
    error.value.banners = '배너를 불러오는데 실패했습니다.'
  } finally {
    loading.value.banners = false
  }
}

// 컴포넌트 마운트 시 데이터 로드
onMounted(async () => {
  // 모든 데이터 동시에 불러오기
  await Promise.all([
    fetchCategories(),
    fetchCampaignProducts(),
    fetchInfluencers(),
    fetchSidebarCategories(),
    fetchEndingTodayProducts(),
    fetchBanners()
  ])

  // 타이머 시작
  timerInterval = setInterval(() => {
    if (seconds.value > 0) {
      seconds.value--
    } else {
      seconds.value = 59
      if (minutes.value > 0) {
        minutes.value--
      } else {
        minutes.value = 59
        if (hours.value > 0) {
          hours.value--
        } else {
          hours.value = 23
          if (days.value > 0) {
            days.value--
          } else {
            clearInterval(timerInterval)
          }
        }
      }
    }
  }, 1000)
})

onUnmounted(() => {
  clearInterval(timerInterval)
})

// 이미지 에러 처리
const handleImageError = (e) => {
  e.target.src = '/placeholder.png'
  e.target.classList.add('error')
}

// 페이지네이션 관련 상태
const itemsPerPage = 4
const currentPage = ref(0)

// 현재 페이지에 표시될 상품들
const displayedProducts = computed(() => {
  const start = currentPage.value * itemsPerPage
  return campaignProducts.value.slice(start, start + itemsPerPage)
})

// 페이지 이동 함수
const nextPage = () => {
  if (currentPage.value < Math.ceil(campaignProducts.value.length / itemsPerPage) - 1) {
    currentPage.value++
  }
}

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
  }
}

// 오늘 마감하는 캠페인 페이지네이션
const endingTodayPage = ref(0)
const endingTodayItemsPerPage = 4

const displayedEndingToday = computed(() => {
  const start = endingTodayPage.value * endingTodayItemsPerPage
  return endingTodayProducts.value.slice(start, start + endingTodayItemsPerPage)
})

const prevEndingTodayPage = () => {
  if (endingTodayPage.value > 0) {
    endingTodayPage.value--
  }
}

const nextEndingTodayPage = () => {
  if (endingTodayPage.value < Math.ceil(endingTodayProducts.value.length / endingTodayItemsPerPage) - 1) {
    endingTodayPage.value++
  }
}

// 슬라이더 관련 상태
const currentSlide = ref(0)
const autoSlideInterval = ref(null)

const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % 5
}

const prevSlide = () => {
  currentSlide.value = currentSlide.value === 0 
    ? 4 
    : currentSlide.value - 1
}

onMounted(() => {
  // 기존 타이머 코드...

  // 자동 슬라이드 시작
  autoSlideInterval.value = setInterval(() => {
    nextSlide()
  }, 5000) // 5초마다 다음 슬라이드로
})

onUnmounted(() => {
  // 기존 타이머 정리 코드...
  
  // 자동 슬라이드 정리
  if (autoSlideInterval.value) {
    clearInterval(autoSlideInterval.value)
  }
})

// 상위 4개 인플루언서만 표시
const displayedInfluencers = computed(() => {
  return influencers.value.slice(0, 4)
})
</script>

<template>
  <div class="home">
    <div class="sidebar">
      <h3 class="sidebar-title">인플루언서</h3>
      <ul class="category-menu">
        <li class="menu-item">
          <button class="view-all-button"  @click="$router.push({ name: 'influencer-list' })">
            전체보기
          </button>
        </li>
        <li v-for="category in sidebarCategories" :key="category.id" class="menu-item">
          <a href="#" :class="{ active: category.active }">
            {{ category.name }}
            <span class="arrow">›</span>
          </a>
        </li>
      </ul>
    </div>

    <!-- 메인 컨텐츠 -->
    <div class="main-content">
      <!-- 배너 슬라이더 섹션 -->
      <section class="banner-section">
        <div class="banner-slider">
          <div v-if="loading.banners" class="loading">로딩 중...</div>
          <div v-else-if="error.banners" class="error">{{ error.banners }}</div>
          <template v-else>
            <div v-for="banner in banners" :key="banner.id" 
                 v-show="banner.id === currentSlide + 1"
                 class="banner-item">
              <img :src="banner.image" :alt="banner.title" class="banner-image" @error="handleImageError" />
              <div class="banner-content">
                <h2>{{ banner.title }}</h2>
                <button class="start-button">Start Linki →</button>
              </div>
            </div>
            <div class="slider-dots">
              <span v-for="i in banners.length" :key="i" 
                    :class="{ active: i === currentSlide + 1 }" 
                    class="dot"></span>
            </div>
          </template>
        </div>
      </section>

      <div class="section-divider"></div>

      <!-- 카테고리 섹션 -->
      <section class="category-section">
        <div class="section-header">
          <div class="title-wrapper">
            <span class="small-title highlight">
              <span class="vertical-bar"></span>종류별로 보는
            </span>
            <h3>캠페인 카테고리 선택</h3>
          </div>
        </div>
        <div class="category-grid">
          <div v-for="category in categories" :key="category.id" 
               :class="['category-item', { active: category.active }]">
            <div class="category-icon">
              {{ category.icon }}
            </div>
            <span class="category-name">{{ category.name }}</span>
          </div>
        </div>
        <div class="center-button-wrapper">
          <button class="more-button"  @click="$router.push({ name: 'campaigns' })">전체보기</button>
        </div>
      </section>

      <div class="section-divider"></div>

      <!-- 캠페인 상품 섹션 -->
      <section class="campaign-section">
        <div class="section-header">
          <div class="title-content">
            <div class="title-wrapper">
              <span class="small-title highlight">
                <span class="vertical-bar"></span>오늘 마감하는
              </span>
              <div class="campaign-title">
                <h3>캠페인</h3>
                <div class="timer">
                  <div class="timer-item">
                    <span class="time">{{ days }}</span>
                    <span class="label">Days</span>
                  </div>
                  <span class="timer-divider">:</span>
                  <div class="timer-item">
                    <span class="time">{{ hours }}</span>
                    <span class="label">Hours</span>
                  </div>
                  <span class="timer-divider">:</span>
                  <div class="timer-item">
                    <span class="time">{{ minutes }}</span>
                    <span class="label">Minutes</span>
                  </div>
                  <span class="timer-divider">:</span>
                  <div class="timer-item">
                    <span class="time">{{ seconds }}</span>
                    <span class="label">Seconds</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="navigation-arrows">
            <button class="nav-arrow" @click="prevPage" :disabled="currentPage === 0">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                <path d="M15 18L9 12L15 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
            <button class="nav-arrow" @click="nextPage" :disabled="currentPage >= Math.ceil(campaignProducts.length / itemsPerPage) - 1">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
        </div>

        <div class="product-grid">
          <div v-for="product in displayedProducts" :key="product.id" class="product-card">
            <div class="product-image">
              <img :src="product.image" :alt="product.name" @error="handleImageError" />
            </div>
            <div class="product-info">
              <h4 class="product-name">{{ product.name }}</h4>
              <div class="rating">
                <div class="stars">★★★★★</div>
                <span class="review-count">({{ product.reviews }})</span>
              </div>
              <span class="time-remaining">{{ product.timeLeft }}</span>
            </div>
          </div>
        </div>
      </section>

      <div class="section-divider"></div>

      <!-- 인플루언서 섹션 -->
      <section class="influencer-section">
        <div class="section-header">
          <div class="title-wrapper">
            <span class="small-title highlight">
              <span class="vertical-bar"></span>이번 달 주목할
            </span>
            <h3>인플루언서</h3>
          </div>
          <button class="more-button" @click="$router.push({ name: 'influencer-list' })">전체보기</button>
        </div>
        <div class="influencer-grid">
          <router-link 
            v-for="influencer in displayedInfluencers" 
            :key="influencer.id" 
            :to="{ name: 'channel-detail', params: { id: influencer.id }}"
            class="influencer-card"
          >
            <div class="influencer-image">
              <img :src="influencer.image" :alt="influencer.name" @error="handleImageError" />
            </div>
            <div class="influencer-info">
              <h4 class="influencer-name">{{ influencer.name }}</h4>
              <div class="rating">
                <div class="stars">★★★★★</div>
                <span class="review-count">({{ influencer.reviews }})</span>
              </div>
              <div class="influencer-stats">
                <span class="subscribers">구독자 {{ influencer.subscribers.toLocaleString() }}명</span>
                <span class="category">{{ influencer.category }}</span>
              </div>
            </div>
          </router-link>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.home {
  display: flex;
  gap: 24px;
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.sidebar {
  width: 200px;
  flex-shrink: 0;
  padding-top: 15px;
}

.main-content {
  flex: 1;
  max-width: 900px;
  padding-top: 15px;
}

.sidebar-title {
  font-size: 1.1rem;
  font-weight: bold;
  margin-bottom: 10px;
  color: #8B5CF6;
  text-align: left;
}

.category-menu {
  list-style: none;
  padding: 0;
  margin: 0;
}

.menu-item {
  margin: 0;
}

.menu-item a {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 12px;
  color: #666;
  text-decoration: none;
  border-radius: 5px;
  transition: all 0.3s ease;
  font-size: 0.95rem;
  letter-spacing: -0.3px;
}

.menu-item a:hover {
  background: #f5f5f5;
}

.menu-item a.active {
  color: #666;
}

.arrow {
  color: #666;
  font-size: 1.2rem;
  font-weight: 300;
}

.view-all-button {
  width: 100%;
  padding: 6px 12px;
  background-color: white;
  color: #8B5CF6;
  border: 1px solid #8B5CF6;
  border-radius: 5px;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-bottom: 10px;
}

.view-all-button:hover {
  background-color: #8B5CF6;
  color: white;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.title-wrapper {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.vertical-bar {
  display: inline-block;
  width: 4px;
  height: 16px;
  background-color: #8B5CF6;
  margin-right: 8px;
  border-radius: 2px;
}

.small-title {
  font-size: 0.9rem;
  font-weight: bold;
  display: flex;
  align-items: center;
}

.highlight {
  color: #8B5CF6;
}

.section-header h3 {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
  margin: 0;
}

/* 배너 섹션 스타일 */
.banner-section {
  width: 100%;
  margin-bottom: 40px;
  margin-top: 45px; /* 전체보기 버튼과 높이 맞추기 */
}

.banner-slider {
  position: relative;
  width: 100%;
  height: 400px; /* 높이 약간 조정 */
  border-radius: 12px;
  overflow: hidden;
}

.banner-item {
  position: relative;
  width: 100%;
  height: 100%;
  background: linear-gradient(to right, #8B5CF6, #6366F1);
  padding: 0 40px;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-content {
  position: absolute;
  top: 50%;
  left: 40px;
  transform: translateY(-50%);
  color: white;
}

.banner-content h2 {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 20px;
}

.start-button {
  padding: 12px 24px;
  background: white;
  color: #8B5CF6;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.start-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.slider-dots {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.2s ease;
}

.dot.active {
  background: white;
  transform: scale(1.2);
}

/* 카테고리 섹션 스타일 */
.category-section {
  margin-bottom: 48px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 24px;
  margin: 24px 0;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px;
  border: 1px solid #eee;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.category-item:hover {
  background-color: #8B5CF6;
  color: white;
  border-color: #8B5CF6;
}

.category-icon {
  margin-bottom: 12px;
  font-size: 2rem; /* 이모지 크기 조절 */
}

.category-icon img {
  display: none; /* 기존 img 태그 숨김 */
}

.category-name {
  font-size: 0.9rem;
  color: inherit;
}

.center-button-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  margin-bottom: 8px;
}

.category-section .more-button {
  margin: 0;
}

/* 상품 그리드 스타일 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-top: 24px;
}

.product-card {
  text-decoration: none;
  border-radius: 12px;
  overflow: hidden;
  background: white;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  border: 1px solid transparent;
}

.product-card:hover {
  transform: translateY(-5px);
  border-color: #8B5CF6;
  box-shadow: 0 8px 16px rgba(139, 92, 246, 0.15);
}

.product-image {
  width: 100%;
  padding-top: 100%;
  position: relative;
}

.product-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 16px;
}

.product-name {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
  margin-bottom: 4px;
}

.rating {
  display: flex;
  align-items: center;
  gap: 6px;
}

.time-remaining {
  display: inline-block;
  margin-top: 8px;
  font-size: 0.9rem;
  color: white;
  background-color: #8B5CF6;
  padding: 6px 12px;
  border-radius: 6px;
  font-weight: 500;
}

.campaign-status {
  margin-top: auto;
  display: flex;
  justify-content: center;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #eee;
  width: 100%;
}

.status-badge {
  background: #8B5CF6;
  color: white;
  padding: 6px 10px;
  border-radius: 6px;
  font-size: 0.85rem;
  white-space: nowrap;
  font-weight: 500;
}

.rating {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: -4px;
}

.stars {
  color: #FFD700;
  font-size: 0.9rem;
}

.review-count {
  color: #666;
  font-size: 0.85rem;
}

/* 캠페인 섹션 네비게이션 화살표 스타일 */
.navigation-arrows {
  display: flex;
  gap: 12px;
}

.nav-arrow {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: white;
  border: 1px solid #E5E7EB;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #8B5CF6;
}

.nav-arrow:hover:not(:disabled) {
  background: #8B5CF6;
  color: white;
  border-color: #8B5CF6;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.2);
}

.nav-arrow:disabled {
  background: #F9FAFB;
  color: #D1D5DB;
  border-color: #E5E7EB;
  cursor: not-allowed;
}

.nav-arrow svg {
  width: 24px;
  height: 24px;
  transition: all 0.2s ease;
}

.nav-arrow:hover:not(:disabled) svg {
  transform: scale(1.1);
}

.category-grid,
.product-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 24px;
  transition: all 0.3s ease;
}

.product-grid {
  grid-template-columns: repeat(4, 1fr);
}

/* 타이머 스타일 */
.timer {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 20px;
  margin-top: 4px;
}

.timer-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.timer .time {
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
}

.timer .label {
  font-size: 0.8rem;
  color: #666;
  text-transform: uppercase;
}

.timer-divider {
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
  margin-top: -8px;
}

.campaign-title {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.campaign-title h3 {
  margin: 0;
}

.section-divider {
  height: 1px;
  background: #E5E7EB;
  margin: 48px 0;
  width: 100%;
}

.discount-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: #8B5CF6;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.9rem;
  font-weight: 500;
}

.influencer-section {
  margin-top: 24px;
}

.influencer-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-top: 24px;
}

.influencer-card {
  text-decoration: none;
  color: inherit;
  border: 1px solid #eee;
  border-radius: 12px;
  overflow: hidden;
  background: white;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.influencer-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(139, 92, 246, 0.15);
  border-color: #8B5CF6;
}

.influencer-image {
  position: relative;
  width: 100%;
  padding-top: 100%;
  background: #f5f5f5;
  overflow: hidden;
}

.influencer-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.influencer-card:hover .influencer-image img {
  transform: scale(1.05);
}

.influencer-info {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.influencer-name {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
}

.influencer-stats {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.subscribers {
  font-size: 0.9rem;
  color: #8B5CF6;
  font-weight: 500;
}

.category {
  font-size: 0.85rem;
  color: #666;
  background-color: #f5f5f5;
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-block;
  width: fit-content;
}

.rating {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: -4px;
}

.stars {
  color: #FFD700;
  font-size: 0.9rem;
}

.review-count {
  color: #666;
  font-size: 0.85rem;
}

.more-button {
  padding: 8px 24px;
  border: 1px solid #8B5CF6;
  border-radius: 4px;
  background: white;
  color: #8B5CF6;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.more-button:hover {
  background: #8B5CF6;
  color: white;
}

.product-image {
  width: 100%;
  height: 200px; /* 이미지 높이 조절 */
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 12px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-image img:hover {
  transform: scale(1.05);
}

.product-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.product-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.influencer-card {
  text-decoration: none;
  color: inherit;
  transition: transform 0.3s ease;
}

.influencer-card:hover {
  transform: translateY(-5px);
}

.loading, .error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  font-size: 1.1rem;
}
</style>

