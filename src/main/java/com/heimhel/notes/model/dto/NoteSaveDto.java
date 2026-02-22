package com.heimhel.notes.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteSaveDto {

    @NotEmpty
    @Schema(description = "Title")
    private String title;

    @Schema(description = "Content")
    private String content;

}
