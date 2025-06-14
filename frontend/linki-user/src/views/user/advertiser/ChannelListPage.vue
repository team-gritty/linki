<template>
  <div class="channel-list-page">
    <div class="channel-list-header">
      <button class="search-option-btn" @click="modalOpen = true">
        <svg class="search-option-icon" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M3 6H17V8H3V6ZM3 10H17V12H3V10ZM3 14H13V16H3V14Z" :fill="searchBtnHover ? '#8C30F5' : '#fff'"/>
        </svg>
        <span>검색 옵션</span>
      </button>
      <SearchBar @update:categories="onCategoryChange" />
    </div>
    <SearchOptionModal v-if="modalOpen" @close="modalOpen = false" @update:categories="onCategoryChange" />

    <div class="channel-list-table">
      <div class="table-header">
        <div class="th th-profile">프로필</div>
        <div class="th th-detail">채널 상세</div>
        <div class="th th-category">카테고리</div>
        <div class="th th-subscribers">구독자 수</div>
        <div class="th th-views">평균 조회수</div>
        <div class="th th-analysis">분석</div>
      </div>
      <div v-for="(item, idx) in pagedListData" :key="idx" class="table-row">
        <div class="td td-profile">
          <img :src="item.profileImage" class="profile-img" />
        </div>
        <div class="td td-detail">
          <div class="channel-info">
            <div class="channel-name">{{ item.name }}</div>
            <div class="review-row">
              <span class="stars">
                <template v-if="reviewStatsMap[item.id] && reviewStatsMap[item.id].count > 0">
                  <span v-for="n in 5" :key="n">
                    <svg v-if="reviewStatsMap[item.id].avg >= n" width="16" height="16" viewBox="0 0 20 20" fill="#FFC107"><polygon points="10,2 12,7.5 18,7.5 13.5,11.5 15,18 10,14.5 5,18 6.5,11.5 2,7.5 8,7.5"/></svg>
                    <svg v-else-if="reviewStatsMap[item.id].avg >= n-0.5" width="16" height="16" viewBox="0 0 20 20"><defs><linearGradient :id="'half'+item.id+n"><stop offset="50%" stop-color="#FFC107"/><stop offset="50%" stop-color="#eee"/></linearGradient></defs><polygon points="10,2 12,7.5 18,7.5 13.5,11.5 15,18 10,14.5 5,18 6.5,11.5 2,7.5 8,7.5" :fill="'url(#half'+item.id+n+')'"/></svg>
                    <svg v-else width="16" height="16" viewBox="0 0 20 20" fill="#eee"><polygon points="10,2 12,7.5 18,7.5 13.5,11.5 15,18 10,14.5 5,18 6.5,11.5 2,7.5 8,7.5"/></svg>
                  </span>
                  <span class="review-avg">{{ reviewStatsMap[item.id].avg.toFixed(1) }}</span>
                </template>
              </span>
              <span class="review-count" v-if="reviewStatsMap[item.id] && reviewStatsMap[item.id].count > 0">({{ reviewStatsMap[item.id].count }} Reviews)</span>
            </div>
          </div>
        </div>
        <div class="td td-category">
          <span class="badge-category">{{ item.category }}</span>
        </div>
        <div class="td td-subscribers">{{ item.subscribers }}</div>
        <div class="td td-views">{{ item.avgViewCount }}</div>
        <div class="td td-analysis">
          <button class="analysis-btn" @click="goToDetail(item.id)">상세 분석</button>
        </div>
      </div>
    </div>
    <!-- 페이징 버튼 -->
    <div class="pagination">
      <button class="page-btn" :class="{ active: page === 1 }" @click="changePage(1)">&#60;</button>
      <button v-for="p in 6" :key="p" class="page-btn" :class="{ active: page === p }" @click="changePage(p)">{{ p }}</button>
      <button class="page-btn" @click="changePage(page + 1)">&#62;</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import SearchBar from '@/components/search/SearchBar.vue'
import SearchOptionModal from '@/components/search/SearchOptionModal.vue'
import channelApi from '@/api/advertiser/advertiser-channel'
import { reviewApi } from '@/api/advertiser/advertiser-review'

const router = useRouter()
const route = useRoute()
const modalOpen = ref(false)
const page = ref(1) // 현재 페이지 번호
const itemsPerPage = 10 // 페이지당 보여지는 채널 개수
const listData = ref([]) // 전체 채널 데이터 저장할 배열 
const error = ref(null)

const selectedCategories = ref([]) // 선택된 카테고리 저장할 배열

// URL 쿼리에서 선택된 카테고리 초기화
const initializeFromQuery = () => {
  const categoriesFromQuery = route.query.selectedCategories
  if (categoriesFromQuery) {
    try {
      const parsedCategories = JSON.parse(categoriesFromQuery)
      selectedCategories.value = parsedCategories
      onCategoryChange(parsedCategories)
    } catch (err) {
      console.error('카테고리 파싱 실패:', err)
    }
  }
}

