package exam.master.service;

import exam.master.domain.History;
import exam.master.domain.Member;
import exam.master.domain.Prompt;
import exam.master.repository.HistoryRepository;
import exam.master.repository.PromptRepository;
import exam.master.status.MemberStatus;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HistoryService {
  private final PromptRepository promptRepository;
  private final HistoryRepository historyRepository;
  private final AwsS3Service awsS3Service;

  @Transactional
  public int deleteHistoryByHistoryIdAndMemberId(UUID historyId, UUID memberId){

    // 유효성 검사
    History history = historyRepository.findOne(historyId);

    int count = 0;

    if(memberId == history.getMember().getMemberId()){

      // s3에 업로드 되어있는 이미지 삭제
      List<Prompt> deletePromptList = promptRepository.findByHistoryId(historyId);
      for(Prompt deletePrompt : deletePromptList){
        // 지금은 photo is null!이 아니라면 s3에 삭제 요청
        if(!deletePrompt.getPhoto().equals("photo is null!")) {
          awsS3Service.deleteFile(deletePrompt.getPhoto());
        }
      }

      // 히스토리에 해당하는 모든 데이터 삭제
      count += promptRepository.deletePromptsByHistoryId(historyId);
      count += historyRepository.deleteHistoryByHistoryId(historyId);
    }

    return count;
  }

}
