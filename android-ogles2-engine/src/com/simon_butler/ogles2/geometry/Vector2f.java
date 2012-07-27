package com.simon_butler.ogles2.geometry;

public class Vector2f
{
	public Vector2f()
	{
		xyz[0] = 0;
		xyz[1] = 0;
	}

	public Vector2f(float x, float y)
	{
		xyz[0] = x;
		xyz[1] = y;
	}

	public Vector2f(float[] array)
	{
		if(array.length != 2)
			throw new RuntimeException("Must create vector with 2 element array");

		xyz[0] = array[0];
		xyz[1] = array[1];
	}
	
	public float[] array()
	{
		return (float[])xyz.clone();
	}

	public Vector2f add(Vector2f rhs)
	{
		return new Vector2f(
			xyz[0] + rhs.xyz[0],
			xyz[1] + rhs.xyz[1]);
	}

	public Vector2f sub(Vector2f rhs)
	{
		return new Vector2f(
			xyz[0] - rhs.xyz[0],
			xyz[1] - rhs.xyz[1] );
	}
	
	public Vector2f neg()
	{
		return new Vector2f(-xyz[0], -xyz[1]);
	}

	public Vector2f mul(float c)
	{
		return new Vector2f(c*xyz[0], c*xyz[1]);
	}

	public Vector2f div(float c)
	{
		return new Vector2f(xyz[0]/c, xyz[1]/c);
	}

	public float dot(Vector2f rhs)
	{
		return xyz[0]*rhs.xyz[0] +
			xyz[1]*rhs.xyz[1];
	}

	public Vector2f cross(Vector2f rhs)
	{
		return new Vector2f(
			xyz[1]*rhs.xyz[2] - xyz[2]*rhs.xyz[1],
			xyz[0]*rhs.xyz[2] - xyz[2]*rhs.xyz[0]
		);
	}

	public boolean equals(Object obj)
	{
		if( obj instanceof Vector2f )
		{
			Vector2f rhs = (Vector2f)obj;

			return xyz[0]==rhs.xyz[0] &&
			       xyz[1]==rhs.xyz[1];
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

	public Vector2f normalize()
	{
		return this.div(norm());
	}
	
	public float distance(Vector2f rhs)
	{
		return rhs.sub(this).norm();
	}

	public float x()
	{
		return xyz[0];
	}
	
	public float y()
	{
		return xyz[1];
	}
	
	public void setX(float x) {
		xyz[0] = x;
	}
	public void setY(float y) {
		xyz[1] = y;
	}

	public String toString()
	{
		return "( " + xyz[0] + " " + xyz[1] + " )"; 
	}
	
	public float xyz[] = new float[2];
}
