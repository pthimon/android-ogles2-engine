package com.simon_butler.ogles2.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Environment;

import com.simon_butler.ogles2.geometry.Vector3f;
import com.simon_butler.ogles2.materials.PhongMaterial;
import com.simon_butler.ogles2.physics.CurtainPhysics;
import com.simon_butler.ogles2.physics.RomanBlindPhysics;
import com.simon_butler.ogles2.primitives.CurtainPrimitive;
import com.simon_butler.ogles2.primitives.PlanePrimitive;

public class RomanBlind extends PlanePrimitive {
	
	protected Bitmap mNormalMap;
	
	private float mNormalSF = 5.0f;
	
	private boolean mUpdatePhysics = false;
	
	protected RomanBlindPhysics mPhysics;
	
	public RomanBlind(int width, int drop) {
		super(width,drop,1,100);
		
		buildPrimitive();
		
		mPhysics = new RomanBlindPhysics(this);
	}
	
	public boolean isDirty() {
		return (mUpdatePhysics || mPhysics.isActive());
	}
	
	public void render(float[] projMatrix, float[] vMatrix) {
		if (isDirty()) {
			mUpdatePhysics = false;
			mPhysics.update();
			updateNormalMap();
		}
		
		super.render(projMatrix, vMatrix);
	}
	
	public void setOpenPos(float pos) {
		mPhysics.setOpenPos(pos);
		mUpdatePhysics = true;
	}
	
	public void setMaterial(PhongMaterial mat) {
		super.setMaterial(mat);
		
		//initialise normalMap to heading normals 
		//(width and height must be power of 2)
		int w = mat.getTextureWidth();
		int h = mat.getTextureHeight();
		mNormalMap = Bitmap.createBitmap(1, h, Bitmap.Config.ARGB_8888);
		mat.setNormalMap(mNormalMap);
		
/*		//heading
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
				mHeadingNormalMap.setPixel(i, j, Color.rgb(red, green, blue));
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
			}*
          	
		mat.setNormalMap(mNormalMap);
		
		//heading height in mm
		float headingHeight = 100f;
		
		FloatBuffer normalCoords = ByteBuffer.allocateDirect(getNumVertices() * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		for (int i=0; i < mSegmentsY; i++) {
    		float v = (i/(float)mSegmentsY) * (mHeight/headingHeight);
    		for (int j=0; j <= mSegmentsX; j++) {
    			float u = j/(float)mSegmentsX;
    			normalCoords.put(u);
        		normalCoords.put(v);
    		}
		}
		for (int j=0; j < mNumBottomVertices; j++) {
			float u = (j/(float)mNumBottomSegments);
			normalCoords.put(u);
    		normalCoords.put(mHeight/headingHeight);
		}
		mat.setNormalCoords(normalCoords);*/
	}
	
	protected void updateNormalMap() {
		if (mMaterial instanceof PhongMaterial) {
			PhongMaterial material = (PhongMaterial)mMaterial;
			int h = material.getTextureHeight();
			
			float segsize = (float)h / (float)mSegmentsY;
			
			//float dx = (float)Math.PI / segsize;
			//float ny = dx;
			int red = 255;
			int green = 127;
			int blue = 255;							
			
			/*float angleIndex = 0;
			float y1 = (float)Math.cos(angleIndex);
			
			//calc first sf
			Vector3f v1 = vertex(0, mSegmentsY);
			Vector3f v2 = vertex(1, mSegmentsY);
			int vertexIndex = 1;
			float sf = (float)Math.abs(v2.z() - v1.z()) / (getWidth() / mSegmentsX);
			if (sf > 1) sf = 1;
			
			int[] curtainRow = new int[w];*/
			
			//TODO just scale pixel values (red channel), don't recalculate cosine & normal
			for (int i=0; i < h; i++) {
				mNormalMap.setPixel(0, i, Color.rgb(red, green, blue));
				//mNormalMap.setPixel(1, i, Color.rgb(red, green, blue));
				
				/*//increment to next pixel
				float oldAngleIndex = angleIndex;
				angleIndex += dx;
				//update scale factor
				if (((oldAngleIndex < 0 && angleIndex > 0) || (angleIndex > Math.PI)) && vertexIndex < mSegmentsX) {
					vertexIndex++;
					v1 = v2;
					v2 = vertex(vertexIndex, mSegmentsY);
					sf = (float)Math.abs(v2.z() - v1.z()) / (getWidth() / mSegmentsX);
					if (sf > 1) sf = 1;
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
				
				//for (int j=15; j < mHeadingTotalHeight; j++) {
				//	mNormalMap.setPixel(i, j, Color.rgb(red, green, blue));
				//}
				curtainRow[i] = Color.rgb(red,green,blue);
				
				y1 = y2;*/	
			}
			
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
