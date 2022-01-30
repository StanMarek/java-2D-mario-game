package engine;

import org.lwjgl.BufferUtils;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene{

//    private boolean changingScene = false;
//    private float timeToChangeScene = 2.0f;

    private final String vertexShaderSource = "#version 330 core\n" +
            "\n" +
            "layout (location = 0) in vec3 aPos;\n" +
            "layout (location = 1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private final String fragmentShaderSource = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexId;
    private int fragmentId;
    private int shaderProgram;

    private float[] vertexArray = {
            //position             color
             0.5f, -0.5f, 0.0f,    1.0f, 0.0f, 0.0f, 1.0f,
            -0.5f,  0.5f, 0.0f,    0.0f, 1.0f, 0.0f, 1.0f,
             0.5f,  0.5f, 0.0f,    0.0f, 0.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, 0.0f,    1.0f, 1.0f, 0.0f, 1.0f,
    };

    private int[] elementArray = {
            2, 1, 0,
            0, 1, 3
    };

    private int vboId;
    private int vaoId;
    private int eboId;

    public LevelEditorScene() {
//        System.out.println("Inside level editor scene");
    }

    @Override
    public void update(float dt) {
//        System.out.println("" + (1.0f/dt) + " FPS");
//
//        if(!changingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE)) {
//            changingScene = true;
//        }
//
//        if (changingScene && timeToChangeScene > 0) {
//            timeToChangeScene -= dt;
//            Window.get().r -= dt * 0.5;
//            Window.get().g -= dt * 0.5;
//            Window.get().b -= dt * 0.5;
//        } else if (changingScene) {
//            Window.changeScene(1);
//        }
        glUseProgram(shaderProgram);
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        glUseProgram(0);
    }

    @Override
    public void init() {
        vertexId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexId, vertexShaderSource);
        glCompileShader(vertexId);
        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);
        if(success == GL_FALSE) {
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: vertex shader compilation failed");
            System.out.println(glGetShaderInfoLog(vertexId, len));
            assert false : "";
        }

        fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentId, fragmentShaderSource);
        glCompileShader(fragmentId);
        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if(success == GL_FALSE) {
            int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: fragment shader compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentId, len));
            assert false : "";
        }

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexId);
        glAttachShader(shaderProgram, fragmentId);
        glLinkProgram(shaderProgram);
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if(success == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: linking shaders failed");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false : "";
        }

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
        int floatSizeBytes = 4;
        int vertexSizeInBytes = (positionsSize + colorsSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeInBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorsSize, GL_FLOAT, false, vertexSizeInBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

}
