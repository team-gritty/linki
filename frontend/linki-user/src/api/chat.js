import httpClient from '@/utils/httpRequest';
import { useAccountStore } from '@/stores/account';

export const chatApi = {
  // 채팅방 생성(비활성)
  createRoom: async (proposalId) => {
  try {
    const response = await httpClient.post(
        `/v1/chat-service/api/influencer/rooms/${proposalId}`);
    return response.data;
  } catch (error) {
    console.error('Error creating chat room:', error);
    alert('채팅방 생성에 실패했습니다.');
    throw error;
  }
},

  // 채팅방 활성화 요청
  activateRoom: async (proposalId) => {
    try {
      const response = await httpClient.post(`/v1/chat-service/api/advertiser/rooms/activate/${proposalId}`)
      return response.data
    } catch (error) {
      console.error('Error activating chat room:', error)
      throw error
    }
  },

  // 광고주의 채팅방 목록 조회
  getChatList: async (campaignId) => {
    try {
      return await httpClient.get(`/v1/chat-service/api/advertiser/list/${campaignId}`)
    } catch (error) {
      console.error('Error getting chat list:', error)
      throw error
    }
  },

  // 광고주의 채팅방 조회
  getChatDetails: async (chatId) => {
    try {
      return await httpClient.get(`/v1/chat-service/api/advertiser/chat-detail/${chatId}`)
    } catch (error) {
      console.error('Error getting chat details:', error)
      throw error
    }
  },

  // 인플루언서의 채팅방 조회
  getChatRoom: async (proposalId) => {
    try {
      const response = await httpClient.get(`/v1/chat-service/api/influencer/room/${proposalId}`);
      return response.data;
    } catch (error) {
      console.error('Error getting chat room:', error);
      throw error;
    }
  },

  // 사용자별 채팅방 목록 조회
  getUserChatList: async () => {
    try {
      const response = await httpClient.get('/v1/chat-service/api/authuser/user-chat-list')
      return response.data
    } catch (error) {
      console.error('Error getting user chat list:', error)
      throw error
    }
  },

  // 메시지 목록 조회 (읽음 처리 포함)
  getMessages: async (chatId) => {
    try {
      const response = await httpClient.get(
          `/v1/chat-service/api/authuser/messages/${chatId}`);
      return response.data;
    } catch (error) {
      console.error('Error getting messages:', error)
      throw error
    }
  },

  // 메시지 목록 조회 (읽음 처리 없음)
  getMessagesWithoutRead: async (chatId) => {
    try {
      const response = await httpClient.get(
          `/v1/chat-service/api/authuser/messages/${chatId}/without-read`);
      return response.data;
    } catch (error) {
      console.error('Error getting messages without read:', error)
      throw error
    }
  },

  // 메시지 읽음 처리
  markMessagesAsRead: async (chatId) => {
    try {
      const response = await httpClient.post(
          `/v1/chat-service/api/authuser/messages/${chatId}/mark-read`);
      return response.data;
    } catch (error) {
      console.error('Error marking messages as read:', error)
      throw error
    }
  },

  // 제안서 거절
  rejectChat: async (proposalId) => {
    try {
      return await httpClient.post(`/v1/chat-service/api/advertiser/proposals/${proposalId}/reject`)
    } catch (error) {
      console.error('Error rejecting proposal:', error)
      throw error
    }
  },

  // 메시지 전송(소켓이 아닌 프론트 용)
  sendMessage: async (messageData) => {
    try {
      return await httpClient.post(`/v1/chat-service/api/messages`, messageData)
    } catch (error) {
      console.error('Error sending message:', error)
      throw error
    }
  },

  // 제안서 상세 조회
  getProposal: async (proposalId) => {
    try {
      const response = await httpClient.get(`/v1/chat-service/api/proposals/${proposalId}`)
      return {
        data: Array.isArray(response.data) ? response.data[0] : response.data
      }
    } catch (error) {
      console.error('Error getting proposal details:', error)
      throw error
    }
  },

  // SSE 연결
  connectSSE: (chatId, onMessage, onError, onOpen) => {
    try {
      // accountStore에서 JWT 토큰 가져오기
      const accountStore = useAccountStore()
      const token = accountStore.getAccessToken

      if (!token) {
        console.error('[SSE] 토큰이 없습니다!')
        throw new Error('Access token not found')
      }

      // 전역 SSE
      if (chatId !== 'global') {
        throw new Error('Only global SSE connections are supported. Use global SSE for all notifications.')
      }
      
      const sseUrl = `/v1/chat-service/api/sse/subscribe/user?token=${encodeURIComponent(token)}`
      const eventSource = new EventSource(sseUrl)
      
      eventSource.onopen = (event) => {
        console.log('[SSE] 연결 성공')
        if (onOpen) onOpen(event)
      }
      
      // 커스텀 이벤트 'NEW_MESSAGE' 리스너 추가
      eventSource.addEventListener('NEW_MESSAGE', (event) => {
        console.log('📨 [SSE] NEW_MESSAGE 수신:', event.data)
        if (onMessage) onMessage(event)
      })
      
      // 연결 확인 이벤트 'CONNECTED' 리스너 추가
      eventSource.addEventListener('CONNECTED', (event) => {
        console.log(' [SSE] CONNECTED 수신:', event.data)
      })
      
      // 기본 message 이벤트도 처리 (혹시 모를 다른 메시지)
      eventSource.onmessage = (event) => {
        console.log('[SSE] 기본 메시지 수신:', event.data)
        if (onMessage) onMessage(event)
      }
      
      eventSource.onerror = (error) => {
        console.error('❌ [SSE] 연결 에러:', error)
        if (onError) onError(error)
      }
      
      return eventSource
    } catch (error) {
      console.error('💥 [SSE] 연결 생성 실패:', error)
      throw error
    }
  },

  // SSE 연결 해제
  disconnectSSE: (eventSource) => {
    if (eventSource) {
      eventSource.close()
      console.log('SSE connection disconnected')
    }
  }
}
