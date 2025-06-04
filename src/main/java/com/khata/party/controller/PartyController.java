package com.khata.party.controller;

import com.khata.party.dto.PartyDTO;
import com.khata.party.service.PartyService;
import com.khata.payload.ApiResponse;
import com.khata.payload.PaginationResponse;
import com.khata.utils.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/party")
@Tag(name = "Party")
@AllArgsConstructor
public class PartyController {

    private final PartyService partyService;

    @Operation(
            description = "Get endpoint for party",
            summary = "This is the summary for the party get endpoint"
    )
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<PartyDTO>> createParty(@Valid @RequestBody PartyDTO partyDTO) {
        PartyDTO party = partyService.createParty(partyDTO);
        ApiResponse<PartyDTO> response = new ApiResponse<>(party, HttpStatus.CREATED.value(), "Party Created Successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<PartyDTO>>> getPartyList(Pageable pageable) {
        Page<PartyDTO> partyDTOPage = partyService.getParties(pageable);

        PaginationResponse<PartyDTO> paginationPayload = PaginationUtil.buildPaginationResponse(partyDTOPage);

        ApiResponse<PaginationResponse<PartyDTO>> response = new ApiResponse<>(paginationPayload, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{partyId}")
    public ResponseEntity<ApiResponse<PartyDTO>> updateParty(@Valid @RequestBody PartyDTO partyDTO, @PathVariable Integer partyId) {
        PartyDTO party = partyService.updateParty(partyDTO, partyId);
        ApiResponse<PartyDTO> response = new ApiResponse<>(party, HttpStatus.OK.value(), "Party Updated Successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{partyId}")
    public ResponseEntity<ApiResponse<PartyDTO>> getPartyDetails(@PathVariable Integer partyId) {
        PartyDTO partyDTO = partyService.getPartyById(partyId);
        return ResponseEntity.ok(new ApiResponse<>(partyDTO, HttpStatus.OK.value()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PaginationResponse<PartyDTO>>> searchPartyByName(@RequestParam String keyword, Pageable pageable) {
        Page<PartyDTO> partyDTOPage = partyService.searchPartyByName(keyword, pageable);

        PaginationResponse<PartyDTO> paginationPayload = PaginationUtil.buildPaginationResponse(partyDTOPage);

        ApiResponse<PaginationResponse<PartyDTO>> response = new ApiResponse<>(paginationPayload, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{partyId}")
    public ResponseEntity<ApiResponse<Void>> deleteParty(@PathVariable Integer partyId) {
        partyService.deleteParty(partyId);
        return ResponseEntity.ok(new ApiResponse<>(null, HttpStatus.OK.value(), "Party Deleted Successfully"));
    }

}
