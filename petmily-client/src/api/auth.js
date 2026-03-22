import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export async function login(email, password) {
  const res = await axios.post(`${BASE_URL}/login`, { email, password })
  const accessToken = res.headers['authorization']?.replace('Bearer ', '')
  const refreshToken = res.headers['authorization-refresh']
  return { accessToken, refreshToken }
}

export async function signup(data) {
  const res = await axios.post(`${BASE_URL}/sign-up`, data)
  return res.data
}

export async function checkEmail(email) {
  const res = await axios.post(`${BASE_URL}/sign-up/email-check`, null, {
    params: { email },
  })
  return res.data
}

export async function checkNickname(nickname) {
  const res = await axios.post(`${BASE_URL}/sign-up/nickname-check`, null, {
    params: { nickname },
  })
  return res.data
}
