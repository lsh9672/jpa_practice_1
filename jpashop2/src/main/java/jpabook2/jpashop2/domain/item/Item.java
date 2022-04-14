package jpabook2.jpashop2.domain.item;

import jpabook2.jpashop2.domain.Category;
import jpabook2.jpashop2.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();


    //==비즈니스 로직==//
    /**
     * 아이템 수량을 추가하는 로직은 아이템 엔티티 안에 있는 것이 응집도 면에서도 좋음
     * 수량 추가에 필요한 필드 값들을 전부 이 엔티티가 가지고 있기 때문
     * stock 증가
     */
    public void addStock(int quantity){
        this.stockQuantity+=quantity;
    }
    /**
     * stock 감소
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }

        this.stockQuantity = restStock;
    }

}
