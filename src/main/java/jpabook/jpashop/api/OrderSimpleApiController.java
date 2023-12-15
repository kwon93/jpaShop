package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simpleQuery.OrderSimpleQueryRepository;
import jpabook.jpashop.repository.order.simpleQuery.SimpleOrderQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * xxxToOne 관계 성능 최적화
 * Order
 * Order -> Member (lazy)
 * Order -> Delivery (lazy)
 */

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    //Entiy를 반환 : 무한 루프 문제 발생.
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        return orderRepository.findAllByCriteria(new OrderSearch());
    }




    //DTO를 반환 : N + 1 문제 발생. {Query 5개 발생}
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());

        return orders.stream().map(SimpleOrderDto::new).collect(Collectors.toList());
    }




    //fetchJoin : N + 1 문제 해결. {Query 1개 발생}
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        return orders.stream().map(SimpleOrderDto::new).collect(Collectors.toList());
    }

    //fetchJoin : DTO변환을 쿼리에서 바로 뽑기 : But 쿼리에서 DTO가 들어가기에 재사용성이 낮다 V3와 장단점이 있다.
    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto> ordersV4(){
        return orderSimpleQueryRepository.findOrderDtos();

    }

    @Data
    public static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
        }

    }



}
