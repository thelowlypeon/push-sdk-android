package com.vibes.vibes;

class TestVibesListener<T> implements VibesListener<T> {
    @Override
    public void onSuccess(T value) { }

    @Override
    public void onFailure(String errorText) { }
}