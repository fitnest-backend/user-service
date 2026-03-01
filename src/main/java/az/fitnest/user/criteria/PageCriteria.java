package az.fitnest.user.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageCriteria {

    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 20;

    private String sortBy;

    @Builder.Default
    private SortDirection direction = SortDirection.ASC;

    public Integer getPage() {
        return page != null && page > 0 ? page - 1 : 0;
    }

    public enum SortDirection {
        ASC, DESC
    }
}
