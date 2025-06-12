import httpClient from '@/utils/httpRequest'

export const proposalAPI = {
  // 광고주가 받은 제안서 목록 조회
  getMyProposals: async (params = {}) => {
    try {
      const response = await httpClient.get('/v1/api/influencer/proposals', {
        params: {
          _page: params._page || 1,
          _limit: params._limit || 10
        }
      });
      
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
      const response = await httpClient.get(`/v1/api/influencer/proposals/${proposalId}`);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch proposal detail:', error);
      throw error;
    }
  },

  // 광고주가 제안서 승낙한다음 제안서 수정
  updateProposal: async (proposalId, proposalData) => {
    try {
      const response = await httpClient.put(`/v1/api/advertiser/proposals/${proposalId}`, proposalData);
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
  getCampaignProposals: async (campaignId, params = {}) => {
    try {
      const response = await httpClient.get(`/v1/api/influencer/campaigns/${campaignId}/proposals`, {
        params: {
          _page: params._page || 1,
          _limit: params._limit || 10,
          _sort: params._sort || 'createdAt',
          _order: params._order || 'desc'
        }
      });
      return {
        proposals: response.data,
        totalItems: parseInt(response.headers['x-total-count'] || '0')
      };
    } catch (error) {
      console.error('Failed to fetch campaign proposals:', error);
      return {
        proposals: [],
        totalItems: 0
      };
    }
  }
} 