package jpabook2.jpashop2;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.security.RunAs;

import static org.junit.jupiter.api.Assertions.*;

//junit5에서는 테스트 클래스에 public을 안붙여도 됨
//junit5 에서는 @RunWith(SpringRunner.class) 대신에 아래와 같이 사용해주어야 된다.
@ExtendWith(SpringExtension.class)
@SpringBootTest
class MemberRepositoryTest {

    //테스트이므로 필드 주입을 써도됨
    @Autowired MemberRepository memberRepository;

    //트랜잭션은 스프링꺼 쓰는게 좋음(안에 쓸수 있는 기능이 많음)
    @DisplayName("멤버 테스트")
    @Test
    @Transactional //이게 테스트에 있으면, 테스트가 끝나고 롤백함(그래야 다음테스트도 할 수 있음)
    @Rollback(false)//이 어노테이션 주면, 롤백안하고 커밋함.
    public void testMember() throws Exception{
        //give
        Member member = new Member();
        member.setUsername("memberA");

        //when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.find(saveId);

        //then

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);//같은 트랜잭션안에서,동작하기 때문, 같은 영속성 컨텍스트 안에서는 같은 객체로 관리되는 것을 보증

    }

}