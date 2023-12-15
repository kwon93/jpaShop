package jpabook.jpashop.repository.order.simpleQuery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    //Repository가 Entity가 아닌 DTO를 조회
    public List<SimpleOrderQueryDto> findOrderDtos() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.simpleQuery.SimpleOrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o"+
                        " join o.member m" +
                        " join o.delivery d", SimpleOrderQueryDto.class
        ).getResultList();
    }
}


