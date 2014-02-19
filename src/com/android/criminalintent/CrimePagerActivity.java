package com.android.criminalintent;

import java.util.List;
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.android.crinimalintent.R;

public class CrimePagerActivity extends FragmentActivity {
	
	private ViewPager viewPager;
	private List<Crime> crimes;
	
	
	@Override
	public void onCreate(Bundle savedInstatanceState){
		super.onCreate(savedInstatanceState);
		viewPager = new ViewPager(this);
		viewPager.setId(R.id.viewPager);
		setContentView(viewPager);
		
		crimes = CrimeLab.get(this).getCrimes();
		
		FragmentManager fm = getSupportFragmentManager();
		viewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
			
			@Override
			public int getCount() {
				return crimes.size();
			
			}
			
			@Override
			public Fragment getItem(int arg0) {
				return CrimeFragment.newInstance(crimes.get(arg0).getId());
			}
		});
		
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int pos) {
				Crime crime = crimes.get(pos);
				if(crime.getTitle()!=null){
					setTitle(crime.getTitle());
				}
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
				
			}
		});
		
		UUID crimeId = (UUID) getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
		for(int i=0;i<crimes.size();i++){
			if(crimes.get(i).getId().equals(crimeId)){
				viewPager.setCurrentItem(i);
				break;
			}
		}
		
	}
}
