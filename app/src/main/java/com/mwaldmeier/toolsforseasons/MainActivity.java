package com.mwaldmeier.toolsforseasons;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Seasons ThisGame;
    SoundPool sp;
    //settings variables
    final String SOUND_ON = "SOUND_ON";
    final String SCREEN_ALWAYS_ON = "SCREEN_ALWAYS_ON";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThisGame = ((Seasons) this.getApplication());

        ThisGame.setUpNewGame(4);

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        setActiveFragment(new ScoreFragment());
    }

    private void setUpSettings() {
        if (getSoundOn() == null) {
            setSetting(SOUND_ON, "1");
        }
        if (getScreenAlwaysOn() == null) {
            setSetting(SCREEN_ALWAYS_ON, "1");
        }
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
                        ThisGame.setUpNewGame((which+2));
                        setActiveFragment(new ScoreFragment());
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
        boolean selected[] = new boolean[2];
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
    }

    private void setScreenSetting(String screenSetting) {
        if (screenSetting.equals("1")) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
