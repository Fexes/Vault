package com.example.encrypt.util;

import android.app.Activity;
import android.graphics.Color;

import com.example.encrypt.R;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import android.widget.TextView;

public class Notifi {

    public static void message(Activity activity, String message, boolean error){

        Snackbar snackbar;
        snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();

        if(error){
            //Red
            snackBarView.setBackgroundColor(Color.parseColor("#558B2F"));
            TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
        }else{
            //Green
            snackBarView.setBackgroundColor(Color.parseColor("#BF360C"));
            TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
        }
        snackbar.show();

    }
}
