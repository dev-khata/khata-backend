package com.khata.party.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class PartyRecordPaginationResponse {
    private List<PartyRecordDTO> items;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private PartyRecordSummaryDTO summary;

    public PartyRecordPaginationResponse(
            List<PartyRecordDTO> items,
            int pageNumber,
            int pageSize,
            long totalElements,
            int totalPages,
            PartyRecordSummaryDTO summary) {
        this.items = items;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.summary = summary;
    }
}
