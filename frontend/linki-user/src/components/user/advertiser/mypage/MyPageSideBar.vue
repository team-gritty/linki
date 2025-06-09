<template>
  <aside class="mypage-sidebar">
    <div class="sidebar-title">Advertiser <span class="divider">/</span> <span class="mypage">My Page</span></div>
    <nav class="sidebar-nav">
      <div class="sidebar-section">내 정보 관리
        <ul>
          <li :class="{ active: $route.name === 'advertiser-profile' }">
            <router-link to="/mypage/advertiser">프로필 / 비밀번호 변경</router-link>
          </li>
        </ul>
      </div>
      <div class="sidebar-section">광고 캠페인
        <ul>
          <li :class="{ active: $route.name === 'campaign-register' }">
            <router-link to="/mypage/campaign-register">캠페인 등록하기</router-link>
          </li>
          <li :class="{ active: $route.name === 'advertiser-campaign-list' }">
            <router-link to="/mypage/campaign-list">캠페인 목록</router-link>
          </li>
        </ul>
      </div>
      <div class="sidebar-section">계약서 관리
        <ul>
          <li :class="{ active: $route.name === 'ContractList' }">
            <router-link to="/mypage/contract-list">계약서 목록</router-link>
          </li>
        </ul>
      </div>
      <div class="sidebar-section">구독 관리
        <ul>
          <li>구독 상태 확인</li>
        </ul>
      </div>
    </nav>
  </aside>
</template>

<script setup>
import { useRoute } from 'vue-router'
const route = useRoute()
</script>

<style>
@import '@/css/mypage-sidebar.css';
</style> 