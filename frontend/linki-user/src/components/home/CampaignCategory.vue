<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { campaignAPI } from '@/api/campaign'

const router = useRouter()
const categories = ref([])
const loading = ref(false)
const error = ref(null)

// 카테고리 데이터 불러오기
const fetchCategories = async () => {
  try {
    loading.value = true
    const response = await campaignAPI.getCategories()
    categories.value = response.map(category => ({
      id: category.id || category.name.toUpperCase().replace('/', '_'),
      name: category.name,
      icon: getCategoryIcon(category.name),
      active: false
    }))
  } catch (err) {
    console.error('카테고리 로딩 실패:', err)
    error.value = '카테고리를 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
}

// 카테고리별 아이콘 매핑 함수
const getCategoryIcon = (categoryName) => {
  const iconMap = {
    '패션': '👗',
    '뷰티': '💄',
    '음식': '🍔',
    '엔터테인먼트': '🎬',
    '여행': '✈️',
    '음악': '🎵',
    '전자제품': '💻',
    '브이로그': '📹',
    '교육': '📚',
    '동물': '🐶',
    '스포츠': '⚽'
  }
  return iconMap[categoryName] || '📌'
}

// 전체보기 버튼 핸들러
const handleViewAll = () => {
  router.push({
    path: '/campaigns'
  })
}

// 카테고리 클릭 핸들러 추가
const handleCategoryClick = (category) => {
  router.push({
    name: 'campaigns',
    query: { category: category.name }
  })
}

onMounted(async () => {
  await fetchCategories()
})
</script>

<template>
  <section class="category-section">
    <div class="section-header">
    </div>
    <div v-if="loading" class="loading">로딩 중...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else class="category-slider">
      <transition-group name="category-fade" tag="div" class="category-grid">
        <div
          v-for="(category, idx) in categories"
          :key="category.id"
          style="display: flex; flex-direction: column; align-items: center;"
          :style="{ transitionDelay: (idx * 80) + 'ms' }"
        >
          <div
            :class="['category-item', { active: category.active } ]"
            @click="handleCategoryClick(category)"
            style="cursor: pointer;"
          >
            <div class="category-icon">
              {{ category.icon }}
            </div>
          </div>
          <span class="category-name">{{ category.name }}</span>
        </div>
      </transition-group>
    </div>
    <div class="center-button-wrapper">
      <button class="more-button" @click="handleViewAll">캠페인 전체보기</button>
    </div>
  </section>
</template>

<style>
@import '@/assets/css/home.css';
</style> 