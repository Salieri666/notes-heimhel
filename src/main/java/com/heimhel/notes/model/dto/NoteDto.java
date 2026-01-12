package com.heimhel.notes.model.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDto {

    @Schema(description = "Item ID")
    private String id;

    @Schema(description = "Title")
    private String title;

    @Schema(description = "Content")
    private String content;

}
