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

  // 제안서 상세 조회 (캠페인 상세 페이지용)
  getProposalDetail: async (proposalId, campaignId) => {
    try {
      const response = await httpClient.get(`/v1/api/advertiser/proposals/${proposalId}?campaignId=${campaignId}`);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch proposal detail:', error);
      throw error;
    }
  },

  /**
   * 제안서 승낙
   * @param proposalId 제안서 ID
   * @param campaignId 캠페인 ID
   * @returns {Promise<*>}
   */
  acceptProposal: async (proposalId, campaignId) => {
    try {
      const response = await httpClient.post(`/v1/api/advertiser/proposals/${proposalId}/accept?campaignId=${campaignId}`);
      return response.data;
    } catch (error) {
      console.error('Failed to accept proposal:', error);
      throw error;
    }
  },

  /**
   * 제안서 거절
   * @param proposalId 제안서 ID
   * @param campaignId 캠페인 ID
   * @returns {Promise<*>}
   */
  rejectProposal: async (proposalId, campaignId) => {
    try {
      const response = await httpClient.post(`/v1/api/advertiser/proposals/${proposalId}/reject?campaignId=${campaignId}`);
      return response.data;
    } catch (error) {
      console.error('Failed to reject proposal:', error);
      throw error;
    }
  },

  /**
   * 제안서 수정 (승낙한 제안서의 내용 수정)
   * @param proposalId 제안서 ID
   * @param contents 수정할 내용
   * @returns {Promise<*>}
   */
  updateProposal: async (proposalId, contents) => {
    try {
      const response = await httpClient.put(`/v1/api/advertiser/proposals/${proposalId}`, {
        contents: contents
      });
      return response.data;
    } catch (error) {
      console.error('Failed to update proposal:', error);
      throw error;
    }
  },

} 