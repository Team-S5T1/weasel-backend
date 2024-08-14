//package exam.master.service;
//
//import static org.junit.Assert.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import exam.master.domain.Member;
//import exam.master.domain.Prompt;
//import java.util.List;
//import java.util.UUID;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@Transactional
//public class PromptServiceTest {
//  @Autowired
//  MemberService memberService;
//  @Autowired
//  PromptService promptService;
//  @Autowired
//  private static Log log = LogFactory.getLog(PromptServiceTest.class);
//
//  @Test
//  @Rollback(false)
//  public void 프롬프트_처음_추가() throws Exception {
//    //Given
//    Member member = new Member();
//    member.setEmail("kim@111");
//    member.setPassword("1111");
//    UUID saveId = memberService.join(member);
//    Prompt prompt = new Prompt();
//    prompt.setPrompt("testPrompt");
//    prompt.setAnswer("testAnswer");
//    prompt.setPhoto("testPhoto");
//    //When
//    promptService.addPrompt(prompt, null, member);
//    //Then
//  }
//
//  @Test
//  @Rollback(false)
//  public void 프롬프트_추가() throws Exception {
//    //Given
//    Member member = new Member();
//    member.setEmail("kim@111");
//    member.setPassword("1111");
//    UUID saveId = memberService.join(member);
//    Prompt prompt = new Prompt();
//    prompt.setPrompt("testPrompt");
//    prompt.setAnswer("testAnswer");
//    prompt.setPhoto("testPhoto");
//    Prompt prompt1 = new Prompt();
//    prompt1.setPrompt("testPrompt1");
//    prompt1.setAnswer("testAnswer1");
//    prompt1.setPhoto("testPhoto1");
//    //When
//    Prompt addPrompt = promptService.addPrompt(prompt, null, member);
//    prompt1.setHistory(addPrompt.getHistory());
//    promptService.addPrompt(prompt1, addPrompt.getHistory().getHistoryId(), member);
//    //Then
//  }
//
//  @Test
//  @Rollback(false)
//  public void 히스토리_리스트() throws Exception {
//    //Given
//    Member member = new Member();
//    member.setEmail("kim@111");
//    member.setPassword("1111");
//    UUID saveId = memberService.join(member);
//    Prompt prompt = new Prompt();
//    prompt.setPrompt("testPrompt");
//    prompt.setAnswer("testAnswer");
//    prompt.setPhoto("testPhoto");
//    Prompt prompt1 = new Prompt();
//    prompt1.setPrompt("testPrompt1");
//    prompt1.setAnswer("testAnswer1");
//    prompt1.setPhoto("testPhoto1");
//    Prompt addPrompt = promptService.addPrompt(prompt, null, member);
//    prompt1.setHistory(addPrompt.getHistory());
//    promptService.addPrompt(prompt1, addPrompt.getHistory().getHistoryId(), member);
//    //When
//    Member loginUser = memberService.findByEmailAndPassword("kim@111", "1111");
//    //Then
////    log.debug(loginUser.getHistories().getFirst().getTitle());
//    System.out.println(loginUser.getHistories().getFirst().getHistoryId());
//    System.out.println(loginUser.getHistories().getFirst().getTitle());
//    System.out.println(loginUser.getHistories().getFirst().getCreatedDate());
//  }
//
//  @Test
//  @Rollback(false)
//  public void 프롬프트_리스트() throws Exception {
//    //Given
//    Member member = new Member();
//    member.setEmail("kim@111");
//    member.setPassword("1111");
//    UUID saveId = memberService.join(member);
//    Prompt prompt = new Prompt();
//    prompt.setPrompt("testPrompt");
//    prompt.setAnswer("testAnswer");
//    prompt.setPhoto("testPhoto");
//    Prompt prompt1 = new Prompt();
//    prompt1.setPrompt("testPrompt1");
//    prompt1.setAnswer("testAnswer1");
//    prompt1.setPhoto("testPhoto1");
//    Prompt addPrompt = promptService.addPrompt(prompt, null, member);
//    prompt1.setHistory(addPrompt.getHistory());
//    promptService.addPrompt(prompt1, addPrompt.getHistory().getHistoryId(), member);
//    //When
//    List<Prompt> list =  promptService.findByHistoryId(addPrompt.getHistory().getHistoryId());
//    //Then
//    System.out.println(list.get(0).getPrompt());
//    System.out.println(list.get(1).getPrompt());
//  }
//}