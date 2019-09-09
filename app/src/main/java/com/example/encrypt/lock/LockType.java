package com.example.encrypt.lock;

import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.encrypt.R;
import com.example.encrypt.activity.BaseActivity;
import com.example.encrypt.activity.BseApplication;
import com.pro100svitlo.fingerprintAuthHelper.FahErrorType;
import com.pro100svitlo.fingerprintAuthHelper.FahListener;
import com.pro100svitlo.fingerprintAuthHelper.FingerprintAuthHelper;

public class LockType extends BaseActivity {
//new 2
private FingerprintAuthHelper mFAH;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_type);

        Switch mSwitch1 = findViewById(R.id.switch1);

        mFAH = new FingerprintAuthHelper
                .Builder(this, new FahListener() {
            @Override
            public void onFingerprintStatus(boolean authSuccessful, int errorType, CharSequence errorMess) {
                if (mFAH != null) {


                    switch (errorType) {
                        case FahErrorType.General.LOCK_SCREEN_DISABLED:
                        case FahErrorType.General.NO_FINGERPRINTS:


                            break;
                        case FahErrorType.Auth.AUTH_NOT_RECOGNIZED:

                            break;
                        case FahErrorType.Auth.AUTH_TO_MANY_TRIES:
                            //do some stuff here
                            break;
                    }
                }
            }

            @Override
            public void onFingerprintListening(boolean listening, long milliseconds) {

            }
        })
                .build();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
            if (!fingerprintManager.isHardwareDetected()) {
                LinearLayout rv1 = findViewById(R.id.rv1);
                rv1.setVisibility(View.GONE);
                TextView textView = findViewById(R.id.textView);
                textView.setVisibility(View.GONE);
                RelativeLayout frl = findViewById(R.id.frl);
                frl.setVisibility(View.GONE);
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                mFAH.showSecuritySettingsDialog();
            } else {
                LinearLayout rv1 = findViewById(R.id.rv1);
                rv1.setVisibility(View.VISIBLE);
                TextView textView = findViewById(R.id.textView);
                textView.setVisibility(View.VISIBLE);
                RelativeLayout frl = findViewById(R.id.frl);
                frl.setVisibility(View.VISIBLE);
            }
        }


        mSwitch1.setChecked(BseApplication.sp.getBoolean("fingerprint", true));

        mSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                BseApplication.editor.putBoolean("fingerprint", b);
            }
        });

    }

    public void pin(View view) {

        Intent mIntentSettings = new Intent(LockType.this, PasscodeActivity.class);
        mIntentSettings.putExtra("ChangePasscode", true);
        startActivity(mIntentSettings);
        finish();
    }

    public void pattern(View view) {
        Intent mIntentSettings = new Intent(LockType.this, PatternActivity.class);
        mIntentSettings.putExtra("ChangePattern", true);
        startActivity(mIntentSettings);
        finish();
    }
}
