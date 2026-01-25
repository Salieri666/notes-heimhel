package com.heimhel.notes.controller.api.v1;

import com.heimhel.notes.model.dto.NoteDto;
import com.heimhel.notes.model.dto.NoteSaveDto;
import com.heimhel.notes.model.filter.NoteFilter;
import com.heimhel.notes.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Note", description = "Note API")
@RequestMapping(path = "/api/v1/note", produces = MediaType.APPLICATION_JSON_VALUE)
public class NoteController {

    private final NoteService service;

    @GetMapping("/{id}")
    @Operation(summary = "Get note by id")
    public NoteDto getNoteById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping("/all")
    @PageableAsQueryParam
    @Operation(summary = "Get all notes by filter")
    public Page<NoteDto> getAllNotes(
            @RequestBody(required = false) NoteFilter filter,
            @Parameter(hidden = true) Pageable pageable
    ) {
        return service.getAll(filter, pageable);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Save new note")
    public NoteDto saveNote(@RequestBody @Valid NoteSaveDto dto) {
        return service.save(dto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update note by id")
    public NoteDto updateNote(
            @PathVariable String id,
            @RequestBody @Valid NoteSaveDto dto
    ) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete note by id")
    public void deleteNote(@PathVariable String id) {
        service.deleteById(id);
    }
}
