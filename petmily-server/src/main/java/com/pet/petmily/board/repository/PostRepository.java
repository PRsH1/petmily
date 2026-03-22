package com.pet.petmily.board.repository;

import com.pet.petmily.board.entity.Post;
import com.pet.petmily.user.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByChannel_ChannelId(Long channelId);
    Page<Post> findAllByChannel_ChannelIdOrderByCreateDateDesc(Long channelId, Pageable pageable);

    List<Post> findByMember(Member member);
    Page<Post> findByMemberOrderByCreateDateDesc(Member member, Pageable pageable);

    List<Post> findByTitleContaining(String query);
    Page<Post> findByTitleContainingOrderByCreateDateDesc(String query, Pageable pageable);
}
