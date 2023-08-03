package jpabook.jpashop.repository;

import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

@Data
public class OrderSearch {

    private String memberName; //회원의 이름
    private OrderStatus orderStatus;

}
