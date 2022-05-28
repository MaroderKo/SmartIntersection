package com.mygdx.game.Serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.mygdx.game.Domain.Road;

import java.io.IOException;

public class RoadSerializer extends StdSerializer<Road> {
    public RoadSerializer()
    {
        this(null);
    }

    public RoadSerializer(Class<Road> t) {
        super(t);
    }

    @Override
    public void serialize(Road value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("first", String.valueOf(value.getFirst().getName()));
        jsonGenerator.writeStringField("second", String.valueOf(value.getSecond().getName()));
        jsonGenerator.writeStringField("name", String.valueOf(value.getName()));
        jsonGenerator.writeStringField("size", String.valueOf(value.getSizePoints()));
        jsonGenerator.writeStringField("workload", String.valueOf(value.getWorkLoad().get()));
        jsonGenerator.writeStringField("reverse_workload", String.valueOf(value.getReverseWorkload().get()));
        jsonGenerator.writeEndObject();
    }
}
