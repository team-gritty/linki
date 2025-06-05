<template>
  <aside class="sidebar-menu">
    <div class="sidebar-title">Advertiser <span class="divider">/</span> <span class="mypage">My Page</span></div>
    <nav class="sidebar-nav">
      <div class="sidebar-section">내 정보 관리
        <ul>
          <li>프로필 / 비밀번호 변경</li>
        </ul>
      </div>
      <div class="sidebar-section">광고 캠페인
        <ul>
          <li class="active">
            <router-link to="/mypage/campaign-register">캠페인 등록하기</router-link>
          </li>
          <li>
            <router-link to="/mypage/campaign-list">캠페인 목록</router-link>
          </li>
        </ul>
      </div>
      <div class="sidebar-section">계약서 관리
        <ul>
          <li>
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
// 추후 props, emits 등 확장 가능
</script>

<style scoped>
.sidebar-menu {
  width: 260px;
  padding: 48px 0 0 60px;
  background: #fff;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
}
.sidebar-title {
  color: #bdbdbd;
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 40px;
}
.sidebar-title .divider {
  margin: 0 8px;
}
.sidebar-title .mypage {
  color: #222;
  font-weight: 400;
}
.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 32px;
}
.sidebar-section {
  color: #222;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}
.sidebar-section ul {
  margin: 8px 0 0 0;
  padding: 0;
  list-style: none;
}
.sidebar-section li {
  color: #bdbdbd;
  font-size: 15px;
  font-weight: 400;
  margin-bottom: 6px;
  cursor: pointer;
  transition: color 0.2s;
}
.sidebar-section li.active,
.sidebar-section li:hover {
  color: #9B51E0;
  font-weight: 600;
}
.sidebar-section li.active > a,
.sidebar-section li > a {
  color: inherit;
  text-decoration: none;
}
</style> 