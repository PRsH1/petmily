package com.pet.petmily.comment.entity;

import com.pet.petmily.user.entity.BaseTimeEntity;
import com.pet.petmily.user.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "comment_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"comment_id", "member_id"}))
public class CommentLike extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public CommentLike(Comment comment, Member member) {
        this.comment = comment;
        this.member = member;
    }
}
