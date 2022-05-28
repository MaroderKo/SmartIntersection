package com.mygdx.game.Deserelializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.mygdx.game.Domain.Intersection;

import java.io.IOException;

public class IntersectionDeserializer extends StdDeserializer<Intersection> {
    public IntersectionDeserializer() {
        this(null);
    }

    public IntersectionDeserializer(Class<Intersection> vc) {
        super(vc);
    }

    @Override
    public Intersection deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        Intersection intersection = new Intersection(0, 0);
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        intersection.setName(node.get("name").asText());
        float x = (float) node.get("x").asDouble();
        float y = (float) node.get("y").asDouble();
        intersection.setPosition(x, y);
        return intersection;
    }
}
