package jpabook2.jpashop2.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * repository는 가급적 순수한 엔티티 조회에만 사용
 */

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {


    private final EntityManager em;

    /**
     * 엔티티의 모든 값을 가져오는것이 아닌 필요한 것만 Select 했지만,
     * 외부에서 봤을떄 Order를 조회하는 것이 아닌 DTO를 만들어서 그것을 조회하는 것이므로, 재사용성이 좋지 않음
     * 코드가 지저분하고 엔티티로 조회한것이 아니라 set으로 더티체킹을 못함.
     * 좋아보이지만 별로임(API 스펙이 바뀌면 이 레파지토리 계층을 뜯어 고쳐야됨)'택
     * 이게 성능이 더 좋을 것 같지만, 결국 성능은 join부분이므로 위에꺼가 더 나은 선
     */
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                        "select new jpabook2.jpashop2.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name ,o.orderDate,o.status, d.address) from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }


}
