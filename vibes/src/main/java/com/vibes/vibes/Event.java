package com.vibes.vibes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

/**
 * A data object to represent an Event to send to Vibes.
 */
public class Event implements Comparable<Event> {
    /**
     * The type of the event
     */
    private TrackedEventType type;

    /**
     * Event unique id (string representation)
     */
    private String uuid = UUID.randomUUID().toString();

    /**
     * Full timestamp the Event occurred
     */
    private Date timestamp;

    /**
     * A set of arbitrary key/values to send along with the event
     */
    private HashMap<String, String> properties;

    /**
     * The time the Event occurred to the second only
     */
    private String timestampSecs;

    /**
     * Initialize this object.
     *
     * @param type       the type of the event
     * @param properties a set of arbitrary key/values to send along with the event
     */
    public Event(TrackedEventType type, HashMap<String, String> properties) {
        this(type, properties, new Date());
    }

    /**
     * Initialize this object.
     *
     * @param type the type of the event
     */
    public Event(TrackedEventType type) {
        this(type, new HashMap<String, String>(), new Date());
    }

    /**
     * Initialize this object.
     *
     * @param type       the type of the event
     * @param properties a set of arbitrary key/values to send along with the event
     * @param timestamp  the time the Event occurred
     */
    public Event(TrackedEventType type, HashMap<String, String> properties, Date timestamp) {
        this.type = type;
        this.properties = properties;
        this.timestamp = timestamp;
        if (this.timestamp != null) {
            this.timestampSecs = ISODateFormatter.toSecondsString(timestamp);
        }
    }

    /**
     * The type of the event
     */
    TrackedEventType getType() {
        return this.type;
    }

    /**
     * Event unique id (string representation)
     */
    String getUUID() {
        return this.uuid;
    }

    /**
     * The time the Event occurred
     */
    Date getTimestamp() {
        return this.timestamp;
    }

    public String getTimestampSecs() {
        return this.timestampSecs;
    }

    /**
     * A set of arbitrary key/values to send along with the event
     */
    HashMap<String, String> getProperties() {
        return this.properties;
    }

    /**
     * Serializes an Event to a JSON string.
     *
     * @return a String of valid JSON
     * @throws JSONException
     */
    public JSONObject serialize() throws JSONException {
        JSONObject json = new JSONObject();
        JSONObject attsJson = new JSONObject(this.properties);
        json.put("uuid", this.uuid);
        json.put("type", this.type.name().toLowerCase(Locale.ENGLISH));
        json.put("attributes", attsJson);
        json.put("timestamp", ISODateFormatter.toISOString(this.timestamp));
        return json;
    }

    /**
     * Deserializes a string of valid JSON to an Event object.
     *
     * @param jsonString a String of valid JSON
     * @return an Event
     * @throws JSONException
     */
    public static Event createInstance(String jsonString) throws JSONException {
        JSONObject json = new JSONObject(jsonString);

        String uuid = json.getString("uuid");
        TrackedEventType type = TrackedEventType.valueOf(json.getString("type").toUpperCase(Locale.ENGLISH));
        JSONObject atts = json.getJSONObject("attributes");
        HashMap<String, String> properties = JSONHelpers.jsonTo(atts);
        String dateString = json.getString("timestamp");
        Event event = new Event(type, properties, ISODateFormatter.fromISOString(dateString));
        event.uuid = uuid;
        return event;
    }

    /**
     * Checks if an Object is equal to this Event.
     *
     * @param other an Object, ideally an Event, to compare against.
     * @return true on equal; false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof Event)) {
            return false;
        }

        Event event = (Event) other;
        return this.uuid.equals(event.uuid);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public int compareTo(Event event) {
        if (this.getTimestampSecs() == null || this.getType() == null) {
            return -1;
        }

        if (event.getTimestampSecs() == null || event.getType() == null) {
            return 1;
        }
        if (this.getType().equals(event.getType())) {
            return this.getTimestampSecs().compareTo(event.getTimestampSecs());
        } else {
            return this.getType().compareTo(event.getType());
        }
    }

    @Override
    public String toString() {
        return "Event{" +
                "type=" + type +
                ", uuid='" + uuid + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", timestampSecs='" + timestampSecs + '\'' +
                ", properties='" + properties + '\'' +
                '}';
    }
}