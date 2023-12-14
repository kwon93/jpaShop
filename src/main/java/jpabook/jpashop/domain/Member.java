package jpabook.jpashop.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded //내장 타입
    private Address address;

    @OneToMany(mappedBy = "member") //order table에 있는 member field에 의해 mapping 됬다는 뜻. 거울 (읽기 전용) 이쪽에서 무언가 수정을해도 외래 키 값이 변경되지 않는다.
    private List<Order> orders = new ArrayList<>();
}
