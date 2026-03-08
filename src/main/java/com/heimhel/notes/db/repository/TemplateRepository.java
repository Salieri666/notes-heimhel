package com.heimhel.notes.db.repository;

import com.heimhel.notes.exception.EntityNotFoundException;
import com.heimhel.notes.model.entity.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity, String>, JpaSpecificationExecutor<TemplateEntity> {

    Optional<TemplateEntity> findByCode(String code);

    Optional<TemplateEntity> findByTemplateName(String templateName);

    default TemplateEntity getEntityById(String id) {
        return this.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id " + id + " not found for class " + TemplateEntity.class));
    }

}
