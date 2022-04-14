package jpabook2.jpashop2.domain;

import jpabook2.jpashop2.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;//주문가격

    private int count;//주문 수량

    //위의 롬복기능과 동일 (@NoArgsConstructor)
//    //문제가 발생하는 상황을 막기 위해
//    protected  OrderItem(){
//
//    }

    /**
     * 생성 메서드
     */
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    /**
     * 비즈니스 로직
     */

    //상품취소
     public void cancel(){
         getItem().addStock(count);
     }

    /**
     * 조회 로직
     */
     //주문 상 전체 가격 조회
     public int getTotalPrice(){
         return getOrderPrice() * getCount();
     }

}
