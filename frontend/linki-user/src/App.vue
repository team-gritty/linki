<script setup>
import { RouterLink, RouterView } from 'vue-router'
import Header from './components/Header.vue'
import Sidebar from './components/Sidebar.vue'
import Footer from './components/Footer.vue'
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import Chatbot from '@/components/chatbot/Chatbot.vue'
import Alert from '@/components/common/Alert.vue'

const router = useRouter()
const openSidebar = ref(false)
const toggleSidebar = () => {
  openSidebar.value = !openSidebar.value
}

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
