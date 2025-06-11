<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { homeAPI } from '@/api/home'

const router = useRouter()
const categories = ref([])
const loading = ref(false)
const error = ref(null)

// 카테고리 슬라이더 관련 상태
const categorySlideIndex = ref(0)
const categoriesPerSlide = 6

const displayedCategories = computed(() => {
  const start = categorySlideIndex.value * categoriesPerSlide
  return categories.value.slice(start, start + categoriesPerSlide)
})

const prevCategorySlide = () => {
  if (categorySlideIndex.value > 0) {
    categorySlideIndex.value--
  }
}

const nextCategorySlide = () => {
  if (categorySlideIndex.value < Math.ceil(categories.value.length / categoriesPerSlide) - 1) {
    categorySlideIndex.value++
  }
}

const fetchCategories = async () => {
  try {
    loading.value = true
    // API 호출로 카테고리 데이터 가져오기
    const response = await homeAPI.getCategories()
    categories.value = response.map(category => ({
      id: category.name.toUpperCase().replace('/', '_'),
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
    '뷰티': '💄',
    '스포츠': '⚽',
    '푸드/먹방': '🍽️',
    '엔터테인먼트': '🎬',
    '여행': '✈️',
    '음악': '🎵',
    '전자기기': '📱',
    'Vlog/라이프스타일': '🎥',
    '교육': '📚',
    '동물/펫': '🐾',
    '패션': '👗'
  }
  return iconMap[categoryName] || '📌'
}

// 카테고리 클릭 핸들러
const handleCategoryClick = (category) => {
  router.push({
    path: '/campaigns',
    query: { 
      category: category.name
    }
  })
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
      <div class="title-wrapper">
        <span class="small-title highlight">
          <span class="vertical-bar"></span>종류별로 보는
        </span>
        <h3>캠페인 카테고리 선택</h3>
      </div>
      <div class="navigation-arrows">
        <button class="nav-arrow" @click="prevCategorySlide" :disabled="categorySlideIndex === 0">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M15 18L9 12L15 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        <button class="nav-arrow" @click="nextCategorySlide" 
                :disabled="categorySlideIndex >= Math.ceil(categories.length / categoriesPerSlide) - 1">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>
    </div>
    <div class="category-slider">
      <div class="category-grid">
        <div v-for="category in displayedCategories" :key="category.id" 
             :class="['category-item', { active: category.active }]"
             @click="handleCategoryClick(category)"
             style="cursor: pointer;">
          <div class="category-icon">
            {{ category.icon }}
          </div>
          <span class="category-name">{{ category.name }}</span>
        </div>
      </div>
    </div>
    <div class="center-button-wrapper">
      <button class="more-button" @click="handleViewAll">전체보기</button>
    </div>
  </section>
</template>

<style>
@import '@/assets/css/home.css';
</style> 