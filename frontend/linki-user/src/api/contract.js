import httpClient from '@/utils/httpRequest'

const BASE_URL = ''; // vite proxy를 사용하므로 BASE_URL은 비워둡니다

export const contractApi = {
  // 계약 목록 조회 (상태별)
  async getMyContracts(statuses = ['ONGOING', 'COMPLETED', 'PENDING_SIGN']) {
    try {
      const statusParams = statuses.map(status => `status=${status}`).join('&');
      const response = await httpClient.get(`/v1/api/influencer/mypage/contracts?${statusParams}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching contracts:', error);
      throw error;
    }
  },

  // 계약 상세 조회
  async getContractDetail(contractId) {
    try {
      const response = await httpClient.get(`/v1/api/influencer/mypage/contracts/${contractId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching contract detail:', error);
      throw error;
    }
  },

  // 계약서 조회
  async getContractDocument(contractId) {
    try {
      const response = await httpClient.get(`/v1/api/influencer/contracts/${contractId}/document`);
      return response.data;
    } catch (error) {
      console.error('Error fetching contract document:', error);
      throw error;
    }
  },

  // 계약 서명
  async signContract(contractId) {
    try {
      const response = await httpClient.post(`/v1/api/influencer/contracts/${contractId}/sign`);
      return response.data;
    } catch (error) {
      console.error('Error signing contract:', error);
      throw error;
    }
  },


}; 