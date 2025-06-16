<template>
  <!-- 페이지네이션 네비게이션 영역 -->
  <nav class="pagination">
    <!-- 이전 페이지 버튼 -->
    <button 
      class="arrow-btn"
      :disabled="currentPage === 1" 
      @click="$emit('update:currentPage', currentPage - 1)"
    >
      <span class="arrow">←</span>
    </button>

    <!-- 첫 페이지 -->
    <button
      v-if="showFirstPage"
      :class="{ active: 1 === currentPage }"
      @click="$emit('update:currentPage', 1)"
    >
      1
    </button>

    <!-- 첫 페이지 생략 표시 -->
    <span v-if="showFirstEllipsis" class="ellipsis">...</span>

    <!-- 페이지 번호 버튼 -->
    <button
      v-for="page in displayedPages"
      :key="page"
      :class="{ active: page === currentPage }"
      @click="$emit('update:currentPage', page)"
    >
      {{ page }}
    </button>

    <!-- 마지막 페이지 생략 표시 -->
    <span v-if="showLastEllipsis" class="ellipsis">...</span>

    <!-- 마지막 페이지 -->
    <button
      v-if="showLastPage"
      :class="{ active: totalPages === currentPage }"
      @click="$emit('update:currentPage', totalPages)"
    >
      {{ totalPages }}
    </button>

    <!-- 다음 페이지 버튼 -->
    <button 
      class="arrow-btn"
      :disabled="currentPage === totalPages" 
      @click="$emit('update:currentPage', currentPage + 1)"
    >
      <span class="arrow">→</span>
    </button>
  </nav>
</template>

<script setup>
import { computed, defineProps } from 'vue'
// 부모로부터 전체 페이지 수, 현재 페이지 번호를 props로 전달받음
const props = defineProps({
  totalPages: { type: Number, required: true },
  currentPage: { type: Number, required: true }
})

// 표시할 페이지 번호 계산
const displayedPages = computed(() => {
  const pages = []
  const maxPages = 10
  const halfMax = Math.floor(maxPages / 2)

  let start = Math.max(1, props.currentPage - halfMax)
  let end = Math.min(props.totalPages, start + maxPages - 1)

  if (end - start + 1 < maxPages) {
    start = Math.max(1, end - maxPages + 1)
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }

  return pages
})

// 첫 페이지 표시 여부
const showFirstPage = computed(() => {
  return displayedPages.value[0] > 1
})

// 마지막 페이지 표시 여부
const showLastPage = computed(() => {
  return displayedPages.value[displayedPages.value.length - 1] < props.totalPages
})

// 첫 페이지 생략 표시 여부
const showFirstEllipsis = computed(() => {
  return displayedPages.value[0] > 2
})

// 마지막 페이지 생략 표시 여부
const showLastEllipsis = computed(() => {
  return displayedPages.value[displayedPages.value.length - 1] < props.totalPages - 1
})
</script>

<style scoped>
.pagination {
  display: flex;
  gap: 8px;
  justify-content: center;
  align-items: center;
  margin-top: 24px;
  margin-bottom: 16px;
}

button {
  min-width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  background: #f5f5f5;
  color: #222;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8px;
}

button.active {
  background: #a259ff;
  color: #fff;
}

button:disabled {
  opacity: 0.4;
  cursor: default;
}

.arrow-btn {
  background: #f5f5f5;
}

.arrow {
  font-size: 1.2rem;
  line-height: 1;
}

.ellipsis {
  color: #666;
  padding: 0 4px;
}
</style> 