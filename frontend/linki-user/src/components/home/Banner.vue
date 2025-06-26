<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAccountStore } from '@/stores/account'

const router = useRouter()
const accountStore = useAccountStore()

// 하드코딩된 배너 데이터
const banners = ref([
  {
    id: 1,
    title: "구독하고 한번에 시작하기",
    description: "링키와 함께 캠페인을 시작해보세요",
    image: "/src/assets/videos/banner1.mp4",
    active: true
  },
  {
    id: 2,
    title: "인플루언서 모집",
    description: "다양한 분야의 인플루언서를 모집합니다",
    image: "/src/assets/videos/banner2.mp4",
    active: true
  }
])

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

const handleImageError = (e) => {
  e.target.src = '/placeholder.png'
  e.target.classList.add('error')
}

function isVideo(url) {
  if (!url) return false
  const videoExtensions = ['.mp4', '.webm', '.ogg']
  return videoExtensions.some(ext => url.toLowerCase().includes(ext))
}

// Start Linki 버튼 클릭 핸들러
const handleStartLinki = () => {
  // 비회원인 경우 로그인 페이지로 이동
  if (!accountStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  // 로그인 상태인 경우 사용자 유형에 따라 이동
  const userType = accountStore.getUserType
  
  if (userType === 'influencer') {
    // 인플루언서인 경우 인플루언서 마이페이지의 구독신청 탭으로 이동 (기존 방식 사용)
    router.push('/mypage/influencer?currentMenu=subscription.apply')
  } else if (userType === 'advertiser') {
    // 광고주인 경우 광고주 마이페이지의 구독신청 탭으로 이동 (DetailHeader.vue와 동일한 방식)
    router.push('/mypage/advertiser/subscription')
  } else {
    // 일반 회원인 경우 인플루언서/광고주 등록신청 탭으로 이동
    router.push('/mypage/register')
  }
}

onMounted(() => {
  if (banners.value && banners.value.length > 0) {
    startAutoSlide()
  }
})

onUnmounted(() => {
  if (autoSlideInterval.value) {
    clearInterval(autoSlideInterval.value)
  }
})
</script>

<template>
  <section class="banner-section">
    <div class="banner-slider">
      <div v-for="(banner, index) in banners" :key="banner.id" 
           :class="['banner-item', { active: index === currentSlide }]">
        <template v-if="isVideo(banner.image)">
          <video
            class="banner-image"
            :src="banner.image"
            autoplay
            loop
            muted
            playsinline
          ></video>
        </template>
        <template v-else>
          <img :src="banner.image" :alt="banner.title" class="banner-image" @error="handleImageError" />
        </template>
        <div class="banner-content" :class="`banner-content--${banner.id}`">
          <template v-if="banner.id === 1">
            <div class="banner-row">
              <div class="banner-content-wrapper">
                <div class="banner-texts banner-texts--left">
                  <h2 style="margin-left:0;">{{ banner.title }}</h2>
                  <p>{{ banner.description }}</p>
                </div>
                <button class="start-button" @click="handleStartLinki">Start Linki →</button>
              </div>
            </div>
          </template>
          <template v-else-if="banner.id === 2">
            <div class="banner-text-bg">
              <h2>{{ banner.title }}</h2>
              <p>{{ banner.description }}</p>
              <button class="start-button" @click="handleStartLinki">Start Linki →</button>
            </div>
          </template>
          <template v-else>
            <div class="banner-texts">
              <h2>{{ banner.title }}</h2>
              <p>{{ banner.description }}</p>
              <button class="start-button" @click="handleStartLinki">Start Linki →</button>
            </div>
          </template>
        </div>
      </div>
      <div class="slider-dots">
        <span v-for="(_, index) in banners" :key="index" 
              :class="['dot', { active: index === currentSlide }]"
              @click="currentSlide = index"></span>
      </div>
    </div>
  </section>
</template>

<style >
@import '@/assets/css/home.css';
</style> 