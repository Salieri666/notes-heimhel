package com.heimhel.notes.model.dto;

import com.heimhel.notes.model.enums.TemplateType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Template dto")
public class TemplateDto {

    @Schema(description = "Item ID")
    private String id;

    @Schema(description = "Template name")
    private String templateName;

    @Schema(description = "Template code")
    private String code;

    @Schema(description = "Type")
    private TemplateType templateType;

    @Schema(description = "Content")
    private String content;

    @Schema(description = "Created date")
    private Instant createdDate;

    @Schema(description = "Modified date")
    private Instant modifiedDate;

}
