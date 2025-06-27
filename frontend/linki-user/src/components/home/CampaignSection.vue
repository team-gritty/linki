<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
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
const itemsPerPage = 5
const slideOffset = ref(0)
const autoSlideInterval = ref(null)

const CARD_WIDTH = 240;
const CARD_GAP = 24;

// 현재 페이지에 표시될 캠페인들
const displayedCampaigns = computed(() => {
  const start = slideOffset.value
  const campaigns = campaignProducts.value.slice(start, start + itemsPerPage)
  return campaigns.map((campaign, index) => ({
    ...campaign,
    displayId: `${campaign.campaignId}-${index}`
  }))
})

// 페이지 이동
const canSlideLeft = computed(() => slideOffset.value > 0)
const canSlideRight = computed(() => slideOffset.value + itemsPerPage < campaignProducts.value.length)
const slideLeft = () => { if (canSlideLeft.value) slideOffset.value-- }
const slideRight = () => { if (canSlideRight.value) slideOffset.value++ }

// 자동 슬라이드 시작
const startAutoSlide = () => {
  autoSlideInterval.value = setInterval(() => {
    if (canSlideRight.value) {
      slideOffset.value++
    } else {
      slideOffset.value = 0
    }
  }, 1500)
}
// 자동 슬라이드 정지
const stopAutoSlide = () => {
  if (autoSlideInterval.value) {
    clearInterval(autoSlideInterval.value)
  }
}

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
    const data = await homeAPI.getEndingTodayCampaigns()
    console.log('Campaign response:', data)
    // 각 캠페인에 timeLeft 추가
    campaignProducts.value = data.map(campaign => ({
      ...campaign,
      timeLeft: calculateTimeLeft(campaign.campaignDeadline)
    }))
    console.log('Set campaign products:', campaignProducts.value)
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
  
  if (diff <= 0) {
    return '마감'
  }
  
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  if (days > 0) {
    return `${days}일 남음`
  }
  
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  if (hours > 0) {
    return `${hours}시간 남음`
  }
  
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  return `${minutes}분 남음`
}

const handleCampaignClick = (campaign) => {
  console.log('Campaign clicked:', campaign)
  console.log('Campaign ID:', campaign.campaignId)
  console.log('Navigating to:', `/campaign/${campaign.campaignId}`)
  router.push(`/campaign/${campaign.campaignId}`)
}

onMounted(async () => {
  await fetchCampaigns()
  calculateTimeUntilMidnight()
  startAutoSlide()
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
  stopAutoSlide()
})
</script>

<template>
  <section class="campaign-section">
    <div class="campaign-inner">
      <div class="arrow left-arrow" 
           @click="slideLeft"
           :class="{ disabled: !canSlideLeft }">
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none"><path d="M15 18L9 12L15 6" stroke="#7B21E8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
      </div>
      <div class="arrow right-arrow" 
           @click="slideRight"
           :class="{ disabled: !canSlideRight }">
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none"><path d="M9 6L15 12L9 18" stroke="#7B21E8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
      </div>
      <div class="section-header">
        
<div class="campaign-title">
        <div class="timer-row">
          <span class="timer-label">마감까지 남은시간</span>
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
      
      <div class="slider-viewport" @mouseenter="stopAutoSlide" @mouseleave="startAutoSlide" style="min-height: 370px; margin-top: 30px;">
        <div class="campaign-grid">
          <div v-for="campaign in displayedCampaigns" 
               :key="campaign.displayId" 
               class="campaign-card"
               :style="{ backgroundImage: `url(${campaign.campaignImg})` }"
               @click="handleCampaignClick(campaign)"
               style="cursor: pointer;">
            <span class="time-remaining">{{ campaign.timeLeft }}</span>
            <div class="campaign-info">
              <h4 class="campaign-name">{{ campaign.campaignName }}</h4>
              <div class="campaign-condition">
                <span class="condition-text">{{ campaign.campaignCondition }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style >
@import '@/assets/css/home.css';
</style> 