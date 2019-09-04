package com.example.encrypt.lock;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.encrypt.R;
import com.example.encrypt.activity.BseApplication;
import com.example.encrypt.lock.utils.AppPreferences;
import com.example.encrypt.util.Notifi;
import com.pro100svitlo.fingerprintAuthHelper.FahErrorType;
import com.pro100svitlo.fingerprintAuthHelper.FahListener;
import com.pro100svitlo.fingerprintAuthHelper.FingerprintAuthHelper;

public class PasscodeActivity extends Activity {
    private static final String TAG = "AndroidCameraApi";
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    TextView TxtPasscode1, TxtPasscode2, TxtPasscode3, TxtPasscode4,
            TxtPasscode5, TxtPasscode6, TxtPasscode7, TxtPasscode8,
            TxtPasscode9, TxtPasscode0, TxtEnterPasscode, TxtForgetPasscode;
    RelativeLayout BtnPasscode1, BtnPasscode2, BtnPasscode3, BtnPasscode4,
            BtnPasscode5, BtnPasscode6, BtnPasscode7, BtnPasscode8,
            BtnPasscode9, BtnPasscode0;
    Typeface UnlockFontThin, UnlockMedium, UnlockLight;
    int attempt=0;
    ImageView PassDot1, PassDot2, PassDot3, PassDot4 ,FingerPrintImage,LockAppIcon;

    String Passcode, EnterPasscode, Passcode1;

    Boolean vibrate = false;
    Boolean mChangePasscode = false;
    Boolean mForget = false;


