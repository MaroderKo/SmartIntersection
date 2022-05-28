package com.mygdx.game.Serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.mygdx.game.Domain.Intersection;

import java.io.IOException;

public class IntersectionSerializer extends StdSerializer<Intersection> {
    public IntersectionSerializer()
    {
        this(null);
    }

    public IntersectionSerializer(Class<Intersection> t) {
        super(t);
    }

    @Override
    public void serialize(Intersection value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("x", String.valueOf(value.getX()));
        jsonGenerator.writeStringField("y", String.valueOf(value.getY()));
        jsonGenerator.writeStringField("name", String.valueOf(value.getName()));
        jsonGenerator.writeEndObject();
    }
}
