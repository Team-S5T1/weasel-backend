package exam.master.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LogInRequest {
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
