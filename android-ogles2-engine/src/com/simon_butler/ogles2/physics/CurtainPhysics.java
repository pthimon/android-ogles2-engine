package com.simon_butler.ogles2.physics;

import com.simon_butler.ogles2.geometry.Vector3f;
import com.simon_butler.ogles2.model.Curtain.CurtainDirection;
import com.simon_butler.ogles2.primitives.PlanePrimitive;

public class CurtainPhysics extends PlaneParticleSystem {
	private float mLeftCorner;
	private float mRightCorner;
	private float mCurrentLeftCorner;
	private float mCurrentRightCorner;
	private float mTop;
	private static float mGather = 1.5f;
	private CurtainDirection mPos;
		
	public CurtainPhysics(PlanePrimitive plane, CurtainDirection pos) {
		super(plane, (plane.getWidth() * mGather) / plane.getSegmentsX(), plane.getHeight() / plane.getSegmentsY());
		
		mLeftCorner = mCurrentLeftCorner = -plane.getWidth()/2;
		mRightCorner = mCurrentRightCorner = plane.getWidth()/2;
		mTop = plane.getHeight()/2;
		mPos = pos;
	}
	
	public void update() {
		if (mPos == CurtainDirection.RIGHT) {
			mCurrentLeftCorner += (mLeftCorner - mCurrentLeftCorner) / 8; 
		} else {
			mCurrentRightCorner += (mRightCorner - mCurrentRightCorner) / 8;
		}
		timeStep();
	}
	
	protected void updateConstraints() {
		int i;
		//position vertices along the curtain rail
		float x = mCurrentLeftCorner;
		float dx = (mCurrentRightCorner - mCurrentLeftCorner) / mPlane.getSegmentsX();
		float dx2 = dx * 2;
		//var dxSq:Number = dx*dx;
		for(i=0; i<mNumParticlesW; i+=2) {
			//even vertices
			mPlane.setVertex(i, 0, new Vector3f(x, mTop, 0));
			x += dx2;
		}
		x = mCurrentLeftCorner + dx;
		//var c:Number = ((mCurrentRightCorner - mCurrentLeftCorner) * mGather) / mPlane.segmentsW;
		//var cSq:Number = c*c;
		for(i=1; i<mNumParticlesW; i+=2) {
			//odd vertices
			//eyelet:
			//var y:Number = Math.sqrt(cSq - dxSq);
			//pencil pleat:
			float z = 0;
			mPlane.setVertex(i, 0, new Vector3f(x, mTop, z));
			x += dx2;
		}
	}
	
	public void setCurtainPos(float pos) {
		if (mPos == CurtainDirection.RIGHT) {
			mLeftCorner = mRightCorner - (pos * mPlane.getWidth());
		} else {
			mRightCorner = mLeftCorner + (pos * mPlane.getWidth());
		}	
	}
	
	public float getCurtainPos() {
		if (mPos == CurtainDirection.RIGHT) {
			return mLeftCorner;
		} else {
			return mRightCorner;
		}	
	}
}
