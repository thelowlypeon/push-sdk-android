package com.vibes.vibes;

import java.util.ArrayList;
import java.util.List;

public class StubPersistentEventStorage implements PersistentEventStorageInterface {

    ArrayList<Event> events = new ArrayList<>();

    @Override
    public EventCollection storedEvents() {
        return new EventCollection(this.events);
    }

    @Override
    public void persistEvents(List<Event> event) {
        this.events.addAll(event);
    }

    @Override
    public void removeEvents(ArrayList<Event> events) {
        this.events.removeAll(events);
    }

    @Override
    public void removeEvent(Event event) {
        this.events.remove(event);
    }
}
