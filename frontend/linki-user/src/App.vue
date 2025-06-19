<script setup>
import { RouterLink, RouterView } from 'vue-router'
import Header from './components/Header.vue'
import Sidebar from './components/Sidebar.vue'
import Footer from './components/Footer.vue'
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAccountStore } from './stores/account'
import Chatbot from '@/components/chatbot/Chatbot.vue'
import Alert from '@/components/common/Alert.vue'
import axios from 'axios'

const router = useRouter()
const accountStore = useAccountStore()
const openSidebar = ref(false)

const toggleSidebar = () => {
  openSidebar.value = !openSidebar.value
}

// 앱 시작 시 로그인 상태 초기화
onMounted(() => {
  // localStorage에서 토큰 확인
  const token = localStorage.getItem('token')
  if (token) {
    try {
      // JWT 토큰 파싱
      const tokenPayload = JSON.parse(atob(token.split('.')[1]))
      const userRole = tokenPayload.userRole
      const userId = tokenPayload.userId
      
      // 토큰 만료 확인
      const currentTime = Date.now() / 1000
      if (tokenPayload.exp > currentTime) {
        // 유효한 토큰이면 Store에 설정
        let userType = 'general'
        if (userRole === 'ROLE_INFLUENCER') {
          userType = 'influencer'
        } else if (userRole === 'ROLE_ADVERTISER') {
          userType = 'advertiser'
        }
        
        accountStore.setLoginInfo(token, { userId, userRole }, userType)
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
      } else {
        // 만료된 토큰이면 제거
        localStorage.removeItem('token')
        accountStore.clearAuth()
      }
    } catch (error) {
      console.error('Token parsing failed:', error)
      localStorage.removeItem('token')
      accountStore.clearAuth()
    }
  }
  
  // 로그인 상태 확인 완료
  accountStore.setChecked(true)
})

// 라우터 변경 감지
watch(
  () => router.currentRoute.value,
  () => {
    // 페이지 변경 시 스크롤을 맨 위로
    window.scrollTo(0, 0)
  }
)
</script>

<template>
  <div class="app">
    <Header :openSidebar="openSidebar" :toggleSidebar="toggleSidebar" />
    <Sidebar :openSidebar="openSidebar" :toggleSidebar="toggleSidebar" />
    <div class="main-container">
      <main class="content">
        <RouterView v-slot="{ Component }">
          <component :is="Component" :key="$route.fullPath" />
        </RouterView>
      </main>
    </div>
    <Footer />
    <Chatbot />
    <Alert />
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-container {
  flex: 1;
  width: 100%;
  min-height: 0;
  padding-top: 121px; /* Header(48) + Navbar(73) */
  padding-bottom: 60px; /* Footer 높이 */
  box-sizing: border-box;
}

.content {
  width: 100%;
  min-height: calc(100vh - 48px - 73px - 60px);
  overflow-y: auto;
  background-color: #f5f5f5;
}

@media (max-width: 768px) {
  .main-container {
    padding-top: 108px; /* Header(48) + Mobile Navbar(60) */
  }

  .content {
    min-height: calc(100vh - 48px - 60px - 60px);
  }
}
</style>
