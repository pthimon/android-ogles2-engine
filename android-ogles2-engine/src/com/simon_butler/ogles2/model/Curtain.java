package com.simon_butler.ogles2.model;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.simon_butler.ogles2.geometry.Vector3f;
import com.simon_butler.ogles2.materials.PhongMaterial;
import com.simon_butler.ogles2.physics.CurtainPhysics;
import com.simon_butler.ogles2.primitives.CurtainPrimitive;

public class Curtain extends CurtainPrimitive {
	
	protected Bitmap mNormalMap;
	private int mHeadingHeight = 15;
	private int mHeadingTotalHeight = 16;//35;
	
	//private float mNormalSF = 0.5f;
	private float mNormalSF = 10.0f;
	
	private boolean mUpdatePhysics = false;
	
	public enum CurtainDirection {
		LEFT,
		RIGHT
	}
	
	protected CurtainPhysics mPhysics;
	
	public Curtain(CurtainDirection direction, int width, int drop) {
		super(width,drop,(int)width/10,4);
		
		buildPrimitive();
		alterVertices();
		
		mPhysics = new CurtainPhysics(this, direction);
	}
	
	public boolean isDirty() {
		return (mUpdatePhysics || mPhysics.isActive());
	}
	
	public void render(float[] projMatrix, float[] vMatrix) {
		if (isDirty()) {
			mUpdatePhysics = false;
			mPhysics.update();
			updateNormalMap();
			updateMesh();
		}
		
		super.render(projMatrix, vMatrix);
	}
	
	public void setCurtainPos(float pos) {
		mPhysics.setCurtainPos(pos);
		mUpdatePhysics = true;
	}
	
