<template>
  <aside class="mypage-sidebar">
    <div class="sidebar-title">Advertiser<span class="divider">/</span> <span class="mypage">My Page</span></div>
    <nav class="sidebar-nav">
      <div class="sidebar-section">내 정보 관리
        <ul>
          <li :class="{ active: $route.name === 'advertiser-profile' }">
            <router-link to="/mypage/advertiser">기본 정보</router-link>
          </li>
        </ul>
      </div>
      <div class="sidebar-section">캠페인 관리
        <ul>
          <li :class="{ active: $route.name === 'advertiser-campaign-list' }">
            <router-link to="/mypage/campaign-list">캠페인 목록</router-link>
          </li>
          <li :class="{ active: $route.name === 'campaign-register' }">
            <router-link to="/mypage/campaign-register">캠페인 등록</router-link>
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

      <div class="sidebar-section">리뷰 관리
        <ul>
          <li :class="{ active: $route.name === 'WrittenReviews' }">
            <router-link to="/mypage/written-reviews">작성한 리뷰</router-link>
          </li>
          <li :class="{ active: $route.name === 'RecievedReviews' }">
            <router-link to="/mypage/received-reviews">받은 리뷰</router-link>
          </li>
        </ul>
      </div>

      <div class="sidebar-section">구독 관리
        <ul>
          <li>구독 신청</li>
          <li>나의 구독 관리</li>
          <li>환불 신청</li>
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
@import '@/assets/css/mypage-sidebar.css';
</style> 