package com.pet.petmily.board.repository;


import com.pet.petmily.board.entity.Channel;
import com.pet.petmily.board.entity.Favorite;
import com.pet.petmily.board.entity.Post;
import com.pet.petmily.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

   // Optional<Favorite> findByPostAndMember(Post post, Member member);

    List<Favorite> findAllByMember(Member member);

    List<Favorite> findByMember(Member member);

    Optional<Favorite> findByChannelAndMember(Channel channel, Member member);
}