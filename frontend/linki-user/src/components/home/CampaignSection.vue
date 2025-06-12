<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { homeAPI } from '@/api/home'

const router = useRouter()
const campaignProducts = ref([])
const loading = ref(false)
const error = ref(null)

// 타이머 상태
const hours = ref(0)
const minutes = ref(0)
const seconds = ref(0)
const timerInterval = ref(null)

// 페이지네이션 관련 상태
const itemsPerPage = 4
const currentPage = ref(0)

// 현재 페이지에 표시될 상품들
const displayedProducts = computed(() => {
  const start = currentPage.value * itemsPerPage
  return campaignProducts.value.slice(start, start + itemsPerPage)
})

// 시간 형식 변환 함수 (2자리 숫자로 표시)
const formatTimeUnit = (unit) => {
  return unit.toString().padStart(2, '0')
}

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

const fetchCampaigns = async () => {
  try {
    loading.value = true
    const data = await homeAPI.getCampaigns()
    console.log('Campaign response:', data)
    campaignProducts.value = data.map(campaign => {
      return {
        id: campaign.campaignId,
        name: campaign.campaignName,
        image: campaign.campaignImg,
        category: campaign.campaignCategory,
        reviews: 0,
        rating: 4.5,
        timeLeft: calculateTimeLeft(campaign.campaignDeadline),
        status: campaign.campaignStatus,
        advertiser: campaign.companyName,
        description: campaign.campaignDesc,
        condition: campaign.campaignCondition
      }
    })
  } catch (err) {
    console.error('캠페인 상품 로딩 실패:', err)
    error.value = '캠페인 상품을 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
}

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

const handleImageError = (e) => {
  e.target.src = '/placeholder.png'
  e.target.classList.add('error')
}

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

onMounted(async () => {
  await fetchCampaigns()
  calculateTimeUntilMidnight()
  
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
})
</script>

<template>
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
</template>

<style >
@import '@/assets/css/home.css';
</style> 