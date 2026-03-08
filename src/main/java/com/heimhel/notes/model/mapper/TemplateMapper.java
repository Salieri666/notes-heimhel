package com.heimhel.notes.model.mapper;


import com.heimhel.notes.model.dto.TemplateDto;
import com.heimhel.notes.model.dto.TemplateSaveDto;
import com.heimhel.notes.model.entity.TemplateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface TemplateMapper {

    TemplateEntity toEntity(TemplateSaveDto dto);

    TemplateDto toDto(TemplateEntity entity);

    void merge(@MappingTarget TemplateEntity entity, TemplateSaveDto dto);

}
