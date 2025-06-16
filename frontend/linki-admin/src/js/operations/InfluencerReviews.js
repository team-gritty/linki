import httpRequester from '@/libs/httpRequester.js'

export const getInfluencerReviewsList = async () => {
  return await httpRequester.get('/v1/admin/api/influencerReviews')
}

export const searchInfluencerReviews = async (searchType, keyword) => {
  // 스프링 연동 시 사용할 코드
  return await httpRequester.post('/v1/admin/api/influencerReviews/search', {
    searchType,
    keyword
  })

  // json-server 테스트용 코드
  // return await httpRequester.get(`/v1/admin/api/influencerReviews/search/${searchType}?q=${keyword}`)
}

export async function exportExcel() {
  return await httpRequester.post('/v1/admin/api/influencerReviews/exportExcel')
}

export const toggleReviewVisibility = async (reviewId, visibility) => {
  return await httpRequester.post('/v1/admin/api/advertiserReviews/visibility', { 
    id: reviewId,
    visibility: visibility 
  })
}