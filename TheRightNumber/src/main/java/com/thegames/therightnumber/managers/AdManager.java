package com.thegames.therightnumber.managers;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.thegames.therightnumber.AbstractActivity;
import com.thegames.therightnumber.Constants;
import com.thegames.therightnumber.R;
import com.thegames.therightnumber.uihome.HomeActivity;

import java.lang.ref.WeakReference;

/**
 * Created by gyljean-lambert on 21/06/2014.
 */
public class AdManager {

    public static final String TAG = "AdManager";

    private static AdManager INSTANCE;
    private Context mAppContext;
    private WeakReference<AbstractActivity> mCurrentActivityWeakReference = new WeakReference<AbstractActivity>(null);

    private InterstitialAd interstitial;
    private boolean mAlreadyShowedInterstitialAd;

    /*
     * GETTERS / SETTERS
     */

    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
    }

    public boolean isAlreadyShowedInterstitialAd() {
        return mAlreadyShowedInterstitialAd;
    }

    public void setAlreadyShowedInterstitialAd(boolean alreadyShowedInterstitialAd) {
        this.mAlreadyShowedInterstitialAd = alreadyShowedInterstitialAd;
    }
/*
     * CTOR
     */

    private AdManager(){}

    public static AdManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new AdManager();
        }
        return INSTANCE;
    }

    public void setCurrentActivity(final AbstractActivity currentActivity) {
        if(currentActivity != null) {
            Log.e(TAG, "===> SET CURRENT ACTIVITY");
            mCurrentActivityWeakReference = new WeakReference<AbstractActivity>(currentActivity);
            loadInterstitialAd();
            interstitial.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    loadInterstitialAd();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.e(TAG, "===> INTERSTITIAL AD LOADED");
                    if(currentActivity instanceof HomeActivity && !mAlreadyShowedInterstitialAd) {
                        showInterstitial();
                    }
                }
            });
        }
    }

    private void loadInterstitialAd() {
        if(mCurrentActivityWeakReference != null && mCurrentActivityWeakReference.get() != null) {
            // Créez l'interstitiel.
            interstitial = new InterstitialAd(mCurrentActivityWeakReference.get());
            interstitial.setAdUnitId(mAppContext.getString(R.string.admob_interstitial_id));

            boolean mIsUserPremium = GameManager.getInstance().getBooleanPref(Constants.PREFS_USER_IS_PREMIUM, false);
            if(!mIsUserPremium) {
                // Créez la demande d'annonce.
                AdRequest adRequest = new AdRequest.Builder().addTestDevice("87ABE0391AFEE440EFC081DF3EB77D4E").build();

                // Lancez le chargement de l'interstitiel.
                interstitial.loadAd(adRequest);
            }
        }
    }

    public void showInterstitial() {
        if (interstitial.isLoaded()) {
            mAlreadyShowedInterstitialAd = true;
            interstitial.show();
            Log.e(TAG, "===> SHOWING INTERSTITIAL");
        }
    }

}
