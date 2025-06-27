import httpRequester from '@/libs/httpRequester.js'

export const getAdminSignUpList = async () => {
  return await httpRequester.get('/v1/admin/api/adminSignUp')
}

export const searchAdminSignUp = async (searchType, keyword) => {
  // 스프링 연동 시 사용할 코드
  return await httpRequester.post('/v1/admin/api/adminSignUp/search', {
    searchType,
    keyword
  })

  // // json-server 테스트용 코드
  // return await httpRequester.get(`/v1/admin/api/adminSignUp/search/${searchType}?q=${keyword}`)
}

// Keyset 페이지네이션 기본 조회
export const getAdminSignUpListWithKeyset = async (cursor = null, size = 10) => {
  const params = { size }
  if (cursor) {
    params.cursor = cursor
  }
  
  return await httpRequester.get('/v1/admin/api/adminSignUp', { params })
}

// Keyset 페이지네이션 검색
export const searchAdminSignUpWithKeyset = async (searchType, keyword, cursor = null, size = 10) => {
  const requestBody = {
    searchType,
    keyword,
    cursor,
    size
  }
  
  return await httpRequester.post('/v1/admin/api/adminSignUp/search', requestBody)
}

export async function exportExcel() {
  const res = await httpRequester.post('/v1/admin/api/adminSignUp/exportExcel');
  const downloadUrl = res.data;
  window.open(downloadUrl);
}

export const approveAdmin = async (adminSignUpId) => {
  return await httpRequester.post(`/v1/admin/api/adminSignUp/approve`, {
    adminSignUpId: adminSignUpId
  })
}

export const rejectAdmin = async (adminSignUpId) => {
  return await httpRequester.post(`/v1/admin/api/adminSignUp/reject`, {
    adminSignUpId: adminSignUpId
  })
}