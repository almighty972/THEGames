package com.thegames.therightnumber.popups;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thegames.therightnumber.R;
import com.thegames.therightnumber.interfaces.PopupDialogListener;
import com.thegames.therightnumber.uigame.PopupAction;

import java.lang.ref.WeakReference;

/**
 * Created by gyljean-lambert on 25/05/2014.
 */
public class Popup extends DialogFragment implements View.OnClickListener {

    protected WeakReference<PopupDialogListener> mWeakPopupListener = new WeakReference<PopupDialogListener>(null);

    protected Button mPositiveButton, mNegativeButton, mPopupContinueButton, mPopupShareButton,
                    mFacebookPageButton, mTwitterPageButton, mBuyPremiumInAppButton;
    protected TextView mTitleTextView, mBodyTextView, mCountdownTextView;
    protected LinearLayout mBottomButtonsLayout, mShareButtonsLayout, mInappsLayout;
    protected RelativeLayout mNoMoreLivesLayout, mRemoveAdLayout;
    protected ProgressBar mProgressRing;

    protected PopupAction mPositiveAction, mNegativeAction, mShareAction, mContinueAction,
                            mFacebookPageAction, mTwitterPageAction, mBuyPremiumInappAction;
    protected String mTitle, mBody;

    protected WeakReference<Popup> mPopupReference;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static Popup newInstance(String title, String body) {
        Popup f = new Popup();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("body", body);

        f.setArguments(args);

        return f;
    }

    @Override
    public void onStart() {
        super.onStart();

        WindowManager.LayoutParams attr = getDialog().getWindow().getAttributes();

        int width = attr.width;
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle = getArguments().getString("title");
        mBody = getArguments().getString("body");

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_popup, container, false);

        mPositiveButton = (Button) v.findViewById(R.id.positiveButton);
        mPositiveButton.setOnClickListener(this);
        mNegativeButton = (Button) v.findViewById(R.id.negativeButton);
        mNegativeButton.setOnClickListener(this);
        mPopupShareButton = (Button) v.findViewById(R.id.popupShareButton);
        mPopupShareButton.setOnClickListener(this);
        mPopupContinueButton = (Button) v.findViewById(R.id.popupContinueButton);
        mPopupContinueButton.setOnClickListener(this);
        mFacebookPageButton = (Button) v.findViewById(R.id.facebookPageButton);
        mFacebookPageButton.setOnClickListener(this);
        mTwitterPageButton = (Button) v.findViewById(R.id.twitterPageButton);
        mTwitterPageButton.setOnClickListener(this);
        mBuyPremiumInAppButton = (Button) v.findViewById(R.id.buyPremiumInappButton);
        mBuyPremiumInAppButton.setOnClickListener(this);

        mBottomButtonsLayout = (LinearLayout) v.findViewById(R.id.bottomButtonsLayout);
        mShareButtonsLayout = (LinearLayout) v.findViewById(R.id.shareButtonsLayout);
        mInappsLayout = (LinearLayout) v.findViewById(R.id.inappsLayout);
        mNoMoreLivesLayout = (RelativeLayout) v.findViewById(R.id.noMoreLivesLayout);
        mRemoveAdLayout = (RelativeLayout) v.findViewById(R.id.removeAdLayout);
        mShareButtonsLayout.setVisibility(View.GONE);
        mProgressRing = (ProgressBar) v.findViewById(R.id.progressRing);

        mTitleTextView = (TextView) v.findViewById(R.id.titleTextView);
        mTitleTextView.setText(mTitle);
        mBodyTextView = (TextView) v.findViewById(R.id.bodyTextView);
        mBodyTextView.setText(mBody);
        mCountdownTextView = (TextView) v.findViewById(R.id.countdownTextView);

        return v;
    }

    public void setPopupListener(final PopupDialogListener listener) {
        if(listener != null) {
            mWeakPopupListener = new WeakReference<PopupDialogListener>(listener);
        }
    }

    @Override
    public void onClick(View v) {
        if (mWeakPopupListener != null && mWeakPopupListener.get() != null &&
                mPopupReference != null && mPopupReference.get() != null) {

            if (v == mPositiveButton) {  mWeakPopupListener.get().onClickPopupButton(mPopupReference.get(), mPositiveAction); }

            if (v == mNegativeButton) {  mWeakPopupListener.get().onClickPopupButton(mPopupReference.get(), mNegativeAction); }

            if (v == mPopupShareButton) {  mWeakPopupListener.get().onClickPopupButton(mPopupReference.get(), mShareAction); }

            if (v == mPopupContinueButton) {  mWeakPopupListener.get().onClickPopupButton(mPopupReference.get(), mContinueAction); }

            if (v == mFacebookPageButton) {  mWeakPopupListener.get().onClickPopupButton(mPopupReference.get(), mFacebookPageAction); }

            if (v == mTwitterPageButton) {  mWeakPopupListener.get().onClickPopupButton(mPopupReference.get(), mTwitterPageAction); }

            if (v == mBuyPremiumInAppButton) {  mWeakPopupListener.get().onClickPopupButton(mPopupReference.get(), mBuyPremiumInappAction); }
        }
    }
}
