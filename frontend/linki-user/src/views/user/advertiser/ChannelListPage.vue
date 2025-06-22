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
          <img :src="item.thumbnailUrl" class="profile-img" />
        </div>
        <div class="td td-detail">
          <div class="channel-info">
            <div class="channel-channelName">{{ item.channelName }}</div>
            <div class="review-row">
              <span class="stars">
                <template v-if="reviewStatsMap[item.channelId] && reviewStatsMap[item.channelId].count > 0">
                  <span v-for="n in 5" :key="n">
                    <svg v-if="reviewStatsMap[item.channelId].avg >= n" width="16" height="16" viewBox="0 0 20 20" fill="#FFC107"><polygon points="10,2 12,7.5 18,7.5 13.5,11.5 15,18 10,14.5 5,18 6.5,11.5 2,7.5 8,7.5"/></svg>
                    <svg v-else-if="reviewStatsMap[item.channelId].avg >= n-0.5" width="16" height="16" viewBox="0 0 20 20"><defs><linearGradient :id="'half'+item.channelId+n"><stop offset="50%" stop-color="#FFC107"/><stop offset="50%" stop-color="#eee"/></linearGradient></defs><polygon points="10,2 12,7.5 18,7.5 13.5,11.5 15,18 10,14.5 5,18 6.5,11.5 2,7.5 8,7.5" :fill="'url(#half'+item.channelId+n+')'"/></svg>
                    <svg v-else width="16" height="16" viewBox="0 0 20 20" fill="#eee"><polygon points="10,2 12,7.5 18,7.5 13.5,11.5 15,18 10,14.5 5,18 6.5,11.5 2,7.5 8,7.5"/></svg>
                  </span>
                  <span class="review-avg">{{ reviewStatsMap[item.channelId].avg.toFixed(1) }}</span>
                </template>
                <template v-else>
                  <span v-for="n in 5" :key="n">
                    <svg width="16" height="16" viewBox="0 0 20 20" fill="#eee"><polygon points="10,2 12,7.5 18,7.5 13.5,11.5 15,18 10,14.5 5,18 6.5,11.5 2,7.5 8,7.5"/></svg>
                  </span>
                  <span class="review-avg">0.0</span>
                </template>
              </span>
            </div>
          </div>
        </div>
        <div class="td td-category">
          <span class="badge-category">{{ item.category }}</span>
        </div>
        <div class="td td-subscribers">{{ item.subscriberCount }}</div>
        <div class="td td-views">{{ item.avgViewCount }}</div>
        <div class="td td-analysis">
          <button class="analysis-btn" @click="goToDetail(item.channelId)">상세 분석</button>
        </div>
      </div>
    </div>
    <!-- 페이징 버튼 -->
    <div class="pagination" v-if="listData.pageInfo">
      <!-- 처음 페이지로 이동 -->
      <button 
        class="page-btn nav-btn" 
        :disabled="page === 1" 
        @click="changePage(1)"
        title="처음 페이지"
      >
        &#171;
      </button>
      
      <!-- 이전 페이지로 이동 -->
      <button 
        class="page-btn nav-btn" 
        :disabled="!listData.pageInfo.hasPrevious" 
        @click="changePage(page - 1)"
        title="이전 페이지"
      >
        &#60;
      </button>
      
      <!-- 페이지 번호들 -->
      <button 
        v-for="p in getVisiblePageNumbers()" 
        :key="p" 
        class="page-btn" 
        :class="{ active: page === p }" 
        @click="changePage(p)"
      >
        {{ p }}
      </button>
      
      <!-- 다음 페이지로 이동 -->
      <button 
        class="page-btn nav-btn" 
        :disabled="!listData.pageInfo.hasNext" 
        @click="changePage(page + 1)"
        title="다음 페이지"
      >
        &#62;
      </button>
      
      <!-- 마지막 페이지로 이동 -->
      <button 
        class="page-btn nav-btn" 
        :disabled="page === listData.pageInfo.totalPages" 
        @click="changePage(listData.pageInfo.totalPages)"
        title="마지막 페이지"
      >
        &#187;
      </button>
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
const listData = ref({ channels: [], pageInfo: null }) // 채널 데이터와 페이지네이션 정보를 포함한 객체
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
  // API에서 이미 페이지네이션이 처리되어 오므로 그대로 반환
  return listData.value.channels || []
})

