package com.heimhel.notes.service.impl;

import com.heimhel.notes.model.entity.TemplateEntity;
import com.heimhel.notes.model.enums.TemplateType;
import com.heimhel.notes.service.DocumentGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class HtmlGenerator implements DocumentGenerator {

    @Value("${wkhtmltopdf-path}")
    private String wkhtmltopdfPath;

    private final Configuration configuration;
    private final StringTemplateLoader templateLoader;

    @Override
    public boolean match(TemplateType templateType) {
        return TemplateType.HTML.equals(templateType);
    }

    @Override
    public byte[] generate(TemplateEntity entity, Map<String, Object> params) {
        String generatedContent = getTemplateContent(entity, params);
        WrapperConfig config = new WrapperConfig(wkhtmltopdfPath);
        Pdf pdf = new Pdf(config);

        pdf.addParam(
                new Param("--encoding", "utf-8"),
                new Param("--enable-local-file-access"),
                new Param("--print-media-type"),
                new Param("--page-size", "A4"),
                new Param("--margin-top", "10mm"),
                new Param("--margin-bottom", "10mm"),
                new Param("--margin-left", "10mm"),
                new Param("--margin-right", "10mm"),
                new Param("--quiet")
        );

        pdf.addPageFromString(generatedContent );

        pdf.setTimeout(120);
        pdf.setSuccessValues(java.util.Arrays.asList(0, 1));
        pdf.setAllowMissingAssets();

        try {
            byte[] result = pdf.getPDF();

            pdf.cleanAllTempFiles();
            return result;
        } catch (Exception ex) {
            log.error("wkhtmltopdf generation failed: {}", ex.getMessage(), ex);
            try { pdf.cleanAllTempFiles(); } catch (Exception ignore) {}
            throw new RuntimeException("Failed to generate PDF with wkhtmltopdf: " + ex.getMessage(), ex);
        }
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.parseMediaType("application/pdf");
    }

    public String getTemplateContent(TemplateEntity templateEntity, Map<String, Object> templateParams) {
        try (StringWriter stringWriter = new StringWriter()) {

            Template template = new Template(
                    String.valueOf(templateEntity.getId()),
                    new StringReader(templateEntity.getContent()),
                    configuration
            );//you can add cache for templates

            template.process(templateParams, stringWriter);

            return stringWriter.toString();
        } catch (Exception e) {
            log.error("Exception in template processing for template id: {} and error: {}",
                    templateEntity.getId(), e.getMessage(), e);

            throw new RuntimeException(
                    "Exception in template processing for template id: " + templateEntity.getId(), e);
        }
    }
}
