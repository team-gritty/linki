<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAccountStore } from '@/stores/account'
import { useChatStore } from '@/stores/chat'

const router = useRouter()
const accountStore = useAccountStore()
const chatStore = useChatStore()
const showDropdown = ref(false)
const dropdownRef = ref(null)
const error = ref(null)

const currentUserId = computed(() => {
  return accountStore.getUser?.userId || accountStore.getUser?.id || null
})

const userType = computed(() => accountStore.getUserType)

// chat store의 정렬된 채팅 목록 사용
const sortedChatList = computed(() => chatStore.sortedChatList)
// 서버 상태 기반 새 메시지 여부 계산
const hasNewMessages = computed(() => {
  return sortedChatList.value.some(chat => shouldShowNewBadge(chat))
})
const loading = computed(() => chatStore.loading)

// 서버 상태 기반 NEW 배지 표시
const shouldShowNewBadge = (chat) => {
  if (chat.chatStatus === 'DELETE') {
    return false
  }
  
  // 서버의 new 상태 사용
  return chat.new === true
}

const loadUserChatList = async () => {
  try {
    error.value = null
    
    if (!currentUserId.value) {
      console.warn('User ID not available')
      error.value = '로그인 후 이용해주세요.'
      return
    }
    
    console.log('Loading chat list for user:', currentUserId.value)
    await chatStore.loadChatList()
  } catch (err) {
    console.error('Failed to load user chat list:', err)
    error.value = '채팅 목록을 불러오는데 실패했습니다.'
  }
}

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
  if (showDropdown.value) {
    loadUserChatList() // 드롭다운 열 때는 새로고침
  }
}

const goToChat = (chat) => {
  if (userType.value === 'influencer') {
    // 현재 같은 제안서 페이지에 있으면 강제로 채팅 탭으로 이동
    const currentPath = router.currentRoute.value.path
    if (currentPath === `/proposal/${chat.proposalId}`) {
      // 같은 페이지에서 탭만 변경하는 경우 replace 사용
      router.replace({
        path: `/proposal/${chat.proposalId}`,
        query: { tab: 'chat' }
      })
    } else {
      router.push({
        path: `/proposal/${chat.proposalId}`,
        query: { tab: 'chat' }
      })
    }
  } else if (userType.value === 'advertiser') {
    router.push({
      path: `/mypage/campaign-detail/${chat.campaignId}`,
      query: { tab: 'chat', chatId: chat.chatId }
    })
  } else {
    // 기본 동작 또는 오류 처리
    console.warn('Unknown user type:', userType.value)
  }
  showDropdown.value = false
}

const formatTime = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  
  if (date.getFullYear() !== now.getFullYear()) {
    const formatted = date.toLocaleDateString('ko-KR', { 
      year: 'numeric',
      month: 'numeric',
      day: 'numeric'
    })
    return formatted.replace(/\.$/, '')
  }

  const diffMs = now - date
  const diffSec = Math.floor(diffMs / 1000)
  const diffMin = Math.floor(diffSec / 60)
  const diffHour = Math.floor(diffMin / 60)
  const diffDay = Math.floor(diffHour / 24)

  if (diffDay > 30) {
    const formatted = date.toLocaleDateString('ko-KR', { 
      month: 'numeric',
      day: 'numeric'
    })
    return formatted.replace(/\.$/, '')
  }

  if (diffSec < 10) {
    return 'now'
  } else if (diffMin < 60) {
    return `${diffMin}m`
  } else if (diffHour < 24) {
    return `${diffHour}H`
  } else {
    return `${diffDay}d`
  }
}

const handleClickOutside = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    showDropdown.value = false
  }
}

// 사용자 ID가 변경될 때 (로그인할 때) 채팅 목록 로드
watch(currentUserId, (newUserId, oldUserId) => {
  if (newUserId && newUserId !== oldUserId) {
    console.log('User logged in, loading chat list:', newUserId)
    loadUserChatList() // 로그인 시 새로고침
  }
}, { immediate: false })

