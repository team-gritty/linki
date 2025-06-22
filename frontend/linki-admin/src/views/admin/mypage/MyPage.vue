<template>
  <div class="mypage-wrapper">
    <MyPageSidebar />
    <div class="mypage-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import MyPageSidebar from '@/components/MyPage/MyPageSidebar.vue'
</script>

<style scoped>
.mypage-wrapper {
  display: flex;
  flex-direction: row;
  min-height: 80vh;
  background: #fff;
  width: 100%;
}
.mypage-content {
  flex: 1;
  background: #fff;
  padding: 48px 40px 0 40px;
  min-height: 80vh;
  max-width: 600px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}
@media (max-width: 900px) {
  .mypage-wrapper {
    flex-direction: column;
  }
  .mypage-content {
    padding: 24px 8px 0 8px;
    max-width: 100%;
  }
}
</style>