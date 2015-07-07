package com.commonsware.empublite;

import java.util.List;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class Preferences extends SherlockPreferenceActivity {

    String[] fragNameWhiteList = new String[]{
            "com.commonsware.empublite.StockPreferenceFragment"
    };


    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
            addPreferencesFromResource(R.xml.pref_display);
        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragName) {

        for (int i = 0; i < fragNameWhiteList.length; i++) {
            if (fragNameWhiteList[i].equals(fragName)) {
                return true;
            }
        }

        return false;
    }
}