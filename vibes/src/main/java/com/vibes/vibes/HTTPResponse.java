package com.vibes.vibes;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A response object containing the HTTP response from executing a
 * request using an HTTPResource.
 */
public class HTTPResponse {
    private static final String TAG = "HTTPResponse";

    /**
     * The HTTP connection used for the request associated
     * with this response.
     */
    private HttpURLConnection connection;

    /**
     * Initializes this object.
     * @param connection - the http connection used for the executed request.
     */
    HTTPResponse(HttpURLConnection connection) {
        this.connection = connection;
    }

    /**
     * The HTTP status code of the response;
     */
    int getCode() {
        try {
            return this.connection.getResponseCode();
        } catch (Exception e) {
            Vibes.getCurrentLogger().log(e);
            return -1;
        }
    }

    /**
     * Whether or not the response is successful.
     * (the http status code is within the 200-range)
     */
    boolean isSuccessful() {
        int code = this.getCode();
        return code >= 200 && code < 300;
    }

    /**
     * Whether or not the response timed out.
     */
    boolean isTimeout() {
        int code = this.getCode();
        return (code == 429 || code == 408 || code == 500 || code == 502 || code == 504);
    }

    /**
     * The body of the http response. Using the input stream or error stream,
     * depending on if the response is successful or not.
     */
    String getText() {
        try {
            if (this.isSuccessful()) {
                return readHttpInputStreamToString(this.connection.getInputStream());
            }
            return readHttpInputStreamToString(this.connection.getErrorStream());
        } catch (IOException e) {
            Vibes.getCurrentLogger().log(e);
        }
        return "";
    }


    public Map<String, List<String>> getHeaders() {
        return this.connection.getHeaderFields();
    }


    public String getUrl() {
        return connection.getURL().toString();
    }

    /**
     * Represents an HTTP response in output that looks like what comes from cURL.
     */
    public String curlString() {
        try {
            String status = this.connection.getResponseMessage();
            ArrayList<String> output = new ArrayList<>();
            output.add("HTTP/1.1 " + this.getCode() + " " + status);
            Set<String> headerKeys = this.connection.getHeaderFields().keySet();
            for (String key : headerKeys) {
                String value = this.connection.getHeaderField(key);
                output.add(key + ": " + value);
            }

            return TextUtils.join("\n\t", output);
        } catch (Exception e) {
            Vibes.getCurrentLogger().log(e);
        }
        return "";
    }

    /**
     * Reads an input stream.
     *
     * NOTE: before calling this function, ensure that the connection is already be open, and any
     * writes to the connection's output stream should have already been completed.
     *
     * @param stream the input stream to read
     * @return String containing the body of the connection response or null if the input stream
     *         could not be read correctly
     */
    private String readHttpInputStreamToString(InputStream stream) {
        String result = null;
        StringBuffer buffer = new StringBuffer();

        try {
            stream = new BufferedInputStream(stream);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                buffer.append(inputLine);
            }
            result = buffer.toString();
        } catch (Exception e) {
            Vibes.getCurrentLogger().log(e);
            result = null;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Vibes.getCurrentLogger().log(e);
                }
            }
        }

        return result;
    }
}