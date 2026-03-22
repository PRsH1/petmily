package com.pet.petmily.board.entity;


import com.pet.petmily.user.entity.Member;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Getter
@Setter
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Channel  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private long channelId;

    @Column(name = "channel_name")
    private String channelName;

    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String url;

}
