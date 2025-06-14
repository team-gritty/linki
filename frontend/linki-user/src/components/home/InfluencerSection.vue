<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { homeAPI } from '@/api/home'

const router = useRouter()
const influencers = ref([])
const loading = ref(false)
const error = ref(null)

// 슬라이더 관련 상태
const currentIndex = ref(0)
const visibleCount = 4 // 한 번에 보여줄 카드 수
const autoSlideInterval = ref(null)

const displayedInfluencers = computed(() => {
  return influencers.value.slice(currentIndex.value, currentIndex.value + visibleCount)
})

const canSlideLeft = computed(() => currentIndex.value > 0)
const canSlideRight = computed(() => currentIndex.value + visibleCount < influencers.value.length)

const slideLeft = () => {
  if (canSlideLeft.value) currentIndex.value--
}
const slideRight = () => {
  if (canSlideRight.value) currentIndex.value++
}

// 자동 슬라이드 시작
const startAutoSlide = () => {
  autoSlideInterval.value = setInterval(() => {
    if (currentIndex.value + visibleCount < influencers.value.length) {
      currentIndex.value++
    } else {
      currentIndex.value = 0
    }
  }, 2000)
}

// 자동 슬라이드 정지
const stopAutoSlide = () => {
  if (autoSlideInterval.value) {
    clearInterval(autoSlideInterval.value)
  }
}

const fetchInfluencers = async () => {
  try {
    loading.value = true
    const data = await homeAPI.getmonthInfluencers()
    console.log('Influencers response:', data)
    influencers.value = data.map(influencer => ({
      id: influencer.influencersId,
      name: influencer.influencerName,
      profileImage: influencer.influencerProfileImage,
      category: influencer.influencerCategory,
      subscribers: typeof influencer.influencerSubscribers === 'string' ?
        influencer.influencerSubscribers :
        influencer.influencerSubscribers.toLocaleString(),
      reviews: influencer.influencerReviewsCount || 0,
      rating: influencer.influencerRating,
      averageViews: influencer.influencerAvgViewCount
    }))
  } catch (err) {
    console.error('인플루언서 로딩 실패:', err)
    error.value = '인플루언서 정보를 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
}

const handleImageError = (e) => {
  e.target.src = '/placeholder.png'
  e.target.classList.add('error')
}

const handleInfluencerClick = (influencerId) => {
  router.push(`/channels/${influencerId}`)
}

onMounted(async () => {
  await fetchInfluencers()
  startAutoSlide()
})

onUnmounted(() => {
  stopAutoSlide()
})
</script>

<template>
  <section class="influencer-section">
    <div class="influencer-inner" style="position: relative; max-width: 1500px; margin: 0 auto;">
      <div class="arrow left-arrow" 
           @click="slideLeft" 
           @mouseenter="stopAutoSlide"
           @mouseleave="startAutoSlide"
           :class="{ disabled: !canSlideLeft }">
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none"><path d="M15 18L9 12L15 6" stroke="#7B21E8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
      </div>
      <div class="arrow right-arrow" 
           @click="slideRight" 
           @mouseenter="stopAutoSlide"
           @mouseleave="startAutoSlide"
           :class="{ disabled: !canSlideRight }">
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none"><path d="M9 6L15 12L9 18" stroke="#7B21E8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
      </div>
      <div class="section-header">
        <div class="influencer-title" style="text-align: center; width: 100%;">
          이번 달, 💫<span class="highlight">LINKI</span>추천 인플루언서
        </div>
      </div>
      <div class="influencer-grid">
        <div
          v-for="influencer in displayedInfluencers"
          :key="influencer.id"
          class="influencer-card"
          :style="{ backgroundImage: `url(${influencer.profileImage})` }"
          @click="handleInfluencerClick(influencer.id)"
          style="cursor: pointer;"
        >
          <span class="category">{{ influencer.category }}</span>
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
              <span class="subscribers">구독자 {{ influencer.subscribers }}명</span>
            </div>
          </div>
        </div>
      </div>
      <div class="center-button-wrapper">
        <button class="more-button" @click="$router.push({ name: 'influencer-list' })">인플루언서 전체보기</button>
      </div>
    </div>
  </section>
</template>

<style >
@import '@/assets/css/home.css';

</style> 