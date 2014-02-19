package com.android.criminalintent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.crinimalintent.R;

public class CrimeCameraFragment extends Fragment {
	
	private static final String TAG = "CrimeCameraFragment";
	
	private Camera camera;
	private SurfaceView surfaceView;
	private View progressContainer;
	
	private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
		
		@Override
		public void onShutter() {
			progressContainer.setVisibility(View.VISIBLE);
			
		}
	};
	
	private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			
			String fileName = UUID.randomUUID().toString()+".jpg";
			FileOutputStream os = null;
			boolean success = true;
			
			try{
				os = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
				os.write(data);
			}catch(IOException e){
				Log.e(TAG, "cannot save image");
				success = false;
			}finally{
				try{
					if (os!=null){
						os.close();
					}
				}catch (Exception e){
					Log.e(TAG, "cannot close file output stream");
					success = false;
				}
			}
			
			if (success){
				Log.i(TAG, "JPEG saved at "+fileName);
				Intent i = new Intent();
				i.putExtra("extra.photo.name", fileName);
				i.putExtra("extra.device.orientation", getActivity().getWindowManager().getDefaultDisplay()
			             .getRotation());
				getActivity().setResult(Activity.RESULT_OK,i);
			}else{
				getActivity().setResult(Activity.RESULT_CANCELED);
			}
			
			getActivity().finish();
		}
	};
	
	@Override
	@SuppressWarnings("deprecation")
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_crime_camera, parent, false);
		
		Button button = (Button) v.findViewById(R.id.crime_take_picture_Button);
		
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (camera != null){
					camera.takePicture(shutterCallback, null, pictureCallback);
				}
				
			}
		});
		
		surfaceView = (SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);
		
		SurfaceHolder holder = surfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		holder.addCallback(new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if (camera!=null){
					camera.stopPreview();
				}
				
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				try{
					if(camera!=null){
						camera.setPreviewDisplay(holder);
					}
				}catch(IOException e){
					Log.e(TAG, "Error setting up preview display", e);
				}
				
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				
				if (camera ==null){
					return;
				}
				
				Camera.Parameters params = camera.getParameters();
				
				Size s = getBestSupportedSize(params.getSupportedPreviewSizes());
				
				params.setPreviewSize(s.width, s.height);
				
				s = getBestSupportedSize(params.getSupportedPictureSizes());
				params.setPictureSize(s.width, s.height);
				
				try{
					camera.startPreview();
				}catch(Exception e){
					camera.release();
					camera = null;
					Log.e(TAG, "Could not start preview", e);
				}
			}
		});
		
		progressContainer = v.findViewById(R.id.crime_camera_progressContainer);
		progressContainer.setVisibility(View.INVISIBLE);
		
		return v;
	}
	
	@TargetApi(9)
	@Override
	public void onResume(){
		super.onResume();
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD){
			camera = Camera.open(0);
		}else{
			camera = Camera.open();
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		if(camera !=null){
			camera.release();
			camera = null;
		}
	}
	
	// find the largest supported preview size
	private Size getBestSupportedSize(List<Size> sizes){
		Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		for (Size size: sizes){
			if (size.width*size.height>largestArea){
				largestArea = size.width * size.height;
				bestSize = size;
			}
		}
		return bestSize;
	}
	
	private int getDeviceOrientation(){
		
		
		int rotation = getResources().getConfiguration().orientation;
		
		return rotation;
	     
	     /*switch (rotation) {
	         case Surface.ROTATION_0: degree = 0; break;
	         case Surface.ROTATION_90: degree = 90; break;
	         case Surface.ROTATION_180: degree = 180; break;
	         case Surface.ROTATION_270: degree = 270; break;
	         
	     }
	     
	     return degree;*/
	}
	
	
}
