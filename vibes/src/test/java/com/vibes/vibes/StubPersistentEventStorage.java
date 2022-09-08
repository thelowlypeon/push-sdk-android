package com.vibes.vibes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class StubPersistentEventStorage implements PersistentEventStorageInterface {

    Set<Event> events = new TreeSet<>();

    @Override
    public EventCollection storedEvents() {
        return new EventCollection(this.events);
    }

    @Override
    public EventCollection storedEvents(TrackedEventType type) {
        Set<Event> eventsFiltered = new TreeSet<>();
        for (Event event : events) {
            if (event.getType().equals(type)) eventsFiltered.add(event);
        }
        return new EventCollection(eventsFiltered);
    }

    @Override
    public void persistEvents(List<Event> newEvents) {
        for (Event event : newEvents) {
            if (!this.events.contains(event)) {
                this.events.add(event);
            }
        }
    }

    @Override
    public void removeEvents(ArrayList<Event> events) {
        this.events.removeAll(events);
    }

    @Override
    public void removeEvent(Event event) {
        this.events.remove(event);
    }

    @Override
    public Event getMostRecent(TrackedEventType type) {
        EventCollection collection = storedEvents(type);
        if (collection.isEmpty()) {
            return null;
        }
        return collection.getSortedEvents().last();
    }
}