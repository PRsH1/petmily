package com.pet.petmily.board.controller;

import com.pet.petmily.board.dto.ChannelDTO;
import com.pet.petmily.board.dto.PostDTO;
import com.pet.petmily.board.entity.Channel;
import com.pet.petmily.board.entity.Post;
import com.pet.petmily.board.repository.ChannelRepository;
import com.pet.petmily.board.repository.PostRepository;
import com.pet.petmily.board.response.ChannelResponse;
import com.pet.petmily.board.response.Response;
import com.pet.petmily.board.service.ChannelService;
import com.pet.petmily.report.dto.ReportDTO;
import com.pet.petmily.report.entity.Report;
import com.pet.petmily.report.repository.ReportRepository;
import com.pet.petmily.user.entity.Member;
import com.pet.petmily.user.repository.MemberRepository;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import com.pet.petmily.board.service.PostService;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PostController {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final ChannelRepository channelRepository;
    private final PostService postService;
    private final ChannelService channelService;
    private final MemberRepository memberRepository;

    @ApiOperation(value = "내가 쓴 게시글 조회", notes = "내가 쓴 게시글 조회")
    @GetMapping("/post/mypage")
    public Response getMyPost(Authentication authentication) {
        log.info("내가 쓴 게시글 조회");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다"));
        return new Response("조회 성공","내가 쓴 게시글 조회",postService.getMyPost(member));

    }
    @ApiOperation(value = "내가 생성한 채널 조회", notes = "내가 생성한 채널 조회")
    @GetMapping("/channel/mypage")
    public Response getMyChannel(Authentication authentication) {
        log.info("내가 생성한 채널 조회");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다"));
        return new Response("조회 성공","내가 생성한 채널 조회",channelService.getMyChannel(member));

    }

    @ApiOperation(value = "게시판 전체 조회", notes = "게시판 전체 조회")
    @GetMapping("/channel/{channelId}/post")
    public ChannelResponse getAllPost(@PathVariable("channelId") Long channelId) {
        log.info("게시판 전체 조회(채널별)");
        if(channelRepository.findById(channelId).isEmpty()){
            //채널 번호 입력 오류
            return new ChannelResponse("조회 실패","채널이 존재하지 않습니다.",null,null,null);
        }
        return new ChannelResponse(
                "조회 성공","채널별 전체 게시물 return",channelService.getChannelById(channelId).getChannelName()
                ,channelRepository.findById(channelId).get().getMember().getNickname()
                ,postService.getAllPost(channelId));
    }

    @ApiOperation(value = "게시판 개별 조회", notes = "해당 postId를 가지는 게시물 조회")
    @GetMapping("/channel/{channelId}/post/{id}")
    public ChannelResponse getPost(@PathVariable("channelId") Long channelId,@PathVariable("id") Long id) {
        log.info("게시판 개별 조회");

        if(channelRepository.findById(channelId).isEmpty()){
            //채널 번호 입력 오류
            return new ChannelResponse("조회 실패","채널이 존재하지 않습니다.",null,null,null);
        }
        else if(postRepository.findById(id).isEmpty()){
            //게시글 번호 입력 오류
            return new ChannelResponse("조회 실패","게시물이 존재하지 않습니다.",null,null,null);
        }
        else {

            return new ChannelResponse("조회 성공", "채널별 개별 게시물 return", channelService.getChannelById(channelId).getChannelName()
                    , channelRepository.findById(channelId).get().getMember().getNickname()
                    , postService.getPost(channelId, id));
        }
    }

    @ApiOperation(value = "채널 생성" , notes = "채널 생성")
    @PostMapping("/channel")
    public ChannelResponse createChannel(@RequestBody ChannelDTO channelDTO, Authentication authentication) {
        log.info("채널 생성");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<Member> memberOptional = memberRepository.findByEmail(userDetails.getUsername());
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            return new ChannelResponse("채널 생성 성공", "채널 생성 성공", channelDTO.getChannelName()
                    , member.getNickname()
                    , channelService.createChannel(channelDTO, member.getId()));

        }
        else {

            return new ChannelResponse(
                    "채널 생성 에러",
                    "유저가 존재하지 않습니다",
                    channelDTO.getChannelName(),
                    null,null
            );
        }

    }
    @ApiOperation(value = "채널 수정" , notes = "채널 수정")
    @PutMapping("/channel/update/{id}")
    public Response updateChannel(@PathVariable("id") Long channelId, @RequestBody ChannelDTO channelDTO, Authentication authentication) {
        log.info("채널 수정");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<Member> memberOptional = memberRepository.findByEmail(userDetails.getUsername());

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            boolean isWriter = channelService.isWriter(channelId, member.getId());
            if(!isWriter)
                return new Response("채널 수정 에러", "당신은 채널 크리에이터가 아닙니다", null);
            else
                return new Response("채널 수정 성공", "채널 수정 성공", channelService.updateChannel(channelId, channelDTO, member.getId()));
        }
        else {

            return new Response(
                    "채널 수정 에러",
                    "유저가 존재하지 않습니다",
                    null
            );
        }
    }
    @ApiOperation(value = "채널 삭제" , notes = "채널 삭제")
    @DeleteMapping("/channel/delete/{id}")
    public Response deleteChannel(@PathVariable("id") Long channelId, Authentication authentication) {
        log.info("채널 삭제");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<Member> memberOptional = memberRepository.findByEmail(userDetails.getUsername());
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            boolean isWriter = channelService.isWriter(channelId, member.getId());
            if(!isWriter)
                return new Response("채널 삭제 에러", "당신은 채널 크리에이터가 아닙니다", null);
            else
                return new Response("채널 삭제 성공", "채널 삭제 성공", channelService.deleteChannel(channelId, member.getId()));

        }
        else {
            // Handle the case when member is not found
            return new Response(
                    "채널 삭제 에러",
                    "유저가 존재하지 않습니다",
                    null
            );
        }
    }
    @ApiOperation(value = "채널 조회" , notes = "채널 조회")
    @GetMapping("/channel")
    public Response getChannel(){
        log.info("채널 조회");

        return new Response("채널 조회 성공","채널 조회 성공",channelService.getChannel());
    }
    @ApiOperation(value = "게시판 작성", notes = "해당 channelId를 가진 채널에 게시물 작성")
    @PostMapping("/channel/{id}/post/write")
    public Response writePost(@PathVariable("id") Long channelId,@RequestBody PostDTO postDto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다"));

        ChannelDTO channelDTO=channelService.getChannelById(channelId);



        log.info("작성완료\n");
        log.info("작성자 : "+member.getNickname());


        return new Response("작성 성공","게시물 작성 성공",postService.writePost(postDto,member,channelDTO));




    }
    @ApiOperation(value = "게시판 수정", notes = "해당 postId를 가진 게시물 수정")
    @PutMapping("/channel/{channelId}/post/update/{id}")
    public Response updatePost(@PathVariable("channelId") Long channelId,@PathVariable("id") Long id,@RequestBody PostDTO postDto,Authentication authentication) {
        UserDetails userDetails=(UserDetails)authentication.getPrincipal();
        Optional<Member> memberOptional = memberRepository.findByEmail(userDetails.getUsername());
        if(memberOptional.isPresent()) {
            Member member = memberOptional.get();
            boolean isWriter = postService.isWriter(id, member.getId());
            if(isWriter){
                log.info("게시판 수정 성공");
                return new Response("수정 성공","게시물 수정 성공",postService.updatePost(channelId,id,postDto));
            }
            else{
                log.info("게시판 수정 실패");
                return new Response("수정 실패","이 글의 작성자가 아닙니다",null);
            }
        }
        else {
            // 존재하지 않는 유저
            return new Response(
                    "수정 에러",
                    "유저가 존재하지 않습니다",
                    null
            );
        }



    }
    @ApiOperation(value = "게시판 삭제", notes = "해당 postId를 가진 게시물 삭제")
    @DeleteMapping("/channel/{channelId}/post/delete/{id}")
    public Response deletePost(@PathVariable("channelId") Long channelId,@PathVariable("id") Long id,Authentication authentication) {
        UserDetails userDetails=(UserDetails)authentication.getPrincipal();
        Optional<Member> memberOptional = memberRepository.findByEmail(userDetails.getUsername());
        if(memberOptional.isPresent()) {
            Member member = memberOptional.get();
            boolean isWriter = postService.isWriter(id, member.getId());
            if(isWriter){
                log.info("게시판 삭제 성공");
                return new Response("삭제 성공","게시물 삭제 성공",postService.deletePost(channelId,id));
            }
            else{
                log.info("게시판 삭제 실패");
                return new Response("삭제 실패","이 글의 작성자가 아닙니다",null);
            }
        }
        else {
            // 존재하지 않는 유저
            return new Response(
                    "삭제 에러",
                    "유저가 존재하지 않습니다",
                    null
            );
        }




        }

    @ApiOperation(value= "좋아요 클릭" ,notes = "해당 postId를 가진 게시물에 좋아요 클릭")
    @PostMapping("/channel/{channelId}/post/{postId}/like")
    public Response likePost(@PathVariable("channelId") Long channelId, @PathVariable("postId") Long postId,
                             Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다"));
        return new Response("좋아요 api 수행", "아래의 메세지를 확인해주세요", postService.likePost(channelId, postId));
    }
    @ApiOperation(value= "게시글 신고" ,notes = "해당 postId를 가진 게시물에 신고")
    @PostMapping("/channel/{channelId}/post/{postId}/report")
    public Response reportPost(@PathVariable("channelId") Long channelId, @PathVariable("postId") Long postId,
                               @RequestBody Report report, Authentication authentication) {
        UserDetails userDetails=(UserDetails)authentication.getPrincipal();
        Optional<Member> memberOptional = memberRepository.findByEmail(userDetails.getUsername());
        if(memberOptional.isPresent()) {
            Member member = memberOptional.get();
            boolean isWriter = postService.isWriter(postId, member.getId());
            if(isWriter){
                log.info("게시판 신고 실패");
                return new Response("신고 실패","자신의 글은 신고할 수 없습니다",null);
            }

            else{

                log.info("게시판 신고 성공");
                return new Response("신고 요청","게시물 신고 api 진입",postService.reportPost(postId,member.getId(),report.getContent()));
            }
        }
        else {
            // 존재하지 않는 유저
            return new Response(
                    "신고 에러",
                    "유저가 존재하지 않습니다",
                    null
            );
        }

    }
    @ApiOperation(value="즐겨찾기 추가", notes="해당 channelId 즐겨찾기 추가")
    @PostMapping("/channel/{channelId}/bookmark")
    public Response bookmarkPost(@PathVariable("channelId") Long channelId,  Authentication authentication, HttpServletRequest request){
        UserDetails userDetails=(UserDetails)authentication.getPrincipal();
        Optional<Member> memberOptional = memberRepository.findByEmail(userDetails.getUsername());
        if(memberOptional.isPresent()) {
            Member member = memberOptional.get();
            boolean isWriter = channelService.isWriter(channelId, member.getId());
            if(isWriter){
                log.info("게시판 즐겨찾기 실패");
                return new Response("즐겨찾기 실패","자신의 글은 즐겨찾기할 수 없습니다",null);
            }

            else{

                log.info("게시판 즐겨찾기 성공");
                return new Response("즐겨찾기 요청","게시물 즐겨찾기 api 진입",channelService.bookmarkChannel(channelId,member));
            }
        }
        else {
            // 존재하지 않는 유저
            return new Response(
                    "즐겨찾기 에러",
                    "유저가 존재하지 않습니다",
                    null
            );
        }

    }
    @ApiOperation(value="즐겨찾기 삭제", notes="해당 게시글 즐겨찾기 삭제")
    @DeleteMapping("/channel/{channelId}/bookmark")
    public Response deleteBookmarkPost(@PathVariable("channelId") Long channelId,Authentication authentication){
        UserDetails userDetails=(UserDetails)authentication.getPrincipal();
        Optional<Member> memberOptional = memberRepository.findByEmail(userDetails.getUsername());
        if(memberOptional.isPresent()) {
            Member member = memberOptional.get();
            boolean isWriter = channelService.isWriter(channelId, member.getId());
            if(isWriter){
                log.info("게시판 즐겨찾기 삭제 실패");
                return new Response("즐겨찾기 삭제 실패","자신의 글은 즐겨찾기할 수 없습니다",null);
            }

            else{

                log.info("게시판 즐겨찾기 삭제 성공");
                return new Response("즐겨찾기 삭제 요청","게시물 즐겨찾기 삭제 api 진입",channelService.deleteBookmarkChannel(channelId,member));
            }
        }
        else {
            // 존재하지 않는 유저
            return new Response(
                    "즐겨찾기 삭제 에러",
                    "유저가 존재하지 않습니다",
                    null
            );
        }

    }
    @ApiOperation(value="즐겨찾기 조회", notes="내가 등록한 즐겨찾기 조회")
    @GetMapping("/bookmark")
    public Response getBookmarkPost( Authentication authentication){
        UserDetails userDetails=(UserDetails)authentication.getPrincipal();
        Optional<Member> memberOptional = memberRepository.findByEmail(userDetails.getUsername());
        if(memberOptional.isPresent()) {
            Member member = memberOptional.get();




            log.info("게시판 즐겨찾기 조회 성공");
            return new Response("즐겨찾기 조회 요청","게시물 즐겨찾기 조회 api 진입",channelService.getBookmarkChannel(member));

        }
        else {
            // 존재하지 않는 유저
            return new Response(
                    "즐겨찾기 조회 에러",
                    "유저가 존재하지 않습니다",
                    null
            );
        }

    }
    @ApiOperation(value = "게시판 검색", notes = "게시판 검색")
    @GetMapping("/post/search")
    public Response searchPosts(@RequestParam("query") String query) {
        try {
            log.info("게시판 검색 api 진입");
            log.info("검색어 : " + query);


            List<Post> foundPosts = postService.searchPosts(query);

            List<PostDTO> postDTOs = new ArrayList<>();
            for (Post post : foundPosts) {
                PostDTO postDTO = PostDTO.toDto(post);
                postDTOs.add(postDTO);
            }


            return new Response<>("게시판 검색 성공", "게시판 검색 성공", postDTOs);
        }catch (Exception e){
            log.info("게시판 검색 실패");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());

        }
    }

    @ApiOperation(value = "채널 이름 검색", notes = "채널 이름 검색")
    @GetMapping("/channel/search")
    public Response searchChannels(@RequestParam("query") String query) {
        List<Channel> foundChannels = channelService.searchChannels(query);
        List<ChannelDTO> channelDTOs = new ArrayList<>();
        for (Channel channel : foundChannels) {
            ChannelDTO channelDTO = ChannelDTO.toDto(channel);
            channelDTOs.add(channelDTO);
        }

        return new Response<>("채널 검색 성공", "채널 검색 성공", channelDTOs);
    }




}

