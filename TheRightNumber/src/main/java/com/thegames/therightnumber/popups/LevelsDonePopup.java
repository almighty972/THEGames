package com.thegames.therightnumber.popups;


import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.thegames.therightnumber.uigame.PopupAction;

import java.lang.ref.WeakReference;

/**
 * Created by gyljean-lambert on 25/05/2014.
 */
public class LevelsDonePopup extends Popup {

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static LevelsDonePopup newInstance(String title, String body) {
        LevelsDonePopup f = new LevelsDonePopup();

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

        mShareAction = PopupAction.SHARE_ALL_LEVELS_DONE;
        mFacebookPageAction = PopupAction.VISIT_FACEBOOK_PAGE;
        mTwitterPageAction = PopupAction.VISITE_TWITTER_PAGE;

        setCancelable(false);

        mPopupReference = new WeakReference<Popup>(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBodyTextView.setGravity(Gravity.CENTER);
        mShareButtonsLayout.setVisibility(View.VISIBLE);
        mFacebookPageButton.setVisibility(View.VISIBLE);
        mTwitterPageButton.setVisibility(View.VISIBLE);
        mPopupContinueButton.setVisibility(View.GONE);
        mBottomButtonsLayout.setVisibility(View.GONE);
    }
}
