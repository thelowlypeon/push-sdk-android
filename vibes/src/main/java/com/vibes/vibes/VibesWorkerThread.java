package com.vibes.vibes;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * A custom HandlerThread to handle making network requests in a background thread and reporting
 * results back on the calling thread (usually the UI thread).
 */
class VibesWorkerThread extends HandlerThread {
    private static final String TAG = "VibesWorkerThread";
    private static final String DEFAULT_MESSAGE = "Unknown error";

    /**
     * A handler for performing work on a separate, background thread.
     */
    private Handler workerHandler;

    /**
     * A handler for responding with work results on the calling thread (usually the UI thread).
     */
    private Handler responseHandler;

    /**
     * Initialize this object.
     * @param responseHandler the response handler to use (a Handler on the main thread).
     */
    public VibesWorkerThread(Handler responseHandler) {
        super(TAG);
        this.responseHandler = responseHandler;
    }

    /**
     * Prepare the worker handler.
     */
    public void prepareHandler() {
        this.workerHandler = new Handler(this.getLooper());
    }

    /**
     * Returns the worker handler. This is used from tests (thus the `protected` ACL).
     */
    protected Handler getWorkerHandler() {
        return this.workerHandler;
    }

    /**
     * Returns the response handler. This is used from tests (thus the `protected` ACL).
     */
    protected Handler getResponseHandler() {
        return this.responseHandler;
    }

    /**
     * Makes an unauthenticated API request on a background thread.
     * @param api the {@link VibesAPIInterface} object to use to make a request
     * @param resource the {@link HTTPResource} to request
     * @param listener the {@link ResourceListener} to notify about the result of the request
     * @param <T> the Type of the resource, e.g. {@link Credential}
     */
    public <T> void request(final VibesAPIInterface api, final HTTPResource<T> resource, final ResourceListener<T> listener) {
        this.workerHandler.post(new Runnable() {
            @Override
            public void run() {
                api.request(resource, listener);
            }
        });
    }

    /**
     * Makes an authenticated API request on a background thread.
     * @param authToken the auth token to use for the request
     * @param api the {@link VibesAPIInterface} object to use to make a request
     * @param resource the {@link HTTPResource} to request
     * @param listener the {@link ResourceListener} to notify about the result of the request
     * @param <T> the Type of the resource, e.g. {@link Credential}
     */
    public <T> void request(final String authToken, final VibesAPIInterface api, final HTTPResource<T> resource, final ResourceListener<T> listener) {
        this.workerHandler.post(new Runnable() {
            @Override
            public void run() {
                api.request(authToken, resource, listener);
            }
        });
    }

    /**
     * Notifies about successful results on the response (UI) thread.
     * @param completion the completion to notify
     * @param value the parsed value to return from the request
     * @param <T> the Type of the parsed value, e.g. {@link Credential}
     */
    public <T> void onSuccess(final VibesListener<T> completion, final T value) {
        responseHandler.post(new Runnable() {
            @Override
            public void run() {
                completion.onSuccess(value);
            }
        });
    }

    /**
     * Notifies about failed results on the response (UI) thread.
     * @param completion the completion to notify
     * @param errorText the error text to return, if any
     */
    public <T> void onFailure(final VibesListener<T> completion, final String errorText) {
        responseHandler.post(new Runnable() {
            @Override
            public void run() {
                String msg = errorText;
                if (msg == null) {
                    msg = DEFAULT_MESSAGE;
                }
                completion.onFailure(msg);
            }
        });
    }
}
