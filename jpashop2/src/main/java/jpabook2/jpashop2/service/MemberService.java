package jpabook2.jpashop2.service;

import jpabook2.jpashop2.domain.Member;
import jpabook2.jpashop2.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//모든 데이터 변경이나 로직들은 트랜잭션 안에서 실행되어야 됨
//데이터 변경은 반드시 트랜잭션이 있어야됨.
//트랜잭셔널 어노테이션을 쓰면, 안에 있는 메서드들에 전부 트랜잭션이 걸림
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor // final이 붙은 필드로만 생성자를 만듦
public class MemberService {

    private final MemberRepository memberRepository;


    /**
     * 회원 가입
     */
    //쓰기 연산이므로 별도로 설정(디폴트가 readOnly=false)
    //클래스 레벨에 설정하고, 좀더 구체적으로 메서드레벨에 설정하면, 메서드 레빌이 우선순위가 더 앞서있음
    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 중복 검증
     */
    private void validateDuplicateMember(Member member){
        //이렇게 하면 멀티스레드 환경에서 문제가 됨 - 따라서 디비에 유니크 제약조건을 걸어서 한번더 검증
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }

    /**
     * 회원 전체 조회
     */
    //readOnly = true로 주면, 조회같은 읽기 연산에서 최적화를 함.
//    @Transactional(readOnly = true)
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    /**
     * 회원 단건 조회
     */

    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }

    /**
     * 회원 정보 수정
     */
    @Transactional
    public void update(Long id, String name){
        Member member = memberRepository.findOne(id);
        member.setName(name);

    }
}
