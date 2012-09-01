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
		super(plane, (plane.getWidth() * mGather) / plane.getSegmentsX(), plane.getHeight() / plane.getSegmentsY(), 2, 1);
		
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
		
		for(i=0; i<mNumParticlesW; i++) {
			mPlane.setVertex(i, 0, new Vector3f(x, mTop, 0));
			x += dx;
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