const reviewStatsMap = ref({}) // { [channelId]: { avg, count } }

async function fetchAllReviewStats(channels) {
  // 방어 코드 추가: channels가 undefined이거나 배열이 아닌 경우 처리
  if (!channels || !Array.isArray(channels) || channels.length === 0) {
    console.log('리뷰 통계 조회 건너뜀: 유효하지 않은 채널 데이터', channels)
    reviewStatsMap.value = {}
    return
  }
  
  console.log('리뷰 통계 조회 시작:', channels.length, '개 채널')
  try {
    const statsArr = await Promise.allSettled(
      channels.map(async c => {
        try {
          const stats = await reviewApi.getReviewStats(c.channelId)
          return {
            channelId: c.channelId,
            ...stats
          }
        } catch (error) {
          console.warn(`채널 ${c.channelId} 리뷰 통계 조회 실패:`, error)
          // 에러가 발생한 채널은 기본값 사용
          return {
            channelId: c.channelId,
            avg: 0,
            count: 0
          }
        }
      })
    )
    
    const map = {}
    statsArr.forEach(result => {
      if (result.status === 'fulfilled' && result.value) {
        const s = result.value
        map[s.channelId] = { avg: s.avg || 0, count: s.count || 0 }
      }
    })
    
    reviewStatsMap.value = map
    console.log('리뷰 통계 조회 완료:', Object.keys(map).length, '개 채널 처리됨')
  } catch (error) {
    console.error('리뷰 통계 전체 조회 실패:', error)
    // 전체 실패 시에도 빈 맵으로 초기화하여 페이지네이션이 계속 작동하도록 함
    reviewStatsMap.value = {}
  }
}

function onCategoryChange(categories) {
  // 선택된 카테고리를 저장
  selectedCategories.value = categories
  // 카테고리 변경 시 페이지 1로 이동
  page.value = 1 
  if (categories.length === 0) {
    // 선택된 카테고리가 없으면 빈 배열로 초기화
    listData.value = { channels: [], pageInfo: null } 
    error.value = '선택된 카테고리에 해당하는 채널이 없습니다'
  } else {
    // 선택된 카테고리에 해당하는 채널 데이터 추출
    fetchChannelsByCategories(categories)
  }
}

// 페이지 번호 변경 시 페이지 번호 업데이트 및 API 호출
function changePage(newPage) {
  console.log('changePage 호출:', { 
    newPage, 
    currentPage: page.value, 
    totalPages: listData.value.pageInfo?.totalPages,
    hasNext: listData.value.pageInfo?.hasNext,
    hasPrevious: listData.value.pageInfo?.hasPrevious
  })
  
  if (newPage < 1) return // 1페이지 미만으로 갈 수 없음
  
  // 백엔드에서 받은 페이지 정보가 있다면 총 페이지 수 확인
  if (listData.value.pageInfo && newPage > listData.value.pageInfo.totalPages) {
    console.log('총 페이지 수를 넘어감:', newPage, '>', listData.value.pageInfo.totalPages)
    return // 총 페이지 수를 넘어갈 수 없음
  }
  
  page.value = newPage // 페이지 번호 업데이트
  console.log('페이지 업데이트 완료:', page.value)
  
  // 선택된 카테고리가 있으면 카테고리별 조회, 없으면 전체 조회
  if (selectedCategories.value.length > 0) {
    console.log('카테고리별 조회 호출:', selectedCategories.value, 'page:', newPage)
    fetchChannelsByCategories(selectedCategories.value, newPage)
  } else {
    console.log('전체 조회 호출, page:', newPage)
    fetchChannels(newPage)
  }
}

