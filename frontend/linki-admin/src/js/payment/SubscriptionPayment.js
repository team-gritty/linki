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
  return await httpRequester.post('/v1/admin/api/subscriptions/exportExcel')
}