<template>
  <div class="channel-list-page">
    <div class="channel-list-header">
      <button class="search-option-btn" @click="modalOpen = true">
        <svg class="search-option-icon" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M3 6H17V8H3V6ZM3 10H17V12H3V10ZM3 14H13V16H3V14Z" :fill="searchBtnHover ? '#8C30F5' : '#fff'"/>
        </svg>
        <span>검색 옵션</span>
      </button>
      <SearchBar />
    </div>
    <SearchOptionModal v-if="modalOpen" @close="modalOpen = false" />

    <div class="channel-list-table">
      <div class="table-header">
        <div class="th th-num">#</div>
        <div class="th th-detail">채널 상세</div>
        <div class="th th-subscribers">구독자 수</div>
        <div class="th th-views">평균 조회수</div>
        <div class="th th-analysis">분석</div>
      </div>
      <div v-for="(item, idx) in listData" :key="idx" class="table-row">
        <div class="td td-num">{{ idx + 1 }}</div>
        <div class="td td-detail">
          <img :src="item.img" class="profile-img" />
          <div class="channel-info">
            <div class="channel-name">{{ item.name }}</div>
            <div class="channel-meta">
              <span class="category">{{ item.category }}</span>
              <span class="genre">{{ item.genre }}</span>
            </div>
            <div class="review-row">
              <span class="stars">★ ★ ★ ★ ★</span>
              <span class="review-count">(150 Reviews)</span>
            </div>
          </div>
        </div>
        <div class="td td-subscribers">{{ item.subscribers }}</div>
        <div class="td td-views">{{ item.views }}</div>
        <div class="td td-analysis">
          <button class="analysis-btn" @click="goToDetail">상세 분석</button>
        </div>
      </div>
    </div>
    <div class="pagination">
      <button class="page-btn" :class="{ active: page === 1 }">&#60;</button>
      <button v-for="p in 6" :key="p" class="page-btn" :class="{ active: page === p }">{{ p }}</button>
      <button class="page-btn">&#62;</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import SearchBar from '@/components/search/SearchBar.vue'
import SearchOptionModal from '@/components/search/SearchOptionModal.vue'

const router = useRouter()
const modalOpen = ref(false)
const page = ref(2)
const listData = [
  { img: 'https://randomuser.me/api/portraits/men/1.jpg', name: 'Channel Name1', category: '엔터테인먼트', genre: '음악', subscribers: '5천만', views: '12319' },
  { img: 'https://randomuser.me/api/portraits/men/1.jpg', name: 'Channel Name1', category: '엔터테인먼트', genre: '음악', subscribers: '1만', views: '12319' },
  { img: 'https://randomuser.me/api/portraits/men/1.jpg', name: 'Channel Name1', category: '엔터테인먼트', genre: '음악', subscribers: '1900', views: '12319' },
  { img: 'https://randomuser.me/api/portraits/men/1.jpg', name: 'Channel Name1', category: '엔터테인먼트', genre: '음악', subscribers: '2만', views: '12319' },
  { img: 'https://randomuser.me/api/portraits/men/1.jpg', name: 'Channel Name1', category: '엔터테인먼트', genre: '음악', subscribers: '30만', views: '12319' },
]

const searchBtnHover = ref(false)
function handleSearchBtnMouseEnter() { searchBtnHover.value = true }
function handleSearchBtnMouseLeave() { searchBtnHover.value = false }

// 검색바 엔터 입력 시 검색 동작
function handleSearchInputKeydown(e) {
  if (e.key === 'Enter') {
    // 실제 검색 동작 구현 필요
    alert('검색 기능은 추후 구현 예정입니다.')
  }
}
onMounted(() => {
  const input = document.querySelector('.search-input')
  if (input) input.addEventListener('keydown', handleSearchInputKeydown)
})

const goToDetail = () => {
  router.push('/channel-detail')
}
</script>

<style scoped>
.channel-list-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
}
.search-option-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  border-radius: 6px;
  background: #8C30F5;
  color: #fff;
  font-weight: 600;
  font-size: 16px;
  border: 1.5px solid #E1CFFF;
  cursor: pointer;
  outline: none;
  transition: border 0.2s, box-shadow 0.2s, background 0.2s;
  box-shadow: 0 2px 8px rgba(140,48,245,0.06);
  min-width: 0;
}
.search-option-btn:hover, .search-option-btn:focus {
  background: #fff;
  color: #8C30F5;
  border: 1.5px solid #8C30F5;
  box-shadow: 0 4px 16px rgba(140,48,245,0.10);
}
.search-option-icon {
  margin-right: 8px;
  transition: fill 0.2s;
}

/***** 테이블 헤더 *****/
.channel-list-table {
  margin-top: 32px;
  border-top: 2px solid #E0E0E0;
}
.table-header {
  display: flex;
  align-items: center;
  border-bottom: 2px solid #E0E0E0;
  background: #fff;
  font-weight: 700;
  font-size: 17px;
  color: #23262F;
  height: 56px;
}
.th {
  padding: 0 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
}
.th-num { flex: 0.7; }
.th-detail { flex: 2.5; justify-content: flex-start; }
.th-subscribers { flex: 1.2; }
.th-views { flex: 1.2; }
.th-analysis { flex: 1; }

/***** 테이블 행 *****/
.table-row {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #E0E0E0;
  background: #fff;
  height: 120px;
  transition: background 0.2s;
}
.td {
  padding: 0 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex: 1;
}
.td-num { flex: 0.7; color: #A0A0A0; font-weight: 500; justify-content: center; }
.td-detail { flex: 2.5; display: flex; align-items: center; justify-content: flex-start; }
.td-subscribers { flex: 1.2; font-weight: 700; font-size: 22px; justify-content: center; }
.td-views { flex: 1.2; font-weight: 700; font-size: 20px; justify-content: center; }
.td-analysis { flex: 1; justify-content: center; }
.profile-img {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 24px;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.channel-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.channel-name {
  font-size: 22px;
  font-weight: 700;
  color: #2D3A8C;
}
.channel-meta {
  font-size: 18px;
  font-weight: 700;
  color: #23262F;
  display: flex;
  gap: 8px;
}
.review-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 2px;
}
.stars {
  color: #FFC107;
  font-size: 16px;
  letter-spacing: 1px;
}
.review-count {
  color: #A0A0A0;
  font-size: 14px;
}
.analysis-btn {
  background: #B18CFF;
  color: #fff;
  font-weight: 700;
  border: none;
  border-radius: 6px;
  padding: 12px 24px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s;
  min-width: 100px;
  white-space: nowrap;
}
.analysis-btn:hover {
  background: #8C30F5;
}

/***** 페이징 *****/
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin: 32px 0 0 0;
}
.page-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: #F2F2F2;
  color: #8C30F5;
  font-weight: 700;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}
.page-btn.active {
  background: #8C30F5;
  color: #fff;
}
</style> 