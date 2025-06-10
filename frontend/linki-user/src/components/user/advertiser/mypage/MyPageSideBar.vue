<template>
  <div class="mypage-sidebar">
    <div class="sidebar-title">Advertiser<span class="divider">/</span> <span class="mypage">My Page</span></div>
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

<script setup>
import { defineProps, defineEmits } from 'vue'
import axios from 'axios'
const props = defineProps({
  currentMenu: {
    type: String,
    required: true
  }
})
const emit = defineEmits(['update:currentMenu', 'contractsLoaded'])

const menuItems = [
  {
    id: 'profile',
    name: '내 정보 관리',
    children: [
      { id: 'profile.basic', name: '기본 정보' }
    ]
  },
  {
    id: 'campaign',
    name: '캠페인 관리',
    children: [
      { id: 'campaign.list', name: '캠페인 목록' },
      { id: 'campaign.register', name: '캠페인 등록' }
    ]
  },
  {
    id: 'contract',
    name: '계약서 관리',
    children: [
      { id: 'contract.list', name: '계약서 목록' },
      { id: 'contract.ongoing', name: '진행중인 계약' },
      { id: 'contract.completed', name: '완료된 계약' }
    ]
  },
  {
    id: 'review',
    name: '리뷰 관리',
    children: [
      { id: 'review.written', name: '작성한 리뷰' },
      { id: 'review.received', name: '받은 리뷰' }
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
]

async function selectMenu(menuId) {
  emit('update:currentMenu', menuId)
  if (menuId === 'contract.list') {
    try {
      const response = await axios.get('/contracts')
      emit('contractsLoaded', response.data)
    } catch (error) {
      console.error('계약서 목록 불러오기 실패:', error)
      emit('contractsLoaded', [])
    }
  }
}
</script>

<style>
  @import '@/assets/css/mypage.css';
</style> 