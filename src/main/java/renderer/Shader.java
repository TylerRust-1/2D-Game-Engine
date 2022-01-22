package renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private int shaderProgramID;

    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes((Paths.get(filepath))));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Find the first pattern after #type 'pattern'
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            // Find the second pattern after #type 'pattern'
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            }
            else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            }
            else{
                throw new IOException("Unexpected Token '" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            }
            else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            }
            else{
                throw new IOException("Unexpected Token '" + secondPattern + "'");
            }
        } catch(IOException e) {
            e.printStackTrace();
            assert false : "Error: could not open file for shader: '" + filepath + "'";
        }

        System.out.println(vertexSource);
        System.out.println(fragmentSource);
    }

    public void compile(){
        int vertexID, fragmentID;

        // Compile and Link Shaders
        //First load and compile the vertex Shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        // Check if there were errors in compilation process
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tVertex shader compilation failed");
            System.out.println(glGetShaderInfoLog(vertexID,len));
            assert false : "";
        }

        //First load and compile the vertex Shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        // Check if there were errors in compilation process
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tFragment shader compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentID,len));
            assert false : "";
        }

        // Link shaders and check for errors
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // Check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tLinking of shaders failed");
            System.out.println(glGetProgramInfoLog(shaderProgramID,len));
            assert false : "";
        }
    }

    public void use() {
        glUseProgram(shaderProgramID);
    }

    public void detach() {
        glUseProgram(0);
    }
}
