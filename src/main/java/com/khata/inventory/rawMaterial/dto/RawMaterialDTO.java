package com.khata.inventory.rawMaterial.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.khata.inventory.rawMaterial.entity.enums.RawMaterialUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RawMaterialDTO {

    private Integer id;

    @NotBlank(message = "Material code cannot be blank.")
    @Size(max = 50, message = "Material code must be less than 50 characters.")
    private String materialCode;

    @NotBlank(message = "Material name cannot be blank.")
    @Size(max = 100, message = "Material name must be less than 100 characters.")
    private String materialName;

    @NotNull(message = "Unit cannot be null.")
    private RawMaterialUnit unit;

    private boolean active = true;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer totalRollCount;
}
