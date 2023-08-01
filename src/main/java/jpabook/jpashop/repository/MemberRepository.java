package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em; //스프링이 엔티티매니저를 인젝션해줌. 엔티티 매니저 팩토리에서 직접 생성할 필요가 없다.

    public void save(Member member){
      em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class, id); // 멤버 반환
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class).getResultList();
        // getResultList() : 멤버를 리스트로 만들어줌.
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name) // :name 과 바인딩
                .getResultList();
    }

}
