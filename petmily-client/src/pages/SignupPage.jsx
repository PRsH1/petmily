import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { signup, checkEmail, checkNickname } from '../api/auth'

export default function SignupPage() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', nickname: '', password: '', passwordConfirm: '' })
  const [status, setStatus] = useState({ email: null, nickname: null })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleChange = (e) => {
    const { name, value } = e.target
    setForm({ ...form, [name]: value })
    if (name === 'email') setStatus((s) => ({ ...s, email: null }))
    if (name === 'nickname') setStatus((s) => ({ ...s, nickname: null }))
  }

  const handleCheckEmail = async () => {
    if (!form.email) return
    try {
      await checkEmail(form.email)
      setStatus((s) => ({ ...s, email: 'ok' }))
    } catch {
      setStatus((s) => ({ ...s, email: 'dup' }))
    }
  }

  const handleCheckNickname = async () => {
    if (!form.nickname) return
    try {
      await checkNickname(form.nickname)
      setStatus((s) => ({ ...s, nickname: 'ok' }))
    } catch {
      setStatus((s) => ({ ...s, nickname: 'dup' }))
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    if (status.email !== 'ok') return setError('이메일 중복 확인을 해주세요.')
    if (status.nickname !== 'ok') return setError('닉네임 중복 확인을 해주세요.')
    if (form.password !== form.passwordConfirm) return setError('비밀번호가 일치하지 않습니다.')
    setLoading(true)
    try {
      await signup({ email: form.email, nickname: form.nickname, password: form.password })
      alert('회원가입이 완료되었습니다.')
      navigate('/login')
    } catch (err) {
      setError(err.response?.data?.message || '회원가입에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-box">
        <div className="auth-logo">🐾 Petmily</div>
        <h2 className="auth-title">회원가입</h2>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label className="form-label">이메일</label>
            <div className="input-row">
              <input
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                placeholder="이메일 입력"
                className="form-input"
                required
              />
              <button type="button" className="btn btn-secondary btn-sm" onClick={handleCheckEmail}>
                중복 확인
              </button>
            </div>
            {status.email === 'ok' && <p className="form-success">사용 가능한 이메일입니다.</p>}
            {status.email === 'dup' && <p className="form-error">이미 사용 중인 이메일입니다.</p>}
          </div>

          <div className="form-group">
            <label className="form-label">닉네임</label>
            <div className="input-row">
              <input
                type="text"
                name="nickname"
                value={form.nickname}
                onChange={handleChange}
                placeholder="닉네임 입력"
                className="form-input"
                required
              />
              <button type="button" className="btn btn-secondary btn-sm" onClick={handleCheckNickname}>
                중복 확인
              </button>
            </div>
            {status.nickname === 'ok' && <p className="form-success">사용 가능한 닉네임입니다.</p>}
            {status.nickname === 'dup' && <p className="form-error">이미 사용 중인 닉네임입니다.</p>}
          </div>

          <div className="form-group">
            <label className="form-label">비밀번호</label>
            <input
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              placeholder="비밀번호 입력"
              className="form-input"
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">비밀번호 확인</label>
            <input
              type="password"
              name="passwordConfirm"
              value={form.passwordConfirm}
              onChange={handleChange}
              placeholder="비밀번호 재입력"
              className="form-input"
              required
            />
          </div>

          {error && <p className="form-error">{error}</p>}

          <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
            {loading ? '처리 중...' : '회원가입'}
          </button>
        </form>

        <p className="auth-footer">
          이미 계정이 있으신가요? <Link to="/login" className="link">로그인</Link>
        </p>
      </div>
    </div>
  )
}
