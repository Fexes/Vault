package com.example.encrypt.onboarding;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.encrypt.R;
import com.example.encrypt.lock.LockType;
import com.example.encrypt.lock.utils.AppPreferences;


public class OnboardingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private OnboardingAdapter onboardingAdapter;
    SharedPreferences prefs = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Bundle extras;

        boolean explore = false;


//fetching extra data passed with intents in a Bundle type variable

        extras = getIntent().getExtras();
        if (extras != null) {
            explore = extras.getBoolean("explore", false);
        }
        makeStatusbarTransparent();
        String Pattern = AppPreferences.getPattern(getApplicationContext());
        String Passcode = AppPreferences.getPasscode(getApplicationContext());
        prefs = getSharedPreferences("com.example.encrypt", MODE_PRIVATE);
        if (!explore) {

            if (!prefs.getBoolean("firstrun", true)) {
                if (Pattern == null && Passcode == null) {
                    Intent i = new Intent(getApplicationContext(), LockType.class);
                    startActivity(i);
                    finish();
                }

            }
        }
        viewPager = findViewById(R.id.onboarding_view_pager);
        onboardingAdapter = new OnboardingAdapter(this);
        viewPager.setAdapter(onboardingAdapter);
        viewPager.setPageTransformer(false, new OnboardingPageTransformer());

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            Button button2 = findViewById(R.id.button2);
            @Override
            public void onPageSelected(int position) {
                if (position == 4) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(OnboardingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1655);
                    } else {

                        button2.setText("Accept");
                        button2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                prefs.edit().putBoolean("firstrun", false).apply();

                                String Pattern = AppPreferences.getPattern(getApplicationContext());
                                String Passcode = AppPreferences.getPasscode(getApplicationContext());

                                if (Pattern == null && Passcode == null) {
                                    Intent i = new Intent(getApplicationContext(), LockType.class);
                                    startActivity(i);

                                }
                                finish();
                            }
                        });
                    }
                } else {
                    button2.setText("Next");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1655) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish();
            } else {
                Button button2 = findViewById(R.id.button2);
                button2.setText("Accept");
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prefs.edit().putBoolean("firstrun", false).apply();

                        String Pattern = AppPreferences.getPattern(getApplicationContext());
                        String Passcode = AppPreferences.getPasscode(getApplicationContext());

                        if (Pattern == null && Passcode == null) {
                            Intent i = new Intent(getApplicationContext(), LockType.class);
                            startActivity(i);

                        }
                        finish();
                    }
                });
            }
        }
    }


    public void nextPage(View view) {
        if (view.getId() == R.id.button2) {
            if (viewPager.getCurrentItem() < onboardingAdapter.getCount() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);

            }
        }


    }

    private void makeStatusbarTransparent() {

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
