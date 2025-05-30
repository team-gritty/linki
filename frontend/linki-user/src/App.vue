<script setup>
import { RouterLink, RouterView } from 'vue-router'
import Header from './components/Header.vue'
import Sidebar from './components/Sidebar.vue'
import Footer from './components/Footer.vue'
import { ref } from 'vue'

const openSidebar = ref(false)
const toggleSidebar = () => {
  openSidebar.value = !openSidebar.value
}
</script>

<template>
  <div class="app">
    <Header :openSidebar="openSidebar" :toggleSidebar="toggleSidebar" />
    <Sidebar :openSidebar="openSidebar" :toggleSidebar="toggleSidebar" />
    <div class="main-container">
      
      <main class="content">
        <RouterView />
      </main>
    </div>
    <Footer />
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
  width: 100vw;
  min-height: 0;
  padding-top: 121px; /* Header(48) + Navbar(73) */
  padding-bottom: 60px; /* Footer 높이 */
  box-sizing: border-box;
}

.content {
  width: 100%;
  height: calc(100vh - 48px - 73px - 60px);
  overflow-y: auto;
  background-color: #f5f5f5;
}
</style>
