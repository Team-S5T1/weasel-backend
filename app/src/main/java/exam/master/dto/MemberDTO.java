package exam.master.dto;
import exam.master.status.MemberStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MemberDTO {
    private UUID memberId;
    private String email;
    private String password;
    private MemberStatus status;
    private String profilePhoto;
}
