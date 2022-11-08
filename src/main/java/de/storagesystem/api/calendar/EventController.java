package de.storagesystem.api.calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/events")
public class EventController {
    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/get")
    public @ResponseBody Optional<Event> getEvents(@RequestParam(value = "id") Long id) {
        return eventRepository.findById(id);
    }

    @DeleteMapping("/delete")
    public @ResponseBody String deleteEvent(@RequestParam(value = "id") Long id) {
        eventRepository.deleteById(id);
        return "Event deleted";
    }

    @PostMapping("/edit")
    public @ResponseBody String editEvent(
            @RequestParam(value = "id") long id,
            @RequestParam(value = "edit") boolean edit,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "description", defaultValue = "") String description,
            @RequestParam(value = "startDate", defaultValue = "") LocalDateTime startDate,
            @RequestParam(value = "endDate", defaultValue = "") LocalDateTime endDate,
            @RequestParam(value = "eventColor", defaultValue = "#555") String eventColor,
            @RequestParam(value = "type", defaultValue = "1") EventType type,
            @RequestParam(value = "allDay", defaultValue = "false") boolean allDay) {

        System.out.println("Editing Event: " + name);
        Optional<Event> event = eventRepository.findById(id);
        Event e = event.orElseGet(Event::new);
        setEventProperties(name, description, startDate, endDate, eventColor, type, allDay, e);
        return "Event edited";
    }

    @PostMapping("/create")
    public @ResponseBody String createEvent(@RequestBody Event event) {
        eventRepository.save(event);
        return "Event created";
    }

    @PostMapping("/post")
    public @ResponseBody String addEvent(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "description", defaultValue = "") String description,
            @RequestParam(value = "startDate", defaultValue = "") LocalDateTime startDate,
            @RequestParam(value = "endDate", defaultValue = "") LocalDateTime endDate,
            @RequestParam(value = "eventColor", defaultValue = "#555") String eventColor,
            @RequestParam(value = "type", defaultValue = "1") EventType type,
            @RequestParam(value = "allDay", defaultValue = "false") boolean allDay) {


        System.out.println("Adding Event: " + name);
        setEventProperties(name, description, startDate, endDate, eventColor, type, allDay, new Event());
        return "Event added";
    }

    private void setEventProperties(
            String name,
            String description,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String eventColor,
            EventType type,
            boolean allDay,
            Event e) {
        e.setName(name);
        e.setDescription(description);
        e.setStartDate(startDate);
        e.setEndDate(endDate);
        e.setEventColor(eventColor);
        e.setType(type);
        e.setAllDay(allDay);
        eventRepository.save(e);
    }
}
