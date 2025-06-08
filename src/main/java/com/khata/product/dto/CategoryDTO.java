package com.khata.product.dto;


import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CategoryDTO {
    private Integer id;

    @NotBlank(message = "Category title cannot be blank.")
    @Size(max = 100, message = "title must be less than 100 characters.")
    private String title;

}
