package com.heimhel.notes.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SseTextMessageDTO {

    @Schema(description = "Title")
    private String title;

    @Schema(description = "Content")
    private String content;

}
