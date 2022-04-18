package jpabook2.jpashop2.api;

import jpabook2.jpashop2.domain.Member;
import jpabook2.jpashop2.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    /**
     * 리스트를 바로 반환하면, 리스트 형태로 나가기 때문에, 아래와 같이 별도의 객체로 감싸서 내보내야 됨
     * ex. [{"data";"q"}] 이런식으로 배열형태가 되서 확장이 불가능해짐
     */
    @GetMapping("/api/v2/members")
    public Result membersV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream().map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect);

    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }


    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 엔티티를 직접사용하는 것은 문제가 많기 때문에, 별도의 DTO를 만들어서 사용합
     * 별도의 DTO를 쓰면, 엔티티가 바뀌더라도, API스펙이 바뀌지않는다는 장점이 있음
     * Validation도 엔티티가 아닌 DTO에 넣어서 스펙별로 Validation을 적용할수 있음
     * 이러한 방식이 API방식의 정석
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 보통 등록과 수정은 스펙이 다르기 떄문에 별도의 DTO를 사용하는 것이 좋음
     */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request){

        memberService.update(id,request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(),findMember.getName());
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }

    /**
     * DTO에는 롬복 막씀 - 딱히 내부에 로직이 있는것도 아니고, 데이터만 왔다갔다 하는 거라 문제가 안됨
     */
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest{
        private String name;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id){
            this.id = id;
        }
    }

}
