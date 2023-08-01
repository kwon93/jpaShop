package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional //테스트때는 롤백됨.
class MemberRepositoryTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("회원가입")
//    @Rollback(value = false) //DB에 진짜 들어가는지 확인하고 싶을 때 사용하자.
    void test1() throws Exception{
        //given
        Member member = new Member();
        member.setName("kwon");

        //when
        Long savedId = memberService.join(member);

        //then
        em.flush(); // 플러시하게되면 영속성 컨텍스트에 있는 데이터를 디비로 쿼리를 날린다. (인서트문을 확인할 수 있다.)
        assertThat(member).isEqualTo(memberRepository.findOne(savedId));
    }


    @Test
    @DisplayName("중복회원예외")
    void test2() throws Exception{
        //given
        Member member1 = new Member();
        member1.setName("kwon");
        Member member2 = new Member();
        member2.setName("kwon");

        //when
        memberService.join(member1);
        try {
            memberService.join(member2); //중복 예외가 발생해야한다.
        }catch (IllegalStateException e){
            System.out.println("예외가 발생했습니다.");
            return;
        }

        //then
        org.junit.jupiter.api.Assertions.fail("예외가 발생해야한다.");
    }

}