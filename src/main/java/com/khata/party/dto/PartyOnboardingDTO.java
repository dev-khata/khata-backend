package com.khata.party.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PartyOnboardingDTO {

    @Valid
    @NotNull(message = "Party details cannot be null.")
    private PartyDTO partyDetails;

    @Valid
    private PartyRecordDTO partyRecords;
}
