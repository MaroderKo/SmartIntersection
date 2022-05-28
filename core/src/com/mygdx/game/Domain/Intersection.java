package com.mygdx.game.Domain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.mygdx.game.Starter;
import com.mygdx.game.Structure.MainMenu;
import com.mygdx.game.Structure.Stats;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Intersection extends Button {
    int halfSize = 32;
    Vector2 center = new Vector2();
    Texture texture= new Texture("intersection.png");
    Rectangle area = new Rectangle();
    String name;

    public Intersection(float x, float y) {
        this.setPosition(x-halfSize,y-halfSize);
        this.setSize(64,64);
        center.set(x,y);
        area.set(x-halfSize,y-halfSize,64,64);
    }

    {
        do {
            name = "Intersection" + Stats.Counter++;
        }
        while (MainMenu.intersections.stream().anyMatch(i -> i.getName().equals(name)));
    }


    public boolean inTexture(float x,float y)
    {
        return area.contains(x,y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture,center.x-halfSize,center.y-halfSize);
    }

    public void select()
    {
        texture.dispose();
        texture = new Texture("intersection_selected.png");
    }

    public void deselect()
    {
        texture.dispose();
        texture = new Texture("intersection.png");
    }


    public void recalculate() {
        float x = getX();
        float y = getY();
        this.setSize(64,64);
        center.set(x,y);
        area.set(x-halfSize,y-halfSize,64,64);
    }
}
