package de.croebe.tickets.controllers;

import de.croebe.tickets.domain.CreateEventRequest;
import de.croebe.tickets.domain.dtos.CreateEventRequestDto;
import de.croebe.tickets.domain.dtos.CreateEventResponseDto;
import de.croebe.tickets.domain.entities.Event;
import de.croebe.tickets.mappers.EventMapper;
import de.croebe.tickets.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventMapper eventMapper;
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<CreateEventResponseDto> createEvent(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateEventRequestDto createEventRequestDto
    ) {
        CreateEventRequest request = eventMapper.fromDto(createEventRequestDto);
        UUID userId = UUID.fromString(jwt.getSubject());
        Event event = eventService.createEvent(userId, request);
        CreateEventResponseDto responseDto = eventMapper.toDto(event);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
