package exam.master.service;

import exam.master.domain.Member;
import exam.master.dto.LogInRequest;
import exam.master.dto.MemberDTO;
import exam.master.repository.HistoryRepository;
import exam.master.repository.MemberRepository;
import exam.master.status.MemberStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

  private static final Log log = LogFactory.getLog(MemberService.class);

  private final MemberRepository memberRepository;
  private final HistoryRepository historyRepository;

  // 회원 등록
  @Transactional
  public MemberDTO joinMember(MemberDTO memberDTO) {

    Member member = new Member();
    member.setEmail(memberDTO.getEmail());

    System.out.println("============");
    System.out.println(" memberDTO.getEmail() : "+ memberDTO.getEmail());
    //중복된 이메일로 가입되있는지 확인
    validateDuplicateMember(memberDTO.getEmail());

    member.setPassword(memberDTO.getEmail());
    member.setStatus(memberDTO.getStatus());
    member.setProfilePhoto(memberDTO.getProfilePhoto());

    // 새로운 멤버를 저장
    Member savedMember = memberRepository.save(member);
    return convertToDTO(savedMember);
  }

  // 회원정보 수정

  @Transactional
  public MemberDTO updateMember(UUID memberId, MemberDTO updatedMemberDTO){
    Optional<Member> optionalMember = memberRepository.findById(memberId);
    if (!optionalMember.isPresent()) {
      throw new RuntimeException("Member not found");
    }
    Member existingMember = optionalMember.get();

    if (updatedMemberDTO.getEmail() != null) {
      existingMember.setEmail(updatedMemberDTO.getEmail());
    }
    if (updatedMemberDTO.getProfilePhoto() != null) {
      existingMember.setProfilePhoto(updatedMemberDTO.getProfilePhoto());
    }
    // 비밀번호 업데이트 시 추가 검증 로직이 필요할 수 있음
    if (updatedMemberDTO.getPassword() != null) {
      existingMember.setPassword(updatedMemberDTO.getPassword());
    }

    // 변경된 내용을 저장
    Member updatedMember = memberRepository.save(existingMember);
    return convertToDTO(updatedMember);

  }
  // 회원탈퇴

  @Transactional
  public void deleteMember(UUID memberId){
    if (!memberRepository.existsById(memberId)){
      throw new RuntimeException("존재하지 않는 회원입니다");
    }
    memberRepository.deleteById(memberId);
  }
  // 회원정보 조회


  public List<Member> findMember(){
    return memberRepository.findAll();
  }

  public MemberDTO getMemberById(UUID memberId) {
    Optional<Member> optionalMember = memberRepository.findById(memberId);
    if (!optionalMember.isPresent()) {
      throw new RuntimeException("존재하지 않는 회원입니다");
    }
    Member member = optionalMember.get();
    return convertToDTO(member);
  }


  /**
   * 로그인 기능
   *  return member - 로그인 성공
   *  return null - 로그인 실패
  */
  public Member login(LogInRequest req){

    // 해당 이메일로 회원인지 확인
    Member checkMember = memberRepository.findByEmail(req.getEmail());

    // Email과 일치하는 Member가 없으면 null return
    if(checkMember.getEmail().isEmpty()){
      return null;
    }

    Member existMember = memberRepository.findByEmailAndPassword(req.getEmail(), req.getPassword());

    // 찾아온 Member의 password와 입력된 password가 다르면 null return
    if (existMember == null || !existMember.getPassword().equals(req.getPassword())) {
      return null;
    }

    return existMember;
  }

  public Member findByEmailAndPassword (String email, String password){
    Member loginUser = memberRepository.findByEmailAndPassword(email, password);

    // 로그인 하면서 프롬프트 창으로 리다이렉트 하기 때문에 히스토리 리스트를 가져온다
    loginUser.setHistories(historyRepository.findAll(loginUser.getMemberId()));
    return loginUser;
  }

  private LogInRequest convertToLoginRequest(Member member) {
    LogInRequest logInRequest = new LogInRequest();

    logInRequest.setEmail(member.getEmail());
    logInRequest.setPassword(member.getPassword());

    return logInRequest;
  }


  // Member api Response에 사용될 DTO로 변환
  private MemberDTO convertToDTO(Member member){
    MemberDTO memberDTO = new MemberDTO();

    memberDTO.setMemberId(member.getMemberId());
    memberDTO.setStatus(member.getStatus());
    memberDTO.setEmail(member.getEmail());
    memberDTO.setProfilePhoto(member.getProfilePhoto());

    return memberDTO;
  }

  /* 아래는 테스트로 썻던 오브젝트 */
  // 등록
/*  @Transactional
  public UUID join(Member member){

    // 중복 회원 검증
    validateDuplicateMember(member);
    memberRepository.save(member);
    return member.getMemberId();
  }*/

  // 수정
  @Transactional
  public UUID update(Member member){
    memberRepository.update(member);
    return member.getMemberId();
  }

  // 탈퇴
  @Transactional
  public UUID delete(Member member){
    member.setStatus(MemberStatus.DELETE);
    memberRepository.update(member);
    return member.getMemberId();
  }
  // 검증
  public void validateDuplicateMember(String email){
    Member findMember = memberRepository.findByEmail(email);
    if (findMember != null) {
      throw new IllegalStateException("이미 존재하는 회원입니다.");
    }
  }

}
