package com.simon_butler.ogles2.primitives;

import com.simon_butler.ogles2.geometry.Vector3f;
import com.simon_butler.ogles2.model.Mesh;

public class PlanePrimitive extends Mesh {
	
	protected int mSegmentsX;
	protected int mSegmentsY;
	
	protected float mWidth;
	protected float mHeight;
	
	protected float mU = 1f;
	protected float mV = 1f;
	protected boolean mFlipU = false;
	
	public PlanePrimitive() {
		super();
	}
	
	// Must call build primitive in subclass
	public PlanePrimitive(float width, float height, int nx, int ny) {
		super();
		
		setDimensions(width, height, nx, ny);
	}
	
	public void setDimensions(float width, float height, int nx, int ny) {
		mSegmentsX = nx;
		mSegmentsY = ny;
		
		mWidth = width;
		mHeight = height;
	}
	
	public Vector3f vertex(int x, int y) {
		return new Vector3f(vertex(getVertexIndex(x,y)));
	}
	
	public void setVertex(int x, int y, Vector3f v) {
		setVertex(getVertexIndex(x,y), v.array());
	}
	
	public int getSegmentsX() { return mSegmentsX; }
	public int getSegmentsY() { return mSegmentsY; }
	public float getWidth() { return mWidth; }
	public float getHeight() { return mHeight; }
	
	public void buildPrimitive() {
		if (mSegmentsX > 0 && mSegmentsY > 0) {
			allocateBuffers();
			createVertices();
			createFaces();
		}
	}
	
	protected void allocateBuffers() {
		// initialize vertex Buffer for plane vertices 
		allocateVerticies((mSegmentsX+1) * (mSegmentsY+1));
		// initialize vertex element Buffer for plane 
    	allocateFaces(mSegmentsX * mSegmentsY * 2); 
    	
    	//System.out.println("buffer's allocated");
	}
	
	public void setTextureCoords(float u, float v, boolean flipU) {
		mU = u;
		mV = v;
		mFlipU = flipU;
	}
	
	protected void createVertices() {
		float x = -mWidth / 2;
		float y = mHeight / 2;
		
        float dx = mWidth / mSegmentsX;
        float dy = mHeight / mSegmentsY;
    	for (int i=0; i <= mSegmentsY; i++) {
    		float v = (i/(float)mSegmentsY) * mV;
    		float py = y - dy*i;
    		for (int j=0; j <= mSegmentsX; j++) {
    			float u = ((mFlipU) ? 1 - j/(float)mSegmentsX : j/(float)mSegmentsX) * mU;
    			float px = x + dx*j;
    			//float nx = (j % 2)*2-1;
    			addVertex(px, py, 0, u, v, 0.0f, 1.0f, 0.0f);
    		}
    	}
	}
	
	protected void createFaces() {
    	//generate faces
        int sx = mSegmentsX+1;
    	for (int i=0; i < mSegmentsY; i++) {
    		for (int j=0; j < mSegmentsX; j++) {
    			addFace((short) (i*sx + j), (short) ((i+1)*sx + j), (short) ((i+1)*sx + j+1));
		        addFace((short) (i*sx + j), (short) ((i+1)*sx + j+1), (short) (i*sx + j+1));
    		}
    	}
    }
	
	protected int getVertexIndex(int x, int y) {
		return y*(mSegmentsX+1) + x;
	}
}
