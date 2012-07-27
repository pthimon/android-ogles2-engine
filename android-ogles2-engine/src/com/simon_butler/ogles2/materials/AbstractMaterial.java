package com.simon_butler.ogles2.materials;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class AbstractMaterial {
	protected int mProgram;
    private int maPositionHandle;
    
    private int muMVPMatrixHandle;
	
	public AbstractMaterial(String vertexShader, String fragmentShader) {
		initShaders(vertexShader, fragmentShader);
	}
	
	public void release() {
		
	}
	
	public void useProgram() {
		GLES20.glUseProgram(mProgram);
	}
	
	public void setData(FloatBuffer vertices, float[] modelMatrix, float[] viewMatrix, float[] mvpMatrix) {
        // Prepare the triangle data
		vertices.position(0);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 8*4, vertices);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mvpMatrix, 0);
	}
	
	protected void initShaders(String vertexShaderCode, String fragmentShaderCode) {
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        
        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram); // creates OpenGL program executables
        
        //load shader params
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        
		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	}
	
	private int loadShader(int type, String shaderCode){
	    
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type); 
        
        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        
        return shader;
    }
}
