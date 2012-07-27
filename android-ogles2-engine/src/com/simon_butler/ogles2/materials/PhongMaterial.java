package com.simon_butler.ogles2.materials;

import java.nio.FloatBuffer;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class PhongMaterial extends BitmapMaterial {
	protected static final String mVertexShaderCode = 
		// This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;   \n" +
        //"uniform mat3 uNMatrix;\n" +
		"uniform mat4 uMMatrix;\n" +
		"uniform vec3 uLightPos;\n" +
        
        "attribute vec4 aPosition;  \n" +
        //"attribute vec3 aNormal;\n" +
        "attribute vec2	aTexture;  \n" +
        
        "varying vec2 		vTexture;  \n" +
        "varying vec3 L, E, H;\n" +
        
        "void main(){               \n" +
        
        " 	vTexture = aTexture; \n" +        
        " 	gl_Position = uMVPMatrix * aPosition; \n" +
        
        "	vec4 eyePosition = uMMatrix  * aPosition;\n" + 
		"	vec4 eyeLightPos = vec4(uLightPos, 1.0);\n" +
		//"	N = normalize(uNMatrix * aNormal);\n" +
		//"	L = normalize(eyeLightPos.xyz - eyePosition.xyz);\n" +
		"   L = normalize(vec3(1.0,0.5,0.0));\n" +
		"	E = -normalize(eyePosition.xyz);\n" +
		"	H = normalize(L + E);\n" +
        
        "}  \n";
    
	protected static final String mFragmentShaderCode = 
        "precision mediump float;  \n" +
        
        "uniform sampler2D  uMap; \n" +
        "uniform sampler2D  uNormalMap; \n" + 
        "uniform vec4 		uSpecularColor;\n" +
		"uniform vec4 		uAmbientColor;\n" +
		"uniform float 		uShininess;\n" +
		
        "varying vec2 vTexture; \n" +
		"varying vec3 L, E, H;\n" +
        
        "void main(){              \n" +
        //"	vec3 Normal = normalize(N);\n" +
        "	vec3 Normal = normalize(texture2D(uNormalMap, vTexture).xyz);\n" +
		"	vec3 Light  = normalize(L);\n" +
		"	vec3 Half   = normalize(H);\n" +

		"	float Kd = max(dot(Normal, Light), 0.0);\n" + 
		//"	float Ks = pow(max(dot(Half, Normal), 0.0), uShininess);\n" + 
		"	float Ks = 0.0;\n" +
	    "	float Ka = 0.0;\n" +
	    "	vec4 diffuse  = Kd * texture2D(uMap, vTexture);\n" + 
	    "	vec4 specular = Ks * uSpecularColor;\n" + 
	    "	vec4 ambient  = Ka * uAmbientColor;\n" + 
	    "	gl_FragColor = ambient + diffuse + specular;\n" + 
        //"  gl_FragColor = texture2D(uNormalMap, vTexture); \n" +
	    //"  gl_FragColor = texture2D(uMap, vTexture); \n" +
        //"  gl_FragColor = vec4 (0.63671875, 0.76953125, 0.22265625, 1.0); \n" +
        "}                         \n";
	
	//protected int maNormalHandle;
	protected int muLightPosHandle;
	//protected int muNormalMatrixHandle;
	//protected int muUseObjectTransformHandle;
	protected int muSpecularColorHandle;
	protected int muAmbientColorHandle;
	protected int muShininessHandle;
	protected int muNormalMapHandle;
	protected int muMMatrixHandle;

	protected float[] mMMatrix;
	protected float[] mLightPos;
	protected float[] mSpecularColor;
	protected float[] mAmbientColor;
	protected float mShininess;
	
	protected int mNormalMapSlot = -1;
	protected int mNormalMapBinding;
    
	public PhongMaterial(Bitmap texture) {
    	super(mVertexShaderCode, mFragmentShaderCode);
    	
    	mTextureBinding = loadTextureFromBitmap(texture);
    	
    	init();
    }
	
    public PhongMaterial(AssetManager assets, String filename) {
    	super(mVertexShaderCode, mFragmentShaderCode);
    	
    	mTextureBinding = loadTextureFromFile(assets, filename);
    	
    	init();
    }
    
    public void init() {
    	mMMatrix = new float[9];
    	mLightPos = new float[] {1.0f, 0.0f, 0.0f};
		mSpecularColor = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
		mAmbientColor = new float[] { 0.2f, 0.2f, 0.2f, 1.0f };
		mShininess = 96.0f;
    }
    
    @Override
    public void release() {
    	super.release();
    	releaseTextureSlot(mNormalMapSlot);
    	mNormalMapSlot = -1;
    }
    
    public void setData(FloatBuffer vertices, float[] modelMatrix, float[] viewMatrix, float[] mvpMatrix) {
    	super.setData(vertices, modelMatrix, viewMatrix, mvpMatrix);
    	
    	//vertices.position(5);
        //GLES20.glVertexAttribPointer(maNormalHandle, 3, GLES20.GL_FLOAT, false, 8*4, vertices);
        //GLES20.glEnableVertexAttribArray(maNormalHandle);
    	
		GLES20.glUniform3fv(muLightPosHandle, 1, mLightPos, 0);
		GLES20.glUniform4fv(muSpecularColorHandle, 1, mSpecularColor, 0);
		GLES20.glUniform4fv(muAmbientColorHandle, 1, mAmbientColor, 0);
		GLES20.glUniform1f(muShininessHandle, mShininess);
		
		// invert modelMatrix to get normalMatrix
		//android.graphics.Matrix normalMatrix = new android.graphics.Matrix();
		android.graphics.Matrix mvMatrix = new android.graphics.Matrix();

		mvMatrix.setValues(new float[]{
				modelMatrix[0], modelMatrix[1], modelMatrix[2], 
				modelMatrix[4], modelMatrix[5], modelMatrix[6],
				modelMatrix[8], modelMatrix[9], modelMatrix[10]
		});
		mvMatrix.getValues(mMMatrix);
		GLES20.glUniformMatrix3fv(muMMatrixHandle, 1, false, mMMatrix, 0);

		/*normalMatrix.reset();
		mvMatrix.invert(normalMatrix);
		float[] values = new float[9];
		normalMatrix.getValues(values);

		normalMatrix.setValues(new float[] {
				values[0], values[3], values[6],
				values[1], values[4], values[7],
				values[2], values[5], values[8]
		});
		normalMatrix.getValues(mNormalMatrix);

	    GLES20.glUniformMatrix3fv(muNormalMatrixHandle, 1, false, mNormalMatrix, 0);*/
	}
    
    protected void initShaders(String vertexShaderCode, String fragmentShaderCode) {
    	super.initShaders(vertexShaderCode, fragmentShaderCode);
    	
    	//maNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
    	
    	muLightPosHandle = GLES20.glGetUniformLocation(mProgram, "uLightPos");
		//muNormalMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uNMatrix");
		muSpecularColorHandle = GLES20.glGetUniformLocation(mProgram, "uSpecularColor");
		muAmbientColorHandle = GLES20.glGetUniformLocation(mProgram, "uAmbientColor");
		muShininessHandle = GLES20.glGetUniformLocation(mProgram, "uShininess");
		muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
		
		muNormalMapHandle = GLES20.glGetUniformLocation(mProgram, "uNormalMap");
    }
    
    public void setSpecularColor(float[] color) {
		mSpecularColor = color;
	}

	public void setAmbientcolor(float[] color) {
		mAmbientColor = color;
	}

	public void setShininess(float shininess) {
		mShininess = shininess;
	}
	
	public void setNormalMap(Bitmap bitmap) {
		if (mNormalMapSlot < 0) {
			mNormalMapSlot = getNextTextureSlot();
		}
    	mNormalMapBinding = loadTexture(bitmap, mNormalMapSlot);     
	}
	
	public void updateNormalMap(Bitmap bitmap) {
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 +  mNormalMapSlot);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mNormalMapBinding);
		GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, bitmap);
	}
	
	protected void bindTextures() {
		super.bindTextures();
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + mNormalMapSlot);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mNormalMapBinding);
        GLES20.glUniform1i(muNormalMapHandle, mNormalMapSlot);
	}
    
}
