package com.heimhel.notes.service.impl;

import com.heimhel.notes.db.repository.TemplateRepository;
import com.heimhel.notes.db.specification.TemplateSpecification;
import com.heimhel.notes.model.dto.FileDTO;
import com.heimhel.notes.model.dto.TemplateDto;
import com.heimhel.notes.model.dto.TemplateSaveDto;
import com.heimhel.notes.model.entity.TemplateEntity;
import com.heimhel.notes.model.filter.TemplateFilter;
import com.heimhel.notes.model.mapper.TemplateMapper;
import com.heimhel.notes.service.DocumentGenerator;
import com.heimhel.notes.service.TemplateService;
import com.heimhel.notes.validator.TemplateValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository repository;
    private final TemplateMapper mapper;
    private final TemplateSpecification specification;
    private final TemplateValidator validator;
    private final List<DocumentGenerator> documentGenerators;

    @Transactional(readOnly = true)
    @Override
    public TemplateDto getById(String id) {
        TemplateEntity entity = repository.getEntityById(id);

        return mapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TemplateDto> getAll(TemplateFilter filter, Pageable pageable) {
        return repository
                .findAll(specification.byFilter(filter), pageable)
                .map(mapper::toDto);
    }

    @Transactional
    @Override
    public TemplateDto save(TemplateSaveDto dto) {
        validator.validateBeforeSave(dto);

        TemplateEntity entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    @Override
    public TemplateDto update(String id, TemplateSaveDto dto) {
        TemplateEntity entity = repository.getEntityById(id);

        validator.validateBeforeUpdate(entity, dto);
        mapper.merge(entity, dto);

        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public FileDTO generateTemplate(String id, Map<String, Object> params) {
        TemplateEntity entity = repository.getEntityById(id);

        DocumentGenerator generator = documentGenerators.stream()
                .filter(el -> el.match(entity.getTemplateType()))
                .findFirst().orElseThrow(() -> new RuntimeException("Template not found for type: " + entity.getTemplateType()));

        byte[] generatedFile = generator.generate(entity, params);

        return FileDTO.builder()
                .body(generatedFile)
                .fileName("template.pdf")
                .contentType(generator.getMediaType())
                .contentLength((long) generatedFile.length)
                .build();
    }
}
