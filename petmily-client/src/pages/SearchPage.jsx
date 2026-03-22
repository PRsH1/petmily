import React, { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { searchPosts } from '../api/post'
import { searchChannels } from '../api/channel'

export default function SearchPage() {
  const [searchParams] = useSearchParams()
  const query = searchParams.get('q') || ''
  const [posts, setPosts] = useState([])
  const [channels, setChannels] = useState([])
  const [loading, setLoading] = useState(false)
  const [tab, setTab] = useState('posts')

  useEffect(() => {
    if (!query) return
    setLoading(true)
    Promise.all([
      searchPosts(query).catch(() => []),
      searchChannels(query).catch(() => []),
    ])
      .then(([p, c]) => { setPosts(p || []); setChannels(c || []) })
      .finally(() => setLoading(false))
  }, [query])

  return (
    <div className="container">
      <h1 className="page-title">검색 결과: "{query}"</h1>

      <div className="tab-list">
        <button className={`tab-btn ${tab === 'posts' ? 'active' : ''}`} onClick={() => setTab('posts')}>
          게시글 ({posts.length})
        </button>
        <button className={`tab-btn ${tab === 'channels' ? 'active' : ''}`} onClick={() => setTab('channels')}>
          채널 ({channels.length})
        </button>
      </div>

      {loading && <div className="loading">검색 중...</div>}

      {!loading && tab === 'posts' && (
        <div className="post-list">
          {posts.length === 0 ? (
            <p className="empty-text">검색 결과가 없습니다.</p>
          ) : (
            posts.map((post) => (
              <Link
                key={post.id}
                to={`/channel/${post.channelId}/post/${post.id}`}
                className="post-item post-item-link"
              >
                <h3 className="post-title">{post.title}</h3>
                <div className="post-meta">
                  <span>{post.channelName}</span>
                  <span>{post.nickname}</span>
                  <span>{post.createDate?.slice(0, 10)}</span>
                  <span>조회 {post.hit}</span>
                </div>
              </Link>
            ))
          )}
        </div>
      )}

      {!loading && tab === 'channels' && (
        <div className="channel-grid">
          {channels.length === 0 ? (
            <p className="empty-text">검색 결과가 없습니다.</p>
          ) : (
            channels.map((ch) => (
              <Link key={ch.channelId} to={`/channel/${ch.channelId}`} className="channel-card">
                <h3 className="channel-name">{ch.channelName}</h3>
              </Link>
            ))
          )}
        </div>
      )}
    </div>
  )
}
