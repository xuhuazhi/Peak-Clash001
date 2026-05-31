import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            redirect: '/login'
        },
        {
            path: '/login',
            name: 'Login',
            component: () => import('../views/Login.vue')
        },
        {
            path: '/character-select',
            name: 'CharacterSelect',
            component: () => import('../views/CharacterSelect.vue')
        },
        {
            path: '/battle',
            name: 'Battle',
            component: () => import('../views/Battle.vue')
        },
        {
            path: '/game-over',
            name: 'GameOver',
            component: () => import('../views/GameOver.vue')
        }
    ]
})

export default router