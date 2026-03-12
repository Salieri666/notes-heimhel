package com.heimhel.notes.controller.api.v1;

import com.heimhel.notes.model.dto.DynamicQueryRequestDTO;
import com.heimhel.notes.service.DynamicQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Dynamic query", description = "Dynamic query API")
@RequestMapping(path = "/api/v1/dynamic", produces = MediaType.APPLICATION_JSON_VALUE)
public class DynamicQueryController {

    private final DynamicQueryService dynamicQueryService;

    @PostMapping("/query")
    @Operation(summary = "Get entities by filter")
    public ResponseEntity<?> query(@RequestBody DynamicQueryRequestDTO req) {
        return ResponseEntity.ok(dynamicQueryService.query(req));
    }

}
