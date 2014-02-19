package com.android.criminalintent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import com.android.crinimalintent.R;

public class DatePickerFragment extends DialogFragment {
	
	public static final String DATE = "com.android.criminalintent.date";
	
	private Date date;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		View view = getActivity().getLayoutInflater().inflate(R.layout.date_picker, null);
		
		date = (Date)getArguments().getSerializable(DATE);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
		datePicker.init(year, month, day, new OnDateChangedListener() {
			
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				date = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
				
				getArguments().putSerializable(DATE, date);
				
			}
		});
		
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.date_picker_title)
				.setView(view)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sendResult(Activity.RESULT_OK);
						
					}
				}).create();
	}
	
	public static DatePickerFragment newInstance(Date date){
		DatePickerFragment fragment = new DatePickerFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(DATE, date);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	private void sendResult(int resultCode){
		if(getTargetFragment()==null){
			return;
		}
		
		Intent i = new Intent();
		i.putExtra(DATE, date);
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}
	
}
