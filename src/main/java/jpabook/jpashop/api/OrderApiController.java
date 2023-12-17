package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDTO;
import jpabook.jpashop.repository.order.query.OrderQueryDTO;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    // Entity를 반환하는 V1
    @GetMapping("api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());

        //Lazy 강제 초기화
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        
        return all;
    }

    //DTO로 반환 V2 / DB로 11개 쿼리 전송
    @GetMapping("api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        return orders.stream().map(OrderDto::new).collect(toList());
    }



    //DTO로 반환 fetchJoin 최적화 진행 / select distinct DB로 쿼리 1개 전송 단점: 페이징 불가능 (Sorting을 DB에서 하지않고 메모리에서 실행하게되어 다량 데이터 존재시 outOfMemory 발생)
    @GetMapping("api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> allWithItem = orderRepository.findAllWithItem();
        return allWithItem.stream().map(OrderDto::new).collect(toList());
    }


    //Paging 처리 , default_batch_fetch_size 적용
    @GetMapping("api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0")int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit ){

        return orderRepository.findAllWithMemberDelivery(offset,limit).stream()
                .map(OrderDto::new)
                .collect(toList());
    }

    //Query에서 DTO 바로 변환  / N + 1 문제 발생
    @GetMapping("api/v4/orders")
    public List<OrderQueryDTO> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    //N + 1 해결 1: 1
    @GetMapping("api/v5/orders")
    public List<OrderQueryDTO> ordersV5(){
        return orderQueryRepository.findAllByDto_Optimization();
    }

    //Query 1개 하지만 애플리케이션 추가 작업이 큼, 페이징 불가능
    @GetMapping("api/v6/orders")
    public List<OrderFlatDTO> ordersV6(){
        return orderQueryRepository.findAllByDto_flat();
    }



    //DTO에는 엔티티가 그대로 나가면 안된다. 무조건 DTO값만 나가게끔한다. Entity 의존을 없애자
    @Data
    public static class OrderDto{

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDTO> orderItems;

        public OrderDto(Order order){
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
            this.orderItems = order.getOrderItems().stream().map(OrderItemDTO::new).collect(toList());
        }
    }

    @Data
    public static class OrderItemDTO{

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDTO(OrderItem item){
            this.itemName = item.getItem().getName();
            this.orderPrice = item.getOrderPrice();
            this.count = item.getCount();
        }
    }
}
