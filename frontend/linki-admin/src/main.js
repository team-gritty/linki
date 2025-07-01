// import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import VueECharts from 'vue-echarts'

import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia()

// Pinia persistence 플러그인 추가
pinia.use(piniaPluginPersistedstate)

app.use(pinia)
app.use(router)
app.component('v-chart', VueECharts)

app.mount('#app')
