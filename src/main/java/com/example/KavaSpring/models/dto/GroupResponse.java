package com.example.KavaSpring.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupResponse {
    @NotBlank
    @Size(max = 120)
    private String id;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Size(max = 120)
    private String description;
}
