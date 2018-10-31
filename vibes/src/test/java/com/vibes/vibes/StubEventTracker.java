package com.vibes.vibes;


import java.util.ArrayList;
import java.util.List;

public class StubEventTracker implements EventTracker {
    ArrayList<Event> eventsTracked = new ArrayList<Event>();

    @Override
    public void trackEvents(List<Event> event) {
        this.eventsTracked.addAll(event);
    }
}
