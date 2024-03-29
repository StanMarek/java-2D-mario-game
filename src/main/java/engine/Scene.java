package engine;

import renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected Renderer renderer = new Renderer();

    public Scene() {}

    public abstract void update(float dt);

    public void init() {}

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
        }
    }

    public void start() {
        for (GameObject go : gameObjects)  {
            go.start();
            this.renderer.add(go);
        }
        isRunning = true;

    }

    public Camera camera() {
        return this.camera;
    }
}
