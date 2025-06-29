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

// 카테고리 매핑 객체
const CATEGORY_MAPPING = {
  // 대문자 버전
  'ENTERTAINMENT': '엔터테인먼트',
  'FASHION': '패션',
  'BEAUTY': '뷰티',
  'SPORTS': '스포츠',
  'FOOD': '푸드/맛집',
  'TRAVEL': '여행',
  'GAMING': '게임',
  'TECH': '기술',
  'MUSIC': '음악',
  'FITNESS': '피트니스',
  'LIFESTYLE': '라이프스타일',
  'EDUCATION': '교육',
  'NEWS': '뉴스',
  'COMEDY': '코미디',
  'ANIMATION': '애니메이션',
  'PETS': '반려동물',
  'AUTO': '자동차',
  'SCIENCE': '과학',
  'HOWTO': '하우투',
  'NONPROFIT': '비영리',
  
  // 소문자 버전
  'entertainment': '엔터테인먼트',
  'fashion': '패션',
  'beauty': '뷰티',
  'sports': '스포츠',
  'food': '푸드/맛집',
  'travel': '여행',
  'gaming': '게임',
  'tech': '기술',
  'music': '음악',
  'fitness': '피트니스',
  'lifestyle': '라이프스타일',
  'education': '교육',
  'news': '뉴스',
  'comedy': '코미디',
  'animation': '애니메이션',
  'pets': '반려동물',
  'auto': '자동차',
  'science': '과학',
  'howto': '하우투',
  'nonprofit': '비영리'
}

// 카테고리 변환 함수
const translateCategory = (englishCategory) => {
  if (!englishCategory) return '일반'
  
  // 이미 한국어인지 확인 (한글 포함 여부로 판단)
  if (/[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/.test(englishCategory)) {
    return englishCategory
  }
  
  // 매핑된 한국어 카테고리 반환, 없으면 '일반' 반환
  return CATEGORY_MAPPING[englishCategory.trim()] || '일반'
}

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
  }, 1500)
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
    
    // 새로운 API 응답 구조에 맞게 데이터 매핑
    const cards = data.influencerCards || []
    influencers.value = cards.map(card => ({
      id: card.influencerId,
      channelId: card.channelId,
      name: card.influencerName,
      profileImage: card.thumbnailUrl || '/placeholder.png', // 썸네일 URL 사용
      category: card.category,
      subscribers: card.subscriberDisplay, // 이미 포맷된 문자열
      reviews: card.reviewCount || 0,
      rating: parseFloat(card.rating) || 0.0,
      channelUrl: card.channelUrl
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

/**
 * 추천 인플루언서 클릭할 시 상세 페지 이동함
 * @param influencer
 */
const handleInfluencerClick = (influencer) => {
  // 채널 ID가 있으면 채널 ID 사용, 없으면 인플루언서 ID 사용
  const targetId = influencer.channelId || influencer.id
  router.push(`/channels/${targetId}`)
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
      <div class="influencer-grid" @mouseenter="stopAutoSlide" @mouseleave="startAutoSlide">
        <div
          v-for="influencer in displayedInfluencers"
          :key="influencer.id"
          class="influencer-card"
          :style="{ backgroundImage: `url(${influencer.profileImage})` }"
          @click="handleInfluencerClick(influencer)"
          style="cursor: pointer;"
        >
          <span class="category">{{ translateCategory(influencer.category) }}</span>
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
              <span class="subscribers">구독자 {{ influencer.subscribers }}</span>
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