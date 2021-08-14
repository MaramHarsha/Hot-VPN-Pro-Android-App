package com.harsha.hotvpnpro.activities;
/*Made By Harsha*/
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anchorfree.partner.api.response.RemainingTraffic;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.harsha.hotvpnpro.BuildConfig;
import com.harsha.hotvpnpro.InAppBilling.IabBroadcastReceiver;
import com.harsha.hotvpnpro.InAppBilling.IabHelper;
import com.harsha.hotvpnpro.InAppBilling.IabResult;
import com.harsha.hotvpnpro.InAppBilling.Inventory;
import com.harsha.hotvpnpro.InAppBilling.Purchase;
import com.harsha.hotvpnpro.Preference;
import com.harsha.hotvpnpro.R;
import com.harsha.hotvpnpro.ipaddressapi.APIClient;
import com.harsha.hotvpnpro.ipaddressapi.ApiResponse;
import com.harsha.hotvpnpro.ipaddressapi.RestApis;
import com.harsha.hotvpnpro.utils.AdMod;
import com.harsha.hotvpnpro.utils.AdModFacebook;
import com.harsha.hotvpnpro.utils.Converter;
import com.facebook.ads.AdError;
import com.google.android.gms.ads.MobileAds;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

import static com.harsha.hotvpnpro.utils.BillConfig.INAPPSKUUNIT;
import static com.harsha.hotvpnpro.utils.BillConfig.IN_PURCHASE_KEY;
import static com.harsha.hotvpnpro.utils.BillConfig.One_Month_Sub;
import static com.harsha.hotvpnpro.utils.BillConfig.One_Year_Sub;
import static com.harsha.hotvpnpro.utils.BillConfig.PRIMIUM_STATE;
import static com.harsha.hotvpnpro.utils.BillConfig.PURCHASETIME;
import static com.harsha.hotvpnpro.utils.BillConfig.Six_Month_Sub;


public abstract class UIActivity extends AppCompatActivity implements View.OnClickListener, IabBroadcastReceiver.IabBroadcastListener {

    protected static final String TAG = MainActivity.class.getSimpleName();
    private InterstitialAd interstitialAd;
    public String SKU_DELAROY_MONTHLY;
    public String SKU_DELAROY_SIXMONTH;
    public String SKU_DELAROY_YEARLY;
    public String base64EncodedPublicKey;
    @BindView(R.id.server_ip)
    TextView server_ip;
    @BindView(R.id.img_connect)
    ImageView img_connect;
    @BindView(R.id.connection_state)
    ImageView connectionStateTextView;


    @BindView(R.id.optimal_server_btn)
    LinearLayout currentServerBtn;
    @BindView(R.id.selected_server)
    TextView selectedServerTextView;
    @BindView(R.id.country_flag)
    ImageView country_flag;
    @BindView(R.id.uploading_speed)
    TextView uploading_speed_textview;
    @BindView(R.id.downloading_speed)
    TextView downloading_speed_textview;


