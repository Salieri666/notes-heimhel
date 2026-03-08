package com.heimhel.notes.service;

import com.heimhel.notes.model.dto.FileDTO;
import com.heimhel.notes.model.dto.TemplateDto;
import com.heimhel.notes.model.dto.TemplateSaveDto;
import com.heimhel.notes.model.filter.TemplateFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface TemplateService {

    TemplateDto getById(String id);

    Page<TemplateDto> getAll(TemplateFilter filter, Pageable pageable);

    TemplateDto save(TemplateSaveDto dto);

    TemplateDto update(String id, TemplateSaveDto dto);

    void deleteById(String id);

    FileDTO generateTemplate(String id, Map<String, Object> params);

}
