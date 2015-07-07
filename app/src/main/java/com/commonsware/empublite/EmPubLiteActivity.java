package com.commonsware.empublite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.commonsware.cwac.wakeful.WakefulIntentService;


public class EmPubLiteActivity extends SherlockFragmentActivity {

    private ViewPager pager = null;

    private SimpleContentFragment help=null;
    private SimpleContentFragment about=null;

    private View sidebar=null;
    private View divider=null;


    private static final String MODEL="model";
    private ModelFragment model=null;
    private SharedPreferences prefs=null;
    private static final String PREF_LAST_POSITION="lastPosition";
    private static final String PREF_SAVE_LAST_POSITION="saveLastPosition";
    private static final String PREF_KEEP_SCREEN_ON="keepScreenOn";
    private static final String HELP="help";
    private static final String ABOUT="about";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportFragmentManager().findFragmentByTag(MODEL) == null) {
            model=new ModelFragment();
            getSupportFragmentManager().beginTransaction().add(model, MODEL)
                    .commit();
        }
        else {
            model=
                    (ModelFragment)getSupportFragmentManager().findFragmentByTag(MODEL);
        }
        setContentView(R.layout.main);
        pager=(ViewPager)findViewById(R.id.pager);
        getSupportActionBar().setHomeButtonEnabled(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(com.commonsware.empublite.R.menu.options, menu);
        return(super.onCreateOptionsMenu(menu));
    }


    void setupPager(SharedPreferences prefs, BookContents contents) {

        // Strore the shared preferences in a Class data member
        this.prefs=prefs;

        // MUST set adapter BEFORE pager currentItem can be set
        ContentsAdapter adapter=new ContentsAdapter(this, contents);
        pager.setAdapter(adapter);

        /*
            Hide the progress spinnner and show the view pager element
         */
        findViewById(com.commonsware.empublite.R.id.progressBar1).setVisibility(View.GONE);
        findViewById(com.commonsware.empublite.R.id.pager).setVisibility(View.VISIBLE);

        // Check if user has requested the app to remember last page in the preferences
        if (prefs.getBoolean(PREF_SAVE_LAST_POSITION, false)) {
            pager.setCurrentItem(prefs.getInt(PREF_LAST_POSITION, 0));
        }

    }

    @Override
    public void onPause() {

        unregisterReceiver(onUpdate);

        if (prefs != null) {
            int position = pager.getCurrentItem();
            prefs.edit().putInt(PREF_LAST_POSITION, position).apply();
        }
        super.onPause();
    }

    /*
        FROM BOOK p467:

            This approach is somewhat limited, in that we are only setting this during the call to
            setupPager(). If the user changes the preference value, that change would only take
            effect when the activity was restarted (e.g., user rotates the screen, user exits the app
            via BACK and returns later).

            The simplest way for us to have this take more immediate effect is to realize that
            EmPubLiteActivity will be paused and stopped when the Preferences activity is on
            the screen, and will be started and resumed when the user is done adjusting
            preferences. So, we can simply override onResume() to also update the screen-on
            setting:
     */

    @Override
    public void onResume() {
        super.onResume();
        if (prefs != null) {
            pager.setKeepScreenOn(prefs.getBoolean(PREF_KEEP_SCREEN_ON, false));
        }
        IntentFilter f=
                new IntentFilter(DownloadInstallService.ACTION_UPDATE_READY);
        f.setPriority(1000);
        registerReceiver(onUpdate, f);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                pager.setCurrentItem(0, false);
                return(true);

            case com.commonsware.empublite.R.id.about:
                Intent i=new Intent(this, SimpleContentActivity.class);
                i.putExtra(SimpleContentActivity.EXTRA_FILE,
                        "file:///android_asset/misc/about.html");
                startActivity(i);
            return(true);

            case com.commonsware.empublite.R.id.help:
                i=new Intent(this, SimpleContentActivity.class);
                i.putExtra(SimpleContentActivity.EXTRA_FILE,
                        "file:///android_asset/misc/help.html");
                startActivity(i);
                return(true);

            case R.id.settings:
                startActivity(new Intent(this, Preferences.class));
                return(true);

            case R.id.notes:
                i=new Intent(this, NoteActivity.class);
                i.putExtra(NoteActivity.EXTRA_POSITION, pager.getCurrentItem());
                startActivity(i);
                return(true);

            case R.id.update:
                WakefulIntentService.sendWakefulWork(this,
                        DownloadCheckService.class);
                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    private BroadcastReceiver onUpdate=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent i) {
            model.updateBook();
            abortBroadcast();
        }
    };

    void openSidebar() {
        LinearLayout.LayoutParams p=
                (LinearLayout.LayoutParams)sidebar.getLayoutParams();
        if (p.weight == 0) {
            p.weight=3;
            sidebar.setLayoutParams(p);
        }
        divider.setVisibility(View.VISIBLE);
    }
}
