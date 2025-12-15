package com.jcmlabs.AccessCore.Utilities;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseFilterInput {
    private Integer pageNumber;
    private Integer itemsPerPage;
    private String keyword;
    private String uuid;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private Long createdBy;
    private Long updatedBy;
    private Boolean active;
}
