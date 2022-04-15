package jpabook2.jpashop2.repository;


import jpabook2.jpashop2.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    /**
     * 아이템 저장
    * @param item
     */
    public void save(Item item){
        //처음 저장할때는 ID가 아직 없음 - 영속화 되어야 그떄 id가 생김
        if(item.getId() == null){
            em.persist(item);
        }
        //편하지만, 단순한 경우에만 적용가능하고, 웬만하면 변경감지를 사용할 것
        else{
            //업데이트라고 생각
            em.merge(item);
        }
    }

    /**
     * 아이템 하나 조회
     */
    public Item findOne(Long id){
        return em.find(Item.class,id);
    }

    /**
     * 아이템 전체 조회
     */
    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
