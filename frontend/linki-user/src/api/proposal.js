import httpClient from '@/utils/httpRequest'

export const proposalAPI = {
  // 내 제안서 목록 조회
  getMyProposals: async (params = {}) => {
    try {
      const response = await httpClient.get('/v1/api/mypageinfluencer/proposals');
      
      // 응답 데이터가 배열인지 확인
      const proposals = Array.isArray(response.data) ? response.data : [];
      
      return {
        proposals,
        totalItems: parseInt(response.headers['x-total-count'] || '0')
      };
    } catch (error) {
      console.error('Failed to fetch proposals:', error);
      throw error;
    }
  },

  // 제안서 상세 조회
  getProposalDetail: async (proposalId) => {
    try {
      console.log('getProposalDetail called with proposalId:', proposalId);
      const url = `/v1/api/influencer/mypage/proposals/${proposalId}`;
      console.log('API URL:', url);
      const response = await httpClient.get(url);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch proposal detail:', error);
      throw error;
    }
  },

  // 제안서 수정
  updateProposal: async (proposalId, proposalData) => {
    try {
      const response = await httpClient.put(`/v1/api/influencer/mypage/proposals/${proposalId}`, proposalData);
      return response.data;
    } catch (error) {
      console.error('Failed to update proposal:', error);
      throw error;
    }
  },

  // 제안서 삭제
  deleteProposal: async (proposalId) => {
    try {
      const response = await httpClient.delete(`/v1/api/influencer/mypage/proposals/${proposalId}`);
      return response.data;
    } catch (error) {
      console.error('Failed to delete proposal:', error);
      throw error;
    }
  },



  // 캠페인별 제안서 목록 조회
  // getCampaignProposals: async (campaignId, params = {}) => {
  //   try {
  //     const response = await httpClient.get(`/v1/api/influencer/campaigns/${campaignId}/proposals`, {
  //       params: {
  //         _page: params._page || 1,
  //         _limit: params._limit || 10,
  //         _sort: params._sort || 'createdAt',
  //         _order: params._order || 'desc'
  //       }
  //     });
  //     return {
  //       proposals: response.data,
  //       totalItems: parseInt(response.headers['x-total-count'] || '0')
  //     };
  //   } catch (error) {
  //     console.error('Failed to fetch campaign proposals:', error);
  //     return {
  //       proposals: [],
  //       totalItems: 0
  //     };
  //   }
  // },
   // 제안서 제출
   submitProposal: async (campaignId, contents) => {
    try {
      const url = `/v1/api/influencer/mypage/campaigns/${campaignId}/proposals`
      const payload = {
        contents: contents,
        campaignId: String(campaignId)
      }
      
      console.log('=== 제안서 제출 API 호출 ===')
      console.log('URL:', url)
      console.log('Payload:', payload)
      console.log('CampaignId type:', typeof campaignId, campaignId)
      console.log('Contents length:', contents.length)
      
      const response = await httpClient.post(url, payload)
      
      console.log('제안서 제출 성공:', response.data)
      return response.data
    } catch (error) {
      console.error('=== 제안서 제출 에러 ===')
      console.error('Error:', error)
      console.error('Response status:', error.response?.status)
      console.error('Response data:', error.response?.data)
      console.error('Response headers:', error.response?.headers)
      throw error
    }
  },
} 