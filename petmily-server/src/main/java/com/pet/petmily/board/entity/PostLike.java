package com.pet.petmily.board.entity;

import com.pet.petmily.user.entity.BaseTimeEntity;
import com.pet.petmily.user.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "member_id"}))
public class PostLike extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public PostLike(Post post, Member member) {
        this.post = post;
        this.member = member;
    }
}
