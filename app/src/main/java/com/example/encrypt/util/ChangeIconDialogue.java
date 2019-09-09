package com.example.encrypt.util;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.example.encrypt.BuildConfig;
import com.example.encrypt.R;

import java.util.ArrayList;
import java.util.List;

public class ChangeIconDialogue extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;

    public ImageView note, finance, calculator;
    Button restore;
    String activeName;
    List<String> disableNames = new ArrayList<String>();

    public ChangeIconDialogue(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.change_icon);
        note = findViewById(R.id.note);
        finance = findViewById(R.id.finance);
        calculator = findViewById(R.id.calculator);
        restore = findViewById(R.id.restore);


        note.setOnClickListener(this);
        finance.setOnClickListener(this);
        calculator.setOnClickListener(this);
        restore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        disableNames = new ArrayList<String>();
        switch (v.getId()) {
            case R.id.restore:

                activeName = "com.example.encrypt.DefaultActivity";
                disableNames.add("com.example.encrypt.CoverNoteActivity");
                disableNames.add("com.example.encrypt.CoverCalculatorActivity");
                disableNames.add("com.example.encrypt.CoverFinanceActivity");
                setAppIcon(activeName, disableNames);

                break;

            case R.id.note:

                activeName = "com.example.encrypt.CoverNoteActivity";
                disableNames.add("com.example.encrypt.DefaultActivity");
                disableNames.add("com.example.encrypt.CoverCalculatorActivity");
                disableNames.add("com.example.encrypt.CoverFinanceActivity");
                setAppIcon(activeName, disableNames);

                break;
            case R.id.finance:

                activeName = "com.example.encrypt.CoverFinanceActivity";
                disableNames.add("com.example.encrypt.DefaultActivity");
                disableNames.add("com.example.encrypt.CoverCalculatorActivity");
                disableNames.add("com.example.encrypt.CoverNoteActivity");
                setAppIcon(activeName, disableNames);
                break;
            case R.id.calculator:

                activeName = "com.example.encrypt.CoverCalculatorActivity";
                disableNames.add("com.example.encrypt.DefaultActivity");
                disableNames.add("com.example.encrypt.CoverNoteActivity");
                disableNames.add("com.example.encrypt.CoverFinanceActivity");
                setAppIcon(activeName, disableNames);
                break;

        }
        dismiss();
    }

    public void setAppIcon(String activeName, List<String> disableNames) {

        new AppIconNameChanger.Builder(c)
                .activeName(activeName) // String
                .disableNames(disableNames) // List<String>
                .packageName(BuildConfig.APPLICATION_ID)
                .build()
                .setNow();

        dismiss();
        Notifi.message(c, "App Icon Changed", true);
    }
}