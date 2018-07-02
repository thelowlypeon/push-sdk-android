package com.vibes.vibes;

class StubResult<T> {
    enum StubResultType { SUCCESS, FAILURE }

    StubResultType which;
    T value;
    String text;
    int statusCode;

    private StubResult(StubResultType which, T value, String text, int statusCode) {
        this.which = which;
        this.value = value;
        this.text = text;
        this.statusCode = statusCode;
    }

    public static <T> StubResult<T> success(T value) {
        return new StubResult(StubResultType.SUCCESS, value, "good job", 201);
    }

    public static <T> StubResult<T> failure(int statusCode, String text) {
        return new StubResult<T>(StubResultType.FAILURE, null, text, statusCode);
    }
}