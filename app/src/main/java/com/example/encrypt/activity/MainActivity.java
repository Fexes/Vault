package com.example.encrypt.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.encrypt.R;
import com.example.encrypt.database.DatabaseAdapter;
import com.example.encrypt.lock.LockType;
import com.example.encrypt.lock.PasscodeActivity;
import com.example.encrypt.lock.PatternActivity;
import com.example.encrypt.lock.utils.AppPreferences;
import com.example.encrypt.onboarding.OnboardingActivity;
import com.example.encrypt.photo.ImageItem;
import com.example.encrypt.vault.PrivatePhotoFragment;
import com.example.encrypt.vault.PrivateVideoFragment;
import com.example.encrypt.vault.SectionsPageAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

     SectionsPageAdapter mSectionsPageAdapter;
     ViewPager mViewPager;
    public static ArrayList<ImageItem> dateList;
    private SystemKeyEventReceiver receiver;
    private static DatabaseAdapter databaseAdapter;
    String preffile = "com.example.encrypt";

    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.settings:
                startActivity(new Intent(MainActivity.this, AdvancedSetup.class));
                break;

            case R.id.button_back:
                finish();
                break;

            default:
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1655) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                exitApp();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs;
        prefs = getSharedPreferences(preffile, MODE_PRIVATE);

        //   Toast.makeText(getApplicationContext(),BseApplication.sp.getBoolean("isapprunning", false)+"",Toast.LENGTH_LONG).show();
      String  Pattern = AppPreferences.getPattern(getApplicationContext());
      String Passcode = AppPreferences.getPasscode(getApplicationContext());


        if(Pattern!=null||Passcode!=null) {

          if (AppPreferences.getPasscodeType(getApplicationContext()).equalsIgnoreCase("0")) {
              Intent i = new Intent(getApplicationContext(), PasscodeActivity.class);
              startActivity(i);
          } else {
              Intent i = new Intent(getApplicationContext(), PatternActivity.class);
              startActivity(i);
          }
      }else{
            if (prefs.getBoolean("firstrun", true)) {
                Intent i = new Intent(getApplicationContext(), OnboardingActivity.class);
                startActivity(i);
                finish();
            } else {
                Intent i = new Intent(getApplicationContext(), LockType.class);
                startActivity(i);
                finish();
            }
      }
/*
  if (AppPreferences.getPasscodeType(getActivity().getApplicationContext()).equalsIgnoreCase("0")) {
                    Intent mIntentSettings = new Intent(getActivity(), PasscodeActivity.class);
                    mIntentSettings.putExtra("ChangePasscode", true);
                    startActivity(mIntentSettings);

                } else {
                    Intent mIntentSettings = new Intent(getActivity(), PatternActivity.class);
                    mIntentSettings.putExtra("ChangePattern", true);
                    startActivity(mIntentSettings);

                }
*/

    //    Notifi.message(this,"yolo",false);

        setContentView(R.layout.activity_main);
        addAppActivity(MainActivity.this);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        receiver = new SystemKeyEventReceiver();

        registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));


    }


    public static void addAppActivity(Activity activity) {
        if (!BaseActivity.activityList.contains(activity)) {
            BaseActivity.activityList.add(activity);
        }
    }

    public void exitApp() {
        for (Activity ac : BaseActivity.activityList) {
            if (!ac.isFinishing()) {
                ac.finish();
            }
        }
        BaseActivity.activityList.clear();

        android.os.Process.killProcess(android.os.Process.myPid());
    }

    void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new PrivatePhotoFragment(), "Images");
        adapter.addFragment(new PrivateVideoFragment(), "Videos");
        viewPager.setAdapter(adapter);
    }

    private class SystemKeyEventReceiver extends BroadcastReceiver {
        private final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                // BseApplication.editor.putBoolean("isapprunning", false);
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason == null) {
                    return;
                }

                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY) && BseApplication.sp.getBoolean("fastExit", false)) {
                    exitApp();
                }

                if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS) && BseApplication.sp.getBoolean("fastExit", false)) {
                    exitApp();
                }
            }
        }
    }


}
