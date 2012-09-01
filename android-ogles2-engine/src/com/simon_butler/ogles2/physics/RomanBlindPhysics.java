package com.simon_butler.ogles2.physics;

import com.simon_butler.ogles2.geometry.Vector3f;
import com.simon_butler.ogles2.model.Curtain.CurtainDirection;
import com.simon_butler.ogles2.primitives.PlanePrimitive;

public class RomanBlindPhysics extends PlaneParticleSystem {
	private float mLeftCorner;
	private float mRightCorner;
	private float mTop;
	
	private float mSectionOffset = 20f;
	private int mSegmentsPerSection = 20;
	private float mSectionSize;
	private int mNumSegments;
	private float mSegmentSize;
	
	private float mOpenPos = 1;
	private float mCurrentOpenPos = 1;
		
	public RomanBlindPhysics(PlanePrimitive plane) {
		super(plane, plane.getWidth() / plane.getSegmentsX(), plane.getHeight() / plane.getSegmentsY(), 20, 1f);
		
		mLeftCorner = -plane.getWidth()/2;
		mRightCorner = plane.getWidth()/2;
		mTop = plane.getHeight()/2;
		mNumSegments = plane.getSegmentsY() / mSegmentsPerSection;
		mSectionSize = plane.getHeight() / mNumSegments;
		mSegmentSize = plane.getHeight()/plane.getSegmentsY();
	}
	
	public void update() {
		mCurrentOpenPos += (mOpenPos - mCurrentOpenPos) / 10;
		timeStep();
	}
	
	protected void updateConstraints() {
		//position vertices along the top rail
		mPlane.setVertex(0, 0, new Vector3f(mLeftCorner, mTop, 0));
		mPlane.setVertex(1, 0, new Vector3f(mRightCorner, mTop, 0));
		
		int segments = mNumSegments;
		float yPos = mTop;
		int movingSeg = (int)Math.ceil(mCurrentOpenPos * (segments-1));
		float openPerSeg = 1f/(mNumSegments-1);
		float maxOpen = movingSeg * openPerSeg;
		// modify openPerSeg to account for the section offset
		openPerSeg *= (1+(mSectionOffset / mSectionSize));
		
		// fix all vertices up until the section that is moving
		for (int i=1; i < mSegmentsPerSection*(movingSeg-1); i++) {
			mPlane.setVertex(0, i, new Vector3f(mLeftCorner, mTop-i*mSegmentSize, 0));
			mPlane.setVertex(1, i, new Vector3f(mRightCorner, mTop-i*mSegmentSize, 0));
		}
		
		//position vertices that are connected to the opening mechanism
		for (int i=movingSeg; i < segments; i++) {
			if (i == movingSeg) {
				yPos = mTop-(mSectionSize*i)+(mSectionSize*((maxOpen-mCurrentOpenPos)/openPerSeg));
			} else if (i > movingSeg) {
				yPos -= mSectionOffset;
			} else {
				yPos = mTop-(mSectionSize*i);
			}
			mPlane.setVertex(0, i*mSegmentsPerSection, new Vector3f(mLeftCorner, yPos, 0));
			mPlane.setVertex(1, i*mSegmentsPerSection, new Vector3f(mRightCorner, yPos, 0));
		}
		
		// fix all vertices on the last section
		for (int i=1; i <= mSegmentsPerSection; i++) {
			mPlane.setVertex(0, (mNumSegments-1)*mSegmentsPerSection + i, new Vector3f(mLeftCorner, yPos-i*mSegmentSize, 0));
			mPlane.setVertex(1, (mNumSegments-1)*mSegmentsPerSection + i, new Vector3f(mRightCorner, yPos-i*mSegmentSize, 0));
		}
		
		//at the "folds" make sure they stick out
		
	}
	
	public void setOpenPos(float pos) {
		mOpenPos = pos;
	}
	
	public float setOpenPos() {
		return mOpenPos;
	}
}
