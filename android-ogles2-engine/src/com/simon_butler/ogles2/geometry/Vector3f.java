package com.simon_butler.ogles2.geometry;

public class Vector3f
{
	public Vector3f()
	{
		xyz[0] = 0;
		xyz[1] = 0;
		xyz[2] = 0;
	}

	public Vector3f(float x, float y, float z)
	{
		xyz[0] = x;
		xyz[1] = y;
		xyz[2] = z;
	}

	public Vector3f(float[] array)
	{
		if(array.length < 3)
			throw new RuntimeException("Must create vector with 3 element array");

		xyz[0] = array[0];
		xyz[1] = array[1];
		xyz[2] = array[2];
	}
	
	public float[] array()
	{
		return (float[])xyz.clone();
	}

	public Vector3f add(Vector3f rhs)
	{
		return new Vector3f(
			xyz[0] + rhs.xyz[0],
			xyz[1] + rhs.xyz[1],
			xyz[2] + rhs.xyz[2] );
	}

	public Vector3f sub(Vector3f rhs)
	{
		return new Vector3f(
			xyz[0] - rhs.xyz[0],
			xyz[1] - rhs.xyz[1],
			xyz[2] - rhs.xyz[2] );
	}
	
	public Vector3f neg()
	{
		return new Vector3f(-xyz[0], -xyz[1], -xyz[2]);
	}

	public Vector3f mul(float c)
	{
		return new Vector3f(c*xyz[0], c*xyz[1], c*xyz[2]);
	}

	public Vector3f div(float c)
	{
		return new Vector3f(xyz[0]/c, xyz[1]/c, xyz[2]/c);
	}

	public float dot(Vector3f rhs)
	{
		return xyz[0]*rhs.xyz[0] +
			xyz[1]*rhs.xyz[1] +
			xyz[2]*rhs.xyz[2];
	}

	public Vector3f cross(Vector3f rhs)
	{
		return new Vector3f(
			xyz[1]*rhs.xyz[2] - xyz[2]*rhs.xyz[1],
			xyz[0]*rhs.xyz[2] - xyz[2]*rhs.xyz[0],
			xyz[0]*rhs.xyz[1] - xyz[1]*rhs.xyz[0]
		);
	}

	public boolean equals(Object obj)
	{
		if( obj instanceof Vector3f )
		{
			Vector3f rhs = (Vector3f)obj;

			return xyz[0]==rhs.xyz[0] &&
			       xyz[1]==rhs.xyz[1] &&
			       xyz[2]==rhs.xyz[2];
		}
		else
		{
			return false;
		}
		
	}

	public float norm()
	{
		return (float)Math.sqrt(this.dot(this));	
	}

	public Vector3f normalize()
	{
		return this.div(norm());
	}
	
	public float distance(Vector3f rhs)
	{
		return rhs.sub(this).norm();
	}
	
	public float distance2(Vector3f rhs)
	{
		Vector3f diff = rhs.sub(this);
		return diff.dot(diff);
	}

	public float x()
	{
		return xyz[0];
	}
	
	public float y()
	{
		return xyz[1];
	}

	public float z()
	{
		return xyz[2];
	}
	
	public void setX(float x) {
		xyz[0] = x;
	}
	public void setY(float y) {
		xyz[1] = y;
	}
	public void setZ(float z) {
		xyz[2] = z;
	}

	public String toString()
	{
		return "( " + xyz[0] + " " + xyz[1] + " " + xyz[2] + " )"; 
	}
	
	public float xyz[] = new float[3];
}
