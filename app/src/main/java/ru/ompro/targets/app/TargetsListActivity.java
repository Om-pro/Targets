package ru.ompro.targets.app;

import android.support.v4.app.Fragment;

/**
 * Created by Ompro on 10.01.2017.
 */

public class TargetsListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new TargetListFragment();
    }
}
