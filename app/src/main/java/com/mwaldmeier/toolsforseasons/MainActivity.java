package com.mwaldmeier.toolsforseasons;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Seasons ThisGame;
    SoundPool sp;
    //settings variables
    final String SOUND_ON = "SOUND_ON";
    final String SCREEN_ALWAYS_ON = "SCREEN_ALWAYS_ON";
    final String P2P_ON = "P2P_ON";
    final String PLAYER_NAMES = "PLAYER_NAMES";
    final String TIMES_OPENED = "TIMES_OPENED";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    NavigationView navigationView;
    double widthByDensity;
    double hiByDensity;
    boolean duelPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThisGame = ((Seasons) this.getApplication());

        //setup defalut game of 2
        ThisGame.setUpNewGame(2);

        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

        preferences = getSharedPreferences("SeasonsPrefs", Context.MODE_PRIVATE);
        editor = preferences.edit();
        setUpSettings();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.getMenu().findItem(R.id.nav_game).setVisible(false);

        getScreenSize();
        screenSetup();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getScreenSize();
        screenSetup();
    }

    private void screenSetup() {
        if (widthByDensity < 3.7) {
            duelPane = false;
            setActiveFragment(new ScoreFragment());
            ActionBar actionBar = (ActionBar) this.getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        } else {
            duelPane = true;
            navigationView.getMenu().findItem(R.id.nav_elements).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_score).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_game).setVisible(true);
            setActiveFragment(new DuelPaneGameFragment());
        }
    }

    public boolean isDuelPane() {
        return duelPane;
    }

    public double getHiByDensity() {
        return hiByDensity;
    }

    private void getScreenSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        int dens=dm.densityDpi;
        widthByDensity = (double)width/(double)dens;
        hiByDensity = (double)height/(double)dens;
    }

    private void setUpSettings() {
        if (getSoundOn() == null) {
            setSetting(SOUND_ON, "1");
        }
        if (getScreenAlwaysOn() == null) {
            setSetting(SCREEN_ALWAYS_ON, "1");
        }
        if (getP2POn() == null) {
            setSetting(P2P_ON, "1");
        }
        setSetting(TIMES_OPENED, (getTimesUsed() + 1));
        if (getTimesUsed() == 5) {
            createRateAppAlert();
        }
    }

    private void createRateAppAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert
                .setTitle("Please rate my app.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        goToRatePage();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
        alert.show();
    }

    public void goToRatePage() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    private int getTimesUsed() {
        return preferences.getInt(TIMES_OPENED, 0);
    }
    public String getP2POn() {
        return preferences.getString(P2P_ON, null);
    }
    public Set<String> getPlayerNames() {
        return preferences.getStringSet(PLAYER_NAMES, null);
    }
    public String getScreenAlwaysOn() {
        return preferences.getString(SCREEN_ALWAYS_ON, null);
    }
    public String getSoundOn() {
        return preferences.getString(SOUND_ON, null);
    }

    private void setSetting(String setting, String value) {
        editor.putString(setting, value);
        editor.apply();
        if (setting.equals(SCREEN_ALWAYS_ON)) {
            setScreenSetting(value);
        }
    }
    private void setSetting(String setting, int value) {
        editor.putInt(setting, value);
        editor.apply();
    }
    public void addNameToSettings(String name) {
        Set<String> set = getPlayerNames();
        set.add(name);
        editor.putStringSet(PLAYER_NAMES, set);
        editor.commit();
    }

    public SoundPool getSoundPool() {
        return sp;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_newGame) {
            sendNewGameAlert();
            return true;
        } else if (id == R.id.action_settings) {
            changeSettingsAlert();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendNewGameAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert
                .setTitle("Number of Players?")
                .setItems(R.array.playerNumbers, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ThisGame.setUpNewGame((which + 2));
                        screenSetup();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });


        alert.show();
    }

    private void changeSettingsAlert() {
        boolean selected[] = new boolean[3];
        if (getSoundOn().equals("1")) {
            selected[0] = true;
        } else {
            selected[0] = false;
        }
        if (getScreenAlwaysOn().equals("1")) {
            selected[1] = true;
        } else {
            selected[1] = false;
        }
        if (getP2POn().equals("1")) {
            selected[2] = true;
        } else {
            selected[2] = false;
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert
                .setTitle("Settings")
                .setMultiChoiceItems(
                        R.array.settings,
                        selected,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                String checkedSetting = null;
                                switch (which) {
                                    case 0:
                                        checkedSetting = SOUND_ON;
                                        break;
                                    case 1:
                                        checkedSetting = SCREEN_ALWAYS_ON;
                                        break;
                                    case 2:
                                        checkedSetting = P2P_ON;
                                        break;
                                }

                                if (isChecked) {
                                    setSetting(checkedSetting, "1");
                                } else {
                                    setSetting(checkedSetting, "0");
                                }
                            }
                        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
        alert.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        if (id == R.id.nav_score) {
            fragment = new ScoreFragment();
            setActiveFragment(fragment);
        } else if (id == R.id.nav_elements) {
            fragment = new ElementsFragment();
            setActiveFragment(fragment);
        } else if (id == R.id.nav_about) {
            fragment = new AboutFragment();
            setActiveFragment(fragment);
        } else if (id == R.id.nav_settings) {
            changeSettingsAlert();
        } else if (id == R.id.nav_newGame){
            sendNewGameAlert();
        } else if (id == R.id.nav_game) {
            fragment = new DuelPaneGameFragment();
            setActiveFragment(fragment);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setActiveFragment(Fragment fragment) {

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

    }

    public void goToPage(int pageNum) {
        Fragment fragment = null;

        if (pageNum == 1) {
            fragment = new ScoreFragment();
        } else if (pageNum == 2) {
            fragment = new ElementsFragment();
        } else if (pageNum == 3) {
            fragment = new AboutFragment();
        }

        if (fragment != null) {
            setActiveFragment(fragment);
        }
        navigationView.getMenu().getItem(pageNum - 1).setChecked(true);
    }

    private void setScreenSetting(String screenSetting) {
        View root = findViewById(android.R.id.content);
        if (screenSetting.equals("1")) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (root != null) {
                root.setKeepScreenOn(true);
            }
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (root != null) {
                root.setKeepScreenOn(false);
            }
        }
    }

    public boolean toggleActionBar() {
        boolean hidden = false;
        ActionBar actionBar= (ActionBar) this.getSupportActionBar();
        if (actionBar.isShowing()) {
            actionBar.hide();
            hidden = true;
        } else {
            actionBar.show();
        }
        return hidden;
    }
}
