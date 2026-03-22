import React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import PrivateRoute from './components/PrivateRoute'
import Header from './components/Header'
import LoginPage from './pages/LoginPage'
import SignupPage from './pages/SignupPage'
import HomePage from './pages/HomePage'
import ChannelPage from './pages/ChannelPage'
import PostDetailPage from './pages/PostDetailPage'
import PostWritePage from './pages/PostWritePage'
import PostEditPage from './pages/PostEditPage'
import MyPage from './pages/MyPage'
import SearchPage from './pages/SearchPage'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Header />
        <main className="main-content">
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/signup" element={<SignupPage />} />
            <Route path="/" element={<PrivateRoute><HomePage /></PrivateRoute>} />
            <Route path="/channel/:channelId" element={<PrivateRoute><ChannelPage /></PrivateRoute>} />
            <Route path="/channel/:channelId/post/:postId" element={<PrivateRoute><PostDetailPage /></PrivateRoute>} />
            <Route path="/channel/:channelId/write" element={<PrivateRoute><PostWritePage /></PrivateRoute>} />
            <Route path="/channel/:channelId/post/:postId/edit" element={<PrivateRoute><PostEditPage /></PrivateRoute>} />
            <Route path="/mypage" element={<PrivateRoute><MyPage /></PrivateRoute>} />
            <Route path="/search" element={<PrivateRoute><SearchPage /></PrivateRoute>} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
      </BrowserRouter>
    </AuthProvider>
  )
}
