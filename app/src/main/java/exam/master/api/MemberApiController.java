package exam.master.api;

import exam.master.dto.MemberDTO;
import exam.master.service.MemberService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<MemberDTO> joinMember(@RequestBody MemberDTO memberDTO) {
        MemberDTO newMember = memberService.joinMember(memberDTO);
        return ResponseEntity.ok(newMember);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<MemberDTO> Memberv1(
            @PathVariable("id") UUID id ){
        MemberDTO memberDTO = memberService.getMemberById(id);
         return ResponseEntity.ok(memberDTO);
    }

    //PatchMapping는 엔티티의 일부를 바꿀 때, PutMapping는 엔티티 전부를 바꿀 때 사용
    @PatchMapping("/update/{id}")
    public ResponseEntity<MemberDTO> updateMemberV1(@PathVariable("id") UUID id, @RequestBody  MemberDTO updatedMemberDTO) {
        MemberDTO memberDTO = memberService.updateMember(id, updatedMemberDTO);
        return ResponseEntity.ok(memberDTO);
    }

    //정상적으로 삭제되면 빈객체 반환
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }



}
