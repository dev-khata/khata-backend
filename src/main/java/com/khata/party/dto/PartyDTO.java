package com.khata.party.dto;

import com.khata.party.entity.enums.PartyType;
import com.khata.party.entity.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class PartyDTO {
    private Integer id;

    @NotBlank(message = "Name cannot be blank.")
    @Size(min = 4, max = 100, message = "Name must be between 4 and 100 characters.")
    private String name;

    @Email(message = "Invalid email address format.")
    @NotBlank(message = "Email cannot be blank.")
    @Size(max = 100, message = "Email must be less than 100 characters.")
    private String email;

    @Size(max = 10, message = "Phone number must be less than or equal to 10 digits.")
    @Pattern(regexp = "^(97|98)[0-9]{8}$", message = "Phone number must be 10 digits and start with 97 or 98.")
    private String phoneNumber;

    @NotBlank(message = "Address cannot be blank.")
    @Size(min = 7, max = 100, message = "Address must be between 7 and 100 characters.")
    private String address;

    @NotBlank(message = "Business name cannot be blank.")
    @Size(min = 7, max = 20, message = "Business name must be between 7 and 20 characters.")
    private String partyBusinessName;

    private String cbf;

    @NotNull(message = "Opening balance cannot be null.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Opening balance must be a positive number.")
    private BigDecimal openingBalance;

    @NotNull(message = "Transaction type cannot be null")
    private TransactionType transactionType;

    @NotNull(message = "Party type cannot be null")
    private PartyType partyType;
}
