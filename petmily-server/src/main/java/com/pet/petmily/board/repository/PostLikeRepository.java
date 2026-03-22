package com.pet.petmily.board.repository;

import com.pet.petmily.board.entity.Post;
import com.pet.petmily.board.entity.PostLike;
import com.pet.petmily.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndMember(Post post, Member member);
}
