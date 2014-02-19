package com.android.criminalintent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

/*
 * Singleton pattern
 */
public class CrimeLab {
	
	private static final String FILE_NAME = "crimes.json";
	
	private static CrimeLab sCrimeLab;
	private Context appContext;
	private CriminalIntentJSONSerializer converter;
	
	private List<Crime> crimes;
	
	private CrimeLab(Context appContext){
		this.appContext = appContext;
		this.converter = new CriminalIntentJSONSerializer(this.appContext, FILE_NAME);
		try{
			this.crimes = converter.loadCrimes();
		}catch(Exception e){
			this.crimes = new ArrayList<Crime>();
		}
		
	}
	
	public static CrimeLab get(Context context){
		if(sCrimeLab == null){
			sCrimeLab = new CrimeLab(context.getApplicationContext());
		}
		
		return sCrimeLab;
	}
	
	public List<Crime> getCrimes(){
		return crimes;
	}
	
	public Crime getCrime(UUID id){
		for(Crime c: crimes){
			if(c.getId().equals(id)){
				return c;
			}
		}
		return null;
	}
	
	public void addCrime(Crime crime){
		this.crimes.add(crime);
	}
	
	public void deleteCrime(Crime crime){
		this.crimes.remove(crime);
	}
	
	public boolean saveCrimes(){
		try{
			converter.saveCrimes(getCrimes());
			return true;
		}catch (Exception e){
			return false;
		}
		
	}

}
