package exam.master.dto;

import exam.master.domain.History;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PromptDTO {

  private Long promptId;
  private HistoryDTO historyDTO;
  private String photo;
  private String prompt;
  private String answer;

}
