<template>
  <aside class="sidebar-menu">
    <div class="sidebar-title">Admin <span class="divider">/</span> <span class="mypage">My Page</span></div>
    <nav class="sidebar-nav">
      <div class="sidebar-section">내 정보 관리
        <ul>
          <li>
            <router-link to="/mypage" exact-active-class="active">프로필</router-link>
          </li>
          <li>
            <router-link to="/mypage/password" exact-active-class="active">비밀번호 변경</router-link>
          </li>
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
@media (max-width: 900px) {
  .sidebar-menu {
    width: 100%;
    padding: 24px 0 0 0;
    border-radius: 0;
    box-shadow: none;
    min-width: unset;
  }
}
</style> 