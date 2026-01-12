package com.heimhel.notes.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteSaveDto {

    @Schema(description = "Title")
    private String title;

    @Schema(description = "Content")
    private String content;

}
