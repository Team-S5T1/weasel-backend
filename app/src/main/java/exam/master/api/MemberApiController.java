package exam.master.api;

import exam.master.domain.Member;
import exam.master.dto.LogInRequest;
import exam.master.dto.LoginResponse;
import exam.master.dto.MemberDTO;
import exam.master.service.MemberService;

import exam.master.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.Hashtable;
import java.util.UUID;

@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor

public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/member/join")
    public ResponseEntity<MemberDTO> joinMember(@RequestBody MemberDTO memberDTO) {
        MemberDTO newMember = memberService.joinMember(memberDTO);
        return ResponseEntity.ok(newMember);
    }

    @GetMapping("/member/view/{id}")
    public ResponseEntity<MemberDTO> Memberv1(
            @PathVariable("id") UUID id ){
        MemberDTO memberDTO = memberService.getMemberById(id);
         return ResponseEntity.ok(memberDTO);
    }

    //PatchMapping는 엔티티의 일부를 바꿀 때, PutMapping는 엔티티 전부를 바꿀 때 사용
    @PatchMapping("/member/update/{id}")
    public ResponseEntity<MemberDTO> updateMemberV1(@PathVariable("id") UUID id, @RequestBody  MemberDTO updatedMemberDTO) {
        MemberDTO memberDTO = memberService.updateMember(id, updatedMemberDTO);
        return ResponseEntity.ok(memberDTO);
    }

    //정상적으로 삭제되면 빈객체 반환
    @DeleteMapping("/member/delete/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LogInRequest logInRequest, HttpServletRequest request){

        Member loginMember = memberService.login(logInRequest);
        if(loginMember == null){
            return new LoginResponse(-1,"존재하지 않는 회원이거나 비밀번호가 일치하지 않습니다.");
        }else {
            // 로그인 성공 처리
            // 세션이 있으면 세션 반환, 없으면 신규 세션 생성
            HttpSession session = request.getSession();
            // 세션에 로그인 회원 정보 보관
            session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

            return new LoginResponse(1, "로그인 성공");
        }

    }

    @GetMapping("/logout")
    public LoginResponse logout(HttpServletRequest request){
        HttpSession session = request.getSession(false); // 로그아웃인데 세션 없을 때 생성 안 함
        if(session != null){
            session.invalidate(); // 세션 정보가 있으면 정보 삭제
        }
        return new LoginResponse(1, "로그아웃 성공");
    }

    // 세션 리스트 확인용 코드
    public static Hashtable sessionList = new Hashtable();

}
