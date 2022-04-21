package jpabook2.jpashop2.repository;

import jpabook2.jpashop2.domain.Member;
import jpabook2.jpashop2.domain.Order;
import jpabook2.jpashop2.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    /**
     * JPQL 문자열 파싱으로 동적쿼리를 짜면 아래처럼 정말 어려움
     * @param orderSearch
     * @return
     */
    public List<Order> findAllByString(OrderSearch orderSearch){
//        em.createQuery("select o from Order o join o.member m" +
//                                " where o.status = :status "+
//                                " and m.name like :name", Order.class)
//                .setParameter("status", orderSearch.getOrderStatus())
//                .setParameter("name", orderSearch.getMemberName())
//                .setMaxResults(1000)//최대 1000건
//                .getResultList();
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    /**
     * JPA Criteria
     * 안씀 - 이거 써도 마찬가지로 어려움(실무에서 사용 불가)
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);}
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대1000건
        return query.getResultList();
    }

//    public List<Order> findAll(OrderSearch orderSearch){
//
//    }

    /**
     * Order를 조회한다는 것을 건들지 않고 내부에서 쿼리만 바꿔서 최적화
     * 따라서 재사용성이 좋음 - Order를 가져왔기 때문에 원하는 DTO로 만들서 사용할수 있음
     */
    public List<Order> findAllWithMemberDelivery(){
        return em.createQuery(
                "select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class)
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset,int limit){
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    /**
     * 엔티티의 모든 값을 가져오는것이 아닌 필요한 것만 Select 했지만,
     * 외부에서 봤을떄 Order를 조회하는 것이 아닌 DTO를 만들어서 그것을 조회하는 것이므로, 재사용성이 좋지 않음
     * 코드가 지저분하고 엔티티로 조회한것이 아니라 set으로 더티체킹을 못함.
     * 좋아보이지만 별로임(API 스펙이 바뀌면 이 레파지토리 계층을 뜯어 고쳐야됨)'택
     * 이게 성능이 더 좋을 것 같지만, 결국 성능은 join부분이므로 위에꺼가 더 나은 선
     */
//    public List<OrderSimpleQueryDto> findOrderDtos() {
//        return em.createQuery(
//                "select new jpabook2.jpashop2.repository.OrderSimpleQueryDto(o.id, m.name ,o.orderDate,o.status, d.address) from Order o" +
//                " join o.member m" +
//                " join o.delivery d", OrderSimpleQueryDto.class)
//                .getResultList();
//    }

    /**
     * 그냥 페치조인을 하면 중복되는 값이 계속발생함
     * 따라서 distint를 붙여줘야됨
     * JPQL의 distinct는 해당 객체(아래에서 Order)를 가져올떄 중복이 되면, 제거
     * 1. 디비에 distinct키워드 날려주고,
     * 2. 엔티티가 중복이면, 중복을 걸러서 컬렉션에 담아줌(아래에서 Order의 중복을 제거함.)
     */
    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderItems oi" +
                " join fetch oi.item i",Order.class)
                .getResultList();
    }
}
