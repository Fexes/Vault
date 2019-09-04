package com.example.encrypt.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.example.encrypt.R;
import com.example.encrypt.lock.LockType;
import com.example.encrypt.lock.PasscodeActivity;
import com.example.encrypt.lock.PatternActivity;
import com.example.encrypt.lock.utils.AppPreferences;
import com.example.encrypt.vault.PrivatePhotoFragment;
import com.example.encrypt.vault.PrivateVideoFragment;
import com.example.encrypt.vault.SectionsPageAdapter;

public class MainActivity extends AppCompatActivity {

     SectionsPageAdapter mSectionsPageAdapter;
     ViewPager mViewPager;
    private SystemKeyEventReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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


          Intent i = new Intent(getApplicationContext(), LockType.class);
          startActivity(i);
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
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        receiver = new SystemKeyEventReceiver();

        registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

     void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new PrivatePhotoFragment(), "Images");
        adapter.addFragment(new PrivateVideoFragment(), "Videos");
        viewPager.setAdapter(adapter);
    }

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1655);
        }
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


    private class SystemKeyEventReceiver extends BroadcastReceiver {
        private final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason == null) {
                    return;
                }

                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY) && BseApplication.sp.getBoolean("fastExit", true)) {
                    exitApp();
                }

                if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS) && BseApplication.sp.getBoolean("fastExit", true)) {
                    exitApp();
                }
            }
        }
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

}
