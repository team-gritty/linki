import httpClient from '@/utils/httpRequest'

export const proposalAPI = {

  /**
   * 특정 캠페인의 제안서 목록 조회 (광고주용)
   * @param campaignId
   * @returns {Promise<*>}
   */
  getProposalsByCampaign: async (campaignId) => {
    try {
      const response = await httpClient.get(`/v1/api/advertiser/proposals?campaignId=${campaignId}`);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch proposals by campaign:', error);
      throw error;
    }
  },

  // 캠페인에 속한 제안서들 중 특정 제안서 상세 조회
  getProposalDetail: async (proposalId, campaignId) => {
    try {
      const response = await httpClient.get(`/v1/api/advertiser/mypage/campaigns/{campaignId}/proposals/{proposalId}`);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch proposal detail:', error);
      throw error;
    }
  },

  // 광고주 - 제안서 수정 (승낙한 다음만 가능)
  updateProposal: async (proposalId, proposalData) => {
    try {
      const response = await httpClient.put(`/v1/api/advertiser/proposals/${proposalId}`, proposalData);
      return response.data;
    } catch (error) {
      console.error('Failed to update proposal:', error);
      throw error;
    }
  },

} 