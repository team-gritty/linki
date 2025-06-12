import httpClient from '@/utils/httpRequest';

export const chatApi = {
  // 채팅방 생성(비활성)
  createRoom: async (proposalId) => {
    try {
      const response = await httpClient.post(`/v1/api/chat-service/rooms/${proposalId}`)
      alert('채팅방이 생성되었습니다. 광고주의 승낙 후 채팅이 가능합니다.')
      return response.data;
    } catch (error) {
      console.error('Error creating chat room:', error)
      alert('채팅방 활성화에 실패했습니다.')
      throw error
    }
  },

  // 채팅방 활성화 요청
  activateRoom: async (proposalId) => {
    try {
      const response = await httpClient.post(`/v1/api/chat-service/rooms/activate/${proposalId}`)
      return response.data
    } catch (error) {
      console.error('Error activating chat room:', error)
      throw error
    }
  },

  // 광고주의 채팅방 목록 조회
  getChatList: async (campaignId) => {
    try {
      return await httpClient.get(`/v1/api/chat-service/list/${campaignId}`)
    } catch (error) {
      console.error('Error getting chat list:', error)
      throw error
    }
  },

  // 광고주의 채팅방 조회
  getChatDetails: async (chatId) => {
    try {
      return await httpClient.get(`/v1/api/chat-service/chat-detail/${chatId}`)
    } catch (error) {
      console.error('Error getting chat details:', error)
      throw error
    }
  },

  // 인플루언서의 채팅방 조회
  getChatRoom: async (proposalId) => {
    try {
      const response = await httpClient.get(`/v1/api/chat-service/room/${proposalId}`);
      return response.data;
    } catch (error) {
      console.error('Error getting chat room:', error);
      throw error;
    }
  },

  // 사용자별 채팅방 목록 조회
  getUserChatList: async (userId = 'user1') => {
    try {
      const response = await httpClient.get(`/v1/api/chat-service/user-chat-list/${userId}`)
      return response.data
    } catch (error) {
      console.error('Error getting user chat list:', error)
      throw error
    }
  },

  // 메시지 목록 조회
  getMessages: async (chatId) => {
    try {
      return await httpClient.get(`/v1/api/chat-service/messages/${chatId}`)
    } catch (error) {
      console.error('Error getting messages:', error)
      throw error
    }
  },

  // 제안서 거절
  rejectChat: async (proposalId) => {
    try {
      return await httpClient.post(`/v1/api/chat-service/proposals/${proposalId}/reject`)
    } catch (error) {
      console.error('Error rejecting proposal:', error)
      throw error
    }
  },

  // 메시지 전송(소켓이 아닌 프론트 용)
  sendMessage: async (messageData) => {
    try {
      return await httpClient.post(`/v1/api/chat-service/messages`, messageData)
    } catch (error) {
      console.error('Error sending message:', error)
      throw error
    }
  },

  // 제안서 상세 조회
  getProposal: async (proposalId) => {
    try {
      const response = await httpClient.get(`/v1/ap/chat-service/proposals/${proposalId}`)
      return {
        data: Array.isArray(response.data) ? response.data[0] : response.data
      }
    } catch (error) {
      console.error('Error getting proposal details:', error)
      throw error
    }
  }
}
