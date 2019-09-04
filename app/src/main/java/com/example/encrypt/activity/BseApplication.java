package com.example.encrypt.activity;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by ruipan.dong on 2018/3/22.
 */

public class BseApplication extends Application {
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    public static final String PRIVATE_SPACE_SP = "share_preference";

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(PRIVATE_SPACE_SP, MODE_PRIVATE);
        editor = sp.edit();
    }

}
