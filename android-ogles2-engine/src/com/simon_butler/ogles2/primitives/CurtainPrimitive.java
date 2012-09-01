package com.simon_butler.ogles2.primitives;

import com.simon_butler.ogles2.geometry.Vector3f;

public class CurtainPrimitive extends PlanePrimitive {
	
	protected int mBottomSegmentMultiplier;
	protected int mNumBottomSegments;
	protected int mNumBottomVertices;
	protected int mNumBottomFaces;
	
	public CurtainPrimitive(float width, float height, int nx, int ny) {
		super(width,height,nx,ny);
		
		mBottomSegmentMultiplier = 4;
		mNumBottomSegments = mSegmentsX * mBottomSegmentMultiplier;
		mNumBottomVertices = mNumBottomSegments+1;
		mNumBottomFaces = mSegmentsX * (mBottomSegmentMultiplier+1);
	}
	
	protected void allocateBuffers() {
		// initialize vertex Buffer for plane vertices 
		allocateVerticies(getNumVertices());
		// initialize vertex element Buffer for plane 
    	allocateFaces((mSegmentsX * (mSegmentsY-1) * 2) + mNumBottomFaces); 
    	
    	//System.out.println("bottom buffers allocated");
	}
	
	//override build primitive to add extra verticies at the bottom
	protected void createVertices() {
		super.createVertices();
		
		float x = -mWidth / 2;
		float py = -mHeight / 2;

        float dx = mWidth / mNumBottomSegments;
		float v = 1;
		//move to the start of the bottom row
		mVertices.position(mSegmentsY*(mSegmentsX+1)*8);
		for (int j=0; j < mNumBottomVertices; j++) {
			float u = (j/(float)mNumBottomSegments);
			float px = x + dx*j;
			addVertex(px, py, 0, u, v, 0.0f, 1.0f, 0.0f);
		}
	}
    	
    protected void createFaces() {
    	super.createFaces();
    	
    	//generate bottom faces
        int sx = mSegmentsX+1;
        int bx = mBottomSegmentMultiplier;
    	//for (int i=0; i < mSegmentsY; i++) {
        int i = mSegmentsY-1;
        //move to the start of the bottom row
        mIndices.position((mSegmentsX * (mSegmentsY-1) * 2)*3);
        int bj = 0;
		for (int j=0; j < mSegmentsX; j++) {
			short a = (short) (i*sx + j);
			short b = (short) (i*sx + j+1);
			//short c = (short) ((i+1)*sx + bj);
			short d = (short) ((i+1)*sx + bj+mBottomSegmentMultiplier);
			for (int k=0; k<mBottomSegmentMultiplier; k++) {
				short ck = (short) ((i+1)*sx + bj+k);
				short dk = (short) (ck+1);
				addFace(a, ck, dk);
			}
	        addFace(a, d, b);
	        bj += bx;
		} 
    }
	
	protected int getVertexIndex(int x, int y) {
		//botttom verticies are different
		if (y == mSegmentsY) {
			x*= mBottomSegmentMultiplier;
		}
		return y*(mSegmentsX+1) + x;
	}
	
	protected int getVertexBottomIndex(int x) {
		return mSegmentsY*(mSegmentsX+1) + x;
	}
	
	protected Vector3f vertexBottom(int x) {
		return new Vector3f(vertex(getVertexBottomIndex(x)));
	}
	
	protected void setVertexBottom(int x, Vector3f v) {
		setVertex(getVertexBottomIndex(x), v.array());
	}
	
	protected int getNumVertices() {
		return ((mSegmentsX+1) * (mSegmentsY)) + mNumBottomVertices;
	}
}
