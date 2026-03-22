import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Header() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [query, setQuery] = useState('')

  const handleSearch = (e) => {
    e.preventDefault()
    if (query.trim()) {
      navigate(`/search?q=${encodeURIComponent(query.trim())}`)
      setQuery('')
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <header className="header">
      <div className="header-inner">
        <Link to="/" className="logo">
          🐾 Petmily
        </Link>

        {user && (
          <form className="search-form" onSubmit={handleSearch}>
            <input
              type="text"
              placeholder="게시글 또는 채널 검색..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              className="search-input"
            />
            <button type="submit" className="search-btn">검색</button>
          </form>
        )}

        <nav className="header-nav">
          {user ? (
            <>
              <Link to="/mypage" className="nav-link">마이페이지</Link>
              <button onClick={handleLogout} className="nav-btn">로그아웃</button>
            </>
          ) : (
            <>
              <Link to="/login" className="nav-link">로그인</Link>
              <Link to="/signup" className="nav-link nav-link-primary">회원가입</Link>
            </>
          )}
        </nav>
      </div>
    </header>
  )
}
