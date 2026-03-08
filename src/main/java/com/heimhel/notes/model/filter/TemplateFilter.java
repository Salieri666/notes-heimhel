package com.heimhel.notes.model.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Template filter")
public class TemplateFilter extends BaseFilter<String> {

    @Schema(description = "Template code")
    private String code;

    @Override
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(getIds().stream().filter(Objects::nonNull).toList())
                && StringUtils.isEmpty(code);
    }
}
