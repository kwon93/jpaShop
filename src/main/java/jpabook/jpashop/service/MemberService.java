package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//@Transactional //트랜잭션안에서 데이터 변경이 진행되어야 한다.  (스프링이 제공하는 어노테이션을 사용하는편이 편하다.)
@Transactional(readOnly = true) //현 서비스에는 조회 메서드가 더 많기에 읽기 전용을 기본값으로 클래스에 지정
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    //회원가입
    @Transactional //readOnly = false , false 가 기본값이다. 메서드에 지정하면 우선순위가 됨.
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        //멤버 중복에관한 예외 처리
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
//    @Transactional(readOnly = true) // JPA는 조회하는곳에서는 성능을 더 최적화한다.읽기전용
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    //회원 단건 조회.
    public Member findOne(Long id){
        return memberRepository.findOne(id);
    }


    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