const pagedListData = computed(() => { 
  const startIndex = (page.value - 1) * itemsPerPage  
  const endIndex = startIndex + itemsPerPage 
  return listData.value.slice(startIndex, endIndex)
})

const reviewStatsMap = ref({}) // { [channelId]: { avg, count } }

async function fetchAllReviewStats(channels) {
  const statsArr = await Promise.all(
    channels.map(async c => ({
      id: c.id,
      ...(await reviewApi.getReviewStats(c.id))
    }))
  )
  const map = {}
  statsArr.forEach(s => { map[s.id] = { avg: s.avg, count: s.count } })
  reviewStatsMap.value = map
}

function onCategoryChange(categories) {
  // 선택된 카테고리를 저장
  selectedCategories.value = categories
  // 카테고리 변경 시 페이지 1로 이동
  page.value = 1 
  if (categories.length === 0) {
    // 선택된 카테고리가 없으면 빈 배열로 초기화
    listData.value = [] 
    error.value = '선택된 카테고리에 해당하는 채널이 없습니다'
  } else {
    // 선택된 카테고리에 해당하는 채널 데이터 추출
    fetchChannelsByCategories(categories)
  }
}

// 카테고리 필터링 채널 데이터 불러오기
async function fetchChannelsByCategories(categories) {
  try {
    listData.value = await channelApi.getChannelsByCategories(categories)
    await fetchAllReviewStats(listData.value)
  } catch (err) {
    error.value = err.message
  }
}

// 전체 채널 데이터 추출
async function fetchChannels() {
  try {
    listData.value = await channelApi.getAllChannels()
    await fetchAllReviewStats(listData.value)
  } catch (err) {
    error.value = err.message
  }
}

// 페이지 번호 변경 시 페이지 번호 업데이트
function changePage(newPage) {
  page.value = newPage // 페이지 번호 업데이트
}

// URL 쿼리 변경 감지
watch(
  () => route.query,
  () => {
    initializeFromQuery()
  }
)

onMounted(() => {
  initializeFromQuery() // URL 쿼리 파라미터 처리
  // 선택된 카테고리가 없을 경우에만 전체 채널 데이터를 불러옴
  if (selectedCategories.value.length === 0) {
    fetchChannels()
  }
})

// 검색 버튼 호버 효과
const searchBtnHover = ref(false)
function handleSearchBtnMouseEnter() { searchBtnHover.value = true } // 검색 버튼 호버 효과
function handleSearchBtnMouseLeave() { searchBtnHover.value = false }

// 검색바 엔터 입력 시 검색 동작
function handleSearchInputKeydown(e) {
  if (e.key === 'Enter') {
    // 실제 검색 동작 구현 필요
    alert('검색 기능은 추후 구현 예정입니다.')
  }
}

// 검색바 엔터 입력 시 검색 동작
onMounted(() => {
  const input = document.querySelector('.search-input') // 검색바 엔터 입력 시 검색 동작
  if (input) input.addEventListener('keydown', handleSearchInputKeydown) // 검색바 엔터 입력 시 검색 동작
})

// 채널 상세 페이지로 이동
const goToDetail = (id) => {
  router.push(`/channels/${id}`)
}
</script>

<style scoped>
.channel-list-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
  padding: 20px;
  justify-content: center;
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
  flex: 1.2;
}
.th-profile { flex: 1.2; }
.th-detail { flex: 1.2; justify-content: flex-start; }
.th-category {
  flex: 1.2;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding-left: 16px;
}
.th-subscribers { flex: 1.2; }
.th-views { flex: 1.2; }
.th-analysis { flex: 1.2; }

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
  flex: 1.2;
}
.td-profile { flex: 1.2; display: flex; align-items: center; justify-content: center; }
.td-detail { flex: 1.2; display: flex; align-items: center; justify-content: flex-start; }
.td-category {
  flex: 1.2;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding-left: 16px;
}
.td-subscribers { flex: 1.2; font-weight: 700; font-size: 22px; justify-content: center; }
.td-views { flex: 1.2; font-weight: 700; font-size: 20px; justify-content: center; }
.td-analysis { flex: 1.2; justify-content: center; }
.profile-img {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  object-fit: cover;
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

.channel-list-page{
    gap: 20px;
    padding: 50px;
    margin: 0;
    max-width: none;
    box-sizing: border-box;
}

.badge-category {
  display: inline-block;
  background: #E1CFFF;
  color: #8C30F5;
  border-radius: 20px;
  padding: 6px 24px;
  font-size: 19px;
  font-weight: 600;
  margin-right: 6px;
  line-height: 1.6;
}
</style> 