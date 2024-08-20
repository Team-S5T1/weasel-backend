//package exam.master.api;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import exam.master.domain.Member;
//import exam.master.dto.PromptDTO;
//import exam.master.service.MemberService;
//import exam.master.service.PromptService;
//import exam.master.session.SessionConst;
//import jakarta.servlet.http.HttpSession;
//import java.io.IOException;
//import java.util.List;
//import java.util.UUID;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/v1/prompt")
//@CrossOrigin(origins = {"https://weasel.kkamji.net", "http://localhost:5173"}, allowCredentials = "true")
////@CrossOrigin(origins = "*", allowCredentials = "true")
//public class PromptApiController {
//
//  private final PromptService promptService;
//
//  @PostMapping("/add")
//  public ResponseEntity<PromptDTO> add(
//      // Json 문자열을 받는다.
//      @RequestParam(value = "promptDTO") String promptDTOStr,
//      // json 형태로 요청 하지 않고 요청 하면 요청 데이터 태그에 name과 자바 객체에 변수 이름과 매핑하여 우리의 DTO 객체를 만들준다.
//      // PromptDTO promptDTO,
//      @RequestParam(value = "historyId", required = false) UUID historyId,
//      @RequestParam(value = "file", required = false) MultipartFile file,
//      HttpSession session) throws IOException {
//
//    // JSON String ==> 자바 객체로 변환
//    PromptDTO promptDTO = convertStringToPromptDTO(promptDTOStr);
//
//    Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
//    log.debug("prompt add : 세선에서 꺼낸 로그인 정보 >>> "+loginMember.getMemberId());
//    log.debug("prompt add : 세선에서 꺼낸 로그인 정보 >>> "+loginMember.getEmail());
//
//    PromptDTO newPromptDTO = promptService.addPrompt(promptDTO, historyId, loginMember,
//        file);
//
//    return ResponseEntity.ok(newPromptDTO);
//  }
//
//  @GetMapping("/list/{historyId}")
//  public ResponseEntity<List<PromptDTO>> list(
//      @PathVariable(value = "historyId") UUID historyId) {
//    List<PromptDTO> list = promptService.findByHistoryId(historyId);
//    return ResponseEntity.ok(list);
//  }
//
//  public PromptDTO convertStringToPromptDTO(String promptDTOStr) throws JsonProcessingException {
//
//    // form으로 보내면 DTO로 받을 수 있지만
//    // 문자열(json)으로 받았을 때 DTO로 변환한다.
//    ObjectMapper objectMapper = new ObjectMapper();
//
//    return objectMapper.readValue(promptDTOStr, PromptDTO.class);
//  }
//}