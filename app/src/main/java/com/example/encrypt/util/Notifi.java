package com.example.encrypt.util;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.example.encrypt.R;
import com.google.android.material.snackbar.Snackbar;

public class Notifi {

    public static void message(Activity activity, String message, boolean error){

        Snackbar snackbar;
        snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();

        if(error){
            //Green

            snackBarView.setBackgroundColor(Color.parseColor("#64DD17"));
            TextView textView = snackBarView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
        }else{
            //Red
            snackBarView.setBackgroundColor(Color.parseColor("#FF3D00"));
            TextView textView = snackBarView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
        }
        snackbar.show();

    }
}
