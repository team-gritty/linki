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
        <th>관리자 이름</th>
        <th>관리자 이메일</th>
        <th>관리자 연락처</th>
        <th>로그인 ID</th>
        <th>관리자 가입 승인</th>
      </tr>
    </thead>
    <tbody>
      <!-- 회원 데이터가 없을 때 안내 메시지 출력 -->
      <tr v-if="adminSignUps.length === 0">
        <td colspan="7" class="no-result">해당 정보가 없습니다.</td>
      </tr>
      <!-- 회원 데이터가 있을 때 각 회원 정보를 행으로 출력 -->
      <tr v-else v-for="admin in adminSignUps" :key="admin.adminSignUpId">
        <td>{{ admin.adminName }}</td>
        <td>{{ admin.adminEmail }}</td>
        <td>{{ admin.adminPhone }}</td>
        <td>{{ admin.adminSignUpId }}</td>
        <td>
          <button 
            v-if="admin.adminStatus === 'PENDING'" 
            class="approve-btn"
            @click="handleApprove(admin.adminSignUpId)"
          >승인</button>
          <button 
            v-if="admin.adminStatus === 'PENDING'" 
            class="reject-btn"
            @click="handleReject(admin.adminSignUpId)"
          >거절</button>
          <span v-else class="status completed">가입 승인</span>
        </td>
      </tr>
    </tbody>
  </table>

  <!-- 모바일 카드 뷰: 모바일 환경에서 회원 정보를 카드 형태로 보여줌 -->
  <div class="mobile-view">
    <!-- 회원 데이터가 없을 때 안내 메시지 출력 -->
    <div v-if="adminSignUps.length === 0" class="no-result-card">
      해당 정보가 없습니다.
    </div>
    <!-- 회원 데이터가 있을 때 각 회원 정보를 카드로 출력 -->
    <div v-else v-for="admin in adminSignUps" :key="admin.adminSignUpId" class="member-card">
      <div class="card-header">
        <span class="user-id">{{ admin.adminName }}</span>
        <span class="user-status" :class="admin.adminStatus">{{ admin.adminStatus }}</span>
      </div>
      <div class="card-body">
        <div class="info-row">
          <span class="label">관리자 이메일</span>
          <span class="value">{{ admin.adminEmail }}</span>
        </div>
        <div class="info-row">
          <span class="label">관리자 연락처</span>
          <span class="value">{{ admin.adminPhone }}</span>
        </div>
        <div class="info-row">
          <span class="label">로그인 ID</span>
          <span class="value">{{ admin.adminSignUpId }}</span>
        </div>
        <div class="info-row">
          <span class="value">
            <button 
              v-if="admin.adminStatus === 'PENDING'" 
              class="approve-btn mobile" 
              @click="handleApprove(admin.adminSignUpId)"
            >승인</button>
            <button 
              v-if="admin.adminStatus === 'PENDING'" 
              class="reject-btn mobile" 
              @click="handleReject(admin.adminSignUpId)"
            >거절</button>
            <span v-else class="status completed">가입 승인</span>
          </span>
        </div>
      </div>
    </div>
  </div>

  <!-- Keyset 페이지네이션 컴포넌트 -->
  <KeysetPagination 
    v-if="adminSignUps.length > 0"
    :hasNext="hasNext"
    :hasPrevious="hasPrevious" 
    :isLoading="isLoading"
    :currentSize="adminSignUps.length"
    :totalLoaded="adminSignUps.length"
    @next="goToNextPage"
    @previous="goToPreviousPage"
  />
</template>

<script setup>
// ----------------------
// import 및 변수 선언
// ----------------------
import { ref, onMounted } from 'vue'
import { getAdminSignUpListWithKeyset, searchAdminSignUpWithKeyset, exportExcel, approveAdmin, rejectAdmin } from '@/js/operations/AdminSignUp.js'
import KeysetPagination from '@/components/common/KeysetPagination.vue'
import SearchBar from '@/components/common/SearchBar.vue'

// 관리자 가입 신청 데이터 배열
const adminSignUps = ref([])
// 페이지네이션 상태
const hasNext = ref(false)
const hasPrevious = ref(false)
const isLoading = ref(false)
const pageSize = 10

// 커서 스택 관리 (전역 상태)
const cursorStack = ref([])
let currentCursor = null

// 검색 관련 상태
const isSearchMode = ref(false)
const searchState = ref({ searchType: '', keyword: '' })

// ----------------------
// 검색바 설정
// ----------------------
const searchConfig = {
  options: [
    { value: 'adminName', label: '관리자 이름', endpoint: '/v1/admin/api/adminSignUp/search' },
    { value: 'adminEmail', label: '관리자 이메일', endpoint: '/v1/admin/api/adminSignUp/search' },
    { value: 'adminPhone', label: '관리자 연락처', endpoint: '/v1/admin/api/adminSignUp/search' }
  ],
  placeholder: '검색어를 입력하세요',
  endpoint: '/v1/admin/api/adminSignUp'
}

