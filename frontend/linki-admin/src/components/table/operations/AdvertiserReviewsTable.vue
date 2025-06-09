<template>
  <!-- 상단: 검색바와 엑셀 내보내기 버튼 영역 -->
  <div class="table-header-row">
    <SearchBar 
      :config="searchConfig" 
      @search="handleSearch"
    />
    <button class="export-excel-btn" @click="handleExportExcel">엑셀 내보내기</button>
  </div>

  <!-- 데스크톱 테이블 뷰: 일반 회원 목록을 표 형태로 보여줌 -->
  <table class="member-table desktop-view">
    <thead>
      <tr>
        <th>No</th>
        <th>광고주 </th>
        <th>작성자</th>
        <th>평점</th>
        <th>작성일</th> 
        <th>공개여부(비공개/공개)</th>
        <th>상세</th>
      </tr>
    </thead>
    <tbody>
      <!-- 회원 데이터가 없을 때 안내 메시지 출력 -->
      <tr v-if="reviews.length === 0">
        <td colspan="6" class="no-result">해당 정보가 없습니다.</td>
      </tr>
      <!-- 회원 데이터가 있을 때 각 회원 정보를 행으로 출력 -->
      <tr v-else v-for="review in pagedReviews" :key="review.influencerReviewId">
        <td>{{ review.advertiserReviewId }}</td>
        <td>{{ review.advertiserName }}</td>
        <td>{{ review.writer }}</td>
        <td>{{ review.rating }}</td>
        <td>{{ review.reviewDate }}</td>
        <td style="text-align:center;">
          <button type="button" 
                  class="toggle-visibility"
                  :class="(review.visibility === true || review.visibility === 'true') ? 'open' : 'closed'"
                  @click="handleToggleVisibility(review)">
            {{ review.visibility === true || review.visibility === 'true' ? '공개' : '비공개' }}
          </button>
        </td>
        <td>
          <button class="detail-btn" @click="openModal(review)">상세</button>
        </td>
      </tr>
    </tbody>
  </table>

  <!-- 모바일 카드 뷰: 모바일 환경에서 회원 정보를 카드 형태로 보여줌 -->
  <div class="mobile-view">
    <!-- 회원 데이터가 없을 때 안내 메시지 출력 -->
    <div v-if="reviews.length === 0" class="no-result-card">
      해당 정보가 없습니다.
    </div>
    <!-- 회원 데이터가 있을 때 각 회원 정보를 카드로 출력 -->
    <div v-else v-for="review in pagedReviews" :key="review.influencerReviewId" class="member-card">
      <div class="card-header-row">
        <span class="user-id">{{ review.advertiserReviewId }}</span>
        <span class="influencer-name">{{ review.advertiserName }}</span>
        <button type="button"
                class="toggle-visibility"
                :class="(review.visibility === true || review.visibility === 'true') ? 'open' : 'closed'"
                @click="handleToggleVisibility(review)">
          {{ review.visibility === true || review.visibility === 'true' ? '공개' : '비공개' }}
        </button>
      </div>
      <div class="card-body">
        <div class="info-row"><span class="label">작성자</span><span class="value">{{ review.writer }}</span></div>
        <div class="info-row"><span class="label">평점</span><span class="value">{{ review.rating }}</span></div>
        <div class="info-row"><span class="label">작성일</span><span class="value">{{ review.reviewDate }}</span></div>
        <div class="info-row"><span class="label">계약ID</span><span class="value contract-id-mobile">{{ review.contractId }}</span></div>
        <div class="info-row review-body-mobile"><span class="label">리뷰</span><span class="value">{{ review.review }}</span></div>
      </div>
    </div>
  </div>

  <!-- 페이지네이션 컴포넌트: 회원 목록 페이지 이동 -->
  <Pagination 
    v-if="reviews.length > 0"
    :totalPages="totalPages" 
    :currentPage="currentPage" 
    @update:currentPage="val => currentPage = val" 
  />

  <!-- 모달 -->
  <div v-if="modalOpen" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content">
      <h3>리뷰 상세</h3>
      <div class="modal-fields">
        <div class="modal-field"><span class="modal-label">광고주</span><span class="modal-value">{{ selectedReview.advertiserName }}</span></div>
        <div class="modal-field"><span class="modal-label">작성자</span><span class="modal-value">{{ selectedReview.writer }}</span></div>
        <div class="modal-field"><span class="modal-label">평점</span><span class="modal-value">{{ selectedReview.rating }}</span></div>
        <div class="modal-field"><span class="modal-label">작성일</span><span class="modal-value">{{ selectedReview.reviewDate }}</span></div>
        <div class="modal-field"><span class="modal-label">공개여부</span><span class="modal-value">{{ selectedReview.visibility }}</span></div>
      </div>
      <div class="review-body">{{ selectedReview.review }}</div>
      <button class="close-btn" @click="closeModal">닫기</button>
    </div>
  </div>
</template>

