import httpRequester from '@/libs/httpRequester'

export const getAdvertisersReviewsList = async () => {
  return await httpRequester.get('/v1/admin/api/advertisersReviews')
}

export const searchAdvertisersReviews = async (searchType, keyword) => {
  // 스프링 연동 시 사용할 코드
  // return await httpRequester.post('/v1/admin/api/advertisersReviews/search', {
  //   searchType,
  //   keyword
  // })

  // json-server 테스트용 코드
  return await httpRequester.get(`/v1/admin/api/advertisersReviews/search/${searchType}?q=${keyword}`)
}

export async function exportExcel() {
  return await httpRequester.post('/v1/admin/api/advertisersReviews/exportExcel')
}

export const toggleReviewVisibility = async (reviewId, visibility) => {
  return await httpRequester.post(`/v1/admin/api/advertisersReviews/${reviewId}`, { visibility })
}