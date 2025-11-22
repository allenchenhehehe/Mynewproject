import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import './styles/style.css'
import { Icon } from '@iconify/vue'
import Vue3Toasity, { toast } from 'vue3-toastify';

const app = createApp(App)

app.use(createPinia())
app.use( Vue3Toasity, {
  autoClose: 1000, //自動關閉時間
  position: toast.POSITION.TOP_CENTER //提示窗位置
});
app.component('Icon', Icon)
app.mount('#app')
