package com.pet.petmily.board.repository;

import com.pet.petmily.board.entity.Channel;
import com.pet.petmily.user.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    Channel findByChannelName(String channelName);
    List<Channel> findAllByMember(Member member);
    List<Channel> findAllByChannelNameContaining(String query);
    Page<Channel> findAllByChannelNameContainingOrderByCreateDateDesc(String query, Pageable pageable);
    Page<Channel> findAllByOrderByCreateDateDesc(Pageable pageable);
}
