<template>
  <div class="my-page">
    <MyPageSideBar v-model:currentMenu="currentMenu" />
      
    <div class="content-area">
      <!-- 내 정보 -->
      <MyPageBasicInfo v-if="currentMenu === 'profile.basic'" />
      <MyPagePassword v-if="currentMenu === 'profile.password'" />
    </div>
  </div>
</template>

<script>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAccountStore } from '@/stores/account'
import MyPageSideBar from '@/components/MyPage/MyPageSidebar.vue'
import MyPageBasicInfo from '@/components/MyPage/MyPageBasicInfo.vue'
import MyPagePassword from '@/components/MyPage/MyPagePassword.vue'

export default {
  name: 'MyPage',
  components: {
    MyPageSideBar,
    MyPageBasicInfo,
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

    onMounted(() => {
      updateMenuFromQuery()
    })

    watch(() => route.query.currentMenu, (newVal) => {
      if (newVal) {
        currentMenu.value = newVal.toString()
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