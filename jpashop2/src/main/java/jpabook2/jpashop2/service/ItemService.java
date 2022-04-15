package jpabook2.jpashop2.service;

import jpabook2.jpashop2.domain.item.Book;
import jpabook2.jpashop2.domain.item.Item;
import jpabook2.jpashop2.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    @Transactional
    public Item updateItem(Long itemId, String name, int price, int stockQuantity){

        //더티 체킹을 이용해서 업데이트를 하는 방법 - merge가 하는 일이 아래와 같음.
        //하지만 merge는 http 메서드의 put 처럼 완전히 갈아 끼우기 떄문에, 업데이트 하지 않을 필드가 있다면, 그 필드는 null로 들어가게 됨.
        //그래서 merge를 쓰면 안됨.
        //실제로 코드를 짤때는 아래처럼 하면 안됨. - set으로 바꾸는 것이 아닌, 별도의 의미 있는 메서드를 만들어야 됨.
        Item findItem = itemRepository.findOne(itemId);
        //findItem.change(price,name,stockQuantity); => 이런식으로 해야지, 어디서 변경이 일어나는지 추적하기도 좋음
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);

        return findItem;
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }
}
