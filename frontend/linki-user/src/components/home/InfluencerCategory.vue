<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { homeAPI } from '@/api/home'

const router = useRouter()
const categories = ref([])
const loading = ref(false)
const error = ref(null)

const fetchCategories = async () => {
  try {
    loading.value = true
    const data = await homeAPI.getCategories()
    console.log(' categories response:', data)
    categories.value = data
  } catch (err) {
    console.error('사이드바 카테고리 로딩 실패:', err)
    error.value = '사이드바 카테고리를 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
}

// 카테고리 클릭 핸들러 수정
const handleCategoryClick = (category) => {
  router.push({
    path: '/channels',  // 실제 채널 목록 페이지 경로로 수정
    query: { 
      selectedCategories: JSON.stringify([category.name])
    }
  })
}

onMounted(async () => {
  await fetchCategories()
})
</script>

<template>
  <div class="sidebar">
    <h3 class="sidebar-title">인플루언서</h3>
    <button class="view-all-button" @click="$router.push({ name: 'influencer-list' })">
      전체보기
    </button>
    <ul class="category-menu">
      <li v-for="category in categories" :key="category.id" class="menu-item">
        <a href="#" 
           :class="{ active: category.active }"
           @click.prevent="handleCategoryClick(category)"
           style="cursor: pointer;">
          {{ category.name }}
          <span class="arrow">›</span>
        </a>
      </li>
    </ul>
  </div>
</template>

<style>
@import '@/assets/css/home.css';
</style> 