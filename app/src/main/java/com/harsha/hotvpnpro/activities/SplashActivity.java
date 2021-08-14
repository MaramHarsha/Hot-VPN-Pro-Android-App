package com.harsha.hotvpnpro.activities;
/*Made By Harsha*/

import android.content.Intent;


import android.os.Bundle;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.response.User;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.google.android.material.snackbar.Snackbar;
import com.onesignal.OneSignal;
import com.harsha.hotvpnpro.BuildConfig;
import com.harsha.hotvpnpro.Preference;
import com.harsha.hotvpnpro.R;
import com.harsha.hotvpnpro.utils.NetworkStateUtility;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.harsha.hotvpnpro.utils.BillConfig.IN_PURCHASE_KEY;
import static com.harsha.hotvpnpro.utils.BillConfig.One_Month_Sub;
import static com.harsha.hotvpnpro.utils.BillConfig.One_Year_Sub;
import static com.harsha.hotvpnpro.utils.BillConfig.Six_Month_Sub;

public class SplashActivity extends AppCompatActivity {

    Preference preference;
    @BindView(R.id.parent)
    ConstraintLayout parent;
    private static int SPLASH_SCREEN = 2500;
    ImageView image;
    TextView logo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        preference = new Preference(SplashActivity.this);
        preference.setStringpreference(IN_PURCHASE_KEY, BuildConfig.IN_APPKEY);
        preference.setStringpreference(One_Month_Sub, BuildConfig.MONTHLY);
        preference.setStringpreference(Six_Month_Sub, BuildConfig.SIX_MONTH);
        preference.setStringpreference(One_Year_Sub, BuildConfig.YEARLY);


        image = findViewById(R.id.imageView);
        logo = findViewById(R.id.textView);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!NetworkStateUtility.isOnline(getApplicationContext())) {


                    Snackbar snackbar = Snackbar
                            .make(parent, "Check internet connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    loginUser();
                }


            }
        }, SPLASH_SCREEN);

    }

    private void loginUser() {
        AuthMethod authMethod = AuthMethod.anonymous();
        UnifiedSDK.getInstance().getBackend().login(authMethod, new Callback<User>() {
            @Override
            public void success(@NonNull User user) {
                Intent intent_main = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent_main);
                finish();
            }

            @Override
            public void failure(@NonNull VpnException e) {
                Snackbar snackbar = Snackbar
                        .make(parent, "Authentication Error, Please try again.", Snackbar.LENGTH_INDEFINITE);

                snackbar.setAction("Try again", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginUser();
                    }
                });
                snackbar.show();
            }
        });

    }
}

