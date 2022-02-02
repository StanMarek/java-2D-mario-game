package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramId;
    private boolean beingUsed = false;
    private String vertexSource;
    private String fragmentSource;
    private String filePath;


    public Shader(String filePath) {
        this.filePath = filePath;

        try {
            String source = new String(Files.readAllBytes(Paths.get(filePath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }
        } catch(IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: '" + filePath + "'";
        }
    }

    public void compile() {
        int vertexId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexId, vertexSource);
        glCompileShader(vertexId);
        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);
        if(success == GL_FALSE) {
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: vertex shader compilation failed");
            System.out.println(glGetShaderInfoLog(vertexId, len));
            assert false : "";
        }

        int fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentId, fragmentSource);
        glCompileShader(fragmentId);
        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if(success == GL_FALSE) {
            int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: fragment shader compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentId, len));
            assert false : "";
        }

        shaderProgramId = glCreateProgram();
        glAttachShader(shaderProgramId, vertexId);
        glAttachShader(shaderProgramId, fragmentId);
        glLinkProgram(shaderProgramId);
        success = glGetProgrami(shaderProgramId, GL_LINK_STATUS);
        if(success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: linking shaders failed");
            System.out.println(glGetProgramInfoLog(shaderProgramId, len));
            assert false : "";
        }
    }

    public void use() {
        if(!beingUsed) {
            glUseProgram(shaderProgramId);
            beingUsed = true;
        }
    }

    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String name, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramId, name);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String name, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgramId, name);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String name, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramId, name);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f(String name, Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderProgramId, name);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVec2f(String name, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramId, name);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    public void uploadFloat(String name, float var) {
        int varLocation = glGetUniformLocation(shaderProgramId, name);
        use();
        glUniform1f(varLocation, var);
    }

    public void uploadInt(String name, int var) {
        int varLocation = glGetUniformLocation(shaderProgramId, name);
        use();
        glUniform1i(varLocation, var);
    }

    public void uploadTexture(String name, int slot) {
        int varLocation = glGetUniformLocation(shaderProgramId, name);
        use();
        glUniform1i(varLocation, slot);
    }
}
