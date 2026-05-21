package de.croebe.tickets.services.impl;

import de.croebe.tickets.domain.CreateEventRequest;
import de.croebe.tickets.domain.entities.Event;
import de.croebe.tickets.domain.entities.TicketType;
import de.croebe.tickets.domain.entities.User;
import de.croebe.tickets.exceptions.UserNotFoundException;
import de.croebe.tickets.repositories.EventRepository;
import de.croebe.tickets.repositories.UserRepository;
import de.croebe.tickets.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
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
    public Page<Event> getEventsByOrganizer(UUID organizerId, Pageable pageable) {
        return eventRepository.findByOrganizerId(organizerId, pageable);
    }
}
