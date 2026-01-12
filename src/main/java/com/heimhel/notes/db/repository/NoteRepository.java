package com.heimhel.notes.db.repository;

import com.heimhel.notes.exception.EntityNotFoundException;
import com.heimhel.notes.model.entity.NoteEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, String>, JpaSpecificationExecutor<NoteEntity> {

    Optional<NoteEntity> findByTitle(String title);

    default NoteEntity getEntityById(String id) {
        return this.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id " + id + " not found for class " + NoteEntity.class));
    }

}
