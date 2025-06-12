<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { chatApi } from '@/api/chat'

const router = useRouter()
const showDropdown = ref(false)
const userChatList = ref([])
const dropdownRef = ref(null)

const sortedChatList = computed(() => {
  return [...userChatList.value].sort((a, b) => {
    // 먼저 읽지 않은 메시지 우선
    if (a.isNew !== b.isNew) {
      return b.isNew - a.isNew
    }
    // 그 다음 최신 메시지 순
    return new Date(b.lastMessageTime) - new Date(a.lastMessageTime)
  })
})

const hasNewMessages = computed(() => {
  return userChatList.value.some(chat => chat.isNew)
})

const loadUserChatList = async () => {
  try {
    const data = await chatApi.getUserChatList()
    userChatList.value = data
  } catch (error) {
    console.error('Failed to load user chat list:', error)
  }
}

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
  if (showDropdown.value) {
    loadUserChatList()
  }
}

const goToChat = (chat) => {
  router.push({
    path: `/proposal/${chat.proposalId}`,
    query: { tab: 'chat' }
  })
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

onMounted(() => {
  loadUserChatList()
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
        <div v-for="chat in sortedChatList"
             :key="chat.chatId"
             class="chat-item"
             @click="goToChat(chat)">
          <div class="chat-info">
            <div class="chat-name">{{ chat.chatPartner }}</div>
            <div class="chat-message">{{ chat.lastMessage }}</div>
          </div>
          <div class="chat-meta">
            <div class="chat-time">{{ formatTime(chat.lastMessageTime) }}</div>
            <div v-if="chat.isNew" class="new-badge">NEW</div>
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
  font-size: 1.2rem;
  cursor: pointer;
  padding: 8px;
  position: relative;
  transition: opacity 0.2s;
}

.chat-button:hover {
  opacity: 0.8;
}

.notification-badge {
  position: absolute;
  top: 0;
  right: 0;
  color: #FFD700;
  font-size: 1.5rem;
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
  width: 300px;
  max-height: 340px;
  overflow: hidden;
  z-index: 1000;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 15px;
  border-bottom: 1px solid rgba(238, 238, 238, 0.8);
  background: rgba(255, 255, 255, 0.98);
  position: sticky;
  top: 0;
  z-index: 1;
}

.chat-header h3 {
  margin: 0;
  font-size: 1rem;
  color: #333;
}

.chat-list {
  padding: 0;
  overflow-y: auto;
  max-height: calc(70px * 4); /* 정확히 4개의 채팅방 높이 */
  scrollbar-width: thin;
  scrollbar-color: #7B21E8 rgba(241, 241, 241, 0.5);
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
  padding: 12px 15px;
  border-bottom: 1px solid rgba(238, 238, 238, 0.8);
  cursor: pointer;
  transition: all 0.2s;
  height: 70px;
  box-sizing: border-box;
  background: transparent;
}

.chat-item:hover {
  background-color: rgba(248, 249, 250, 0.9);
}

.chat-info {
  flex: 1;
  min-width: 0;
}

.chat-name {
  font-weight: 600;
  margin-bottom: 5px;
  color: #333;
}

.chat-message {
  color: #666;
  font-size: 0.9rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chat-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 5px;
  min-width: 60px;
}

.chat-time {
  font-size: 0.8rem;
  color: #999;
}

.new-badge {
  font-size: 0.7rem;
  font-weight: 600;
  color: #7B21E8;
  background-color: #FFD700;
  padding: 2px 6px;
  border-radius: 8px;
}
</style> 