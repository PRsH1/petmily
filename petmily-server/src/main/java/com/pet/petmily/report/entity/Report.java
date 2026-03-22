package com.pet.petmily.report.entity;

import com.pet.petmily.board.entity.Post;
import com.pet.petmily.user.entity.Member;
import lombok.*;
import lombok.extern.slf4j.Slf4j;


import javax.persistence.*;

@Entity
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;


    @JoinColumn(name = "post_id")
    @ManyToOne
    private Post post;

    @JoinColumn(name = "member_id")
    @ManyToOne
    private Member member;


}