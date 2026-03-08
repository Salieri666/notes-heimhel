package com.heimhel.notes.controller.api.v1;

import com.heimhel.notes.model.dto.TemplateDto;
import com.heimhel.notes.model.dto.TemplateSaveDto;
import com.heimhel.notes.model.filter.TemplateFilter;
import com.heimhel.notes.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Template", description = "Template API")
@RequestMapping(path = "/api/v1/template", produces = MediaType.APPLICATION_JSON_VALUE)
public class TemplateController {

    private final TemplateService service;

    @GetMapping("/{id}")
    @Operation(summary = "Get template by id")
    public TemplateDto getTemplateById(@PathVariable String id) {
        log.info("Get template by id {}", id);
        return service.getById(id);
    }

    @PostMapping("/all")
    @PageableAsQueryParam
    @Operation(summary = "Get all templates by filter")
    public Page<TemplateDto> getAllTemplates(
            @RequestBody(required = false) TemplateFilter filter,
            @Parameter(hidden = true) Pageable pageable
    ) {
        log.info("Get all templates by filter {}", filter);
        return service.getAll(filter, pageable);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Save new template")
    public TemplateDto saveTemplate(@RequestBody @Valid TemplateSaveDto dto) {
        log.info("Save new template {}", dto);
        return service.save(dto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update template by id")
    public TemplateDto updateTemplate(
            @PathVariable String id,
            @RequestBody @Valid TemplateSaveDto dto
    ) {
        log.info("Update template by id {}", id);
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete template by id")
    public void deleteTemplate(@PathVariable String id) {
        log.info("Delete template by id {}", id);
        service.deleteById(id);
    }

    @PostMapping("/{id}/generate-document")
    @Operation(summary = "Generate document by template id")
    public ResponseEntity<Resource> generateDocument(
            @PathVariable String id,
            @RequestBody Map<String, Object> params
    ) {
        return service.generateTemplate(id, params).getResource();
    }
}
