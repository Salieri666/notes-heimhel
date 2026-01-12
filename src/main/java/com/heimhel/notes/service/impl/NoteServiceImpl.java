package com.heimhel.notes.service.impl;

import com.heimhel.notes.db.repository.NoteRepository;
import com.heimhel.notes.db.specification.NoteSpecification;
import com.heimhel.notes.model.dto.NoteDto;
import com.heimhel.notes.model.dto.NoteSaveDto;
import com.heimhel.notes.model.entity.NoteEntity;
import com.heimhel.notes.model.filter.NoteFilter;
import com.heimhel.notes.model.mapper.NoteMapper;
import com.heimhel.notes.service.NoteService;
import com.heimhel.notes.validator.NoteValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository repository;
    private final NoteMapper mapper;
    private final NoteSpecification specification;
    private final NoteValidator validator;

    @Transactional(readOnly = true)
    @Override
    public NoteDto getById(String id) {
        NoteEntity entity = repository.getEntityById(id);

        return mapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<NoteDto> getAll(NoteFilter filter, Pageable pageable) {
        return repository
                .findAll(specification.byFilter(filter), pageable)
                .map(mapper::toDto);
    }

    @Transactional
    @Override
    public NoteDto save(NoteSaveDto dto) {
        validator.validateBeforeSave(dto);

        NoteEntity entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    @Override
    public NoteDto update(String id, NoteSaveDto dto) {
        NoteEntity entity = repository.getEntityById(id);

        validator.validateBeforeUpdate(entity, dto);
        mapper.merge(entity, dto);

        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
