package com.example.encrypt.lock;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.example.encrypt.R;
import com.example.encrypt.activity.BseApplication;
import com.example.encrypt.lock.utils.AppPreferences;
import com.pro100svitlo.fingerprintAuthHelper.FahErrorType;
import com.pro100svitlo.fingerprintAuthHelper.FahListener;
import com.pro100svitlo.fingerprintAuthHelper.FingerprintAuthHelper;

import java.util.List;

public class PatternActivity extends Activity {

    int tries = 0;
    String Pattern, CurrentPattern;
    TextView TxtEnterPasscode;
    PatternLockView mPatternLockView;
    Boolean mChangePattern = false;
    Boolean mForget = false;
    ImageView FingerPrintImage, LockAppIcon;
    private FingerprintAuthHelper mFAH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern);


        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        Intent init = getIntent();
        Bundle bundle = init.getExtras();
        LockAppIcon = findViewById(R.id.LockAppIcon);
        if (bundle != null) {
            String mpackage = (String) bundle.get("package");


            try {
                Drawable icon = getPackageManager().getApplicationIcon(mpackage);
                LockAppIcon.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }


        mPatternLockView = findViewById(R.id.pattern_lock_view);
        if ((BseApplication.sp.getBoolean("hidepattern", false))) {
            mPatternLockView.setInStealthMode(true);
        } else {
            mPatternLockView.setInStealthMode(false);
        }


        TxtEnterPasscode = findViewById(R.id.TxtEnterPasscode);

        FingerPrintImage = findViewById(R.id.fingerfrint_image);
        Pattern = AppPreferences.getPattern(getApplicationContext());

        mChangePattern = this.getIntent().hasExtra("ChangePattern");
        mForget = this.getIntent().hasExtra("Forget");

        if (mChangePattern) {
            Pattern = null;
        }
        if(!BseApplication.sp.getBoolean("fingerprint", true)){
            FingerPrintImage.setVisibility(View.INVISIBLE);
            mFAH=null;
        }
        if (mForget || mChangePattern || Pattern == null ) {
            mFAH=null;
            FingerPrintImage.setVisibility(View.INVISIBLE);

        }else{
            if(!BseApplication.sp.getBoolean("fingerprint", true)){
                FingerPrintImage.setVisibility(View.INVISIBLE);
                mFAH=null;
            }else {
                mFAH = new FingerprintAuthHelper
                        .Builder(this, new FahListener() {
                    @Override
                    public void onFingerprintStatus(boolean authSuccessful, int errorType, CharSequence errorMess) {
                        // authSuccessful - boolean that shows auth status
                        // errorType - if auth was failed, you can catch error type
                        // errorMess - if auth was failed, errorMess will tell you (and user) the reason

                        if (authSuccessful) {
                            // do some stuff here in case auth was successful
                            // Toast.makeText(getBaseContext(), "Success", Toast.LENGTH_SHORT).show();


                            finish();


                            applyGreenThemeToDrawable(FingerPrintImage.getDrawable());

                            //   lockStatus.setText("Success! Decrypting data..");

                            new CountDownTimer(1200, 1000) {

                                @Override
                                public void onTick(long l) {

                                }

                                @Override
                                public void onFinish() {
                                    //  Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                                    // startActivity(intent);
                                    // finish();
                                    mFAH.startListening();
                                }
                            }.start();

                        } else if (mFAH != null) {
                            // do some stuff here in case auth failed
                            switch (errorType) {
                                case FahErrorType.General.LOCK_SCREEN_DISABLED:
                                case FahErrorType.General.NO_FINGERPRINTS:
                                    FingerPrintImage.setVisibility(View.INVISIBLE);
                                    mFAH.showSecuritySettingsDialog();
                                    break;
                                case FahErrorType.Auth.AUTH_NOT_RECOGNIZED:
                                    //do some stuff here
                                    applyRedThemeToDrawable(FingerPrintImage.getDrawable());


                                    //     lockStatus.setText("Fingerprint not recognized. Try again!");

                                    break;
                                case FahErrorType.Auth.AUTH_TO_MANY_TRIES:
                                    //do some stuff here
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFingerprintListening(boolean listening, long milliseconds) {
                        // listening - status of fingerprint listen process
                        // milliseconds - timeout value, will be > 0, if listening = false & errorType = AUTH_TO_MANY_TRIES

                        if (listening) {
                            //add some code here
                        } else {
                            //add some code here
                        }
                        if (milliseconds > 0) {
                            //if u need, u can show timeout for user
                            //   lockStatus.setText("Too many attempts! Please wait for " + (milliseconds / 1000) + " seconds");
                        } else if (milliseconds == 0) {
                            // lockStatus.setText("Scan your finger to unlock");
                        }
                    }
                }) //(Context inscance of Activity, FahListener)
                        .build();
            }
        }


        mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(final List<PatternLockView.Dot> pattern) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        CurrentPattern = PatternLockUtils.patternToString(mPatternLockView, pattern);
                        if (Pattern == null) {
                            Pattern = CurrentPattern;
                            TxtEnterPasscode.setText("Confirm Pattern");
                            mPatternLockView.clearPattern();
                        } else {
                            if (Pattern.equalsIgnoreCase(CurrentPattern)) {
                                Log.i("CurrentPattern", "" + CurrentPattern);
                                AppPreferences.setPattern(getApplicationContext(), CurrentPattern);
                                AppPreferences.setPasscode(getApplicationContext(), null);
                                AppPreferences.setPasscodeType(getApplicationContext(), "1");
                                AppPreferences.setPasscodeSetup(getApplicationContext(), true);
                                mPatternLockView.clearPattern();
                                if (mChangePattern) {
                                    finish();
                                } else {
                                    finish();
                                }
                            } else {

                                tries++;

                                if (tries >= 5) {
                                    Toast.makeText(getApplicationContext(), "5 wrong attempts ", Toast.LENGTH_SHORT).show();
                                    capture();
                                    tries = 0;
                                }


                                mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                            }
                        }
                    }
                }, 300);
            }

            @Override
            public void onCleared() {

            }
        });





    }



    public void applyRedThemeToDrawable(Drawable image) {
        if (image != null) {
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.RED,
                    PorterDuff.Mode.SRC_ATOP);

            image.setColorFilter(porterDuffColorFilter);
        }
    }

    public void applyGreenThemeToDrawable(Drawable image) {
        if (image != null) {
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.rgb(56,172,84),
                    PorterDuff.Mode.SRC_ATOP);

            image.setColorFilter(porterDuffColorFilter);
        }
    }

    @Override
    public void onBackPressed() {
        if (mChangePattern && !mForget) {
            finish();
        } else {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if(mFAH!=null){
             mFAH.startListening();
    }}



    @Override
    protected void onStop() {
        super.onStop();
        if(mFAH!=null){
             mFAH.stopListening();
    }}


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mFAH!=null){
             mFAH.onDestroy();
    }}


    private void capture() {
    }
}
