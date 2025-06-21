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
      const response = await httpClient.put(`/v1/api/influencer/proposals/${proposalId}`, proposalData);
      return response.data;
    } catch (error) {
      console.error('Failed to update proposal:', error);
      throw error;
    }
  },

  // 제안서 삭제
  deleteProposal: async (proposalId) => {
    try {
      const response = await httpClient.delete(`/v1/api/influencer/proposals/${proposalId}`);
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
      const response = await httpClient.post(`/v1/api/influencer/campaigns/${campaignId}/proposals`, {
        contents: contents,
        campaign_id: String(campaignId),
        status: 'PENDING',
        submitted_at: new Date().toISOString(),
        influencer_id: 'INF001' // TODO: 실제 로그인한 사용자 ID로 대체
      })
      return response.data
    } catch (error) {
      console.error('Error submitting proposal:', error)
      throw error
    }
  },
} 