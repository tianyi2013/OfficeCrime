package com.android.criminalintent;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.android.crinimalintent.R;

public class CrimeFragment extends Fragment {

    private Crime crime;

    private EditText titleField;

    private Button dateButton;

    private CheckBox checkBox;

    private ImageButton photoButton;

    private ImageView imageView;

    public static final String EXTRA_CRIME_ID = "com.android.criminalintent.crime_id";
    public static final String DIALOG_DATE = "datePicker";
    public static final String DATE = "com.android.criminalintent.date";
    public static final String DIALOG_IMAGE = "image";
    private static final String TAG = "CrimeFragment";

    private static final int REQUEST_DATE = 0;

    private static final int REQUEST_PHOTO = 1;

    /*
     * chapter 20 change 2, delte picture using Contextual Action Bar
     */
    @SuppressLint("NewApi")
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.crime_list_item_context, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after
        // onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                deletePhoto();
                mode.finish(); // Action picked, so close the CAB
                return true;
            default:
                return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID crimeId = (UUID) this.getArguments().getSerializable(EXTRA_CRIME_ID);
        crime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crime, parent, false);

        // add app back button if the build version is lareger than v11
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }

        }

        titleField = (EditText) view.findViewById(R.id.crime_title);

        titleField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                crime.setTitle(text.toString());

            }

        });

        titleField.setText(crime.getTitle());

        dateButton = (Button) view.findViewById(R.id.crime_date);
        updateDate();
        dateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment fragment = DatePickerFragment.newInstance(crime.getDate());
                fragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                fragment.show(fm, DIALOG_DATE);

            }
        });

        checkBox = (CheckBox) view.findViewById(R.id.crime_solved);
        checkBox.setChecked(crime.isSolved());
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setSolved(isChecked);

            }
        });

        photoButton = (ImageButton) view.findViewById(R.id.crime_imageButton);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);

            }
        });

        // disable photo button if there is no camera on device
        PackageManager pm = getActivity().getPackageManager();
        boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                || (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD && Camera.getNumberOfCameras() > 0);
        if (!hasCamera) {
            photoButton.setEnabled(false);
        }

        imageView = (ImageView) view.findViewById(R.id.crime_imageView);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Photo p = crime.getPhoto();
                if (p != null) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    String path = getActivity().getFileStreamPath(p.getFileName()).getAbsolutePath();
                    int orientation = p.getOrientation();

                    ImageFragment.newInstance(path, orientation).show(fm, DIALOG_IMAGE);
                }

            }
        });

        // register context menu

        imageView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "On Long Click");
                getActivity().startActionMode(actionModeCallback);
                return true;

            }

        });

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DATE);
            crime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_PHOTO) {
            String filename = data.getStringExtra("extra.photo.name");
            int deviceOrientation = data.getIntExtra("extra.device.orientation", 0);
            Photo photo = new Photo(filename, deviceOrientation);
            crime.setPhoto(photo);
            showPhoto();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:

            if (NavUtils.getParentActivityName(getActivity()) != null) {
                NavUtils.navigateUpFromSameTask(getActivity());
            }

            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(imageView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private void updateDate() {
        String dateString = DateFormat.format("yyyy-MM-dd", crime.getDate()).toString();
        dateButton.setText(dateString);
    }

    public EditText getTitleField() {
        return titleField;
    }

    public void setTitleField(EditText titleField) {
        this.titleField = titleField;
    }

    public Crime getCrime() {
        return crime;
    }

    public void setCrime(Crime crime) {
        this.crime = crime;
    }

    public Button getDateButton() {
        return dateButton;
    }

    public void setDateButton(Button dateButton) {
        this.dateButton = dateButton;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    @SuppressLint("NewApi")
    private void showPhoto() {
        Photo p = crime.getPhoto();
        BitmapDrawable b = null;
        if (p != null) {
            b = PictureUtils.getScaledDrawable(getActivity(), getActivity().getFileStreamPath(p.getFileName()).getAbsolutePath());

            if (p.getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
                b = PictureUtils.getPortraitDrawable(imageView, b);
            }

            imageView.setImageDrawable(b);

        }

    }

    private boolean deletePhoto() {
        if (crime.getPhoto() == null)
            return false;

        String path = getActivity().getFileStreamPath(crime.getPhoto().getFileName()).getAbsolutePath();
        File f = new File(path);
        f.delete();
        crime.setPhoto(null);
        PictureUtils.cleanImageView(imageView);
        return true;

    }

}