	public void setMaterial(PhongMaterial mat) {
		super.setMaterial(mat);
		
		//initialise normalMap to heading normals 
		//(width and height must be power of 2)
		int w = mat.getTextureWidth();
		int h = mat.getTextureHeight();
		mNormalMap = Bitmap.createBitmap(w, mHeadingTotalHeight, Bitmap.Config.ARGB_8888);
		
		//heading
		float segsize = w / (mSegmentsX*6);
		float dx = (float)Math.PI / segsize;
		float angleIndex = 0;
		float y1 = (float)Math.cos(angleIndex);

		for (int i=0; i < w; i++) {
			//increment to next pixel
			angleIndex += dx;
			//loop every 2*pi
			if (angleIndex > Math.PI) {
				angleIndex = (float)-Math.PI;
			}
			//get dy
			float y2 = (float)Math.cos(angleIndex);
			float dy = (y2 - y1) * 0.5f;
			//calc normal
			float nx = -dy;
			float ny = dx;
			//normalise
			float len = (float)Math.sqrt(nx*nx + ny*ny);
			nx /= len;
			ny /= len;
			//convert to colour (x = R, y = G)
			int red = (int)((nx * 127)+128);
			int green = (int)((ny * 127)+128);
			int blue = 127;
			
			for (int j=0; j < mHeadingTotalHeight-1; j++) {
				mNormalMap.setPixel(i, j, Color.rgb(red, green, blue));
			}
			
			y1 = y2;
		}
		
		
		
           /*try {
            	String path = Environment.getExternalStorageDirectory().toString();
                OutputStream fOut = null;
                File file = new File(path, "Normal.jpg");
				fOut = new FileOutputStream(file);
				
				mNormalMap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
	            fOut.flush();
	            fOut.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
          	
		mat.setNormalMap(mNormalMap);
	}
	
	// initilise folds in curtain
	protected void alterVertices() {
		for (int i=0; i <= mSegmentsY; i++) {
			for (int j=0; j <= mSegmentsX; j++) {
				Vector3f v = vertex(j,i);
				if (j % 2 == 0)
					setVertex(j,i, new Vector3f(v.x(), v.y(), -10));
				else
					setVertex(j,i, new Vector3f(v.x(), v.y(), 0));
			}
		}
	}
	
	protected void updateMesh() {
		//make bottom curve
		for (int i=0; i < mSegmentsX; i++) {
			Vector3f x1 =  vertex(i, mSegmentsY);
			Vector3f x2 =  vertex(i+1, mSegmentsY);
			Vector3f x12 = x2.sub(x1);

			for (int j=1; j < mBottomSegmentMultiplier; j++) {
				Vector3f xtmp = x12.mul((float)j/mBottomSegmentMultiplier);
				Vector3f xab = x1.add(xtmp);
				
				//setVertex
				xab.setZ(xab.z() - ((float)Math.cos(((Math.PI / mBottomSegmentMultiplier)*j) + (Math.PI*i)) * 2));
				setVertexBottom(i*mBottomSegmentMultiplier + j, xab);
			}
		}
	}
	
	protected void updateNormalMap() {
		if (mMaterial instanceof PhongMaterial) {
			PhongMaterial material = (PhongMaterial)mMaterial;
			int w = material.getTextureWidth();
			
			float segsize = w / mSegmentsX;
			
			float dx = (float)Math.PI / segsize;
			float ny = dx;
			int green = (int)((ny * 127)+128);
			int blue = 127;
			
			float angleIndex = 0;
			float y1 = (float)Math.cos(angleIndex);
			
			//calc first sf
			Vector3f v1 = vertex(0, mSegmentsY);
			Vector3f v2 = vertex(1, mSegmentsY);
			int vertexIndex = 1;
			float sf = (float)Math.abs(v2.z() - v1.z()) / segsize;
			
			//TODO just scale pixel values (red channel), don't recalculate cosine & normal
			for (int i=0; i < w; i++) {
				//increment to next pixel
				float oldAngleIndex = angleIndex;
				angleIndex += dx;
				//update scale factor
				if (((oldAngleIndex < 0 && angleIndex > 0) || (angleIndex > Math.PI)) && vertexIndex < mSegmentsX) {
					vertexIndex++;
					v1 = v2;
					v2 = vertex(vertexIndex, mSegmentsY);
					sf = (float)Math.abs(v2.z() - v1.z()) / segsize;
				}
				//loop every 2*pi
				if (angleIndex > Math.PI) {
					angleIndex = (float)-Math.PI;
				}
				//get dy
				float y2 = (float)Math.cos(angleIndex);
				float dy = (y2 - y1) * sf * mNormalSF;
				//calc normal
				float nx = -dy;
				
				//normalise
				//float len = (float)Math.sqrt(nx*nx + ny*ny);
				//nx /= len;
				//ny /= len;
				//convert to colour (x = R, y = G)
				int red = (int)((nx * 127)+128);
				//int green = (int)((ny * 127)+128);
				
				//set row 124
				//mNormalMap.setPixel(i, mHeadingTotalHeight-1, Color.rgb(red, green, blue));
				
				for (int j=0; j < mHeadingTotalHeight; j++) {
					mNormalMap.setPixel(i, j, Color.rgb(red, green, blue));
				}
				
				y1 = y2;
			}
			
			//row 15 to 124 linearly interp
			/*for (var i:uint=mHeadingHeight; i < mHeadingTotalHeight-1; i++) {
				//copy in dest
				mNormalMap.copyPixels(mNormalMap, curtainRow, new Point(0,i));
				var mult:int = 255 - (((i-14)/(mHeadingTotalHeight-mHeadingHeight)) * 255);
				mNormalMap.merge(mNormalMap, headingRow, new Point(0,i), mult, mult, mult, mult);
			}*/
			
			material.updateNormalMap(mNormalMap);
			
			/*try {
	        	String path = Environment.getExternalStorageDirectory().toString();
	            OutputStream fOut = null;
	            File file = new File(path, "Normal.jpg");
				fOut = new FileOutputStream(file);
				
				mNormalMap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
	            fOut.flush();
	            fOut.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		
	}
}