    @BindView(R.id.premium)
    ImageView premium;
    Toolbar toolbar;
    IabHelper mHelper;
    IabBroadcastReceiver mBroadcastReceiver;
    Preference preference;
    boolean mSubscribedToDelaroy = false;
    boolean connected = false;
    String mDelaroySku = "";
    boolean mAutoRenewEnabled = false;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (mHelper == null) return;
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                unlockdata();
                return;
            }

            // First find out which subscription is auto renewing
            Purchase delaroyMonthly = inventory.getPurchase(SKU_DELAROY_MONTHLY);
            Purchase delaroySixMonth = inventory.getPurchase(SKU_DELAROY_SIXMONTH);
            Purchase delaroyYearly = inventory.getPurchase(SKU_DELAROY_YEARLY);
            if (delaroyMonthly != null && delaroyMonthly.isAutoRenewing()) {
                mDelaroySku = SKU_DELAROY_MONTHLY;
                mAutoRenewEnabled = true;
            } else if (delaroySixMonth != null && delaroySixMonth.isAutoRenewing()) {
                mDelaroySku = SKU_DELAROY_SIXMONTH;
                mAutoRenewEnabled = true;
            } else if (delaroyYearly != null && delaroyYearly.isAutoRenewing()) {
                mDelaroySku = SKU_DELAROY_YEARLY;
                mAutoRenewEnabled = true;
            } else {
                mDelaroySku = "";
                mAutoRenewEnabled = false;
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            mSubscribedToDelaroy = (delaroyMonthly != null && verifyDeveloperPayload(delaroyMonthly))
                    || (delaroySixMonth != null && verifyDeveloperPayload(delaroySixMonth))
                    || (delaroyYearly != null && verifyDeveloperPayload(delaroyYearly));

            if (mDelaroySku != "") {
                preference.setStringpreference(INAPPSKUUNIT, mDelaroySku);
                preference.setLongpreference(PURCHASETIME, inventory.getPurchase(mDelaroySku).getPurchaseTime());
            }
            unlockdata();
        }
    };
    int[] Onconnect = {R.drawable.ic_on};
    int[] Ondisconnect = {R.drawable.ic_off};
    private Handler mUIHandler = new Handler(Looper.getMainLooper());

    final Runnable mUIUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
            checkRemainingTraffic();
            mUIHandler.postDelayed(mUIUpdateRunnable, 10000);
        }
    };
    private BillingProcessor bp;

    protected abstract void isLoggedIn(Callback<Boolean> callback);

    protected abstract void loginToVpn();

    protected abstract void isConnected(Callback<Boolean> callback);

    protected abstract void connectToVpn();

    protected abstract void disconnectFromVnp();

    protected abstract void chooseServer();

    protected abstract void getCurrentServer(Callback<String> callback);

    protected abstract void checkRemainingTraffic();

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }

    void complain(String message) {
        alert("Error: " + message);
    }

    void alert(String message) {
        android.app.AlertDialog.Builder bld = new android.app.AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }

    private void unlockdata() {
        if (mSubscribedToDelaroy) {
            unlock();
        } else {
            preference.setBooleanpreference(PRIMIUM_STATE, false);
        }
        if (!preference.isBooleenPreference(PRIMIUM_STATE)) {
            premium.setVisibility(View.VISIBLE);

        } else {
            premium.setVisibility(View.GONE);

        }
        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_app_ID));
