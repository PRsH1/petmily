package com.pet.petmily.comment.controller;

import com.pet.petmily.board.entity.Post;
import com.pet.petmily.comment.dto.CommentDTO;
import com.pet.petmily.comment.entity.Comment;
import com.pet.petmily.comment.response.CommentAndPostResponse;
import com.pet.petmily.comment.response.CommentResponse;
import com.pet.petmily.comment.service.CommentService;

import com.pet.petmily.user.entity.Member;
import com.pet.petmily.user.repository.MemberRepository;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CommentController {
    private final MemberRepository memberRepository;

    private final CommentService commentService;

    @ApiOperation(value = "댓글 목록 조회", notes = "게시물의 댓글 목록 조회")
    @GetMapping("/post/{postId}/comment")
    public CommentAndPostResponse<List<CommentDTO>> getCommentListByPost(@PathVariable Long postId) {
        try {
            List<CommentDTO> commentDTOList = commentService.getCommentListByPost(postId);
            log.info("댓글 목록 조회 성공");
            return new CommentAndPostResponse<>(postId,commentDTOList);
        } catch (Exception e) {
            log.error("댓글 목록 조회 실패", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    @ApiOperation(value = "내가 쓴 댓글 조회" ,notes = "내가 쓴 댓글 조회")
    @GetMapping("/comment/mycomment")
    public CommentResponse<List<CommentDTO>> getMyCommentList(Authentication auth){
        UserDetails userDetails=(UserDetails)auth.getPrincipal();
        Optional<Member> memberOptional=memberRepository.findByEmail(userDetails.getUsername());
        if(memberOptional.isPresent()){
            Member member=memberOptional.get();
            List<CommentDTO> commentDTOList=commentService.getMyCommentList(member.getId());

            return new CommentResponse<>("성공","내 댓글 목록 조회",member.getNickname(),commentDTOList);
        }
        else{
            return new CommentResponse<>("실패","없는 회원입니다.",null,null);
        }
    }



    @ApiOperation(value = "댓글 등록", notes = "게시물에 댓글 등록")
    @PostMapping("/post/{postId}/comment/add")
    public ResponseEntity<String> addComment(@PathVariable("postId") Long postId, @RequestBody CommentDTO commentDTO
    ,Authentication auth) {
        log.info("댓글 등록 요청");
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다"));
        try {
            CommentDTO createdComment = commentService.addComment(postId, commentDTO,member);
            return ResponseEntity.status(HttpStatus.CREATED).body("댓글을 등록하였습니다.");
        } catch (Exception e) {
            log.error("댓글 등록 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @ApiOperation(value = "댓글 삭제", notes = "댓글 삭제")
    @DeleteMapping("/comment/{commentId}/delete")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId,Authentication auth) {
        log.info("댓글 삭제 요청");
        UserDetails userDetails=(UserDetails)auth.getPrincipal();
        Optional<Member> memberOptional=memberRepository.findByEmail(userDetails.getUsername());
        if(memberOptional.isPresent()) {
            Member member=memberOptional.get();
            boolean isWriter = commentService.isCommentWriter(commentId, member.getId());
            if(!isWriter){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("댓글 작성자만 삭제할 수 있습니다.");
            }
            else{
                try {
                    commentService.deleteComment(commentId);
                    return ResponseEntity.ok("댓글이 삭제되었습니다.");
                } catch (IllegalArgumentException e) {
                    log.error("댓글 삭제 실패", e);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("없는 댓글입니다.");
                } catch (Exception e) {
                    log.error("댓글 삭제를 실패했습니다.", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제 중 오류가 발생했습니다.");
                }

            }

        }
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("없는 회원입니다.");
    }

    @ApiOperation(value = "댓글 수정", notes = "댓글 수정")
    @PutMapping("comment/{commentId}/update")
    public CommentResponse<CommentDTO> updateComment(@PathVariable Long commentId, @RequestBody CommentDTO commentDTO
    , Authentication auth) {
        log.info("댓글 수정 요청");
        UserDetails userDetails=(UserDetails)auth.getPrincipal();
        Optional<Member> memberOptional=memberRepository.findByEmail(userDetails.getUsername());
        if(memberOptional.isPresent()) {
            Member member=memberOptional.get();
            boolean isWriter = commentService.isCommentWriter(commentId, member.getId());
            if(!isWriter){
                return new CommentResponse("댓글 수정 실패.","수정이 실패되었습니다:당신은 댓글 작성자가 아닙니다",null,null);

            }
            else{
                try {

                    return new CommentResponse("댓글이 수정되었습니다.","댓글이 수정되었습니다.",member.getNickname(),commentService.updateComment(commentId, commentDTO));
                } catch (IllegalArgumentException e) {
                    log.error("댓글 수정 실패", e);
                    return new CommentResponse<>("댓글 수정 실패", "없는 댓글입니다.", null, null);
                } catch (Exception e) {
                    log.error("댓글 수정 실패", e);
                    return new CommentResponse<>("댓글 수정 실패", "댓글 수정 중 오류가 발생했습니다.", null, null);
                }
            }
        }
        return new CommentResponse<>("댓글 수정 실패", "없는 회원입니다.", null, null);

    }

    @ApiOperation(value = "댓글 좋아요", notes = "댓글 좋아요 추가")
    @PostMapping("comment/{commentId}/like")
    public ResponseEntity<String> likeComment(@PathVariable Long commentId) {
        try {
            commentService.likeComment(commentId);
            return ResponseEntity.ok("댓글에 좋아요를 표시했습니다.");
        } catch (Exception e) {
            log.error("댓글 좋아요 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 좋아요 표시 중 오류가 발생했습니다.");
        }
    }
}
