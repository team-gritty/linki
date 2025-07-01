<template>
  <!-- 인플루언서 채널 목록 페이지에서 사용하는 검색바 컴포넌트 시작-->
  <div class="search-bar">
    <div class="category-dropdown" @click="toggleDropdown">
      <span class="category-label">{{ selectedCategoryLabel }}</span>
      <svg class="dropdown-icon" :class="{ 'rotated': dropdownOpen }" width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M4 6L8 10L12 6" stroke="#5f6368" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <div v-if="dropdownOpen" class="dropdown-menu google-style-dropdown" @click.stop>
        <div v-for="cat in categories" 
             :key="cat" 
             class="category-option"
             :class="{ 'selected': selectedCategory === cat }"
             @click="() => { selectedCategory = cat; closeDropdownAndEmit(); }">
          <span>{{ cat }}</span>
        </div>
      </div>
    </div>
    <div class="divider"></div>
    <input 
      v-model="searchKeyword"
      class="search-input" 
      placeholder="원하는 인플루언서를 검색해보세요!" 
      @keyup.enter="handleSearch"
    />
    <button class="search-btn" @click="handleSearch">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="11" cy="11" r="8" stroke="#8C30F5" stroke-width="2"/>
        <path d="M20 20L16.65 16.65" stroke="#8C30F5" stroke-width="2" stroke-linecap="round"/>
      </svg>
    </button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, defineEmits, defineProps, watch } from 'vue'

const emit = defineEmits(['update:categories', 'search'])

const props = defineProps({
  selectedCategoryProp: {
    type: String,
    default: '전체'
  }
})

// 프론트엔드 표시용 한국어 카테고리
const categories = [
  '전체', '패션', '뷰티', '푸드 / 먹방', '엔터테인먼트', '여행', '스포츠', '음악', '전자기기', 'Vlog/라이프스타일', '교육', '동물/펫'
]

// 한국어 -> 영어 카테고리 매핑 (백엔드 API용)
const categoryMapping = {
  '전체': null, // 전체는 카테고리 필터 없음
  '패션': 'FASHION',
  '뷰티': 'BEAUTY', 
  '푸드 / 먹방': 'FOOD',
  '엔터테인먼트': 'ENTERTAINMENT',
  '여행': 'TRAVEL',
  '스포츠': 'SPORTS',
  '음악': 'MUSIC',
  '전자기기': 'ELECTRONICS',
  'Vlog/라이프스타일': 'VLOG',
  '교육': 'EDUCATION',
  '동물/펫': 'ANIMAL'
}

const selectedCategory = ref(props.selectedCategoryProp)
const searchKeyword = ref('')
const dropdownOpen = ref(false)

// props 변경 감지하여 내부 상태 업데이트
watch(() => props.selectedCategoryProp, (newCategory) => {
  selectedCategory.value = newCategory
}, { immediate: true })

const selectedCategoryLabel = computed(() => {
  return selectedCategory.value || '전체'
})

const toggleDropdown = () => { dropdownOpen.value = !dropdownOpen.value }

function closeDropdownAndEmit() {
  dropdownOpen.value = false
  // 한국어 카테고리를 영어로 변환하여 emit
  // "전체"인 경우 null이므로 빈 배열을 emit (전체 채널 조회)
  const englishCategory = selectedCategory.value ? categoryMapping[selectedCategory.value] : null
  emit('update:categories', englishCategory ? [englishCategory] : [])
}

function handleSearch() {
  console.log('검색 요청:', searchKeyword.value)
  emit('search', searchKeyword.value.trim())
}

function onDocumentClick(e) {
  if (dropdownOpen.value) {
    const dropdown = document.querySelector('.google-style-dropdown')
    if (dropdown && !dropdown.contains(e.target)) closeDropdownAndEmit()
  }
}

function onDocumentKeydown(e) {
  if (dropdownOpen.value && (e.key === 'Enter' || e.keyCode === 13)) closeDropdownAndEmit()
}

onMounted(() => {
  document.addEventListener('mousedown', onDocumentClick)
  document.addEventListener('keydown', onDocumentKeydown)
})

onBeforeUnmount(() => {
  document.removeEventListener('mousedown', onDocumentClick)
  document.removeEventListener('keydown', onDocumentKeydown)
})
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 800px;
  border: 2px solid #8C30F5;
  border-radius: 8px;
  padding: 0 20px;
  background: #fff;
  height: 56px;
  box-sizing: border-box;
  gap: 16px;
  position: relative;
}
.category-dropdown {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 500;
  color: #202124;
  font-size: 14px;
  cursor: pointer;
  position: relative;
  user-select: none;
  min-width: 180px;
  max-width: 180px;
  width: 180px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.15s;
}
.category-dropdown:hover {
  background-color: #f8f9fa;
}
.category-label {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  display: inline-block;
}
.dropdown-icon {
  transition: transform 0.2s;
  margin-left: 8px;
  flex-shrink: 0;
}
.dropdown-icon path {
  stroke: #5f6368;
}
.rotated {
  transform: rotate(180deg);
}
.google-style-dropdown {
  position: absolute;
  top: 110%;
  left: 0;
  background: #fff;
  border: none;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 220px;
  width: 220px;
  z-index: 1000;
  padding: 4px 0;
  max-height: 200px;
  overflow-y: auto;
  /* 스크롤바 숨기기 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* Internet Explorer 10+ */
}
.google-style-dropdown::-webkit-scrollbar {
  display: none; /* Safari and Chrome */
}
.category-option {
  padding: 10px 16px;
  cursor: pointer;
  transition: background 0.15s;
  font-size: 14px;
  color: #202124;
  white-space: nowrap;
}
.category-option:hover {
  background: #f8f9fa;
}
.category-option.selected {
  background: #f5f0ff;
  color: #8C30F5;
  font-weight: 500;
}
.divider {
  width: 1px;
  height: 32px;
  background: #E0E0E0;
  margin: 0 12px;
}
.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 17px;
  color: #23262F;
  background: transparent;
  padding: 0 8px;
}
.search-input::placeholder {
  color: #A0A0A0;
  font-weight: 400;
}
.search-btn {
  background: none;
  border: none;
  padding: 0 4px;
  margin-left: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  height: 44px;
  width: 44px;
  justify-content: center;
}
.search-btn svg {
  display: block;
  width: 28px;
  height: 28px;
}
</style> 