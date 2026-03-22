package com.pet.petmily.scrap.dto;

import com.pet.petmily.board.dto.PostDTO;
import com.pet.petmily.scrap.entity.Scrap;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ScrapDTO{
    private Long id;

    @ApiModelProperty(value = "게시물 객체", example = "게시물 객체")
    private PostDTO post;


    public static ScrapDTO toDto(Scrap scrap) {
        log.info("ScrapDTO toDto 메소드");

        return new ScrapDTO(
                scrap.getId(),
                PostDTO.toDto(scrap.getPost())
        );
    }
}
