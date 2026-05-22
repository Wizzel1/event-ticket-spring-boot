package de.croebe.tickets.services.impl;

import de.croebe.tickets.domain.CreateEventRequest;
import de.croebe.tickets.domain.UpdateEventRequest;
import de.croebe.tickets.domain.UpdateTicketTypeRequest;
import de.croebe.tickets.domain.entities.Event;
import de.croebe.tickets.domain.entities.EventStatus;
import de.croebe.tickets.domain.entities.TicketType;
import de.croebe.tickets.domain.entities.User;
import de.croebe.tickets.exceptions.EventNotFoundException;
import de.croebe.tickets.exceptions.EventUpdateException;
import de.croebe.tickets.exceptions.TicketTypeNotFoundException;
import de.croebe.tickets.exceptions.UserNotFoundException;
import de.croebe.tickets.repositories.EventRepository;
import de.croebe.tickets.repositories.UserRepository;
import de.croebe.tickets.services.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Event createEvent(UUID organizerId, CreateEventRequest request) {
        User organizer = userRepository.findById(organizerId).orElseThrow(() -> {
            var message = "User with ID " + organizerId + " not found";
            return new UserNotFoundException(message);
        });

        Event eventToCreate = new Event();
        List<TicketType> ticketTypes = request.getTicketTypes().stream().map(ticketRequest -> {
            TicketType ticketTypeToCreate = new TicketType();
            ticketTypeToCreate.setName(ticketRequest.getName());
            ticketTypeToCreate.setPrice(ticketRequest.getPrice());
            ticketTypeToCreate.setDescription(ticketRequest.getDescription());
            ticketTypeToCreate.setTotalAvailable(ticketRequest.getTotalAvailable());
            ticketTypeToCreate.setEvent(eventToCreate);
            return ticketTypeToCreate;
        }).toList();

        eventToCreate.setName(request.getName());
        eventToCreate.setStart(request.getStart());
        eventToCreate.setEnd(request.getEnd());
        eventToCreate.setVenue(request.getVenue());
        eventToCreate.setSalesStart(request.getSalesStart());
        eventToCreate.setSalesEnd(request.getSalesEnd());
        eventToCreate.setStatus(request.getStatus());
        eventToCreate.setOrganizer(organizer);
        eventToCreate.setTicketTypes(ticketTypes);
        return eventRepository.save(eventToCreate);
    }

    @Override
    public Page<Event> listEventsForOrganizer(UUID organizerId, Pageable pageable) {
        return eventRepository.findByOrganizerId(organizerId, pageable);
    }

    @Override
    public Optional<Event> getEventForOrganizer(UUID organizerId, UUID id) {
        return eventRepository.findByIdAndOrganizerId(id, organizerId);
    }

    @Override
    @Transactional
    public Event updateEventForOrganizer(UUID organizerId, UUID eventId, UpdateEventRequest updateEventReq) {
        if (updateEventReq.getId() == null) {
            throw new EventUpdateException("Event ID must be provided for update");
        }
        if (!eventId.equals(updateEventReq.getId())) {
            throw new EventUpdateException("Cannot update ID of an event");
        }

        Event existingEvent = eventRepository
                .findByIdAndOrganizerId(eventId, organizerId)
                .orElseThrow(() -> {
                    String message = String.format("Event with ID %s not found", eventId);
                    return new EventNotFoundException(message);
                });

        existingEvent.setName(updateEventReq.getName());
        existingEvent.setStart(updateEventReq.getStart());
        existingEvent.setEnd(updateEventReq.getEnd());
        existingEvent.setVenue(updateEventReq.getVenue());
        existingEvent.setSalesStart(updateEventReq.getSalesStart());
        existingEvent.setSalesEnd(updateEventReq.getSalesEnd());
        existingEvent.setStatus(updateEventReq.getStatus());

        Set<UUID> ticketTypeIds = updateEventReq
                .getTicketTypes()
                .stream()
                .map(UpdateTicketTypeRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existingEvent
                .getTicketTypes()
                .removeIf(ticketType -> !ticketTypeIds.contains(ticketType.getId()));

        Map<UUID, TicketType> existingTicketTypeIndex = existingEvent
                .getTicketTypes()
                .stream()
                .collect(Collectors.toMap(TicketType::getId, Function.identity()));


        for (UpdateTicketTypeRequest typeRequest : updateEventReq.getTicketTypes()) {
            if (null == typeRequest.getId()) {
                //Create a new ticket type
                TicketType ticketTypeToCreate = new TicketType();
                ticketTypeToCreate.setName(typeRequest.getName());
                ticketTypeToCreate.setPrice(typeRequest.getPrice());
                ticketTypeToCreate.setDescription(typeRequest.getDescription());
                ticketTypeToCreate.setTotalAvailable(typeRequest.getTotalAvailable());
                ticketTypeToCreate.setEvent(existingEvent);
                existingEvent.getTicketTypes().add(ticketTypeToCreate);
            } else if (existingTicketTypeIndex.containsKey(typeRequest.getId())) {
                //Update existing ticket type
                TicketType existing = existingTicketTypeIndex.get(typeRequest.getId());
                existing.setName(typeRequest.getName());
                existing.setPrice(typeRequest.getPrice());
                existing.setDescription(typeRequest.getDescription());
                existing.setTotalAvailable(typeRequest.getTotalAvailable());
            } else {
                String message = String.format("Ticket type with ID %s does not exist", typeRequest.getId());
                throw new TicketTypeNotFoundException(message);
            }
        }

        return eventRepository.save(existingEvent);
    }


    @Override
    @Transactional
    public void deleteEventForOrganizer(UUID organizerId, UUID eventId) {
        getEventForOrganizer(organizerId, eventId).ifPresent(eventRepository::delete);
    }

    @Override
    public Page<Event> listPublishedEvents(UUID organizerId, Pageable pageable) {
        return eventRepository.findByStatus(EventStatus.PUBLISHED, pageable);
    }
}