// ----------------------
// 데이터 로드 함수
// ----------------------
const loadAdminSignUps = async (cursor = null) => {
  try {
    isLoading.value = true
    console.log('🔍 관리자 가입 신청 목록 로드 - cursor:', cursor, 'size:', pageSize)
    
    let response
    if (isSearchMode.value) {
      // 검색 모드
      response = await searchAdminSignUpWithKeyset(
        searchState.value.searchType,
        searchState.value.keyword,
        cursor,
        pageSize
      )
    } else {
      // 일반 모드
      response = await getAdminSignUpListWithKeyset(cursor, pageSize)
    }
    
    if (response.data) {
      // Keyset 응답 구조 처리
      if (response.data.list) {
        adminSignUps.value = response.data.list
        hasNext.value = response.data.hasNext || false
        currentCursor = response.data.nextCursor || null
        
        console.log('📊 관리자 가입 신청 데이터 로드 완료:', {
          count: adminSignUps.value.length,
          hasNext: hasNext.value,
          nextCursor: currentCursor
        })
      } else {
        // 기존 방식 응답
        adminSignUps.value = Array.isArray(response.data) ? response.data : []
        hasNext.value = false
        currentCursor = null
      }
    }
  } catch (error) {
    console.error('관리자 가입 신청 목록 로드 중 오류:', error)
    window.alert('관리자 가입 신청 목록을 불러오지 못했습니다.')
  } finally {
    isLoading.value = false
  }
}

// ----------------------
// 다음 페이지로 이동
// ----------------------
const goToNextPage = async () => {
  if (!hasNext.value || isLoading.value) return
  
  // 현재 커서를 스택에 저장 (이전 페이지로 돌아갈 때 사용)
  if (currentCursor !== null) {
    const stackEntry = {
      cursor: currentCursor,
      searchMode: isSearchMode.value,
      searchType: searchState.value.searchType,
      keyword: searchState.value.keyword
    }
    cursorStack.value.push(stackEntry)
    console.log('📚 커서 스택에 추가:', stackEntry)
  }
  
  // 다음 페이지 로드
  await loadAdminSignUps(currentCursor)
  
  // 이전 페이지 버튼 활성화
  hasPrevious.value = cursorStack.value.length > 0
}

// ----------------------
// 이전 페이지로 이동
// ----------------------
const goToPreviousPage = async () => {
  if (!hasPrevious.value || cursorStack.value.length === 0 || isLoading.value) return
  
  // 스택에서 이전 상태 복원
  const prevState = cursorStack.value.pop()
  console.log('📚 커서 스택에서 복원:', prevState)
  
  // 검색 상태 복원
  isSearchMode.value = prevState.searchMode
  if (prevState.searchMode) {
    searchState.value.searchType = prevState.searchType
    searchState.value.keyword = prevState.keyword
  }
  
  // 이전 페이지 로드
  await loadAdminSignUps(prevState.cursor)
  
  // 이전 페이지 버튼 상태 업데이트
  hasPrevious.value = cursorStack.value.length > 0
}

// ----------------------
// 검색 이벤트 처리 함수
// ----------------------
const handleSearch = async (searchEventState) => {
  try {
    if (!searchEventState.keyword.trim()) {
      // 빈 검색어면 일반 모드로 전환
      isSearchMode.value = false
      searchState.value = { searchType: '', keyword: '' }
      // 커서 스택 초기화
      cursorStack.value = []
      currentCursor = null
      hasPrevious.value = false
      
      await loadAdminSignUps(null)
      return
    }

    // 검색 모드로 전환
    isSearchMode.value = true
    searchState.value = {
      searchType: searchEventState.selectedOption,
      keyword: searchEventState.keyword
    }
    
    // 커서 스택 초기화 (새로운 검색)
    cursorStack.value = []
    currentCursor = null
    hasPrevious.value = false
    
    console.log('🔍 검색 모드 활성화:', searchState.value)
    await loadAdminSignUps(null)
    
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
// 컴포넌트 마운트 시 관리자 가입 신청 목록 불러오기
// ----------------------
onMounted(async () => {
  console.log('🚀 AdminSignUpTable 마운트 시작')
  await loadAdminSignUps(null)
})

// ----------------------
// approve/reject 핸들러 추가
// ----------------------
const handleApprove = async (adminSignUpId) => {
  try {
    await approveAdmin(adminSignUpId)
    window.alert('승인되었습니다.')
    // 현재 페이지 다시 로드
    await loadAdminSignUps(cursorStack.value.length > 0 ? cursorStack.value[cursorStack.value.length - 1].cursor : null)
  } catch (e) {
    window.alert('승인 처리에 실패했습니다.')
  }
}

const handleReject = async (adminSignUpId) => {
  try {
    await rejectAdmin(adminSignUpId)
    window.alert('거절되었습니다.')
    // 현재 페이지 다시 로드
    await loadAdminSignUps(cursorStack.value.length > 0 ? cursorStack.value[cursorStack.value.length - 1].cursor : null)
  } catch (e) {
    window.alert('거절 처리에 실패했습니다.')
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

.status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.9rem;
  font-weight: 500;
}

.status.completed {
  background: #d4edda;
  color: #155724;
}

/* 버튼 스타일 */
.approve-btn {
  background: #2e7d32;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 6px 16px;
  margin-right: 6px;
  font-weight: 600;
  cursor: pointer;
}
.approve-btn:hover { background: #256025; }
.reject-btn {
  background: #fff;
  color: #d32f2f;
  border: 1.5px solid #d32f2f;
  border-radius: 6px;
  padding: 6px 16px;
  font-weight: 600;
  cursor: pointer;
}
.reject-btn:hover {
  background: #d32f2f;
  color: #fff;
}
</style> 