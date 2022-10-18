package com.vibes.vibes;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * An interface that defines what it means to be a {@link ResourceClient}.
 */
interface ResourceClientInterface {
    /**
     * Request an HTTP resource, and notify the listener as to the result.
     *
     * @param resource a resource to request
     * @param behavior a resource behavior to apply to the request
     * @param listener a listener to notify about the result of the request
     * @param <T>      the type that is going to be returned on success, e.g. {@link Credential}
     */
    <T> void request(HTTPResource<T> resource, ResourceBehavior behavior, ResourceListener<T> listener);

    /**
     * Request an HTTP resource, and notify the listener as to the result.
     *
     * @param resource      a resource to request
     * @param behavior      a resource behavior to apply to the request
     * @param listener      a listener to notify about the result of the request
     * @param <T>           the type that is going to be returned on success, e.g. {@link Credential}
     * @param ignoreBaseUrl a flag to ignore the system base URL and use the path as specified
     */
    <T> void request(HTTPResource<T> resource, ResourceBehavior behavior, ResourceListener<T> listener, boolean ignoreBaseUrl);
}

/**
 * An interface for modifying a connection before it sends a request.
 */
interface ResourceBehavior {
    /**
     * Modifies the connection (e.g. to add a request header) before using it to make a request.
     *
     * @param connection the URLConnection to modify
     */
    void modifyConnection(URLConnection connection);
}

/**
 * A generic interface for being notified about the result of requesting an HTTP Resource.
 *
 * @param <T> the type that will be returned on success, e.g. {@link Credential}
 */
interface ResourceListener<T> {
    /**
     * Indicates that the resource was successfully requested and parsed.
     *
     * @param value the parsed Resource, e.g. a {@link Credential} object.
     */
    void onSuccess(T value);

    /**
     * Indicates that the resource was not successfully requested or parsed.
     *
     * @param statusCode the HTTP status code of the request, e.g. 404
     * @param errorText  the error text returned, if any
     */
    void onFailure(int statusCode, String errorText);
}

/**
 * An HTTP client for requesting {@link HTTPResource} objects.
 */
class ResourceClient implements ResourceClientInterface {
    private static final String TAG = "ResourceClient";

    /**
     * The base URL for making HTTP requests, e.g. "http://example.com"
     */
    protected String baseURL;

    /**
     * Logger used when logging http traffic
     */
    private VibesLogger logger;

    /**
     * An optional SSL socket factory to use, if not the default
     */
    private SSLSocketFactory sslSocketFactory;

    /**
     * Initialize this object.
     *
     * @param baseURL the base URL for making HTTP requests, e.g. "http://example.com"
     */
    public ResourceClient(String baseURL, VibesLogger logger) {
        this.baseURL = baseURL;
        this.logger = logger;
    }

    /**
     * Initialize this object.
     *
     * @param baseURL          the base URL for making HTTP requests, e.g. "http://example.com"
     * @param sslSocketFactory an optional SSL socket factory to use, if not the default
     */
    public ResourceClient(String baseURL, VibesLogger logger, SSLSocketFactory sslSocketFactory) {
        this(baseURL, logger);
        this.sslSocketFactory = sslSocketFactory;
    }

    /**
     * Request an HTTP resource, and notify the listener as to the result. This is a convenience
     * method for making a request without a {@link ResourceBehavior}.
     *
     * @param resource a resource to request
     * @param listener a listener to notify about the result of the request
     * @param <T>      the type that is going to be returned on success, e.g. {@link Credential}
     */
    public <T> void request(HTTPResource<T> resource, ResourceListener<T> listener) {
        this.request(resource, null, listener);
    }

    /**
     * Same as {@link #request(HTTPResource, ResourceBehavior, ResourceListener, boolean)}, with <code>ignoreBaseUrl</code> set to false.
     */
    public <T> void request(HTTPResource<T> resource, ResourceBehavior behavior, ResourceListener<T> listener) {
        this.request(resource, behavior, listener, false);
    }

    /**
     * Request an HTTP resource, and notify the listener as to the result. It uses a
     * {@link ResourceBehavior} to modify the request before it is sent.
     *
     * @param resource a resource to request
     * @param behavior a resource behavior to apply to the request
     * @param listener a listener to notify about the result of the request
     * @param <T>      the type that is going to be returned on success, e.g. {@link Credential}
     * @param ignoreBaseUrl enables the invocation of a non-vibes URL but retaining every other behaviour
     */
    public <T> void request(HTTPResource<T> resource, ResourceBehavior behavior, ResourceListener<T> listener, boolean ignoreBaseUrl) {
        int responseCode = -1;

        try {
            URL url = null;
            if (ignoreBaseUrl) {
                url = new URL(resource.getPath());
            } else {
                url = new URL(baseURL + resource.getPath());
            }
            this.logger.log(resource);

            if (sslSocketFactory != null) {
                HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
            }
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            if (resource.getMethod() == HTTPMethod.DELETE || resource.getMethod() == HTTPMethod.GET) {
                connection.setDoOutput(false);
            } else {
                connection.setDoOutput(true);
            }
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod(resource.getMethod().toString());
            for (Map.Entry<String, String> entry : resource.getHeaders().entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            if (behavior != null) {
                behavior.modifyConnection(connection);
            }

            String requestBody = resource.getRequestBody();
            if (requestBody != null) {
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                writer.write(requestBody);
                writer.close();
            }
            HTTPResponse response = new HTTPResponse(connection);
            responseCode = response.getCode();
            this.logger.log(response);
            String responseText = response.getText();
            if (response.isSuccessful()) {
                try {
                    listener.onSuccess(resource.getParser().parse(responseText));
                } catch (Exception exception) {
                    listener.onFailure(responseCode, "Failed: " + exception.toString());
                    this.logger.log(exception);
                }
            } else if (response.isTimeout()) {
                this.logger.log(new Exception("HTTP Error: " + responseCode + " " + responseText));
                listener.onFailure(VibesRequestError.TIMEOUT.getCode(), responseText);
            } else {
                this.logger.log(new Exception("HTTP Error: " + responseCode + " " + responseText));
                listener.onFailure(responseCode, responseText);
            }
        } catch (MalformedURLException exception) {
            listener.onFailure(responseCode, "Failed - bad URL: " + exception.toString());
            this.logger.log(exception);
        } catch (UnknownHostException | ConnectException exception) {
            listener.onFailure(VibesRequestError.TIMEOUT.getCode(), "Host is unreachable");
            this.logger.log(exception);
        } catch (IOException exception) {
            listener.onFailure(responseCode, "Failed - IO problem: " + exception.toString());
            this.logger.log(exception);
        }
    }
}