// 전체 채널 데이터 추출
async function fetchChannels(pageNumber = 1) {
  console.log('fetchChannels 호출:', { pageNumber, backendPage: pageNumber - 1 })
  try {
    const result = await channelApi.getAllChannels(pageNumber - 1) // 백엔드는 0부터 시작
    console.log('전체 채널 API 응답:', result)
    listData.value = result
    
    // 리뷰 통계는 선택적으로 로드 (실패해도 메인 기능에 영향 없음)
    try {
      // result.channels가 존재하고 배열인지 확인 후 호출
      if (result && result.channels && Array.isArray(result.channels)) {
        await fetchAllReviewStats(result.channels)
      } else {
        console.warn('채널 데이터가 유효하지 않음:', result)
        reviewStatsMap.value = {}
      }
    } catch (reviewError) {
      console.warn('리뷰 통계 조회 실패했지만 채널 목록은 정상 표시:', reviewError)
    }
  } catch (err) {
    console.error('전체 채널 조회 실패:', err)
    error.value = err.message
  }
}

// 카테고리 필터링 채널 데이터 불러오기
async function fetchChannelsByCategories(categories, pageNumber = 1) {
  console.log('fetchChannelsByCategories 호출:', { categories, pageNumber, backendPage: pageNumber - 1 })
  try {
    const result = await channelApi.getChannelsByCategories(categories, pageNumber - 1) // 백엔드는 0부터 시작
    console.log('카테고리별 API 응답:', result)
    listData.value = result
    
    // 리뷰 통계는 선택적으로 로드 (실패해도 메인 기능에 영향 없음)
    try {
      // result.channels가 존재하고 배열인지 확인 후 호출
      if (result && result.channels && Array.isArray(result.channels)) {
        await fetchAllReviewStats(result.channels)
      } else {
        console.warn('채널 데이터가 유효하지 않음:', result)
        reviewStatsMap.value = {}
      }
    } catch (reviewError) {
      console.warn('리뷰 통계 조회 실패했지만 채널 목록은 정상 표시:', reviewError)
    }
  } catch (err) {
    console.error('카테고리별 채널 조회 실패:', err)
    error.value = err.message
  }
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
const goToDetail = (channelId) => {
  router.push(`/channels/${channelId}`)
}

// 페이지 번호 배열 생성
function getPageNumbers() {
  const pageNumbers = []
  for (let i = 1; i <= listData.value.pageInfo.totalPages; i++) {
    pageNumbers.push(i)
  }
  return pageNumbers
}

// 현재 페이지 주변의 5개 페이지만 보여주도록 수정
function getVisiblePageNumbers() {
  const totalPages = listData.value.pageInfo.totalPages
  const currentPage = page.value
  const visiblePages = []
  
  // 총 페이지가 5개 이하면 모든 페이지 표시
  if (totalPages <= 5) {
    for (let i = 1; i <= totalPages; i++) {
      visiblePages.push(i)
    }
    return visiblePages
  }
  
  // 현재 페이지를 중심으로 앞뒤 2개씩 총 5개 페이지 표시
  let start = Math.max(1, currentPage - 2)
  let end = Math.min(totalPages, currentPage + 2)
  
  // 시작점이나 끝점 조정
  if (end - start < 4) {
    if (start === 1) {
      end = Math.min(totalPages, start + 4)
    } else if (end === totalPages) {
      start = Math.max(1, end - 4)
    }
  }
  
  for (let i = start; i <= end; i++) {
    visiblePages.push(i)
  }
  
  return visiblePages
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
  height: 140px;
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
.channel-channelName {
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
  gap: 4px;
  margin: 32px 0 0 0;
}

.page-btn {
  width: 36px;
  height: 36px;
  border-radius: 6px;
  border: 1px solid #E0E0E0;
  background: #fff;
  color: #666;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-btn:hover:not(:disabled) {
  background: #f8f9fa;
  border-color: #8C30F5;
  color: #8C30F5;
}

.page-btn.active {
  background: #8C30F5;
  color: #fff;
  border-color: #8C30F5;
}

.page-btn:disabled {
  background: #f5f5f5;
  color: #ccc;
  border-color: #e5e5e5;
  cursor: not-allowed;
}

.page-btn.nav-btn {
  font-size: 16px;
  font-weight: 700;
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