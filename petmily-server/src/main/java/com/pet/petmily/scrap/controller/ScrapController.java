package com.pet.petmily.scrap.controller;

import com.pet.petmily.scrap.dto.ScrapDTO;
import com.pet.petmily.scrap.service.ScrapService;
import com.pet.petmily.scrap.response.ScrapResponse;
import com.pet.petmily.user.entity.Member;
import com.pet.petmily.user.repository.MemberRepository;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ScrapController {
    private final MemberRepository memberRepository;
    private final ScrapService scrapService;

    @ApiOperation(value = "게시글 스크랩", notes = "게시글을 스크랩합니다.")
    @PostMapping("/post/{postId}/scrap")
    public ResponseEntity<ScrapResponse<String>> addScrap(@PathVariable("postId") Long postId, Authentication auth) {
        log.info("게시글 스크랩 요청");

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<Member> memberOptional = memberRepository.findByEmail(userDetails.getUsername());

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            try {
                scrapService.addScrap(postId, member);
                ScrapResponse<String> response = new ScrapResponse<>("success", "게시글을 스크랩하였습니다.", null, null);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                log.error("게시글 스크랩 실패", e);
                ScrapResponse<String> response = new ScrapResponse<>("error", "게시글 스크랩에 실패하였습니다.", null, e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } else {
            ScrapResponse<String> response = new ScrapResponse<>("error", "없는 회원입니다.", null, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @ApiOperation(value = "게시글 스크랩 취소", notes = "게시글 스크랩을 취소합니다.")
    @DeleteMapping("/scrap/{scrapid}/cancel")
    public ResponseEntity<ScrapResponse<String>> cancelScrap(@PathVariable("scrapid") Long scrapId, Authentication auth) {
        log.info("게시글 스크랩 취소 요청");
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<Member> memberOptional = memberRepository.findByEmail(userDetails.getUsername());

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            try {
                scrapService.cancelScrap(scrapId, member);
                ScrapResponse<String> response = new ScrapResponse<>("success", "게시글 스크랩을 취소하였습니다.", null, null);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                log.error("게시글 스크랩 취소 실패", e);
                ScrapResponse<String> response = new ScrapResponse<>("error", "게시글 스크랩 취소에 실패하였습니다.", null, e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } else {
            ScrapResponse<String> response = new ScrapResponse<>("error", "없는 회원입니다.", null, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @ApiOperation(value = "스크랩 목록 조회", notes = "회원의 스크랩 목록을 조회합니다.")
    @GetMapping("/scrap/list")
    public ResponseEntity<ScrapResponse<List<ScrapDTO>>> getScrapList(Authentication auth) {
        log.info("스크랩 목록 조회 요청");
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<Member> memberOptional = memberRepository.findByEmail(userDetails.getUsername());

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            try {
                List<ScrapDTO> scrapDTOList = scrapService.getScrapListByMember(member.getId());
                ScrapResponse<List<ScrapDTO>> response = new ScrapResponse<>("success", "스크랩 목록을 조회하였습니다.", null, scrapDTOList);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                log.error("스크랩 목록 조회 실패", e);
                ScrapResponse<List<ScrapDTO>> response = new ScrapResponse<>("error", "스크랩 목록 조회에 실패하였습니다.", null, null);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } else {
            ScrapResponse<List<ScrapDTO>> response = new ScrapResponse<>("error", "없는 회원입니다.", null, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


}
