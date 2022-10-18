package com.vibes.vibes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * A simple object to encapsulate the notion of an array of Events.
 */
class EventCollection {
    /**
     * The events in the collection
     */
    private TreeSet<Event> events;

    /**
     * Initialize this object
     *
     * @param events the initial events to contain
     */
    EventCollection(Collection<Event> events) {
        if (events == null) {
            this.events = new TreeSet<>();
        } else {
            this.events = new TreeSet<>(events);
        }
    }

    /**
     * The events in the collection
     */
    Collection<Event> getEvents() {
        return this.events;
    }

    /**
     * Returns a List version of the events in case indexing is important.
     */
    ArrayList<Event> getIndexedEvents() {
        return new ArrayList<>(this.events);
    }

    /**
     * Returns a Sorted version of the events in case ordering is important.
     */
    TreeSet<Event> getSortedEvents() {
        return this.events;
    }

    /**
     * Add an event to the collection.
     *
     * @param event the event to add
     */
    void add(Event event) {
        this.events.add(event);
    }

    /**
     * Removes an event from the collection
     *
     * @param event the event to remove
     */
    void remove(Event event) {
        this.events.remove(event);
    }

    public boolean isEmpty() {
        if (this.events == null || this.events.isEmpty()) {
            return true;
        }
        return false;
    }

    static class EventCollectionObjectFactory implements JSONObjectFactory<EventCollection> {
        /**
         * Serializes an EventCollection to a JSON string.
         *
         * @param collection the EventCollection to serialize
         * @return a String of valid JSON
         * @throws JSONException
         */
        public String serialize(EventCollection collection) throws JSONException {
            JSONArray eventsJSON = new JSONArray();
            for (Event event : collection.events) {
                eventsJSON.put(event.serialize());
            }
            JSONObject json = new JSONObject();
            json.put("events", eventsJSON);
            return json.toString(2);
        }

        /**
         * Deserializes a string of valid JSON to an EventCollection object.
         *
         * @param jsonString a String of valid JSON
         * @return an EventCollection
         * @throws JSONException
         */
        public EventCollection createInstance(String jsonString) throws JSONException {
            JSONObject json = new JSONObject(jsonString);
            JSONArray eventsJSON = json.getJSONArray("events");
            Set<Event> events = new TreeSet<>();
            for (int i = 0; i < eventsJSON.length(); i++) {
                Event event = Event.createInstance(eventsJSON.getString(i));
                events.add(event);
            }
            return new EventCollection(events);
        }
    }
}