package com.heimhel.notes.db.specification;

import com.heimhel.notes.model.entity.NoteEntity;
import com.heimhel.notes.model.filter.NoteFilter;
import com.heimhel.notes.utils.SpecificationUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

import static java.util.Objects.isNull;

@Component
public class NoteSpecification {

    public Specification<NoteEntity> byFilter(NoteFilter filter) {
        if (isNull(filter)) {
            filter = NoteFilter.builder().build();
        }
        return Specification.where(byIds(filter.getIds()))
                .and(byTitle(filter.getTitle()));

    }

    private Specification<NoteEntity> byIds(Collection<String> ids) {
        return SpecificationUtils.searchIn(NoteEntity.Fields.id, ids);
    }

    private Specification<NoteEntity> byTitle(String title) {
        return SpecificationUtils.searchLike(NoteEntity.Fields.title, title);
    }

}