<script setup>
// ----------------------
// import 및 변수 선언
// ----------------------
import { ref, computed, onMounted } from 'vue'
import { getAdvertisersReviewsList, searchAdvertisersReviews, exportExcel, toggleReviewVisibility } from '@/js/operations/AdvertisersReviews.js'
import Pagination from '@/components/common/Pagination.vue'
import SearchBar from '@/components/common/SearchBar.vue'

// 회원 데이터 배열
const reviews = ref([])
// 현재 페이지 번호
const currentPage = ref(1)
// 한 페이지에 보여줄 회원 수
const pageSize = 10

// ----------------------
// 검색바 설정
// ----------------------
const searchConfig = {
  options: [
    { value: 'advertisersName', label: '광고주', endpoint: '/v1/admin/api/advertisersReviews/search' },
    { value: 'writer', label: '작성자', endpoint: '/v1/admin/api/advertisersReviews/search' },
    { value: 'contractId', label: '계약 ID', endpoint: '/v1/admin/api/advertisersReviews/search' }
  ],
  placeholder: '검색어를 입력하세요',
  endpoint: '/v1/admin/api/advertisersReviews'
}

// ----------------------
// 검색 이벤트 처리 함수
// ----------------------
const handleSearch = async (searchState) => {
  try {
    const response = await searchAdvertisersReviews(
      searchState.selectedOption,
      searchState.keyword
    )
    if (response.data) {
      reviews.value = Array.isArray(response.data) ? response.data : []
      currentPage.value = 1 // 검색 시 첫 페이지로 이동
    }
  } catch (error) {
    console.error('검색 중 오류 발생:', error)
    window.alert('검색 중 오류가 발생했습니다.')
  }
}

// ----------------------
// 엑셀 내보내기 버튼 클릭 시 실행되는 함수
// ----------------------
const handleExportExcel = async () => {
  try {
    await exportExcel()
    window.alert('엑셀 파일이 성공적으로 요청되었습니다.')
  } catch (e) {
    window.alert('엑셀 내보내기 요청에 실패했습니다.')
  }
}

// ----------------------
// 컴포넌트 마운트 시 회원 목록 불러오기
// ----------------------
onMounted(async () => {
  try {
    const res = await getAdvertisersReviewsList()
    reviews.value = Array.isArray(res.data) ? res.data : []
  } catch (e) {
    window.alert('리뷰 목록을 불러오지 못했습니다.')
  }
})

// ----------------------
// 현재 페이지에 보여줄 회원 데이터 계산
// ----------------------
const pagedReviews = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return reviews.value.slice(start, start + pageSize)
})

// ----------------------
// 전체 페이지 수 계산
// ----------------------
const totalPages = computed(() => Math.ceil(reviews.value.length / pageSize))

const modalOpen = ref(false)
const selectedReview = ref({})

const openModal = (review) => {
  selectedReview.value = review
  modalOpen.value = true
}
const closeModal = () => {
  modalOpen.value = false
}

// 공개여부 토글 함수 추가
const handleToggleVisibility = async (review) => {
  try {
    const newVisibility = !review.visibility
    await toggleReviewVisibility(review.advertiserReviewId, newVisibility)
    review.visibility = newVisibility
  } catch (e) {
    window.alert('공개여부 변경에 실패했습니다.')
  }
}
</script>

<style scoped>
.table-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  width: 100%;
}

@media screen and (max-width: 768px) {
  .table-header-row {
    flex-direction: column;
    align-items: center;
    gap: 10px;
    max-width: 360px;
    margin: 0 auto 12px auto;
    width: 100%;
  }
  .export-excel-btn,
  .search-wrapper {
    width: 100%;
    max-width: 360px;
  }
  .export-excel-btn {
    margin-left: 0;
  }
}

.member-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(80, 60, 180, 0.08);
  margin-bottom: 32px;
  font-family: 'Pretendard', 'Noto Sans KR', Arial, sans-serif;
}

th, td {
  text-align: center;
}

th {
  background: #f4f2fa;
  color: #333;
  font-weight: 700;
  font-size: 1.08rem;
  border-bottom: 2px solid #ececec;
  padding: 18px 0;
  height: 56px;
}

td {
  background: #fff;
  font-size: 1rem;
  color: #222;
  border-bottom: 1px solid #f0f0f0;
  padding: 16px 0;
  height: 56px;
  transition: background 0.2s;
}

tr:hover td {
  background: #f7f3ff;
}

tr:last-child td {
  border-bottom: none;
}

.no-result {
  text-align: center;
  padding: 20px 0;
  color: #666;
  font-size: 1rem;
  background: #fff;
}

.no-result-card {
  text-align: center;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(80, 60, 180, 0.08);
  color: #666;
  font-size: 1rem;
  margin-bottom: 16px;
}

/* 모바일 카드 스타일 */
.mobile-view {
  display: none;
}

.member-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(80, 60, 180, 0.08);
  margin-bottom: 16px;
  overflow: hidden;
  max-width: 360px;
  margin-left: auto;
  margin-right: auto;
}

