package com.vibes.vibes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StubPersistentEventStorage implements PersistentEventStorageInterface {

    ArrayList<Event> events = new ArrayList<>();

    @Override
    public EventCollection storedEvents() {
        return new EventCollection(this.events);
    }

    @Override
    public EventCollection storedEvents(TrackedEventType type) {
        ArrayList<Event> eventsFiltered = new ArrayList<>();
        for (Event event : events) {
            if (event.getType().equals(type)) eventsFiltered.add(event);
        }
        Collections.sort(events);
        return new EventCollection(eventsFiltered);
    }

    @Override
    public void persistEvents(List<Event> newEvents) {
        for (Event existing : this.events) {
            Event comparison = newEvents.get(0);
            if (comparison.getTimestampSecs() != null && existing.getTimestampSecs() != null && comparison.getType().equals(existing.getType()) && comparison.getTimestampSecs().equals(existing.getTimestampSecs())) {
                return;
            }
        }
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
        return collection.getEvents().get(collection.getEvents().size() - 1);
    }
}
