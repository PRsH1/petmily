package com.pet.petmily.comment.repository;

import com.pet.petmily.comment.entity.Comment;
import com.pet.petmily.comment.entity.CommentLike;
import com.pet.petmily.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndMember(Comment comment, Member member);
}
