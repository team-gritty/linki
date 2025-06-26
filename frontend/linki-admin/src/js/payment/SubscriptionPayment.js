import httpRequester from '@/libs/httpRequester.js'

export const getSubscriptionPaymentList = async (page, size) => {
  return await httpRequester.get('/v1/admin/api/subscriptions')
}

export const searchSubscriptionPayment = async (searchType, keyword) => {
  // 스프링 연동 시 사용할 코드
  return await httpRequester.post('/v1/admin/api/subscriptions/search', {
    searchType,
    keyword
  })

  // json-server 테스트용 코드
  // return await httpRequester.get(`/v1/admin/api/subscriptions/search/${searchType}?q=${keyword}`)
}

export async function exportExcel() {
  const res = await httpRequester.post('/v1/admin/api/subscriptions/exportExcel');
  const downloadUrl = res.data;
  window.open(downloadUrl);
}

// Keyset 페이지네이션 기본 조회 (userId를 커서로 사용)
export const getSubscriptionPaymentListWithKeyset = async (cursor = null, size = 10) => {
  const params = { size }
  if (cursor) {
    params.cursor = cursor
  }
  
  return await httpRequester.get('/v1/admin/api/subscriptions', { params })
}

// Keyset 페이지네이션 검색 (userId를 커서로 사용)
export const searchSubscriptionPaymentWithKeyset = async (searchType, keyword, cursor = null, size = 10) => {
  const requestBody = {
    searchType,
    keyword,
    cursor,
    size
  }
  
  return await httpRequester.post('/v1/admin/api/subscriptions/search', requestBody)
}