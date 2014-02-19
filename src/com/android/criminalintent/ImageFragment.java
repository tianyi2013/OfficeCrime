package com.android.criminalintent;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends DialogFragment {

	public static final String EXTRA_IMAGE_PATH = "com.android.criminalintent.image.path";
	
	public static final String EXTRA_DEVICE_ORIENTATION = "com.android.criminalintent.device.orientation";
	
	private ImageView imageView;
	
	
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		imageView = new ImageView(getActivity());
		String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
		int orientation = (Integer) getArguments().getSerializable(EXTRA_DEVICE_ORIENTATION);
		BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path);
		
		if (orientation == Configuration.ORIENTATION_PORTRAIT){
			image = PictureUtils.getPortraitDrawable(imageView, image);
		}
		
		imageView.setImageDrawable(image);
		
		return imageView;
	}
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		PictureUtils.cleanImageView(imageView);
	}
	
	public static ImageFragment newInstance(String path, int orientation){
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_IMAGE_PATH, path);
		args.putSerializable(EXTRA_DEVICE_ORIENTATION, orientation);
		
		ImageFragment fragment = new ImageFragment();
		fragment.setArguments(args);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		
		return fragment;
	}
	
}
