package jpabook2.jpashop2.service.query;


import jpabook2.jpashop2.api.OrderApiController;
import jpabook2.jpashop2.domain.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static java.util.stream.Collectors.toList;


/**
 * open in view 기능을 껐을떄 지연로딩이 정상적으로 동작하게 하려면
 * 아래와 같이 트랜잭션 안에서 서비스 부분이 동작하게 해줘야됨
 * 쿼리DTO 같은것도 여기로 다 가져와야됨
 */

@Transactional(readOnly = true)
public class OrderQueryService {

//    @GetMapping("/api/v3/orders")
//    public List<OrderApiController.OrderDto> orderV3() {
//        List<Order> orders = orderRepository.findAllWithItem();
//
//        //레퍼런스를 찍어보면 동일함, JPA는 id값(pk값)이 같으면 같은 객체임
////        for(Order order:orders){
////            System.out.println("order ref= " + order + " id=" + order.getId());
////        }
//
//        List<OrderApiController.OrderDto> result = orders.stream()
//                .map(o -> new OrderApiController.OrderDto(o))
//                .collect(toList());
//
//        return result;
//    }
}
