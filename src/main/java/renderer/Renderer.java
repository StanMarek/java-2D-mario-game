package renderer;

import components.SpriteRenderer;
import engine.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Renderer {

    private final int MAX_BATCH_SIZE = 10000;
    private List<RenderBatch> batches;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go) {
        SpriteRenderer sprite = go.getComponent(SpriteRenderer.class);
        if (sprite != null) {
            add(sprite);
        }
    }

    public void add(SpriteRenderer sr) {
        boolean added = false;
        for(RenderBatch rb : batches) {
            if(rb.hasRoom()) {
                rb.addSprite(sr);
                added = true;
                break;
            }
        }
        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sr);
        }
    }

    public void render() {
        for (RenderBatch rb : batches) {
            rb.render();
        }
    }

}
