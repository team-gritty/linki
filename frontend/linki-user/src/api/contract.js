import axios from 'axios';

const BASE_URL = 'http://localhost:3000';

export const contractApi = {
  // 계약 목록 조회
  getMyContracts() {
    return axios.get(`${BASE_URL}/influencer-contracts`);
  },

  // 계약 상세 조회
  getContractDetail(contractId) {
    return axios.get(`${BASE_URL}/influencer-contracts/${contractId}`);
  },

  // 계약 서명
  signContract(contractId, signData) {
    return axios.post(`${BASE_URL}/influencer-contracts/${contractId}/sign`, signData);
  },

  // 정산 내역 조회
  getSettlements() {
    return axios.get(`${BASE_URL}/settlements`);
  }
}; 