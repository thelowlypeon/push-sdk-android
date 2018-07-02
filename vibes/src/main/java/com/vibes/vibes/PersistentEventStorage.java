package com.vibes.vibes;

import java.util.ArrayList;
import java.util.List;

/**
 * An interface for being considered a PeristentEventStorage.
 */
interface PersistentEventStorageInterface {
    /**
     * The events currently stored locally.
     * @return the events
     */
    EventCollection storedEvents();

    /**
     * Persist events to local storage.
     * @param events the list of events to persist
     */
    void persistEvents(List<Event> events);

    /**
     * Removes events from local storage.
     * @param events am array of events to remove
     */
    void removeEvents(ArrayList<Event> events);

    /**
     * Removes an event from local storage
     * @param event the event to remove
     */
    void removeEvent(Event event);
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
     * @param localStorage a local storage mechanism for storing events
     */
    PersistentEventStorage(LocalStorage localStorage) {
        this.localStorage = localStorage;
    }

    /**
     * The events currently stored locally.
     * @return the events
     */
    public EventCollection storedEvents() {
        EventCollection collection = this.localStorage.get(LocalObjectKeys.storedEvents);
        if (collection == null) {
            collection = new EventCollection(new ArrayList<Event>());
        }
        return collection;
    }

    /**
     * Persist events to local storage.
     * @param events the list of events to persist
     */
    public synchronized void persistEvents(List<Event> events) {
        EventCollection collection = this.storedEvents();
        for (Event event: events) {
            if (!collection.getEvents().contains(event)) {
                collection.add(event);
                this.localStorage.put(LocalObjectKeys.storedEvents, collection);
            }
        }
    }

    /**
     * Removes events from local storage.
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
     * @param event the event to remove
     */
    public void removeEvent(Event event) {
        ArrayList<Event> events = new ArrayList<>();
        events.add(event);
        this.removeEvents(events);
    }
}
