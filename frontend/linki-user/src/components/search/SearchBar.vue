<template>
  <!-- 인플루언서 채널 목록 페이지에서 사용하는 검색바 컴포넌트 시작-->
  <div class="search-bar">
    <div class="category-dropdown" @click="toggleDropdown">
      <span class="category-label">{{ selectedCategoryLabel }}</span>
      <svg class="dropdown-icon" width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M4 6L8 10L12 6" stroke="#6B7280" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <div v-if="dropdownOpen" class="dropdown-menu custom-category-dropdown" @click.stop>
        <button class="select-all-btn" :class="{ active: allSelected }" @click.stop="toggleSelectAll">전체 선택</button>
        <div class="category-list">
          <label v-for="cat in categories" :key="cat" class="category-item">
            <input type="checkbox" v-model="selectedCategories" :value="cat" />
            <span>{{ cat }}</span>
          </label>
        </div>
      </div>
    </div>
    <div class="divider"></div>
    <input class="search-input" placeholder="원하는 인플루언서를 검색해보세요!" />
    <button class="search-btn">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="11" cy="11" r="8" stroke="#8C30F5" stroke-width="2"/>
        <path d="M20 20L16.65 16.65" stroke="#8C30F5" stroke-width="2" stroke-linecap="round"/>
      </svg>
    </button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, defineEmits } from 'vue'

const emit = defineEmits(['update:categories'])
const categories = [
  '패션', '뷰티', '푸드 / 먹방', '엔터테인먼트', '여행', '스포츠', '음악', '전자기기', 'Vlog/라이프스타일', '교육', '동물/펫'
]
const selectedCategories = ref([])
const dropdownOpen = ref(false)
const allSelected = computed(() => selectedCategories.value.length === categories.length)
const selectedCategoryLabel = computed(() => {
  if (selectedCategories.value.length === 0) return '카테고리'
  if (selectedCategories.value.length === categories.length) return '전체 선택됨'
  if (selectedCategories.value.length === 1) return selectedCategories.value[0]
  return `${selectedCategories.value[0]} 외 ${selectedCategories.value.length - 1}개`
})
const toggleDropdown = () => { dropdownOpen.value = !dropdownOpen.value }
const toggleSelectAll = () => {
  if (allSelected.value) selectedCategories.value = []
  else selectedCategories.value = [...categories]
}
// 카테고리 드롭다운 닫기 및 선택된 카테고리 전달
function closeDropdownAndEmit() {
  dropdownOpen.value = false
  emit('update:categories', [...selectedCategories.value])
}
function onDocumentClick(e) {
  if (dropdownOpen.value) {
    const dropdown = document.querySelector('.custom-category-dropdown')
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
  gap: 6px;
  font-weight: 700;
  color: #23262F;
  font-size: 17px;
  cursor: pointer;
  position: relative;
  user-select: none;
  min-width: 180px;
  max-width: 180px;
  width: 180px;
}
.category-label {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
  display: inline-block;
}
.custom-category-dropdown {
  position: absolute;
  top: 110%;
  left: 0;
  background: #fff;
  border: 1.5px solid #8C30F5;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(140,48,245,0.08);
  min-width: 260px;
  width: 260px;
  z-index: 20;
  padding: 18px 0 12px 0;
}
.select-all-btn {
  width: auto;
  min-width: 80px;
  background: #F5F0FF;
  color: #8C30F5;
  border: none;
  font-weight: 700;
  font-size: 0.98em;
  padding: 4px 16px;
  border-radius: 16px;
  cursor: pointer;
  margin-bottom: 8px;
  transition: background 0.2s, color 0.2s;
  display: inline-block;
  margin-left: 28px;
}
.select-all-btn.active, .select-all-btn:hover {
  background: #8C30F5;
  color: #fff;
}
.category-list {
  max-height: 220px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 0 18px;
}
.category-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.08em;
  padding: 8px 0;
  cursor: pointer;
  border-radius: 6px;
  transition: background 0.15s;
}
.category-item:hover {
  background: #F5F0FF;
}
.category-item input[type="checkbox"] {
  accent-color: #8C30F5;
  width: 18px;
  height: 18px;
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