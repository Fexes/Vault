package com.example.encrypt.lock.view;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.encrypt.R;


public class PasscodeView extends RelativeLayout{

    private static ViewGroup mContainer;
    private static Context mContext;

    TextView TxtPasscode1, TxtPasscode2, TxtPasscode3, TxtPasscode4,
            TxtPasscode5, TxtPasscode6, TxtPasscode7, TxtPasscode8,
            TxtPasscode9, TxtPasscode0, TxtEnterPasscode;
    RelativeLayout BtnPasscode1, BtnPasscode2, BtnPasscode3, BtnPasscode4,
            BtnPasscode5, BtnPasscode6, BtnPasscode7, BtnPasscode8,
            BtnPasscode9, BtnPasscode0;
    Typeface UnlockFontThin, UnlockMedium, UnlockLight;

    ImageView PassDot1, PassDot2, PassDot3, PassDot4;

    String Passcode, EnterPasscode;

    Boolean vibrate = true;

    public PasscodeView(Context context) {
        super(context);
    }

    public PasscodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PasscodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PasscodeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public static PasscodeView fromXml(Context paramContext,
                                         ViewGroup paramViewGroup) {

        mContext = paramContext;
        mContainer = paramViewGroup;
        return (PasscodeView) LayoutInflater.from(paramContext).inflate(
                R.layout.passcode_layout, paramViewGroup, false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Passcode = "1234";//getpreferences("Passcode");
        EnterPasscode = "";
        //vibrate = true;
        /*if (getBoolVibration(mContext)) {


        }*/

        UnlockFontThin = Typeface.createFromAsset(mContext.getAssets(),
                "Roboto-Thin.ttf");
        UnlockMedium = Typeface.createFromAsset(mContext.getAssets(),
                "Roboto-Medium.ttf");
        UnlockLight = Typeface.createFromAsset(mContext.getAssets(),
                "Roboto-Light.ttf");

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

        TxtEnterPasscode = (TextView) findViewById(R.id.TxtEnterPasscode);

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

        BtnPasscode1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(EnterPasscode.length()!=4) {
                    EnterPasscode = EnterPasscode + "1";

                    PasscodeEntered(EnterPasscode);
                }
            }

        });

        BtnPasscode2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(EnterPasscode.length()!=4) {
                    EnterPasscode = EnterPasscode + "2";

                    PasscodeEntered(EnterPasscode);
                }
            }
        });

        BtnPasscode3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EnterPasscode.length() != 4) {
                    EnterPasscode = EnterPasscode + "3";

                    PasscodeEntered(EnterPasscode);
                }
            }
        });

        BtnPasscode4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EnterPasscode.length() != 4) {
                    EnterPasscode = EnterPasscode + "4";

                    PasscodeEntered(EnterPasscode);
                }
            }
        });

        BtnPasscode5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EnterPasscode.length() != 4) {
                    EnterPasscode = EnterPasscode + "5";

                    PasscodeEntered(EnterPasscode);
                }
            }
        });

        BtnPasscode6.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EnterPasscode.length() != 4) {
                    EnterPasscode = EnterPasscode + "6";

                    PasscodeEntered(EnterPasscode);
                }
            }
        });

        BtnPasscode7.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EnterPasscode.length() != 4) {
                    EnterPasscode = EnterPasscode + "7";

                    PasscodeEntered(EnterPasscode);
                }
            }
        });

        BtnPasscode8.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EnterPasscode.length() != 4) {
                    EnterPasscode = EnterPasscode + "8";

                    PasscodeEntered(EnterPasscode);
                }
            }
        });

        BtnPasscode9.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EnterPasscode.length() != 4) {
                    EnterPasscode = EnterPasscode + "9";

                    PasscodeEntered(EnterPasscode);
                }
            }
        });

        BtnPasscode0.setOnClickListener(new OnClickListener() {

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

            PassDot1.setImageDrawable(getResources().getDrawable(
                    R.drawable.pass_fill));
        } else if (passdot == 2) {
            PassDot1.setImageDrawable(getResources().getDrawable(
                    R.drawable.pass_fill));
            PassDot2.setImageDrawable(getResources().getDrawable(
                    R.drawable.pass_fill));

        } else if (passdot == 3) {
            PassDot1.setImageDrawable(getResources().getDrawable(
                    R.drawable.pass_fill));
            PassDot2.setImageDrawable(getResources().getDrawable(
                    R.drawable.pass_fill));
            PassDot3.setImageDrawable(getResources().getDrawable(
                    R.drawable.pass_fill));

        } else if (passdot == 4) {
            PassDot1.setImageDrawable(getResources().getDrawable(
                    R.drawable.pass_fill));
            PassDot2.setImageDrawable(getResources().getDrawable(
                    R.drawable.pass_fill));
            PassDot3.setImageDrawable(getResources().getDrawable(
                    R.drawable.pass_fill));
            PassDot4.setImageDrawable(getResources().getDrawable(
                    R.drawable.pass_fill));

        }
        if (EPass.length() >= 4) {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (EPass.equalsIgnoreCase(Passcode)) {
                        //if (getBoolSound(mContext)) {
                        //    play(mContext, R.raw.unlock);
                        //}

                        //SavePreferences("LockState", "OPEN");
                    } else {

                        Animation shake = AnimationUtils.loadAnimation(
                                mContext, R.anim.shake);
                        findViewById(R.id.LayoutPasscode).startAnimation(shake);
                        // txt_enter_password.setText("Try again");
                        // txt_delete.setText("Cancel");
                        /*if (getBoolVibration(mContext)) {
                            Vibrator v = (Vibrator) mContext
                                    .getApplicationContext().getSystemService(
                                            Context.VIBRATOR_SERVICE);
                            // Vibrate for 500 milliseconds
                            v.vibrate(500);
                        }*/
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

                                PassDot1.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.pass_border));
                                PassDot2.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.pass_border));
                                PassDot3.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.pass_border));
                                PassDot4.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.pass_border));
                            }
                        });

                    }
                }
            }, 300);

        }
    }

    public void openLayout() {
        mContainer.addView(this);
        try {
            requestFocus();
            requestLayout();
            return;
        } catch (Exception localException) {
            for (;;) {
                localException.printStackTrace();
            }
        }
    }
}
