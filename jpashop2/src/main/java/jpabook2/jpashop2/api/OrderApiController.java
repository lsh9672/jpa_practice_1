package jpabook2.jpashop2.api;

import jpabook2.jpashop2.domain.Address;
import jpabook2.jpashop2.domain.Order;
import jpabook2.jpashop2.domain.OrderItem;
import jpabook2.jpashop2.domain.OrderStatus;
import jpabook2.jpashop2.repository.OrderRepository;
import jpabook2.jpashop2.repository.OrderSearch;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    /**
     * 주문서의 내용을 전부 출력
     * 이 방법은 엔티티를 직접 노출시키는 것이므로 좋은 방법이 아님
     */
    @GetMapping("/api/v1/orders")
    public List<Order> orderV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            //한번에 정보를 다 출력하고 싶어서 강제초기화를 시킴
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); //Lazy로딩이므로 강제 초기화 필요
        }

        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return collect;
    }

    /**
     * V2의 경우,쿼리가 굉장히 많이 나감
     * order를 조회하면, member가 연관관계로 있어서 member를 조회하고, orderItem을 조회하고, 여기서 연관된 item을 다시 조회하고 ...
     * 쿼리가 여러번 호출되어서 네트워크를 여러번 타기 때문에 굉장한 성능적 이슈가 생김
     * 이를 페치조인을 이용해서 한번에 가져오는 식으로 해결함
     * 단점: 조인하면 데이터가 뻥튀기 되어서 같은게 여러개 나옴
     * 추가 단점 : 1대다를 페치조인하면 페이징 불가(최대단점) - 페이징 써도 안
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        //레퍼런스를 찍어보면 동일함, JPA는 id값(pk값)이 같으면 같은 객체임
//        for(Order order:orders){
//            System.out.println("order ref= " + order + " id=" + order.getId());
//        }

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> orderV3_page(@RequestParam(value="offset",defaultValue = "0") int offset,
                                       @RequestParam(value="limit",defaultValue = "100") int limit) {

        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit);

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }


    /**됨
     * 엔티티를 노출시키지 말라는 뜻은 단순히 DTO로 감싸서 보내라는게 아님
     * 엔티티에 대한 의존을 완전히 끊어야됨.
     * 아래 코드를 보면 orderItem이 들어가있는데 이것도 DTO로 변환해야됨
     * (현재 아래 코드는 orderItem도 Dto를 적용한 버전)
     * 단 Address같은 value object는 노출해도됨(이런값들은 딱히 바뀔일 업음)
     */
    @Getter
    static class OrderDto{

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        //DTO안에 엔티티가 있으면 안됨

        private List<OrderItemDto> orderItems;

        public OrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }

    }
    @Getter
    static class OrderItemDto{

        private String itemName;//상품명
        private int orderPrice;//주문 가격
        private int count;//주문 수량

        public OrderItemDto(OrderItem orderItem){
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }

    }
}
