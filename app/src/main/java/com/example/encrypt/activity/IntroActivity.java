package com.example.encrypt.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.encrypt.R;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

public class IntroActivity extends MaterialIntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.black)
                        .buttonsColor(R.color.colorAccent)

                     //   .possiblePermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_SMS})
                     //   .neededPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
                        .image(R.drawable.checkbox)
                        .title("title 3")

                         .description("Description 3")
                        .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMessage("We provide solutions to make you love your work");
                    }
                }, "Work with love"));


    }
}
