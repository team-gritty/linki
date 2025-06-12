<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { homeAPI } from '@/api/home'

const router = useRouter()
const influencers = ref([])
const loading = ref(false)
const error = ref(null)

// 상위 4개 인플루언서만 표시
const displayedInfluencers = computed(() => {
  return influencers.value.slice(0, 4)
})

const fetchInfluencers = async () => {
  try {
    loading.value = true
    const data = await homeAPI.getmonthInfluencers()
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
      rating: influencer.rating,
      averageViews: influencer.avgViewCount
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
})
</script>

<template>
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
            <span class="subscribers">구독자 {{ influencer.subscribers }}명</span>
            <span class="category">{{ influencer.category }}</span>
          </div>
        </div>
      </router-link>
    </div>
  </section>
</template>

<style >
@import '@/assets/css/home.css';

</style> 