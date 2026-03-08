package com.heimhel.notes.config;

import freemarker.cache.StringTemplateLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FreemarkerConfig {

    @Bean
    public StringTemplateLoader stringTemplateLoader() {
        return new StringTemplateLoader();
    }

    @Bean
    @Primary
    public freemarker.template.Configuration freemarkerConfiguration(StringTemplateLoader stringTemplateLoader) {
        freemarker.template.Configuration configuration = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_33);
        configuration.setTemplateLoader(stringTemplateLoader);
        return configuration;
    }
}
