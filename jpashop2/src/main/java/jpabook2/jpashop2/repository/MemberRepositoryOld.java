package jpabook2.jpashop2.repository;

import jpabook2.jpashop2.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryOld {
    //엔티티매니저를 주입받음
    //스프링 데이터 JPA를 쓰면, 이 별도의 어노테이션 없이 일반적인 @Autowired를 쓸수 있음
    //@Autowired를 쓸수 있다는 것은, 롬복을 이용한 생성자 주입이 된다는 것
//    @PersistenceContext
//    private EntityManager em;

    private final EntityManager em;

    //만약 엔티티매니저 팩토리를 주입받고 싶으면 아래와 같이 하면 되는데, 이렇게 쓸일은 없
//    @PersistenceUnit음
//    private EntityManagerFactory emf;

    //회원정보 저장
    public void save(Member member){
        em.persist(member);
    }

    //id로 조회
    public Member findOne(Long id){
        return em.find(Member.class,id);
    }

    //모든 회원 조회(회원 목록)
    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    //이름으로 검색 - 회원가입시 중복여부 확인용으로 씀
    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name",Member.class)
                .setParameter("name",name)
                .getResultList();

    }


}
