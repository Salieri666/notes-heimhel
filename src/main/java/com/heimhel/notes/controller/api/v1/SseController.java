package com.heimhel.notes.controller.api.v1;

import com.heimhel.notes.model.dto.SseTextMessageDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "SSE", description = "SSE API")
@RequestMapping("/api/v1/events")
public class SseController {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(120_000L);

        //should add handler for disconnected clients
        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            log.info("Client disconnected. Active connections: {}", emitters.size());
        });
        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            emitter.complete();
            log.info("Connection timeout. Active connections: {}", emitters.size());
        });
        emitter.onError(ex -> {
            emitters.remove(emitter);
            emitter.complete();
            log.error("Connection error: {}", ex.getMessage());
        });

        emitters.add(emitter);

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Connected to SSE"));
        } catch (IOException e) {
            log.error("Error sending connected event: {}", e.getMessage());
            emitter.completeWithError(e);
        }

        return emitter;
    }

    @PostMapping(value = "/broadcast", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> broadcast(@RequestBody SseTextMessageDTO messageDTO) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(messageDTO));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }

        emitters.removeAll(deadEmitters);
        return ResponseEntity.ok("Message broadcasted");
    }
}
