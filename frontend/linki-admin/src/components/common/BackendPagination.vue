<template>
  <div class="pagination-container">
    <!-- 페이지 정보 표시 -->
    <div class="pagination-info">
      총 {{ totalElements }}개 중 {{ startItem }}-{{ endItem }}개 표시 ({{ currentPage + 1 }}/{{ totalPages }} 페이지)
    </div>
    
    <!-- 페이지네이션 버튼들 -->
    <div class="pagination-controls">
      <!-- 첫 페이지 버튼 -->
      <button 
        class="pagination-btn first"
        :disabled="currentPage === 0"
        @click="$emit('update:currentPage', 0)"
      >
        첫 페이지
      </button>
      
      <!-- 이전 페이지 버튼 -->
      <button 
        class="pagination-btn prev"
        :disabled="currentPage === 0"
        @click="$emit('update:currentPage', currentPage - 1)"
      >
        이전
      </button>
      
      <!-- 페이지 번호 버튼들 -->
      <button
        v-for="page in visiblePages"
        :key="page"
        class="pagination-btn page-number"
        :class="{ active: page === currentPage }"
        @click="$emit('update:currentPage', page)"
      >
        {{ page + 1 }}
      </button>
      
      <!-- 다음 페이지 버튼 -->
      <button 
        class="pagination-btn next"
        :disabled="currentPage >= totalPages - 1"
        @click="$emit('update:currentPage', currentPage + 1)"
      >
        다음
      </button>
      
      <!-- 마지막 페이지 버튼 -->
      <button 
        class="pagination-btn last"
        :disabled="currentPage >= totalPages - 1"
        @click="$emit('update:currentPage', totalPages - 1)"
      >
        마지막 페이지
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  currentPage: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  },
  totalElements: {
    type: Number,
    required: true
  },
  pageSize: {
    type: Number,
    default: 10
  }
})

const emit = defineEmits(['update:currentPage'])

// 현재 페이지에서 보여주는 항목의 시작 번호
const startItem = computed(() => {
  return props.currentPage * props.pageSize + 1
})

// 현재 페이지에서 보여주는 항목의 끝 번호
const endItem = computed(() => {
  const end = (props.currentPage + 1) * props.pageSize
  return Math.min(end, props.totalElements)
})

// 보여줄 페이지 번호들 계산
const visiblePages = computed(() => {
  const maxVisible = 5 // 최대 5개 페이지 번호 표시
  const pages = []
  
  if (props.totalPages <= maxVisible) {
    // 총 페이지가 5개 이하면 모두 표시
    for (let i = 0; i < props.totalPages; i++) {
      pages.push(i)
    }
  } else {
    // 현재 페이지를 중심으로 앞뒤 2개씩 표시
    let start = Math.max(0, props.currentPage - 2)
    let end = Math.min(props.totalPages - 1, props.currentPage + 2)
    
    // 시작이나 끝에 치우쳐 있으면 조정
    if (end - start < maxVisible - 1) {
      if (start === 0) {
        end = Math.min(props.totalPages - 1, start + maxVisible - 1)
      } else {
        start = Math.max(0, end - maxVisible + 1)
      }
    }
    
    for (let i = start; i <= end; i++) {
      pages.push(i)
    }
  }
  
  return pages
})
</script>

<style scoped>
.pagination-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  margin: 24px 0;
  font-family: 'Pretendard', 'Noto Sans KR', Arial, sans-serif;
}

.pagination-info {
  color: #666;
  font-size: 0.9rem;
  font-weight: 500;
}

.pagination-controls {
  display: flex;
  gap: 8px;
  align-items: center;
}

.pagination-btn {
  padding: 8px 12px;
  border: 1px solid #ddd;
  background: #fff;
  color: #333;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s;
  min-width: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pagination-btn:hover:not(:disabled) {
  background: #f8f9fa;
  border-color: #7C3AED;
  color: #7C3AED;
}

.pagination-btn:disabled {
  background: #f5f5f5;
  color: #999;
  cursor: not-allowed;
  border-color: #e0e0e0;
}

.pagination-btn.active {
  background: #7C3AED;
  color: #fff;
  border-color: #7C3AED;
  font-weight: 700;
}

.pagination-btn.first,
.pagination-btn.last {
  padding: 8px 16px;
  font-size: 0.85rem;
}

.pagination-btn.page-number {
  min-width: 36px;
}

@media screen and (max-width: 768px) {
  .pagination-container {
    gap: 12px;
  }
  
  .pagination-controls {
    gap: 4px;
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .pagination-btn {
    padding: 6px 8px;
    font-size: 0.85rem;
    min-width: 32px;
  }
  
  .pagination-btn.first,
  .pagination-btn.last {
    padding: 6px 12px;
    font-size: 0.8rem;
  }
  
  .pagination-info {
    font-size: 0.8rem;
    text-align: center;
  }
}
</style> 