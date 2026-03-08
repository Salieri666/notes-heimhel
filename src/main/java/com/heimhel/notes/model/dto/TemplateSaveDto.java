package com.heimhel.notes.model.dto;

import com.heimhel.notes.model.enums.TemplateType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Template save dto")
public class TemplateSaveDto {

    @NotEmpty
    @Schema(description = "Template name")
    private String templateName;

    @NotEmpty
    @Schema(description = "Template code")
    private String code;

    @NotNull
    @Schema(description = "Type")
    private TemplateType templateType;

    @NotEmpty
    @Schema(description = "Content")
    private String content;

}
