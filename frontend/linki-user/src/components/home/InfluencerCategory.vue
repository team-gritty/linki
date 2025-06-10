<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { homeAPI } from '@/api/home'

const router = useRouter()
const sidebarCategories = ref([])
const loading = ref(false)
const error = ref(null)

const fetchSidebarCategories = async () => {
  try {
    loading.value = true
    const data = await homeAPI.getSidebarCategories()
    console.log('Sidebar categories response:', data)
    sidebarCategories.value = data
  } catch (err) {
    console.error('사이드바 카테고리 로딩 실패:', err)
    error.value = '사이드바 카테고리를 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await fetchSidebarCategories()
})
</script>

<template>
  <div class="sidebar">
    <h3 class="sidebar-title">인플루언서</h3>
    <ul class="category-menu">
      <li class="menu-item">
        <button class="view-all-button" @click="$router.push({ name: 'influencer-list' })">
          전체보기
        </button>
      </li>
      <li v-for="category in sidebarCategories" :key="category.id" class="menu-item">
        <a href="#" :class="{ active: category.active }">
          {{ category.name }}
          <span class="arrow">›</span>
        </a>
      </li>
    </ul>
  </div>
</template>

<style >
@import '@/assets/css/home.css';

</style> 