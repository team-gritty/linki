//광고주가 사용하는 리뷰 관련 api

import axios from 'axios';


const BASE_URL = '';  

export const reviewApi = {
  // 내가 받은(광고주) 리뷰 조회
  getReceivedReviews() {
    return axios.get(`/v1/api/advertiser/reviews/received`);
  },

  // 내가 쓴(인플루언서에게 남긴) 리뷰 조회
  getGivenReviews() {
    return axios.get(`/v1/api/advertiser/reviews/given`);
  },

  // 인플루언서에 대한 리뷰 작성 (작성한 리뷰 DB에 저장)
  writeInfluencerReview(reviewData) {
    return axios.post(`/v1/api/advertiser/reviews/given`, reviewData);
  }
};