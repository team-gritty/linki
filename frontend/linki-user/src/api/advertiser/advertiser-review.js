//광고주가 사용하는 리뷰 관련 api

import axios from 'axios';


const BASE_URL = '';  

export const reviewApi = {
  // 내가 받은 (광고주) 리뷰 조회
  getReceivedReviews() {
    return axios.get(`${BASE_URL}/influencer-reviews`);
  },

  // 내가 준 (인플루언서에게) 리뷰 조회 
  getGivenReviews() {
    return axios.get(`${BASE_URL}/advertiser-reviews`);
  },

  // 인플루언서 대한 리뷰 작성하기
  submitAdvertiserReview(reviewData) {
    return axios.post(`${BASE_URL}/advertiser/influencer-reviews`, reviewData);
  }
};