//        LoadInterstitialAd();
        LoadBannerAd();
    }

    public void unlock() {
        preference.setBooleanpreference(PRIMIUM_STATE, true);
    }

    @Override
    public void receivedBroadcast() {
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mHelper == null) {
            return;
        }
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
        }
    }


    LinearLayout layDrawer;
    LinearLayout layDrawerBg;
    FrameLayout imgFrame;


    TextView layPremium;
    TextView layPrivacyPolicy;
    TextView layRateUs;
    TextView layShare;
    TextView layFAQ;


    void loadAds() {
        interstitialAd = new InterstitialAd(this, BuildConfig.FACEBOOK_INTERSTITIAL);
        // Create listeners for the Interstitial Ad
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                loadAds();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        loginToVpn();
        loadAds();
        ImageView img_rate = findViewById(R.id.imgrate);
        ImageView imgMenu = findViewById(R.id.imgMenu);


        layPremium = findViewById(R.id.layPremium);
        layPrivacyPolicy = findViewById(R.id.layPrivacyPolicy);
        layRateUs = findViewById(R.id.layRateUs);
        layShare = findViewById(R.id.layShare);
        layFAQ = findViewById(R.id.layFAQ);
        layDrawerBg = findViewById(R.id.layDrawerBg);
        layDrawer = findViewById(R.id.layDrawer);


        layDrawer.setVisibility(View.VISIBLE);
        layDrawerBg.setVisibility(View.GONE);


        TranslateAnimation animation = new TranslateAnimation(0, -800, 0, 0);
        animation.setDuration(0); // duartion in ms
        animation.setFillAfter(true);
        layDrawer.startAnimation(animation);

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                layDrawer.setVisibility(View.VISIBLE);
                layDrawerBg.setVisibility(View.VISIBLE);

                TranslateAnimation animation = new TranslateAnimation(-800, 0, 0, 0);
                animation.setDuration(500); // duartion in ms
                animation.setFillAfter(true);
                layDrawer.startAnimation(animation);


                layDrawerBg.setOnClickListener(new View.OnClickListener() {
                    private Animation animSlide;

                    @Override
                    public void onClick(View view) {


                        drwaerClose();


                    }
                });
                layPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drwaerClose();

                        Uri uri = Uri.parse(getResources().getString(R.string.privacy_policy_link)); // missing 'http://' will cause crashed
                        Intent intent_policy = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent_policy);


                    }
                });
                layRateUs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drwaerClose();

                        Uri uri = Uri.parse("market://details?id=" + UIActivity.this.getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=" + UIActivity.this.getPackageName())));
                        }

                    }
                });
                layShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drwaerClose();


                        Intent ishare = new Intent(Intent.ACTION_SEND);
                        ishare.setType("text/plain");
                        String sAux = "\n" + getResources().getString(R.string.app_name) + "\n\n";
                        sAux = sAux + "https://play.google.com/store/apps/details?id=" + getApplication().getPackageName();
                        ishare.putExtra(Intent.EXTRA_TEXT, sAux);
                        startActivity(Intent.createChooser(ishare, "choose one"));

                    }
                });
                layFAQ.setOnClickListener(v -> {
                    drwaerClose();
                    startActivity(new Intent(UIActivity.this, Faq.class));


                });
                layPremium.setOnClickListener(v -> {
                    drwaerClose();
                    startActivity(new Intent(UIActivity.this, GetPremiumActivity.class));

                });

            }
        });


        img_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("market://details?id=" + UIActivity.this.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + UIActivity.this.getPackageName())));
                }
            }


        });
        ImageView img_menu = findViewById(R.id.imgmenu);
        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UIActivity.this, MenuActivity.class));

            }
        });

        preference = new Preference(this);
        if (BuildConfig.USE_IN_APP_PURCHASE) {
            base64EncodedPublicKey = preference.getStringpreference(IN_PURCHASE_KEY, base64EncodedPublicKey);
            SKU_DELAROY_MONTHLY = preference.getStringpreference(One_Month_Sub, SKU_DELAROY_MONTHLY);
            SKU_DELAROY_SIXMONTH = preference.getStringpreference(Six_Month_Sub, SKU_DELAROY_SIXMONTH);
            SKU_DELAROY_YEARLY = preference.getStringpreference(One_Year_Sub, SKU_DELAROY_YEARLY);


            mHelper = new IabHelper(this, base64EncodedPublicKey);
            mHelper.enableDebugLogging(true);


            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {

                    if (!result.isSuccess()) {
                        complain("Problem setting up in-app billing: " + result);
                        return;
                    }

                    if (mHelper == null) return;

                    mBroadcastReceiver = new IabBroadcastReceiver(UIActivity.this);
                    IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                    registerReceiver(mBroadcastReceiver, broadcastFilter);

                    try {
                        mHelper.queryInventoryAsync(mGotInventoryListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        complain("Error querying inventory. Another async operation in progress.");
                    }
                }
            });
        } else {
            preference.setBooleanpreference(PRIMIUM_STATE, false);
            premium.setVisibility(View.GONE);
            MobileAds.initialize(this, getString(R.string.admob_app_ID));
//            LoadInterstitialAd();
            LoadBannerAd();
        }


        getip();
    }

    void drwaerClose() {
        layDrawer.setVisibility(View.VISIBLE);
        layDrawerBg.setVisibility(View.GONE);


        TranslateAnimation animation = new TranslateAnimation(0, -800, 0, 0);
        animation.setDuration(500); // duartion in ms
        animation.setFillAfter(true);
        layDrawer.startAnimation(animation);


        layDrawerBg.setOnClickListener(new View.OnClickListener() {
            private Animation animSlide;

            @Override
            public void onClick(View view) {


            }
        });
        layPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layFAQ.setOnClickListener(v -> {


        });
        layPremium.setOnClickListener(v -> {

        });
    }

    private void getip() {
        RestApis mRestApis = APIClient.getRetrofitInstance("https://api.ipify.org").create(RestApis.class);
        Call<ApiResponse> userAdd = mRestApis.requestip("json");
        userAdd.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.e(TAG, "onResponse: " + response.body().getIp());
                if (response != null) {
                    server_ip.setText(response.body().getIp());
                } else {
                    server_ip.setText(R.string.default_server_ip_text);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                server_ip.setText(R.string.default_server_ip_text);
            }
        });
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    startUIUpdateTask();
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUIUpdateTask();
    }

    @OnClick(R.id.premium)
    public void premiumMenu(View v) {
        startActivity(new Intent(this, GetPremiumActivity.class));
    }

    @OnClick(R.id.img_connect)
    public void onConnectBtnClick(View v) {





        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(UIActivity.this);

                    builder.setCancelable(false)
                            .setPositiveButton("Disconnect ", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (interstitialAd.isAdLoaded())
                                        interstitialAd.show();


                                    disconnectFromVnp();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.cancel();

                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Are you sure you want to disconnect?");
                    alert.show();


                } else {
                    if (interstitialAd.isAdLoaded())
                        interstitialAd.show();

                    connectToVpn();
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
            }
        });
    }

    //    @OnClick(R.id.optimal_server_btn)
    @OnClick(R.id.country_flag)
    public void onServerChooserClick(View v) {


        if (interstitialAd.isAdLoaded())
            interstitialAd.show();

        chooseServer();
    }


    protected void startUIUpdateTask() {
        stopUIUpdateTask();
        mUIHandler.post(mUIUpdateRunnable);
    }

    protected void stopUIUpdateTask() {
        mUIHandler.removeCallbacks(mUIUpdateRunnable);
        updateUI();
    }


    protected void updateUI() {
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {
                switch (vpnState) {
                    case IDLE: {
                        Log.e(TAG, "success: IDLE");
                        connectionStateTextView.setImageResource(R.drawable.disc);
                        getip();
                        if (connected) {
                            connected = false;
                            animate(img_connect, Ondisconnect, 0, false);
                        }
                        country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                        selectedServerTextView.setText(R.string.select_country);
                        ChangeBlockVisibility();
                        uploading_speed_textview.setText("");
                        downloading_speed_textview.setText("");

                        hideConnectProgress();
                        break;
                    }
                    case CONNECTED: {
                        Log.e(TAG, "success: CONNECTED");
                        if (!connected) {
                            connected = true;
                            animate(img_connect, Onconnect, 0, false);
                        }
                        connectionStateTextView.setImageResource(R.drawable.conne);
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTING_VPN:
                    case CONNECTING_CREDENTIALS:
                    case CONNECTING_PERMISSIONS: {


                        Glide
                                .with(UIActivity.this)
                                .load(R.drawable.searching)
                                .into(img_connect);


                        connectionStateTextView.setImageResource(R.drawable.connecting);
                        ChangeBlockVisibility();
                        country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                        selectedServerTextView.setText(R.string.select_country);
                        showConnectProgress();
                        break;
                    }
                    case PAUSED: {
                        Log.e(TAG, "success: PAUSED");
                        ChangeBlockVisibility();
                        country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                        selectedServerTextView.setText(R.string.select_country);
                        break;
                    }
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                selectedServerTextView.setText(R.string.select_country);
            }
        });
        getCurrentServer(new Callback<String>() {
            @Override
            public void success(@NonNull final String currentServer) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                        selectedServerTextView.setText(R.string.select_country);
                        if (!currentServer.equals("")) {
                            Locale locale = new Locale("", currentServer);
                            Resources resources = getResources();
                            String sb = "drawable/" + currentServer.toLowerCase();
                            country_flag.setImageResource(resources.getIdentifier(sb, null, getPackageName()));
                            selectedServerTextView.setText(locale.getDisplayCountry());
                        } else {
                            country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                            selectedServerTextView.setText(R.string.select_country);
                        }
                    }
                });
            }

            @Override
            public void failure(@NonNull VpnException e) {
                country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                selectedServerTextView.setText(R.string.select_country);
            }
        });
    }

    private void ChangeBlockVisibility() {
        if (BuildConfig.USE_IN_APP_PURCHASE) {
            if (preference.isBooleenPreference(PRIMIUM_STATE)) {
                premium.setVisibility(View.GONE);
            } else {
                premium.setVisibility(View.VISIBLE);
            }
        } else {
            premium.setVisibility(View.GONE);
        }
    }

    private void animate(final ImageView imageView, final int images[], final int imageIndex, final boolean forever) {


        int fadeInDuration = 500;
        int timeBetween = 3000;
        int fadeOutDuration = 1000;

        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(images[imageIndex]);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(fadeInDuration);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(fadeInDuration + timeBetween);
        fadeOut.setDuration(fadeOutDuration);

        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);

        animation.setRepeatCount(1);
        imageView.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (images.length - 1 > imageIndex) {
                    animate(imageView, images, imageIndex + 1, forever); //Calls itself until it gets to the end of the array
                } else {
                    if (forever) {
                        animate(imageView, images, 0, forever);  //Calls itself to start the animation all over again in a loop if forever = true
                    }
                }
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });
    }


    protected void updateTrafficStats(long outBytes, long inBytes) {
        String outString = Converter.humanReadableByteCountOld(outBytes, false);
        String inString = Converter.humanReadableByteCountOld(inBytes, false);

        uploading_speed_textview.setText(inString);
        downloading_speed_textview.setText(outString);

    }

    protected void updateRemainingTraffic(RemainingTraffic remainingTrafficResponse) {
        if (remainingTrafficResponse.isUnlimited()) {

        } else {
            String trafficUsed = Converter.megabyteCount(remainingTrafficResponse.getTrafficUsed()) + "Mb";
            String trafficLimit = Converter.megabyteCount(remainingTrafficResponse.getTrafficLimit()) + "Mb";

        }
    }

    protected void ShowIPaddera(String ipaddress) {
        server_ip.setText(ipaddress);
    }


    protected void showConnectProgress() {

    }

    protected void hideConnectProgress() {

    }

    protected void showMessage(String msg) {
        Toast.makeText(UIActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void LoadInterstitialAd() {
        /*if (BuildConfig.GOOGlE_AD) {
            AdMod.buildAdFullScreen(getApplicationContext(), new AdMod.MyAdListener() {
                @Override
                public void onAdClicked() {
                }

                @Override
                public void onAdClosed() {
                }

                @Override
                public void onAdLoaded() {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onFaildToLoad(int i) {
                }
            });
        } else if (BuildConfig.FACEBOOK_AD) {
            AdModFacebook.buildAdFullScreen(getApplicationContext(), new AdModFacebook.MyAdListener() {
                @Override
                public void onAdClicked() {
                }

                @Override
                public void onAdClosed() {
                }

                @Override
                public void onAdLoaded() {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onFaildToLoad(AdError adError) {
                }

                @Override
                public void onInterstitialDismissed() {
                }

                @Override
                public void onInterstitialDisplayed() {
                }

                @Override
                public void onLoggingImpression() {
                }
            });
        }*/
    }

    public void LoadBannerAd() {
        RelativeLayout adContainer = findViewById(R.id.adView);
        if (BuildConfig.GOOGlE_AD) {
            AdMod.buildAdBanner(getApplicationContext(), adContainer, 0, new AdMod.MyAdListener() {
                @Override
                public void onAdClicked() {
                }

                @Override
                public void onAdClosed() {
                }

                @Override
                public void onAdLoaded() {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onFaildToLoad(int i) {
                }
            });
        } else if (BuildConfig.FACEBOOK_AD) {
            AdModFacebook.buildAdBanner(this, adContainer, 0, new AdModFacebook.MyAdListener() {
                @Override
                public void onAdClicked() {
                }

                @Override
                public void onAdClosed() {
                }

                @Override
                public void onAdLoaded() {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onFaildToLoad(AdError adError) {
                }

                @Override
                public void onInterstitialDismissed() {
                }

                @Override
                public void onInterstitialDisplayed() {
                }

                @Override
                public void onLoggingImpression() {
                }
            });
        }
    }
}
