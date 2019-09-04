package com.example.encrypt.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.encrypt.BuildConfig;
import com.example.encrypt.R;
import com.example.encrypt.lock.LockType;
import com.example.encrypt.util.AlipayUtil;

import java.util.ArrayList;
import java.util.List;

import in.myinnos.library.AppIconNameChanger;


public class AdvancedSetup extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static FingerprintManager mFingerprintManager;
    RelativeLayout rv1;
    Switch mSwitch1, /*mSwitch2, mSwitch3,*/
            mSwitch4;

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

    @SuppressLint("NewApi")
    private void initView() {

        TextView appversion = findViewById(R.id.appversion);
        appversion.setText("Version "+BuildConfig.VERSION_NAME);

        rv1 = (RelativeLayout) findViewById(R.id.rv1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mFingerprintManager.isHardwareDetected()) {
            rv1.setVisibility(View.VISIBLE);
        }
        mSwitch1 = (Switch) findViewById(R.id.switch1);


        mSwitch4 = (Switch) findViewById(R.id.switch4);

        mSwitch1.setChecked(BseApplication.sp.getBoolean("fingerprint", true));


        mSwitch4.setChecked(BseApplication.sp.getBoolean("fastExit", false));

        mSwitch1.setOnCheckedChangeListener(this);


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
        }
        BseApplication.editor.commit();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rv1:
                mSwitch1.toggle();
                break;
            case R.id.rv11:
                String activeName = "in.myinnos.changeappiconandname.MainActivitySettings";

// Disable alias names
                List<String> disableNames = new ArrayList<String>();

                disableNames.add("com.example.encrypt.activity.MainActivity");

// Initiate App Icon Name Changer
                new AppIconNameChanger.Builder(AdvancedSetup.this)
                        .activeName(activeName) // String
                        .disableNames(disableNames) // List<String>
                        .packageName(BuildConfig.APPLICATION_ID)
                        .build()
                        .setNow();

                break;
            case R.id.rv4:
                mSwitch4.toggle();
                break;
            case R.id.rv5:
                 startActivity(new Intent(AdvancedSetup.this, LockType.class));
                break;
            case R.id.rv6:
               // startActivity(new Intent(AdvancedSetup.this, SecurityQuestion.class));
                break;
            case R.id.rv7:
                startActivity(new Intent(AdvancedSetup.this, UseHelp.class));
                break;
            case R.id.rv8:
                if (AlipayUtil.hasInstalledAlipayClient(AdvancedSetup.this)) {

                    AlipayUtil.startAlipayClient(AdvancedSetup.this, "FKX08718ZXKVQ3OY5FFH09");
                } else {
                 //   Toast.makeText(AdvancedSetup.this, R.string.not_install_alipay, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

}
