import { defineStore } from 'pinia'

/**
 * JWT 토큰에서 페이로드를 추출하는 함수
 * - JWT는 header.payload.signature 구조로 되어 있음
 * - payload 부분에 사용자 정보가 Base64로 인코딩되어 저장됨
 * - channelAccess.js에서 사용자 역할을 확인하기 위해 필요함
 */
function parseJwtPayload(token) {
  try {
    if (!token) return null
    
    // JWT 토큰을 '.'으로 분리 (header.payload.signature)
    const parts = token.split('.')
    if (parts.length !== 3) return null
    
    // payload 부분을 Base64 디코딩하여 JSON 파싱
    const payload = JSON.parse(atob(parts[1]))
    return payload
  } catch (error) {
    console.error('JWT 토큰 파싱 실패:', error)
    return null
  }
}

export const useAccountStore = defineStore('account', {
    // persist: true - localStorage에 상태를 자동 저장
    // 페이지 새로고침 시에도 로그인 상태 유지를 위해 필요
    persist: true,
    
    state: () => ({
        // JWT 액세스 토큰 저장
        // API 호출 시 Authorization 헤더에 포함하기 위해 필요
        accessToken: null,
        
        // 사용자 정보 객체 (JWT에서 추출된 정보)
        // channelAccess.js에서 userId와 userRole을 참조하기 위해 필요
        user: null, // { userId, userRole, userName, userEmail, exp, iat }
        
        // 추가 사용자 타입 정보 (필요한 경우)
        // 현재는 JWT의 userRole과 중복될 수 있지만, 확장성을 위해 유지
        userType: null,
        
        // 인증 상태 확인 여부
        // 앱 초기화 시 토큰 유효성 검사가 완료되었는지 추적하기 위해 필요
        checked: false,
        
        // 로그인 성공 여부
        // accessToken만으로는 토큰의 유효성을 보장할 수 없으므로 별도 플래그 필요
        loggedIn: false
    }),

    getters: {
        // 완전한 로그인 상태 확인
        // channelAccess.js에서 접근 제한 로직에서 사용
        isLoggedIn: (state) => !!state.accessToken && state.loggedIn && !!state.user,
        
        // 사용자 정보 반환
        // 다른 컴포넌트에서 사용자 정보 접근 시 사용
        getUser: (state) => state.user,
        
        // 액세스 토큰 반환
        // API 클라이언트에서 Authorization 헤더 설정 시 사용
        getAccessToken: (state) => state.accessToken,
        
        // 사용자 타입 반환 (확장성을 위해 유지)
        getUserType: (state) => state.userType,
        
        // 인증 상태 확인 완료 여부
        // 앱 초기화 시 로딩 상태 관리를 위해 필요
        isChecked: (state) => state.checked,
        
        /**
         * 사용자 역할별 확인 헬퍼 메서드들
         * channelAccess.js에서 접근 제어 로직에 사용됨
         * 각 역할별로 다른 접근 권한을 부여하기 위해 필요
         */
        
        // 일반회원 확인 (5회 제한 대상)
        // channelAccess.js에서 접근 제한 로직의 핵심 조건
        isRegularUser: (state) => state.user?.userRole === 'ROLE_USER',
        
        // 인플루언서 확인 (무제한 접근)
        // 채널 상세 분석에 제한 없이 접근 가능
        isInfluencer: (state) => state.user?.userRole === 'ROLE_INFLUENCER',
        
        // 광고주 확인 (무제한 접근)
        // 채널 상세 분석에 제한 없이 접근 가능
        isAdvertiser: (state) => state.user?.userRole === 'ROLE_ADVERTISER',
        
        // 관리자 확인 (모든 권한)
        // 향후 관리자 기능 구현 시 사용
        isAdmin: (state) => state.user?.userRole === 'ROLE_ADMIN'
    },

    actions: {
        /**
         * 액세스 토큰 설정 및 사용자 정보 자동 추출
         * - 로그인 성공 시 호출되는 핵심 메서드
         * - JWT에서 사용자 정보를 추출하여 user 객체에 저장
         * - channelAccess.js에서 userId와 userRole 정보가 필요하므로 필수
         */
        setAccessToken(token) {
            this.accessToken = token

            // 토큰이 유효하면
            if (token) {
                // JWT에서 사용자 정보 추출
                const payload = parseJwtPayload(token)
                if (payload) {
                    // channelAccess.js에서 참조하는 필수 정보들
                    this.user = {
                        userId: payload.userId,        // 접근 횟수 추적 키로 사용
                        userRole: payload.userRole,    // 접근 제한 로직 조건
                        userName: payload.userName,    // UI 표시용
                        userEmail: payload.userEmail,  // 사용자 식별용
                        exp: payload.exp,             // 토큰 만료 시간 (자동 로그아웃용)
                        iat: payload.iat              // 토큰 발급 시간 (디버깅용)
                    }
                    this.loggedIn = true
                    this.checked = true
                    
                    console.log('사용자 정보 설정 완료:', this.user)
                } else {
                    console.error('JWT 토큰에서 사용자 정보 추출 실패')
                    this.clearAuth()
                }
            } else {
                this.clearAuth()
            }
        },

        setUser(user) {
            this.user = user
        },

        setUserType(userType) {
            this.userType = userType
        },

        setLoginInfo(token, user, userType) {
            this.accessToken = token
            this.user = user
            this.userType = userType
            this.loggedIn = true
            this.checked = true
        },

        /**
         * 인증 정보 완전 초기화
         * - 로그아웃 시 또는 토큰 만료 시 호출
         * - channelAccess.js에서 참조하는 모든 정보를 안전하게 제거
         * - 메모리와 localStorage 모두에서 토큰 제거
         */
        clearAuth() {
            this.accessToken = null
            this.user = null           // channelAccess.js에서 userId/userRole 참조 불가하게 됨
            this.userType = null
            this.loggedIn = false
            this.checked = true        // 인증 확인은 완료된 상태로 설정
            
            // localStorage에서도 토큰 제거 (보안상 중요)
            localStorage.removeItem('accessToken')
        },

        async login(credentials) {
            try {
                // 실제 로그인 API 호출은 나중에 구현
                // const response = await axios.post('/api/login', credentials)
                // this.setAccessToken(response.data.token)
            } catch (error) {
                throw error
            }
        },

        /**
         * 로그아웃 메서드
         * - 서버에 로그아웃 요청 후 로컬 상태 초기화
         * - channelAccess.js의 접근 데이터는 유지됨 (사용자별로 관리되므로)
         */
        async logout() {
            try {
                // 실제 로그아웃 API 호출은 나중에 구현
                // await axios.post('/api/logout')
                this.clearAuth()
            } catch (error) {
                throw error
            }
        }
    }
}) 