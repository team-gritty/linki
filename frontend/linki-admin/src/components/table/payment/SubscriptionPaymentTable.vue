<template>
  <!-- 상단: 검색바와 엑셀 내보내기 버튼 영역 -->
  <div class="table-header-row">
    <SearchBar 
      :config="searchConfig" 
      @search="handleSearch"
    />
    <button class="export-excel-btn" @click="handleExportExcel">엑셀 내보내기</button>
  </div>

  <!-- 회원 구분 탭 -->
  <div class="member-type-tabs">
    <button :class="{active: selectedType === 'ALL'}" @click="selectedType = 'ALL'">전체</button>
    <button :class="{active: selectedType === 'INFLUENCER'}" @click="selectedType = 'INFLUENCER'">인플루언서</button>
    <button :class="{active: selectedType === 'ADVERTISER'}" @click="selectedType = 'ADVERTISER'">광고주</button>
  </div>

  <!-- 데스크톱 테이블 뷰: 일반 회원 목록을 표 형태로 보여줌 -->
  <table class="member-table desktop-view">
    <thead>
      <tr>
        <th>회원 구분</th>
        <th>이름</th>
        <th>로그인 ID</th>
        <th>연락처</th>
        <th>구독 시작일</th>
        <th>구독 마감일</th>
        <th>이전 결제일</th>
        <th>다음 결제일</th>
      </tr>
    </thead>
    <tbody>
      <!-- 회원 데이터가 없을 때 안내 메시지 출력 -->
      <tr v-if="filteredUsers.length === 0">
        <td colspan="8" class="no-result">해당 정보가 없습니다.</td>
      </tr>
      <!-- 회원 데이터가 있을 때 각 회원 정보를 행으로 출력 -->
      <tr v-else v-for="user in filteredUsers" :key="user.loginId">
        <td>{{ user.memberType }}</td>
        <td>{{ user.name }}</td>
        <td>{{ user.loginId }}</td>
        <td>{{ user.phone }}</td>
        <td>{{ user.subscriptionStartDate }}</td>
        <td>{{ user.subscriptionEndDate }}</td>
        <td>{{ user.previousPaymentDate }}</td>
        <td>{{ user.nextPaymentDate }}</td>
      </tr>
    </tbody>
  </table>

  <!-- 모바일 카드 뷰: 모바일 환경에서 회원 정보를 카드 형태로 보여줌 -->
  <div class="mobile-view">
    <!-- 회원 데이터가 없을 때 안내 메시지 출력 -->
    <div v-if="filteredUsers.length === 0" class="no-result-card">
      해당 정보가 없습니다.
    </div>
    <!-- 회원 데이터가 있을 때 각 회원 정보를 카드로 출력 -->
    <div v-else v-for="user in filteredUsers" :key="user.loginId" class="member-card">
      <div class="card-header">
        <span class="user-id">회원 구분: {{ user.memberType }}</span>
        <span class="user-status" :class="user.memberType">{{ user.memberType }}</span>
      </div>
      <div class="card-body">
        <div class="info-row">
          <span class="label">이름</span>
          <span class="value">{{ user.name }}</span>
        </div>
        <div class="info-row">
          <span class="label">로그인 ID</span>
          <span class="value">{{ user.loginId }}</span>
        </div>
        <div class="info-row">
          <span class="label">연락처</span>
          <span class="value">{{ user.phone }}</span>
        </div>
        <div class="info-row">
          <span class="label">구독 시작일</span>
          <span class="value">{{ user.subscriptionStartDate }}</span>
        </div>
        <div class="info-row">
          <span class="label">구독 마감일</span>
          <span class="value">{{ user.subscriptionEndDate }}</span>
        </div>
        <div class="info-row">
          <span class="label">이전 결제일</span>
          <span class="value">{{ user.previousPaymentDate }}</span>
        </div>
        <div class="info-row">
          <span class="label">다음 결제일</span>
          <span class="value">{{ user.nextPaymentDate }}</span>
        </div>
      </div>
    </div>
  </div>

  <!-- Keyset 페이지네이션 컴포넌트 -->
  <KeysetPagination 
    v-if="filteredUsers.length > 0"
    :hasNext="hasNext"
    :hasPrevious="hasPrevious" 
    :isLoading="isLoading"
    :currentSize="filteredUsers.length"
    :totalLoaded="filteredUsers.length"
    @next="goToNextPage"
    @previous="goToPreviousPage"
  />
</template>

<script setup>
// ----------------------
// import 및 변수 선언
// ----------------------
import { ref, computed, onMounted, watch } from 'vue'
import { getSubscriptionPaymentListWithKeyset, searchSubscriptionPaymentWithKeyset, exportExcel } from '@/js/payment/SubscriptionPayment.js'
import KeysetPagination from '@/components/common/KeysetPagination.vue'
import SearchBar from '@/components/common/SearchBar.vue'

// 구독 결제 데이터 배열
const users = ref([])
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

// 회원 구분 탭
const selectedType = ref('ALL')

// ----------------------
// 검색바 설정
// ----------------------
const searchConfig = {
  options: [
    { value: 'name', label: '이름', endpoint: '/v1/admin/api/subscriptions/search' },
    { value: 'loginId', label: '로그인 ID', endpoint: '/v1/admin/api/subscriptions/search' },
    { value: 'phone', label: '연락처', endpoint: '/v1/admin/api/subscriptions/search' }
  ],
  placeholder: '검색어를 입력하세요',
  endpoint: '/v1/admin/api/subscriptions'
}

