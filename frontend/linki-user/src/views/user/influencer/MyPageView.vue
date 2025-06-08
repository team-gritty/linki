<template>
  <div class="my-page">
    <h1>My Page</h1>
    
    <div class="menu-container">
      <div class="menu-sidebar">
        <div v-for="(menu, index) in menuItems" :key="index" class="menu-group">
          <div 
            class="menu-parent"
            :class="{ active: currentMenu.startsWith(menu.id) }"
            @click="toggleMenu(menu.id)"
          >
            {{ menu.name }}
            <span class="arrow" :class="{ open: openMenus.includes(menu.id) }">▶</span>
          </div>
          
          <div v-if="menu.children" 
               class="submenu" 
               :class="{ open: openMenus.includes(menu.id) }">
            <div
              v-for="(submenu, subIndex) in menu.children"
              :key="subIndex"
              class="submenu-item"
              :class="{ active: currentMenu === submenu.id }"
              @click="selectMenu(submenu.id)"
            >
              {{ submenu.name }}
            </div>
          </div>
        </div>
      </div>
      
      <div class="content-area">
        <!-- 제안서 목록 -->
        <div v-if="currentMenu === 'campaign.proposals'" class="proposals-list">
          <h2>내 제안서 목록</h2>
          <div v-if="loading" class="loading">
            Loading...
          </div>
          <div v-else-if="error" class="error">
            {{ error }}
          </div>
          <div v-else-if="proposals.length === 0" class="empty-state">
            제출한 제안서가 없습니다.
          </div>
          <div v-else class="proposals-container">
            <div v-for="proposal in proposals" :key="proposal.proposal_id" class="proposal-item">
              <div class="proposal-header">
                <div class="campaign-info">
                  <img 
                    :src="proposal.campaign?.productImg" 
                    :alt="proposal.campaign?.productName"
                    class="campaign-img"
                  >
                  <div class="campaign-details">
                    <h3 class="campaign-name">{{ proposal.campaign?.productName || '캠페인 정보 없음' }}</h3>
                    <span class="submission-date">제출일: {{ formatDate(proposal.submitted_at) }}</span>
                  </div>
                </div>
                <div class="proposal-status">
                  <span :class="['status', proposal.status.toLowerCase()]">
                    {{ getStatusText(proposal.status) }}
                  </span>
                  <button class="detail-button" @click="viewDetail(proposal.proposal_id)">상세 조회</button>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 다른 메뉴 컨텐츠들은 여기에 추가 -->
        <div v-if="currentMenu === 'profile.basic'" class="profile-section">
          <h2>기본 정보</h2>
          <!-- 기본 정보 내용 -->
        </div>
        
        <div v-if="currentMenu === 'profile.channel'" class="channel-section">
          <h2>채널 정보</h2>
          <!-- 채널 정보 내용 -->
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

export default {
  name: 'MyPageView',
  
  setup() {
    const router = useRouter();
    const currentMenu = ref('campaign.proposals');
    const openMenus = ref(['campaign']); // 초기에 열려있을 메뉴
    const proposals = ref([]);
    const loading = ref(false);
    const error = ref(null);
    
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
    
    const getStatusText = (status) => {
      const statusMap = {
        'PENDING': '검토중',
        'ACCEPTED': '수락됨',
        'REJECTED': '거절됨'
      };
      return statusMap[status] || status;
    };
    
    const formatDate = (dateString) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    };
    
    const fetchProposals = async () => {
      loading.value = true;
      error.value = null;
      try {
        // 제안서 목록 조회
        const proposalRes = await axios.get('http://localhost:3000/proposals');
        const proposalList = proposalRes.data;
        
        // 각 제안서에 대한 캠페인 정보 가져오기
        const proposalsWithCampaign = await Promise.all(
          proposalList.map(async (proposal) => {
            try {
              const campaignRes = await axios.get(`http://localhost:3000/campaigns?productId=${proposal.product_id}`);
              const campaign = campaignRes.data[0];
              return {
                ...proposal,
                campaign
              };
            } catch (err) {
              console.error(`Failed to fetch campaign for proposal ${proposal.proposal_id}:`, err);
              return {
                ...proposal,
                campaign: null
              };
            }
          })
        );
        
        proposals.value = proposalsWithCampaign;
      } catch (err) {
        error.value = '제안서 목록을 불러오는데 실패했습니다.';
        console.error('Failed to fetch proposals:', err);
      } finally {
        loading.value = false;
      }
    };
    
    const toggleMenu = (menuId) => {
      const index = openMenus.value.indexOf(menuId);
      if (index === -1) {
        openMenus.value.push(menuId);
      } else {
        openMenus.value.splice(index, 1);
      }
    };
    
    const selectMenu = (menuId) => {
      currentMenu.value = menuId;
    };
    
    const viewDetail = (proposalId) => {
      router.push(`/proposal/${proposalId}`);
    };
    
    onMounted(() => {
      fetchProposals();
    });
    
    return {
      currentMenu,
      openMenus,
      menuItems,
      proposals,
      loading,
      error,
      getStatusText,
      formatDate,
      toggleMenu,
      selectMenu,
      viewDetail
    };
  }
};
</script>

<style scoped>
.my-page {
  padding: 20px;
}

.menu-container {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}

.menu-sidebar {
  width: 240px;
  background: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
}

.menu-group {
  margin-bottom: 8px;
}

.menu-parent {
  padding: 12px;
  cursor: pointer;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s ease;
  font-weight: 500;
}

.menu-parent:hover {
  background: #e0e0e0;
}

.menu-parent.active {
  background: #6200ea;
  color: white;
}

.arrow {
  font-size: 12px;
  transition: transform 0.3s ease;
}

.arrow.open {
  transform: rotate(90deg);
}

.submenu {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease;
}

.submenu.open {
  max-height: 500px;
}

.submenu-item {
  padding: 10px 12px 10px 24px;
  cursor: pointer;
  border-radius: 4px;
  margin: 4px 0;
  transition: all 0.3s ease;
  font-size: 0.95em;
}

.submenu-item:hover {
  background: #e0e0e0;
}

.submenu-item.active {
  background: #ede7f6;
  color: #6200ea;
}

.content-area {
  flex: 1;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.proposals-list {
  padding: 20px;
}

.proposals-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 20px;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #666;
  background: #f5f5f5;
  border-radius: 8px;
  margin-top: 20px;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #666;
}

.error {
  color: #c62828;
  padding: 20px;
  background: #ffebee;
  border-radius: 8px;
  margin-top: 20px;
}

.proposal-item {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  background: white;
  transition: transform 0.2s ease;
}

.proposal-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.proposal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.campaign-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.campaign-img {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
}

.campaign-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.campaign-name {
  font-size: 1.1rem;
  font-weight: 500;
  color: #333;
  margin: 0;
}

.submission-date {
  font-size: 0.9rem;
  color: #666;
}

.proposal-status {
  display: flex;
  align-items: center;
  gap: 16px;
}

.status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.9em;
  font-weight: 500;
}

.status.pending {
  background: #fff3e0;
  color: #e65100;
}

.status.accepted {
  background: #e8f5e9;
  color: #2e7d32;
}

.status.rejected {
  background: #ffebee;
  color: #c62828;
}

.detail-button {
  padding: 6px 12px;
  background-color: #6200ea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.3s ease;
}

.detail-button:hover {
  background-color: #5000d6;
}
</style> 