    private FingerprintAuthHelper mFAH;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_passcode);

        Intent init= getIntent();
        Bundle bundle = init.getExtras();
        LockAppIcon=(ImageView)findViewById(R.id.LockAppIcon);
        if(bundle!=null)
        {
            String mpackage =(String) bundle.get("package");


            try
            {
                Drawable icon = getPackageManager().getApplicationIcon(mpackage);
                LockAppIcon.setImageDrawable(icon);
            }
            catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }
        }




        UnlockFontThin = Typeface.createFromAsset(getAssets(),
                "Roboto-Thin.ttf");
        UnlockMedium = Typeface.createFromAsset(getAssets(),
                "Roboto-Medium.ttf");
        UnlockLight = Typeface.createFromAsset(getAssets(),
                "Roboto-Light.ttf");

        Passcode = AppPreferences.getPasscode(getApplicationContext());
        EnterPasscode = "";

        mChangePasscode = this.getIntent().hasExtra("ChangePasscode");
        mForget = this.getIntent().hasExtra("Forget");

        if (mChangePasscode) {
            Passcode = null;
        }
        FingerPrintImage = (ImageView) findViewById(R.id.fingerfrint_image);


        BtnPasscode1 = (RelativeLayout) findViewById(R.id.BtnPasscode1);
        BtnPasscode2 = (RelativeLayout) findViewById(R.id.BtnPasscode2);
        BtnPasscode3 = (RelativeLayout) findViewById(R.id.BtnPasscode3);
        BtnPasscode4 = (RelativeLayout) findViewById(R.id.BtnPasscode4);
        BtnPasscode5 = (RelativeLayout) findViewById(R.id.BtnPasscode5);
        BtnPasscode6 = (RelativeLayout) findViewById(R.id.BtnPasscode6);
        BtnPasscode7 = (RelativeLayout) findViewById(R.id.BtnPasscode7);
        BtnPasscode8 = (RelativeLayout) findViewById(R.id.BtnPasscode8);
        BtnPasscode9 = (RelativeLayout) findViewById(R.id.BtnPasscode9);
        BtnPasscode0 = (RelativeLayout) findViewById(R.id.BtnPasscode0);

        TxtPasscode1 = (TextView) findViewById(R.id.TxtPasscode1);
        TxtPasscode2 = (TextView) findViewById(R.id.TxtPasscode2);
        TxtPasscode3 = (TextView) findViewById(R.id.TxtPasscode3);
        TxtPasscode4 = (TextView) findViewById(R.id.TxtPasscode4);
        TxtPasscode5 = (TextView) findViewById(R.id.TxtPasscode5);
        TxtPasscode6 = (TextView) findViewById(R.id.TxtPasscode6);
        TxtPasscode7 = (TextView) findViewById(R.id.TxtPasscode7);
        TxtPasscode8 = (TextView) findViewById(R.id.TxtPasscode8);
        TxtPasscode9 = (TextView) findViewById(R.id.TxtPasscode9);
        TxtPasscode0 = (TextView) findViewById(R.id.TxtPasscode0);

        TxtForgetPasscode = (TextView) findViewById(R.id.TxtForgetPasscode);

        TxtEnterPasscode = (TextView) findViewById(R.id.TxtEnterPasscode);

        if (mForget || mChangePasscode || Passcode==null ) {
            mFAH=null;
            FingerPrintImage.setVisibility(View.INVISIBLE);
            TxtForgetPasscode.setVisibility(View.INVISIBLE);

        }else{
            if(!BseApplication.sp.getBoolean("fingerprint", false)){
                FingerPrintImage.setVisibility(View.INVISIBLE);
                mFAH=null;
            }else {
                mFAH = new FingerprintAuthHelper
                        .Builder(this, new FahListener() {
                    @Override
                    public void onFingerprintStatus(boolean authSuccessful, int errorType, CharSequence errorMess) {
                        // authSuccessful - boolean that shows auth status
                        // errorType - if auth was failed, you can catch error type
                        // errorMess - if auth was failed, errorMess will tell you (and user) the reason

                        if (authSuccessful) {
                            // do some stuff here in case auth was successful
                            // Toast.makeText(getBaseContext(), "Success", Toast.LENGTH_SHORT).show();


                            finish();


                            applyGreenThemeToDrawable(FingerPrintImage.getDrawable());

                            //   lockStatus.setText("Success! Decrypting data..");

                            new CountDownTimer(1200, 1000) {

                                @Override
                                public void onTick(long l) {

                                }

                                @Override
                                public void onFinish() {
                                    //  Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                                    // startActivity(intent);
                                    // finish();
                                    mFAH.startListening();
                                }
                            }.start();

                        } else if (mFAH != null) {
                            // do some stuff here in case auth failed
                            switch (errorType) {
                                case FahErrorType.General.LOCK_SCREEN_DISABLED:
                                case FahErrorType.General.NO_FINGERPRINTS:
                                    mFAH.showSecuritySettingsDialog();
                                    break;
                                case FahErrorType.Auth.AUTH_NOT_RECOGNIZED:
                                    //do some stuff here
                                    applyRedThemeToDrawable(FingerPrintImage.getDrawable());


                                    Notifi.message(PasscodeActivity.this, "Fingerprint dose not match.", false);
                                    Animation shake = AnimationUtils.loadAnimation(
                                            getApplicationContext(), R.anim.shake);
                                    findViewById(R.id.LayoutPasscode).startAnimation(shake);

                                    shake.setAnimationListener(new Animation.AnimationListener() {

                                        @Override
                                        public void onAnimationStart(Animation animation) {
                                            // TODO Auto-generated method stub
                                            EnterPasscode = "";
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {
                                            // TODO Auto-generated method stub

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            // TODO Auto-generated method stub

                                            PassDot1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                                            PassDot2.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                                            PassDot3.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                                            PassDot4.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                                        }
                                    });


                                    //     lockStatus.setText("Fingerprint not recognized. Try again!");

                                    break;
                                case FahErrorType.Auth.AUTH_TO_MANY_TRIES:
                                    //do some stuff here
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFingerprintListening(boolean listening, long milliseconds) {
                        // listening - status of fingerprint listen process
                        // milliseconds - timeout value, will be > 0, if listening = false & errorType = AUTH_TO_MANY_TRIES

                        if (listening) {
                            //add some code here
                        } else {
                            //add some code here
                        }
                        if (milliseconds > 0) {
                            //if u need, u can show timeout for user
                            //   lockStatus.setText("Too many attempts! Please wait for " + (milliseconds / 1000) + " seconds");
                        } else if (milliseconds == 0) {
                            // lockStatus.setText("Scan your finger to unlock");
                        }
                    }
                }) //(Context inscance of Activity, FahListener)
                        .build();
            }


        }










        PassDot1 = (ImageView) findViewById(R.id.PassDot1);
        PassDot2 = (ImageView) findViewById(R.id.PassDot2);
        PassDot3 = (ImageView) findViewById(R.id.PassDot3);
        PassDot4 = (ImageView) findViewById(R.id.PassDot4);

        TxtPasscode1.setTypeface(UnlockFontThin);
        TxtPasscode2.setTypeface(UnlockFontThin);
        TxtPasscode3.setTypeface(UnlockFontThin);
        TxtPasscode4.setTypeface(UnlockFontThin);
        TxtPasscode5.setTypeface(UnlockFontThin);
        TxtPasscode6.setTypeface(UnlockFontThin);
        TxtPasscode7.setTypeface(UnlockFontThin);
        TxtPasscode8.setTypeface(UnlockFontThin);
        TxtPasscode9.setTypeface(UnlockFontThin);
        TxtPasscode0.setTypeface(UnlockFontThin);
        TxtEnterPasscode.setTypeface(UnlockLight);

        if (Passcode == null) {
            TxtEnterPasscode.setText("Enter Pincode");
        }


        TxtForgetPasscode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent mIntentSettings = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
            //    startActivity(mIntentSettings);
                finish();
            }
        });

        BtnPasscode1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EnterPasscode.length() != 4) {
                    EnterPasscode = EnterPasscode + "1";
                    PasscodeEntered(EnterPasscode);
                }

            }

        });

        BtnPasscode2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EnterPasscode.length() != 4) {
                    EnterPasscode = EnterPasscode + "2";
                    PasscodeEntered(EnterPasscode);
                }
            }
        });

        BtnPasscode3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EnterPasscode.length() != 4) {
                    EnterPasscode = EnterPasscode + "3";
                    PasscodeEntered(EnterPasscode);
                }
            }
        });

        BtnPasscode4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EnterPasscode.length() != 4) {
                    EnterPasscode = EnterPasscode + "4";
                    PasscodeEntered(EnterPasscode);
                }
            }
        });

        BtnPasscode5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EnterPasscode.length() != 4) {
                    EnterPasscode = EnterPasscode + "5";
                    PasscodeEntered(EnterPasscode);
                }
            }
        });

        BtnPasscode6.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EnterPasscode.length() != 4) {
                    EnterPasscode = EnterPasscode + "6";
                    PasscodeEntered(EnterPasscode);
                }
            }
        });

        BtnPasscode7.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EnterPasscode.length() != 4) {
                    EnterPasscode = EnterPasscode + "7";
                    PasscodeEntered(EnterPasscode);
                }
            }
        });

        BtnPasscode8.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EnterPasscode.length() != 4) {
                    EnterPasscode = EnterPasscode + "8";
                    PasscodeEntered(EnterPasscode);
                }
            }
        });

        BtnPasscode9.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EnterPasscode.length() != 4) {
                    EnterPasscode = EnterPasscode + "9";
                    PasscodeEntered(EnterPasscode);
                }
            }
        });

        BtnPasscode0.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EnterPasscode.length() != 4) {
                    EnterPasscode = EnterPasscode + "0";
                    PasscodeEntered(EnterPasscode);
                }
            }
        });
    }

    public void PasscodeEntered(final String EPass) {
        // TODO Auto-generated method stub

        int passdot = EPass.length();
        if (passdot == 1) {

            PassDot1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.pass_fill));
        } else if (passdot == 2) {
            PassDot1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.pass_fill));
            PassDot2.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.pass_fill));

        } else if (passdot == 3) {
            PassDot1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.pass_fill));
            PassDot2.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.pass_fill));
            PassDot3.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.pass_fill));

        } else if (passdot == 4) {
            PassDot1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.pass_fill));
            PassDot2.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.pass_fill));
            PassDot3.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.pass_fill));
            PassDot4.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.pass_fill));

        }
        if (EPass.length() >= 4) {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                    if (Passcode == null) {
                        if (Passcode1 == null) {
                            Passcode1 = EnterPasscode;
                            TxtEnterPasscode.setText("Confirm Pincode");
                            PassDot1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                            PassDot2.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                            PassDot3.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                            PassDot4.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                            EnterPasscode = "";
                        } else {
                            if (Passcode1.equalsIgnoreCase(EnterPasscode)) {
                                AppPreferences.setPasscode(getApplicationContext(), Passcode1);
                                EnterPasscode = "";
                                AppPreferences.setPattern(getApplicationContext(), null);
                                AppPreferences.setPasscodeType(getApplicationContext(), "0");
                                AppPreferences.setPasscodeSetup(getApplicationContext(), true);
                                if (mChangePasscode) {
                                    finish();
                                } else {
                                    finish();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Passcode dose not match.", Toast.LENGTH_SHORT).show();
                                Animation shake = AnimationUtils.loadAnimation(
                                        getApplicationContext(), R.anim.shake);
                                findViewById(R.id.LayoutPasscode).startAnimation(shake);

                                shake.setAnimationListener(new Animation.AnimationListener() {

                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        // TODO Auto-generated method stub
                                        EnterPasscode = "";
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        // TODO Auto-generated method stub

                                        PassDot1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                                        PassDot2.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                                        PassDot3.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                                        PassDot4.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                                    }
                                });
                            }
                        }
                    } else if (EnterPasscode.equalsIgnoreCase(Passcode)) {
                        EnterPasscode = "";
                        finish();
                    } else {
                        attempt++;
                        if(attempt==5){

                            attempt=0;
                        }

                        Animation shake = AnimationUtils.loadAnimation(
                                getApplicationContext(), R.anim.shake);
                        findViewById(R.id.LayoutPasscode).startAnimation(shake);

                        shake.setAnimationListener(new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {
                                // TODO Auto-generated method stub
                                EnterPasscode = "";
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                // TODO Auto-generated method stub

                                PassDot1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                                PassDot2.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                                PassDot3.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                                PassDot4.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                            }
                        });
                    }
                    /*if (EPass.equalsIgnoreCase(Passcode)) {
                        //if (getBoolSound(mContext)) {
                        //    play(mContext, R.raw.unlock);
                        //}
                       *//* mContext.stopService(new Intent(mContext,
                                LockService.class));*//*
                        //SavePreferences("LockState", "OPEN");
                    } else {

                        Animation shake = AnimationUtils.loadAnimation(
                                getApplicationContext(), R.anim.shake);
                        findViewById(R.id.LayoutPasscode).startAnimation(shake);
                        // txt_enter_password.setText("Try again");
                        // txt_delete.setText("Cancel");
                        *//*if (getBoolVibration(mContext)) {
                            Vibrator v = (Vibrator) mContext
                                    .getApplicationContext().getSystemService(
                                            Context.VIBRATOR_SERVICE);
                            // Vibrate for 500 milliseconds
                            v.vibrate(500);
                        }*//*
                        shake.setAnimationListener(new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {
                                // TODO Auto-generated method stub
                                EnterPasscode = "";
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                // TODO Auto-generated method stub

                                PassDot1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                                PassDot2.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                                PassDot3.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                                PassDot4.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pass_border));
                            }
                        });

                    }*/
                }
            }, 300);

        }
    }

    @Override
    public void onBackPressed() {
        if (mChangePasscode && !mForget) {
            finish();
        } else {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }

    public void applyRedThemeToDrawable(Drawable image) {
        if (image != null) {
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.RED,
                    PorterDuff.Mode.SRC_ATOP);

            image.setColorFilter(porterDuffColorFilter);
        }
    }

    public void applyGreenThemeToDrawable(Drawable image) {
        if (image != null) {
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.rgb(56,172,84),
                    PorterDuff.Mode.SRC_ATOP);

            image.setColorFilter(porterDuffColorFilter);
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        if(mFAH!=null){
            mFAH.stopListening();
    }}


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mFAH!=null){
            mFAH.onDestroy();
    }}




    @Override
    protected void onResume() {
        super.onResume();
        if(mFAH!=null) {
            mFAH.startListening();
        }
        Log.e(TAG, "onResume");

    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        //closeCamera();
         super.onPause();
    }
}
