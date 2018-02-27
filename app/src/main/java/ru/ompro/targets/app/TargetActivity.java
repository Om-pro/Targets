package ru.ompro.targets.app;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class TargetActivity extends SingleFragmentActivity {

    private static final String EXTRA_CRIME_ID = "com.example.ompro.app.crime_id";


    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, TargetActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return TargetFragment.newInstance(crimeId);
    }
}
