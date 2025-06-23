import { defineStore } from 'pinia'

export const useAccountStore = defineStore('account', {
    persist: true,
    state: () => ({
        accessToken: null,
        user: null,
        userType: null,
        checked: false,
        loggedIn: false
    }),

    getters: {
        isLoggedIn: (state) => !!state.accessToken && state.loggedIn,
        getUser: (state) => state.user,
        getAccessToken: (state) => state.accessToken,
        getUserType: (state) => state.userType,
        isChecked: (state) => state.checked
    },

    actions: {
        setAccessToken(token) {
            this.accessToken = token
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

        clearAuth() {
            this.accessToken = null
            this.user = null
            this.userType = null
            this.loggedIn = false
            this.checked = true
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
                localStorage.removeItem('token')
            } catch (error) {
                throw error
            }
        }
    }
}) 