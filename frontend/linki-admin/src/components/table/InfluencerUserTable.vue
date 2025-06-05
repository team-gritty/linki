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
        <th>이름</th>
        <th>이메일</th>
        <th>연락처</th>
        <th>SNS 채널명</th>
        <th>SNS 링크</th>
      </tr>
    </thead>
    <tbody>
      <!-- 회원 데이터가 없을 때 안내 메시지 출력 -->
      <tr v-if="users.length === 0">
        <td colspan="6" class="no-result">해당 정보가 없습니다.</td>
      </tr>
      <!-- 회원 데이터가 있을 때 각 회원 정보를 행으로 출력 -->
      <tr v-else v-for="user in pagedUsers" :key="user.userId">
        <td>{{ user.userId }}</td>
        <td>{{ user.name }}</td>
        <td>{{ user.email }}</td>
        <td>{{ user.phone }}</td>
        <td>{{ user.snsChannelName }}</td>
        <td><a :href="user.snsLink" target="_blank">Link</a></td>
      </tr>
    </tbody>
  </table>

  <!-- 모바일 카드 뷰: 모바일 환경에서 회원 정보를 카드 형태로 보여줌 -->
  <div class="mobile-view">
    <!-- 회원 데이터가 없을 때 안내 메시지 출력 -->
    <div v-if="users.length === 0" class="no-result-card">
      해당 정보가 없습니다.
    </div>
    <!-- 회원 데이터가 있을 때 각 회원 정보를 카드로 출력 -->
    <div v-else v-for="user in pagedUsers" :key="user.userId" class="member-card">
      <div class="card-header">
        <span class="user-id">No. {{ user.userId }}</span>
        <span class="user-status" :class="user.user_status">{{ user.user_status }}</span>
      </div>
      <div class="card-body">
        <div class="info-row">
          <span class="label">SNS 채널명</span>
          <span class="value">{{ user.snsChannelName }}</span>
        </div>
        <div class="info-row">
          <span class="label">SNS 링크</span>
          <span class="value"><a :href="user.snsLink" target="_blank">Link</a></span>
        </div>
        <div class="info-row">
          <span class="label">이름</span>
          <span class="value">{{ user.name }}</span>
        </div>
        <div class="info-row">
          <span class="label">연락처</span>
          <span class="value">{{ user.phone }}</span>
        </div>
      </div>
    </div>
  </div>

  <!-- 페이지네이션 컴포넌트: 회원 목록 페이지 이동 -->
  <Pagination 
    v-if="users.length > 0"
    :totalPages="totalPages" 
    :currentPage="currentPage" 
    @update:currentPage="val => currentPage = val" 
  />
</template>

<script setup>
// ----------------------
// import 및 변수 선언
// ----------------------
import { ref, computed, onMounted } from 'vue'
import { getInfluencerUserList, searchInfluencerUser, exportExcel } from '@/js/InfluencerUser'
import Pagination from '@/components/common/Pagination.vue'
import SearchBar from '@/components/common/SearchBar.vue'

// 회원 데이터 배열
const users = ref([])
// 현재 페이지 번호
const currentPage = ref(1)
// 한 페이지에 보여줄 회원 수
const pageSize = 10

// ----------------------
// 검색바 설정
// ----------------------
const searchConfig = {
  options: [
    { value: 'userId', label: '회원번호', endpoint: '/v1/admin/api/advertiserUser/search' },
    { value: 'name', label: '이름', endpoint: '/v1/admin/api/advertiserUser/search' },
    { value: 'email', label: '이메일', endpoint: '/v1/admin/api/advertiserUser/search' },
    { value: 'phone', label: '연락처', endpoint: '/v1/admin/api/advertiserUser/search' },
    { value: 'snsChannelName', label: 'SNS 채널명', endpoint: '/v1/admin/api/advertiserUser/search' },
    { value: 'snsLink', label: 'SNS 링크', endpoint: '/v1/admin/api/advertiserUser/search' }
  ],
  placeholder: '검색어를 입력하세요',
  endpoint: '/v1/admin/api/influencerUser'
}

// ----------------------
// 검색 이벤트 처리 함수
// ----------------------
const handleSearch = async (searchState) => {
  try {
    const response = await searchInfluencerUser(
      searchState.selectedOption,
      searchState.keyword
    )
    
    if (response.data) {
      users.value = Array.isArray(response.data) ? response.data : []
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
    const res = await getInfluencerUserList(1, 10)
    users.value = Array.isArray(res.data) ? res.data : []
  } catch (e) {
    window.alert('회원 목록을 불러오지 못했습니다.')
  }
})

// ----------------------
// 현재 페이지에 보여줄 회원 데이터 계산
// ----------------------
const pagedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return users.value.slice(start, start + pageSize)
})

// ----------------------
// 전체 페이지 수 계산
// ----------------------
const totalPages = computed(() => Math.ceil(users.value.length / pageSize))
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

.card-header {
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
</style> 