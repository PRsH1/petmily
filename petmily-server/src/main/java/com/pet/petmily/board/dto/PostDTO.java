package com.pet.petmily.board.dto;


import com.pet.petmily.board.entity.Post;
import com.pet.petmily.user.entity.BaseTimeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO extends BaseTimeEntity {
    private long id;
    @ApiModelProperty(value = "제목", example = "제목입니다.")
    @NotNull(message = "제목은 필수입니다.")
    private String title;

    @ApiModelProperty(value = "내용", example = "여기에 글의 내용을 적습니다.")
    private String content;
    @ApiModelProperty(value = "url", example = "url입니다.")
    private String url; // url
    private int likePost;
    private long hit;

    @ApiModelProperty(value = "이미지 경로", example = "이미지 경로입니다.")
    private String imagePath;

    @ApiModelProperty(value = "작성자아이디", example = "작성자 아이디입니다.")
    private Long memberId;

    @ApiModelProperty(value = "작성자", example = "작성자입니다.")
    private String nickname;

    @ApiModelProperty(value = "채널 아이디", example = "채널 아이디입니다.")
    private Long channelId;
    @ApiModelProperty(value = "채널 이름", example = "채널 이름입니다.")
    private String channelName;

    @ApiModelProperty(value = "작성일", example = "작성일입니다.")
    private LocalDateTime createDate;
    @ApiModelProperty(value = "수정일", example = "수정일입니다.")
    private LocalDateTime lastModifiedDate;
    private int commentCount; // 댓글 수

    public static PostDTO toDto(Post post) {

        log.info("postDTO todto메소드 호출");
        return new PostDTO(

                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getUrl(),
                post.getLikePost(),
                post.getHit(),

                post.getImagePath(),

                post.getMember().getId(),
                post.getMember().getNickname(),

                post.getChannel().getChannelId(),
                post.getChannel().getChannelName(),
                post.getCreateDate(),
                post.getLastModifiedDate(),
                post.getComments().size());


    }



}
