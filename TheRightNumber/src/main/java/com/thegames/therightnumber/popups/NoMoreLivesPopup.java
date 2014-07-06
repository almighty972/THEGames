package com.thegames.therightnumber.popups;


import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.jirbo.adcolony.AdColonyAd;
import com.jirbo.adcolony.AdColonyAdAvailabilityListener;
import com.jirbo.adcolony.AdColonyAdListener;
import com.jirbo.adcolony.AdColonyVideoAd;
import com.thegames.therightnumber.AbstractActivity;
import com.thegames.therightnumber.Constants;
import com.thegames.therightnumber.R;
import com.thegames.therightnumber.billing.Inventory;
import com.thegames.therightnumber.billing.Purchase;
import com.thegames.therightnumber.interfaces.BillingListener;
import com.thegames.therightnumber.interfaces.InAppViewListener;
import com.thegames.therightnumber.managers.BillingManager;
import com.thegames.therightnumber.managers.GameManager;
import com.thegames.therightnumber.model.InApp;
import com.thegames.therightnumber.uigame.GameActivity;
import com.thegames.therightnumber.views.InAppView;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by gyljean-lambert on 25/05/2014.
 */
public class NoMoreLivesPopup extends Popup implements BillingListener, InAppViewListener, AdColonyAdListener, AdColonyAdAvailabilityListener {

    private CountDownTimer mCountDownTimer;
    private long mMillisUntilFinished;
    private long mLastStartedTime;
    private AbstractActivity mActivity;

