import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import './styles/style.css'
import { Icon } from '@iconify/vue'
const app = createApp(App)

app.use(createPinia())
app.component('Icon', Icon)
app.mount('#app')
