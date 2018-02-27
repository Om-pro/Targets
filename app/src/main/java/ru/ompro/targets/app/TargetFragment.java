package ru.ompro.targets.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Ompro on 09.01.2017.
 */

public class TargetFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;

    private Target mTarget;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;

//    GoogleSignInActivity auth = new GoogleSignInActivity();
//    FirebaseUser firebaseUser = auth.getmAuth().getCurrentUser();
//    private DatabaseReference myRef;
//    FirebaseListAdapter<String> fAdapter;

    public static TargetFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        TargetFragment fragment = new TargetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mTarget.isSolved()) {
            solvedString = getString(R.string.target_report_solved);
        } else {
            solvedString = getString(R.string.target_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mTarget.getDate()).toString();

        String suspect = mTarget.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.target_report_no_collaboration);
        } else {
            suspect = getString(R.string.target_report_collaborated, suspect);
        }

        String report = getString(R.string.target_report, mTarget.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mTarget = TargetLab.get(getActivity()).getTarget(crimeId);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        TargetLab.get(getActivity()).updateTarget(mTarget);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(ru.ompro.targets.app.R.layout.fragment_target, container, false);
        mTitleField = (EditText) v.findViewById(ru.ompro.targets.app.R.id.crime_title);
        mTitleField.setText(mTarget.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTarget.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDateButton = (Button) v.findViewById(ru.ompro.targets.app.R.id.crime_date);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        mDateButton.setText(format.format(mTarget.getDate()));
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mTarget.getDate());
                dialog.setTargetFragment(TargetFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(ru.ompro.targets.app.R.id.crime_solved);
        mSolvedCheckBox.setChecked(mTarget.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mTarget.setSolved(isChecked);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.target_report_subject));
                startActivity(i);
            }
        });

        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mTarget.setDate(date);
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
            mDateButton.setText(format1.format(mTarget.getDate()));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(ru.ompro.targets.app.R.menu.fragment_target, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ru.ompro.targets.app.R.id.menu_item_delete_crime:
                TargetLab.get(getActivity()).deleteTarget(mTarget);
                getActivity().finish();
                return true;
            default:
//                myRef = FirebaseDatabase.getInstance().getReference();
//                myRef.setValue(mTarget.getTitle());
//                fAdapter = new FirebaseListAdapter<String>(this, String.class, android.R.layout.simple_list_item_1, myRef.child(firebaseUser.getUid()).child("Tasks")) {
//                    @Override
//                    protected void populateView(View v, String s, int position) {
//                        TextView text = (TextView) v.findViewById(android.R.id.text1);
//
//                    }
//                };
//                myRef.child(String.valueOf(mTarget.getId())).child("Tasks").push().setValue(mTitleField.getText());
                return super.onOptionsItemSelected(item);
        }
    }
}
