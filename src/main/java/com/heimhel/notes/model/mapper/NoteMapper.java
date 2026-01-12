package com.heimhel.notes.model.mapper;


import com.heimhel.notes.model.dto.NoteDto;
import com.heimhel.notes.model.dto.NoteSaveDto;
import com.heimhel.notes.model.entity.NoteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface NoteMapper {

    NoteEntity toEntity(NoteSaveDto dto);

    NoteDto toDto(NoteEntity entity);

    void merge(@MappingTarget NoteEntity entity, NoteSaveDto dto);

}
