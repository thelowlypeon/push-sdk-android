package com.vibes.vibes;


class TestResourceListener<T> implements ResourceListener<T> {
    @Override
    public void onSuccess(T value) {
    }

    @Override
    public void onFailure(int responseCode, String errorText) {
    }
}
