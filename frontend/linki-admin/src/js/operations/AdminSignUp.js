import httpRequester from '@/libs/httpRequester.js'

export const getAdminSignUpList = async () => {
  return await httpRequester.get('/v1/admin/api/adminSignUp')
}

export const searchAdminSignUp = async (searchType, keyword) => {
  // 스프링 연동 시 사용할 코드
  // return await httpRequester.post('/v1/admin/api/adminSignUp/search', {
  //   searchType,
  //   keyword
  // })

  // json-server 테스트용 코드
  return await httpRequester.get(`/v1/admin/api/adminSignUp/search/${searchType}?q=${keyword}`)
}

export async function exportExcel() {
  return await httpRequester.post('/v1/admin/api/adminSignUp/exportExcel')
}

export const approveAdmin = async (adminId) => {
  return await httpRequester.post(`/v1/admin/api/adminSignUp/${adminId}/approve`)
}

export const rejectAdmin = async (adminId) => {
  return await httpRequester.post(`/v1/admin/api/adminSignUp/${adminId}/reject`)
}