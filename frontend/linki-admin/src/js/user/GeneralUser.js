import httpRequester from '@/libs/httpRequester.js'

export const getGeneralUserList = async (page, size) => {
  return await httpRequester.get('/v1/admin/api/generalUsers')
}

export const searchGeneralUser = async (searchType, keyword) => {
  // 스프링 연동 시 사용할 코드
  return await httpRequester.post('/v1/admin/api/generalUsers/search', {
    searchType,
    keyword
  })

  // json-server 테스트용 코드
  // return await httpRequester.get(`/v1/admin/api/generalUsers/search/${searchType}?q=${keyword}`)
}

export async function exportExcel() {
  const res = await httpRequester.post('/v1/admin/api/generalUsers/exportExcel');
  const downloadUrl = res.data;
  window.open(downloadUrl);
}

// Keyset 페이지네이션 기본 조회
export const getGeneralUserListWithKeyset = async (cursor = null, size = 10) => {
  const params = { size }
  if (cursor) {
    params.cursor = cursor
  }
  return await httpRequester.get('/v1/admin/api/generalUsers', { params })
}

// Keyset 페이지네이션 검색
export const searchGeneralUserWithKeyset = async (searchType, keyword, cursor = null, size = 10) => {
  const requestBody = {
    searchType,
    keyword,
    cursor,
    size
  }
  return await httpRequester.post('/v1/admin/api/generalUsers/search', requestBody)
}