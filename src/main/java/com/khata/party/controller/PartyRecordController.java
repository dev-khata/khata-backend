package com.khata.party.controller;

import com.khata.party.dto.PartyRecordDTO;
import com.khata.party.dto.PartyRecordPaginationResponse;
import com.khata.party.service.PartyRecordService;
import com.khata.payload.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/party/record")
@Tag(name = "PartyRecord")
public class PartyRecordController {

    private final PartyRecordService partyRecordService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<PartyRecordDTO>> createPartyRecord(@Valid @RequestBody PartyRecordDTO partyRecordDTO){
        PartyRecordDTO partyRecord = partyRecordService.createPartyRecord(partyRecordDTO);
        ApiResponse<PartyRecordDTO> response = new ApiResponse<>(partyRecord, HttpStatus.CREATED.value(), "Party Record created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{partyId}")
    public ResponseEntity<ApiResponse<PartyRecordPaginationResponse>> getPartyRecords(
            @PathVariable Integer partyId,
            @RequestParam(required = false) Integer fiscalYearId,
            Pageable pageable) {
        PartyRecordPaginationResponse recordsPage = partyRecordService.getPartyRecordsByPartyId(partyId, fiscalYearId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(recordsPage, HttpStatus.OK.value()));
    }

}
