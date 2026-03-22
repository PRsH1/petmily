package com.pet.petmily.report.repository;

import com.pet.petmily.board.entity.Post;
import com.pet.petmily.report.entity.Report;
import com.pet.petmily.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByPost(Post post);
    Optional<Report> findByPostAndMember(Post post, Member member);

    int countByPost(Post post);

    void deleteByPost(Post post);
}