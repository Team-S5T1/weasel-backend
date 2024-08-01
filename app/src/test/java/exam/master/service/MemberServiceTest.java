package exam.master.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import exam.master.domain.Member;
import exam.master.repository.MemberRepository;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

  @Autowired
  MemberService memberService;
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  private static Log log = LogFactory.getLog(MemberServiceTest.class);

  @Test
  @Rollback(false)
  public void 회원가입() throws Exception {
    //Given
    Member member = new Member();
    member.setEmail("kim");
    member.setPassword("1111");
    //When
    UUID saveId = memberService.join(member);
    //Then
    assertEquals(member, memberRepository.findById(saveId));
  }
  //  @Test(expected = IllegalStateException.class)
  @Test
  public void 중복_회원_예외() throws Exception {
    //Given
    Member member1 = new Member();
    member1.setEmail("kim");
    member1.setPassword("1111");
    Member member2 = new Member();
    member2.setEmail("kim");
    member2.setPassword("1111");
    //When
    memberService.join(member1);
    memberService.join(member2); //예외가 발생해야 한다.
    //Then
    fail("예외가 발생해야 한다.");
  }

  @Test
  @Rollback(false)
  public void 회원수정() throws Exception {
    //Given
    Member member = new Member();
    member.setEmail("kim@111");
    member.setPassword("1111");
    UUID saveId = memberService.join(member);
    //When
    member.setEmail("kim@222");
    UUID updateId = memberService.update(member);
    //Then
    assertEquals(member, memberRepository.findById(updateId));
  }

  @Test
  @Rollback(false)
  public void 회원탈퇴() throws Exception {
    //Given
    Member member = new Member();
    member.setEmail("kim@111");
    member.setPassword("1111");
    UUID saveId = memberService.join(member);
    //When
    UUID updateId = memberService.delete(member);
    //Then
    assertEquals(member, memberRepository.findById(updateId));
  }

  @Test
  @Rollback(false)
  public void 로그인() throws Exception {
    //Given
    Member member = new Member();
    member.setEmail("kim@111");
    member.setPassword("1111");
    UUID saveId = memberService.join(member);
    //When
    Member loginUser = memberService.findByEmailAndPassword(member.getEmail(), member.getPassword());
    //Then
    System.out.println(loginUser.getEmail());
    System.out.println(loginUser.getPassword());
    System.out.println(loginUser.getMemberId());
  }
}