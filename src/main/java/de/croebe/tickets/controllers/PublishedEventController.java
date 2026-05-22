package de.croebe.tickets.controllers;

import de.croebe.tickets.domain.dtos.GetPublishedEventDetailsResponseDto;
import de.croebe.tickets.domain.dtos.ListPublishedEventsResponseDto;
import de.croebe.tickets.domain.entities.Event;
import de.croebe.tickets.mappers.EventMapper;
import de.croebe.tickets.services.EventService;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/published-events")
@RequiredArgsConstructor
public class PublishedEventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping
    public ResponseEntity<Page<ListPublishedEventsResponseDto>> listPublishedEvents(
            @RequestParam(required = false) String q,
            Pageable pageable) {

        Page<Event> events;
        if (null != q && !q.trim().isEmpty()) {
            events = eventService.searchPublishedEvents(q, pageable);
        } else {
            events = eventService.listPublishedEvents(pageable);
        }

        return ResponseEntity.ok(events.map(eventMapper::toListPublishedEventsResponseDto));
    }

    @GetMapping(path = "/{eventId}")
    public ResponseEntity<GetPublishedEventDetailsResponseDto> getPublishedEventDetails(
            @PathVariable UUID eventId
    ) {
        return eventService.getPublishedEventById(eventId)
                .map(eventMapper::toGetPublishedEventDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}