import httpClient from '@/utils/httpRequest'

export const getInfluencerReviewsList = async () => {
  return await httpClient.get('/v1/admin/api/influencerReviews')
}

export const searchInfluencerReviews = async (searchType, keyword) => {
  // 스프링 연동 시 사용할 코드
  return await httpClient.post('/v1/admin/api/influencerReviews/search', {
    searchType,
    keyword
  })

  // json-server 테스트용 코드
  // return await httpClient.get(`/v1/admin/api/influencerReviews/search/${searchType}?q=${keyword}`)
}

export async function exportExcel() {
  const res = await httpClient.post('/v1/admin/api/influencerReviews/exportExcel');
  const downloadUrl = res.data;
  window.open(downloadUrl);
}

export const toggleReviewVisibility = async (reviewId, visibility) => {
  return await httpClient.post('/v1/admin/api/influencerReviews/visibility', { 
    id: reviewId,
    visibility: visibility 
  })
}

// Keyset 페이지네이션 기본 조회
export const getInfluencerReviewsListWithKeyset = async (cursor = null, size = 10) => {
  const params = { size }
  if (cursor) {
    params.cursor = cursor
  }
  
  return await httpClient.get('/v1/admin/api/influencerReviews', params)
}

// Keyset 페이지네이션 검색
export const searchInfluencerReviewsWithKeyset = async (searchType, keyword, cursor = null, size = 10) => {
  const requestBody = {
    searchType,
    keyword,
    cursor,
    size
  }
  
  return await httpClient.post('/v1/admin/api/influencerReviews/search', requestBody)
}