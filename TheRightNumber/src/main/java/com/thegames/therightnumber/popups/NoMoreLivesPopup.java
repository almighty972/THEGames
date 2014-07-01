package com.thegames.therightnumber.popups;


import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.thegames.therightnumber.AbstractActivity;
import com.thegames.therightnumber.Constants;
import com.thegames.therightnumber.R;
import com.thegames.therightnumber.billing.InAppProductsFragments;
import com.thegames.therightnumber.billing.Inventory;
import com.thegames.therightnumber.interfaces.BillingListener;
import com.thegames.therightnumber.managers.BillingManager;
import com.thegames.therightnumber.managers.GameManager;
import com.thegames.therightnumber.model.InApp;
import com.thegames.therightnumber.views.InAppView;

import java.lang.ref.WeakReference;

/**
 * Created by gyljean-lambert on 25/05/2014.
 */
public class NoMoreLivesPopup extends Popup implements BillingListener {

    private CountDownTimer mCountDownTimer;
    private long mMillisUntilFinished;
    private long mLastStartedTime;
    private InAppProductsFragments mInAppProductsFragments;
    private AbstractActivity mActivity;

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
    }

    @Override
    public void onResume() {
        super.onResume();
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
                    if(mCountdownTextView != null) {
                        mCountdownTextView.setText("00:00");
                    }
                    GameManager.getInstance().saveLongPref(Constants.PREFS_COUNTDOWN_TIME, 0);
                    GameManager.getInstance().saveLongPref(Constants.PREFS_COUNTDOWN_STARTED_TIME, 0);
                    /// TODO notify listener to bring back game view and add 50 free lives
                }
            };
            mCountDownTimer.start();
        } else {
//            dismiss();
            /// TODO notify listener to bring back game view and add 50 free lives
        }
    }

    @Override
    public void onProductsReceived(Inventory inventory) {
        mProgressRing.setVisibility(View.GONE);
        if(mActivity != null) {
            InApp inapp = null;

            // adding free inapp
            inapp = new InApp("25", mActivity.getString(R.string.free_inapp));
            addInappView(inapp);

            // adding other inapp
            if (inventory != null) {
                if (inventory.hasDetails(Constants.SKU_PACK_200_VIES)) {
                    inapp = new InApp(inventory.getSkuDetails(Constants.SKU_PACK_200_VIES));
                    addInappView(inapp);
                }
                if (inventory.hasDetails(Constants.SKU_PACK_1500_VIES)) {
                    inapp = new InApp(inventory.getSkuDetails(Constants.SKU_PACK_1500_VIES));
                    addInappView(inapp);
                }
                if (inventory.hasDetails(Constants.SKU_PACK_5000_VIES)) {
                    inapp = new InApp(inventory.getSkuDetails(Constants.SKU_PACK_5000_VIES));
                    addInappView(inapp);
                }

                startOrResumeCountDown();
            }
        }
    }

    private void addInappView(InApp inApp) {
        if(mInappsLayout != null) {
            mInappsLayout.addView(new InAppView(getActivity(), inApp));
        }
    }
}
