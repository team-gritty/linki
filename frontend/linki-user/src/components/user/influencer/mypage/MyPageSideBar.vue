<template>
  <div class="mypage-sidebar">
    <div class="sidebar-title">Influencer <span class="divider">/</span> <span class="mypage">My Page</span></div>
    <nav class="sidebar-nav">
      <div v-for="(menu, index) in menuItems" :key="index" class="sidebar-section">
        {{ menu.name }}
        <ul>
          <li v-for="(submenu, subIndex) in menu.children"
              :key="subIndex"
              :class="{ active: currentMenu === submenu.id }"
              @click="selectMenu(submenu.id)">
            {{ submenu.name }}
          </li>
        </ul>
      </div>
    </nav>
  </div>
</template>

<script>
export default {
  name: 'MyPageSideBar',
  props: {
    currentMenu: {
      type: String,
      required: true
    }
  },
  setup(props, { emit }) {
    const menuItems = [
      {
        id: 'profile',
        name: '내 정보',
        children: [
          { id: 'profile.basic', name: '기본 정보' },
          { id: 'profile.channel', name: '채널 등록' }
        ]
      },
      {
        id: 'campaign',
        name: '캠페인 관리',
        children: [
          { id: 'campaign.proposals', name: '제안서 조회' }
        ]
      },
      {
        id: 'review',
        name: '리뷰 관리',
        children: [
          { id: 'review.received', name: '받은 리뷰' },
          { id: 'review.written', name: '작성한 리뷰' }
        ]
      },
      {
        id: 'contract',
        name: '계약 관리',
        children: [
          { id: 'contract.ongoing', name: '진행중인 계약' },
          { id: 'contract.completed', name: '완료된 계약' },
          { id: 'contract.settlement', name: '정산 내역' }
        ]
      },
      {
        id: 'subscription',
        name: '구독 관리',
        children: [
          { id: 'subscription.apply', name: '구독 신청' },
          { id: 'subscription.manage', name: '나의 구독 관리' },
          { id: 'subscription.refund', name: '환불 신청' }
        ]
      }
    ];

    const selectMenu = (menuId) => {
      emit('update:currentMenu', menuId);
    };

    return {
      menuItems,
      selectMenu
    };
  }
};
</script>

<style>
@import '@/assets/css/mypage-sidebar.css';
</style> 