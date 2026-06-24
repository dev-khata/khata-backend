package com.khata.party.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khata.exceptions.BadRequestException;
import com.khata.party.dto.PartyDTO;
import com.khata.party.dto.PartyOnboardingDTO;
import com.khata.party.service.PartyService;
import com.khata.payload.ApiResponse;
import com.khata.payload.PaginationResponse;
import com.khata.utils.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/party")
@Tag(name = "Party")
@AllArgsConstructor
public class PartyController {

    private final PartyService partyService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @Operation(
            description = "Get endpoint for party",
            summary = "This is the summary for the party get endpoint"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)

    public ResponseEntity<ApiResponse<PartyDTO>> createParty(@Valid @RequestBody PartyOnboardingDTO partyOnboardingDTO) {
        PartyDTO partyDTO = partyService.createParty(partyOnboardingDTO);
        ApiResponse<PartyDTO> response = new ApiResponse<>(partyDTO, HttpStatus.CREATED.value(), "Party Created Successfully");
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
    public ResponseEntity<ApiResponse<PartyDTO>> updateParty(@RequestBody JsonNode requestBody, @PathVariable Integer partyId) {
        PartyDTO partyDetails = extractPartyDetails(requestBody);
        validatePartyDetails(partyDetails);
        PartyDTO party = partyService.updateParty(partyDetails, partyId);
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

    private PartyDTO extractPartyDetails(JsonNode requestBody) {
        if (requestBody == null || requestBody.isNull()) {
            throw new BadRequestException("Request body cannot be null.");
        }

        JsonNode partyDetailsNode = requestBody.has("partyDetails")
                ? requestBody.get("partyDetails")
                : requestBody;

        if (partyDetailsNode == null || partyDetailsNode.isNull()) {
            throw new BadRequestException("Party details cannot be null.");
        }

        return objectMapper.convertValue(partyDetailsNode, PartyDTO.class);
    }

    private void validatePartyDetails(PartyDTO partyDetails) {
        Set<ConstraintViolation<PartyDTO>> violations = validator.validate(partyDetails);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            throw new BadRequestException(message);
        }
    }

}
