import React from 'react'

export default function Modal({ title, message, onConfirm, onCancel }) {
  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h3 className="modal-title">{title}</h3>
        <p className="modal-message">{message}</p>
        <div className="modal-actions">
          <button className="btn btn-danger" onClick={onConfirm}>확인</button>
          <button className="btn btn-secondary" onClick={onCancel}>취소</button>
        </div>
      </div>
    </div>
  )
}