    private Handler mUiHandler;
    private boolean mIsCredited;
    private boolean mVisible;
    private boolean mTimesUp;


    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static NoMoreLivesPopup newInstance(String title, String body) {
        NoMoreLivesPopup f = new NoMoreLivesPopup();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("body", body);

        f.setArguments(args);

        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle = getArguments().getString("title");
        mBody = getArguments().getString("body");

        setCancelable(false);

        mPopupReference = new WeakReference<Popup>(this);
        mUiHandler = new Handler();
        mIsCredited = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mVisible = true;
        if(!mTimesUp) {
            startOrResumeCountDown();
        } else {
            creditLivesAfterCountdown();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (AbstractActivity) activity;

        BillingManager.getInstance().setCurrentActivity(mActivity);
        BillingManager.getInstance().setBillingListener(this);
        BillingManager.getInstance().getInAppProducts();
    }

    @Override
    public void onPause() {
        super.onPause();
        mVisible = false;
        if(mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBodyTextView.setGravity(Gravity.CENTER);
        mNoMoreLivesLayout.setVisibility(View.VISIBLE);
        mShareButtonsLayout.setVisibility(View.GONE);
        mBottomButtonsLayout.setVisibility(View.GONE);
        mProgressRing.setVisibility(View.VISIBLE);
    }

    private void startOrResumeCountDown() {
        mLastStartedTime = GameManager.getInstance().getLongPref(Constants.PREFS_COUNTDOWN_STARTED_TIME);
        Log.d("", "startOrResumeCountDown() ---> started: " + mLastStartedTime);

        if(mLastStartedTime <= 0) {
            mLastStartedTime = System.currentTimeMillis();
            GameManager.getInstance().saveLongPref(Constants.PREFS_COUNTDOWN_STARTED_TIME, mLastStartedTime);
        }

        mMillisUntilFinished = System.currentTimeMillis() - mLastStartedTime;
        if(mMillisUntilFinished < Constants.COUNTDOWN_INIT_TIME) {
            mMillisUntilFinished = Constants.COUNTDOWN_INIT_TIME - mMillisUntilFinished;
            mCountDownTimer = new CountDownTimer(mMillisUntilFinished, 1000) {

                public void onTick(long millisUntilFinished) {
                    int minutes = (int) (millisUntilFinished / 1000) / 60;
                    int seconds = (int) (millisUntilFinished / 1000) % 60;
                    String time = String.format("%02d:%02d", minutes, seconds);
                    Log.d("", "--->" + time);
                    if(mCountdownTextView != null) {
                        mCountdownTextView.setText(time);
                    }
                }

                public void onFinish() {
                    mTimesUp = true;
                    if(mCountdownTextView != null) {
                        mCountdownTextView.setText("00:00");
                    }
                    creditLivesAfterCountdown();
                }
            };
            mCountDownTimer.start();
        } else {
            creditLivesAfterCountdown();
        }
    }

    private void creditLivesAfterCountdown() {
        GameManager.getInstance().saveLongPref(Constants.PREFS_COUNTDOWN_TIME, 0);
        GameManager.getInstance().saveLongPref(Constants.PREFS_COUNTDOWN_STARTED_TIME, 0);
        if(!mIsCredited) {
            mIsCredited = true;
            GameManager.getInstance().addBonusLives(Constants.SKU_COUNTDOWN);
        }
        if(mVisible) {
            dismiss();
        }
    }

    @Override
    public void onIabSetupFinished() {

    }

    @Override
    public void onProductsReceived(Inventory inventory) {
        mProgressRing.setVisibility(View.GONE);
        if(mActivity != null) {
            InApp inapp = null;

            // adding free inapp
            inapp = new InApp(String.valueOf(Constants.FREE_IN_APP_VIDEO_LIVES_COUNT), mActivity.getString(R.string.free_inapp), mActivity.getString(R.string.inapp_25_below_text));
            inapp.setSku(Constants.SKU_FREE_INAPP_VIDEO);
            addInappView(inapp);

            // adding other inapp
            if (inventory != null) {
                if (inventory.hasDetails(Constants.SKU_PACK_200_VIES)) {
                    inapp = new InApp(inventory.getSkuDetails(Constants.SKU_PACK_200_VIES));
                    inapp.setBelowText(mActivity.getString(R.string.inapp_200_below_text));
                    inapp.setSku(Constants.SKU_PACK_200_VIES);
                    addInappView(inapp);
                }
                if (inventory.hasDetails(Constants.SKU_PACK_1500_VIES)) {
                    inapp = new InApp(inventory.getSkuDetails(Constants.SKU_PACK_1500_VIES));
                    inapp.setBelowText(mActivity.getString(R.string.inapp_1500_below_text));
                    inapp.setSku(Constants.SKU_PACK_1500_VIES);
                    addInappView(inapp);
                }
                if (inventory.hasDetails(Constants.SKU_PACK_5000_VIES)) {
                    inapp = new InApp(inventory.getSkuDetails(Constants.SKU_PACK_5000_VIES));
                    inapp.setBelowText(mActivity.getString(R.string.inapp_5000_below_text));
                    inapp.setSku(Constants.SKU_PACK_5000_VIES);
                    addInappView(inapp);
                }

                startOrResumeCountDown();
            }
        }
    }

    @Override
    public void onQueryPurchasedItems(HashMap<String, Purchase> itemsMap) { }

    @Override
    public void onConsumePurchasedItems(boolean consumeSuccess, Purchase purchase) { }


    private void addInappView(InApp inApp) {
        if(mInappsLayout != null) {
            InAppView inAppView = new InAppView(getActivity(), inApp);
            inAppView.setInAppListener(this);
            mInappsLayout.addView(inAppView);
        }
    }

    @Override
    public void onSelectInApp(String sku) {
        Log.d("", "==> sku: " + sku);
        if(sku.equals(Constants.SKU_FREE_INAPP_VIDEO)) { // free adColony video to get 25 lives
            AdColonyVideoAd ad = new AdColonyVideoAd( getString(R.string.testZoneId) ).withListener(this);
            ad.show();
        } else {
            BillingManager.getInstance().setCurrentActivity(mActivity);
            GameActivity.GameFragment gameFragment = ((GameActivity) mActivity).getGameFragment();
            BillingManager.getInstance().setBillingListener(gameFragment);
            BillingManager.getInstance().purchaseInApp(sku);
            dismiss();
        }

    }

    @Override
    public void onAdColonyAdAvailabilityChange(boolean available, String zoneId) {
        if (available) {
            Log.d("AdColony", "onAdColonyAdAvailabilityChange");
//            AdColonyVideoAd ad = new AdColonyVideoAd( getString(R.string.testZoneId) ).withListener(this);
//            ad.show();
        }
    }

    @Override
    public void onAdColonyAdAttemptFinished(AdColonyAd adColonyAd) {
        // You can ping the AdColonyAd object here for more information:
        // ad.shown() - returns true if the ad was successfully shown.
        // ad.notShown() - returns true if the ad was not shown at all (i.e. if onAdColonyAdStarted was never triggered)
        // ad.skipped() - returns true if the ad was skipped due to an interval play setting
        // ad.canceled() - returns true if the ad was cancelled (either programmatically or by the user)
        // ad.noFill() - returns true if the ad was not shown due to no ad fill.

        Log.d("AdColony", "onAdColonyAdAttemptFinished");
        if(adColonyAd != null && adColonyAd.shown()) {
            GameManager.getInstance().addBonusLives(Constants.SKU_FREE_INAPP_VIDEO);
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            });
        }
    }

    @Override
    public void onAdColonyAdStarted(AdColonyAd adColonyAd) {
        Log.d("AdColony", "onAdColonyAdStarted");
    }
}
