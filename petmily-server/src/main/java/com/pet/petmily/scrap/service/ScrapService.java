package com.pet.petmily.scrap.service;

import com.pet.petmily.board.dto.PostDTO;
import com.pet.petmily.board.entity.Post;
import com.pet.petmily.scrap.entity.Scrap;
import com.pet.petmily.user.entity.Member;
import com.pet.petmily.scrap.dto.ScrapDTO;
import com.pet.petmily.user.repository.MemberRepository;
import com.pet.petmily.scrap.repository.ScrapRepository;
import com.pet.petmily.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public ScrapDTO addScrap(Long postId, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. " + postId));

        // 스크랩 중복 확인
        List<Scrap> existingScraps = scrapRepository.findByMemberAndPost(member, post);
        if (!existingScraps.isEmpty()) {
            throw new IllegalArgumentException("이미 스크랩한 게시물입니다.");
        }

        Scrap scrap = new Scrap();
        scrap.setPost(post);
        scrap.setMember(member);
        scrapRepository.save(scrap);

        return ScrapDTO.toDto(scrap);
    }


    public void cancelScrap(Long scrapId, Member member) {
        Scrap scrap = scrapRepository.findById(scrapId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스크랩이 없습니다. " + scrapId));

        if (!scrap.getMember().equals(member)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        scrapRepository.delete(scrap);
    }


    // 스크랩 목록 조회
    public List<ScrapDTO> getScrapListByMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다. " + memberId));
        List<Scrap> scraps = scrapRepository.findByMember(member);

        return scraps.stream()
                .filter(scrap -> scrap.getMember().equals(member))
                .map(scrap -> {
                    ScrapDTO scrapDTO = new ScrapDTO();
                    scrapDTO.setId(scrap.getId());
                    Post post = postRepository.findById(scrap.getPost().getPostId()).orElse(null);
                    scrapDTO.setPost(PostDTO.toDto(post)); // 게시물 객체 설정
                    return scrapDTO;
                })
                .collect(Collectors.toList());
    }




}
