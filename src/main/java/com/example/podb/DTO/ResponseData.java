package com.example.podb.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseData {
    private List<ProductDTO> response;
    private int pageSize;
    private int pageNo;
    private int totalItems;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
}