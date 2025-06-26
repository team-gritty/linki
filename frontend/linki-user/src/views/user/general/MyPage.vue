<template>
  <div class="my-page">
    <MyPageSideBar v-model:currentMenu="currentMenu" />
      
    <div class="content-area">
      <!-- 내 정보 -->
      <MyPageBasicInfo v-if="currentMenu === 'profile.basic'" />
      <MyPageChannelChange v-if="currentMenu === 'profile.channel'" />
      <MyPagePassword v-if="currentMenu === 'profile.password'" />
    </div>
  </div>
</template>

<script>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAccountStore } from '@/stores/account'
import MyPageSideBar from '@/components/user/general/mypage/MyPageSideBar.vue'
import MyPageBasicInfo from '@/components/user/general/mypage/MyPageBasicInfo.vue'
import MyPageChannelChange from '@/components/user/general/mypage/MyPageChannelChange.vue'
import MyPagePassword from '@/components/user/general/mypage/MyPagePassword.vue'

export default {
  name: 'MyPage',
  components: {
    MyPageSideBar,
    MyPageBasicInfo,
    MyPageChannelChange,
    MyPagePassword
  },
  
  setup() {
    const route = useRoute()
    const router = useRouter()
    const accountStore = useAccountStore()
    const currentMenu = ref('profile.basic')
    
    const updateMenuFromQuery = () => {
      const queryMenu = route.query.currentMenu
      if (queryMenu) {
        currentMenu.value = queryMenu.toString()
      }
    }

    const checkUserRoleAndRedirect = () => {
      const userType = accountStore.getUserType
      
      if (userType === 'influencer') {
        router.replace('/mypage/influencer')
      } else if (userType === 'advertiser') {
        router.replace('/mypage/advertiser')
      }
      // 일반 사용자(ROLE_USER)인 경우에만 현재 페이지에 머무름
    }

    onMounted(() => {
      checkUserRoleAndRedirect()
      updateMenuFromQuery()
    })

    watch(() => route.query.currentMenu, (newVal) => {
      if (newVal) {
        currentMenu.value = newVal.toString()
      }
    })

    // code 파라미터 변경 감지 추가
    watch(() => route.query.code, (newVal) => {
      if (newVal) {
        // 코드가 있으면 채널 변경 탭으로 이동
        currentMenu.value = 'profile.channel'
      }
    })
    
    return {
      currentMenu
    }
  }
}
</script>

<style>
.my-page {
  display: flex;
  min-height: calc(100vh - 48px);
  margin-top: 48px;
  background: #f5f5f5;
}

.content-area {
  flex: 1;
  padding: 20px;
  background: white;
  margin: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
  .my-page {
    flex-direction: column;
  }
  
  .content-area {
    margin: 0;
    border-radius: 0;
  }
}
</style> 