.card-header-row {
  background: #f4f2fa;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #ececec;
}

.user-id {
  font-weight: 600;
  color: #333;
}

.user-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.9rem;
  font-weight: 500;
}

.card-body {
  padding: 16px;
}

.info-row {
  display: flex;
  margin-bottom: 12px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.label {
  width: 80px;
  color: #666;
  font-size: 0.9rem;
}

.value {
  flex: 1;
  color: #222;
  font-weight: 500;
}

/* 반응형 스타일 */
@media screen and (max-width: 768px) {
  .desktop-view {
    display: none;
  }
  
  .mobile-view {
    display: block;
  }
}

.export-excel-btn {
  padding: 8px 20px;
  background: #2e7d32;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
  margin-left: 16px;
}

.export-excel-btn:hover {
  background: #256025;
}

.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}
.modal-content {
  background: #fff;
  border-radius: 16px;
  padding: 36px 32px 28px 32px;
  min-width: 340px;
  max-width: 95vw;
  box-shadow: 0 8px 32px rgba(80,60,180,0.13), 0 1.5px 6px rgba(0,0,0,0.06);
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  position: relative;
}
.modal-content h3 {
  font-size: 1.25rem;
  font-weight: 700;
  margin-bottom: 18px;
  color: #7C3AED;
}
.modal-fields {
  width: 100%;
  margin-bottom: 18px;
}
.modal-field {
  display: flex;
  margin-bottom: 10px;
  font-size: 1.05rem;
}
.modal-label {
  width: 90px;
  color: #888;
  font-weight: 500;
  flex-shrink: 0;
}
.modal-value {
  color: #222;
  font-weight: 600;
  word-break: break-all;
}
.review-body {
  background: #f7f3ff;
  padding: 18px 14px;
  border-radius: 8px;
  margin: 10px 0 24px 0;
  white-space: pre-line;
  color: #333;
  font-size: 1.08rem;
  line-height: 1.7;
  width: 100%;
  box-sizing: border-box;
}
.close-btn {
  align-self: flex-end;
  background: #7C3AED;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 10px 28px;
  font-size: 1.05rem;
  font-weight: 600;
  cursor: pointer;
  margin-top: 8px;
  transition: background 0.18s;
}
.close-btn:hover {
  background: #5B21B6;
}
@media (max-width: 600px) {
  .modal-content {
    padding: 18px 6px 18px 6px;
    min-width: unset;
    max-width: 100vw;
  }
  .modal-fields {
    margin-bottom: 10px;
  }
  .review-body {
    padding: 12px 6px;
    font-size: 1rem;
  }
  .close-btn {
    width: 100%;
    padding: 12px 0;
    font-size: 1rem;
  }
}

.detail-btn {
  background: none;
  border: 1.5px solid #7C3AED;
  color: #7C3AED;
  border-radius: 6px;
  padding: 6px 18px;
  font-size: 0.98rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.18s;
  outline: none;
}
.detail-btn:hover {
  background: #7C3AED;
  color: #fff;
  box-shadow: 0 2px 8px rgba(124, 58, 237, 0.08);
}

/* 스타일 추가 */
.toggle-visibility {
  display: inline-block;
  min-width: 56px;
  padding: 7px 0;
  border-radius: 8px;
  background: #fff;
  border: 2px solid #a78bfa;
  color: #7C3AED;
  font-weight: 700;
  font-size: 1rem;
  text-align: center;
  cursor: pointer;
  transition: 
    background 0.18s, 
    color 0.18s, 
    border-color 0.18s, 
    box-shadow 0.18s;
  box-shadow: 0 1.5px 6px rgba(124,58,237,0.06);
  outline: none;
}
.toggle-visibility:hover, .toggle-visibility:focus {
  background: #7C3AED;
  color: #fff;
  border-color: #7C3AED;
  box-shadow: 0 2px 8px rgba(124,58,237,0.13);
}

/* 상태별 색상 분기 */
.toggle-visibility.open {
  background: #7C3AED;
  color: #fff;
  border-color: #7C3AED;
}
.toggle-visibility.open:hover, .toggle-visibility.open:focus {
  background: #5B21B6;
  border-color: #5B21B6;
}
.toggle-visibility.closed {
  background: #fff;
  color: #7C3AED;
  border: 2px solid #a78bfa;
}
.toggle-visibility.closed:hover, .toggle-visibility.closed:focus {
  background: #ede9fe;
  color: #5B21B6;
  border-color: #7C3AED;
}

.contract-id {
  max-width: 110px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
  vertical-align: middle;
  font-size: 0.97em;
}

.contract-id-mobile {
  word-break: break-all;
  white-space: normal;
  font-size: 0.97em;
  line-height: 1.4;
  display: block;
}

@media screen and (max-width: 768px) {
  .contract-id-mobile {
    word-break: break-all;
    white-space: normal;
    font-size: 0.97em;
    line-height: 1.4;
    display: block;
  }
}
</style> 