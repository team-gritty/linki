import axios from 'axios';

const BASE_URL = ''; // vite proxy를 사용하므로 BASE_URL은 비워둡니다

export const contractApi = {
  // 계약 목록 조회
  getMyContracts() {
    return axios.get(`/v1/api/contracts`);
  },

  // 계약 상세 조회
  getContractDetail(contractId) {
    return axios.get(`/v1/api/contracts/${contractId}`);
  },

  // 계약서 조회
  getContractDocument(contractId) {
    return axios.get(`/v1/api/contracts/${contractId}/document`);
  },

  // 계약 서명
  signContract(contractId, signData) {
    return axios.post(`/v1/api/contracts/${contractId}/sign`, signData);
  },

  // 특정 계약의 정산 내역 조회
  getSettlements(contractId) {
    return axios.get(`/v1/api/contracts/${contractId}/settlements`);
  },

  // 전체 정산 내역 조회
  getAllSettlements() {
    return axios.get(`/v1/api/settlements`);
  }
}; 