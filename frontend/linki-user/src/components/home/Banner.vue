<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { homeAPI } from '@/api/home'

const banners = ref([])
const currentSlide = ref(0)
const autoSlideInterval = ref(null)
const loading = ref(false)
const error = ref(null)

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

const fetchBanners = async () => {
  try {
    loading.value = true
    const data = await homeAPI.getBanners()
    console.log('Banners response:', data)
    banners.value = data.filter(banner => banner.active)
  } catch (err) {
    console.error('배너 로딩 실패:', err)
    error.value = '배너를 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
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

onMounted(async () => {
  await fetchBanners()
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
      <div v-if="loading" class="loading">로딩 중...</div>
      <div v-else-if="error" class="error">{{ error }}</div>
      <template v-else>
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
            <div v-if="banner.id === 2" class="banner-text-bg">
              <h2>{{ banner.title }}</h2>
              <p>{{ banner.description }}</p>
              <button class="start-button">Start Linki →</button>
            </div>
            <div v-else class="banner-texts">
              <h2>{{ banner.title }}</h2>
              <p>{{ banner.description }}</p>
              <button class="start-button">Start Linki →</button>
            </div>
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
</template>

<style >
@import '@/assets/css/home.css';
</style> 