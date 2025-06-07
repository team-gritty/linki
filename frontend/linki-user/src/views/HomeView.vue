<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import HeadphoneIcon from '../components/icons/headphone.svg'
import GamingIcon from '../components/icons/gaming.svg'
import ComputerIcon from '../components/icons/computer.svg'
import PhoneIcon from '../components/icons/phone.svg'
import SmartWatchIcon from '../components/icons/smartwatch.svg'
import CameraIcon from '../components/icons/camera.svg'
// 카테고리 데이터
const categories = ref([
  { id: 1, name: 'Phones', icon: PhoneIcon },
  { id: 2, name: 'Computers', icon: ComputerIcon },
  { id: 3, name: 'SmartWatch', icon: SmartWatchIcon },
  { id: 4, name: 'Camera', icon: CameraIcon },
  { id: 5, name: 'HeadPhones', icon: HeadphoneIcon },
  { id: 6, name: 'Gaming', icon: GamingIcon }
])

// 타이머 상태
const days = ref(3)
const hours = ref(23)
const minutes = ref(19)
const seconds = ref(56)

// 캠페인 상품 데이터
const campaignProducts = ref([
  {
    id: 1,
    name: 'HAVIT HV-G92 Gamepad',
    currentPrice: 100,
    originalPrice: 160,
    timeLeft: '2일 남음',
    reviews: 88,
    image: '/images/gamepad.jpg'
  },
  {
    id: 2,
    name: 'AK-900 Wired Keyboard',
    currentPrice: 960,
    originalPrice: 1160,
    timeLeft: '3일 남음',
    reviews: 75,
    image: '/images/keyboard.jpg'
  },
  {
    id: 3,
    name: 'IPS LCD Gaming Monitor',
    currentPrice: 370,
    originalPrice: 400,
    timeLeft: '5일 남음',
    reviews: 99,
    image: '/images/monitor.jpg'
  },
  {
    id: 4,
    name: 'S-Series Comfort Chair',
    currentPrice: 375,
    originalPrice: 400,
    timeLeft: '8일 남음',
    reviews: 99,
    image: '/images/chair.jpg'
  }
])

// 인플루언서 데이터
const influencers = ref([
  {
    id: 1,
    name: 'The north coat',
    image: '/images/north-coat.jpg',
    reviews: 65
  },
  {
    id: 2,
    name: 'Gucci duffle bag',
    image: '/images/gucci-bag.jpg',
    reviews: 65
  },
  {
    id: 3,
    name: 'RGB liquid CPU Cooler',
    image: '/images/cpu-cooler.jpg',
    reviews: 65
  },
  {
    id: 4,
    name: 'Small BookSelf',
    image: '/images/bookshelf.jpg',
    reviews: 65
  }
])

// 사이드바 카테고리 데이터
const sidebarCategories = ref([
  { id: 1, name: '패션', active: false },
  { id: 2, name: '뷰티', active: false },
  { id: 3, name: '푸드/방송', active: false },
  { id: 4, name: '엔터테이너/방송', active: false },
  { id: 5, name: '라이프/여행', active: false },
  { id: 6, name: 'Vlog/일상', active: false },
  { id: 7, name: '여행', active: false },
  { id: 8, name: 'ASMR', active: false },
  { id: 9, name: '게임', active: false }
])

