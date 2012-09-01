package com.simon_butler.ogles2.materials;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class BitmapMaterial extends AbstractMaterial {
	protected static final String mVertexShaderCode = 
		// This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;   \n" +
        
        "attribute vec4 	aPosition;  \n" +
        "attribute vec2		aTexture;  \n" +
        "varying vec2 		vTexture;  \n" +
        
        "void main(){               \n" +
        
        " vTexture = aTexture; \n" +        
        // the matrix must be included as a modifier of gl_Position
        " gl_Position = uMVPMatrix * aPosition; \n" +
        
        "}  \n";
    
	protected static final String mFragmentShaderCode = 
        "precision mediump float;  \n" +
        
        "uniform sampler2D   uMap; \n" +        
        "varying vec2        vTexture; \n" +
        
        "void main(){              \n" +
        "  gl_FragColor = texture2D(uMap, vTexture); \n" +
        //"  gl_FragColor = vec4 (0.63671875, 0.76953125, 0.22265625, 1.0); \n" +
        "}                         \n";

	private int muMapsHandle;
    private int maTextureHandle;
    protected int mTextureBinding;
    protected int mTextureSlot = -1;
    protected int mTextureWidth;
    protected int mTextureHeight;
    static protected boolean[] mTextureSlots = new boolean[32];
    
    public BitmapMaterial() {
    	super(mVertexShaderCode, mFragmentShaderCode);
    	
    	mTextureBinding = genBinding();
    }
    
    public BitmapMaterial(String vertexShader, String fragmentShader) {
    	super(vertexShader, fragmentShader);
    };
    
    public BitmapMaterial(Bitmap texture) {
    	super(mVertexShaderCode, mFragmentShaderCode);
    	
    	mTextureBinding = loadTextureFromBitmap(texture);
    }
    
    public BitmapMaterial(AssetManager assets, String filename) {
    	super(mVertexShaderCode, mFragmentShaderCode);
    	
    	mTextureBinding = loadTextureFromFile(assets, filename);
    }
    
    @Override
    public void release() {
    	releaseTextureSlot(mTextureSlot);
    	mTextureSlot = -1;
    }
    
    public void setData(FloatBuffer vertices, float[] modelMatrix, float[] viewMatrix, float[] mvpMatrix) {
    	super.setData(vertices, modelMatrix, viewMatrix, mvpMatrix);
        
        vertices.position(3);
        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false, 8*4, vertices);
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        
        bindTextures();
	}
    
    protected void bindTextures() {
    	if (mTextureSlot > -1) {
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + mTextureSlot);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureBinding);
	        GLES20.glUniform1i(muMapsHandle, mTextureSlot);
    	}
	}
    
    protected void initShaders(String vertexShaderCode, String fragmentShaderCode) {
    	super.initShaders(vertexShaderCode, fragmentShaderCode);
    	
    	maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTexture");
		
		muMapsHandle = GLES20.glGetUniformLocation(mProgram, "uMap");
    }
    
    protected int genBinding() {
    	int[] texIds = new int[1];
        GLES20.glGenTextures(1, texIds, 0);
        int textureBinding = texIds[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureBinding);
        
        return textureBinding;
    }
    
    protected int loadTexture(Bitmap texture, int textureSlot, int textureMode) {
    	/*android.graphics.Matrix matrix = new android.graphics.Matrix();
    	matrix.preScale(1, -1);
    	Bitmap tex = Bitmap.createBitmap(texture, 0, 0, texture.getWidth(), texture.getHeight(), matrix, false);
    	texture.recycle();*/
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureSlot);
		
		int textureBinding = genBinding();	
		
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture, 0);
        
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST );
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, textureMode);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, textureMode);
        
        //texture.recycle();
        
        return textureBinding;
    }
    
	protected int loadTexture(Bitmap texture, int textureSlot) {
    	return loadTexture(texture, textureSlot, GLES20.GL_REPEAT);
    }
    
	protected int loadTextureFromFile(AssetManager assets, String filename) 
    {
    	int texId = 0;
		try {
			InputStream iss = assets.open(filename);
			Bitmap texture = BitmapFactory.decodeStream(iss);
	        iss.close();
	        
	        mTextureWidth = texture.getWidth();
	        mTextureHeight = texture.getHeight();
	        
	        if (mTextureSlot < 0) {
	        	mTextureSlot = getNextTextureSlot();
	        }
	        texId = loadTexture(texture, mTextureSlot);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return texId;
    }
	
	protected int loadTextureFromBitmap(Bitmap texture) {
		mTextureWidth = texture.getWidth();
        mTextureHeight = texture.getHeight();
        if (mTextureSlot < 0) {
        	mTextureSlot = getNextTextureSlot();
        }
		return loadTexture(texture, mTextureSlot);	
	}
	
	public void initTexture(int textureSize) {
		mTextureSlot = getNextTextureSlot();
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 +  mTextureSlot);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureBinding);
        
		byte[] frame;
		frame = new byte[textureSize*textureSize*3];
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, textureSize,
				textureSize, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE ,
				ByteBuffer.wrap(frame));
		
		GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
	}
    
    public void updateTexture(Bitmap texture) {
    	mTextureWidth = texture.getWidth();
        mTextureHeight = texture.getHeight();
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 +  mTextureSlot);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureBinding);
		GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, texture);
	}
    
    public void updateTexture(ByteBuffer texture) {
    	updateTexture(texture, mTextureWidth, mTextureHeight);
    }
    
    public void updateTexture(ByteBuffer texture, int width, int height) {
    	mTextureWidth = width;
    	mTextureHeight = height;
    	GLES20.glActiveTexture(GLES20.GL_TEXTURE0 +  mTextureSlot);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureBinding);
    	GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, width, height, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, texture);
    }
    
    public void setTextureSize(int width, int height) {
    	mTextureWidth = width;
    	mTextureHeight = height;
    }
    
    public int getTextureWidth() {
    	return mTextureWidth;
    }
    public int getTextureHeight() {
    	return mTextureHeight;
    }
    
    protected static int getNextTextureSlot() {
    	for (int i=0; i < 32; i++) {
    		if (mTextureSlots[i] != true) {
    			mTextureSlots[i] = true;
    			return i;
    		}
    	}
    	// error!
    	return 0;
    	//return mTextureTotal++;
    }
	
    protected static void releaseTextureSlot(int i) {
    	if (i >= 0) {
    		mTextureSlots[i] = false;
    	} else {
    		System.out.println("WARN: Attempted to release unassigned texture");
    	}
    }
}
