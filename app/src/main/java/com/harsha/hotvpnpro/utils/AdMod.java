package com.harsha.hotvpnpro.utils;
/*Made By Harsha*/
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.harsha.hotvpnpro.BuildConfig;
import com.harsha.hotvpnpro.Preference;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class AdMod {

    public static void buildAdBanner(Context context, RelativeLayout linearLayout, int i, final MyAdListener myAdListener) {
        Preference preference = new Preference(context);
        if (!preference.isBooleenPreference(BillConfig.PRIMIUM_STATE)) {
            AdView adView = new AdView(context);
            if (i == 0) {
                adView.setAdSize(AdSize.BANNER);
            } else if (i == 1) {
                adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            } else if (i != 2) {
                adView.setAdSize(AdSize.SMART_BANNER);
            } else {
                adView.setAdSize(AdSize.LARGE_BANNER);
            }
            adView.setAdUnitId(BuildConfig.GOOGLE_BANNER);
            linearLayout.addView(adView);
            AdRequest build = new AdRequest.Builder().build();
            if (adView.getAdSize() != null || adView.getAdUnitId() != null) {
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        myAdListener.onAdClosed();
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                        Log.e("adloadFailed", String.valueOf(i));
                        myAdListener.onFaildToLoad(i);
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                        myAdListener.onAdOpened();
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        myAdListener.onAdLoaded();
                    }
                });
                adView.loadAd(build);
                return;
            }
            adView.setVisibility(View.GONE);
        }
    }

    public static void buildAdFullScreen(Context context, final MyAdListener myAdListener) {
        /*Preference preference = new Preference(context);
        if (!preference.isBooleenPreference(BillConfig.PRIMIUM_STATE)) {
            final InterstitialAd interstitialAd = new InterstitialAd(context);
            interstitialAd.setAdUnitId(BuildConfig.GOOGLE_INTERSTITIAL);
            AdRequest build = new AdRequest.Builder().build();
            interstitialAd.setAdListener(new com.google.android.gms.ads.AdListener() {
                public void onAdClosed() {
                    myAdListener.onAdClosed();
                    super.onAdClosed();
                }

                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                }

                public void onAdLoaded() {
                    if (interstitialAd == null || !interstitialAd.isLoaded()) {
                    } else {
                        interstitialAd.show();
                    }
                    super.onAdLoaded();
                }

                public void onAdClicked() {
                    myAdListener.onAdClicked();
                    super.onAdClicked();
                }

                @Override
                public void onAdOpened() {
                    myAdListener.onAdOpened();
                    super.onAdOpened();
                }
            });
            interstitialAd.loadAd(build);
        }*/
    }


    public interface MyAdListener {
        void onAdClicked();

        void onAdClosed();

        void onAdLoaded();

        void onAdOpened();

        void onFaildToLoad(int i);
    }
}