onMounted(() => {
  loadUserChatList() // 최초 로드시 새로고침
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<template>
  <div class="chat-dropdown" ref="dropdownRef">
    <button class="chat-button" @click.stop="toggleDropdown">
      <i class="fas fa-comments"></i>
      <span class="notification-badge" v-if="hasNewMessages">•</span>
    </button>
    <div class="chat-dropdown-content" v-show="showDropdown">
      <div class="chat-header">
        <h3>채팅 목록</h3>
      </div>
      <div class="chat-list">
        <div v-if="loading" class="loading">
          <p>채팅 목록을 불러오는 중...</p>
        </div>
        <div v-else-if="error" class="error-message">
          <p>{{ error }}</p>
        </div>
        <div v-else-if="sortedChatList.length === 0" class="empty-chat">
          <p>채팅 목록이 존재하지 않습니다</p>
        </div>
        <div v-else v-for="chat in sortedChatList" 
             :key="chat.chatId" 
             :class="['chat-item', { 'deleted-chat': chat.chatStatus === 'DELETE' }]" 
             @click="goToChat(chat)">
          <div class="chat-info">
            <div class="chat-name">
              {{ chat.opponentName || '알 수 없는 사용자' }}
              <span v-if="chat.chatStatus === 'DELETE'" class="rejected-badge">거절됨</span>
            </div>
            <div class="chat-message">{{ chat.lastMessage || '메시지가 없습니다' }}</div>
          </div>
          <div class="chat-meta">
            <div class="chat-time">{{ formatTime(chat.lastMessageTime || new Date()) }}</div>
            <div v-if="shouldShowNewBadge(chat)" class="new-badge">NEW</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chat-dropdown {
  position: relative;
  margin-left: 8px;
}

.chat-button {
  background: none;
  border: none;
  color: #7B21E8;
  font-size: 1.7rem;
  cursor: pointer;
  padding: 0;
  position: relative;
  transition: opacity 0.2s;
}

.chat-button:hover {
  opacity: 0.8;
}

.notification-badge {
  position: absolute;
  top: -15px;
  right: -5px;
  color: #FFD700;
  font-size: 2rem;
  text-shadow: 0 0 2px rgba(0,0,0,0.2);
}

.chat-dropdown-content {
  position: absolute;
  top: 100%;
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(5px);
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  width: 400px;
  max-height: 500px;
  overflow: hidden;
  z-index: 1000;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 20px;
  border-bottom: 1px solid rgba(238, 238, 238, 0.8);
  background: rgba(255, 255, 255, 0.98);
  position: sticky;
  top: 0;
  z-index: 1;
}

.chat-header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #333;
}

.chat-list {
  padding: 0;
  overflow-y: auto;
  max-height: calc(90px * 5);
  scrollbar-width: thin;
  scrollbar-color: #7B21E8 rgba(241, 241, 241, 0.5);
  
  width: 100%;
}

.chat-list::-webkit-scrollbar {
  width: 6px;
}

.chat-list::-webkit-scrollbar-track {
  background: rgba(241, 241, 241, 0.5);
  border-radius: 3px;
}

.chat-list::-webkit-scrollbar-thumb {
  background: rgba(123, 33, 232, 0.8);
  border-radius: 3px;
}

.chat-item {
  display: flex;
  padding: 18px 22px;
  border-bottom: 1px solid rgba(238, 238, 238, 0.8);
  cursor: pointer;
  transition: all 0.2s;
  height: 90px;
  box-sizing: border-box;
  background: transparent;
  font-size: 1.1rem;
  width: 100%;
}

.chat-item:hover {
  background-color: rgba(248, 249, 250, 0.9);
}

.chat-info {
  flex: 1;
  min-width: 0;
}

.chat-name {
  font-weight: 700;
  margin-bottom: 7px;
  color: #333;
  font-size: 1.15rem;
}

.chat-message {
  color: #666;
  font-size: 1rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chat-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 7px;
  min-width: 70px;
}

.chat-time {
  font-size: 0.95rem;
  color: #999;
}

.new-badge {
  font-size: 0.85rem;
  font-weight: 700;
  color: #7B21E8;
  background-color: #FFD700;
  padding: 3px 10px;
  border-radius: 10px;
}

.empty-chat {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 250px;
  color: #999;
  font-size: 1.1rem;
  text-align: center;
  padding: 30px;
}

.empty-chat p {
  margin: 0;
  line-height: 1.5;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 250px;
  color: #666;
  font-size: 1.1rem;
  text-align: center;
  padding: 30px;
}

.loading p {
  margin: 0;
  line-height: 1.5;
}

.error-message {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 250px;
  color: #dc3545;
  font-size: 1.1rem;
  text-align: center;
  padding: 30px;
}

.error-message p {
  margin: 0;
  line-height: 1.5;
}

/* 삭제된 채팅방 스타일 */
.deleted-chat {
  opacity: 0.6;
  background-color: rgba(248, 249, 250, 0.7);
}

.deleted-chat:hover {
  background-color: rgba(233, 236, 239, 0.8);
}

.rejected-badge {
  background: #dc3545;
  color: white;
  padding: 2px 6px;
  border-radius: 8px;
  font-size: 0.7rem;
  font-weight: 600;
  margin-left: 8px;
}

.deleted-chat .chat-name {
  color: #6c757d;
}

.deleted-chat .chat-message {
  color: #adb5bd;
}

.deleted-chat .chat-time {
  color: #adb5bd;
}
</style> 