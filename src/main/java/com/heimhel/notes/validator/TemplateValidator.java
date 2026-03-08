package com.heimhel.notes.validator;

import com.heimhel.notes.db.repository.TemplateRepository;
import com.heimhel.notes.exception.BusinessException;
import com.heimhel.notes.model.dto.TemplateSaveDto;
import com.heimhel.notes.model.entity.TemplateEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateValidator {

    private final TemplateRepository repository;

    public void validateBeforeSave(TemplateSaveDto dto) {
        repository.findByCode(dto.getCode()).ifPresent(template -> {
            throw new BusinessException("Template with code '" + dto.getCode() + "' already exists");
        });

        repository.findByTemplateName(dto.getTemplateName()).ifPresent(template -> {
            throw new BusinessException("Template with title '" + dto.getTemplateName() + "' already exists");
        });
    }

    public void validateBeforeUpdate(TemplateEntity entity, TemplateSaveDto dto) {
        repository.findByCode(dto.getCode()).ifPresent(template -> {
            if (!template.getId().equals(entity.getId())) {
                throw new BusinessException("Template with code '" + dto.getCode() + "' already exists");
            }
        });

        repository.findByTemplateName(dto.getTemplateName()).ifPresent(template -> {
            if (!template.getId().equals(entity.getId())) {
                throw new BusinessException("Template with title '" + dto.getTemplateName() + "' already exists");
            }
        });
    }
}