// ----------------------
// 데이터 로드 함수 (userId를 커서로 사용)
// ----------------------
const loadUsers = async (cursor = null) => {
  try {
    isLoading.value = true
    console.log('🔍 구독 결제 목록 로드 - cursor:', cursor, 'size:', pageSize)
    
    let response
    if (isSearchMode.value) {
      // 검색 모드
      response = await searchSubscriptionPaymentWithKeyset(
        searchState.value.searchType,
        searchState.value.keyword,
        cursor,
        pageSize
      )
    } else {
      // 일반 모드
      response = await getSubscriptionPaymentListWithKeyset(cursor, pageSize)
    }
    
    if (response.data) {
      // Keyset 응답 구조 처리
      if (response.data.list) {
        users.value = response.data.list
        hasNext.value = response.data.hasNext || false
        currentCursor = response.data.nextCursor || null
        
        console.log('📊 구독 결제 데이터 로드 완료:', {
          count: users.value.length,
          hasNext: hasNext.value,
          nextCursor: currentCursor
        })
      } else {
        // 기존 방식 응답
        users.value = Array.isArray(response.data) ? response.data : []
        hasNext.value = false
        currentCursor = null
      }
    }
  } catch (error) {
    console.error('구독 결제 목록 로드 중 오류:', error)
    window.alert('구독 결제 목록을 불러오지 못했습니다.')
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
      keyword: searchState.value.keyword,
      selectedType: selectedType.value
    }
    cursorStack.value.push(stackEntry)
    console.log('📚 커서 스택에 추가:', stackEntry)
  }
  
  // 다음 페이지 로드
  await loadUsers(currentCursor)
  
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
  
  // 회원 구분 탭 상태 복원
  selectedType.value = prevState.selectedType
  
  // 이전 페이지 로드
  await loadUsers(prevState.cursor)
  
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
      
      await loadUsers(null)
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
    await loadUsers(null)
    
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
// 컴포넌트 마운트 시 구독 결제 목록 불러오기
// ----------------------
onMounted(async () => {
  console.log('🚀 SubscriptionPaymentTable 마운트 시작')
  await loadUsers(null)
})

// ----------------------
// 회원 구분별 필터링 (키셋 페이지네이션에서는 클라이언트 필터링)
// ----------------------
const filteredUsers = computed(() => {
  if (selectedType.value === 'ALL') return users.value
  return users.value.filter(user => user.memberType === selectedType.value)
})

// ----------------------
// 회원 구분 탭 변경 시 해당 타입의 데이터가 나올 때까지 로드
// ----------------------
watch(selectedType, async () => {
  // 탭 변경 시 커서 스택 초기화하고 첫 페이지로 이동
  cursorStack.value = []
  currentCursor = null
  hasPrevious.value = false
  
  // 해당 타입의 데이터가 나올 때까지 계속 로드
  await loadUsersUntilTypeFound(null)
})

// ----------------------
// 특정 타입의 데이터가 나올 때까지 계속 로드하는 함수
// ----------------------
const loadUsersUntilTypeFound = async (cursor = null) => {
  try {
    isLoading.value = true
    console.log('🔍 구독 결제 목록 로드 (타입 필터링) - cursor:', cursor, 'size:', pageSize, 'selectedType:', selectedType.value)
    
    let response
    if (isSearchMode.value) {
      // 검색 모드
      response = await searchSubscriptionPaymentWithKeyset(
        searchState.value.searchType,
        searchState.value.keyword,
        cursor,
        pageSize
      )
    } else {
      // 일반 모드
      response = await getSubscriptionPaymentListWithKeyset(cursor, pageSize)
    }
    
    if (response.data) {
      // Keyset 응답 구조 처리
      if (response.data.list) {
        const newUsers = response.data.list
        const hasMoreData = response.data.hasNext || false
        const nextCursor = response.data.nextCursor || null
        
        // 새로 받은 데이터를 기존 데이터에 추가
        users.value = cursor === null ? newUsers : [...users.value, ...newUsers]
        
        // 필터링된 결과 확인
        const filtered = selectedType.value === 'ALL' ? users.value : users.value.filter(user => user.memberType === selectedType.value)
        
        if (filtered.length === 0 && hasMoreData && nextCursor) {
          // 필터링 결과가 없고 더 많은 데이터가 있으면 다음 페이지 로드
          console.log('📄 필터링 결과 없음, 다음 페이지 로드...')
          await loadUsersUntilTypeFound(nextCursor)
        } else {
          // 필터링 결과가 있거나 더 이상 데이터가 없으면 로딩 완료
          hasNext.value = hasMoreData
          currentCursor = nextCursor
          isLoading.value = false
          
          console.log('📊 구독 결제 데이터 로드 완료 (타입 필터링):', {
            totalCount: users.value.length,
            filteredCount: filtered.length,
            hasNext: hasNext.value,
            nextCursor: currentCursor
          })
        }
      } else {
        // 기존 방식 응답
        users.value = Array.isArray(response.data) ? response.data : []
        hasNext.value = false
        currentCursor = null
        isLoading.value = false
      }
    }
  } catch (error) {
    console.error('구독 결제 목록 로드 중 오류:', error)
    window.alert('구독 결제 목록을 불러오지 못했습니다.')
    isLoading.value = false
  }
}

// 숫자 세자리마다 콤마(,) 포맷 함수
function formatNumber(num) {
  if (num === null || num === undefined) return ''
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
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

.member-type-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  justify-content: flex-start;
  align-items: flex-start;
  width: 100%;
  padding-left: 0;
  margin-left: 0;
}
.member-type-tabs button {
  padding: 8px 18px;
  border: 1.5px solid #7C3AED;
  background: #fff;
  color: #7C3AED;
  border-radius: 8px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.18s;
}
.member-type-tabs button.active, .member-type-tabs button:focus {
  background: #7C3AED;
  color: #fff;
  border-color: #7C3AED;
}
</style> 