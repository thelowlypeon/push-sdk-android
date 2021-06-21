package com.vibes.vibes.versioning;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vibes.vibes.HTTPResource;
import com.vibes.vibes.JSONResourceParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

import static com.vibes.vibes.HTTPMethod.GET;

/**
 * Obtains the latest release of this SDK.
 */
public class GitVersionTracker {
    public static final String GET_TAGS_URL = "https://api.github.com/repos/vibes/push-sdk-android/tags";

    /**
     * An HTTP resource for obtaining the most current release of this library .
     */
    public static HTTPResource<GitTag> getCurrentVersion() {
        return new HTTPResource<>(GET_TAGS_URL, GET, null, jsonHeaders(), new GitTagsParser());
    }

    /**
     * A utility method for returning the request headers needed for a JSON request.
     */
    private static HashMap<String, String> jsonHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/vnd.github.v3+json");
        return headers;
    }

    public static class GitTagsParser implements JSONResourceParser<GitTag> {

        @Override
        public GitTag parse(String text) throws JSONException {
            JSONArray json = new JSONArray(text);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            GitTag.GitTags tags = new GitTag.GitTags();
            for (int i = 0; i < json.length(); i++) {
                tags.getList().add(gson.fromJson(json.getJSONObject(i).toString(), GitTag.class));
            }
            return tags.getCurrent();
        }
    }
}
