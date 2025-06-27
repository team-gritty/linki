<template>
  <!-- Keyset 페이지네이션: 이전/다음 네비게이션 -->
  <nav class="keyset-pagination">
    <!-- 이전 페이지 버튼 -->
    <button 
      class="nav-btn prev-btn"
      :disabled="!hasPrevious || isLoading" 
      @click="$emit('previous')"
    >
      <span class="arrow">←</span>
      <span class="text">이전</span>
    </button>

    <!-- 페이지 정보 표시 -->
    <div class="page-info">
      <span class="current-size">{{ currentSize }}개 표시</span>
      <span class="separator">|</span>
      <span class="loading-text" v-if="isLoading">로딩 중...</span>
      <span class="total-loaded" v-else>총 {{ totalLoaded }}개 로드됨</span>
    </div>

    <!-- 다음 페이지 버튼 -->
    <button 
      class="nav-btn next-btn"
      :disabled="!hasNext || isLoading" 
      @click="$emit('next')"
    >
      <span class="text">다음</span>
      <span class="arrow">→</span>
    </button>
  </nav>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

// Props 정의
const props = defineProps({
  hasPrevious: { type: Boolean, default: false },
  hasNext: { type: Boolean, default: false },
  isLoading: { type: Boolean, default: false },
  currentSize: { type: Number, default: 0 },
  totalLoaded: { type: Number, default: 0 }
})

// Events 정의
defineEmits(['previous', 'next'])
</script>

<style scoped>
.keyset-pagination {
  display: flex;
  gap: 16px;
  justify-content: center;
  align-items: center;
  margin-top: 24px;
  margin-bottom: 16px;
  padding: 16px;
  background: #f9f9f9;
  border-radius: 12px;
  font-family: 'Pretendard', 'Noto Sans KR', Arial, sans-serif;
}

.nav-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  background: #fff;
  color: #333;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 100px;
  justify-content: center;
}

.nav-btn:enabled:hover {
  background: #a259ff;
  color: #fff;
  border-color: #a259ff;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(162, 89, 255, 0.3);
}

.nav-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  background: #f5f5f5;
  color: #999;
  border-color: #ddd;
}

.arrow {
  font-size: 1.2rem;
  line-height: 1;
}

.text {
  font-weight: 600;
}

.page-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  background: #fff;
  border-radius: 6px;
  border: 1px solid #e0e0e0;
  font-size: 0.9rem;
  color: #666;
}

.current-size {
  font-weight: 600;
  color: #a259ff;
}

.separator {
  color: #ddd;
}

.total-loaded {
  color: #888;
}

/* 반응형 디자인 */
@media screen and (max-width: 768px) {
  .keyset-pagination {
    flex-direction: column;
    gap: 12px;
  }
  
  .nav-btn {
    width: 100%;
    max-width: 200px;
  }
  
  .page-info {
    order: -1;
    text-align: center;
  }
}

@media screen and (max-width: 480px) {
  .page-info {
    flex-direction: column;
    gap: 4px;
  }
  
  .separator {
    display: none;
  }
}
</style> 