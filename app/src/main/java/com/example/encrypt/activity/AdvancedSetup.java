package com.example.encrypt.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.encrypt.BuildConfig;
import com.example.encrypt.R;
import com.example.encrypt.lock.LockType;
import com.example.encrypt.lock.utils.AppPreferences;
import com.example.encrypt.onboarding.OnboardingActivity;
import com.example.encrypt.util.ChangeIconDialogue;


public class AdvancedSetup extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static FingerprintManager mFingerprintManager;
    RelativeLayout rv1, rv44;
    Switch mSwitch1, mSwitch4, mSwitch44;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_setup);


        addAppActivity(AdvancedSetup.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
        }
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mFingerprintManager = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        String Pattern = AppPreferences.getPattern(getApplicationContext());
        if (Pattern != null) {

            if (AppPreferences.getPasscodeType(getApplicationContext()).equalsIgnoreCase("0")) {
                rv44.setVisibility(View.GONE);
            } else {
                rv44.setVisibility(View.VISIBLE);
            }
        } else {
            rv44.setVisibility(View.GONE);
        }

    }

    @SuppressLint("NewApi")
    private void initView() {

        TextView appversion = findViewById(R.id.appversion);
        appversion.setText("Version "+BuildConfig.VERSION_NAME);

        rv1 = findViewById(R.id.rv1);
        rv44 = findViewById(R.id.rv44);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mFingerprintManager.isHardwareDetected()) {
            rv1.setVisibility(View.VISIBLE);
        }
        mSwitch1 = findViewById(R.id.switch1);
        String Pattern = AppPreferences.getPattern(getApplicationContext());
        if (Pattern != null) {

            if (AppPreferences.getPasscodeType(getApplicationContext()).equalsIgnoreCase("0")) {
                rv44.setVisibility(View.GONE);
            } else {
                rv44.setVisibility(View.VISIBLE);
            }
        }

        mSwitch4 = findViewById(R.id.switch4);
        mSwitch44 = findViewById(R.id.switch44);
        mSwitch1.setChecked(BseApplication.sp.getBoolean("fingerprint", true));


        mSwitch44.setChecked(BseApplication.sp.getBoolean("hidepattern", false));

        mSwitch4.setChecked(BseApplication.sp.getBoolean("fastExit", false));

        mSwitch1.setOnCheckedChangeListener(this);
        mSwitch44.setOnCheckedChangeListener(this);
        mSwitch4.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch1:
                BseApplication.editor.putBoolean("fingerprint", isChecked);
                break;

            case R.id.switch4:
                BseApplication.editor.putBoolean("fastExit", isChecked);
                break;

            case R.id.switch44:
                BseApplication.editor.putBoolean("hidepattern", isChecked);
                break;
        }
        BseApplication.editor.commit();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rv1:
                mSwitch1.toggle();
                break;

            case R.id.rv4:
                mSwitch4.toggle();
                break;
            case R.id.rv44:
                mSwitch44.toggle();
                break;
            case R.id.rv5:
                 startActivity(new Intent(AdvancedSetup.this, LockType.class));
                break;
            case R.id.rv19:
                Intent init = new Intent(AdvancedSetup.this, OnboardingActivity.class);
                init.putExtra("explore", true);
                startActivity(init);
                break;
            case R.id.rv11:
               // startActivity(new Intent(AdvancedSetup.this, SecurityQuestion.class));
                ChangeIconDialogue cdd = new ChangeIconDialogue(AdvancedSetup.this);
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
                break;
            case R.id.rv7:
                startActivity(new Intent(AdvancedSetup.this, UseHelp.class));
                break;

        }
    }

}
