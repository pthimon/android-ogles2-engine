package com.simon_butler.ogles2.physics;

public class Constraint {
	public float mRestLength;
	public float mRestLength2;
	public int mI1;
	public int mJ1;
	public int mI2;
	public int mJ2;
		
	public Constraint(int i1, int j1, int i2, int j2, float len) {
		mRestLength = len;
		mRestLength2 = len*len;
		
		mI1 = i1;
		mJ1 = j1;
		
		mI2 = i2;
		mJ2 = j2;
	}
}
