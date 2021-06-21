package com.vibes.vibes;

import java.util.ArrayList;

class StubAPI implements VibesAPIInterface {
    ArrayList<StubResult> results;
    public StubAPI() {
    }

    public StubAPI(StubResult result) {
        this.results = new ArrayList<StubResult>(1);
        this.results.add(result);
    }

    public StubAPI(ArrayList<StubResult> results) {
        this.results = results;
    }

    @Override
    public <T> void request(HTTPResource<T> resource, ResourceListener<T> listener) {
        respond(listener);
    }

    @Override
    public <T> void request(HTTPResource<T> resource, ResourceListener<T> listener, boolean ignoreBaseUrl) {
        respond(listener);
    }

    @Override
    public <T> void request(String authToken, HTTPResource<T> resource, ResourceListener<T> listener) {
        respond(listener);
    }

    private <T> void respond(ResourceListener<T> listener) {
        StubResult<T> result = this.results.remove(0);
        switch (result.which) {
            case SUCCESS: listener.onSuccess(result.value);
            case FAILURE: listener.onFailure(result.statusCode, result.text);
        }
    }
}