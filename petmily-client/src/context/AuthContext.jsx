import React, { createContext, useContext, useState, useEffect } from 'react'
import { login as loginApi } from '../api/auth'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('accessToken')
    const email = localStorage.getItem('userEmail')
    const nickname = localStorage.getItem('userNickname')
    if (token && email) {
      setUser({ email, nickname })
    }
    setLoading(false)
  }, [])

  const login = async (email, password) => {
    const { accessToken, refreshToken } = await loginApi(email, password)
    if (!accessToken) throw new Error('토큰을 받아오지 못했습니다.')
    localStorage.setItem('accessToken', accessToken)
    if (refreshToken) localStorage.setItem('refreshToken', refreshToken)
    localStorage.setItem('userEmail', email)
    setUser({ email })
  }

  const logout = () => {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userEmail')
    localStorage.removeItem('userNickname')
    setUser(null)
  }

  const updateNickname = (nickname) => {
    localStorage.setItem('userNickname', nickname)
    setUser((prev) => ({ ...prev, nickname }))
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, updateNickname }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
