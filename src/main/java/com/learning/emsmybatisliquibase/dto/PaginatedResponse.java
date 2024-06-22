package com.learning.emsmybatisliquibase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginatedResponse<T> {

    private List<T> data;

    private int currentPage;

    private Long totalItems;

    private int totalPages;
}
