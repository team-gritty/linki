<template>
  <!-- 페이지네이션 네비게이션 영역 -->
  <nav class="pagination">
    <!-- 이전 페이지 버튼 -->
    <button :disabled="currentPage === 1" @click="$emit('update:currentPage', currentPage - 1)">←</button>
    <!-- 각 페이지 번호 버튼 -->
    <span v-for="page in pages" :key="page">
      <button
        :class="{ active: page === currentPage }"
        @click="$emit('update:currentPage', page)"
      >
        {{ page }}
      </button>
    </span>
    <!-- 다음 페이지 버튼 -->
    <button :disabled="currentPage === totalPages" @click="$emit('update:currentPage', currentPage + 1)">→</button>
  </nav>
</template>

<script setup>
import { computed, defineProps } from 'vue'
// 부모로부터 전체 페이지 수, 현재 페이지 번호를 props로 전달받음
const props = defineProps({
  totalPages: { type: Number, required: true },
  currentPage: { type: Number, required: true }
})
// 페이지 번호 배열 계산 (1~totalPages)
const pages = computed(() => {
  // 간단하게 1~totalPages까지 모두 표시
  return Array.from({ length: props.totalPages }, (_, i) => i + 1)
})
</script>

<style scoped>
.pagination {
  display: flex;
  gap: 8px;
  justify-content: center;
  margin-top: 24px;
  margin-bottom: 16px;
}
button {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  background: #f5f5f5;
  color: #222;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}
button.active {
  background: #a259ff;
  color: #fff;
}
button:disabled {
  opacity: 0.4;
  cursor: default;
}
</style> 