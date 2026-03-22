package com.pet.petmily.scrap.repository;

import com.pet.petmily.board.entity.Post;
import com.pet.petmily.scrap.entity.Scrap;
import com.pet.petmily.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    List<Scrap> findByMember(Member member);
    List<Scrap> findByMemberAndPost(Member member, Post post);
}
