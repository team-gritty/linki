import { defineStore } from 'pinia'

export const useAccountStore = defineStore('account', {
    state: () => ({
        checked: false,        // 로그인 상태 확인 여부
        loggedIn: false,       // 로그인 여부
        accessToken: null,     // JWT 액세스 토큰
        user: null,           // 사용자 정보
        userType: null        // 사용자 타입 (influencer, advertiser, general)
    }),

    getters: {
        isLoggedIn: (state) => state.loggedIn && !!state.accessToken,
        getUser: (state) => state.user,
        getAccessToken: (state) => state.accessToken,
        getUserType: (state) => state.userType,
        isChecked: (state) => state.checked
    },

    actions: {
        setChecked(val) {
            this.checked = val
        },

        setLoggedIn(val) {
            this.loggedIn = val
        },

        setAccessToken(token) {
            this.accessToken = token
            this.loggedIn = !!token
        },

        setUser(user) {
            this.user = user
        },

        setUserType(userType) {
            this.userType = userType
        },

        clearAuth() {
            this.accessToken = null
            this.user = null
            this.userType = null
            this.loggedIn = false
        },

        // 로그인 성공 시 모든 정보 설정
        setLoginInfo(token, user, userType) {
            this.setAccessToken(token)
            this.setUser(user)
            this.setUserType(userType)
            this.setLoggedIn(true)
            this.setChecked(true)
        },

        async login(credentials) {
            try {
                // 실제 로그인 API 호출은 나중에 구현
                // const response = await axios.post('/api/login', credentials)
                // this.setAccessToken(response.data.token)
                // this.setUser(response.data.user)
            } catch (error) {
                throw error
            }
        },

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