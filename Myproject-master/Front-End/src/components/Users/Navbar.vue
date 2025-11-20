<script setup>
import { ref,defineEmits,defineProps } from 'vue'
import Searchbar from './Searchbar.vue'
import AccountMenu from './AccountMenu.vue'
const props = defineProps({
    currentPage: String  // ✅ 從 App.vue 接收
})
const emit = defineEmits(['change-page'])
const currentPage = ref('Home')
const navLinks = ref([{ name: 'Home' }, { name: 'My Fridge' }, { name: 'Recipes' }, { name: 'Shopping List' }, ])

const isActive = (pageName) => {
   return props.currentPage === pageName
} //控制哪個按鈕要顯示橘色

const handlePageChange = (pageName) => {
    currentPage.value = pageName
    console.log('Navbar emit:', pageName)
    emit('change-page', pageName) 
} //通知App.vue切換畫面
</script>

<template>
    <header class="fixed top-0 w-full max-w-screen overflow-x-hidden z-50 bg-gray-100 shadow-sm">
        <div class="px-8 py-4 relative">
            <div class="absolute left-8 top-1/2 -translate-y-1/2 z-10">
                <div class="text-3xl font-bold text-[#ff5400]">Pantry Pilot</div>
            </div>

            <div class="flex justify-center">
                <nav class="flex items-center space-x-6 text-sm">
                    <a
                        href="#"
                        v-for="link in navLinks"
                        :key="link.name"
                        :class="[
                            'px-6 py-2.5 rounded-full cursor-pointer transition-all duration-200 ease-in-out whitespace-nowrap text-base font-semibold',
                            {
                                'bg-orange-100 text-[#ff5400] shadow-lg scale-110 border-b-4 border-[#ff5400]': isActive(link.name),
                                'text-gray-500 hover:text-[#ff5400] hover:bg-orange-50 hover:scale-105': !isActive(link.name),
                            },
                        ]"
                        @click.prevent="handlePageChange(link.name)"
                    >
                        {{ link.name }}
                    </a>
                </nav>
            </div>

            <div class="absolute right-8 top-1/2 -translate-y-1/2 z-10 flex items-center space-x-4">
                <Searchbar />
                <AccountMenu />
            </div>
        </div>
    </header>
</template>
