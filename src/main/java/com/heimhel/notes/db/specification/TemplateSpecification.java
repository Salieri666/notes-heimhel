package com.heimhel.notes.db.specification;

import com.heimhel.notes.model.entity.TemplateEntity;
import com.heimhel.notes.model.filter.TemplateFilter;
import com.heimhel.notes.utils.SpecificationBuilder;
import com.heimhel.notes.utils.SpecificationUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class TemplateSpecification {

    public Specification<TemplateEntity> byFilter(TemplateFilter filter) {
        if (filter == null) {
            filter = TemplateFilter.builder().build();
        }

        return new SpecificationBuilder<TemplateEntity>()
                .and(byIds(filter.getIds()))
                .and(byCode(filter.getCode()))
                .build();
    }

    private Specification<TemplateEntity> byIds(Collection<String> ids) {
        return SpecificationUtils.searchIn(TemplateEntity.Fields.id, ids);
    }

    private Specification<TemplateEntity> byCode(String code) {
        return SpecificationUtils.byFieldEqual(TemplateEntity.Fields.code, code);
    }

}
