import httpClient from '@/utils/httpRequest'

export const settlementAPI = {
  // 특정 계약의 정산 내역 조회
  async getSettlements(contractId) {
    try {
      const response = await httpClient.get(`/v1/api/influencer/contracts/${contractId}/settlements`);
      return response.data;
    } catch (error) {
      console.error('Error fetching settlements:', error);
      throw error;
    }
  },

  // 전체 정산 내역 조회
  async getAllSettlements() {
    try {
      const response = await httpClient.get(`/v1/api/influencer/settlements`);
      return response.data;
    } catch (error) {
      console.error('Error fetching all settlements:', error);
      throw error;
    }
  }
}
