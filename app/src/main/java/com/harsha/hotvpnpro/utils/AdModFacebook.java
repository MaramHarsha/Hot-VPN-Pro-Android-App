package com.harsha.hotvpnpro.utils;
/*Made By Harsha*/
import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;

import com.harsha.hotvpnpro.BuildConfig;
import com.harsha.hotvpnpro.Preference;
import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;

public class AdModFacebook {

    private static String TAG = "facebook_ads";

    public static void buildAdBanner(Context context, RelativeLayout linearLayout, int i, final MyAdListener myAdListener) {
        Preference preference=new Preference(context);
        if(!preference.isBooleenPreference(BillConfig.PRIMIUM_STATE)) {
            AdSize adSize;
            switch (i) {
                case 0:
                    adSize = AdSize.BANNER_HEIGHT_50;
                    break;
                case 1:
                    adSize = AdSize.BANNER_HEIGHT_90;
                    break;
                case 2:
                    adSize = AdSize.BANNER_320_50;
                    break;
                default:
                    adSize = AdSize.BANNER_HEIGHT_50;
                    break;
            }


            AdView adView = new AdView(context, BuildConfig.FACEBOOK_BANNER, adSize);
            linearLayout.addView(adView);
            adView.setAdListener(new AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    Log.e(TAG, "onError: " + adError.getErrorMessage());
                    myAdListener.onFaildToLoad(adError);
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    myAdListener.onAdLoaded();
                }

                @Override
                public void onAdClicked(Ad ad) {
                    myAdListener.onAdClicked();
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            });
            adView.loadAd();
        }
    }

    public static void buildAdFullScreen(Context context, final MyAdListener myAdListener) {
        Preference preference=new Preference(context);
        if(!preference.isBooleenPreference(BillConfig.PRIMIUM_STATE)) {
            final InterstitialAd interstitialAd = new InterstitialAd(context, BuildConfig.FACEBOOK_INTERSTITIAL);
            interstitialAd.setAdListener(new AbstractAdListener() {
                @Override
                public void onError(Ad ad, AdError error) {
                    myAdListener.onFaildToLoad(error);
                    Log.e(TAG, "onError: " + error.getErrorMessage());
                    super.onError(ad, error);
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (interstitialAd == null || !interstitialAd.isAdLoaded()) {
                    } else {
                        interstitialAd.show();
                    }
                    myAdListener.onAdLoaded();
                    super.onAdLoaded(ad);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    myAdListener.onAdClicked();
                    super.onAdClicked(ad);
                }

                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    myAdListener.onInterstitialDisplayed();
                    super.onInterstitialDisplayed(ad);
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    myAdListener.onInterstitialDismissed();
                    super.onInterstitialDismissed(ad);
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    myAdListener.onLoggingImpression();
                    super.onLoggingImpression(ad);
                }
            });

            interstitialAd.loadAd();
        }
    }


    public interface MyAdListener {
        void onAdClicked();

        void onAdClosed();

        void onAdLoaded();

        void onAdOpened();

        void onFaildToLoad(AdError adError);

        void onInterstitialDismissed();

        void onInterstitialDisplayed();

        void onLoggingImpression();
    }
}
