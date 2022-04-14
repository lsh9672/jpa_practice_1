package jpabook2.jpashop2.service;

import jpabook2.jpashop2.domain.Address;
import jpabook2.jpashop2.domain.Member;
import jpabook2.jpashop2.domain.Order;
import jpabook2.jpashop2.domain.OrderStatus;
import jpabook2.jpashop2.domain.item.Book;
import jpabook2.jpashop2.domain.item.Item;
import jpabook2.jpashop2.exception.NotEnoughStockException;
import jpabook2.jpashop2.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @DisplayName("상품주문")
    @Test
    public void 상품주문() throws Exception{
        //give
        Member member = createMember();

        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER,getOrder.getStatus(),"상품 주문시 상태는 ORDER");
        assertEquals(1,getOrder.getOrderItems().size(),"주문한 상품 종류 수가 정확해야 한다.");
        assertEquals(10000 * orderCount,getOrder.getTotalPrice(),"주문 가격은 가격 * 수량이다.");
        assertEquals(8,book.getStockQuantity(),"주문 수량만큼 제고가 줄어야 한다.");
    }



    @DisplayName("상품주문 재고수량 초과")
    @Test
    public void 상품주문_재고수량초과() throws Exception{
        //give
        Member member = createMember();

        Item item = createBook("시골 JPA", 10000, 10);

        int orderCount = 11;

        //when

        //then
        Throwable errorMessage = assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), item.getId(), orderCount);
        });
    }

    @DisplayName("주문취소")
    @Test
    public void 주문취소() throws Exception{
        //give
        Member member = createMember();
        Book item = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL,getOrder.getStatus(),"주문 취소시 상태는 CANCEL이다.");
        assertEquals(10,item.getStockQuantity(),"주문이 취소된 상품은 그만큼 재고가 증가해야 한다.");
    }


    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울","경기","123-123"));
        em.persist(member);
        return member;
    }


}