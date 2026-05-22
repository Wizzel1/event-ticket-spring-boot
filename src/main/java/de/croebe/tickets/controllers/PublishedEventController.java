package de.croebe.tickets.controllers;

import de.croebe.tickets.domain.dtos.ListPublishedEventsResponseDto;
import de.croebe.tickets.domain.entities.Event;
import de.croebe.tickets.mappers.EventMapper;
import de.croebe.tickets.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class PublishedEventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping(path = "/api/v1/published-events")
    public ResponseEntity<Page<ListPublishedEventsResponseDto>> listPublishedEvents(
            @RequestParam(required = false) String q,
            Pageable pageable
    ) {
        Page<Event> events;
        if (q != null) {
            events = eventService.searchPublishedEvents(q, pageable);
        } else {
            events = eventService.listPublishedEvents(pageable);
        }

        return ResponseEntity.ok(events.map(eventMapper::toListPublishedEventsResponseDto));
    }


}
