import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { chatApi } from '@/api/chat'

export const useChatStore = defineStore('chat', () => {
  const chatList = ref([])
  const loading = ref(false)
  
  // 새로운 메시지가 있는지 여부
  const hasNewMessages = computed(() => {
    return chatList.value.some(chat => chat.new === true)
  })

  // 정렬된 채팅 목록
  const sortedChatList = computed(() => {
    return [...chatList.value].sort((a, b) => {
      // 먼저 읽지 않은 메시지 우선
      if (a.new !== b.new) {
        return b.new - a.new
      }
      // 그 다음 최신 메시지 순
      return new Date(b.lastMessageTime) - new Date(a.lastMessageTime)
    })
  })

  // 채팅 목록 로드
  const loadChatList = async () => {
    // 이미 로딩 중인 경우 중복 요청 방지
    if (loading.value) {
      return chatList.value
    }
    
    try {
      loading.value = true
      const data = await chatApi.getUserChatList()
      chatList.value = data || []
      return chatList.value
    } catch (error) {
      console.error('Failed to load chat list:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 특정 채팅의 읽음 상태 업데이트
  const markChatAsRead = (chatId) => {
    const chat = chatList.value.find(c => c.chatId === chatId)
    if (chat) {
      chat.new = false
    }
  }

  // 특정 채팅의 캠페인 ID로 읽음 상태 업데이트
  const markChatAsReadByCampaignId = (campaignId) => {
    const chat = chatList.value.find(c => c.campaignId === campaignId)
    if (chat) {
      chat.new = false
    }
  }

  // 특정 채팅의 제안 ID로 읽음 상태 업데이트
  const markChatAsReadByProposalId = (proposalId) => {
    const chat = chatList.value.find(c => c.proposalId === proposalId)
    if (chat) {
      chat.new = false
    }
  }

  // 강제 새로고침
  const refreshChatList = () => {
    return loadChatList()
  }

  // 캐시 무효화
  const invalidateCache = () => {
    // No cache management needed
  }

  // 전역 함수로 쉽게 접근할 수 있도록 (실시간 업데이트용)
  if (typeof window !== 'undefined' && !window.chatStoreInitialized) {
    // 전역 함수들 - 다른 컴포넌트에서 쉽게 사용할 수 있도록
    window.markChatAsRead = markChatAsRead
    window.markChatAsReadByCampaignId = markChatAsReadByCampaignId  
    window.markChatAsReadByProposalId = markChatAsReadByProposalId
    window.updateChatList = loadChatList
    
    window.chatStoreInitialized = true
  }

  return {
    chatList,
    loading,
    hasNewMessages,
    sortedChatList,
    loadChatList,
    markChatAsRead,
    markChatAsReadByCampaignId,
    markChatAsReadByProposalId,
    refreshChatList
  }
}) 