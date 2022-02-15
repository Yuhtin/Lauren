package com.yuhtin.lauren.core.player.serializer;

import com.google.gson.*;
import lombok.val;

import java.lang.reflect.Type;

public class AbstractAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    @Override
    public final JsonElement serialize(final T object, final Type interfaceType, final JsonSerializationContext context) {
        val member = new JsonObject();

        member.addProperty("type", object.getClass().getName());
        member.add("data", context.serialize(object));

        return member;
    }

    @Override
    public final T deserialize(final JsonElement elem, final Type interfaceType, final JsonDeserializationContext context)
            throws JsonParseException {
        val member = (JsonObject) elem;
        val typeString = get(member, "type");
        val data = get(member, "data");
        val actualType = typeForName(typeString);

        return context.deserialize(data, actualType);
    }

    private Type typeForName(final JsonElement typeElem) {
        try {
            return Class.forName(typeElem.getAsString());
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    private JsonElement get(final JsonObject wrapper, final String memberName) {
        val elem = wrapper.get(memberName);

        if (elem == null) {
            throw new JsonParseException(
                    "no '" + memberName + "' member found in json file.");
        }
        return elem;
    }

}