package com.heimhel.notes.service.impl;

import com.heimhel.notes.model.entity.TemplateEntity;
import com.heimhel.notes.model.enums.TemplateType;
import com.heimhel.notes.service.DocumentGenerator;
import com.heimhel.notes.utils.DocxTemplateUtilityService;
import com.heimhel.notes.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocxGenerator implements DocumentGenerator {

    private final DocxTemplateUtilityService templateUtilityService;

    @Override
    public boolean match(TemplateType templateType) {
        return TemplateType.DOCX.equals(templateType);
    }

    @Override
    public byte[] generate(TemplateEntity entity, Map<String, Object> params) {
        try {
            WordprocessingMLPackage wordMLPackage = templateUtilityService.loadTemplateFromSource(getTemplateInputStream(entity));
            if (wordMLPackage == null) {
                log.error("Failed to load template with UUID: {}", entity.getId());
                throw new RuntimeException("Template loading failed");
            }

            log.info("Generate with params: {}", params);

//            loadFonts(wordMLPackage);

            templateUtilityService.fillTablesInTemplate(wordMLPackage, params);

            templateUtilityService.fillTemplate(wordMLPackage, params);

            return FileUtil.toPdfBytes(wordMLPackage);

        } catch (Exception e) {
            log.error("Error generating agreement document: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate agreement document", e);
        }
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }


    private InputStream getTemplateInputStream(TemplateEntity entity) throws IOException {
        String templateNamePath = entity.getContent();

        Resource resource = new ClassPathResource("templates/" + templateNamePath);
        byte[] templateFile = resource.getContentAsByteArray();

        return new ByteArrayInputStream(templateFile);
    }
}
