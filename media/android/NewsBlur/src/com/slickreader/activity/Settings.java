package com.slickreader.activity;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.slickreader.R;
import com.slickreader.util.PrefConstants;

import android.os.Bundle;

public class Settings extends SherlockPreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.getPreferenceManager().setSharedPreferencesName(PrefConstants.PREFERENCES);
        addPreferencesFromResource(R.layout.activity_settings);
    }

}
