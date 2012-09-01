package com.simon_butler.ogles2.physics;

import com.simon_butler.ogles2.geometry.Vector3f;
import com.simon_butler.ogles2.primitives.PlanePrimitive;

public class PlaneParticleSystem {
	private Vector3f[] m_oldx; // Previous positions
	private Vector3f[] m_a; // Force accumulators
	private Vector3f m_vGravity; // Gravity
	private float mDrag;
	
	private Constraint[] mConstraints;
	private int mNumConstraints;
	
	protected int mNumParticles;
	protected int mNumParticlesW;
	protected int mNumParticlesH;
	
	private int mNumIterations;
	
	protected PlanePrimitive mPlane;
	protected boolean mUpdating = true;
	private float mMinDelta = 0.0001f;
			
	public PlaneParticleSystem(PlanePrimitive plane, float segSizeW, float segSizeH, int numIterations, float gravity) {
		//constants				
		m_vGravity = new Vector3f(0, -1f * gravity, 0);
		mNumIterations = numIterations;
		mDrag = 0.97f;
		
		//variables
		mPlane = plane;
		
		mNumParticlesW = mPlane.getSegmentsX() + 1;
		mNumParticlesH = mPlane.getSegmentsY() + 1;
		mNumParticles = mNumParticlesW * mNumParticlesH;
		
		//initialise old verticies & gravity
		m_oldx = new Vector3f[mNumParticles];
		m_a = new Vector3f[mNumParticles];
		int index = 0;
		for(int i=0; i<mNumParticlesH; i++) {
			for(int j=0; j<mNumParticlesW; j++) {
				m_oldx[index] = mPlane.vertex(j,i);
				m_a[index] = m_vGravity;
				index++;
			}
		}
		
		//setup constraints
		mNumConstraints = ((mNumParticlesW - 1) * mNumParticlesH) + (mNumParticlesW * (mNumParticlesH - 1)); 
		mConstraints = new Constraint[mNumConstraints];
		index = 0;
		for(int i=0; i<mNumParticlesH; i++) {
			for(int j=0; j<mNumParticlesW; j++) {
				if (i < mNumParticlesH-1) mConstraints[index++] = new Constraint(i,j,i+1,j,segSizeH);
				if (j < mNumParticlesW-1) mConstraints[index++] = new Constraint(i,j,i,j+1,segSizeW);
			}
		}
	}
	
	protected void timeStep() {
		verlet();
		satisfyConstraints();
	}
	
	// Verlet integration step
	private void verlet() {
		int index = 0;
		mUpdating = false;
		for(int i=0; i<mNumParticlesH; i++) {
			for(int j=0; j<mNumParticlesW; j++) {
				 //m_x[i];
				Vector3f x = mPlane.vertex(j,i);
				
				Vector3f temp = new Vector3f(x.array());
				
				//x += (drag*x)-(drag*oldx)+a*fTimeStep*fTimeStep;			
				x = x.add(x.mul(mDrag).sub(m_oldx[index].mul(mDrag)).add(m_a[index]));
				
				if (!mUpdating && m_oldx[index].distance2(x) > mMinDelta) {
					mUpdating = true;
				}
				
				mPlane.setVertex(j,i,x);
				
				m_oldx[index] = temp;
				index++;
			}
		}
	}
	private void satisfyConstraints() {
		for(int j=0; j < mNumIterations; j++) {
			//update springs
			for(int i=0; i < mNumConstraints; i++) {
				Constraint c = mConstraints[i];
				Vector3f x1 = mPlane.vertex(c.mJ1,c.mI1);
				Vector3f x2 = mPlane.vertex(c.mJ2,c.mI2);
				Vector3f delta = x2.sub(x1);
				
				/*float deltalength = (float)Math.sqrt(delta.dot(delta));
				float diff = (deltalength-c.mRestLength)/deltalength;
				Vector3f result = delta.mul(-0.5*diff);*/
				
				//sqrt approximation
				float delta2 = delta.dot(delta);
				Vector3f result = delta.mul(c.mRestLength2/(delta2+c.mRestLength2)-0.5f);
				
				x1 = x1.sub(result);
				mPlane.setVertex(c.mJ1,c.mI1, x1);
				x2 = x2.add(result);
				mPlane.setVertex(c.mJ2,c.mI2, x2);
			}
			
			updateConstraints();
		}
				
	}
	
	//Override this to do what you want
	protected void updateConstraints() {
		//constrain top corners
		//mPlane.updateVertex(mPlane.vertex(mNumParticlesH-1, 0), -250, 0, 250, true);
		//mPlane.updateVertex(mPlane.vertex(mNumParticlesH-1, mNumParticlesW-1), 250, 0, 250, true);
	}
	
	// This function should accumulate forces for each particle
	/*private function accumulateForces():void {
		for(var i:int=0; i<mNumParticles; i++)  {
			m_a[i] = m_vGravity;
		}
	}*/
	
	public boolean isActive() {
		return mUpdating;
	}

}
