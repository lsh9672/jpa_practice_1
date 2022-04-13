package jpabook2.jpashop2.service;

import jpabook2.jpashop2.domain.Member;
import jpabook2.jpashop2.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.security.RunAs;

import static org.junit.jupiter.api.Assertions.*;
//통합테스트
//테스트 실행시에 스프링과 엮어서 한다. 없어도 잘돌아감
//이는 단위테스트 시에, 공통적으로 사용할 기능을 만들어서 테스트시에 해당기능을 확장해서 쓸수 있도록 하는 것
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @DisplayName("회원가입")
    @Test
    public void 회원_가입() throws Exception{
        //give
        Member member = new Member();
        member.setName("kim");

        //when
        Long saveId = memberService.join(member);

        //then
        Assertions.assertEquals(member,memberRepository.findOne(saveId));
    }

    @DisplayName("중복 회원 예외")
//    @Test(expected = IllegalStateException.class) // junit4에서는 이처럼 예외가 터지는 경우를 테스트함
    @Test
    public void 중복_회원_예외() throws Exception{
        //give
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);

        //then
        //예외가 터져야됨
        //junit5에서는 이와같이 예외가 터지는 테스트의 경우, 람다를 이용한다.
        Assertions.assertThrows(IllegalStateException.class, () -> {memberService.join(member2);});
    }

}