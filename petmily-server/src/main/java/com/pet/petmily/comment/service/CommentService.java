package com.pet.petmily.comment.service;

import com.pet.petmily.board.entity.Post;
import com.pet.petmily.comment.entity.Comment;
import com.pet.petmily.user.entity.Member;
import com.pet.petmily.comment.dto.CommentDTO;
import com.pet.petmily.user.repository.MemberRepository;
import com.pet.petmily.comment.repository.CommentRepository;
import com.pet.petmily.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    //댓글 목록 조회
    public List<CommentDTO> getCommentListByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("없는 게시물입니다: " + postId));
        List<Comment> comments = commentRepository.findByPostOrderByCreateDateAsc(post);

        return comments.stream()
                .map(comment -> {
                    CommentDTO commentDTO = CommentDTO.toDto(comment);
                    String nickname = comment.getMember().getNickname();
                    commentDTO.setNickname(nickname);
                    return commentDTO;
                })
                .collect(Collectors.toList());
    }

    //댓글 작성
    public CommentDTO addComment(Long postId, CommentDTO commentDTO,Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다: " + postId));


        Comment comment = new Comment();
        comment.setPost(post);
        comment.setStatus(true);
        comment.setMember(member);
        comment.setContent(commentDTO.getContent());
        commentRepository.save(comment);

        return CommentDTO.toDto(comment);
    }

    //댓글 삭제
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("없는 댓글입니다: " + commentId));
        commentRepository.delete(comment);
    }
    // 댓글 수정
    public CommentDTO updateComment(Long commentId, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("없는 댓글입니다: " + commentId));
        comment.setContent(commentDTO.getContent());
        commentRepository.save(comment);
        return CommentDTO.toDto(comment);
    }

    //댓글 좋아요
    public void likeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("없는 댓글입니다: " + commentId));
        comment.setCommentLike(comment.getCommentLike() + 1);
        commentRepository.save(comment);
    }

    //댓글 작성자 확인
    public boolean isCommentWriter(Long commentId, long memberId) {
        Optional<Comment> commentOptional=commentRepository.findById(commentId);
        if(commentOptional.isPresent()){
            Comment comment=commentOptional.get();
            return comment.getMember().getId()==memberId;

        }
        throw new IllegalArgumentException("없는 댓글입니다: " + commentId);
    }

    public List<CommentDTO> getMyCommentList(long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 회원입니다: " + id));
        List<Comment> comments = commentRepository.findByMemberOrderByCreateDateAsc(member);

        return comments.stream()
                .map(comment -> {
                    CommentDTO commentDTO = CommentDTO.toDto(comment);
                    String nickname = comment.getMember().getNickname();
                    commentDTO.setNickname(nickname);
                    return commentDTO;
                })
                .collect(Collectors.toList());
    }
}
