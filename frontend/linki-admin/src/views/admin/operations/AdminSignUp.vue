<template>
  <div class="member-list-view">
    <h2 class="page-title">관리자 가입 신청 조회</h2>
    <!-- <SearchBar /> -->
    <AdminSignUpTable />
  </div>
</template>

<script setup>
import AdminSignUpTable from '@/components/table/operations/AdminSignUpTable.vue'
</script>


<style scoped>
.member-list-view {
  max-width: 1200px;
  margin: 60px auto 0 auto;
  padding: 0 32px 40px 32px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.page-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 32px;
  align-self: flex-start;
}
@media screen and (max-width: 768px) {
  .page-title {
    text-align: center;
    align-self: center;
    width: 100%;
  }
}
.member-table {
  width: 100%;
  max-width: none;
  border-collapse: separate;
  border-spacing: 0;
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0,0,0,0.06);
  margin-bottom: 32px;
  font-family: 'Pretendard', 'Noto Sans KR', Arial, sans-serif;
}
</style> 