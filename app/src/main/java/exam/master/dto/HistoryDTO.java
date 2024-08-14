package exam.master.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class HistoryDTO {
  private UUID historyId;
  private MemberDTO memberDTO;
  private String title;
  private LocalDateTime createdDate;
}

