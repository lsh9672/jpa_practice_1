package jpabook2.jpashop2.controller;


import jpabook2.jpashop2.domain.Address;
import jpabook2.jpashop2.domain.Member;
import jpabook2.jpashop2.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String createForm(@Valid MemberForm form, BindingResult bindingResult){

        //에러가 있으면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            return "members/createMemberForm";
        }
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);

        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model){

        //현재 요구사항이 간단하고, 서버에서 랜더링 하는 것이라서 엔티티를 그대로 넘겼는데,
        //api에서는 이유를 불문하고 엔티티를 웹으로 반환하면 안됨. - (예를 들면 엔티티에 패스워드를 추가하면, 그정보도 같이 나감)
        //가장 좋은 방법은 DTO를 만들어서 그걸로 반환하는 것이 좋음
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);

        return "members/memberList";
    }
}
