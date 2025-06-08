<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { homeAPI } from '@/api/home'
import { useRouter } from 'vue-router'

// 라우터 설정
const router = useRouter()

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
const hours = ref(0)
const minutes = ref(0)
const seconds = ref(0)
const timerInterval = ref(null)

// 데이터 로딩 상태
const loading = ref({
  categories: false,
  campaigns: false,
  influencers: false,
  sidebarCategories: false,
  endingTodayProducts: false,
  banners: false
})

// 에러 상태
const error = ref({
  categories: null,
  campaigns: null,
  influencers: null,
  sidebarCategories: null,
  endingTodayProducts: null,
  banners: null
})

// 내일 자정까지 남은 시간 계산
const calculateTimeUntilMidnight = () => {
  const now = new Date()
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  tomorrow.setHours(0, 0, 0, 0)
  
  const diff = tomorrow - now
  
  hours.value = Math.floor(diff / (1000 * 60 * 60))
  minutes.value = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  seconds.value = Math.floor((diff % (1000 * 60)) / 1000)
}

// 시간 형식 변환 함수 (2자리 숫자로 표시)
const formatTimeUnit = (unit) => {
  return unit.toString().padStart(2, '0')
}

// 데이터 불러오기 함수들
const fetchCategories = async () => {
  try {
    loading.value.categories = true
    const data = await homeAPI.getCategories()
    console.log('Categories response:', data)
    categories.value = data.map(category => ({
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
    '패션': '👗',
    '뷰티': '💄',
    '푸드/먹방': '🍽️',
    '엔터테인먼트': '🎮',
    '여행': '✈️',
    '음악': '🎵',
    '전자기기': '📱',
    'Vlog/라이프스타일': '🎥',
    '교육': '📚',
    '동물/펫': '🐾',
    '스포츠': '⚽'
  }
  return iconMap[categoryName] || '📱'
}

const fetchCampaigns = async () => {
  try {
    loading.value.campaigns = true
    const data = await homeAPI.getCampaigns()
    console.log('Campaign response:', data)
    campaignProducts.value = data.map(campaign => {
      return {
        id: campaign.productId,
        name: campaign.productName,
        image: campaign.productImg,
        category: campaign.productCategory,
        reviews: 0,
        rating: 4.5,
        timeLeft: calculateTimeLeft(campaign.productDeadline),
        status: campaign.productPublishStatus,
        advertiser: campaign.companyName,
        description: campaign.productDesc,
        condition: campaign.productCondition
      }
    })
    console.log('Mapped campaigns:', campaignProducts.value)
  } catch (err) {
    console.error('캠페인 상품 로딩 실패:', err)
    error.value.campaigns = '캠페인 상품을 불러오는데 실패했습니다.'
  } finally {
    loading.value.campaigns = false
  }
}

// 시간 계산 유틸리티 함수 추가
const calculateTimeLeft = (deadline) => {
  const now = new Date()
  const deadlineDate = new Date(deadline)
  const diff = deadlineDate - now
  
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  if (days > 0) {
    return `${days}일 남음`
  }
  
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  return `${hours}시간 남음`
}

const fetchInfluencers = async () => {
  try {
    loading.value.influencers = true
    const data = await homeAPI.getInfluencers()
    console.log('Influencers response:', data)
    influencers.value = data.map(influencer => ({
      id: influencer.id,
      name: influencer.name,
      profileImage: influencer.profileImage,
      category: influencer.category,
      subscribers: typeof influencer.subscribers === 'string' ? 
        influencer.subscribers : 
        influencer.subscribers.toLocaleString(),
      reviews: influencer.avgCommentCount || 0,
      rating: 4.5,
      platform: 'YouTube',
      averageViews: influencer.avgViewCount
    }))
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
    const data = await homeAPI.getSidebarCategories()
    console.log('Sidebar categories response:', data)
    sidebarCategories.value = data
  } catch (err) {
    console.error('사이드바 카테고리 로딩 실패:', err)
    error.value.sidebarCategories = '사이드바 카테고리를 불러오는데 실패했습니다.'
  } finally {
    loading.value.sidebarCategories = false
  }
}

const fetchEndingTodayProducts = async () => {
  try {
    loading.value.endingTodayProducts = true
    const data = await homeAPI.getEndingTodayProducts()
    console.log('Ending today products response:', data)
    endingTodayProducts.value = data
  } catch (err) {
    console.error('오늘 마감 상품 로딩 실패:', err)
    error.value.endingTodayProducts = '오늘 마감 상품을 불러오는데 실패했습니다.'
  } finally {
    loading.value.endingTodayProducts = false
  }
}

const fetchBanners = async () => {
  try {
    loading.value.banners = true
    const data = await homeAPI.getBanners()
    console.log('Banners response:', data)
    banners.value = data.filter(banner => banner.active)
  } catch (err) {
    console.error('배너 로딩 실패:', err)
    error.value.banners = '배너를 불러오는데 실패했습니다.'
  } finally {
    loading.value.banners = false
  }
}

// 슬라이더 관련 상태
const currentSlide = ref(0)
const autoSlideInterval = ref(null)

const nextSlide = () => {
  if (!banners.value || banners.value.length === 0) return
  currentSlide.value = (currentSlide.value + 1) % banners.value.length
}

const prevSlide = () => {
  if (!banners.value || banners.value.length === 0) return
  currentSlide.value = currentSlide.value === 0 
    ? banners.value.length - 1 
    : currentSlide.value - 1
}

const startAutoSlide = () => {
  if (autoSlideInterval.value) {
    clearInterval(autoSlideInterval.value)
  }
  autoSlideInterval.value = setInterval(() => {
    nextSlide()
  }, 5000)
}

// 카테고리 슬라이더 관련 상태 추가
const categorySlideIndex = ref(0)
const categoriesPerSlide = 6

const displayedCategories = computed(() => {
  const start = categorySlideIndex.value * categoriesPerSlide
  return categories.value.slice(start, start + categoriesPerSlide)
})

const prevCategorySlide = () => {
  if (categorySlideIndex.value > 0) {
    categorySlideIndex.value--
  }
}

const nextCategorySlide = () => {
  if (categorySlideIndex.value < Math.ceil(categories.value.length / categoriesPerSlide) - 1) {
    categorySlideIndex.value++
  }
}

// 인플루언서 카드 클릭 핸들러
const handleInfluencerClick = (influencerId) => {
  router.push(`/channels/${influencerId}`)
}

onMounted(async () => {
  // 모든 데이터 동시에 불러오기
  await Promise.all([
    fetchCategories(),
    fetchCampaigns(),
    fetchInfluencers(),
    fetchSidebarCategories(),
    fetchEndingTodayProducts(),
    fetchBanners()
  ])

  // 배너 데이터가 로드되면 자동 슬라이드 시작
  if (banners.value && banners.value.length > 0) {
    startAutoSlide()
  }

  // 초기 시간 계산
  calculateTimeUntilMidnight()

  // 타이머 시작 (1초마다 업데이트)
  timerInterval.value = setInterval(() => {
    if (seconds.value > 0) {
      seconds.value--
    } else {
      if (minutes.value > 0) {
        minutes.value--
        seconds.value = 59
      } else {
        if (hours.value > 0) {
          hours.value--
          minutes.value = 59
          seconds.value = 59
        } else {
          // 자정이 되면 타이머 리셋
          calculateTimeUntilMidnight()
        }
      }
    }
  }, 1000)
})

onUnmounted(() => {
  if (timerInterval.value) {
    clearInterval(timerInterval.value)
  }
  if (autoSlideInterval.value) {
    clearInterval(autoSlideInterval.value)
  }
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

// 상위 4개 인플루언서만 표시
const displayedInfluencers = computed(() => {
  return influencers.value.slice(0, 4)
})

// 별점 표시를 위한 computed 함수들
const createStarRating = (rating) => {
  return {
    full: Math.floor(rating),
    partial: rating % 1,
    empty: 5 - Math.ceil(rating)
  }
}
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
            <div v-for="(banner, index) in banners" :key="banner.id" 
                 :class="['banner-item', { active: index === currentSlide }]">
              <img :src="banner.image" :alt="banner.title" class="banner-image" @error="handleImageError" />
              <div class="banner-content">
                <h2>{{ banner.title }}</h2>
                <p>{{ banner.description }}</p>
                <button class="start-button">Start Linki →</button>
              </div>
            </div>
            <div class="slider-dots">
              <span v-for="(_, index) in banners" :key="index" 
                    :class="['dot', { active: index === currentSlide }]"
                    @click="currentSlide = index"></span>
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
          <div class="navigation-arrows">
            <button class="nav-arrow" @click="prevCategorySlide" :disabled="categorySlideIndex === 0">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                <path d="M15 18L9 12L15 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
            <button class="nav-arrow" @click="nextCategorySlide" 
                    :disabled="categorySlideIndex >= Math.ceil(categories.length / categoriesPerSlide) - 1">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
        </div>
        <div class="category-slider">
          <div class="category-grid">
            <div v-for="category in displayedCategories" :key="category.id" 
                 :class="['category-item', { active: category.active }]">
              <div class="category-icon">
                {{ category.icon }}
              </div>
              <span class="category-name">{{ category.name }}</span>
            </div>
          </div>
        </div>
        <div class="center-button-wrapper">
          <button class="more-button" @click="$router.push({ name: 'campaigns' })">전체보기</button>
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
                    <span class="time">{{ formatTimeUnit(hours) }}</span>
                    <span class="label">Hours</span>
                  </div>
                  <span class="timer-divider">:</span>
                  <div class="timer-item">
                    <span class="time">{{ formatTimeUnit(minutes) }}</span>
                    <span class="label">Minutes</span>
                  </div>
                  <span class="timer-divider">:</span>
                  <div class="timer-item">
                    <span class="time">{{ formatTimeUnit(seconds) }}</span>
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
          <div v-for="product in displayedProducts" 
               :key="product.id" 
               class="product-card"
               @click="router.push(`/campaign/${product.id}`)"
               style="cursor: pointer;">
            <div class="product-image">
              <img :src="product.image" :alt="product.name" @error="handleImageError" />
            </div>
            <div class="product-info">
              <h4 class="product-name">{{ product.name }}</h4>
              <div class="rating">
                <div class="stars-container">
                  <div class="stars-bg">★★★★★</div>
                  <div 
                    class="stars-filled" 
                    :style="{ width: `${(product.rating / 5) * 100}%` }"
                  >★★★★★</div>
                </div>
                <span class="rating-score">{{ product.rating.toFixed(1) }}</span>
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
            @click="handleInfluencerClick(influencer.id)"
          >
            <div class="influencer-image">
              <img :src="influencer.profileImage" :alt="influencer.name" @error="handleImageError" />
            </div>
            <div class="influencer-info">
              <h4 class="influencer-name">{{ influencer.name }}</h4>
              <div class="rating">
                <div class="stars-container">
                  <div class="stars-bg">★★★★★</div>
                  <div 
                    class="stars-filled" 
                    :style="{ width: `${(influencer.rating / 5) * 100}%` }"
                  >★★★★★</div>
                </div>
                <span class="rating-score">{{ influencer.rating.toFixed(1) }}</span>
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
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0;
  transition: opacity 0.5s ease;
}

.banner-item.active {
  opacity: 1;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-content {
  position: absolute;
  top: 50%;
  left: 8%;
  transform: translateY(-50%);
  color: white;
  text-align: left;
  width: 60%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 2rem;
}

.banner-content h2 {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
  line-height: 1.2;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.banner-content p {
  font-size: 1.2rem;
  margin-bottom: 2.5rem;
  opacity: 0.9;
  max-width: 100%;
}

.start-button {
  background-color: #8B5CF6;
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: auto;
  width: fit-content;
}

.start-button:hover {
  background-color: #7C3AED;
  transform: translateY(-2px);
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
  margin: 2rem 0;
  position: relative;
}

.category-slider {
  overflow: hidden;
  position: relative;
  margin: 0 -1rem;
  padding: 0 1rem;
}

.category-grid {
  display: flex;
  gap: 1rem;
  transition: transform 0.3s ease;
}

.category-item {
  flex: 0 0 calc(16.666% - 1rem);
  min-width: 120px;
  aspect-ratio: 1;
  background: #fff;
  border-radius: 12px;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.category-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.category-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.category-name {
  font-size: 0.9rem;
  text-align: center;
  color: #333;
}

.navigation-arrows {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.nav-arrow {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 1px solid #e0e0e0;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.nav-arrow:hover {
  background: #f5f5f5;
}

.nav-arrow:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
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
  letter-spacing: 1px;
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
  gap: 4px;
}

.rating-score {
  font-weight: 600;
  color: #333;
  font-size: 0.9rem;
}

.review-count {
  color: #666;
  font-size: 0.8rem;
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

.stars-container {
  position: relative;
  display: inline-block;
  font-size: 0.9rem;
  line-height: 1;
}

.stars-bg {
  color: #E0E0E0;  /* 배경 별 색상 - 연한 회색 */
}

.stars-filled {
  position: absolute;
  top: 0;
  left: 0;
  white-space: nowrap;
  overflow: hidden;
  color: #FFD700;  /* 채워진 별 색상 - 금색 */
}

.rating {
  display: flex;
  align-items: center;
  gap: 4px;
}

.review-count {
  color: #666;
  font-size: 0.8rem;
}

.center-button-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
  width: 100%;
  text-align: center;
}

.category-section .more-button {
  margin: 0 auto;
  padding: 0.75rem 2rem;
  border-radius: 8px;
  background-color: #8B5CF6;
  color: white;
  border: none;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.category-section .more-button:hover {
  background-color: #7C3AED;
  transform: translateY(-2px);
}

.influencer-section .more-button {
  padding: 0.5rem 1rem;
  border-radius: 6px;
  background-color: transparent;
  color: #8B5CF6;
  border: 1px solid #8B5CF6;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.influencer-section .more-button:hover {
  background-color: #8B5CF6;
  color: white;
}
</style>

