package com.heimhel.notes.service.impl;

import com.heimhel.notes.model.entity.TemplateEntity;
import com.heimhel.notes.model.enums.TemplateType;
import com.heimhel.notes.service.DocumentGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocxGenerator implements DocumentGenerator {

    @Override
    public boolean match(TemplateType templateType) {
        return false;
    }

    @Override
    public byte[] generate(TemplateEntity entity, Map<String, Object> params) {
        return new byte[0];
    }

    @Override
    public MediaType getMediaType() {
        return null;
    }
}
