package com.khata.party.dto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PartyOnboardingDTO {

    @Valid
    private PartyDTO partyDetails;

    @Valid
    private PartyRecordDTO partyRecords;
}
