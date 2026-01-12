package com.heimhel.notes.validator;

import com.heimhel.notes.db.repository.NoteRepository;
import com.heimhel.notes.exception.BusinessException;
import com.heimhel.notes.model.dto.NoteSaveDto;
import com.heimhel.notes.model.entity.NoteEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoteValidator {

    private final NoteRepository repository;

    public void validateBeforeSave(NoteSaveDto dto) {
        repository.findByTitle(dto.getTitle()).ifPresent(note -> {
            throw new BusinessException("Note with title '" + dto.getTitle() + "' already exists");
        });
    }

    public void validateBeforeUpdate(NoteEntity entity, NoteSaveDto dto) {
        repository.findByTitle(dto.getTitle()).ifPresent(note -> {
            if (!note.getId().equals(entity.getId())) {
                throw new BusinessException("Note with title '" + dto.getTitle() + "' already exists");
            }
        });
    }
}
