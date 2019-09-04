package com.example.encrypt.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.example.encrypt.R;

/**
 * Created by ruipan.dong on 2017/9/27.
 */

public class UseHelp extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_help);
        addAppActivity(UseHelp.this);
    }
}
