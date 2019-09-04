package com.example.encrypt.lock;

import android.app.Activity;
import android.content.Intent;
 import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.encrypt.R;
import com.example.encrypt.activity.BaseActivity;
import com.example.encrypt.activity.BseApplication;

public class LockType extends BaseActivity {
//new 2
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_type);

        Switch mSwitch1 = findViewById(R.id.switch1);


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
