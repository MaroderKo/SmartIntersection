package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mygdx.game.Deserelializer.IntersectionDeserializer;
import com.mygdx.game.Deserelializer.RoadDeserializer;
import com.mygdx.game.Domain.Intersection;
import com.mygdx.game.Domain.Road;
import com.mygdx.game.Domain.TrafficLight;
import com.mygdx.game.Serializers.IntersectionSerializer;
import com.mygdx.game.Serializers.RoadSerializer;
import com.mygdx.game.Structure.IntersectionMenu;
import com.mygdx.game.Structure.MainMenu;
import com.mygdx.game.Structure.RoadMenu;
import com.mygdx.game.Structure.Stats;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SuppressWarnings("NonJREEmulationClassesInClientCode")
@EqualsAndHashCode(callSuper = true)
@Data
    public class Starter extends ApplicationAdapter {
    TrafficLight trafficLight;
    public static Starter starter;
    MainMenu mainMenu;
    private IntersectionMenu intersectionMenu;
    private RoadMenu roadMenu;
    protected Stats stats;
    public static boolean debug = false;



    @Override
    public void create() {

        starter = this;
        Gdx.gl.glLineWidth(5);
        stats = new Stats(this);
        mainMenu = new MainMenu(stats);
        mainMenu.create();
        Gdx.input.setInputProcessor(mainMenu.getStage());
        intersectionMenu = new IntersectionMenu(stats);
        roadMenu = new RoadMenu(stats);
        stats.setViewport(new FitViewport(0, 0, stats.getCamera()));
        Gdx.input.setInputProcessor(mainMenu.getStage());
        resize(800, 800);
        load();
        trafficLight = new TrafficLight(this);
        trafficLight.run();
    }

    private void load() {
        File intersections = new File("save/intersections.json");
        File roads = new File("save/roads.json");
        if (!intersections.exists() || !roads.exists())
            return;
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module =
                new SimpleModule("IntersectionDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Intersection.class, new IntersectionDeserializer());
        module.addDeserializer(Road.class, new RoadDeserializer());
        mapper.registerModule(module);
        try {
            List<Intersection> intersectionList = mapper.readValue(intersections, new TypeReference<List<Intersection>>() {
            });
            intersectionList.forEach( i -> {
                i.recalculate();
                mainMenu.getStage().addActor(i);
            });
            MainMenu.intersections = intersectionList;
            List<Road> roadList = mapper.readValue(roads, new TypeReference<List<Road>>() {
            });
            mainMenu.setRoads(roadList);


        } catch (IOException e) {
            System.out.println("Save load error!");
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void render() {
        float value = 0.8f;
        Gdx.gl20.glClearColor(value, value, value, 1f);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        switch (stats.getScene()) {
            case MAIN:
                if (Gdx.input.getInputProcessor() != mainMenu.getStage()) {
                    Gdx.input.setInputProcessor(mainMenu.getStage());
                }
                mainMenu.redraw();
                mainMenu.render();
                break;
            case INTERSECTION_INFO:
                if (Gdx.input.getInputProcessor() != intersectionMenu.getStage()) {
                    Gdx.input.setInputProcessor(intersectionMenu.getStage());
                    intersectionMenu.reset();
                    if (stats.getSelected() == null)
                        throw new IllegalArgumentException();
                    intersectionMenu.getNameField().setText(stats.getSelected().getName());
                }
                intersectionMenu.render();
                break;
            case ROAD_MENU:
                if (Gdx.input.getInputProcessor() != roadMenu.getStage()) {
                    Gdx.input.setInputProcessor(roadMenu.getStage());
                    if (stats.getSelected() == null)
                        throw new IllegalArgumentException();
                    roadMenu.reset();
                    // TODO: 21.05.2022
                }
                roadMenu.render();
                break;
        }

    }

    @Override
    public void dispose() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module =
                new SimpleModule("CustomCarSerializer", new Version(1, 0, 0, null, null, null));
        module.addSerializer(Intersection.class, new IntersectionSerializer());
        module.addSerializer(Road.class, new RoadSerializer());
        objectMapper.registerModule(module);
        String intersections = null;
        String roads = null;
        try {
            intersections = objectMapper.writeValueAsString(MainMenu.intersections);
            roads = objectMapper.writeValueAsString(mainMenu.getRoads());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        try {
            FileUtils.writeStringToFile(new File("save/intersections.json"), intersections, StandardCharsets.UTF_8);
            FileUtils.writeStringToFile(new File("save/roads.json"), roads, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        trafficLight.getScheduler().shutdown();
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("Resize");
        stats.getCamera().setToOrtho(false, width, height);
        stats.getViewport().setWorldSize(width, height);
    }


}
