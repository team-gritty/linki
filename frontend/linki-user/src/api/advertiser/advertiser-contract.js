import axios from 'axios';

const BASE_URL = 'http://localhost:3000'; // json-server URL

export const contractApi = {
  // 계약 목록 조회
  getMyContracts() {
    return axios.get(`${BASE_URL}/contracts`);
  },

  // 계약 상세 조회
  getContractDetail(contractId) {
    return axios.get(`${BASE_URL}/contracts/${contractId}`);
  },

  // 계약서 조회
  getContractDocument(contractId) {
    return axios.get(`${BASE_URL}/contracts/${contractId}/document`);
  },

  // 계약 서명
  signContract(contractId, signData) {
    return axios.post(`${BASE_URL}/contracts/${contractId}/sign`, signData);
  }
};