package com.vibes.vibes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;

public class InboxMessagesParser implements JSONResourceParser<Collection<InboxMessage>> {

    @Override
    public Collection<InboxMessage> parse(String text) throws JSONException {
        JSONArray json = new JSONArray(text);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Collection<InboxMessage> list = new ArrayList<>();
        for (int i = 0; i < json.length(); i++) {
            list.add(gson.fromJson(json.getJSONObject(i).toString(), InboxMessage.class));
        }
        return list;
    }
}
