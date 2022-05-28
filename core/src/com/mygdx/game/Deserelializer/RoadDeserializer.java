package com.mygdx.game.Deserelializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.mygdx.game.Domain.Intersection;
import com.mygdx.game.Domain.Road;
import com.mygdx.game.Structure.MainMenu;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class RoadDeserializer extends StdDeserializer<Road> {
    public RoadDeserializer() {
        this(null);
    }

    public RoadDeserializer(Class<Intersection> vc) {
        super(vc);
    }

    @Override
    public Road deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        Road road = new Road(null, null);
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        road.setName(node.get("name").asText());
        AtomicInteger workload = new AtomicInteger(node.get("workload").asInt());
        int size = node.get("size").asInt();
        road.setSizePoints(size);
        road.setWorkLoad(workload);
        AtomicInteger rworkload = new AtomicInteger(node.get("reverse_workload").asInt());
        road.setReverseWorkload(rworkload);
        String firstName = node.get("first").asText();
        String secondName = node.get("second").asText();
        Intersection first = MainMenu.intersections.stream().filter(i -> i.getName().equals(firstName)).findAny().orElse(null);
        Intersection second = MainMenu.intersections.stream().filter(i -> i.getName().equals(secondName)).findAny().orElse(null);
        if (first == null || second == null)
            throw new IllegalArgumentException("Save corrupted!");
        road.setFirst(first);
        road.setSecond(second);
        return road;
    }
}
