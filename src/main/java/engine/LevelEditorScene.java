package engine;

import components.FontRenderer;
import components.SpriteRenderer;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import renderer.Texture;
import util.Time;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene{

    private final float[] vertexArray = {
         //position                color                        UV coord
         100.5f, 0.0f,   0.0f,     1.0f, 0.0f, 0.0f, 1.0f,      1, 0,
         0.0f,   100.0f, 0.0f,     0.0f, 1.0f, 0.0f, 1.0f,      0, 1,
         100.5f, 100.0f, 0.0f,     0.0f, 0.0f, 1.0f, 1.0f,      1, 1,
         0.0f,   0.0f,   0.0f,     1.0f, 1.0f, 0.0f, 1.0f,      0, 0
    };

    private final int[] elementArray = {
            2, 1, 0,
            0, 1, 3
    };

    private int vboId;
    private int vaoId;
    private int eboId;

    private Shader defaultShader;
    private Texture textTexture;

    private GameObject testGameObject;
    private boolean firstTime = false;

    public LevelEditorScene() {
//        System.out.println("Inside level editor scene");
    }

    @Override
    public void init() {
        System.out.println("creating test obj");
        this.testGameObject = new GameObject("testObj");
        this.testGameObject.addComponent(new SpriteRenderer());
        this.testGameObject.addComponent(new FontRenderer());
        this.addGameObjectToScene(this.testGameObject);
        this.camera = new Camera(new Vector2f());

        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();

        this.textTexture = new Texture("assets/images/mario.png");

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        int positionsSize = 3;
        int colorsSize = 4;
        int uvSize = 2;
        //int floatSizeBytes = 4;
        int vertexSizeInBytes = (positionsSize + colorsSize + uvSize) * Float.BYTES;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeInBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorsSize, GL_FLOAT, false, vertexSizeInBytes, positionsSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeInBytes, (positionsSize + colorsSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {
        camera.position.x -= dt * 50.0f;
        camera.position.y -= dt * 50.0f;
        defaultShader.use();

        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        textTexture.bind();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        defaultShader.detach();

        if(!firstTime) {
            System.out.println("creating game object");
            GameObject go = new GameObject("Game test 2");
            go.addComponent(new SpriteRenderer());
            this.addGameObjectToScene(go);
            firstTime = true;
        }

        for(GameObject go : this.gameObjects) {
            go.update(dt);
        }
    }

}
