package com.vibes.vibes;

import android.text.TextUtils;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static com.vibes.vibes.HTTPMethod.HEAD;

/**
 * A generic interface for parsing an HTTPResource from a string into some object.
 * @param <T> the type of resulting object, e.g. `String` or {@link Credential}.
 */
interface ResourceParser<T> {
    /**
     * Parses the passed-in text into an object of type T.
     * @param text the text received from the HTTP response
     */
    T parse(String text) throws Exception;
}

/**
 * A generic interface for parsing an HTTPResource from a JSON string into some object.
 * @param <T> the type of resulting object, e.g. `String` or {@link Credential}.
 */
interface JSONResourceParser<T> extends ResourceParser<T> {
    /**
     * Parses the passed-in JSON text into an object of type T.
     * @param text the JSON text received from the HTTP response
     */
    T parse(String text) throws JSONException;
}

/**
 * A custom enum for HTTP methods.
 */
enum HTTPMethod { GET, POST, PUT, HEAD, DELETE, PATCH }

/**
 * A custom request error to encapsulate multiple HTTP or Connection Error
 * (so far use for timeout: 429, 408, 504, ConnectionTimeoutException, UnknownHostException)
 */
enum VibesRequestError {
    TIMEOUT(-1000); // Negative value to avoid collision with HTTP errors

    private final int code;

    private VibesRequestError(int errorCode) {
        code = errorCode;
    }

    public int getCode() {
        return code;
    }
}

/**
 * A generic value object representing an HTTP resource that can be requested and parsed.
 * @param <T> represents the Type that will be returned for this resource, e.g. {@link Credential}
 */
public class HTTPResource<T> {
    /**
     * The path of the resource relative to the base url, e.g. "/devices"
     */
    private String path;

    /**
     * The HTTP method to use to fetch this resource, e.g. .POST
     */
    private HTTPMethod method;

    /**
     * The body to send when requesting this resource.
     */
    private String requestBody;

    /**
     * A set of headers to send when requesting this resource.
     */
    private HashMap<String, String> headers;

    /**
     * A parser to use to transform the returned response text for the resource into the generic
     * type, T.
     */
    private ResourceParser<T> parser;

    /**
     * Initialize this object.
     * @param path The path of the resource relative to the base url, e.g. "/devices"
     * @param method The HTTP method to use to fetch this resource, e.g. POST
     * @param requestBody The body to send when requesting this resource.
     * @param headers A set of headers to send when requesting this resource.
     * @param parser A parser to use to transform the returned response text for the resource into
     *                 the generic type, T.
     */
    public HTTPResource(String path, HTTPMethod method, String requestBody, HashMap<String, String> headers, ResourceParser<T> parser) {
        this.path = path;
        this.method = method;
        this.requestBody = requestBody;
        this.headers = headers;
        this.parser = parser;
    }

    /**
     * Returns the relative path for this resource.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the HTTP method for this resource.
     */
    public HTTPMethod getMethod() {
        return method;
    }

    /**
     * Returns the headers for this resource.
     */
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * Returns the request body for this resource.
     */
    public String getRequestBody() {
        return requestBody;
    }

    /**
     * Returns the parser for this resource.
     */
    public ResourceParser<T> getParser() {
        return parser;
    }

    /**
     * Represents an HTTP request as a copy-and-pasteable string that can be used with cURL.
     */
    public String curlString() {
        String baseCommand = "curl " + this.getPath();
        HTTPMethod method = this.getMethod();

        if (method == HEAD) {
            baseCommand += " --head";
        }

        ArrayList<String> command = new ArrayList<>();
        command.add(baseCommand);

        if (method != HTTPMethod.GET && method != HEAD) {
            command.add("-X " + method);
        }

        Set<String> headerKeys = this.headers.keySet();
        for (String key : headerKeys) {
            if (!key.equals("Cookie")) {
                String value = this.headers.get(key);
                command.add("-H '" + key + ": " + value + "'");
            }
        }

        command.add("-d '" + this.requestBody + "'");

        return TextUtils.join(" \\\n\t", command);
    }
}