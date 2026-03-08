package com.heimhel.notes.service;

import com.heimhel.notes.model.entity.TemplateEntity;
import com.heimhel.notes.model.enums.TemplateType;
import org.springframework.http.MediaType;

import java.util.Map;

public interface DocumentGenerator {

    boolean match(TemplateType templateType);

    byte[] generate(TemplateEntity entity, Map<String, Object> params);

    MediaType getMediaType();

}
