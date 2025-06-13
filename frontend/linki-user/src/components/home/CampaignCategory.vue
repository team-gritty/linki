<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { homeAPI } from '@/api/home'

const router = useRouter()
const categories = ref([])
const loading = ref(false)
const error = ref(null)

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
    <div class="category-slider">
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