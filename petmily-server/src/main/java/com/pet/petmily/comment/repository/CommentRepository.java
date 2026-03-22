package com.pet.petmily.comment.repository;

import com.pet.petmily.board.entity.Post;
import com.pet.petmily.comment.entity.Comment;
import com.pet.petmily.user.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = {"member"})
    List<Comment> findByPostOrderByCreateDateAsc(Post post);

    @EntityGraph(attributePaths = {"member"})
    List<Comment> findByMemberOrderByCreateDateAsc(Member member);
}
