package az.fitnest.user.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;
import java.util.List;

@Builder
public record PaginatedResponse<T>(
    List<T> items,
    long total,
    int page,
    int pageSize
) {
    public static <T> PaginatedResponse<T> of(Page<T> pageResult) {
        return PaginatedResponse.<T>builder()
                .items(pageResult.getContent())
                .total(pageResult.getTotalElements())
                .page(pageResult.getNumber() + 1)
                .pageSize(pageResult.getSize())
                .build();
    }
}
