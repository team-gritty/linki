<template>
  <div class="channel-access-tester" v-if="isDevelopment">
    <h3>🔧 채널 접근 제한 테스터 (개발용)</h3>
    
    <div class="tester-section">
      <h4>사용자 설정</h4>
      <div class="user-controls">
        <button @click="setTestUser('ROLE_USER')" :class="{ active: currentRole === 'ROLE_USER' }">
          일반회원 (5회 제한)
        </button>
        <button @click="setTestUser('ROLE_INFLUENCER')" :class="{ active: currentRole === 'ROLE_INFLUENCER' }">
          인플루언서 (제한 없음)
        </button>
        <button @click="setTestUser('ROLE_ADVERTISER')" :class="{ active: currentRole === 'ROLE_ADVERTISER' }">
          광고주 (제한 없음)
        </button>
        <button @click="logout" class="logout-btn">로그아웃</button>
      </div>
    </div>

    <div class="tester-section" v-if="accountStore.isLoggedIn">
      <h4>현재 상태</h4>
      <div class="status-info">
        <p><strong>사용자 ID:</strong> {{ accountStore.user?.userId }}</p>
        <p><strong>역할:</strong> {{ accountStore.user?.userRole }}</p>
        <p><strong>조회 횟수:</strong> {{ channelAccessStore.getCurrentUserAccessCount }}/5</p>
        <p><strong>접근 가능:</strong> {{ accessInfo.canAccess ? '✅' : '❌' }}</p>
        <p><strong>남은 횟수:</strong> {{ accessInfo.remainingCount }}</p>
      </div>
    </div>

    <div class="tester-section">
      <h4>테스트 기능</h4>
      <div class="test-controls">
        <button @click="testChannelAccess" class="test-btn">
          채널 접근 테스트
        </button>
        <button @click="resetAccessCount" class="reset-btn">
          조회 횟수 초기화
        </button>
        <button @click="cleanupData" class="cleanup-btn">
          모든 데이터 정리
        </button>
      </div>
    </div>

    <div class="tester-section" v-if="testResults.length > 0">
      <h4>테스트 결과</h4>
      <div class="test-results">
        <div v-for="(result, index) in testResults" :key="index" class="result-item">
          <span class="result-time">{{ result.time }}</span>
          <span class="result-message" :class="{ success: result.success, error: !result.success }">
            {{ result.message }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useAccountStore } from '@/stores/account'
import { useChannelAccessStore } from '@/stores/channelAccess'

// 개발 환경에서만 표시
const isDevelopment = ref(import.meta.env.MODE === 'development')

const accountStore = useAccountStore()
const channelAccessStore = useChannelAccessStore()
const testResults = ref([])

const currentRole = computed(() => accountStore.user?.userRole)
const accessInfo = computed(() => channelAccessStore.canAccessChannelDetail)

// 테스트 사용자 설정
function setTestUser(role) {
  const testToken = createTestToken(role)
  accountStore.setAccessToken(testToken)
  
  addTestResult(`${role} 사용자로 로그인 완료`, true)
}

// 테스트용 JWT 토큰 생성 (실제 JWT는 아니지만 구조만 맞춤)
function createTestToken(role) {
  const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }))
  
  const payload = btoa(JSON.stringify({
    userId: `test-user-${Date.now()}`,
    userRole: role,
    userName: `테스트 ${role.replace('ROLE_', '')}`,
    userEmail: `test@example.com`,
    exp: Math.floor(Date.now() / 1000) + (60 * 60), // 1시간 후 만료
    iat: Math.floor(Date.now() / 1000)
  }))
  
  const signature = btoa('test-signature')
  
  return `${header}.${payload}.${signature}`
}

// 로그아웃
function logout() {
  accountStore.clearAuth()
  addTestResult('로그아웃 완료', true)
}

// 채널 접근 테스트
function testChannelAccess() {
  if (!accountStore.isLoggedIn) {
    addTestResult('로그인이 필요합니다', false)
    return
  }

  const result = channelAccessStore.attemptChannelAccess()
  
  if (result.success) {
    addTestResult(`채널 접근 성공! (${result.currentCount || 'unlimited'}/${result.maxCount || '∞'})`, true)
  } else {
    addTestResult(`채널 접근 실패: ${result.message}`, false)
  }
}

// 조회 횟수 초기화
function resetAccessCount() {
  const success = channelAccessStore.resetUserAccessCount()
  addTestResult(success ? '조회 횟수 초기화 완료' : '초기화 실패', success)
}

// 모든 데이터 정리
function cleanupData() {
  channelAccessStore.resetAllAccessData()
  accountStore.clearAuth()
  testResults.value = []
  addTestResult('모든 데이터 정리 완료', true)
}

// 테스트 결과 추가
function addTestResult(message, success) {
  testResults.value.unshift({
    time: new Date().toLocaleTimeString(),
    message,
    success
  })
  
  // 최대 10개 결과만 유지
  if (testResults.value.length > 10) {
    testResults.value = testResults.value.slice(0, 10)
  }
}
</script>

<style scoped>
.channel-access-tester {
  position: fixed;
  top: 20px;
  right: 20px;
  width: 350px;
  background: white;
  border: 2px solid #8C30F5;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  z-index: 9999;
  font-size: 14px;
}

.channel-access-tester h3 {
  margin: 0 0 16px 0;
  color: #8C30F5;
  font-size: 16px;
}

.channel-access-tester h4 {
  margin: 12px 0 8px 0;
  color: #333;
  font-size: 14px;
}

.tester-section {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.tester-section:last-child {
  border-bottom: none;
}

.user-controls {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.user-controls button {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
}

.user-controls button:hover {
  background: #f5f5f5;
}

.user-controls button.active {
  background: #8C30F5;
  color: white;
  border-color: #8C30F5;
}

.logout-btn {
  background: #ff4757 !important;
  color: white !important;
  border-color: #ff4757 !important;
}

.status-info p {
  margin: 4px 0;
}

.test-controls {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.test-btn {
  background: #2ed573;
  color: white;
  border: none;
}

.reset-btn {
  background: #ffa502;
  color: white;
  border: none;
}

.cleanup-btn {
  background: #ff4757;
  color: white;
  border: none;
}

.test-results {
  max-height: 200px;
  overflow-y: auto;
}

.result-item {
  display: flex;
  gap: 8px;
  padding: 4px 0;
  font-size: 12px;
}

.result-time {
  color: #666;
  min-width: 70px;
}

.result-message.success {
  color: #2ed573;
}

.result-message.error {
  color: #ff4757;
}
</style> 