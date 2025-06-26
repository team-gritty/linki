import httpClient from '@/utils/httpRequest';

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
  getUserChatList: async (userId = 'user1') => {
    try {
      const response = await httpClient.get(`/v1/chat-service/api/authuser/user-chat-list`)
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
  }
}
