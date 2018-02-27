package ru.ompro.targets.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by Ompro on 27.01.2017.
 */

public class TargetPagerActivity extends AppCompatActivity {

    private static final String EXTRA_CRIME_ID = "com.example.ompro.app.crime_id";

    private ViewPager mViewPager;
    private List<Target> mTargets;

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, TargetPagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ru.ompro.targets.app.R.layout.activity_crime_pager);
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = (ViewPager) findViewById(ru.ompro.targets.app.R.id.activity_crime_pager_view_pager);

        mTargets = TargetLab.get(this).getmCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Target target = mTargets.get(position);
                return TargetFragment.newInstance(target.getId());
            }

            @Override
            public int getCount() {
                return mTargets.size();
            }
        });
        for (int i = 0; i < mTargets.size(); i++) {
            if (mTargets.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
