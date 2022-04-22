package jpabook2.jpashop2.service;

import jpabook2.jpashop2.domain.Member;
import jpabook2.jpashop2.repository.MemberRepositoryOld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
//통합테스트
//테스트 실행시에 스프링과 엮어서 한다. 없어도 잘돌아감
//이는 단위테스트 시에, 공통적으로 사용할 기능을 만들어서 테스트시에 해당기능을 확장해서 쓸수 있도록 하는 것
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired
    MemberRepositoryOld memberRepository;

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
        //Throwable은 예외를 처리하기 위한 최상위 클래스이다.(약간 자바에서 클래스타입을 잘 모르면 무지성 Object해도 되는것과 비슷)
        Throwable errorMessage = assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        });

        //예외시 발생하는 메시지도 맞는지 확인
        Assertions.assertEquals("이미 존재하는 회원입니다",errorMessage.getMessage());
    }

}