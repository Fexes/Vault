package com.example.encrypt.lock.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {



    public static void setPasscode(Context context, String Passcode) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConstant.APP_PREFERENCE,
                Context.MODE_PRIVATE).edit();
        editor.putString("Passcode", Passcode);
        editor.apply();
    }

    public static String getPasscode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstant.APP_PREFERENCE,
                Context.MODE_PRIVATE);
        return preferences.getString("Passcode", null);
    }

    public static String getPasscodeType(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstant.APP_PREFERENCE,
                Context.MODE_PRIVATE);
        return preferences.getString("PasscodeType", "0");
    }

    public static void setPasscodeType(Context context, String PasscodeType) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConstant.APP_PREFERENCE,
                Context.MODE_PRIVATE).edit();
        editor.putString("PasscodeType", PasscodeType);
        editor.apply();
    }

    public static void setPattern(Context context, String Pattern) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConstant.APP_PREFERENCE,
                Context.MODE_PRIVATE).edit();
        editor.putString("Pattern", Pattern);
        editor.apply();
    }

    public static String getPattern(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstant.APP_PREFERENCE,
                Context.MODE_PRIVATE);
        return preferences.getString("Pattern", null);
    }



    public static void setPasscodeSetup(Context context, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConstant.APP_PREFERENCE,
                Context.MODE_PRIVATE).edit();
        editor.putBoolean("PasscodeSetup", value);
        editor.apply();
    }


    public static String getSecurityAnswer(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstant.APP_PREFERENCE,
                Context.MODE_PRIVATE);
        return preferences.getString("SecurityAnswer", null);
    }

    public static void setSecurityAnswer(Context context, String SecurityAnswer) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppConstant.APP_PREFERENCE,
                Context.MODE_PRIVATE).edit();
        editor.putString("SecurityAnswer", SecurityAnswer);
        editor.apply();
    }

    public static String getSecurityQuestion(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstant.APP_PREFERENCE,
                Context.MODE_PRIVATE);
        return preferences.getString("SecurityQuestion", null);
    }


}
