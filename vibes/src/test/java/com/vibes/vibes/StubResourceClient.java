package com.vibes.vibes;

class StubResourceClient implements ResourceClientInterface {
    public Object lastResource;
    public Object lastListener;
    public ResourceBehavior lastBehavior;

    public StubResourceClient() {
    }

    @Override
    public <T> void request(HTTPResource<T> resource, ResourceBehavior behavior, ResourceListener<T> listener) {
        this.lastBehavior = behavior;
        this.lastResource = resource;
        this.lastListener = listener;
    }
}