// 타이머 로직
let timerInterval
onMounted(() => {
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

// 카테고리 섹션에서는 페이지네이션 제거
const displayedCategories = computed(() => categories.value)

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

// 오늘 마감하는 캠페인 데이터
const endingTodayProducts = ref([
  {
    id: 1,
    name: '무선 게이밍 마우스',
    timeLeft: '3시간 남음',
    reviews: 28,
    image: '/images/mouse.jpg'
  },
  {
    id: 2,
    name: '기계식 키보드',
    timeLeft: '5시간 남음',
    reviews: 42,
    image: '/images/keyboard.jpg'
  },
  {
    id: 3,
    name: '게이밍 헤드셋',
    timeLeft: '2시간 남음',
    reviews: 35,
    image: '/images/headset.jpg'
  },
  {
    id: 4,
    name: '울트라와이드 모니터',
    timeLeft: '1시간 남음',
    reviews: 56,
    image: '/images/monitor.jpg'
  },
  {
    id: 5,
    name: '게이밍 의자',
    timeLeft: '4시간 남음',
    reviews: 31,
    image: '/images/chair.jpg'
  },
  {
    id: 6,
    name: '웹캠',
    timeLeft: '6시간 남음',
    reviews: 24,
    image: '/images/webcam.jpg'
  },
  {
    id: 7,
    name: '마이크',
    timeLeft: '2시간 남음',
    reviews: 45,
    image: '/images/mic.jpg'
  },
  {
    id: 8,
    name: '스피커',
    timeLeft: '7시간 남음',
    reviews: 38,
    image: '/images/speaker.jpg'
  }
])

// 더 많은 테스트 데이터 추가
campaignProducts.value = [
  ...campaignProducts.value,
  {
    id: 5,
    name: 'Wireless Gaming Mouse',
    currentPrice: 80,
    originalPrice: 100,
    timeLeft: '4일 남음',
    reviews: 45,
    image: '/images/mouse.jpg'
  },
  {
    id: 6,
    name: 'Gaming Headset Pro',
    currentPrice: 150,
    originalPrice: 200,
    timeLeft: '6일 남음',
    reviews: 32,
    image: '/images/headset.jpg'
  },
  {
    id: 7,
    name: 'Mechanical Keyboard',
    currentPrice: 120,
    originalPrice: 150,
    timeLeft: '3일 남음',
    reviews: 56,
    image: '/images/keyboard.jpg'
  },
  {
    id: 8,
    name: 'Ultra-wide Monitor',
    currentPrice: 450,
    originalPrice: 500,
    timeLeft: '7일 남음',
    reviews: 28,
    image: '/images/monitor-wide.jpg'
  }
]

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
</script>

<template>
  <div class="home">
    <div class="sidebar">
      <h3 class="sidebar-title">인플루언서</h3>
      <ul class="category-menu">
        <li class="menu-item">
          <button class="view-all-button">
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
          <div class="banner-item">
            <img src="" alt="Banner" class="banner-image" />
            <div class="banner-content">
              <h2>구독하고 한번에 시작하기</h2>
              <button class="start-button">Start Linki →</button>
            </div>
          </div>
          <div class="slider-dots">
            <span v-for="i in 5" :key="i" :class="{ active: i === currentSlide + 1 }" class="dot"></span>
          </div>
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
          <div v-for="category in displayedCategories" :key="category.id" 
               :class="['category-item', { active: category.active }]">
            <div class="category-icon">
              <img :src="category.icon" :alt="category.name" />
            </div>
            <span class="category-name">{{ category.name }}</span>
          </div>
        </div>
        <div class="center-button-wrapper">
          <button class="more-button">전체보기</button>
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
              <div class="name-and-time">
                <h4 class="product-name">{{ product.name }}</h4>
                <span class="time-remaining">{{ product.timeLeft }}</span>
              </div>
              <div class="rating">
                <div class="stars">★★★★★</div>
                <span class="review-count">({{ product.reviews }})</span>
              </div>
              <button class="detail-button">상세보기</button>
            </div>
          </div>
        </div>
      </section>

      <div class="section-divider"></div>

      <!-- 인플루언서 섹션 -->
      <section class="influencer-section">
        <div class="section-header">
          <div class="title-content">
            <div class="title-wrapper">
              <span class="small-title highlight">
                <span class="vertical-bar"></span>이번 달 주목할
              </span>
              <h3>인플루언서</h3>
            </div>
          </div>
          <button class="more-button">전체보기</button>
        </div>
        <div class="influencer-grid">
          <div v-for="influencer in influencers" :key="influencer.id" class="influencer-card">
            <div class="influencer-image">
              <img :src="influencer.image" :alt="influencer.name" @error="handleImageError">
            </div>
            <div class="influencer-info">
              <h4 class="influencer-name">{{ influencer.name }}</h4>
              <div class="rating">
                <div class="stars">★★★★★</div>
                <span class="review-count">({{ influencer.reviews }})</span>
              </div>
              <button class="detail-button">상세보기</button>
            </div>
          </div>
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
}

.category-icon img {
  width: 32px;
  height: 32px;
  object-fit: contain;
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
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  display: flex;
  flex-direction: column;
}

.product-image {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 12px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-img.error {
  object-fit: contain;
  padding: 1rem;
}

.image-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #666;
  font-size: 0.9rem;
}

.image-placeholder span {
  padding: 1rem;
  text-align: center;
}

.time-left-badge {
  display: none;
}

.product-info {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
}

.name-and-time {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 8px;
}

.product-name {
  margin: 0;
  font-size: 1rem;
  color: #333;
  line-height: 1.4;
  flex: 1;
}

.time-remaining {
  background: #8B5CF6;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.85rem;
  white-space: nowrap;
}

.rating {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stars {
  color: #FFD700;
}

.review-count {
  color: #666;
  font-size: 0.9rem;
}

.detail-button {
  margin-top: auto;
  padding: 8px;
  background: white;
  border: 1px solid #8B5CF6;
  color: #8B5CF6;
  border-radius: 4px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.detail-button:hover {
  background: #8B5CF6;
  color: white;
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
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  display: flex;
  flex-direction: column;
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
}

.influencer-info {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.influencer-name {
  margin: 0;
  font-size: 1rem;
  color: #333;
  line-height: 1.4;
}

.rating {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stars {
  color: #FFD700;
}

.review-count {
  color: #666;
  font-size: 0.9rem;
}

.detail-button {
  margin-top: auto;
  padding: 8px;
  background: white;
  border: 1px solid #8B5CF6;
  color: #8B5CF6;
  border-radius: 4px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.detail-button:hover {
  background: #8B5CF6;
  color: white;
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
</style>

