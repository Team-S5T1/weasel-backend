package exam.master.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exam.master.config.InvokeModelWithResponseStream;
import exam.master.domain.Member;
import exam.master.dto.PromptDTO;
import exam.master.service.PromptService2;
import exam.master.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/prompt")
@CrossOrigin(origins = {"https://weasel.kkamji.net", "http://localhost:5173"}, allowCredentials = "true")
//@CrossOrigin(origins = "*", allowCredentials = "true")
public class PromptApiController2 {

  private final PromptService2 promptService;
  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
  private final ExecutorService executorService = Executors.newCachedThreadPool();

  @PostMapping("/add")
  public ResponseEntity<PromptDTO> addPrompt(@RequestParam(value = "promptDTO") String promptDTOStr,
                                             // json 형태로 요청 하지 않고 요청 하면 요청 데이터 태그에 name과 자바 객체에 변수 이름과 매핑하여 우리의 DTO 객체를 만들준다.
                                             // PromptDTO promptDTO,
                                             @RequestParam(value = "historyId", required = false) UUID historyId,
                                             @RequestParam(value = "file", required = false)
                                               MultipartFile file,
                                             HttpSession session) throws IOException {
    String sessionId = session.getId();
    Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
    log.debug("prompt add : 세선에서 꺼낸 로그인 정보 >>> "+loginMember.getMemberId());
    log.debug("prompt add : 세선에서 꺼낸 로그인 정보 >>> "+loginMember.getEmail());

    PromptDTO promptDTO = convertStringToPromptDTO(promptDTOStr);
    PromptDTO newPrompt = promptService.addPrompt(promptDTO,historyId ,loginMember, file);
    SseEmitter emitter = new SseEmitter(-1L);
    emitters.put(sessionId, emitter);

    executorService.execute(() -> {
      try {
        ObjectMapper objectMapper = new ObjectMapper();
        // 첫 번째 이벤트로 PromptDTO 전송
        emitter.send(SseEmitter.event()
                .data("{\"type\": \"promptDTO\", \"data\": " + objectMapper.writeValueAsString(promptDTO) + "}")
                .build());


        // 파일 처리 및 응답 생성 로직...
        InvokeModelWithResponseStream.invokeModel(promptDTO.getPrompt(), file.getBytes(), emitter, executorService);

        emitter.complete();
      } catch (Exception e) {
        emitter.completeWithError(e);
      } finally {
        emitters.remove(sessionId);
      }
    });

    return ResponseEntity.ok(newPrompt);
  }

  @GetMapping("/add")
  public SseEmitter streamResponse(HttpServletRequest request) {
    String sessionId = request.getSession().getId();
    return emitters.get(sessionId);
  }

  @GetMapping("/list/{historyId}")
  public ResponseEntity<List<PromptDTO>> list(
      @PathVariable(value = "historyId") UUID historyId) {
    List<PromptDTO> list = promptService.findByHistoryId(historyId);
    return ResponseEntity.ok(list);
  }

  public PromptDTO convertStringToPromptDTO(String promptDTOStr) throws JsonProcessingException {

    // form으로 보내면 DTO로 받을 수 있지만
    // 문자열(json)으로 받았을 때 DTO로 변환한다.
    ObjectMapper objectMapper = new ObjectMapper();

    return objectMapper.readValue(promptDTOStr, PromptDTO.class);
  }
}