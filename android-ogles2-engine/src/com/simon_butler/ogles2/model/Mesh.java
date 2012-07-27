package com.simon_butler.ogles2.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.simon_butler.ogles2.geometry.Vector3f;
import com.simon_butler.ogles2.materials.AbstractMaterial;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Mesh {
	protected FloatBuffer mVertices;
	protected ShortBuffer mIndices;
	
	protected float[] mMVPMatrix = new float[16];
	protected float[] mMMatrix = new float[16];
	
	protected float x = 0.0f, y = 0.0f, z = 0.0f;
	
	protected AbstractMaterial mMaterial;
	
	public Mesh() {
		setPos(0,0,0);
	}
	
	public void init() {}
	
	public void setMaterial(AbstractMaterial mat) {
		mMaterial = mat;
	}
	
	public AbstractMaterial getMaterial() {
		return mMaterial;
	}
	
	public void render(float[] projMatrix, float[] vMatrix) {
		//setup material
		mMaterial.useProgram();
		
		Matrix.multiplyMM(mMVPMatrix, 0, vMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projMatrix, 0, mMVPMatrix, 0);
		
        mMaterial.setData(mVertices, mMMatrix, vMatrix, mMVPMatrix);
        
        mIndices.position(0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndices.limit(), GLES20.GL_UNSIGNED_SHORT, mIndices);
	}
	
	public void release() {
		mMaterial.release();
	}
	
	public void setPos(Vector3f p) {
		setPos(p.x(), p.y(), p.z());
	}
	
	public void setPos(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		Matrix.setIdentityM(mMMatrix, 0);
		Matrix.translateM(mMMatrix, 0, x, y, z);
	}
	
	protected void allocateVerticies(int num) {
		//System.out.println("allocating "+num+" vertices");
		mVertices = ByteBuffer.allocateDirect(num * 8 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}
	
	protected void addVertex(float x, float y, float z, float u, float v) {
		float triangleCoords[] = {x, y, z, u, v, 0.0f, 1.0f, 0.0f};
		mVertices.put(triangleCoords);
	}
	
	protected void addVertex(float x, float y, float z, float u, float v, float nx, float ny, float nz) {
		float triangleCoords[] = {x, y, z, u, v, nx, ny, nz};
		mVertices.put(triangleCoords);
	}
	
	public float[] vertex(int i) {
		mVertices.position(i*8);
		float v[] = new float[3];
		mVertices.get(v);
		return v;
	}
	
	public void setVertex(int i, float[] v) {
		mVertices.position(i*8);
		mVertices.put(v);
	}
	
	protected void allocateFaces(int num) {
		//System.out.println("allocating "+num+" faces");
		mIndices = ByteBuffer.allocateDirect(num * 3 * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
	}
	
	protected void addFace(short a, short b, short c) {
		short triangleCoords[] = { a, b, c };
		mIndices.put(triangleCoords);
	}
}
