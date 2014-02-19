package com.android.criminalintent;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;

public class CriminalIntentJSONSerializer {

	private Context context;
	private String fileName;

	public CriminalIntentJSONSerializer(Context context, String fileName) {
		this.context = context;
		this.fileName = fileName;
	}

	public void saveCrimes(List<Crime> crimes) throws JSONException,
			IOException {
		JSONArray array = new JSONArray();

		for (Crime c : crimes) {
			array.put(c.toJSON());
		}

		Writer writer = null;
		try {
			OutputStream out = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public List<Crime> loadCrimes() throws IOException, JSONException {
		List<Crime> crimes = new ArrayList<Crime>();

		BufferedReader reader = null;

		try {
			FileInputStream in = context.openFileInput(fileName);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			JSONArray array = (JSONArray) new JSONTokener(sb.toString())
					.nextValue();
			for (int i = 0; i < array.length(); i++) {
				crimes.add(new Crime(array.getJSONObject(i)));
			}

			return crimes;

		} catch (FileNotFoundException e) {

		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return crimes;
	}
}
