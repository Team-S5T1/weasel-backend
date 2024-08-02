package exam.master.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponse {

    @NotEmpty
    private int resultCode; // 성공 : 1, 실패 : -1

    @NotEmpty
    private String msg;
}
