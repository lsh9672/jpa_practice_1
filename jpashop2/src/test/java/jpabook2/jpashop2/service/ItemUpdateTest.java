package jpabook2.jpashop2.service;


import jpabook2.jpashop2.domain.item.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;

@SpringBootTest
class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @DisplayName("업데이트 테스트")
    @Test
    public void updateTest() throws Exception{
        //give
        Book book = em.find(Book.class, 1L);
        //when
        book.setName("asdf");

        //then
    }
}
