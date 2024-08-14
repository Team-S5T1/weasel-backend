package exam.master.service;

import exam.master.domain.History;
import exam.master.domain.Member;
import exam.master.domain.Prompt;
import exam.master.dto.HistoryDTO;
import exam.master.dto.MemberDTO;
import exam.master.dto.PromptDTO;
import exam.master.repository.HistoryRepository;
import exam.master.repository.MemberRepository;
import exam.master.repository.PromptRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PromptService {

  private final PromptRepository promptRepository;
  private final HistoryRepository historyRepository;
  private final MemberRepository memberRepository;
  private final MemberService memberService;
  private final AwsS3Service awsS3Service;

  @Transactional
  public PromptDTO addPrompt(PromptDTO promptDTO, UUID historyId,
      UUID memberId, MultipartFile file) {

    History history = new History();

    Member loginUser = memberRepository.findOne(memberId);
    MemberDTO memberDTO = memberService.convertToDTO(loginUser);
    HistoryDTO historyDTO;

    // 컨트롤러에서 프롬프트에 히스토리id를 검사
    if (historyId == null) {

      if(promptDTO.getPrompt().length() >= 20){
        // 히스토리 제목을 짧은 형태로 변환
        history.setTitle(promptDTO.getPrompt().substring(0, 20) + "...");
      }else {
        history.setTitle(promptDTO.getPrompt());
      }

      history.setMember(loginUser);
      history = historyRepository.save(history);
      historyDTO = convertToHistoryDTO(history, memberDTO);

    } else {
      history = historyRepository.findOne(historyId);
      historyDTO = convertToHistoryDTO(history, memberDTO);
    }

    Prompt prompt = new Prompt();
    prompt.setPrompt(promptDTO.getPrompt());
    prompt.setHistory(history);
    
    // 테스트
    prompt.setAnswer("answer");

    if (file != null) {
      String fileName = awsS3Service.uploadFile(file);
      prompt.setPhoto(fileName);
    }else{
      prompt.setPhoto("photo is null!");
    }


//    베드락에 프롬프트와 사진을 보내고 응답을 받는다
//    String answer = "";
//    prompt.setAnswer(answer);

    return convertToPromptDTO(promptRepository.save(prompt), historyDTO);

  }

  public List<PromptDTO> findByHistoryId(UUID historyId) {

    List<Prompt> promptList = promptRepository.findByHistoryId(historyId);
    List<PromptDTO> promptDTOList = new ArrayList<>();


    for(Prompt prompt : promptList){
      promptDTOList.add(convertToPromptDTO(prompt, null));
    }

    return promptDTOList;
  }

  public PromptDTO convertToPromptDTO(Prompt prompt, HistoryDTO historyDTO) {

    if(historyDTO == null){
      return PromptDTO.builder()
          .promptId(prompt.getPromptId())
          .prompt(prompt.getPrompt())
          .photo(prompt.getPhoto())
          .answer(prompt.getAnswer())
          .build();
    }else{
    // @Builder 애노테이션을 쓰면 빌더 패턴 사용 가능
    return PromptDTO.builder()
        .promptId(prompt.getPromptId())
        .historyDTO(historyDTO)
        .prompt(prompt.getPrompt())
        .photo(prompt.getPhoto())
        .answer(prompt.getAnswer())
        .build();
    }
  }

  public HistoryDTO convertToHistoryDTO(History history, MemberDTO memberDTO) {

    return HistoryDTO.builder()
        .historyId(history.getHistoryId())
        .title(history.getTitle())
        .createdDate(history.getCreatedDate())
        .memberDTO(memberDTO)
        .build();
  }

}
