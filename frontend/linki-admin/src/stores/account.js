
import { defineStore } from 'pinia'

export const useAccountStore = defineStore('account', {
    state: () => ({
        accessToken: null,
        user: null
    }),

    getters: {
        isLoggedIn: (state) => !!state.accessToken,
        getUser: (state) => state.user,
        getAccessToken: (state) => state.accessToken
    },

    actions: {
        setAccessToken(token) {
            this.accessToken = token
        },

        setUser(user) {
            this.user = user
        },

        clearAuth() {
            this.accessToken = null
            this.user = null
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