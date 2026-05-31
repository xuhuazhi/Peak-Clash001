import axios from 'axios'

const request = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 5000
})

export function loginApi(username: string, password: string) {
    return request.post('/user/login', { username, password })
}

export function registerApi(username: string, password: string) {
    return request.post('/user/register', { username, password })
}

export function getCaptchaApi() {
    return request.get('/user/captcha')
}