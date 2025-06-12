import httpClient from '@/utils/httpRequest'

const BASE_URL = ''; // vite proxy를 사용하므로 BASE_URL은 비워둡니다

export const contractApi = {
  // 계약 목록 조회
  async getMyContracts() {
    try {
      const response = await httpClient.get(`/v1/api/influencer/contracts`);
      return response.data;
    } catch (error) {
      console.error('Error fetching contracts:', error);
      throw error;
    }
  },

  // 계약 상세 조회
  async getContractDetail(contractId) {
    try {
      const response = await httpClient.get(`/v1/api/influencer/contracts/${contractId}`);
      // routes.json에 맞춰서 응답 처리
      return Array.isArray(response.data) ? response.data[0] : response.data;
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
}; 