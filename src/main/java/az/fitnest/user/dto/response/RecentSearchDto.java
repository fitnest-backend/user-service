package az.fitnest.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentSearchDto {
    private String query;
    private String type;
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime createdDate;
}
