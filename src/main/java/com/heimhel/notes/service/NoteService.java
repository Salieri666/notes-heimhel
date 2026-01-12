package com.heimhel.notes.service;

import com.heimhel.notes.model.dto.NoteDto;
import com.heimhel.notes.model.dto.NoteSaveDto;
import com.heimhel.notes.model.filter.NoteFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoteService {

    NoteDto getById(String id);

    Page<NoteDto> getAll(NoteFilter filter, Pageable pageable);

    NoteDto save(NoteSaveDto dto);

    NoteDto update(String id, NoteSaveDto dto);

    void deleteById(String id);

}
