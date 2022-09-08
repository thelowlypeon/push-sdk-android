package com.vibes.vibes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * An interface for being considered a PeristentEventStorage.
 */
interface PersistentEventStorageInterface {
    /**
     * The events currently stored locally.
     *
     * @return the events
     */
    EventCollection storedEvents();

    /**
     * The events currently stored locally filtered by type.
     *
     * @param type the type to filter by
     * @return the events
     */
    EventCollection storedEvents(TrackedEventType type);

    /**
     * Persist events to local storage.
     *
     * @param events the list of events to persist
     */
    void persistEvents(List<Event> events);

    /**
     * Removes events from local storage.
     *
     * @param events am array of events to remove
     */
    void removeEvents(ArrayList<Event> events);

    /**
     * Removes an event from local storage
     *
     * @param event the event to remove
     */
    void removeEvent(Event event);

    /**
     * This returns the most recent Event record from storage that matches the event type supplied
     *
     * @param type
     * @return
     */
    Event getMostRecent(TrackedEventType type);
}


/**
 * An object to take care of persisting incoming events when they are received
 * and removing them once they have been successfully sent to Vibes.
 */
class PersistentEventStorage implements PersistentEventStorageInterface {
    /**
     * The local storage mechanism we are using for storing events
     */
    private LocalStorage localStorage;

    /**
     * Initializes a new storage object
     *
     * @param localStorage a local storage mechanism for storing events
     */
    PersistentEventStorage(LocalStorage localStorage) {
        this.localStorage = localStorage;
    }

    /**
     * The events currently stored locally.
     *
     * @return the events
     */
    public EventCollection storedEvents() {
        EventCollection collection = this.localStorage.get(LocalObjectKeys.storedEvents);
        if (collection == null) {
            collection = new EventCollection(new TreeSet<Event>());
        }
        return collection;
    }

    /**
     * The events currently stored locally filtered by type.
     *
     * @param type the type to filter by
     * @return the events
     */
    public EventCollection storedEvents(TrackedEventType type) {
        EventCollection collection = this.localStorage.get(LocalObjectKeys.storedEvents);
        if (collection == null) {
            collection = new EventCollection(new TreeSet<Event>());
        }
        Set<Event> events = new TreeSet<>();
        for (Event event : collection.getEvents()) {
            if (event.getType().equals(type)) events.add(event);
        }
        return new EventCollection(events);
    }

    /**
     * Persist events to local storage.
     *
     * @param newEvents the list of events to persist
     */
    public synchronized void persistEvents(List<Event> newEvents) {
        EventCollection collection = this.storedEvents();
        for (Event event : newEvents) {
            if (!collection.getEvents().contains(event)) {
                collection.add(event);
                this.localStorage.put(LocalObjectKeys.storedEvents, collection);
            }
        }
    }

    /**
     * Removes events from local storage.
     *
     * @param events am array of events to remove
     */
    public synchronized void removeEvents(ArrayList<Event> events) {
        EventCollection collection = this.storedEvents();
        for (Event event : events) {
            collection.remove(event);
        }
        this.localStorage.put(LocalObjectKeys.storedEvents, collection);
    }

    /**
     * Removes an event from local storage
     *
     * @param event the event to remove
     */
    public void removeEvent(Event event) {
        ArrayList<Event> events = new ArrayList<>();
        events.add(event);
        this.removeEvents(events);
    }

    /**
     * @param type
     * @return
     * @see PersistentEventStorage#getMostRecent(TrackedEventType)
     */
    public Event getMostRecent(TrackedEventType type) {
        EventCollection collection = storedEvents(type);
        if (collection.isEmpty()) {
            return null;
        }
        return collection.getSortedEvents().last();
    }
}
