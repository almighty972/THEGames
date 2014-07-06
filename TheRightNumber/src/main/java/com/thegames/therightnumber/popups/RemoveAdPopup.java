package com.thegames.therightnumber.popups;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.thegames.therightnumber.uigame.PopupAction;

import java.lang.ref.WeakReference;

/**
 * Created by gyljean-lambert on 25/05/2014.
 */
public class RemoveAdPopup extends Popup {

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static RemoveAdPopup newInstance(String title, String body) {
        RemoveAdPopup f = new RemoveAdPopup();

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

        mBuyPremiumInappAction = PopupAction.BUY_PREMIUM_INAPP;

        mPopupReference = new WeakReference<Popup>(this);

        setCancelable(true);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mWeakPopupListener != null && mWeakPopupListener.get() != null) {
            mWeakPopupListener.get().onDismiss();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBodyTextView.setVisibility(View.GONE);
        mShareButtonsLayout.setVisibility(View.GONE);
        mBottomButtonsLayout.setVisibility(View.GONE);
        mRemoveAdLayout.setVisibility(View.VISIBLE);
    }
}
