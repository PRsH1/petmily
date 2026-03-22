package com.pet.petmily.board.repository;

import com.pet.petmily.board.entity.Post;
import com.pet.petmily.user.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByChannel_ChannelId(Long channelId);


    List<Post> findByMember(Member member);

    List<Post> findByTitleContaining(String query);
}
